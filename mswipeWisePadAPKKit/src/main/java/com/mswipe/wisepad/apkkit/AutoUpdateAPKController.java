/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mswipe.wisepad.apkkit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

public class AutoUpdateAPKController
{
	private final String MSWIPER_ERROR_INTERNET = "error connecting to the internet, please try again";
	//private final static String log_tab = "AutoUpdateAPKController=>";
	
	private String url;
	private Handler parentHandler = null;
	

    public AutoUpdateAPKController(Context context,Handler handler)
    {
    	parentHandler = handler;
    }

    protected void processApkUpdateDetails(String url)
	{
		this.url = url;
		new AsynckCall().execute();
	}
    
    
    class AsynckCall extends AsyncTask<Void, Void, String>
    {
    	String errMsg = "";
        
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			InputStream in = null;
	        HttpURLConnection con = null; 
			try
			{

				URL httpurl = new URL(url);
	            con = (HttpURLConnection) httpurl.openConnection();
	            con.setConnectTimeout( 15 * 1000);
	            con.setReadTimeout( 60 * 1000);
	            con.connect();
			   
			   
			   /*if(ApplicationData.IS_DEBUGGING_ON)
					Logs.v(ApplicationData.PackageName, logs_tab + " TaskAPKUpdate downloading apk file " +  url,true, true);
				*/
			    File file = Environment.getExternalStorageDirectory();
			    
				File outputFile = new File(file, "BALiveUpdate.apk");
	            if(outputFile.exists()){
	                outputFile.delete();
	            }
	            FileOutputStream fos =  new FileOutputStream(outputFile);
	            in = con.getInputStream();

			   byte[] buffer = new byte[1024];
	           int len1 = 0;
	            while ((len1 = in.read(buffer)) != -1) {
	                fos.write(buffer, 0, len1);
	            }
	            fos.close();
	            in.close();
	            
	               
			   
			   in.close();
			   
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
				errMsg = (MSWIPER_ERROR_INTERNET + "," +e.getLocalizedMessage());
			}catch (UnknownHostException e) {
				errMsg = (MSWIPER_ERROR_INTERNET + "("+405+")");
				e.printStackTrace();
			}catch (SocketTimeoutException e){
				if(e.getLocalizedMessage().toLowerCase().contains("read timed out")){
					errMsg = (MSWIPER_ERROR_INTERNET + "("+404+")");
				}else{
					errMsg = (MSWIPER_ERROR_INTERNET + "("+403+")");
				}
				e.printStackTrace();
			}catch(ConnectException e){
				errMsg = (MSWIPER_ERROR_INTERNET + "("+302+")");
				e.printStackTrace();
			}catch (IOException e) {
				errMsg = (MSWIPER_ERROR_INTERNET + "," +e.getLocalizedMessage());
				e.printStackTrace();
			} catch (Exception e) {
				errMsg = (MSWIPER_ERROR_INTERNET + ","+e.getLocalizedMessage());
				e.printStackTrace();
			} 
			finally{
				try{
					if(in!=null)
					{
						in.close();
						
					}
				}catch(Exception ex){
					
					
				}
			}
			
			return errMsg;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			parse("", errMsg);
		}
    	
    }


    protected void parse(String httpResponse,String _errMsg) 
   	{
    	
    	Log.d("AutoUpdateAPKController", "Response " + _errMsg);
		
    	String errMsg = _errMsg;
   	  	Message messageToParent = new Message();
   		Bundle messageData = new Bundle();
   		messageToParent.what = 0;
   		messageData.putString("httperror",errMsg);
   		messageToParent.setData(messageData);
   		parentHandler.sendMessage(messageToParent);

   	}

    
}

