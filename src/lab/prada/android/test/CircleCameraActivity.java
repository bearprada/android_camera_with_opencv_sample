package lab.prada.android.test;

import lab.prada.android.test.view.SubImageView;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

public class CircleCameraActivity extends BaseCameraActivity {

    @Override
    protected void initView() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.sub_container);
        for (int i = 0 ; i < vg.getChildCount() ; i++) {
            SubImageView ssv = (SubImageView) vg.getChildAt(i);
            addListener(ssv);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_3;
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
