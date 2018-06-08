package com.crclee.project.offlineregister.module;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class BackgroundServerCaller extends AsyncTask<String,Void,String> {


    private ATaskCompleteListner<String> callback;
    private Context context;
    private List<NameValuePair> parameters;

    public BackgroundServerCaller(Context context, ATaskCompleteListner<String> cb,List<NameValuePair> parameters) {
        this.context = context;
        this.callback = cb;
        this.parameters=parameters;
    }

    @Override
    protected String doInBackground(String... params) {
        String url=params[0];

        try {
            if(!parameters.isEmpty()){

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String response;
                return response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            }else {
                URL Url = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) Url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                        //append means to add something at the end
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(!parameters.isEmpty()){
            callback.onCompleteTaskWithParams(s);
        }else {
            callback.onCompleteTask(s);
        }
    }
}
