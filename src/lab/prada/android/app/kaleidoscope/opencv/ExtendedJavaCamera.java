package lab.prada.android.app.kaleidoscope.opencv;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

public class ExtendedJavaCamera extends JavaCameraView {

    public ExtendedJavaCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedJavaCamera(Context context, int cameraId) {
        super(context, cameraId);
    }

    public Camera getCurrentCamera() {
        return mCamera;
    }
}
