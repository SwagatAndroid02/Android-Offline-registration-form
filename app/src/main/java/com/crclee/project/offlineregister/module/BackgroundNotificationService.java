package com.crclee.project.offlineregister.module;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.crclee.project.offlineregister.R;
import com.crclee.project.offlineregister.activity.MainActivity;
import com.crclee.project.offlineregister.database.UserCredentialDatabase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundNotificationService extends Service implements ATaskCompleteListner<String>{

    public static final int notify = 10000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    UserCredentialDatabase userCredentialDatabase;

    CheckServiceProvider checkServiceProvider;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        checkServiceProvider=new CheckServiceProvider(this);
        userCredentialDatabase=new UserCredentialDatabase(this);
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
    }

    @Override
    public void onCompleteTask(String result) {

    }

    @Override
    public void onCompleteTaskWithParams(String result) {


        Intent intent = new Intent(BackgroundNotificationService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("OfflineRegister")
                .setContentText(result)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(result))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        userCredentialDatabase.removeAllItems();

        mTimer.cancel();    //For Cancel Timer
        stopSelf();
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //call api

                    if(checkServiceProvider.isConnected()){
                        List<String> userDetails=new ArrayList<>();
                        userDetails=userCredentialDatabase.getUserDetails();
                        String phone=userDetails.get(0);
                        String email=userDetails.get(1);
                        String age=userDetails.get(2);
                        String name=userDetails.get(3);

                        callApi(phone,email,age,name);

                    }

                }
            });
        }
    }

    private void callApi(String phone, String email, String age, String name) {
        String url="http://13.126.160.18/movilo/mobi_index.php?page=user";

        List<NameValuePair> parameters = null;
        parameters=new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("emailid", email));
        parameters.add(new BasicNameValuePair("name", "Test123"));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("age", "25"));
       /* UserRegistration usrRedg=new UserRegistration(parameters);
        usrRedg.execute(params);*/

       BackgroundServerCaller backgroundServerCaller=new BackgroundServerCaller(BackgroundNotificationService.this,this,parameters);
       backgroundServerCaller.execute(url);
    }


    private void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();

    }
}
