package lab.prada.android.test;

import lab.prada.android.test.MainActivity.OnCameraFrameListener;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class SubSurfaceView extends SurfaceView implements OnCameraFrameListener{
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

    public void setId(int id) {
        mId = id;
    }

    public void attachBuffer(Mat mat) {
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap((int)mat.size().width, (int)mat.size().height, Bitmap.Config.ARGB_8888);
        }
        Core.flip(mat, dstMat, mId % 2);
        Utils.matToBitmap(dstMat, mCacheBitmap);
        if (mCacheBitmap != null) {
            Canvas canvas = this.getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                Rect src = new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight());
                Rect dst = new Rect((canvas.getWidth() - mCacheBitmap.getWidth()) / 2,
                        (canvas.getHeight() - mCacheBitmap.getHeight()) / 2,
                        (canvas.getWidth() - mCacheBitmap.getWidth()) / 2 + mCacheBitmap.getWidth(),
                        (canvas.getHeight() - mCacheBitmap.getHeight()) / 2 + mCacheBitmap.getHeight()); 
                canvas.drawBitmap(mCacheBitmap, src, dst, null);
                this.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onFrame(Mat inputFrame) {
        attachBuffer(inputFrame);
    }

    public void drawLastCache(Canvas canvas) {
        if (mCacheBitmap != null) {
            canvas.drawBitmap(mCacheBitmap, getLeft(), getTop(), null);
        }
        
    }
}