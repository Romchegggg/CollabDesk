package com.example.collabdesk;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private volatile boolean running = true; //флаг для остановки потока
    private DrawThread drawThread;

    private Paint backgroundPaint = new Paint();
    private Paint paintText = new Paint();
    private Paint paintPicture = new Paint();

    private Bitmap bitmap;
    private int towardPointX;
    private int towardPointY;

    {
        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(35.0f);
        paintText.setStyle(Paint.Style.STROKE);
    }
    public DrawThread(Context context, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }
    public void requestStop() {
        running = false;
    }
    @Override
    public void run() {
        int txtX = 200;
        int txtY = 200;
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    //фон
                    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
                    for (SpaceObject spaceObject:DataViewer.getInstance().getSpaceObjectArrayList()) {
                        if(spaceObject.getType() == 3){
                            canvas.drawText(spaceObject.getText(), spaceObject.getCoordX(), spaceObject.getCoordY(),paintText );}
                        else if (spaceObject.getType() == 2){
                            if(spaceObject.getBitmap() != null) {
                                float a = spaceObject.getBitmap().getHeight();
                                float b = spaceObject.getBitmap().getWidth();
                                while (a > 999 && b > 999){
                                    a = a / 10;
                                    b = b / 10;

                                }
                                spaceObject.setHeight(a);
                                spaceObject.setWidth(b);
                                canvas.drawBitmap(spaceObject.getBitmap(), spaceObject.getCoordX(), spaceObject.getCoordY(), paintText);
                            }
                        }
                    }

                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }


    }

    public void setTowardPoint(int x, int y) {
        towardPointX = x;
        towardPointY = y;
    }

    public boolean onTouchEvent(MotionEvent event) {
        drawThread.setTowardPoint((int)event.getX(),(int)event.getY());
        return false;
    }





}