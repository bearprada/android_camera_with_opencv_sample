package lab.prada.android.app.kaleidoscope;

import lab.prada.android.app.kaleidoscope.view.ISubView;
import lab.prada.android.app.kaleidoscope.view.MyListAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.GridView;

public class GridCameraActivity extends BaseCameraActivity {

    private GridView mGridView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_grid;
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
            ((ISubView)mGridView.getChildAt(i)).drawLastCache(canvas);
        }
        return bitmap;
    }

    @Override
    protected View getContainer() {
        return null;
    }
}
