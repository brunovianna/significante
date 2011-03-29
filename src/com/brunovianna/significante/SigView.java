package com.brunovianna.significante;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

class SigView extends TextView implements View.OnClickListener  {

	private Paint bigPaint, smallPaint;
	private Path pathA, pathN, pathM, pathE, path;
	private String strSifdo, strSifnte, strName, strMean;
	private float spacing, correction;
	public boolean name, layoutOk = false;
	private Canvas meanCanvas, nameCanvas;
	private Bitmap meanBitmap, nameBitmap;

	public boolean buttonPressed = false;
	
	private float spring_x, spring_y;
	private float spring_k = 0.005f;
	private float friction = 0.05f;
	public float x, y = 0;
	public float ax, ay, vx, vy = 0;
	
	private Rect textRect;

	private RefreshHandler mRedrawHandler = new RefreshHandler();

	private long mLastMove = System.currentTimeMillis();

	private long mRefresh = 60;

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			SigView.this.update();
			SigView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};



	public SigView(Context context,  AttributeSet attrs) {
		super(context, attrs);

		this.setOnClickListener(this);

		ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				float h = getHeight();
				float w = getWidth();

				Rect displayRect = new Rect();
				getWindowVisibleDisplayFrame(displayRect);
				
				
//				spring_x = 0;
//				spring_y = 0;

				spacing = h / 4;

				correction = 1.4f;


				// set the color and font size
				bigPaint = new Paint();
				bigPaint.setColor(Color.WHITE);
				bigPaint.setTextSize((int) spacing * correction);
				//bigPaint.setFakeBoldText(true);
				//bigPaint.setAntiAlias(true);
				bigPaint.setStyle(Paint.Style.STROKE);
				bigPaint.setTypeface(Typeface.MONOSPACE);
				bigPaint.setLinearText(true);

				Rect bounds = new Rect();
				bigPaint.getTextBounds("A", 0, 1, bounds);

				pathA = new Path();
				pathN = new Path();
				pathM = new Path();
				pathE = new Path();
				path = new Path();

				char[] c = strName.toCharArray();

				bigPaint.getTextPath("A", 0, 1, 0, 0, pathA);
				bigPaint.getTextPath("N", 0, 1, 0, 0, pathN);
				bigPaint.getTextPath("E", 0, 1, 0, 0, pathE);
				bigPaint.getTextPath("M", 0, 1, 0, 0, pathM);
				bigPaint.getTextPath(c, 0, 4, 0, 0, path);


				// set the color and font size
				smallPaint = new Paint();
				smallPaint.setColor(Color.WHITE);
				smallPaint.setTextSize(8);
				smallPaint.setAntiAlias(true);

				textRect = new Rect();
				bigPaint.getTextBounds(strMean, 0, strMean.length(), textRect);

				
				spring_x = (h-textRect.width())/2;		// ^|v
				spring_y = (w-textRect.height())/2;  // <-- -->

				//		spring_x = (displayFrame.height()-r.width())/2 ;		// ^|v
				//		spring_y = (cWidth-r.height())/2;  // <-- -->

				meanBitmap = Bitmap.createBitmap((int)w, (int)h, Bitmap.Config.ARGB_8888);
				meanCanvas = new Canvas ();
				//	meanCanvas.setDensity(metrics.densityDpi);
				meanCanvas.setBitmap(meanBitmap);
				meanCanvas.rotate(90);
				meanCanvas.translate(spring_x, -spring_y);
				meanCanvas.drawTextOnPath(strSifnte, pathM, 0,0, smallPaint);
				meanCanvas.translate(spacing/1.2f, 0);
				meanCanvas.drawTextOnPath(strSifnte, pathE, 0,0, smallPaint);
				meanCanvas.translate(spacing/1.2f, 0);
				meanCanvas.drawTextOnPath(strSifnte, pathA, 0,0, smallPaint);
				meanCanvas.translate(spacing/1.2f, 0);
				meanCanvas.drawTextOnPath(strSifnte, pathN, 0,0, smallPaint);

				nameBitmap = Bitmap.createBitmap((int)w, (int)h, Bitmap.Config.ARGB_8888);
				nameCanvas = new Canvas ();
				//	nameCanvas.setDensity(metrics.densityDpi);
				nameCanvas.setBitmap(nameBitmap);
				nameCanvas.rotate(90);
				nameCanvas.translate(spring_x, -spring_y);
				nameCanvas.drawTextOnPath(strSifdo, pathN, 0,0, smallPaint);
				nameCanvas.translate(spacing/1.2f, 0);
				nameCanvas.drawTextOnPath(strSifdo, pathA, 0,0, smallPaint);
				nameCanvas.translate(spacing/1.2f, 0);
				nameCanvas.drawTextOnPath(strSifdo, pathM, 0,0, smallPaint);
				nameCanvas.translate(spacing/1.2f, 0);
				nameCanvas.drawTextOnPath(strSifdo, pathE, 0,0, smallPaint);

				layoutOk = true;
			}
		});

		String temp = "SIGNIFICADO ";
		strSifdo = "";
		for (int i=0; i < 30; i++)
			strSifdo=strSifdo.concat(temp);

		temp = "SIGNIFICANTE ";
		strSifnte = "";
		for (int i=0; i < 30; i++)
			strSifnte=strSifnte.concat(temp);

		strName = "NAME";
		strMean = "MEAN";
		
		mRedrawHandler.sleep(60);				

	}

	
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);


		if (layoutOk) {
			canvas.save();
			canvas.translate( x,  y);

			if (name) {
				canvas.drawBitmap(nameBitmap,00,00, null);
			} else {
				canvas.drawBitmap(meanBitmap,00,00,null);
			}


			//canvas.drawText(strMean, 0, 0, bigPaint);
			//canvas.drawTextOnPath(strSifdo, path, 0,0, smallPaint);
			//canvas.drawPath(path, bigPaint);

			canvas.restore();

		}
	}


	public void update() {

		long now = System.currentTimeMillis();

		long diff = now - mLastMove;

		if (diff > mRefresh) {
			//update

			ax = -x * spring_k - vx*friction;
			ay = -y * spring_k - vy*friction;
			vx = vx + ax * diff / 20f;
			vy = vy + ay * diff/ 20f;
			x = x+ vx * diff/ 10f;
			y = y+ vy * diff/ 10f;

			//			x-=5;
			//			y-=5;

			mLastMove = now;

		}

		mRedrawHandler.sleep(mRefresh);

	}

	public void onClick(View v) {

		if (name==false) {
			//			x = y = 0;
			name = true;
		} else {
			name = false;
		}	

	}

}

