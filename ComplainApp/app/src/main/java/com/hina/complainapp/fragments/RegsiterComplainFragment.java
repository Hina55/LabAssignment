package com.hina.complainapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hina.complainapp.BuildConfig;
import com.hina.complainapp.Database.ComplaintDatabase;
import com.hina.complainapp.R;
import com.hina.complainapp.UserHomeActivity;
import com.hina.complainapp.models.ComplaintHelperClass;
import com.hina.complainapp.session.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RegsiterComplainFragment extends Fragment {

    public static final int REQUEST_TAKE_PHOTO = 1;
    TextInputLayout categorySpinner, severitySpinner, descriptionLayout;
    AutoCompleteTextView autoCompleteCategory, autoCompleteSeverity;
    Button photoCapturebtn;
    ImageView selectedImage;
    public static final int CAMERA_PIC_REQUEST = 101;
    String currentPhotoPath;
    ComplaintDatabase complaintDatabase;
    Button btnSaveData;
    TextView checktext;
    String imageToStore;

    private static final long UPDATE_IN_MILLI = 10000;
    private static final long FAST_UPDATE_IN_MILLI = 5000;
    private static final long REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    double longitude, latitude;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ProgressBar progressBar;
    String username,category,severity,description,lati,longi,imageUri;
    Calendar calendar;
    String todaysDate;
    String currentime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_complain_fragment, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Post Complain");

        categorySpinner = view.findViewById(R.id.categoryDropdown);
        severitySpinner = view.findViewById(R.id.severityDropdwon);
        autoCompleteCategory = view.findViewById(R.id.autoCategory);
        autoCompleteSeverity = view.findViewById(R.id.autoSeverity);
        descriptionLayout = view.findViewById(R.id.description);
        photoCapturebtn = view.findViewById(R.id.btnImageCapture);
        selectedImage = view.findViewById(R.id.takenPhoto);
        btnSaveData = view.findViewById(R.id.btn_save_data);
        checktext = view.findViewById(R.id.checktxt);
        progressBar=view.findViewById(R.id.complainProgressBar);
        rootNode=FirebaseDatabase.getInstance();
        reference=rootNode.getReference("Complains");

        descriptionLayout.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionLayout.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);

        complaintDatabase = new ComplaintDatabase(getContext());
        init();

        String[] category = new String[]{
                "E-commerce",
                "General"
        };

        ArrayAdapter<String> categoryData = new ArrayAdapter<String>(getContext(), R.layout.dropdown_menu, category);
        autoCompleteCategory.setAdapter(categoryData);

        String[] severity = new String[]{
                "Low",
                "Medium",
                "High"
        };
        ArrayAdapter<String> severityData = new ArrayAdapter<String>(getContext(), R.layout.dropdown_menu, severity);
        autoCompleteSeverity.setAdapter(severityData);

        photoCapturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();

                Dexter.withActivity((Activity) getContext())
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                mRequestingLocationUpdates = true;
                                Log.e("allowpermission", "allow");
                                setLocation();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                openSettings();
                                Log.e("allowpermission", "cancelled");
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });


        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConnected((UserHomeActivity) getContext())){
                    showCustomDialog();
                }else{
                    //get current date and time
                    calendar = Calendar.getInstance();
                    todaysDate = calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH);
                    //currentime = pad(calendar.get(Calendar.HOUR))+":"+pad(calendar.get(Calendar.MINUTE));
                    currentime = formatTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
                    storeAllValues();
                    storeValuesInFirebase();

                }


            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermissions()) {
            setLocation();

        }
        setLocation();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void setLocation() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        if (mCurrentLocation != null) {
                            latitude = mCurrentLocation.getLatitude();
                            longitude = mCurrentLocation.getLongitude();
                            checktext.setText("Lat: " + latitude + "\nLong: " + longitude);
                        }
                    }
                }).addOnFailureListener((Activity) getContext(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int code = ((ApiException) e).getStatusCode();
                switch (code) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException re = (ResolvableApiException) e;
                        try {
                            re.startResolutionForResult((Activity) getContext(), (int) REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(getContext(), "Please check your Location Settings", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }

    private void init() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        mSettingsClient = LocationServices.getSettingsClient(getContext());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_IN_MILLI);
        mLocationRequest.setFastestInterval(FAST_UPDATE_IN_MILLI);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

    }

    private void openSettings() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        i.setData(uri);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void askCameraPermission() {

        Log.d("tag", "verify permissions, asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(getContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), permissions[1]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), permissions[2]) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) getContext(), permissions, CAMERA_PIC_REQUEST);
        } else {
            dispatchTakePictureIntent();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        askCameraPermission();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                // The picture was returned


                File f = new File(currentPhotoPath);
                selectedImage.setVisibility(View.VISIBLE);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "onActivityResult: Capture Image URI: " + Uri.fromFile(f));
                Log.d("tag", "onActivityResult: Capture Image Name: " + f.getName());

                Log.e("allowpermission", "allow");

                setLocation();
                checktext.setVisibility(View.VISIBLE);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getContext().sendBroadcast(mediaScanIntent);

                //image name variable: f.getName()
                //image path variable: contentUri
                imageToStore=contentUri.toString().trim();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("allowpermission", "cancelled");
                mRequestingLocationUpdates = false;

            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Complain_" + timeStamp + "_";

        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); /* this will not save image in gallery  */


        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // this will save image in gallery
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }


    }

    public void storeAllValues() {

        try {
            SessionManager sessionManager = new SessionManager(getContext());
            HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
            String user_name = userDetails.get(SessionManager.KEY_USERNAME);
            if (!autoCompleteCategory.getText().toString().isEmpty() &&
                    !autoCompleteSeverity.getText().toString().isEmpty() &&
                    !descriptionLayout.getEditText().getText().toString().isEmpty() &&
                    imageToStore != null &&
                    latitude != 0.0 &&
                    longitude != 0.0)
            {
                complaintDatabase.storeValues(new ComplaintHelperClass(user_name,
                        imageToStore,
                        latitude,
                        longitude,
                        autoCompleteCategory.getText().toString(),
                        autoCompleteSeverity.getText().toString(),
                        descriptionLayout.getEditText().getText().toString(),
                        todaysDate,currentime));

            }
            else{
                Toast.makeText(getContext(), "Values are Empty", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please connect to the internet to proceed further.")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        builder.show();
    }

    private boolean isConnected(UserHomeActivity loginActivity) {

        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificonn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileconn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wificonn != null && wificonn.isConnected()) || (mobileconn != null && mobileconn.isConnected())){
            return true;
        }else{
            return false;
        }

    }

    public void storeValuesInFirebase(){
        progressBar.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference("Complains");
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        String user_name = userDetails.get(SessionManager.KEY_USERNAME);
        username=user_name;
        category=autoCompleteCategory.getText().toString();
        severity=autoCompleteSeverity.getText().toString();
        description=descriptionLayout.getEditText().getText().toString();
        lati=String.valueOf(latitude);
        longi=String.valueOf(longitude);
        imageUri=imageToStore;
        ComplaintHelperClass objectComplainClass = new ComplaintHelperClass(username,
                imageUri,
                Double.parseDouble(lati),
                Double.parseDouble(longi),
                category,severity,description,
                todaysDate,currentime);

        if (!category.isEmpty() &&
                !severity.isEmpty() &&
                !description.isEmpty() && imageUri != null && !lati.equals(null) && !longi.equals(null)) {
            reference.child(user_name).push().setValue(objectComplainClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Complaint Registered!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    autoCompleteCategory.setText("");
                    autoCompleteSeverity.setText("");
                    descriptionLayout.getEditText().setText("");
                    selectedImage.setVisibility(View.GONE);
                    checktext.setVisibility(View.GONE);
                    loadFragment(new MainFragment());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(getContext(), "Please Enter Information", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void loadFragment(Fragment secondFragment) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,secondFragment);
        fragmentTransaction.commit();
    }
    public String formatTime(int hourOfDay, int minute) {

        String timeSet;
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            timeSet = "pm";
        } else if (hourOfDay == 0) {
            hourOfDay += 12;
            timeSet = "am";
        } else if (hourOfDay == 12)
            timeSet = "pm";
        else
            timeSet = "am";

        String minutes;
        if (minute < 10)
            minutes = "0" + minute;
        else
            minutes = String.valueOf(minute);

        return String.valueOf(hourOfDay) + ':' + minutes + " " + timeSet;
    }



}
