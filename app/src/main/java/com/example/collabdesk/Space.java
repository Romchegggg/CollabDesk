package com.example.collabdesk;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Space extends SurfaceView implements SurfaceHolder.Callback {
    final static String TAG = "Space";
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ScaleGestureDetector mScaleDetector;
    int mode;
    Matrix mMatrix = new Matrix();
    float mScaleFactor = 1.f;
    float mTouchX;
    float mTouchY;
    float mTouchBackupX;
    float mTouchBackupY;
    float mTouchDownX;
    float mTouchDownY;

    Rect boundingBox = new Rect();

    private SpaceInt spaceInt;

    private Paint backgroundPaint = new Paint();
    private Paint paintText = new Paint();
    private Paint paintPicture = new Paint();

    Timer timer;

    TimerTask task1;




    Intent myIntent = new Intent(this.getContext(), Space.class);

    public Space(Context context) {
        super(context);
        setId(R.id.space_view);
        getHolder().addCallback(this);
        this.setCameraDistance(0.001f);

        SurfaceHolder sh = this.getHolder();
        sh.addCallback(this);

        if (context instanceof SpaceInt) {
            spaceInt = (SpaceInt) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SpaceInt");
        }

        // for zooming (scaling) the view with two fingers
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        boundingBox.set(0, 0, 1024, 768);

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        setFocusable(true);

        // initial center/touch point of the view (otherwise the view would jump
        // around on first pan/move touch
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mTouchX = metrics.widthPixels / 2;
        mTouchY = metrics.heightPixels / 2;

        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        paintText.setColor(Color.RED);
        paintText.setTextSize(35.0f);
        paintText.setStyle(Paint.Style.STROKE);


    }

    public Space(Context context, AttributeSet attrs) {
        super(context);
        getHolder().addCallback(this);
        setId(R.id.space_view);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        boundingBox.set(0, 0, 1024, 768);

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        setFocusable(true);

        if (context instanceof SpaceInt) {
            spaceInt = (SpaceInt) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SpaceInt");
        }


        // initial center/touch point of the view (otherwise the view would jump
        // around on first pan/move touch
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mTouchX = metrics.widthPixels / 2;
        mTouchY = metrics.heightPixels / 2;

        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        paintText.setColor(Color.RED);
        paintText.setTextSize(35.0f);
        paintText.setStyle(Paint.Style.STROKE);

    }

    void CalculateMatrix(boolean invalidate) {
        float sizeX = this.getWidth() / 2;
        float sizeY = this.getHeight() / 2;

        mMatrix.reset();

        // move the view so that it's center point is located in 0,0
        mMatrix.postTranslate(-sizeX, -sizeY);

        // scale the view
        mMatrix.postScale(mScaleFactor, mScaleFactor);

        // re-move the view to it's desired location
        mMatrix.postTranslate(mTouchX, mTouchY);

        if (invalidate)
            invalidate(); // re-draw
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("CHECK " + event.getAction());
        mScaleDetector.onTouchEvent(event);

        if (!this.mScaleDetector.isInProgress()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:

                    mode = 0;

                    Log.d(TAG, "Touch up event");
                    break;


                case MotionEvent.ACTION_DOWN:

                    Log.i("TEST", "Touch down event SPACE " + DataViewer.getInstance().isWaitClick());

                    mTouchDownX = event.getX();
                    mTouchDownY = event.getY();
                    mTouchBackupX = mTouchX;
                    mTouchBackupY = mTouchY;

                    if (DataViewer.getInstance().isWaitClick()){
                        Log.i("TEST", "Touch down isWaitClick ");
                        spaceInt.onSpaceClickListener((int) mTouchDownX, (int) mTouchDownY);
                        System.out.println("Coords: " + mTouchDownX + " " + mTouchDownY);
                        System.out.println("SIZE: " + this.getWidth() + " " + this.getHeight());
                        DataViewer.getInstance().setWaitClick(false);
                    }


                    // pan/move started
                    mode = 1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // make sure we don't handle the last move event when the first
                    // finger is still down and the second finger is lifted up
                    // already after a zoom/scale interaction. see
                    // ScaleListener.onScaleEnd
                    if (mode == 1) {
                        Log.d(TAG, "Touch move event");

                        // get current location
                        final float x = event.getX();
                        final float y = event.getY();

                        // get distance vector from where the finger touched down to
                        // current location
                        final float diffX = x - mTouchDownX;
                        final float diffY = y - mTouchDownY;

                        mTouchX = mTouchBackupX + diffX;
                        mTouchY = mTouchBackupY + diffY;

                        CalculateMatrix(true);

                    }
                    break;
            }
        }

        return true;
    }
    
    @Override
    public void onDraw(Canvas canvas) {

        int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.concat(mMatrix);

        canvas.drawColor(Color.WHITE);


        //фон
        for (SpaceObject spaceObject:DataViewer.getInstance().getSpaceObjectArrayList()) {
            if(spaceObject.getType() == 3){
                canvas.drawText(spaceObject.getText(), spaceObject.getCoordX(), spaceObject.getCoordY(),paintText );}
            else if (spaceObject.getType() == 2){
                if(spaceObject.getBitmap() != null) {
                    spaceObject.getBitmap().getWidth();
                    canvas.drawBitmap(spaceObject.getBitmap(), spaceObject.getCoordX(), spaceObject.getCoordY(), paintText);
                }
            }
        }
        canvas.restoreToCount(saveCount);


    }


    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        float mFocusStartX;
        float mFocusStartY;
        float mZoomBackupX;
        float mZoomBackupY;

        public ScaleListener() {
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            mode = 2;

            mFocusStartX = detector.getFocusX();
            mFocusStartY = detector.getFocusY();
            mZoomBackupX = mTouchX;
            mZoomBackupY = mTouchY;

            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            mode = 0;

            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (mode != 2)
                return true;

            Log.d(TAG, "Touch scale event");

            // get current scale and fix its value
            float scale = detector.getScaleFactor();
            mScaleFactor *= scale;
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            // get current focal point between both fingers (changes due to
            // movement)
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            // get distance vector from initial event (onScaleBegin) to current
            float diffX = focusX - mFocusStartX;
            float diffY = focusY - mFocusStartY;

            // scale the distance vector accordingly
            diffX *= scale;
            diffY *= scale;

            // set new touch position
            mTouchX = mZoomBackupX + diffX;
            mTouchY = mZoomBackupY + diffY;

            CalculateMatrix(true);

            return true;
        }

    }





        @Override
        public void surfaceCreated (SurfaceHolder holder){
            this.setWillNotDraw(false);
        }
        @Override
        public void surfaceChanged (SurfaceHolder holder,int format, int width, int height){
        }
        @Override
        public void surfaceDestroyed (SurfaceHolder holder){


            }
        }






