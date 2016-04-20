package com.mrtechs.m_park;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener ,GoogleMap.OnMarkerClickListener , GoogleMap.OnMapLongClickListener{

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int RC_SIGN_IN = 9001;
    private String toggle = "normal";

    private JSONArray jsonArray;
    private ProgressDialog progressDialog;
    private Button park;

    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

    private LocationRequest mLocationRequest;

    private Context mContext;
    CircularImageView iv;

    RelativeLayout rl;

    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("MyLoc",Context.MODE_PRIVATE);
        edit = pref.edit();


        rl = (RelativeLayout)findViewById(R.id.rellay);


        park = (Button) findViewById(R.id.button);

        iv = (CircularImageView)findViewById(R.id.imageView);







        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //noinspection deprecation
        mMap = mapFragment.getMap();






        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
            signIn();
            showProgressDialog();
            mMap.setTrafficEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapLongClickListener(this);
            park.setVisibility(View.VISIBLE);


            //  tutorial here

        }


        Boolean isTutorial = pref.getBoolean("tutorial" , false);
//        Log.d("isTutorial" , isTutorial);
        {
            if(!isTutorial)
            {
                showTutorial();
            }
        }


     /*   else
        {
            GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.(this),
                    this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
        }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }






   /*     manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Boolean checkGps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!checkGps) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


        */

        String lt = pref.getString("lat",null);
        String ln = pref.getString("lng",null);

        if(lt!=null&&ln!=null) {




            MarkerOptions mo = new MarkerOptions();
            mMap.addMarker(mo.position(new LatLng(Double.parseDouble(lt), Double.parseDouble(ln))).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_asdasd)).title("Your Car"));
            park.setText(R.string.parked);
        }



        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String sts = pref.getString("ststus",null);
                if(park.getText().equals("PARK")) {

                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    MarkerOptions mo = new MarkerOptions();
                    mMap.addMarker(mo.position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_asdasd)).title("Your Car"));

                    park.setText(R.string.parked);
                    edit.putString("lat",String.valueOf(mLastLocation.getLatitude()));
                    edit.putString("lng",String.valueOf(mLastLocation.getLongitude()));
                    edit.putString("ststus","parked");
                    edit.apply();


                }
                else if(park.getText().equals("PARKED"))
                {
                    String lt = pref.getString("lat",null);
                    String ln = pref.getString("lng",null);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lt),Double.parseDouble(ln)),13));
                }


            }
        });









        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this , iv);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu , popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.supportId)
                        {



                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.dialog);
                            dialog.setTitle(R.string.Support);
                            CircularImageView dialogImage = (CircularImageView)dialog.findViewById(R.id.imageDialog);
                            dialogImage.setImageBitmap(bitmap);
                            dialog.show();
                            Window window = dialog.getWindow();
                            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            Button dialogEmail = (Button)dialog.findViewById(R.id.buttondialog);
                            dialogEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    sendEmail();
                                }
                            });


                        }
                        else if(item.getItemId()==R.id.tutorialId)
                        {
                            showTutorial();
                        }
                        return true;
                    }
                });

                popupMenu.show();


            }
        });

    }


    public void showTutorial()
    {
        Intent intent = new Intent(getBaseContext() , Tutorial.class);
        startActivity(intent);
        edit.putBoolean("tutorial" , true);
        edit.commit();
    }





    @Override
    protected void onStart() {
        super.onStart();

        if(checkPlayServices()) {

            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                showProgressDialog();
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {

                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPlayServices()) {

            // Resuming the periodic location updates
            boolean mRequestingLocationUpdates = false;
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

    }

    protected void onPause() {
        super.onPause();
        if(checkPlayServices()) {
            stopLocationUpdates();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }








    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"mukulraw199517@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "M-Park");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            //Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();
            doSomething(mLastLocation);


        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();

        doSomething(mLastLocation);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void doSomething(Location location)
    {
        Log.d("asdasdasd","do Sometjing");

        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,14));



        String lt = String.valueOf(location.getLatitude());
        String ln = String.valueOf(location.getLongitude());

        new fetchUrl(lt,ln).execute();


    }

    private synchronized void buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).enableAutoManage(this,this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            new loadImage(String.valueOf(acct.getPhotoUrl())).execute();
            Log.d("asdasdasd", String.valueOf(acct.getPhotoUrl()));

        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker.getTitle().equals("Your Car"))
        {

            Snackbar sc = Snackbar.make(rl,"Remove Car from here",Snackbar.LENGTH_LONG).setAction("REMOVE", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    park.setText(R.string.park);
                    edit.remove("lat");
                    edit.remove("lng");
                    edit.remove("ststus");
                    edit.apply();
                    marker.remove();
                }
            });
            sc.show();





        }
        else {

            Snackbar snackbar = Snackbar.make(rl,"Press OPEN to view on Maps",Snackbar.LENGTH_LONG).setAction("OPEN", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double lat = marker.getPosition().latitude;

                    double lng = marker.getPosition().longitude;

                    String format = "geo:0,0?q=" + lat + "," + lng + "( Location title)";

                    Uri uri = Uri.parse(format);


                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(intent);
                    }catch (ActivityNotFoundException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(),"Google Maps is not installed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            snackbar.show();




        }



        return false;

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        switch (toggle)
        {
            case "normal":mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Log.d("asdasasd","satelite toggled");
                toggle="satelite";
                break;
            case "satelite":mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Log.d("asdasasd","normal togglrd");
                toggle="normal";
                break;
        }
    }


    public class loadImage extends AsyncTask<Void,Void,Void>
    {

        String src;
        Bitmap myBitmap,dialogBM;


        loadImage(String src)
        {
        this.src = src;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(src);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                myBitmap = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            try {
                URL url = new URL("https://lh4.googleusercontent.com/-ijnCKTKrhgU/AAAAAAAAAAI/AAAAAAAAAHc/YopZs21TtL4/photo.jpg");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                bitmap = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(myBitmap);

        }
    }





    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        Log.i("DEBUG_TAG",
                "checkGooglePlayServicesAvailable, connectionStatusCode="
                        + checkGooglePlayServices);

        if (GooglePlayServicesUtil.isUserRecoverableError(checkGooglePlayServices)) {
            showGooglePlayServicesAvailabilityErrorDialog(checkGooglePlayServices);
            return false;
        }


        return true;

    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,MainActivity.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                if (dialog == null) {
                    Log.e("DEBUG_TAG",
                            "couldn't get GooglePlayServicesUtil.getErrorDialog");
                    Toast.makeText(getBaseContext(),
                            "incompatible version of Google Play Services",
                            Toast.LENGTH_LONG).show();

                    Button button = (Button)findViewById(R.id.button);
                    button.setVisibility(View.GONE);

                    dialog.show();
                }
                //this was wrong here -->dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    /**
     * Starting the location updates
     * */
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    private void stopLocationUpdates() {

        if(mGoogleApiClient.isConnected()) {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            }
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    public class fetchUrl extends AsyncTask<Void,Void,Void>
    {
        final String lat;
        final String lng;
        List<placesBean> placesBeenparking;
        List<placesBean> placesBeenGas;



        public fetchUrl(String lat,String lng)
        {
            this.lat = lat;
            this.lng = lng;
        }




        @Override
        protected Void doInBackground(Void... params) {


            HandlerHttp handle = new HandlerHttp();




            String url = "https://maps.googleapis.com/maps/api/place/search/json?location="+lat+","+lng+"&rankby=distance&sensor=true&key=AIzaSyB0G7N5-NqllJTY9sABDoNhkbajLveUJMI&types=parking";

            //String url = "https://maps.googleapis.com/maps/api/place/search/json?location=33.6667,73.1667&rankby=distance&sensor=true&key=AIzaSyDtIBsf3Gj-Q5tdlK4PZONxKA9CSGFARl8&types=parking";




            String result_parking = handle.handleresponse(url);

            Log.d("asdasd", result_parking);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result_parking);
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            placesBeenparking = new ArrayList<>();



            for(int i=0;i<jsonArray.length();i++)
            {
                placesBean pb = new placesBean();

                try {
                    JSONObject jobj = (JSONObject) jsonArray.get(i);
                    JSONObject locationObj = ((JSONObject) jsonArray.get(i)).getJSONObject("geometry").getJSONObject("location");

                    pb.setLat(locationObj.optString("lat"));
                    pb.setLng(locationObj.optString("lng"));
                    pb.setTitle(jobj.getString("name"));
                    pb.setVicinity(jobj.getString("vicinity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                placesBeenparking.add(pb);

            }


            String url1 = "https://maps.googleapis.com/maps/api/place/search/json?location="+lat+","+lng+"&rankby=distance&sensor=true&key=AIzaSyB0G7N5-NqllJTY9sABDoNhkbajLveUJMI&types=gas_station";

            //String url = "https://maps.googleapis.com/maps/api/place/search/json?location=33.6667,73.1667&rankby=distance&sensor=true&key=AIzaSyDtIBsf3Gj-Q5tdlK4PZONxKA9CSGFARl8&types=parking";




            String result_gas = handle.handleresponse(url1);

            Log.d("asdasd", result_gas);

            try {
                jsonObject = new JSONObject(result_gas);
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            placesBeenGas = new ArrayList<>();



            for(int i=0;i<jsonArray.length();i++)
            {
                placesBean pb = new placesBean();

                try {
                    JSONObject jobj = (JSONObject) jsonArray.get(i);
                    JSONObject locationObj = ((JSONObject) jsonArray.get(i)).getJSONObject("geometry").getJSONObject("location");

                    pb.setLat(locationObj.optString("lat"));
                    pb.setLng(locationObj.optString("lng"));
                    pb.setTitle(jobj.getString("name"));
                    pb.setVicinity(jobj.getString("vicinity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                placesBeenGas.add(pb);

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            progressDialog.dismiss();
            for(int i=0;i<placesBeenparking.size();i++)
            {
                placesBean pb = placesBeenparking.get(i);


                LatLng markers = new LatLng(Double.parseDouble(pb.getLat()),Double.parseDouble(pb.getLng()));
                mMap.addMarker(new MarkerOptions().title(pb.getTitle()).position(markers).snippet(pb.getVicinity()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parking2)));

            }

            for(int j=0;j<placesBeenGas.size();j++)
            {
                placesBean pb = placesBeenGas.get(j);


                LatLng markers = new LatLng(Double.parseDouble(pb.getLat()),Double.parseDouble(pb.getLng()));
                mMap.addMarker(new MarkerOptions().title(pb.getTitle()).position(markers).snippet(pb.getVicinity()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_gaz)));

            }
            super.onPostExecute(aVoid);
        }
    }

}
