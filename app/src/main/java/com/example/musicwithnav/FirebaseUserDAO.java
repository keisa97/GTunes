package com.example.musicwithnav;

import androidx.annotation.NonNull;

import com.example.musicwithnav.models.Sound;
import com.example.musicwithnav.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUserDAO {

    public static final String USERS = "Users";

    public static FirebaseUserDAO shared = new FirebaseUserDAO();

    public FirebaseUserDAO() {
    }


    public void saveUser(User user){
        DatabaseReference newUserRef = FirebaseDatabase.getInstance()
                .getReference(USERS).child(user.getName());
                //.push();
        user.setName(user.getName());
        user.setUid(user.getUid());
        user.setOnlineStatus(user.getOnlineStatus());
        user.setUserProfileImageUrl(user.getUserProfileImageUrl());
        user.setUserDetails(user.getUserDetails());

        newUserRef.setValue(user);

    }

    public void saveDetailsOnly(User user){
        DatabaseReference newUserRef = FirebaseDatabase.getInstance()
                .getReference(USERS).child(user.getName());
               // .push();
        user.setName(user.getName());
        //user.setUserDetails(user.getUserDetails());
        user.setUserDetails("test");

        newUserRef.setValue(user);
    }



    public void readUser(UserListener userListener){

        ArrayList<User> users = new ArrayList<>();
        FirebaseDatabase.getInstance().
                getReference(USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //children=> json of the messages
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //each child is a single message json
                            users.add(child.getValue(User.class));
                        }
                        userListener.UserArrived(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public interface UserListener{
        void UserArrived(List<User> users);
    }
}
