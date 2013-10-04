package com.ptlms.distancecamera;

import android.app.Activity;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CameraSnapshot {
	private Activity activity;
	private CameraSetting camset;
	private CameraDistance campro;
	private DataManager dm;
	private TextView UIdistance;
	private Button btnReset;
	private float distance1;
	private float distance2;
	private View btnLayout;
	CameraSnapshot(Activity a,CameraSetting c,CameraDistance p,DataManager d)
	{
		activity=a;
		camset=c;
		dm=d;
		campro=p;
		UIdistance=(TextView)activity.findViewById(R.id.UIdistance);
		btnReset=(Button)activity.findViewById(R.id.buttonreset);
		btnLayout=(View)activity.findViewById(R.id.buttonContainer);
	}
	public void refreshUI() {
		if(dm.getInt("State")==0)
			{
			distance1=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);	
			UIdistance.setText(activity.getString(R.string.distance)+" "+distance1+" "+camset.getStringUnit());
			}
		if(dm.getInt("State")==1)
			{
				if(dm.getInt("CameraFind")==1)
					{
						distance2=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);
						UIdistance.setText(activity.getString(R.string.distance)+" "+distance1+" "+camset.getStringUnit()+"\n"+activity.getString(R.string.high)+" = "+campro.getHigh(distance1, distance2, camset.gethigh(), (float)9.86)+camset.getStringUnit());
					}
				if(dm.getInt("CameraFind")==2)
					{
						distance2=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);
						UIdistance.setText(activity.getString(R.string.distance)+" "+distance1+" "+camset.getStringUnit()+"\n"+activity.getString(R.string.wide)+" = "+campro.getWide(distance1, distance2)+camset.getStringUnit());
					}
			}
		}
	
	public void snapshot() {
		if(dm.getInt("State")==0)
		{
			distance1=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);
			Toast.makeText(activity,activity.getString(R.string.distance)+" = "+distance1+camset.getStringUnit(),Toast.LENGTH_LONG).show();
			dm.setInt("State",1);
			btnReset.setVisibility(View.VISIBLE);
			btnLayout.setVisibility(View.VISIBLE);
			dm.setInt("CameraFind",0);
		}
		if(dm.getInt("State")==1)
		{
			if(dm.getInt("CameraFind")==0)
			{
				distance1=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);
				Toast.makeText(activity,activity.getString(R.string.distance)+" = "+distance1+camset.getStringUnit(),Toast.LENGTH_LONG).show();
			}
			if(dm.getInt("CameraFind")==1)
			{
				distance2=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);
				Toast.makeText(activity,activity.getString(R.string.high)+" = "+campro.getHigh(distance1, distance2, camset.gethigh(), (float)9.86)+camset.getStringUnit(),Toast.LENGTH_LONG).show();
			}
			if(dm.getInt("CameraFind")==2)
			{
				distance2=campro.getDistance(camset.gethigh(),dm.getFloat("Accelometer"),(float)9.86);
				Toast.makeText(activity,activity.getString(R.string.wide)+" = "+campro.getWide(distance1, distance2)+camset.getStringUnit(),Toast.LENGTH_LONG).show();
			}
		}
	}
	public void reset() {
		dm.setInt("State",0);
		btnReset.setVisibility(View.INVISIBLE);
		btnLayout.setVisibility(View.INVISIBLE);
	}
}
