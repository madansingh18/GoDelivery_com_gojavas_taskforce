package com.mswipe.wisepad.apkkit;

public interface WisePadControllerListener 
{
	public void onError(String error, int errorCode);
	public void onMswipeAppInstalled();
	public void onMswipeAppUpdated();
}