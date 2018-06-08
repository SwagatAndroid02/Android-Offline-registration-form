package com.crclee.project.offlineregister.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crclee.project.offlineregister.R;
import com.crclee.project.offlineregister.database.UserCredentialDatabase;
import com.crclee.project.offlineregister.module.ATaskCompleteListner;
import com.crclee.project.offlineregister.module.BackgroundNotificationService;
import com.crclee.project.offlineregister.module.BackgroundServerCaller;
import com.crclee.project.offlineregister.module.CheckServiceProvider;
import com.crclee.project.offlineregister.module.UserDataitems;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ATaskCompleteListner<String>{

    EditText et_phone,et_email,et_password,et_confirm_pass;
    Button btn_register;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar loading;

    CheckServiceProvider serviceProvider;

    UserCredentialDatabase userCredentialDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceProvider=new CheckServiceProvider(MainActivity.this);
        userCredentialDatabase=new UserCredentialDatabase(MainActivity.this);
        userCredentialDatabase.removeAllItems();

        et_phone=findViewById(R.id.et_phone);
        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        et_confirm_pass=findViewById(R.id.et_confirm_pass);

        loading=findViewById(R.id.loading);

        btn_register=findViewById(R.id.btn_register);

        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v==btn_register){

            String phone,email,pass,confirm_pass;
            phone=et_phone.getText().toString();
            email=et_email.getText().toString();
            pass=et_password.getText().toString();
            confirm_pass=et_confirm_pass.getText().toString();

            if(!phone.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !confirm_pass.isEmpty()){

                if(pass.equals(confirm_pass)){
                    if(email.matches(emailPattern)){
                        if (phone.length()!=10) {
                            Toast.makeText(getApplicationContext(),"Enter valid phone number!!",Toast.LENGTH_SHORT).show();
                        }else {
                            if(serviceProvider.isConnected()){
                                loading.setVisibility(View.VISIBLE);
                                registerUser(phone,email);
                            }else {
                               saveInDatabse(phone,email);
                               Toast.makeText(getApplicationContext(),"Credential saved.",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Enter valid email address!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"password not matching",Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(getApplicationContext(),"all fields are mandatory",Toast.LENGTH_SHORT).show();
            }

        }

    }



    private void registerUser(String phone, String email) {

        String url="http://13.126.160.18/movilo/mobi_index.php?page=user";

        List<NameValuePair> parameters = null;
        parameters=new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("emailid", email));
        parameters.add(new BasicNameValuePair("name", "Test123"));
        parameters.add(new BasicNameValuePair("phone", phone));
        parameters.add(new BasicNameValuePair("age", "25"));
       /* UserRegistration usrRedg=new UserRegistration(parameters);
        usrRedg.execute(params);*/

        BackgroundServerCaller backgroundService=new BackgroundServerCaller(MainActivity.this,this,parameters);
        backgroundService.execute(url);
    }

    @Override
    public void onCompleteTask(String result) {

    }

    @Override
    public void onCompleteTaskWithParams(String result) {
        loading.setVisibility(View.GONE);
        showNotification(result);

    }

    private void saveInDatabse(String phone, String email) {
        userCredentialDatabase.removeAllItems();
        userCredentialDatabase.saveDataInDb(phone,email,"test123","25");
        //getDatabseItems();
        startService(new Intent(MainActivity.this, BackgroundNotificationService.class));

    }


    private void showNotification(String result){
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
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
    }

}
