package lab.prada.android.app.kaleidoscope;

import lab.prada.android.app.kaleidoscope.view.ISubView;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

public class CircleCameraActivity extends BaseCameraActivity {

    @Override
    protected void initView() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.sub_container);
        for (int i = 0 ; i < vg.getChildCount() ; i++) {
            addListener((ISubView) vg.getChildAt(i));
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_circle;
    }

    @Override
    protected Bitmap getScreenShot() {
        ViewGroup view = (ViewGroup) findViewById(R.id.sub_container);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    @Override
    protected View getContainer() {
        return findViewById(R.id.sub_container);
    }
}
