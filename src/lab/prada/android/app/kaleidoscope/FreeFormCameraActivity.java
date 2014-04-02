package lab.prada.android.app.kaleidoscope;

import lab.prada.android.app.kaleidoscope.utils.StoreImageHelper;
import lab.prada.collage.component.OneShotPhotoView;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

public class FreeFormCameraActivity extends BaseCameraActivity {

    private ViewGroup mPhotoPanel;

    public void clickAddPreview(View button) {
        OneShotPhotoView view = new OneShotPhotoView(this);
        addListener(view);
        mPhotoPanel.addView(view);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_free_form;
    }

    @Override
    protected void initView() {
        aQuery.find(R.id.btnAddPic).clicked(this, "clickAddPreview");
        mPhotoPanel = (ViewGroup) aQuery.find(R.id.frame_images).getView();
        aQuery.find(R.id.btn_frozon).clicked(this, "clickFrezze");
    }

    @Override
    protected Bitmap getScreenShot() {
        return StoreImageHelper.view2Bitmap(getContentResolver(), mPhotoPanel);
    }

    @Override
    protected View getContainer() {
        return aQuery.find(R.id.frame).getView();
    }

    public void clickFrezze(View view) {
        OneShotPhotoView pv = findAliveView();
        if (pv != null) {
            pv.freeze();
        }
    }

    private OneShotPhotoView findAliveView() {
        int len = mPhotoPanel.getChildCount();
        for (int i = 0 ; i < len ; i++ ) {
            OneShotPhotoView pv = (OneShotPhotoView) mPhotoPanel.getChildAt(i);
            if (!pv.isFreeze()) {
                return pv;
            }
        }
        return null;
    }
}
