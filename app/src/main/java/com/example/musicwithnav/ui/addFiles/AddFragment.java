package com.example.musicwithnav.ui.addFiles;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import android.widget.Toast;

import com.example.musicwithnav.FirebaseSoundDAO;
import com.example.musicwithnav.R;
import com.example.musicwithnav.models.Sound;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    private AddViewModel mViewModel;

    private StorageReference mStorageRef;
    private static final int PICK_SOUND_REQUEST = 222;
    private static final int PICK_IMAGE_REQUEST = 111;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Buttons
    private Button btnFileChoose;
    private Button btnUpload;
    private Button btnRecordSound;


    //ImageView
    private ImageView imageView;
    private ImageView btnImageChoose;
    private CropImageView cropImageView;

    //bitmap- image for sound file
    private Bitmap bm;



    //EditText for use in the Dialog
    //EditText et_dialog_sound_name;
    private String soundType;
    private String soundName;

    //a Uri object to store file path
    //getting the sound object
    private Uri soundFilePath;
    private Uri soundImagePath;


    //for save in database:
    private String soundNameToDatabase;
    private String soundBeatProducerID;
    private String soundBeatProducerName;
    private String soundVocalID;
    private String soundVocalName;
    private String soundImageUrl;
    private String soundDownloadUrl;
    private String soundLength;
    private String soundID;

    private boolean soundUploaded = false;
    private boolean imageUploaded = false;
    private boolean readyToUpload = false;


    private static final String TAG = "UserUploadAudio";

    private static final String LOG_TAG = "AudioRecordTest";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    //private RecordButton recordButton = null;
    private MediaRecorder recorder = null;

    //private PlayButton   playButton = null;
    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnFileChoose = view.findViewById(R.id.btnChooseAudio);
        btnImageChoose = view.findViewById(R.id.btn_image_choose_for_sound);
        btnUpload = view.findViewById(R.id.btn_sum_sound_upload);
        cropImageView = view.findViewById(R.id.cropImageView);




        //for dialog
        final EditText et_dialog_sound_name = view.findViewById(R.id.et_dialog_sound_name);


        btnFileChoose.setOnClickListener(v -> {
            showSoundFileChooser();
            //showDialog();
        });

        btnUpload.setOnClickListener(v -> {

            uploadSoundAndImageFinal();

        });

        btnImageChoose.setOnClickListener(v -> {
            //for simple image chooser without cropping
            //showSoundImageChooser();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setMaxCropResultSize(1000,1000)
                    .setFixAspectRatio(true)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(getActivity(),this);


        });


        //upload the sound to firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();


        //upload recorded audio part


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddViewModel.class);
        // TODO: Use the ViewModel


    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //for cropping use
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                    btnImageChoose.setImageBitmap(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                System.out.println(error);
            }
        }



            switch (requestCode){
                case PICK_SOUND_REQUEST:

            //get data for sound file(mp3)

            if (requestCode == PICK_SOUND_REQUEST) {
                if (resultCode == RESULT_OK) {
                    try {


                        soundFilePath = data.getData();
                        showDialog();
                        System.out.println(soundFilePath.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }




                case  PICK_IMAGE_REQUEST:
            //option 2: without cropping image
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (resultCode == RESULT_OK) {


                    soundImagePath = data.getData();

                    try {

                        bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), soundImagePath);

                        btnImageChoose.setImageBitmap(bm);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }



            }


    }


    private void verifyStoragePermissions(FragmentActivity activity) {
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

    private void showSoundFileChooser() {

        Intent soundChooseIntent = new Intent();
        soundChooseIntent.setType("audio/*");
        soundChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(soundChooseIntent,"select audio"),PICK_SOUND_REQUEST);

    }

    private void showSoundImageChooser(){
        Intent imageChooseIntent = new Intent();
        imageChooseIntent.setType("image/*");
        imageChooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageChooseIntent, "Select Picture"), PICK_IMAGE_REQUEST);



    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) ;

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    /*class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }*/



    //dialog for choose name and type of the uploaded sound
    private void showDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_sound_details,null);
        mBuilder.setTitle("Choose Sound Details");
        Spinner mSpinner = mView.findViewById(R.id.planets_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
               android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sounds_types));




        //getting result from edit text: - using mView and the EditText(final)

        final EditText et_dialog_sound_name = mView.findViewById(R.id.et_dialog_sound_name);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("")){
                    Toast.makeText(getContext(),
                            " Selected "+mSpinner.getSelectedItem().toString()+" is ready for upload!"
                            , Toast.LENGTH_SHORT)
                            .show();
                    dialog.dismiss();
                     soundType = mSpinner.getSelectedItem().toString();
                     soundName = et_dialog_sound_name.getText().toString();
                    System.out.println(soundType);
                    System.out.println(soundName);
                    //handleSoundUpload(); --  only if need to upload image and sound separate


                }
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

    private void handleSoundUpload(){


        if (soundFilePath!=null){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("uploading sound");
            progressDialog.show();
            progressDialog.setCancelable(false);


            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("audio/mp3")
                    .build();


        String fileStorageInDatabase = "sound/"+user.getUid()+"/"+soundType+"/"+soundName+".mp3";

        //upload file
        StorageReference soundRef = mStorageRef.child(fileStorageInDatabase);

        soundRef.putFile(soundFilePath,metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        getSoundFileDownloadUrl(soundRef);
                        progressDialog.dismiss();

                        //Toast.makeText(getContext(), "file uploaded!", Toast.LENGTH_SHORT).show();


                        //databaseSet();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Sorry, the file didn't uploaded!"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage((int)progress+"% uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "File didn't upload", Toast.LENGTH_SHORT).show();
            }
        });
        }else
        {
            Toast.makeText(getContext(), "No audio file selected", Toast.LENGTH_LONG).show();
        }
    }

    private void handleImageUpload(Bitmap bitmap) {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("uploading image");
        progressDialog.show();
        progressDialog.setCancelable(false);



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);





        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("SoundsImages/"+uid+"/"+soundType+"/"+soundName)
                .child(uid+".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //soundImageUrl = String.valueOf(taskSnapshot.getMetadata());
                        getImageFileDownloadUrl(reference);



                        progressDialog.dismiss();


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
                //progressDialog.setMessage((int)progress+"% uploaded");
            }
        });
    }

    private void databaseSet(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        soundVocalName = user.getDisplayName();
        soundVocalID = user.getUid();

        FirebaseSoundDAO.shared.saveSampleSoundName(new Sound(soundName,
                "beat name", "url",
                soundVocalID,soundVocalName,soundImageUrl,
                        soundDownloadUrl,"ss","s",0)

                ,soundType,

                "",soundName,"","",
                soundVocalID,soundVocalName,soundImageUrl,soundDownloadUrl);

    }

    private void getImageFileDownloadUrl(StorageReference reference) {


        imageUploaded = false;


        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                //soundDownloadUrl = uri.toString();
                soundImageUrl = uri.toString();
                imageUploaded = true;
                checkIfSoundAndImageUploaded();

                Log.e(TAG, "onSuccess: " + uri);
            }
        });

    }

    private void getSoundFileDownloadUrl(StorageReference reference) {

        soundUploaded = false;

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                soundDownloadUrl = uri.toString();
                soundUploaded = true;
                checkIfSoundAndImageUploaded();

                //databaseSet();

                Log.e(TAG, "onSuccess: " + uri);
            }
        });

    }

    private void checkIfSoundAndImageUploaded(){

        readyToUpload = false;
        if (soundUploaded && imageUploaded && soundFilePath!=null){
            readyToUpload = true;
        }
        //when all is ready to upload ->:
        if (readyToUpload){
            databaseSet();
            Toast.makeText(getContext(), "file uploaded!", Toast.LENGTH_SHORT).show();

        }

    }

    //last part of uploading the file
    // uploading the mp3 and the image

    private void uploadSoundAndImageFinal(){
        if (bm == null){
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.temp_cover_album_sample_image);

        }

        handleImageUpload(bm);

        handleSoundUpload();

    //than - verify we got both files Url and set all in database
        checkIfSoundAndImageUploaded();


    }

    /*public static Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }*/



}
