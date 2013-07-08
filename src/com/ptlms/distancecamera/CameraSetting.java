package com.ptlms.distancecamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class CameraSetting {
	public final int MODE_DISTANCE = 0;
	public final int MODE_WIDE  = 1;
	public final int MODE_HIGH  = 2;
	private int cam_mode;
	private Activity activity;
	private Float High;
	private DataManager dm;
	private boolean usepressure;
	public CameraSetting(Activity a)
	{
		activity=a;
		dm = new DataManager(a);
		
	}
	public void selectmode() {
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		final CharSequence[] items = {activity.getString(R.string.distance),activity.getString(R.string.wide),activity.getString(R.string.high)};
		builder.setTitle(activity.getString(R.string.set_mode));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int choose) {
		    		cam_mode=choose;
		    		dm.setInt("CameraMode",cam_mode);
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void pressurechoosedialog()
	{
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
    	builder.setTitle("Want use pressure?")
    	.setMessage("Enable Pressure y/n")
    	.setCancelable(false)
    	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dm.setBool("UsePressure", true);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dm.setBool("UsePressure",false);
			}
		})
    	.show();
    	dm.setBool("AskForPressure",false);
    	
	}
	public void set_high() {
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		String a = "",b = "";
		if(dm.getBool("UsePressure"))
				b=" : "+activity.getString(R.string.active);
		else
			    a=" : "+activity.getString(R.string.active);
			final CharSequence[] items = {activity.getString(R.string.manuel_input)+a,activity.getString(R.string.use_pressure)+b};
		builder.setTitle(activity.getString(R.string.set_high));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int choose) {
		    		if(choose==0)
		    			manuel_high();
		    		if(choose==1)
		    				pressure_capture();
		    }

		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void manuel_high()
	{
		dm.setBool("UsePressure",false);
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.manuel_input));
		builder.setMessage(activity.getString(R.string.manuel_input_msg));
		builder.setCancelable(false);
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText("11");
		builder.setView(input);
		builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int whichButton) {
				High = Float.parseFloat(input.getText().toString());
				High = High/changeUnit();
		  		dm.setFloat("High",High);
			}
		});
		builder.show();
	}
	public void pressure_capture()
	{
		dm.setBool("UsePressure",true);
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.captureing)).setMessage(activity.getString(R.string.captureground));
		builder.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
					dm.setFloat("PressureGround",dm.getFloat("Pressure"));
				}
			});
		builder.show();
	}
//	public int getmode()
//	{
//		return cam_mode;
//	}
	public String getStringUnit()
	{
		if(dm.getInt("Unit")==0)
			return "meter";
		if(dm.getInt("Unit")==1)
			return "centimeter";
		return "Error Unit fault";
	}
	public float changeUnit(){
		if(dm.getInt("Unit")==0)
			return (float) 1.0;
		if(dm.getInt("Unit")==1)
			return (float) 100.0;
		return (float)1.0;
	}
	public float gethigh()
	{
		if(dm.getBool("UsePressure"))
			return SensorManager.getAltitude(dm.getFloat("PressureGround"), dm.getFloat("Pressure"))*changeUnit();
		else
			return dm.getFloat("High")*changeUnit();
	}
	public void set_meter() {
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		final CharSequence[] items = {activity.getString(R.string.m),activity.getString(R.string.cm)};
		builder.setTitle(activity.getString(R.string.set_mode));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int choose) {
		    		if(choose==0)
		    			dm.setInt("Unit",0); // set meter
		    		if(choose==1)
		    			dm.setInt("Unit",1); // set cm
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
