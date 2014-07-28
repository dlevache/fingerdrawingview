/*
 * Copyright 2014 elbaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package fr.elbaquero.fingerdrawingview;

import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Finger drawing view.
 */
public final class FingerDrawingView extends View
{
    /**
     * Touch event listener.
     */
    public interface TouchEventListener
    {
        /**
         * Called when a touch down event is dispatched to a view.
         * 
         * @param x
         *            the event X position.
         * @param y
         *            the event Y position.
         */
        void onTouchDown(float x, float y);

        /**
         * Called when a touch move event is dispatched to a view.
         * 
         * @param x
         *            the event X position.
         * @param y
         *            the event Y position.
         */
        void onTouchMove(float x, float y);

        /**
         * Called when a touch up event is dispatched to a view.
         * 
         * @param x
         *            the event X position.
         * @param y
         *            the event Y position.
         */
        void onTouchUp(float x, float y);
    }

    /** Default drawing pen width, in dip. */
    private static final int DEFAULT_DRAWING_PEN_WIDTH = 2;

    /** Default erasing pen width, in dip. */
    private static final int DEFAULT_ERASING_PEN_WIDTH = 4;

    /** Default drawing pen color, in dip. */
    private static final int DEFAULT_DRAWING_PEN_COLOR = Color.WHITE;

    /** Touch event listener. */
    public WeakReference<TouchEventListener> mTouchEventListener = new WeakReference<FingerDrawingView.TouchEventListener>(
            null);

    /** Drawing mode indicator. */
    private boolean mIsDrawing = true;

    /** Current pen width. */
    private int mCurrentPenWidth;

    /** Drawing pen width, in dip. */
    private int mDrawingPenWidth = dpToPx(DEFAULT_DRAWING_PEN_WIDTH);

    /** Erasing pen width, in dip. */
    private int mErasingPenWidth = dpToPx(DEFAULT_ERASING_PEN_WIDTH);

    /** Drawing pen color. */
    private int mDrawingPenColor = DEFAULT_DRAWING_PEN_COLOR;

    /** Last touch down event X position. */
    private int mFirstTouchX;

    /** Last touch down event Y position. */
    private int mFirstTouchY;

    /** Last touch event X position. */
    private int mLastTouchX;

    /** Last touch event Y position. */
    private int mLastTouchY;

    /** Rectangle used to invalidate the smallest possible area. */
    private final RectF mDirtyRect = new RectF();

    private FrameLayout mContainer;
    private View mBackgroundView;
    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundCanvas;
    private BitmapDrawable mBackgroundDrawable;

    /***/
    private Paint mPaint = new Paint();

    /***/
    private Path mPath = new Path();

    /**
     * Create a new {@link FingerDrawingView}.
     * 
     * @param context
     *            the current context.
     */
    public FingerDrawingView(final Context context)
    {
        super(context);
        initialize();
    }

