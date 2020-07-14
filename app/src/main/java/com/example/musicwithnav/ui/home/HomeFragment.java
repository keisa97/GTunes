package com.example.musicwithnav.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.musicwithnav.R;
import com.example.musicwithnav.models.Sound;
import com.example.musicwithnav.ui.home.inHomeFragment.ArtistFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.BeatFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.InstrumentalFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.AcapellaFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.ProducerFragment;
import com.example.musicwithnav.ui.home.inHomeFragment.ViewPagerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {



    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";



    private HomeViewModel homeViewModel;

    private View soundsView;
    private RecyclerView myRecylceViewSoundsList;

    List<Sound> soundsList = new ArrayList<>();

    FirebaseRecyclerAdapter<Sound, SoundsViewHolder> adapter;

   // private FirebaseDatabase database; //= FirebaseDatabase.getInstance();


    private DatabaseReference soundsRef, userRef;
    private DatabaseReference samplesRef, songRef, vocalRef;

    private FirebaseAuth auth;

    private String currentUserId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        //for fragment navigation:





        soundsView = inflater.inflate(R.layout.fragment_home, container, false);

        //myRecylceViewSoundsList = soundsView.findViewById(R.id.sounds_list_recycler);
     //   myRecylceViewSoundsList.setHasFixedSize(true);
//        myRecylceViewSoundsList.setLayoutManager(new LinearLayoutManager(getActivity()));


        return soundsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //for fragment navigation:



        /*tabLayout = view.findViewById(R.id.tab_layout_home_fragment);
        viewPager = view.findViewById(R.id.viepager_home_fragment);
        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        //add Fragment:
        viewPagerAdapter.addFragment(new ArtistFragment(),"Artist");
        viewPagerAdapter.addFragment(new ProducerFragment(),"producer");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);*/

        //replaceFragment(getClass().getName());

    }

    @Override
    public void onResume() {
        super.onResume();






        adapterSetter();

        tabLayout = getView().findViewById(R.id.tab_layout_home_fragment);
        viewPager = getView().findViewById(R.id.viepager_home_fragment);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        //add Fragment:
        viewPagerAdapter.addFragment(new AcapellaFragment(),"Acapella");
        viewPagerAdapter.addFragment(new BeatFragment(),"Beat");
        viewPagerAdapter.addFragment(new InstrumentalFragment(),"Instrumental");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    /*private void selectItem(int position) {
        Fragment problemSearch = null, problemStatistics = null;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        String backStateName = null;
        Fragment fragmentName = null;
        boolean fragmentPopped = false;
        switch (position) {
            case 0:
                fragmentName = profile;
                break;
            case 1:
                fragmentName = submissionStatistics;
                break;
        }
    }*/

    /*public static void attachFragment ( int fragmentHolderLayoutId, Fragment fragment, Context context, String tag ) {


        FragmentManager manager = ( (AppCompatActivity) context ).getSupportFragmentManager ();
        FragmentTransaction ft = manager.beginTransaction ();

        if (manager.findFragmentByTag ( tag ) == null) { // No fragment in backStack with same tag..
            ft.add ( fragmentHolderLayoutId, fragment, tag );
            ft.addToBackStack ( tag );
            ft.commit ();
        }
        else {
            ft.show ( manager.findFragmentByTag ( tag ) ).commit ();
        }
    }*/

    public void onTabSelected(int position) {
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Add the new tab fragment
        try {
            fragmentManager.beginTransaction()
                    .replace(R.id.viepager_home_fragment, ArtistFragment.class.newInstance())
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a fragment on top of the current tab
     */
    public void addFragmentOnTop(Fragment fragment) {
        getActivity().
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.viepager_home_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static class SoundsViewHolder extends RecyclerView.ViewHolder {
        TextView soundName, artist, producer;
        ImageView soundImageCover;



        public SoundsViewHolder(@NonNull View itemView) {
            super(itemView);

            soundName = itemView.findViewById(R.id.tv_user_item_sound_name);
            artist = itemView.findViewById(R.id.tv_user_soundItem_artist);
            producer = itemView.findViewById(R.id.tv_user_soundItem_producer);
            soundImageCover = itemView.findViewById(R.id.imageview_user_item_sound_cover);

        }
    }


/*    public void retriveData() {
        soundsList.clear();

        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference()
                .child("sound");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot soundSnapShot : dataSnapshot.getChildren()) {
                    Sound sound = soundSnapShot.getValue(Sound.class);

                    System.out.println("ininini");
                    soundsList.add(sound);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("message", "onCancelled: " + databaseError.getMessage());
            }
        });
    }*/

    private void setData() {
        Query query = FirebaseDatabase.getInstance().getReference().child("sounds");
        FirebaseRecyclerOptions<Sound> options = new FirebaseRecyclerOptions.Builder<Sound>()
                .setQuery(query,Sound.class)
                .build();


        }


        private void adapterSetter(){

            FirebaseRecyclerOptions options =
                    new FirebaseRecyclerOptions.Builder<Sound>()
                            .setQuery(samplesRef, Sound.class)
                            .build();


          adapter
                    = new FirebaseRecyclerAdapter<Sound, SoundsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull SoundsViewHolder holder, int position, @NonNull Sound model) {
                    System.out.println("test in bindeViewModel");

                    holder.soundName.setText(model.getSoundName());
                    holder.artist.setText(model.getSoundVocalName());
                }

                @NonNull
                @Override
                public SoundsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_item, parent, false);
                    SoundsViewHolder viewHolder = new SoundsViewHolder(view);
                    return viewHolder;
                }
            };

//            myRecylceViewSoundsList.setAdapter(adapter);

        }









}