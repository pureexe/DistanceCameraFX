package com.ptlms.distancecamera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
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
	private Camera camera;
	public CameraSetting(Activity a,Camera b)
	{
		activity=a;
		camera=b;
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
	/** First time Dialog is temporaty disable ***
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
    	
	}*/
	public void set_high() {
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		String a = "",b = "";
		if(dm.getBool("UsePressure"))
				b=" : "+activity.getString(R.string.active);
		else
			    a=" : "+activity.getString(R.string.active);
			final CharSequence[] items = {activity.getString(R.string.manuale_input)+a,activity.getString(R.string.use_pressure)+b};
		builder.setTitle(activity.getString(R.string.set_high));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int choose) {
		    		if(choose==0)
		    			manuale_high();
		    		if(choose==1)
		    				pressure_capture();
		    }

		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void manuale_high()
	{
		dm.setBool("UsePressure",false);
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.manuale_input));
		builder.setMessage(activity.getString(R.string.manuale_input_msg)+getStringUnit());
		builder.setCancelable(false);
		final EditText input = new EditText(activity);
		//input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
		input.setText(""+gethigh());
		builder.setView(input);
		builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int whichButton) {
				try{
					High = Float.parseFloat(input.getText().toString());
					High = High/changeUnit();
			  		dm.setFloat("High",High);
				}
				catch(Exception e)
				{
					Toast.makeText(activity, activity.getString(R.string.fail_is_not_number), Toast.LENGTH_LONG).show();
				}
				
			
			}
		});
		builder.show();
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
	public void manuale_zoom()
	{
		if(Build.VERSION.SDK_INT<8)
		{
			AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
			builder.setTitle(activity.getString(R.string.unsupport)).setMessage(activity.getString(R.string.err_sdkver_a)+" 2.2 "+activity.getString(R.string.err_sdkver_b));
			builder.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
						
					}
				});
			builder.show();
		}
		else{
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.manuale_zoom));
		builder.setMessage(activity.getString(R.string.manuale_zoom_msg));
		builder.setCancelable(false);
		final SeekBar input = new SeekBar(activity);
		final Camera.Parameters params = camera.getParameters();
		input.setProgress(params.getZoom());
		input.setMax(params.getMaxZoom());
		builder.setView(input);
		builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {	
		public void onClick(DialogInterface dialog, int whichButton) {
				try{
					params.setZoom(input.getProgress());
					camera.setParameters(params);
				}
				catch(Exception e)
				{
					Toast.makeText(activity,"zoom faile type = "+e, Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.show();
		}
		}
	public void pressure_capture()
	{
		if(dm.getBool("hasPressure")==false)
		{
			AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
			builder.setTitle(activity.getString(R.string.cant_conpres)).setMessage(activity.getString(R.string.cant_conpres_msg));
			builder.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
						
					}
				});
			builder.show();
		}
		else{
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
	}
