package com.ptlms.distancecamera;


import java.util.Vector;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class CameraGetSharedPicture extends Activity {
	 Activity activity;
	 RelativeLayout body;
	 ImageView paintpoint;
	 int count_point=0;
	 float distance;
	 Vector<Coord> location_point;
	 CameraSetting camset;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getsharedpicture);
		camset=new CameraSetting(this,null);
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	            if (imageUri != null) {
	            	ImageView a = (ImageView)findViewById(R.id.imageView1);
	            	try{
	            	a.setImageURI(imageUri);
	            	}
	            	catch(Exception e)
	            	{
	            		Toast.makeText(getApplicationContext(), getString(R.string.set_picture_failed)+e,Toast.LENGTH_LONG).show();
	            	}
	            }
	            
	        }
	    }
        activity=this;
        location_point=new Vector();
        View view1 = findViewById(R.id.view1);
        body = (RelativeLayout)findViewById(R.id.body_getsharedpicture);
        view1.setOnTouchListener(new OnTouchListener() {
            
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub
				paintpoint = new ImageView(activity);
				paintpoint.setBackgroundResource(R.drawable.reddot);
				paintpoint.setX(e.getX());
				paintpoint.setY(e.getY());
				body.addView(paintpoint);
				if(count_point<4){
					location_point.add(new Coord(e.getX(),e.getY()));
				}
				count_point++;
				if(count_point==2)
				{
						AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		                builder.setTitle(getString(R.string.how_long));
		                builder.setMessage(getString(R.string.distance_between_two_point_in_unit)+camset.getStringUnit());
		                builder.setCancelable(false);
		                final EditText input = new EditText(activity);
		                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
		                builder.setView(input);
		                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {            
		                public void onClick(DialogInterface dialog, int whichButton) {
		                                try{
		                                        distance = Float.parseFloat(input.getText().toString());
		                                  }
		                                catch(Exception e)
		                                {
		                                        Toast.makeText(activity,"failed type :"+e.getMessage(), Toast.LENGTH_LONG).show();
		                                }
		                        }
		                });
		                builder.show();
				}
		                if(count_point==4)
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			                builder.setTitle(getString(R.string.calculated_distance));
			                builder.setMessage(getString(R.string.result_distance_between_two_point)+getDiffDistance(distance,location_point.get(0).x,location_point.get(0).y,location_point.get(1).x,location_point.get(1).y,location_point.get(2).x,location_point.get(2).y,location_point.get(3).x,location_point.get(3).y)+camset.getStringUnit());
			                builder.setCancelable(false);
			                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           
			                public void onClick(DialogInterface dialog, int whichButton) {
			                	activity.finish();	
			                	return ;
			                	}
			                });
			                builder.show();
						}
		             
				return false;
			}		
		});
	}
	
public float getDiffDistance(float diffdis1, float x1, float y1,
		float x2, float y2, float x3, float y3, float x4, float y4) {
	float pixel_ratio=(float) (diffdis1/Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2)));
	float pixel_dist=(float) Math.sqrt(Math.pow(y4-y3, 2)+Math.pow(x4-x3, 2));
	  return (float)pixel_dist*pixel_ratio ;
}




@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
//	super.onBackPressed();
	if(count_point==0)
		activity.finish();
	else{
		count_point--;
		location_point.remove(count_point);
		body.removeViewAt(body.getChildCount()-1);
	}
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.getsharedpicture_main, menu);
    return true;
	}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case R.id.how_to_use:
        		camset.web_dialog("http://pureexe.github.io/DistanceCameraFXinfo/howtouse_pixelcompare.html");
    	   		return true;
       case R.id.set_meter:
    	   		camset.set_meter();
    	   		return true;
        default:
            return super.onOptionsItemSelected(item);
    }
}
}