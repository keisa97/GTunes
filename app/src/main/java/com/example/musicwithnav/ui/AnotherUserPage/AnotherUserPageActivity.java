package com.example.musicwithnav.ui.AnotherUserPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.musicwithnav.R;
import com.example.musicwithnav.ui.UserDashboard.UserInstrumentalContentFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.ViewPagerAdapter;

public class AnotherUserPageActivity extends AppCompatActivity {

    private AnotherUserPageViewModel mViewModel;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user_page);

        String userUid;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userUid= null;
            } else {
                userUid= extras.getString("uid");
                System.out.println(userUid);
            }
        } else {
            userUid = (String) savedInstanceState.getSerializable("uid");
        }



        //
        viewPager = findViewById(R.id.viewpager_dashboard_user_content);
        //viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        //add Fragment:
        viewPagerAdapter.addFragment(new UserInstrumentalContentFragment(),"My Sounds");


        viewPager.setAdapter(viewPagerAdapter);
    }


}