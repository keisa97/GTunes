package com.example.musicwithnav.ui.home.inHomeFragment.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicwithnav.R;
import com.example.musicwithnav.Sound;

import java.io.IOException;
import java.util.List;

public class ArtistFragmentAdapter extends RecyclerView.Adapter<ArtistFragmentAdapter.MyViewHolder> {
    private List<Sound> modelList;
    Context context;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;

    private boolean playPause;
    private boolean initialStage = true;
    Boolean prepared = false;
    String filename, checkFileName;
    int lastPos, currentPos;
    static final int PICK_CONTACT_REQUEST = 1;


    private Handler mHandler=new Handler();
    private Runnable mRunnable;

    public ArtistFragmentAdapter(List<Sound> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_item2, parent, false);

        return new ArtistFragmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Sound model = modelList.get(position);


        holder.soundName.setText(model.getSoundName());
        holder.artist.setText(model.getSoundVocalName());

        holder.likesCounter.setText(String.valueOf(model.getLikes()));

        holder.like.setOnClickListener((view)->{
            model.setLikes(+1);

        });

        Drawable drawablePause = context.getResources().getDrawable(R.drawable.pause_btn);
        Drawable drawablePlay = context.getResources().getDrawable(R.drawable.play_btn);


        Glide.with(context)
                .load(model.getSoundImageUrl())
                .into(holder.soundImageCover);

        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPos =position;
                //Drawable drawablePause = context.getResources().getDrawable(R.drawable.pause_btn);
                //Drawable drawablePlay = context.getResources().getDrawable(R.drawable.play_btn);


                holder.btn_play.setBackground(drawablePause);





                if (!playPause) {

                    if (initialStage) {
                        Player(model.getSoundDownloadUrl(),holder.btn_play,holder.songSeekBar);
                        checkFileName =model.getSoundName();
                        lastPos =position;
                        //holder.songSeekBar.setProgress(mediaPlayer.getCurrentPosition());

                        //initializeSeekBar(holder.songSeekBar);
                    }
                    else if(!checkFileName.equals(model.getSoundName()))
                    {
                        mediaPlayer.stop();
                        //mediaPlayer.release();
                        //mediaPlayer.reset();
                        mediaPlayer = new MediaPlayer();

                        Player(model.getSoundDownloadUrl(),holder.btn_play,holder.songSeekBar);
                        checkFileName =model.getSoundName();
                        lastPos =position;
                    } else {

                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();

                    }
                    playPause = true;

                } else {
                    //Drawable drawablePlay = context.getResources().getDrawable(R.drawable.play_btn);
                    holder.btn_play.setBackground(drawablePlay);

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        holder.btn_play.setBackground(drawablePlay);
                    }

                    playPause = false;



                }

                mediaPlayer.setOnCompletionListener((mediaPlayer)->{

                    if (!mediaPlayer.isPlaying()) {
                        //mediaPlayer.release();
                        //mediaPlayer.reset();
                        holder.btn_play.setBackground(drawablePlay);
                    }

                });


            }

        });


        if (playPause==true){
            holder.btn_play.setBackground(drawablePause);

        }else {
            holder.btn_play.setBackground(drawablePlay);
        }

        holder.songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mediaPlayer.seekTo(progress);
                if(mediaPlayer != null ){
                    mediaPlayer.seekTo(progress * 1000);
                }

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

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView soundName, artist, producer, seekbartime, likesCounter;
        ImageView soundImageCover;

        Button like;

        public SeekBar songSeekBar;
        Thread updateSeekBar;
        int position;

        Button btn_play, btn_pause;

        public MyViewHolder(View itemView) {
            super(itemView);
            btn_play = itemView.findViewById(R.id.btn_user_sound_item_play);
            btn_pause = itemView.findViewById(R.id.btn_user_sound_item_pause);
            soundName = itemView.findViewById(R.id.tv_user_item_sound_name);
            artist = itemView.findViewById(R.id.tv_user_soundItem_artist);
            producer = itemView.findViewById(R.id.tv_user_soundItem_producer);
            soundImageCover = itemView.findViewById(R.id.imageview_user_item_sound_cover);
            songSeekBar = itemView.findViewById(R.id.seekBar_user_sound_item);
            //seekbartime = itemView.findViewById(R.id.tv_user_sound_stars_count);
            likesCounter = itemView.findViewById(R.id.tv_user_sound_stars_count);
            like = itemView.findViewById(R.id.btn_user_sound_item_star);

        }
    }
    private  void Player(String filename,Button btn_play,SeekBar seekBar) {
        try {
//            Toast.makeText(con, filename, Toast.LENGTH_SHORT).show();
            mediaPlayer.setDataSource(filename);
            mediaPlayer.prepareAsync();
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                 @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    initializeSeekBar(seekBar,mp);
                    if (mediaPlayer.isPlaying()) ;
                    {
                        //run(seekBar);
//                        seekBar.setProgress(mp.getCurrentPosition() / 1000);

                    }

                    initialStage = false;
                }
            });

        } catch (IOException e) {
            Log.e("MyAudioStreamingApp", e.getMessage());
        }
    }

    protected void initializeSeekBar(SeekBar seekBar,MediaPlayer mplayer){


        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition = mplayer.getCurrentPosition()/1000; // In milliseconds
                    seekBar.setProgress(mCurrentPosition);
                    //getAudioStats();
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
    }
}

