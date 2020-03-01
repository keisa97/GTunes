package com.example.musicwithnav.ui.UserDashboard;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.musicwithnav.Sound;
import com.example.musicwithnav.ui.home.inHomeFragment.ProducerFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserContentFragment extends Fragment {


    private View soundsView;
    private RecyclerView myRecylceViewSoundsList;

    List<Sound> soundsList = new ArrayList<>();

    FirebaseRecyclerAdapter<Sound, UserContentFragment.SoundsViewHolder> adapter;

    // private FirebaseDatabase database; //= FirebaseDatabase.getInstance();


    private DatabaseReference soundsRef, userRef;
    private DatabaseReference samplesRef, songRef, vocalRef;

    private FirebaseAuth auth;

    private String currentUserId;


    public UserContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        soundsView = inflater.inflate(R.layout.fragment_user_content, container, false);

        myRecylceViewSoundsList = soundsView.findViewById(R.id.for_UserContentFragment_list_recycler);
        //   myRecylceViewSoundsList.setHasFixedSize(true);
        myRecylceViewSoundsList.setLayoutManager(new LinearLayoutManager(getContext()));


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();




        samplesRef = FirebaseDatabase.getInstance().getReference().child("sound").child("Instrumental");
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
        public TextView soundName, artist, producer;
        public Button delete, like;
        ImageView soundImageCover;



        public SoundsViewHolder(@NonNull View itemView) {
            super(itemView);

            //TextViews
            soundName = itemView.findViewById(R.id.tv_user_item_sound_name);
            artist = itemView.findViewById(R.id.tv_user_soundItem_artist);
            producer = itemView.findViewById(R.id.tv_user_soundItem_producer);
            soundImageCover = itemView.findViewById(R.id.imageview_user_item_sound_cover);

            //buttons
            delete = itemView.findViewById(R.id.btn_user_sound_item_delete);

            String soundImageUrl;
            String soundDownloadUrl;

        }
    }



    private void adapterSetter(){

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Sound>()
                        .setQuery(samplesRef.orderByChild("soundVocalID").equalTo(currentUserId) , Sound.class)
                        .build();


        adapter
                = new FirebaseRecyclerAdapter<Sound, UserContentFragment.SoundsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserContentFragment.SoundsViewHolder holder, int position, @NonNull Sound model) {
                System.out.println("test in bindeViewModel");



                holder.soundName.setText(model.getSoundName());
                holder.artist.setText(model.getSoundVocalName());

                DatabaseReference databaseReference = getRef(position);
                //StorageReference  storageReference = (model.getSoundDownloadUrl());



                holder.delete.setOnClickListener((v -> {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.dialog_delete_sound)
                                    .setPositiveButton(R.string.agree_delete_sound, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            adapter.getRef(position).removeValue();

                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel_delete_sound, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            // Create the AlertDialog object and return it





                    //databaseReference.removeValue();
                }));





            }


            @NonNull
            @Override
            public UserContentFragment.SoundsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_dashboard_sound_item, parent, false);
                UserContentFragment.SoundsViewHolder viewHolder = new UserContentFragment.SoundsViewHolder(view);
                return viewHolder;

            }
        };

        myRecylceViewSoundsList.setAdapter(adapter);

    }

}
