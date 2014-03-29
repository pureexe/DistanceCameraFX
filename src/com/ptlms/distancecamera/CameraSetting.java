package com.ptlms.distancecamera;

import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CameraSetting {
	public final int MODE_DISTANCE = 0;
	public final int MODE_WIDE  = 1;
	public final int MODE_HIGH  = 2;
	private int cam_mode;
	private Activity activity;
	private Float High;
	private DataManager dm;
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
	public void set_gravity_sorce() {
		AlertDialog.Builder	builder = new AlertDialog.Builder(activity);
		String a = "",b = "";
		if(dm.getBool("UseGravity"))
				b=" : "+activity.getString(R.string.active);
		else
			    a=" : "+activity.getString(R.string.active);
			final CharSequence[] items = {activity.getString(R.string.sensor_acc)+a,activity.getString(R.string.sensor_gravity)+b};
		builder.setTitle(activity.getString(R.string.select_gravity_source));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int choose) {
		    		if(choose==0)
		    			dm.setBool("UseGravity",false);
		    		if(choose==1)
		    			dm.setBool("UseGravity",true);
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
					//params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
					camera.autoFocus(null);
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
		if(dm.getInt("Unit")==5)
			return dm.getString("Unit_name");
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
		if(dm.getInt("Unit")==5)
			return (float) dm.getFloat("Unit_size");
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
		final CharSequence[] items = {activity.getString(R.string.m),activity.getString(R.string.cm),activity.getString(R.string.inch),activity.getString(R.string.feet),activity.getString(R.string.yard),activity.getString(R.string.define_new_unit)};
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
		    		if(choose==5)
		    			manule_setmeter(); // define new unit
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	protected void manule_setmeter() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.define_new_unit));
        builder.setCancelable(false);
        LinearLayout lila1= new LinearLayout(activity);
        lila1.setOrientation(1); //1 is for vertical orientation
        final EditText input1 = new EditText(activity); 
        final EditText input2 = new EditText(activity);
        final TextView txt1 = new TextView(activity);
        final TextView txt2 = new TextView(activity);
        txt1.setText(activity.getString(R.string.unit_name));
        txt2.setText(activity.getString(R.string.unit_size));
        lila1.addView(txt1);
        lila1.addView(input1);
        lila1.addView(txt2);
        lila1.addView(input2);
        input2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
        builder.setView(lila1);
        builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {            
        public void onClick(DialogInterface dialog, int whichButton) {
        	try{
        	dm.setString("Unit_name",input1.getText().toString());
        	dm.setFloat("Unit_size",Float.parseFloat(input2.getText().toString()));
        	dm.setInt("Unit",5);
        	}
        	catch (Exception e){
        		dm.setInt("Unit",1);
            	Toast.makeText(activity,activity.getString(R.string.failed_type)+e.getMessage(), Toast.LENGTH_LONG).show();

        	}
        }
        });
        builder.show();
		
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
				if(choice==3)
					license();
				
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
		sensor_detail+=activity.getString(R.string.sensor_name)+"<font color='blue'>"+dm.getString("s_acc_name")+"</font><br>"
					+activity.getString(R.string.sensor_vendor)+"<font color='blue'>"+dm.getString("s_acc_vendor")+"</font><br>"
					+activity.getString(R.string.sensor_version)+"<font color='blue'>"+dm.getString("s_acc_version")+"</font><br>"
					+activity.getString(R.string.sensor_power)+"<font color='blue'>"+dm.getString("s_acc_power")+activity.getString(R.string.amp)+"</font><br>"
					+activity.getString(R.string.sensor_max_range)+"<font color='blue'>"+dm.getString("s_acc_maxrange")+activity.getString(R.string.m_s2)+"</font><br>"
					+activity.getString(R.string.sensor_Resolution_raw)+"<font color='blue'>"+dm.getFloat("s_acc_resolution")+activity.getString(R.string.m_s2)+"</font><br>"
					+activity.getString(R.string.sensor_Resolution)+"<font color='blue'>"+Math.toDegrees(Math.asin(dm.getFloat("s_acc_resolution")/9.86))+activity.getString(R.string.degree)+"</font><br>";
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(R.string.sensor_information))
		.setMessage(Html.fromHtml(sensor_detail))
		.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
		}).show();
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected void sensor_pres_information() {
		String sensor_detail = "";
		sensor_detail+=activity.getString(R.string.sensor_name)+" <font color='blue'>"+dm.getString("s_pres_name")+"</font><br>"
					+activity.getString(R.string.sensor_vendor)+" <font color='blue'>"+dm.getString("s_pres_vendor")+"</font><br>"
					+activity.getString(R.string.sensor_version)+" <font color='blue'>"+dm.getString("s_pres_version")+"</font><br>"
					+activity.getString(R.string.sensor_power)+" <font color='blue'>"+dm.getString("s_pres_power")+activity.getString(R.string.amp)+"</font><br>"
					+activity.getString(R.string.sensor_max_range)+" <font color='blue'>"+dm.getString("s_pres_maxrange")+activity.getString(R.string.hpa)+"</font><br>"
					+activity.getString(R.string.sensor_Resolution_raw)+" <font color='blue'>"+dm.getFloat("s_pres_resolution")+activity.getString(R.string.hpa)+"</font><br>"
					+activity.getString(R.string.sensor_Resolution)+" <font color='blue'>"+SensorManager.getAltitude(1000+dm.getFloat("s_pres_resolution"),1000)+activity.getString(R.string.m)+"</font><br>\n";
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(R.string.sensor_information))
		.setMessage(Html.fromHtml(sensor_detail))
		.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
		}).show();
		
	}
	protected void license() {
		web_dialog("http://pureexe.github.io/DistanceCameraFXinfo/license.html");
	}

	protected void about_dev() {
		web_dialog("https://github.com/pureexe");
	}

	protected void how_to_use() {
		web_dialog("http://pureexe.github.io/DistanceCameraFXinfo/howtouse_mainapp.html");
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
	public void debug_list(String a,String b)
	{
		
		new AlertDialog.Builder(activity)
			.setTitle(a)
			.setMessage(b)
			.setCancelable(false)
			.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
					{
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
	public void err_congrav()
	{
    	new AlertDialog.Builder(activity)
    		.setTitle(activity.getString(R.string.cant_change_grav))
    		.setMessage(activity.getString(R.string.cant_change_grav_msg))
    		.setCancelable(false)
    		.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton)
    				{
    				
    				}
    		}).show();
	}
	public void set_repeatment() {
		// TODO Auto-generated method stub
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText(""+dm.getInt("repeat_snapshot"));
		new AlertDialog.Builder(activity)
			.setTitle(activity.getString(R.string.repeatment))
			.setMessage(activity.getString(R.string.repeatment_num))
			.setView(input)
			.setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int choose) {
					try{
						int i=Integer.parseInt(input.getText().toString());
						dm.setInt("repeat_snapshot",i);
					}
					catch(Exception e)
					{
						Toast.makeText(activity, activity.getString(R.string.fail_is_not_number), Toast.LENGTH_LONG).show();
					}
				}
			}).show();
	}
	
	public void web_dialog(String url){
		AlertDialog.Builder alert = new AlertDialog.Builder(activity); 
//		alert.setTitle("WebView");
		WebView wv = new WebView(activity);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.loadUrl(url);
		wv.setWebViewClient(new WebViewClient() {
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        view.loadUrl(url);

		        return true;
		    }
		});

		alert.setView(wv);
		alert.setNegativeButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int id) {
		        dialog.dismiss();
		    }
		});
		alert.show();
		
	}
}
