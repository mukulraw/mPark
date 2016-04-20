package com.mrtechs.m_park;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


@SuppressWarnings("ALL")
class ConnectionDetector {
    private final Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

}
