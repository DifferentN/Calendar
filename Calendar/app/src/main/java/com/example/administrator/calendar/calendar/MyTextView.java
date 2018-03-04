package com.example.administrator.calendar.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018/2/14 0014.
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView{

    //判断是否时当前天
    private boolean sign = false,clickSign=false;
    private Paint mPaint;

    public MyTextView(Context context) {
        this(context,null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSign(boolean sign){
        this.sign = sign;
    }

    public void setClickSign(boolean clickSign){
        this.clickSign=clickSign;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius=0,centerX=getX()+getWidth()/2,centerY=getY()+getHeight()/2;
        if(getHeight()>getWidth()){
            radius = getWidth()/2;
        }else radius= getHeight()/2;
        float left = centerX-radius,top = centerY-radius,right = centerX+radius,bottom = centerY+radius;
        mPaint=new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        RectF rectF=new RectF(left,top,right,bottom);
        if(sign){
            canvas.drawArc(rectF,0,360,true,mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX,centerY,radius,mPaint);
        }

        super.onDraw(canvas);
        if(clickSign){
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX,centerY,radius,mPaint);
            clickSign = false;
        }
    }
}
