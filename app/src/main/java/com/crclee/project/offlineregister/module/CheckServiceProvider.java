package com.crclee.project.offlineregister.module;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckServiceProvider {
    Context context;

    public CheckServiceProvider(Context context){
        this.context=context;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean NisConnected = activeNetwork != null && activeNetwork.isConnected();
        if (NisConnected) {
            //  if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
            else
                return false;
        }
        return false;
    }
}
