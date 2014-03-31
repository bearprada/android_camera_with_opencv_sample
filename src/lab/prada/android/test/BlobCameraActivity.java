package lab.prada.android.test;

import lab.prada.android.test.view.ISubView;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

public class BlobCameraActivity extends BaseCameraActivity {

    @Override
    protected void initView() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.sub_container);
        for (int i = 0 ; i < vg.getChildCount() ; i++) {
            addListener((ISubView) vg.getChildAt(i));
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_blob;
    }

    @Override
    protected Bitmap getScreenShot() {
        ViewGroup view = (ViewGroup) findViewById(R.id.sub_container);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        for (int i = 0 ; i < view.getChildCount() ; i++) {
            ((ISubView)view.getChildAt(i)).drawLastCache(canvas);
        }
        return bitmap;
    }

    @Override
    protected View getContainer() {
        return findViewById(R.id.sub_container);
    }
}
