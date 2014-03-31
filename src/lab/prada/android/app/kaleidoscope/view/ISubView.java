package lab.prada.android.app.kaleidoscope.view;

import lab.prada.android.app.kaleidoscope.BaseCameraActivity.OnCameraFrameListener;
import android.graphics.Canvas;

public interface ISubView extends OnCameraFrameListener {
    public void setId(int id);
    public void drawLastCache(Canvas canvas);
}