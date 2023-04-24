package com.gojavas.taskforce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.ui.activity.SignatureActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by GJS280 on 4/5/2015.
 */
public class SignatureView extends View {

    // drawing path
    private Path drawPath;
    // drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    // initial color
    private int paintColor = 0xFF000000;
    // canvas
    private Canvas drawCanvas;
    // canvas bitmap
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase=false;
    private boolean isDateTimeAdded = false;
    private String aWBNo = "";
    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isDateTimeAdded = false;
        setupDrawing();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }


    public void setAWBNo(String _aWBNo){
        this.aWBNo = _aWBNo;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvasPaint.setTextSize(32);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);


        canvasPaint.setColor(getResources().getColor(R.color.black));
        canvasPaint.setTextSize(22);
        DateFormat dateFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dateFormatter2 = new SimpleDateFormat("hh:mm:ss");
        dateFormatter1.setLenient(false);
        dateFormatter2.setLenient(false);
        java.util.Date today = new java.util.Date();
        // java.util.Timer;
        String d = dateFormatter1.format(today);
        String t = dateFormatter2.format(today);
        if(!isDateTimeAdded){
            if(this.aWBNo!=null&&this.aWBNo.length()>0){
                drawCanvas.drawText("" + d + ", " + t + " AWN No: "+this.aWBNo, 20f , getHeight() - 24, canvasPaint);
            }else{
                drawCanvas.drawText("" + d + ", " + t, 20f , getHeight() - 24, canvasPaint);
            }
            isDateTimeAdded = true;
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                SignatureActivity.isEmpty=false;
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    private void setupDrawing() {
        // get drawing area setup for interaction
        brushSize = 2.0f;
        lastBrushSize = brushSize;

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    public void setBrushSize(float newSize) {
        // update size
        float pixelAmount = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, newSize, getResources()
                        .getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {
        erase=isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void startNew(){
        isDateTimeAdded = false;
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

}
