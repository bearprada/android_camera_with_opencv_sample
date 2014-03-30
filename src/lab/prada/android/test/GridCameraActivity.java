package lab.prada.android.test;

import lab.prada.android.test.view.MyListAdapter;
import lab.prada.android.test.view.SubSurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.GridView;

public class GridCameraActivity extends BaseCameraActivity {

    private GridView mGridView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_2;
    }

    @Override
    protected void initView() {
        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setAdapter(new MyListAdapter(this));
    }

    @Override
    protected Bitmap getScreenShot() {
        Bitmap bitmap = Bitmap.createBitmap(mGridView.getWidth(), mGridView.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        for (int i = 0 ; i < mGridView.getChildCount() ; i++) {
            SubSurfaceView sv = (SubSurfaceView)mGridView.getChildAt(i);
            sv.drawLastCache(canvas);
        }
        return bitmap;
    }

    @Override
    protected View getContainer() {
        return mGridView;
    }
}
