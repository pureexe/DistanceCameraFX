package com.ptlms.distancecamera;

import android.util.FloatMath;

public class CameraProcessor {
	public float getDistance(float High,float XGravity,float DefaultGravity)
	{
		return (float) High*((XGravity/DefaultGravity)/FloatMath.sqrt((float) (1-Math.pow((XGravity/DefaultGravity),2))));		
	}
	public float getHigh(float distance1,float distance2,float High,float XGravity)
	{
		if(distance1<distance2)
		{
			float swaptmp = distance1;
			distance1 = distance2;
			distance2 = swaptmp;
		}
		if(XGravity>=0)
			return (float)((distance1-distance2)/distance1)*High;
		else
			return (float)((distance1+distance2)/distance1)*High;
	}
	
	public float getWide(float distance1,float distance2)
	{
		return (float) Math.sqrt(Math.abs((distance1*distance1)-(distance2*distance2)));
	}
	
	public float getHigh(float distance1,float distance2,float accelerometer)
	{
		return 0;
	}
}
