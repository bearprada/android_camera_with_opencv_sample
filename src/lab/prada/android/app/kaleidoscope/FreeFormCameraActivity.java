package lab.prada.android.app.kaleidoscope;

import lab.prada.android.app.kaleidoscope.utils.StoreImageHelper;
import lab.prada.collage.component.PhotoView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

public class FreeFormCameraActivity extends BaseCameraActivity {

    private ViewGroup mPhotoPanel;

    public void clickAddPreview(View button) {
        PhotoView pv = new PhotoView(this);
        addListener(pv);
        mPhotoPanel.addView(pv);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_exit_title)
                .setMessage(R.string.dialog_exit_message)
                .setPositiveButton(R.string.dialog_exit_ok,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                                finish();
                            }

                        })
                .setNegativeButton(R.string.dialog_exit_cancel,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_free_form;
    }

    @Override
    protected void initView() {
        aQuery.find(R.id.btnAddPic).clicked(this, "clickAddPreview");
        mPhotoPanel = (ViewGroup) aQuery.find(R.id.frame_images).getView();
    }

    @Override
    protected Bitmap getScreenShot() {
        return StoreImageHelper.view2Bitmap(getContentResolver(), mPhotoPanel);
    }

    @Override
    protected View getContainer() {
        return aQuery.find(R.id.frame).getView();
    }
}
