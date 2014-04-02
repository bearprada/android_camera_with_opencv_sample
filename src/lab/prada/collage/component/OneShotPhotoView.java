package lab.prada.collage.component;

import org.opencv.core.Mat;

import android.content.Context;

public class OneShotPhotoView extends PhotoView {

    private boolean isFreeze = false;

    public OneShotPhotoView(Context context) {
        super(context);
    }

    public boolean isFreeze() {
        return isFreeze;
    }

    public void freeze() {
        isFreeze = true;
    }

    @Override
    public void onFrame(Mat mat) {
        if (isFreeze == false) {
            super.onFrame(mat);
        }
    }
}
