package lab.prada.android.test;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

public class ExtendedJavaCamera extends JavaCameraView {

	public ExtendedJavaCamera(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public ExtendedJavaCamera(Context context, int cameraId) {
		super(context, cameraId);
		// TODO Auto-generated constructor stub
	}
	
	public Camera getCurrentCamera(){
		return mCamera;
	}
}