    /**
     * Create a new {@link FingerDrawingView}.
     * 
     * @param context
     *            the current context.
     * @param attrs
     *            the view attributes.
     */
    public FingerDrawingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (mBackgroundView == null)
        {
            ViewParent parent = getParent();

            if (parent instanceof ViewGroup)
            {
                ViewGroup parentGroup = (ViewGroup) parent;
                int positionInGroup = parentGroup.indexOfChild(this);

                mContainer = new FrameLayout(getContext());

                // Replace the current view with a container.
                parentGroup.removeView(this);
                parentGroup.addView(mContainer, positionInGroup, getLayoutParams());

                mBackgroundView = new View(getContext());
                mContainer.addView(mBackgroundView, new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                mBackgroundView.setBackgroundColor(Color.TRANSPARENT);
                mContainer.addView(this, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom)
    {
        if ((mBackgroundBitmap == null) && ((right - left) != 0) && ((top - bottom) != 0))
        {
            mBackgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mBackgroundCanvas = new Canvas(mBackgroundBitmap);
            mBackgroundDrawable = new BitmapDrawable(getResources(), mBackgroundBitmap);
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        if (mIsDrawing)
        {
            canvas.drawPath(mPath, mPaint);
        }
        else
        {
            // When erasing, draw on the background bitmap directly
            mBackgroundCanvas.drawPath(mPath, mPaint);
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        mBackgroundView = null;

        if (mBackgroundBitmap != null)
        {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }

        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event)
    {
        boolean isEventHandled = false;
        boolean invalidate = false;

        int lastTouchX = (int) event.getX();
        int lastTouchY = (int) event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mFirstTouchX = lastTouchX;
                mFirstTouchY = lastTouchY;

                // Move the drawing path to the pressed location
                mPath.moveTo(lastTouchX, lastTouchY);

                // Notify the listener
                if (mTouchEventListener.get() != null)
                {
                    mTouchEventListener.get().onTouchDown(lastTouchX, lastTouchY);
                }

                isEventHandled = true;
                invalidate = false;
                break;

            case MotionEvent.ACTION_MOVE:
                // Start tracking the dirty region.
                resetDirtyRect(lastTouchX, lastTouchY);

                // When the hardware tracks events faster than they are delivered, the event will contain a history of
                // those skipped points.
                for (int i = 0; i < event.getHistorySize(); ++i)
                {
                    int historicalX = (int) event.getHistoricalX(i);
                    int historicalY = (int) event.getHistoricalY(i);

                    onMoveEvent(historicalX, historicalY);
                }

                onMoveEvent(lastTouchX, lastTouchY);

                // Notify the listener
                if (mTouchEventListener.get() != null)
                {
                    mTouchEventListener.get().onTouchMove(lastTouchX, lastTouchY);
                }

                isEventHandled = true;
                invalidate = true;
                break;

            case MotionEvent.ACTION_UP:
                // Start tracking the dirty region.
                resetDirtyRect(lastTouchX, lastTouchY);

                // Single point case
                if ((Math.abs(lastTouchX - mFirstTouchX) < 2) || (Math.abs(lastTouchY - mFirstTouchY) < 2))
                {
                    onMoveEvent(lastTouchX + 1, lastTouchY + 1);
                    onMoveEvent(lastTouchX + 1, lastTouchY - 1);
                    onMoveEvent(lastTouchX - 1, lastTouchY - 1);
                    onMoveEvent(lastTouchX - 1, lastTouchY + 1);
                    onMoveEvent(lastTouchX, lastTouchY);
                }

                // The the previously drawn path into the background view
                commitChanges();

                // Notify the listener
                if (mTouchEventListener.get() != null)
                {
                    mTouchEventListener.get().onTouchUp(lastTouchX, lastTouchY);
                }

                isEventHandled = true;
                invalidate = true;
                break;

            default:
                break;
        }

        mLastTouchX = lastTouchX;
        mLastTouchY = lastTouchY;

        if (invalidate)
        {
            if (mIsDrawing)
            {
                // Include half the stroke width to avoid clipping.
                invalidate((int) (mDirtyRect.left - mCurrentPenWidth / 2),
                        (int) (mDirtyRect.top - mCurrentPenWidth / 2), (int) (mDirtyRect.right + mCurrentPenWidth / 2),
                        (int) (mDirtyRect.bottom + mCurrentPenWidth / 2));
            }
            else
            {
                invalidate();
            }
        }

        return isEventHandled;
    }

    /**
     * Set the touch event listener.
     * 
     * @param touchEventListner
     *            the touch event listener.
     */
    public void setTouchEventListener(final TouchEventListener touchEventListner)
    {
        mTouchEventListener = new WeakReference<FingerDrawingView.TouchEventListener>(touchEventListner);
    }

    /**
     * Set the drawing pen width.
     * 
     * @param drawingPenWidth
     *            the drawing pen width.
     */
    public void setDrawingPenWidth(final int drawingPenWidth)
    {
        mDrawingPenWidth = dpToPx(drawingPenWidth);

        if (mIsDrawing)
        {
            mPaint.setStrokeWidth(mDrawingPenWidth);
            mCurrentPenWidth = mDrawingPenWidth;
        }
    }

    /**
     * Set the erasing pen width.
     * 
     * @param erasingPenWidth
     *            the erasing pen width.
     */
    public void setErasingPenWidth(final int erasingPenWidth)
    {
        mErasingPenWidth = dpToPx(erasingPenWidth);

        if (!mIsDrawing)
        {
            mPaint.setStrokeWidth(mErasingPenWidth);
            mCurrentPenWidth = mErasingPenWidth;
        }
    }

    /**
     * Set the drawing pen color.
     * 
     * @param drawingPenColor
     *            the drawing pen color.
     */
    public void setDrawingPenColor(final int drawingPenColor)
    {
        mDrawingPenColor = drawingPenColor;
        mPaint.setColor(mDrawingPenColor);
    }

    /**
     * Start the drawing mode.
     */
    public void startDrawingMode()
    {
        mIsDrawing = true;
        mPaint.setXfermode(null);
        mPaint.setColor(mDrawingPenColor);
        mPaint.setStrokeWidth(mDrawingPenWidth);

        mCurrentPenWidth = mDrawingPenWidth;
    }

    /**
     * Start the erasing mode.
     */
    public void startErasingMode()
    {
        mIsDrawing = false;
        mPaint.setColor(0xFFF44FFF);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mPaint.setStrokeWidth(mErasingPenWidth);

        mCurrentPenWidth = mErasingPenWidth;
    }

    /**
     * Erase the current drawing.
     */
    public void eraseAll()
    {
        mBackgroundView.setBackgroundColor(Color.TRANSPARENT);
        mBackgroundCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        commitChanges();

        startDrawingMode();
    }

    /**
     * Save the view as a PNG file, at the specified location.
     * 
     * @param filename
     *            the destination full path.
     */
    public void saveAsFile(final String filename)
    {
        FileOutputStream out = null;

        try
        {
            out = new FileOutputStream(filename);
            mBackgroundBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        }
        catch (Exception e)
        {
            Log.e(FingerDrawingView.class.getName(), "An error occurred during file saving", e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (Throwable ignore)
            {
            }
        }
    }

    /**
     * Initialize the view.
     */
    private void initialize()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mPath = new Path();

        setBackgroundColor(Color.TRANSPARENT);

        startDrawingMode();
    }

    /**
     * Erase all the view.
     */
    private void clearPath()
    {
        mPath.reset();

        // Repaints the entire view.
        invalidate();
    }

    /**
     * Handle a touch move event.
     * 
     * @param eventX
     *            the event X position.
     * @param eventY
     *            the event Y position.
     */
    private void onMoveEvent(final int eventX, final int eventY)
    {
        // Expand the dirty region
        expandDirtyRect(eventX, eventY);

        // Update the drawing path with the new position
        mPath.lineTo(eventX, eventY);
    }

    /**
     * Called when replaying history to ensure the dirty region includes all points.
     * 
     * @param historicalX
     * @param historicalY
     */
    private void expandDirtyRect(final int historicalX, final int historicalY)
    {
        if (historicalX < mDirtyRect.left)
        {
            mDirtyRect.left = historicalX;
        }
        else if (historicalX > mDirtyRect.right)
        {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top)
        {
            mDirtyRect.top = historicalY;
        }
        else if (historicalY > mDirtyRect.bottom)
        {
            mDirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     * 
     * @param eventX
     * @param eventY
     */
    private void resetDirtyRect(final int eventX, final int eventY)
    {
        // The lastTouchX and lastTouchY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mLastTouchX, eventX);
        mDirtyRect.right = Math.max(mLastTouchX, eventX);
        mDirtyRect.top = Math.min(mLastTouchY, eventY);
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
    }

    /**
     * Save the drawn path into the background bitmap.
     */
    private void commitChanges()
    {
        mContainer.draw(mBackgroundCanvas);
        mBackgroundView.setBackground(mBackgroundDrawable);

        clearPath();
    }

    /**
     * Conversion between dip and pixels.
     * 
     * @param dp
     *            the dip value to convert into pixels.
     */
    private int dpToPx(final int dp)
    {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
