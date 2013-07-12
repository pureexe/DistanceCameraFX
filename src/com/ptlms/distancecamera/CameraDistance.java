package com.ptlms.distancecamera;

import android.util.FloatMath;

public class CameraDistance {
	public float getDistance(float High,float XGravity,float DefaultGravity)
	{
		 /* Calculate the distance from device to object using trigonometry.
			Variable :
			High -> the distance from the device to the ground. get from user input or pressure sensor.
			XGravity -> the value of x-axis accelerometer to read the acceleration to calculate angle.
			DefaultGravity -> value of gravity acceleration. As we know that the general average g value is 9.86, but the average gravity in Thailand is 9.875 m/s2.
		 */
		return (float) High*((XGravity/DefaultGravity)/FloatMath.sqrt((float) (1-Math.pow((XGravity/DefaultGravity),2))));	
	}
	public float getHigh(float distance1,float distance2,float High,float XGravity)
	{
		/* Calculate object high use Triangle.
		   Variable :
		   distance1 , distance2 -> distance from getDistance()
		   High -> the distance from the device to the ground. get from user input or pressure sensor.
		   XGravity -> the value of x-axis accelerometer to read the acceleration to calculate angle.
		*/
		
		// swap distance1,distance2
		if(distance1<distance2)
		{
			float swaptmp = distance1;
			distance1 = distance2;
			distance2 = swaptmp;
		}
		// detect device state (angle from ground is less than 90 degree return true if more than return false)
		if(XGravity>=0)
			return (float)((distance1-distance2)/distance1)*High; // less than 90 degree
		else
			return (float)((distance1+distance2)/distance1)*High; // more than 90 degree
	}
	
	public float getWide(float distance1,float distance2)
	{
		return (float) Math.sqrt(Math.abs((distance1*distance1)-(distance2*distance2))); // use Pythagoras.
	}
	
}
