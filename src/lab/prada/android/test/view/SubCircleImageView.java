package lab.prada.android.test.view;

import java.util.concurrent.Callable;

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
import bolts.Task;

public class SubCircleImageView extends CircularImageView implements ISubView {
    private Bitmap mCacheBitmap;
    private Mat dstMat;
    private int mId;

    public SubCircleImageView(Context context) {
        super(context);
        init();
    }

    public SubCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubCircleImageView(Context context, AttributeSet attrs, int defStyle) {
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
    public void drawLastCache(Canvas canvas) {
//        canvas.save(); // FIXME get the same result : View.getDrawingCache
//        Rect rect = new Rect();
//        this.getDrawingRect(rect);
//        canvas.clipRect(rect);
        onDraw(canvas); // FIXME
//        canvas.restore();
    }
}