package com.ptlms.distancecamera;

import android.util.FloatMath;

public class CameraDistance {
	public float getDistance(float High,float XGravity,float DefaultGravity)
	{
		// คำนวณหาจากตำแหน่งเครื่องถึงวัตถุโดยใช้ ตรีโกณมิติ
		// High คือระยะทางจากบริเวณตัวเครื่องถึงพื้น รับเข้ามาได้ทาง การกรอกของผู้ใช้ หรือผ่าน pressure sensor
		// XGravity คือ ค่าของ accelerometerในแกน x เพื่ออ่านความเร่งเนื่องจากแรงโน้มถ่วง
		// DefaultGravity คือค่าความเร่งเนื่องจากแรงโน้มถ่วง อย่างที่เรารู้กันว่า ทั่วไป ค่า g เฉลี่ย คือ 9.86 แต่ค่าเฉลี่ยประเทศไทยกลับเป็น 9.875 m/s2
		return (float) High*((XGravity/DefaultGravity)/FloatMath.sqrt((float) (1-Math.pow((XGravity/DefaultGravity),2))));	
	}
	public float getHigh(float distance1,float distance2,float High,float XGravity)
	{
		// คำนวณหาความสูงของวัตถุโดยใช้สามเหลี่ยมคล้าย
		// distance1 และ distance2 คือระยะทางที่ได้มาจากเมทธอด getDistance()
		// High คือระยะทางจากบริเวณตัวเครื่องถึงพื้น รับเข้ามาได้ทาง การกรอกของผู้ใช้ หรือผ่าน pressure sensor
		// XGravity คือ ค่าของ accelerometerในแกน x เพื่ออ่านความเร่งเนื่องจากแรงโน้มถ่วง (ในที่นี้ใช้ตรวจสอบว่าตัวเครื่อง ก้มหรือเงย หากเงย ค่าจะติดลบ)
		
		// สลับค่า ให้ distance1 เป็นตัวมากเพื่อจะได้ไม่มีปัญหาเวลาลบ
		if(distance1<distance2)
		{
			float swaptmp = distance1;
			distance1 = distance2;
			distance2 = swaptmp;
		}
		// ตรวจสอบว่าก้มหรือเงย
		if(XGravity>=0)
			return (float)((distance1-distance2)/distance1)*High; // ถ้าก้ม
		else
			return (float)((distance1+distance2)/distance1)*High; // ถ้าเงย
	}
	
	public float getWide(float distance1,float distance2)
	{
		return (float) Math.sqrt(Math.abs((distance1*distance1)-(distance2*distance2))); // ปิทาโกรัส
	}
	
}