//	public int getmode()
//	{
//		return cam_mode;
//	}
	public String getStringUnit()
	{
		if(dm.getInt("Unit")==0)
			return activity.getString(R.string.m);
		if(dm.getInt("Unit")==1)
			return activity.getString(R.string.cm);
		if(dm.getInt("Unit")==2)
			return activity.getString(R.string.inch);
		if(dm.getInt("Unit")==3)
			return activity.getString(R.string.feet);
		if(dm.getInt("Unit")==4)
			return activity.getString(R.string.yard);
		return "Error Unit fault";
	}
	public float changeUnit(){
		if(dm.getInt("Unit")==0)
			return (float) 1.0;
		if(dm.getInt("Unit")==1)
			return (float) 100.0; // 1 meter = 100 cm
		if(dm.getInt("Unit")==2)
			return (float) 39.3700787; // 1 meter = 39 inch
		if(dm.getInt("Unit")==3)
			return (float) 3.2808399; // 1 meter = 3.2808399 feet
		if(dm.getInt("Unit")==4)
			return (float) 1.0936133; // 1 meter = 1.0936133
		return (float)1.0;
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public float gethigh()
	{
		if(Build.VERSION.SDK_INT > 4 && dm.getBool("UsePressure"))
			return SensorManager.getAltitude(dm.getFloat("PressureGround"), dm.getFloat("Pressure"))*changeUnit();
		else
			return dm.getFloat("High")*changeUnit();
	}
	public void set_meter() {
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		final CharSequence[] items = {activity.getString(R.string.m),activity.getString(R.string.cm),activity.getString(R.string.inch),activity.getString(R.string.feet),activity.getString(R.string.yard)};
		builder.setTitle(activity.getString(R.string.set_meter));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int choose) {
		    		
		    		if(choose==0)
		    			dm.setInt("Unit",0); // set meter
		    		if(choose==1)
		    			dm.setInt("Unit",1); // set cm
		    		if(choose==2)
		    			dm.setInt("Unit",2); // set inch
		    		if(choose==3)
		    			dm.setInt("Unit",3); // set feet
		    		if(choose==4)
		    			dm.setInt("Unit",4); // set yard
	
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void app_information() {
		final CharSequence[] items = {activity.getString(R.string.how_to_use),activity.getString(R.string.sensor_information),activity.getString(R.string.about_dev),activity.getString(R.string.license)};
		new AlertDialog.Builder(activity).setTitle(R.string.app_information).setItems(items, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int choice) {
				if(choice==0)
					how_to_use();
				if(choice==1)
					sensor_information();
				if(choice==2)
					about_dev();
				
			}
		}).create().show();
		}
	public void sensor_information() {
		final CharSequence[] items = {activity.getString(R.string.sensor_acc),activity.getString(R.string.sensor_pressure)};
		new AlertDialog.Builder(activity).setTitle(R.string.app_information).setItems(items, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int choice) {
				if(choice==0)
					sensor_acc_information();
				if(choice==1)
					if(Build.VERSION.SDK_INT>=9)
						sensor_pres_information();
					else
						Toast.makeText(activity,activity.getString(R.string.err_sdkver_a)+" 2.3 "+activity.getString(R.string.err_sdkver_b),Toast.LENGTH_LONG).show();
				
			}
		}).create().show();		
	}
	public void sensor_acc_information() {
		String sensor_detail = "";
		sensor_detail+=activity.getString(R.string.sensor_name)+" "+dm.getString("s_acc_name")+"\n"
					+activity.getString(R.string.sensor_vendor)+" "+dm.getString("s_acc_vendor")+"\n"
					+activity.getString(R.string.sensor_version)+" "+dm.getString("s_acc_version")+"\n"
					+activity.getString(R.string.sensor_power)+" "+dm.getString("s_acc_power")+activity.getString(R.string.amp)+"\n"
					+activity.getString(R.string.sensor_max_range)+" "+dm.getString("s_acc_maxrange")+activity.getString(R.string.m_s2)+"\n"
					+activity.getString(R.string.sensor_Resolution_raw)+" "+dm.getFloat("s_acc_resolution")+activity.getString(R.string.m_s2)+"\n"
					+activity.getString(R.string.sensor_Resolution)+" "+Math.toDegrees(Math.asin(dm.getFloat("s_acc_resolution")/9.86))+activity.getString(R.string.degree)+"\n";
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(R.string.sensor_information))
		.setMessage(sensor_detail)
		.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
		}).show();
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected void sensor_pres_information() {
		String sensor_detail = "";
		sensor_detail+=activity.getString(R.string.sensor_name)+" "+dm.getString("s_pres_name")+"\n"
					+activity.getString(R.string.sensor_vendor)+" "+dm.getString("s_pres_vendor")+"\n"
					+activity.getString(R.string.sensor_version)+" "+dm.getString("s_pres_version")+"\n"
					+activity.getString(R.string.sensor_power)+" "+dm.getString("s_pres_power")+activity.getString(R.string.amp)+"\n"
					+activity.getString(R.string.sensor_max_range)+" "+dm.getString("s_pres_maxrange")+activity.getString(R.string.m_s2)+"\n"
					+activity.getString(R.string.sensor_Resolution_raw)+" "+dm.getFloat("s_pres_resolution")+activity.getString(R.string.m_s2)+"\n"
					+activity.getString(R.string.sensor_Resolution)+" "+SensorManager.getAltitude(1000+dm.getFloat("s_pres_resolution"),1000)+activity.getString(R.string.m)+"\n";
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(R.string.sensor_information))
		.setMessage(sensor_detail)
		.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
		}).show();
		
	}
	protected void about_dev() {
		// TODO Auto-generated method stub
		
	}

	protected void how_to_use() {
		// TODO Auto-generated method stub
		
	}
	public void err_concam()
	{
		
		new AlertDialog.Builder(activity)
			.setTitle(activity.getString(R.string.cant_concamera))
			.setMessage(activity.getString(R.string.cant_concamera_msg))
			.setCancelable(false)
			.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
					{
						activity.finish();
					}
			}).show();
	}
	public void err_conacc()
	{
    	new AlertDialog.Builder(activity)
    		.setTitle(activity.getString(R.string.cant_conacc))
    		.setMessage(activity.getString(R.string.cant_conacc_msg))
    		.setCancelable(false)
    		.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton)
    				{
    					activity.finish();
    				}
    		}).show();
	}
	
	
}
