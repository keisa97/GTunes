package com.example.musicwithnav.ui.UserDashboard;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicwithnav.FirebaseUserDAO;
import com.example.musicwithnav.R;
import com.example.musicwithnav.models.User;
import com.example.musicwithnav.ui.MyAccount.ui.myaccount.MyAccount_activity;
import com.example.musicwithnav.ui.home.inHomeFragment.ViewPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class UserDashboardFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    private static final String TAG = "ProfileActivity";


    private UserDashboardViewModel mViewModel;
    private ImageView profileImageView;
    private TextView userName;
    Bitmap bitmap;
    FirebaseAuth mAuth;
    private Button btn_logout;

    private Button btn_edit_details;

    private TextView tv_user_details;

    private FirebaseUser user;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    //request codes
    final int TAKE_IMAGE_CODE = 1001;
    final int IMAGE_FROM_GALLEY_CODE = 1002;

    private String userDetails;


    //callback when our user is Logged in/Logged out
    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            //if the user null ->go login
            if (FirebaseAuth.getInstance().getCurrentUser()==null){
                startActivity(new Intent(getActivity(), MyAccount_activity.class));
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);

    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    public static UserDashboardFragment newInstance() {
        return new UserDashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_dashboard_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init:

        mAuth = FirebaseAuth.getInstance();


        userName = view.findViewById(R.id.tv_dashboard_userName);
        profileImageView= view.findViewById(R.id.imageView_dashboard_userPhoto);
        btn_logout = view.findViewById(R.id.btn_userDashboard_logout);

        btn_edit_details = view.findViewById(R.id.btn_user_edit_details);

        tv_user_details = view.findViewById(R.id.tv_dashboard_details);

        profileImageView.setOnClickListener((v -> {

            selectImage(getContext());
/*
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager())!=null){
                startActivityForResult(intent,TAKE_IMAGE_CODE);
            }*/
        }));

        btn_logout.setOnClickListener(v -> logout());


        btn_edit_details.setOnClickListener(v -> { showDialog(); });



        user = FirebaseAuth.getInstance().getCurrentUser();

        //user.getPhotoUrl();

        setProfileImageView(user);

        //profileImageView.setImageBitmap(bitmap);


        //set user when singed in:
       // databaseUserSet();

        //set user description
        if(tv_user_details.toString() == ""){
            tv_user_details.setText("set description for yourself");
        }
        databaseRead();

        //TODO:move to a class(MVC)
        /*//re to message in json (message Table)
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");

        //for creating new line- (datum)
        messageRef.push().setValue("hi "+user.getDisplayName() +new Date().toString());

        //read the data:
        ArrayList<String> data = new ArrayList<>();
        //3) ways: Single -> fetch all the data once
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String value = child.getValue(String.class);
                    data.add(value);

                }
                System.out.println(data);
                //recyclerView
                System.out.println();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        //if we have a user:
        //sender ID:
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //FirebaseDAO.shared.saveMessage(new Message("text",null,uid));



        //for the tabs:

        tabLayout = view.findViewById(R.id.tabs_user_dashboard);
        viewPager = view.findViewById(R.id.viewpager_dashboard_user_content);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        //add Fragment:
        viewPagerAdapter.addFragment(new UserInstrumentalContentFragment(),"My Instrumental");
        viewPagerAdapter.addFragment(new UserAcapellaContentFragment(),"My Acapella");
        viewPagerAdapter.addFragment(new UserBeatsContentFragment(),"My Beats");
        //viewPagerAdapter.addFragment(new UserCollaborationsFragment(),"Collaborations");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserDashboardViewModel.class);
        // TODO: Use the ViewModel
        databaseRead();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //
        //if (requestCode==TAKE_IMAGE_CODE){
        if(resultCode == RESULT_OK) {

            switch (requestCode){
                case TAKE_IMAGE_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                   profileImageView.setImageBitmap(bitmap);
                    handleUpload(bitmap);

                case IMAGE_FROM_GALLEY_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        verifyStoragePermissions(getActivity());
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                profileImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                handleUpload(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }

            }


        }
    }

    private void handleUpload(Bitmap bitmap) {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("uploading");
        progressDialog.show();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid+".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "onFailure: ",e.getCause() );
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage((int)progress+"% uploaded");
            }
        });
    }


    private void getDownloadUrl(StorageReference reference){


        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                setUserProfileUrl(uri);
                Log.e(TAG, "onSuccess: "+ uri );
                //setProfileImageView(user);
            }
        });
    }



    private void setUserProfileUrl(Uri uri){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), " profile Image Fail to upload", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, TAKE_IMAGE_CODE);

                } else if (options[item].equals("Choose from Gallery")) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , IMAGE_FROM_GALLEY_CODE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    /*public void dataIntoFirebase(){
        FirebaseSoundDAO.shared.saveSampleSoundName(new Sound("soundName",
                "creator name", "url",
                "might be an int","creatorID","soundID","downloadUrl","ss","s"),"sample");

    }*/


    private void setProfileImageView(FirebaseUser user){
         if (user!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileImageView);
            userName.setText(user.getDisplayName().toString());

        }
    }
    //dialog for user description
    private void showDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_user_details,null);
        mBuilder.setTitle("describe yourself");
        Spinner mSpinner = mView.findViewById(R.id.planets_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sounds_types));




        //getting result from edit text: - using mView and the EditText(final)

        final EditText et_dialog_user_details = mView.findViewById(R.id.et_dialog_user_details);



//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(adapter);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                userDetails =  et_dialog_user_details.getText().toString();
                //databaseDetailsSet();
                databaseUserSet();


            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void databaseUserSet(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        FirebaseUserDAO.shared.saveUser(new User(user.getDisplayName(), user.getUid(), user.getPhotoUrl().toString(), Calendar.getInstance().getTime().toString(),userDetails));

    }

    private void databaseDetailsSet(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseUserDAO.shared.saveDetailsOnly(new User(user.getDisplayName()));
    }

    private void databaseRead(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + user.getDisplayName());

// Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println(user);
                //if (userDetails != null){
                    tv_user_details.setText(user.getUserDetails().toString());

                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }



}
