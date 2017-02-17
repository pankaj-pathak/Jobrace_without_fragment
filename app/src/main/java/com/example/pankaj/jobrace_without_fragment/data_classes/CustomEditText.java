package com.example.pankaj.jobrace_without_fragment.data_classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by pankaj on 2/13/2017.
 */

public class CustomEditText extends EditText
{
    private Rect mRect;
    private Paint mPaint;
    private Paint text_paint;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        text_paint = new Paint();
        // define the style of line
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        text_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // define the color of line
        text_paint.setColor(Color.GREEN);
        mPaint.setTextSize(30);
        text_paint.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int lHeight = getLineHeight();
        // the number of line
        int count = height / lHeight;
        if (getLineCount() > count) {
            // for long text with scrolling
            count = getLineCount();
        }
        Rect r = mRect;
        Paint paint = mPaint;

        // first line
        int baseline = getLineBounds(0, r);
        canvas.drawText("hello how are you?".toCharArray(),0,5,r.left,baseline,paint);
        canvas.drawText("hello how are you?".toCharArray(),6,3,r.left+5,baseline+1,text_paint);
        canvas.drawText("hello how are you?".toCharArray(),9,9,r.left+8,baseline+2,paint);

        // draw the remaining lines.
        for (int i = 0; i < count; i++) {
            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);

            // next line
            baseline += getLineHeight();
        }
        super.onDraw(canvas);

    }

    /*
    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }*/
}
