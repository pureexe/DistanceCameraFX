package com.ptlms.distancecamera;
/** Build On Windows8**/
/** Use Nexus4 for test Device**/

import java.util.ArrayList;
import java.util.Vector;


import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class CameraMain extends Activity implements SensorEventListener {
	final boolean isDebug=true;
	private DataManager dm;
	private DialogUtil util;
	private CameraSetting camset;
	private CameraDistance campro;
	private CameraPreview mPreview;
	private CameraSnapshot camsnap;
	private Camera mCamera;
	private SensorManager sensorManager;
	private FrameLayout preview;
	private Vector vex_dist ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_main);
		vex_dist= new Vector();
		util = new DialogUtil(this,getApplicationContext());/*** Load DialogUtil for use dialog***/
		dm = new DataManager(this);/*** Load DataManager for save data ***/
		camset=new CameraSetting(this);
		campro=new CameraDistance();
		camsnap=new CameraSnapshot(this,camset,campro,util,dm);
		/** Load Camera **/
        mCamera = CameraPreview.getCameraInstance();
        if(mCamera==null)
        	util.errDialog(getString(R.string.cant_concamera),getString(R.string.cant_concamera_msg));	
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        /** Checking Sensor **/
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
	    if(!sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL))
	    {
	    	util.errDialog(getString(R.string.cant_conacc),getString(R.string.cant_conacc_msg));
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
	      /* case R.id.show_vexdistance:
	    	   		util.okDialog("List"," "+vex_dist+"\n");
	    	   		return true;
	       case R.id.del_vexdistance:
   	   			vex_dist.clear();
	    	   return true;*/
	    	   		
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
			dm.setFloat("Accelometer",event.values[0]);
		if(event.sensor.getType()==Sensor.TYPE_PRESSURE)
			dm.setFloat("Pressure",event.values[0]);
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
