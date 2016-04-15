package com.mrtechs.m_park;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);





        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getBaseContext() , MainActivity.class);
                startActivity(i);
                finish();

            }
        });




        ConnectionDetector cd = new ConnectionDetector(this);
        if(cd.isConnectingToInternet())
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Marshmallow+

                if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
                {
                    requestLocationPErmission();
                }
                else
                {
                    th.start();
                }


            } else {
                // Pre-Marshmallow
                th.start();
            }






        }
        else
        {
            Toast.makeText(getBaseContext(),"No internet Connection",Toast.LENGTH_SHORT).show();
            finish();
        }





    }



    private void requestLocationPErmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS_LOCATION,
                    124);




        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {



        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
