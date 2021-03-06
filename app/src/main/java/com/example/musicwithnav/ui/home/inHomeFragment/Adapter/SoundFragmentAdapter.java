package com.example.musicwithnav.ui.home.inHomeFragment.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.musicwithnav.models.Sound;
import com.example.musicwithnav.ui.AnotherUserPage.AnotherUserPageActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class SoundFragmentAdapter extends RecyclerView.Adapter<SoundFragmentAdapter.MyViewHolder> {
    private List<Sound> modelList;
    Context context;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;

    private boolean playPause;
    private boolean initialStage = true;

    private boolean userLiked;
    Boolean prepared = false;
    String filename, checkFileName;
    int lastPos, currentPos;
    static final int PICK_CONTACT_REQUEST = 1;


    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mRunnable;

    public SoundFragmentAdapter(List<Sound> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_item_dark_theme, parent, false);

        return new SoundFragmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Sound model = modelList.get(position);


        holder.soundName.setText(model.getSoundName());
        holder.artist.setText(model.getSoundVocalName());
        //holder.soundLength.setText((mediaPlayer.getDuration())); - may try toString

//        holder.likesCounter.setText(String.valueOf(model.getLikes()));

        Drawable drawableLiked = context.getResources().getDrawable(R.drawable.ic_star_liked_24dp);
        Drawable drawableUnLiked = context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp);

        holder.artist.setOnClickListener((view)->{
//            Intent intent = new Intent();
//            intent.setClass(this.context, AnotherUserPageActivity.class);
//            startActivity(intent);

            Gson gson = new Gson();
            String modelAsAString = gson.toJson(model);

            Intent intent = new Intent(this.context, AnotherUserPageActivity.class);
            intent.putExtra("uid", model.getSoundVocalID());
            intent.putExtra("model", modelAsAString);

            context.startActivity(intent);

        });


//        holder.like.setOnClickListener((view) -> {
//            //holder.like.setBackground(drawableLiked);
//
//
//            /*if (!userLiked) {
//                model.setLikes(+1);
//                holder.like.setBackground(drawableLiked);
//            }else
//                model.setLikes(-1);
//                holder.like.setBackground(drawableUnLiked);
//             */
//
//        });

        Drawable drawablePause = context.getResources().getDrawable(R.drawable.pause_btn_dark);
        Drawable drawablePlay = context.getResources().getDrawable(R.drawable.play_btn_dark);


        Glide.with(context)
                .load(model.getSoundImageUrl())
                .into(holder.soundImageCover);

        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPos = position;

                holder.btn_play.setBackground(drawablePause);


                if (!playPause) {

                    if (initialStage) {
                        Player(model.getSoundDownloadUrl(), holder.btn_play, holder.songSeekBar);
                        checkFileName = model.getSoundName();
                        lastPos = position;

                        //initializeSeekBar(holder.songSeekBar,mediaPlayer);
                    } else if (!checkFileName.equals(model.getSoundName())) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();

                        mediaPlayer = new MediaPlayer();


                        Player(model.getSoundDownloadUrl(), holder.btn_play, holder.songSeekBar);
                        checkFileName = model.getSoundName();
                        lastPos = position;
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
                        mSeekbarUpdateHandler.removeCallbacks(mRunnable);

                        holder.btn_play.setBackground(drawablePlay);
                    }

                    playPause = false;


                }

                mediaPlayer.setOnErrorListener((paramMediaPlayer, paramInt1, paramInt2) -> true);

                mediaPlayer.setOnCompletionListener(mp -> {
                    holder.btn_play.setBackground(drawablePlay);
                    playPause = false;


                });

            }

        });


        holder.songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
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
        TextView soundName, producer, soundLength, likesCounter;
        ImageView soundImageCover;

        Button artist;
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
            soundLength = itemView.findViewById(R.id.tv_user_sound_item_timeLeft);
            likesCounter = itemView.findViewById(R.id.tv_user_sound_stars_count);
            like = itemView.findViewById(R.id.btn_user_sound_item_star);

        }
    }

    private void Player(String filename, Button btn_play, SeekBar seekBar) {
        try {
//            Toast.makeText(con, filename, Toast.LENGTH_SHORT).show();
            mediaPlayer.setDataSource(filename);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();

                    initializeSeekBar(seekBar, mp);
                    if (mediaPlayer.isPlaying()) ;
                    {
                        //run(seekBar);
                        //seekBar.setProgress(mp.getCurrentPosition() / 1000);

                    }

                    initialStage = false;
                }
            });

            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e("MyAudioStreamingApp", e.getMessage());
        }
    }

    private void initializeSeekBar(SeekBar seekBar, MediaPlayer mplayer) {


        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mplayer.getCurrentPosition()); // In milliseconds
                    seekBar.setMax(mplayer.getDuration());
                    //getAudioStats();
                }
                mSeekbarUpdateHandler.postDelayed(this::run, 50);
            }
        };
        mSeekbarUpdateHandler.post(mRunnable);

    }
}

