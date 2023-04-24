package com.mswipe.wisepad.apkkit;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog 
{

		String Message = "";
	    Context context = null;
	    @Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.progressdlg);

			TextView msg = (TextView) findViewById(R.id.tvmessagedialogtitle);
			msg.setText(Message);
			
		}

	    public CustomProgressDialog(Context context, String msg) {
	        super(context, R.style.styleCustDlg);
	    	this.context = context;
	        Message = msg;
	    	this.setTitle("");
	    	this.setCancelable(false);

	    }

}
