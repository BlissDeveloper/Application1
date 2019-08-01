package com.example.application1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.strictmode.CleartextNetworkViolation;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference locationsRef;

    private ImageView imageViewCamera;
    private ImageView imageViewChat;
    private ImageView imageViewSendLocation;
    private ImageView imageViewSendNetwork;
    private ImageView imageViewGallery;
    private ImageView imageViewMap;

    private CardView cardViewCamera;
    private CardView cardViewLocation;
    private CardView cardViewChat;

    private Toolbar toolbarHome;
    private ProgressDialog progressDialog;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    private final int CAMERA_REQUEST = 12;
    private final int IMAGE_CAPTURE_REQUEST = 1;
    private final int LOCATION_REQUEST = 2;
    private final int LOCATION_SETTINGS_REQUEST = 3;
    private final int WIFI_STATE_REQUEST = 4;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationManager locationManager;

    double[] latLong = new double[2];

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private Location location;

    //Misc
    private WifiManager wifiManager;
    private WifiInfo connection;
    private TelephonyManager telephonyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        locationsRef = FirebaseFirestore.getInstance().collection("User_Locations");

        toolbarHome = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbarHome);
        getSupportActionBar().setTitle("Home");

        progressDialog = new ProgressDialog(HomeActivity.this);

        imageViewCamera = findViewById(R.id.imageViewCamera);
        imageViewChat = findViewById(R.id.imageViewChat);
        imageViewSendLocation = findViewById(R.id.imageViewLocation);
        imageViewSendNetwork = findViewById(R.id.imageViewNetwork);
        imageViewGallery = findViewById(R.id.imageViewGallery);
        imageViewMap = findViewById(R.id.imageViewMap);

        cardViewCamera = findViewById(R.id.cardView);
        cardViewLocation = findViewById(R.id.cardView3);
        cardViewChat = findViewById(R.id.cardView2);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connection = wifiManager.getConnectionInfo();


        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);

        imageViewSendNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestWifiStatePermission();
                    Log.d("Avery", "Not yet");
                } else {
                    showNetworkDialog();
                    Log.e("Avery", "Permitted");
                }
            }
        });

        imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        imageViewSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    //Not granted
                    requestLocation();
                } else {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.d("Avery", "GPS disabled");
                        showLocationDialog();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case Dialog.BUTTON_POSITIVE:
                                        //Send
                                        sendCurrentLocation();
                                        break;
                                    case Dialog.BUTTON_NEGATIVE:
                                        dialogInterface.dismiss();
                                        break;
                                }
                            }
                        };
                        builder.setTitle("Send Location?")
                                .setPositiveButton("YES", clickListener)
                                .setNegativeButton("NO", clickListener)
                                .show();
                    }
                }
            }
        });

        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCameraPermissionGranted()) {
                    //Open Camera
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageViewCamera.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                signOutUser();
                Log.d("Avery", "Logout");
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageThumbnail = (Bitmap) extras.get("data");
            Intent intent = new Intent(HomeActivity.this, ImagePreviewActivity.class);
            intent.putExtra("image", imageThumbnail);
            startActivity(intent);
        } else if (resultCode == LOCATION_SETTINGS_REQUEST) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d("Avery", "GPS disabled");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, LOCATION_SETTINGS_REQUEST);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case Dialog.BUTTON_POSITIVE:
                                //Send
                                sendCurrentLocation();
                                break;
                            case Dialog.BUTTON_NEGATIVE:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                };
                builder.setTitle("Send Location?")
                        .setPositiveButton("YES", clickListener)
                        .setNegativeButton("NO", clickListener)
                        .show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Granted
                    Log.d("Avery", "Result: " + grantResults[0]);
                } else {
                    Toast.makeText(this, "Camera permission is required to access this feature", Toast.LENGTH_SHORT).show();
                }
                break;
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Location permission is required to access this feature", Toast.LENGTH_SHORT).show();
                }
                break;
            case WIFI_STATE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Wifi information permission is required to access this feature.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void showNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case Dialog.BUTTON_POSITIVE:
                        //Yes
                        String BSSID = connection.getBSSID();
                        String networkOperator = telephonyManager.getNetworkOperator();

                        if (!TextUtils.isEmpty(networkOperator)) {

                            if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                                if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_WIFI_STATE) ==
                                        PackageManager.PERMISSION_GRANTED) {
                                    final GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
                                    if (location != null) {

                                        int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                                        int mnc = Integer.parseInt(networkOperator.substring(3));
                                        Log.d("Avery", "BSSID: " + BSSID);
                                        Log.d("Avery", "MCC: " + mcc);
                                        Log.d("Avery", "MNC: " + mnc);
                                        Log.d("Avery", "LAC: " + location.getLac());
                                        Log.d("Avery", "CID: " + location.getCid());

                                        /*
                                        1. NEEDS MORE CHECKING. KUNG GSM BA SIYA OR ANYWHAT
                                        2. FIRESTORE INSERTION
                                         */
                                    }
                                }
                            }

                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        //No
                        dialogInterface.dismiss();
                        break;
                }
            }
        };
        builder.setPositiveButton("Yes", clickListener)
                .setNegativeButton("No", clickListener)
                .show();
    }

    //Class that handles location changes
    public void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setCancelable(false)
                .setTitle("Location Request")
                .setMessage("This feature uses location service to be used. Kindly activate it.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, LOCATION_SETTINGS_REQUEST);
                    }
                });


        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, LOCATION_SETTINGS_REQUEST);
    }

    public void sendCurrentLocation() {
        //For progress
        progressDialog.setTitle("Sending location...");
        progressDialog.show();

        try {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GPSUtils gpsTracker = new GPSUtils(HomeActivity.this);
                Location location = gpsTracker.getLocation();

                if (location != null) {
                    Log.d("Avery", "Lat: " + location.getLatitude());
                    Log.d("Avery", "Long: " + location.getLongitude());
                    Toast.makeText(this, "Lat: " + location.getLatitude() + "\n " + "Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    insertLocationInformation(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

    public void insertLocationInformation(double lat, double longti) {
        Map<String, Object> map = new ArrayMap<>();
        map.put("email", mAuth.getCurrentUser().getEmail());
        map.put("latitude", lat);
        map.put("longtitude", longti);
        map.put("date", getCurrentDate());
        map.put("time", getCurrentTime());
        map.put("user_id", currentUserID);

        locationsRef.document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, "Location sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    public String getCurrentTimestamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public String getCurrentTime() {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss aa");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public void requestWifiStatePermission() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, WIFI_STATE_REQUEST);
    }

    public void requestLocation() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
    }

    public double[] getLatLong() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = task.getResult();

                        if (currentLocation != null) {
                            Log.d("Avery", "Lat: " + currentLocation.getLatitude());
                            Log.d("Avery", "Long: " + currentLocation.getLongitude());
                        } else {
                            Log.e("Avery", "Current location is null");
                        }
                    } else {

                    }
                }
            });
        }
        return latLong;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST);
        }
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
    }

    public boolean isCameraPermissionGranted() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        } else {
            return true;
        }
    }

    public void signOutUser() {
        mAuth.signOut();
        goToLogin();
    }

    public void goToLogin() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
