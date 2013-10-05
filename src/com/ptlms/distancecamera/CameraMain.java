package com.ptlms.distancecamera;
/** Build On Windows8**/
/** Use Nexus4 for test Device**/

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraMain extends Activity implements SensorEventListener {
	final boolean isDebug=true;
	private DataManager dm;
	private CameraSetting camset;
	private CameraDistance campro;
	private CameraPreview mPreview;
	private CameraSnapshot camsnap;
	private Camera mCamera;
	private SensorManager sensorManager;
	private FrameLayout preview;
	private Vector vex_dist,sensor_acc,sensor_pres;
	private float sensor_acc_sum,sensor_pres_sum;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_main);
		/** Load Camera **/
        mCamera = CameraPreview.getCameraInstance();
        if(mCamera==null)
        	{
        		camset.err_concam();
        	}
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        /** Class init... **/
    	vex_dist= new Vector();
    	sensor_acc=new Vector();
    	sensor_pres=new Vector();
		dm = new DataManager(this);/*** Load DataManager for save data ***/
		camset=new CameraSetting(this,mCamera);
		campro=new CameraDistance();
		camsnap=new CameraSnapshot(this,camset,campro,dm);
        preview.addView(mPreview);
        /** Checking Sensor **/
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
	    if(!sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL))
	    {
	    	camset.err_conacc();
	    }
	    else
	    {
	    	// write sensor detail
	    	Sensor s_detail = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
	    	dm.setString("s_acc_name", s_detail.getName());
	    	dm.setString("s_acc_vender", s_detail.getVendor());
	    	dm.setString("s_acc_maxrange",""+s_detail.getMaximumRange());
	    	dm.setFloat("s_acc_resolution", s_detail.getResolution());
	    	dm.setString("s_acc_version", ""+s_detail.getVersion());
	    	dm.setString("s_acc_power",""+s_detail.getPower());
	    	if(Build.VERSION.SDK_INT>=9)
	    		dm.setString("s_acc_mindelay", ""+s_detail.getMinDelay());
	    }
	    if(!sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL))
	    {
	    	dm.setBool("hasPressure", false);
	    }
	    else
	    {
	    	dm.setBool("hasPressure", true);
	    	Sensor s2_detail = sensorManager.getSensorList(Sensor.TYPE_PRESSURE).get(0);
	    	dm.setString("s_pres_name", s2_detail.getName());
	    	dm.setString("s_pres_vendor", s2_detail.getVendor());
	    	dm.setString("s_pres_maxrange",""+s2_detail.getMaximumRange());
	    	dm.setFloat("s_pres_resolution", s2_detail.getResolution());
	    	dm.setString("s_pres_version", ""+s2_detail.getVersion());
	    	dm.setString("s_pres_power",""+s2_detail.getPower());
	    	if(Build.VERSION.SDK_INT>=9)
	    		dm.setString("s_pres_mindelay", ""+s2_detail.getMinDelay());
	    }
	    	
	    /** First time ask for use Pressure Sensor **
	    if(sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL)&&dm.getBool("AskForPressure"))
	    {
	    	util.errDialog(getString(R.string.cant_conpres),getString(R.string.cant_conpres_msg));
	    	dm.setBool("AskForPressure",false);			    
	    }*/
	    /**One time variable recheck **/
	    camsnap.reset();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	       /*case R.id.set_mode:
	    	   		camset.selectmode();
	            return true;*/
	       case R.id.set_high:
	    	   		camset.set_high();
	    	   		return true;
	       case R.id.set_meter:
	    	   		camset.set_meter();
	    	   		return true;
	       case R.id.repeatment:
   	   			camset.set_repeatment();
   	   		return true;
	       case R.id.manuale_zoom:
	    	   		camset.manuale_zoom();
	    	   		return true;
	      /* case R.id.show_vexdistance:
	    	   		util.okDialog("List"," "+vex_dist+"\n");
	    	   		return true;
	       case R.id.del_vexdistance:
   	   			vex_dist.clear();
	    	   return true;*/
	       case R.id.app_information:
	    	   	camset.app_information();
	       		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	public void onSnapshot(View v)
	{
		Log.d("CFX",">>>> onSnapshot method");
		camsnap.snapshot();
		vex_dist.add(new Float(campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86)));
		//util.toast("Distance = "+vexdistance.lastElement());	
		//util.toast(""+campro.getDistance(dm.getFloat("High"),dm.getFloat("Accelometer"),(float)9.86)+" "+unit);
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
			{
				sensor_acc_sum+=event.values[0];
				sensor_acc.add(event.values[0]);
				int repeat = dm.getInt("repeat_snapshot");
				while(sensor_acc.size()>repeat)
				{
					sensor_acc_sum=sensor_acc_sum-Float.parseFloat(""+sensor_acc.elementAt(0));
					sensor_acc.remove(0);
				}
				dm.setFloat("Accelometer",sensor_acc_sum/sensor_acc.size());
			
			}
		if(event.sensor.getType()==Sensor.TYPE_PRESSURE)
			{

				sensor_pres_sum+=event.values[0];
				sensor_pres.add(event.values[0]);
				int repeat = dm.getInt("repeat_snapshot");
				while(sensor_pres.size()>repeat)
				{
					sensor_pres_sum=sensor_pres_sum-Float.parseFloat(""+sensor_pres.elementAt(0));
					sensor_pres.remove(0);
				}
				dm.setFloat("Pressure",sensor_pres_sum/sensor_pres.size());
			//dm.setFloat("Pressure",event.values[0]);
			}
		camsnap.refreshUI();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sensorManager.unregisterListener(this);
		mCamera.stopPreview();
		mCamera.release();
        mCamera = null;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this);
		// camera will fix soon
	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);
		// camera will fix soon
		
	}
	/** Button listenner **/
	public void clickReset(View v)
	{
		camsnap.reset();
	}
	public void clickHigh(View v)
	{
		dm.setInt("CameraFind",1);
	}
	public void clickWide(View v)
	{
		dm.setInt("CameraFind",2);
	}
}
