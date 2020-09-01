package com.example.musicwithnav.ui.AnotherUserPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicwithnav.R;
import com.example.musicwithnav.chat.ChatActivity;
import com.example.musicwithnav.models.Sound;
import com.example.musicwithnav.models.User;
import com.example.musicwithnav.ui.AnotherUserPage.viewPagers.anotherUserAcapellaFragment;
import com.example.musicwithnav.ui.AnotherUserPage.viewPagers.anotherUserBeatFragment;
import com.example.musicwithnav.ui.AnotherUserPage.viewPagers.anotherUserInstrumentalFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class AnotherUserPageActivity extends AppCompatActivity {

    private AnotherUserPageViewModel mViewModel;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private TextView userid;

    private ImageView profileImageView;
    private TextView userName;
    private TextView tv_user_details;
    private Button sendMessage;

    private String userUid;

    private String userNamePassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user_page);


        userName = findViewById(R.id.tv_dashboard_userName);
        tv_user_details = findViewById(R.id.tv_dashboard_details);
        profileImageView = findViewById(R.id.imageView_dashboard_userPhoto);
        sendMessage = findViewById(R.id.btn_another_user_send_message);

        sendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("uid", userUid.toString());
            startActivity(intent);
        });




        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userUid= null;
            } else {
                userUid = extras.getString("uid");
                System.out.println(userUid);
            }
        } else {
            userUid = (String) savedInstanceState.getSerializable("uid");
        }

        Gson gson = new Gson();
        String modelAsAString = getIntent().getStringExtra("model");
        Sound modelObject = gson.fromJson(modelAsAString, Sound.class);

        System.out.println(modelObject);

        userNamePassed = modelObject.getSoundVocalName();
        System.out.println("userNamePassed" + userNamePassed);
        databaseRead();

        //userid = findViewById(R.id.tv_another_user_id);

        //userid.setText(userUid);

        tabLayout = findViewById(R.id.tabs_another_user_dashboard);

        viewPager = findViewById(R.id.vp_another_user);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //add Fragment:
        viewPagerAdapter.addFragment(new anotherUserInstrumentalFragment(userUid),"Instrumental");
        viewPagerAdapter.addFragment(new anotherUserAcapellaFragment(userUid),"Acapella");
        viewPagerAdapter.addFragment(new anotherUserBeatFragment(userUid),"Beats");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setProfileImageView(User user) {
        if (user != null) {
            Glide.with(this)
                    .load(user.getUserProfileImageUrl().toString())
                    .into(profileImageView);

        }
    }

    private void databaseRead(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + userNamePassed);

// Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                setProfileImageView(user);
                if (user.getUserDetails() != null){
                    tv_user_details.setText(user.getUserDetails().toString());
                }
                userName.setText(user.getName().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}