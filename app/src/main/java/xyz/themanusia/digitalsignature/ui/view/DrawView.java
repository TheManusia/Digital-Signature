package xyz.themanusia.digitalsignature.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawView extends View {
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

    private final Paint mPaint = new Paint();

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);

        setFocusable(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();
                invalidate();
                break;

            default:
                super.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
    }

    public void clear() {
        mStartX = 0;
        mStartY = 0;
        mEndX = 0;
        mEndY = 0;
        invalidate();
    }

    public float getRectX() {
        return mStartX;
    }

    public float getRectY() {
        return mStartY;
    }

    public float getRectWidth() {
        return mStartX - mEndX;
    }

    public float getRectHeight() {
        return mStartY - mEndY;
    }
}
