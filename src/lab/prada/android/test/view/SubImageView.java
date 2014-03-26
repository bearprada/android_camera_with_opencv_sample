package lab.prada.android.test.view;

import java.util.concurrent.Callable;

import lab.prada.android.test.CameraActivity.OnCameraFrameListener;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import bolts.Task;

public class SubImageView extends ImageView implements OnCameraFrameListener{
    private Bitmap mCacheBitmap;
    private Mat dstMat;
    private int mId;

    public SubImageView(Context context) {
        super(context);
        init();
    }

    public SubImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        dstMat = new Mat();
    }

    public void setId(int id) {
        mId = id;
    }
    private Paint testBgPaint = new Paint();
    {
        testBgPaint.setColor(Color.BLUE);
        testBgPaint.setStrokeWidth(10);
    }

    public void attachBuffer(Mat mat) {
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap((int)mat.size().width, (int)mat.size().height, Bitmap.Config.ARGB_8888);
        }
        Core.flip(mat, dstMat, mId % 2);
        Utils.matToBitmap(dstMat, mCacheBitmap);
        if (mCacheBitmap != null) {
            Task.call(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    setImageBitmap(mCacheBitmap);
                    return null;
                }
            }, lab.prada.android.test.Utils.sUiThreadExecutor);
            
        }
    }

    @Override
    public void onFrame(Mat inputFrame) {
        attachBuffer(inputFrame);
    }

    public void drawLastCache(Canvas canvas) {
        if (mCacheBitmap != null) {
            canvas.save();
            canvas.rotate(getRotation());
            canvas.translate(getTranslationX(), getTranslationY());
            canvas.drawBitmap(mCacheBitmap, getLeft() , getTop(), null);
            canvas.restore();
        }
    }
}