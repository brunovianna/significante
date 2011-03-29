package com.brunovianna.significante;


import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.brunovianna.significante.SigView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;



public class Significante extends Activity {

	private SigView sigView;
	private Display display;

	private SensorManager myManager;
	private List<Sensor> sensors;
	private Sensor accSensor;
	private long shakeTime, lastUpdate = -1;
	private float acc_x, acc_y, acc_z;
	private float acc_last_x, acc_last_y, acc_last_z;
	private static final int SHAKE_THRESHOLD = 400;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		display = getWindowManager().getDefaultDisplay(); 
				
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		
		//sigView  = new SigView (this, display.getWidth(), display.getHeight(), metrics);
		setContentView(R.layout.main);
		sigView = (SigView) findViewById(R.id.sig);
		
        final Button button = (Button) findViewById(R.id.button1);
        final TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	sigView.buttonPressed = true;
            	table.setVisibility(View.INVISIBLE);
            }
        });

		// Set Sensor + Manager
		myManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensors = myManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size() > 0)
		{
			accSensor = sensors.get(0);
		}
		
		myManager.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME); 	



	}

	private final SensorEventListener mySensorListener = new SensorEventListener()
	{

		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			Random random = new Random();
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 100) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				acc_x = event.values[SensorManager.DATA_X];
				acc_y = event.values[SensorManager.DATA_Y];
				acc_z = event.values[SensorManager.DATA_Z];

				float speed = Math.abs(acc_x+acc_y+acc_z - acc_last_x - acc_last_y - acc_last_z) / diffTime * 10000;
				if ((speed > SHAKE_THRESHOLD)&&(curTime-shakeTime > 1000)) {
					// yes, this is a shake action! Do something about it!
					shakeTime = curTime;
					if (sigView!=null) {
						if (sigView.name)
							sigView.name = false;
						else
							sigView.name = true;
						sigView.x = random.nextInt((int) (Math.abs(acc_x)*20))-acc_x*10;
						sigView.y = random.nextInt((int) (Math.abs(acc_y)*20))-acc_y*10;
						if (acc_x < 0) sigView.x = -sigView.x;
						if (acc_y < 0) sigView.y = -sigView.y;
						sigView.update();
						sigView.invalidate();
					}
				}
				acc_last_x = acc_x;
				acc_last_y = acc_y;
				acc_last_z = acc_z;
			}
		}
	};

//	@Override
	protected void onResume()
	{
		super.onResume();
		myManager.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME); 	
	}

	@Override
	protected void onStop()
	{    	
		super.onStop();
		myManager.unregisterListener(mySensorListener);
	}

}


