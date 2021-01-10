/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import tool.compet.core.gesturedetector.DkDoubleFingerDetector;

public class DkPhotoView extends View implements DkDoubleFingerDetector.Listener, View.OnTouchListener {
    private int maxZoomLevel = 30;
    private int minZoomLevel = -30;
    private int zoomLevel;
    private Bitmap bitmap;
    private Bitmap drawBitmap;
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint();
    private DkDoubleFingerDetector detector;
    private int boardWidth;
    private int boardHeight;

    // Centerize bitmap and fit inside board
    private boolean isFitCenterBoard = true;

    public DkPhotoView(Context context) {
        this(context, null);
    }

    public DkPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DkPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        detector = new DkDoubleFingerDetector(context, this);

        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        boardWidth = w;
        boardHeight = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public void zoom(boolean bigger) {
        zoom(bigger, bigger ? 1.2f : 0.8f);
    }

    public void zoom(boolean bigger, float scaleFactor) {
        if (bitmap == null) {
            return;
        }
        zoomLevel += bigger ? 1 : -1;

        if (zoomLevel > maxZoomLevel) {
            zoomLevel = maxZoomLevel;
            return;
        }
        else if (zoomLevel < minZoomLevel) {
            zoomLevel = minZoomLevel;
            return;
        }

        matrix.postScale(scaleFactor, scaleFactor, boardWidth >> 1, boardHeight >> 1);

        invalidate();
    }

    /**
     * @param deltaDegrees clockwise delta angle in degrees
     */
    public void rotate(float deltaDegrees) {
        matrix.postRotate(-deltaDegrees, boardWidth >> 1, boardHeight >> 1);
        invalidate();
    }

    public void clear() {
        bitmap = drawBitmap = null;
        zoomLevel = 0;
        matrix.reset();
        invalidate();
    }

    @Override
    public boolean onScale(float scaleFactor, float px, float py) {
        matrix.postScale(scaleFactor, scaleFactor, boardWidth >> 1, boardHeight >> 1);
        invalidate();

        return true;
    }

    @Override
    public boolean onDrag(float firstLastX, float firstDx, float dx, float dy) {
        matrix.postTranslate(dx, dy);
        invalidate();

        return true;
    }

    @Override
    public boolean onRotate(float degrees, float px, float py) {
        float rotateAngle = degrees;

        if (rotateAngle > 5) {
            rotateAngle = 5;
        }
        if (rotateAngle < -5) {
            rotateAngle = -5;
        }

        rotate(rotateAngle);

        return true;
    }

    @Override
    public boolean onDoubleTap() {
        zoom(true);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawBitmap == null) {
            return;
        }

        if (isFitCenterBoard) {
            isFitCenterBoard = false;

            float w = drawBitmap.getWidth();
            float h = drawBitmap.getHeight();
            float dx = (boardWidth - w) / 2f;
            float dy = (boardHeight - h) / 2f;
            float scaleFactor = Math.min(boardWidth / w, boardHeight / h);

            matrix.postTranslate(dx, dy);
            matrix.postScale(scaleFactor, scaleFactor, boardWidth >> 1, boardHeight >> 1);
        }

        canvas.drawBitmap(drawBitmap, matrix, paint);
    }

    //region GetSet

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = drawBitmap = bitmap;
        invalidate();
    }

    public Bitmap getDrawBitmap() {
        return drawBitmap;
    }

    public void setDrawBitmap(Bitmap drawBitmap) {
        this.drawBitmap = drawBitmap;

        invalidate();
    }

    public void setMinZoomLevel(int minZoomLevel) {
        this.minZoomLevel = minZoomLevel;
    }

    public void setMaxZoomLevel(int maxZoomLevel) {
        this.maxZoomLevel = maxZoomLevel;
    }

    public void setFitCenterBoard(boolean fitCenterBoard) {
        isFitCenterBoard = fitCenterBoard;
    }

    //endregion GetSet
}
