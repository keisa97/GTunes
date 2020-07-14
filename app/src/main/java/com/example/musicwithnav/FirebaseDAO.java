package com.example.musicwithnav;


import androidx.annotation.NonNull;

import com.example.musicwithnav.models.Message;
import com.example.musicwithnav.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Usage: FirebaseDAO.shared.saveMessage
public class FirebaseDAO {
    public static final String MESSAGES = "messages";
    public static final String USERS = "users";

    //singleton: share a single object across the entire app

    private FirebaseDAO(){ }

    public static FirebaseDAO shared = new FirebaseDAO();

    public void saveUser(User user){

        DatabaseReference newUserRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .push();
        //    public static final String USERS = "users"; got with press Command-option-C
        user.setUid(newUserRef.getKey());

        newUserRef.setValue(user);

    }

    public void saveMessage(Message message){
        //new database listing:
        DatabaseReference newMessageRef = FirebaseDatabase.getInstance()
                .getReference(MESSAGES)
                .push();
        //message.setID(newMessageRef.id) not null anymore
        message.setMessageID(newMessageRef.getKey());

        //save the data:

                newMessageRef.setValue(message);
    }

    public void readMessages(MessageListener messageListener){

        ArrayList<Message> messages = new ArrayList<>();
        FirebaseDatabase.getInstance().
                getReference(MESSAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //children=> json of the messages
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //each child is a single message json
                            messages.add(child.getValue(Message.class));
                        }
                        messageListener.messagesArrived(messages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public interface MessageListener{
        void messagesArrived(List<Message> messages);
    }




}
