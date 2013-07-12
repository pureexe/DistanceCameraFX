package com.ptlms.distancecamera;

import android.util.FloatMath;

public class CameraDistance {
	public float getDistance(float High,float XGravity,float DefaultGravity)
	{
		// �ӹǳ�Ҩҡ���˹�����ͧ�֧�ѵ������ ���⡳�Ե�
		// High ������зҧ�ҡ����ǳ�������ͧ�֧��� �Ѻ�������ҧ ��á�͡�ͧ����� ���ͼ�ҹ pressure sensor
		// XGravity ��� ��Ңͧ accelerometer�᡹ x ������ҹ����������ͧ�ҡ�ç�����ǧ
		// DefaultGravity ��ͤ�Ҥ���������ͧ�ҡ�ç�����ǧ ���ҧ���������ѹ��� ����� ��� g ����� ��� 9.86 ��������»�����¡�Ѻ�� 9.875 m/s2
		return (float) High*((XGravity/DefaultGravity)/FloatMath.sqrt((float) (1-Math.pow((XGravity/DefaultGravity),2))));	
	}
	public float getHigh(float distance1,float distance2,float High,float XGravity)
	{
		// �ӹǳ�Ҥ����٧�ͧ�ѵ���������������������
		// distance1 ��� distance2 ������зҧ������Ҩҡ����ʹ getDistance()
		// High ������зҧ�ҡ����ǳ�������ͧ�֧��� �Ѻ�������ҧ ��á�͡�ͧ����� ���ͼ�ҹ pressure sensor
		// XGravity ��� ��Ңͧ accelerometer�᡹ x ������ҹ����������ͧ�ҡ�ç�����ǧ (㹷�������Ǩ�ͺ��ҵ������ͧ ��������� �ҡ�� ��ҨеԴź)
		
		// ��Ѻ��� ��� distance1 �繵���ҡ���ͨ�������ջѭ������ź
		if(distance1<distance2)
		{
			float swaptmp = distance1;
			distance1 = distance2;
			distance2 = swaptmp;
		}
		// ��Ǩ�ͺ��ҡ��������
		if(XGravity>=0)
			return (float)((distance1-distance2)/distance1)*High; // ��ҡ��
		else
			return (float)((distance1+distance2)/distance1)*High; // �����
	}
	
	public float getWide(float distance1,float distance2)
	{
		return (float) Math.sqrt(Math.abs((distance1*distance1)-(distance2*distance2))); // �Է�����
	}
	
}
