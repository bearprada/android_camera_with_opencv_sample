package lab.prada.android.test.view;

import lab.prada.android.test.BaseCameraActivity.OnCameraFrameListener;
import android.graphics.Canvas;

public interface ISubView extends OnCameraFrameListener {
    public void setId(int id);
    public void drawLastCache(Canvas canvas);
}