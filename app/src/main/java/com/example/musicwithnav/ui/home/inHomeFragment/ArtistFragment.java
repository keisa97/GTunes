package com.example.musicwithnav.ui.home.inHomeFragment;


import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicwithnav.R;
import com.example.musicwithnav.models.Sound;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {


    private View soundsView;
    private RecyclerView myRecylceViewSoundsList;

    List<Sound> soundsList = new ArrayList<>();

    FirebaseRecyclerAdapter<Sound, ArtistFragment.SoundsViewHolder> adapter;

    // private FirebaseDatabase database; //= FirebaseDatabase.getInstance();


    private DatabaseReference soundsRef, userRef;
    private DatabaseReference samplesRef, songRef, vocalRef;

    private FirebaseAuth auth;

    //sound item properties:
   /* private String soundName;
    private String soundBeatProducerID;
    private String soundBeatProducerName;
    private String soundVocalID;
    private String soundVocalName;*/
    private static String soundImageUrl;
    private static String soundDownloadUrl;
    private String soundLength;
    private String soundID;




    private String currentUserId;

    private Button btn_play;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        soundsView = inflater.inflate(R.layout.fragment_acapella, container, false);

        myRecylceViewSoundsList = soundsView.findViewById(R.id.for_acapella_list_recycler);
        //   myRecylceViewSoundsList.setHasFixedSize(true);
        myRecylceViewSoundsList.setLayoutManager(new LinearLayoutManager(getContext()));



        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();





        samplesRef = FirebaseDatabase.getInstance().getReference().child("sound").child("Acapella");




        adapterSetter();
        return soundsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
        TextView soundName, artist, producer;
        ImageView soundImageCover;

        SeekBar songSeekBar;
        Thread updateSeekBar;
        int position;

        Button btn_play, btn_pause;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void playMusic(MediaPlayer mediaPlayer){

            //try {
                mediaPlayer.setAudioAttributes( new AudioAttributes
                        .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                /*mediaPlayer.setDataSource(soundDownloadUrl);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();*/




                try {
                    mediaPlayer.setDataSource(soundDownloadUrl);
                    mediaPlayer.prepareAsync(); // prepare async to not block main thread

                } catch (IOException e) {
                    //Toast.makeText(this , "mp3 not found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                //mp3 will be started after completion of preparing...
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer player) {
                        player.start();
                        songSeekBar.setMax(mediaPlayer.getDuration());
                        changeSeekbar();

                        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                mediaPlayer.seekTo(progress);
                                songSeekBar.setProgress(progress);
                                //songSeekBar.
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                                mediaPlayer.seekTo(seekBar.getProgress());
                            }
                        });
                    }

                });



                btn_play.setVisibility(View.INVISIBLE);
                btn_pause.setVisibility(View.VISIBLE);

                /*if (mediaPlayer.isPlaying()!=false){
                    btn_pause.setVisibility(View.INVISIBLE);
                    btn_play.setVisibility(View.VISIBLE);
                }*/



            /*} catch (IOException e) {
                e.printStackTrace();
            }*/


        }

        private void changeSeekbar() {
            //songSeekBar.setProgress();
        }

        private void stopMusic(MediaPlayer mediaPlayer){


            /*mediaPlayer.pause();
            mediaPlayer.reset();
            mediaPlayer.release();*/

            if(mediaPlayer!=null) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    btn_pause.setVisibility(View.INVISIBLE);
                    btn_play.setVisibility(View.VISIBLE);
                }
            }

        }





        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public SoundsViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_play = itemView.findViewById(R.id.btn_user_sound_item_play);
            btn_pause = itemView.findViewById(R.id.btn_user_sound_item_pause);
            soundName = itemView.findViewById(R.id.tv_user_item_sound_name);
            artist = itemView.findViewById(R.id.tv_user_soundItem_artist);
            producer = itemView.findViewById(R.id.tv_user_soundItem_producer);
            soundImageCover = itemView.findViewById(R.id.imageview_user_item_sound_cover);
            songSeekBar = itemView.findViewById(R.id.seekBar_user_sound_item);




            MediaPlayer mediaPlayer = new MediaPlayer();

            /*updateSeekBar = new Thread(){
                @Override
                public void run() {

                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = 0;

                    while (currentPosition<totalDuration){
                        try{

                            sleep(500);
                            currentPosition = mediaPlayer.getCurrentPosition();
                            songSeekBar.setProgress(currentPosition);

                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
            };*/




            btn_play.setOnClickListener(v -> playMusic(mediaPlayer));
            btn_pause.setOnClickListener(v -> stopMusic(mediaPlayer));


            Glide.with(itemView)
                    .load(soundImageUrl)
                    .into(soundImageCover);
        }
    }




    private void adapterSetter(){

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Sound>()
                        .setQuery(samplesRef, Sound.class)
                        .build();


        adapter
                = new FirebaseRecyclerAdapter<Sound, ArtistFragment.SoundsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ArtistFragment.SoundsViewHolder holder, int position, @NonNull Sound model) {
                System.out.println("test in bindeViewModel");

               holder.soundName.setText(model.getSoundName());
               holder.artist.setText(model.getSoundVocalName());
               soundDownloadUrl = model.getSoundDownloadUrl();
               soundImageUrl = model.getSoundImageUrl();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @NonNull
            @Override
            public ArtistFragment.SoundsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_item, parent, false);
                ArtistFragment.SoundsViewHolder viewHolder = new ArtistFragment.SoundsViewHolder(view);
                return viewHolder;
            }
        };

        myRecylceViewSoundsList.setAdapter(adapter);

    }


    //Music part:

    //play music at the item - sound

    private void playMusic(){

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(soundDownloadUrl);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
