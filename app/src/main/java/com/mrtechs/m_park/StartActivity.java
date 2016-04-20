package com.mrtechs.m_park;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private Thread th;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);





        th = new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    Thread.sleep(150);
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                if(hasLocationPermission!=PackageManager.PERMISSION_GRANTED)
                {
                    //request permission
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION} , REQUEST_CODE_ASK_PERMISSIONS);
                }
                else{
                    //permission is already granted

                    th.start();
                }
            }

            else
            {
                th.start();
            }




        }
        else
        {
            Toast.makeText(getBaseContext(),"No internet Connection",Toast.LENGTH_SHORT).show();
            finish();
        }





    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
//permission granted
                    th.start();

                }
                else
                {
                    //permission denied
Toast.makeText(this , "Permission denied" , Toast.LENGTH_SHORT).show();

                    finish();
                }

                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }



    }
}
