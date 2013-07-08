package com.ptlms.distancecamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogUtil {
	public AlertDialog.Builder alert;
	Activity activity;
	Context context;
	public DialogUtil(Activity act,Context con)
	{
		activity=act;
		context=con;
		alert = new AlertDialog.Builder(activity);
	}
	public void okDialog(String title,String msg)
	{
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			
			}
		});
		alert.show();
	}
	public void errDialog(String title,String msg)
	{
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setCancelable(false);
		alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				activity.finish();
			}
		});
		alert.show();
	}
	public void toast(String str)
	{
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}
	
	
}
