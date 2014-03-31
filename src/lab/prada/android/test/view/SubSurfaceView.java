package lab.prada.android.test.view;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class SubSurfaceView extends SurfaceView implements ISubView{
    private Bitmap mCacheBitmap;
    private Mat dstMat;
    private int mId;

    public SubSurfaceView(Context context) {
        super(context);
        init();
    }

    public SubSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        dstMat = new Mat();
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    private Paint testBgPaint = new Paint();
    {
        testBgPaint.setColor(Color.BLUE);
        testBgPaint.setStrokeWidth(10);
    }

    @Override
    public void onFrame(Mat mat) {
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap((int)mat.size().width, 
                    (int)mat.size().height, Bitmap.Config.ARGB_8888);
        }
        Core.flip(mat, dstMat, mId % 2);
        Utils.matToBitmap(dstMat, mCacheBitmap);
        if (mCacheBitmap != null) {
            Canvas canvas = this.getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                Rect src = new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight());
                Rect dst = new Rect(0, 0, getWidth(), getHeight()); 
                canvas.drawBitmap(mCacheBitmap, src, dst, null);
                canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), testBgPaint);
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void drawLastCache(Canvas canvas) {
        if (mCacheBitmap != null) {
            canvas.drawBitmap(mCacheBitmap, getLeft(), getTop(), null);
        }
    }
}