package xyz.themanusia.digitalsignature.widget.clipart;

/*
src: https://stackoverflow.com/questions/22011259/move-drag-zoom-an-imageview-in-android
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class ClipArt extends androidx.appcompat.widget.AppCompatImageView {
    private final Paint borderPaint;
    private final Paint backgroundPaint;

    private float mPosX = 0f;
    private float mPosY = 0f;

    private float mLastTouchX;
    private float mLastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    public ClipArt(Context context) {
        this(context, null, 0);
    }

    public ClipArt(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private final ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private int drawableHeight;
    private int drawableWidth;

    // Existing code ...
    public ClipArt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        borderPaint = new Paint();
        borderPaint.setARGB(32, 255, 255, 255);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);

        backgroundPaint = new Paint();
        backgroundPaint.setARGB(32, 255, 255, 255);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * (non-Javadoc)
     *
     * @see ImageView#setImageDrawable(Drawable)
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            // Constrain to given size but keep aspect ratio
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            drawableWidth = width;
            drawableHeight = height;
            mLastTouchX = mPosX = 0;
            mLastTouchY = mPosY = 0;

            int borderWidth = (int) borderPaint.getStrokeWidth();
            mScaleFactor = Math.min(((float) getLayoutParams().width - borderWidth) / width,
                    ((float) getLayoutParams().height - borderWidth)
                            / height) / 2;
            pivotPointX = Math.max(0, (((float) getLayoutParams().width - borderWidth) - width));
            pivotPointY = Math.max(0, (((float) getLayoutParams().height - borderWidth) - height));
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null) {
            Drawable drawable = new BitmapDrawable(getContext().getResources(), bm);
            setImageDrawable(drawable);
        }
    }

    float pivotPointX = 0f;
    float pivotPointY = 0f;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            pivotPointX = detector.getFocusX();
            pivotPointY = detector.getFocusY();

            mScaleFactor = Math.max(0.05f, mScaleFactor);
            Log.d(LOG_TAG, "onScale mScaleFactor " + mScaleFactor);
            Log.d(LOG_TAG, "onScale pivotPointY " + pivotPointY + ", pivotPointX= " + pivotPointX);

            invalidate();
            return true;
        }
    }
}