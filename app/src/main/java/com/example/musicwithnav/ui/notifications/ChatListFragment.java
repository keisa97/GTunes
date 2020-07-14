package com.example.musicwithnav.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicwithnav.MainActivity;
import com.example.musicwithnav.R;
import com.example.musicwithnav.chat.Chat;
import com.example.musicwithnav.chat.ChatList;
import com.example.musicwithnav.chat.ChatListAdapter;
import com.example.musicwithnav.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {


    //firebase auth
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ChatList> chatlistList;
    List<User> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    ChatListAdapter adapterChatlist;


    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        chatlistList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ChatList chatlist = ds.getValue(ChatList.class);
                    chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    for (ChatList chatlist : chatlistList){
                        if (user.getUid() != null && user.getUid().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatlist = new ChatListAdapter(getContext(),userList);
                    //set adapter
                    recyclerView.setAdapter(adapterChatlist);
                    //set last message
                    for (int i = 0; i < userList.size(); i++) {
                        lastMessage(userList.get(i).getUid());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat == null){
                        continue;
                    }
                    String sender = chat.getSenderUid();
                    String receiver = chat.getReceiverUid();
                    if (sender == null || receiver==null){
                        continue;
                    }
                    if (chat.getReceiverUid().equals(currentUser.getUid())&&
                            chat.getSenderUid().equals(userId) ||
                            chat.getReceiverUid().equals(userId) &&
                                    chat.getSenderUid().equals(currentUser.getUid())){
                        //instead of displaying url in message show "sent photo"
                        if (chat.getType().equals("image")){
                            theLastMessage = "Sent a photo";
                        }
                        else{
                            theLastMessage = chat.getMessage();
                        }

                    }
                }
                adapterChatlist.setLastMessageMap(userId,theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUsersStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            //user is signed in stay here
            //set email of logged in user
            //mProfileTv.setText(user.getEmail());
        }
        else{
            //user not signed in,go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //options menu
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main,menu);
//
//        //hide addPost icon from this fragment
//        menu.findItem(R.id.action_add_post).setVisible(false);
//        menu.findItem(R.id.action_add_participant).setVisible(false);
//        menu.findItem(R.id.action_groupinfo).setVisible(false);
//        menu.findItem(R.id.action_create_group).setVisible(false);
//
//
//
//        super.onCreateOptionsMenu(menu,inflater);
//    }



    //handle click in menu
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        //get item id
//        int id = item.getItemId();
//        if (id == R.id.action_logout){
//            firebaseAuth.signOut();
//            checkUsersStatus();
//        }
//        else if (id==R.id.action_settings){
//            //go to settings activity
//            startActivity(new Intent(getActivity(), SettingsActivity.class));
//        }
//        else if (id==R.id.action_create_group){
//            //go to Group Create activity
//            startActivity(new Intent(getActivity(), GroupCreateActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }


}
