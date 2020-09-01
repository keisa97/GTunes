package com.example.musicwithnav.ui.UserDashboard.adapter.tabs.userTabs;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicwithnav.R;
import com.example.musicwithnav.models.Sound;
import com.example.musicwithnav.ui.UserDashboard.adapter.UserSoundFragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAcapellaContentFragment extends Fragment {


    private View soundsView;
    private RecyclerView myRecylceViewSoundsList;

    ProgressDialog dialog;

    // FirebaseRecyclerAdapter<Sound, ArtistFragment.SoundsViewHolder> adapter;

    UserSoundFragmentAdapter mAdapter;

    // private FirebaseDatabase database; //= FirebaseDatabase.getInstance();


    private DatabaseReference soundsRef, userRef;
    private DatabaseReference samplesRef, songRef, vocalRef;

    private FirebaseAuth auth;

    private String currentUserId;

    private Button btn_play;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        soundsView = inflater.inflate(R.layout.fragment_instrumental, container, false);

        myRecylceViewSoundsList = soundsView.findViewById(R.id.for_instrumental_list_recycler);
        //   myRecylceViewSoundsList.setHasFixedSize(true);
        myRecylceViewSoundsList.setLayoutManager(new LinearLayoutManager(getContext()));

        dialog = new ProgressDialog(getContext());
        dialog.show();
        dialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();


        samplesRef = FirebaseDatabase.getInstance().getReference().child("sound").child("Acapella");
        samplesRef.orderByChild("soundVocalID").equalTo(currentUserId);



        //adapterSetter();



        samplesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Sound> soundsList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Sound sound = dataSnapshot1.getValue(Sound.class);

                    soundsList.add(sound);
                    dataSnapshot1.getRef();
                    //mAdapter.deleteSound(dataSnapshot1);

                }
                dialog.dismiss();
                mAdapter = new UserSoundFragmentAdapter(soundsList, getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false);
                myRecylceViewSoundsList.setLayoutManager(layoutManager);
                myRecylceViewSoundsList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });



        samplesRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return soundsView;
    }


    public void deleteSound(DataSnapshot dataSnapshot){

        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
        firstChild.getRef().removeValue();


//            String key = dataSnapshot.getKey();
//
//            if (mKeys.contains(key)) {
//                int index = mKeys.indexOf(key);
//                T item = mItems.get(index);
//
//                mKeys.remove(index);
//                mItems.remove(index);
//
//                notifyItemRemoved(index);
//                itemRemoved(item, key, index);
//            }
    }

}
