package com.example.musicwithnav;

import androidx.annotation.NonNull;

import com.example.musicwithnav.models.Sound;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseSoundDAO {

    public static final String SOUND = "sound";

    public static FirebaseSoundDAO shared = new FirebaseSoundDAO();

    public FirebaseSoundDAO() {
    }

    public void saveSampleSoundName(Sound sound, String typeOfSound, String setSoundID, String setSoundName,
                                    String setSoundBeatProducerID, String setSoundBeatProducerName, String setSoundVocalID,
                                    String setSoundVocalName, String setSoundImageUrl, String setSoundDownloadUrl ){
        //new database listing:
        DatabaseReference newSoundRef = FirebaseDatabase.getInstance()
                .getReference(SOUND).child(typeOfSound)
                .push();
        sound.setSoundID(newSoundRef.getKey());
        sound.setSoundName(setSoundName);
        sound.setSoundBeatProducerID(setSoundBeatProducerID);
        sound.setSoundBeatProducerName(setSoundBeatProducerName);
        sound.setSoundVocalID(setSoundVocalID);
        sound.setSoundVocalName(setSoundVocalName);
        sound.setSoundImageUrl(setSoundImageUrl);
        sound.setSoundDownloadUrl(setSoundDownloadUrl);
        sound.setLikes(0);

        //save the data:

        newSoundRef.setValue(sound);

    }

    public void saveBeatsSoundName(Sound sound){
        //new database listing:
        DatabaseReference newSoundRef = FirebaseDatabase.getInstance()
                .getReference(SOUND).child("beats")
                .push();
        sound.setSoundID(newSoundRef.getKey());
        sound.setSoundName(newSoundRef.toString());
        sound.setSoundBeatProducerID(newSoundRef.getKey());
        sound.setSoundBeatProducerName(newSoundRef.getKey());
        sound.setSoundVocalID(newSoundRef.getKey());
        sound.setSoundVocalName(newSoundRef.getKey());
        sound.setSoundImageUrl(newSoundRef.getKey());
        sound.setSoundDownloadUrl(newSoundRef.getKey());


        //save the data:

        newSoundRef.setValue(sound);

    }

    public void readSound(SoundListener soundListener){

        ArrayList<Sound> sounds = new ArrayList<>();
        FirebaseDatabase.getInstance().
                getReference(SOUND)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //children=> json of the messages
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //each child is a single message json
                            sounds.add(child.getValue(Sound.class));
                        }
                        soundListener.SoundArrived(sounds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public interface SoundListener{
        void SoundArrived(List<Sound> sounds);
    }
}
