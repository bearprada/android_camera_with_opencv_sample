package lab.prada.collage.component;

import java.util.concurrent.Callable;

import lab.prada.android.app.kaleidoscope.view.ISubView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import bolts.Task;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

public class PhotoView extends ImageView implements BaseComponent, ISubView {

    private Bitmap mCacheBitmap;

    public PhotoView(Context context) {
        super(context);
        this.setOnTouchListener(new MultiTouchListener());
    }

    public void setImage(Bitmap bitmap) {
        setImageBitmap(bitmap);
    }

    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setXY(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public void onFrame(Mat mat) {
        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap((int)mat.size().width, (int)mat.size().height, Bitmap.Config.ARGB_8888);
        }
        Utils.matToBitmap(mat, mCacheBitmap);
        if (mCacheBitmap != null) {
            Task.call(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    setImageBitmap(mCacheBitmap);
                    return null;
                }
            }, lab.prada.android.app.kaleidoscope.utils.Utils.sUiThreadExecutor);
            
        }
        
    }

    @Override
    public void drawLastCache(Canvas canvas) {
        // TODO
    }
}
