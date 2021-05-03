package xyz.themanusia.digitalsignature.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawView extends View {
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

    private static final String TAG = DrawView.class.getSimpleName();

    private final Paint mPaint = new Paint();

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5.0f);
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
        Log.d(TAG, "onDraw: mStartX= " + mStartX + ", mStartY= " + mStartY);
        Log.d(TAG, "onDraw: mEndX= " + mEndX + ", mEndY= " + mEndY);
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
        return Math.min(mStartX, mEndX);
    }

    public float getRectY() {
        return Math.max(mStartY, mEndY);
    }

    public float getRectWidth() {
        return Math.abs(mStartX - mEndX);
    }

    public float getRectHeight() {
        return Math.abs(mStartY - mEndY);
    }
}
