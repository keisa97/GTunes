package com.example.musicwithnav.ui.home.inHomeFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicwithnav.R;
import com.example.musicwithnav.models.Sound;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProducerFragment extends Fragment {

    View v;

    private View soundsView;
    private RecyclerView myRecylceViewSoundsList;

    List<Sound> soundsList = new ArrayList<>();

    FirebaseRecyclerAdapter<Sound, ProducerFragment.SoundsViewHolder> adapter;

    // private FirebaseDatabase database; //= FirebaseDatabase.getInstance();


    private DatabaseReference soundsRef, userRef;
    private DatabaseReference samplesRef, songRef, vocalRef;

    private FirebaseAuth auth;

    private String currentUserId;

    public ProducerFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        soundsView = inflater.inflate(R.layout.fragment_instrumental, container, false);

        myRecylceViewSoundsList = soundsView.findViewById(R.id.for_instrumental_list_recycler);
        //   myRecylceViewSoundsList.setHasFixedSize(true);
        myRecylceViewSoundsList.setLayoutManager(new LinearLayoutManager(getContext()));


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();




        samplesRef = FirebaseDatabase.getInstance().getReference().child("sound").child("Beat");
       /* songRef = database.getInstance().getReference().child("Song");
        vocalRef = database.getInstance().getReference().child("Vocal");*/



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
      public   TextView soundName, artist, producer, likesCounter;
        ImageView soundImageCover;
               Button likes;




        public SoundsViewHolder(@NonNull View itemView) {
            super(itemView);

            soundName = itemView.findViewById(R.id.tv_user_item_sound_name);
            artist = itemView.findViewById(R.id.tv_user_soundItem_artist);
            producer = itemView.findViewById(R.id.tv_user_soundItem_producer);
            soundImageCover = itemView.findViewById(R.id.imageview_user_item_sound_cover);
            likes = itemView.findViewById(R.id.btn_user_sound_item_star);
            likesCounter = itemView.findViewById(R.id.tv_user_sound_stars_count);

        }
    }


/*    public void retriveData() {
        soundsList.clear();

        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference()
                .child("sound");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot soundSnapShot : dataSnapshot.getChildren()) {
                    Sound sound = soundSnapShot.getValue(Sound.class);

                    System.out.println("ininini");
                    soundsList.add(sound);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("message", "onCancelled: " + databaseError.getMessage());
            }
        });
    }*/

    private void setData() {
        Query query = FirebaseDatabase.getInstance().getReference().child("sounds");
        FirebaseRecyclerOptions<Sound> options = new FirebaseRecyclerOptions.Builder<Sound>()
                .setQuery(query,Sound.class)
                .build();


    }


    private void adapterSetter(){

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Sound>()
                        .setQuery(samplesRef, Sound.class)
                        .build();


        adapter
                = new FirebaseRecyclerAdapter<Sound, ProducerFragment.SoundsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProducerFragment.SoundsViewHolder holder, int position, @NonNull Sound model) {
                System.out.println("test in bindeViewModel");

               holder.soundName.setText(model.getSoundName());
               holder.artist.setText(model.getSoundVocalName());
               holder.likesCounter.setText(String.valueOf(model.getLikes()));

               holder.likes.setOnClickListener((view)->{
                   model.setLikes(+1);

               });

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
