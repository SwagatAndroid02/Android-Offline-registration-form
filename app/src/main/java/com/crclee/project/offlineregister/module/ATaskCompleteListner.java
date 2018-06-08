package com.crclee.project.offlineregister.module;

public interface ATaskCompleteListner<String> {
    public void onCompleteTask(String result);
    public void onCompleteTaskWithParams(String result);

}
