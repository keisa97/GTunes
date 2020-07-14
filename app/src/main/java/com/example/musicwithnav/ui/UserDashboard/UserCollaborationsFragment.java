package com.example.musicwithnav.ui.UserDashboard;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicwithnav.R;
import com.example.musicwithnav.models.Sound;
import com.example.musicwithnav.ui.home.inHomeFragment.ProducerFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCollaborationsFragment extends Fragment {


    private View soundsView;
    private RecyclerView myRecylceViewSoundsList;

    List<Sound> soundsList = new ArrayList<>();

    FirebaseRecyclerAdapter<Sound, ProducerFragment.SoundsViewHolder> adapter;

    // private FirebaseDatabase database; //= FirebaseDatabase.getInstance();


    private DatabaseReference soundsRef, userRef;
    private DatabaseReference samplesRef, songRef, vocalRef;
    private DatabaseReference collobartions;

    private FirebaseAuth auth;

    private String currentUserId;


    public UserCollaborationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        soundsView = inflater.inflate(R.layout.fragment_acapella, container, false);

        myRecylceViewSoundsList = soundsView.findViewById(R.id.for_acapella_list_recycler);
        //   myRecylceViewSoundsList.setHasFixedSize(true);
        myRecylceViewSoundsList.setLayoutManager(new LinearLayoutManager(getContext()));


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();




        samplesRef = FirebaseDatabase.getInstance().getReference().child("sound").child("Vocal");
       /* songRef = database.getInstance().getReference().child("Song");
        vocalRef = database.getInstance().getReference().child("Vocal");*/

       //setting the collabration

        //collobartions = (DatabaseReference) samplesRef.orderByChild("soundVocalID").equalTo(currentUserId)& samplesRef.orderByChild("soundBeatProducerID").equalTo(true);



        adapterSetter();
        return soundsView;

    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();

    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static class SoundsViewHolder extends RecyclerView.ViewHolder {
        public TextView soundName, artist, producer;
        ImageView soundImageCover;



        public SoundsViewHolder(@NonNull View itemView) {
            super(itemView);

            soundName = itemView.findViewById(R.id.tv_user_item_sound_name);
            artist = itemView.findViewById(R.id.tv_user_soundItem_artist);
            producer = itemView.findViewById(R.id.tv_user_soundItem_producer);
            soundImageCover = itemView.findViewById(R.id.imageview_user_item_sound_cover);

        }
    }



    private void adapterSetter(){

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Sound>()
                        .setQuery(samplesRef.orderByChild("soundVocalID").equalTo(currentUserId) , Sound.class)
                        .build();


        adapter
                = new FirebaseRecyclerAdapter<Sound, ProducerFragment.SoundsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProducerFragment.SoundsViewHolder holder, int position, @NonNull Sound model) {
                System.out.println("test in bindeViewModel");

                holder.soundName.setText(model.getSoundName());
                holder.artist.setText(model.getSoundVocalName());
            }

            @NonNull
            @Override
            public ProducerFragment.SoundsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_item, parent, false);
                ProducerFragment.SoundsViewHolder viewHolder = new ProducerFragment.SoundsViewHolder(view);
                return viewHolder;
            }
        };

        myRecylceViewSoundsList.setAdapter(adapter);

    }

}
