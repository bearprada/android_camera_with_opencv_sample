package lab.prada.android.test;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.Mat;
import org.opencv.samples.facedetect.FaceDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.androidquery.AQuery;

public abstract class BaseCameraActivity extends Activity implements CvCameraViewListener, OnClickListener {

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("detection_based_tracker");
    }

    private int numberOfCameras;
    private int defaultCameraId;
    private int currentCameraId;

    private boolean isOpenFaceDetection;
    private boolean isFlashOn = false;

    protected AQuery aQuery;
    private ExtendedJavaCamera mCamera;
    private Vector<OnCameraFrameListener> mListener = new Vector<OnCameraFrameListener>();
    private FaceDetector mFaceDetector;
    private BackgroundManager mBackgroundMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        setContentView(getContentViewId());

        numberOfCameras = Camera.getNumberOfCameras();
        defaultCameraId = findDefaultCameraId();
        mCamera = new ExtendedJavaCamera(this, defaultCameraId);
        mCamera.setMaxFrameSize(480, 480);
        ((ViewGroup)findViewById(R.id.root)).addView(mCamera, 0);

        aQuery = new AQuery(this);
        // initial the common UI components
        aQuery.find(R.id.btn_camera).clicked(this);
        aQuery.find(R.id.btn_next).clicked(this);
        aQuery.find(R.id.btn_prev).clicked(this);

        initView();

        mFaceDetector = new FaceDetector(this);
        mBackgroundMgr = BackgroundManager.getInstance(this);

        try {
            doSetBackground(mBackgroundMgr.getBackground(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract int getContentViewId();

    protected abstract void initView();

    protected abstract Bitmap getScreenShot();

    protected abstract View getContainer();

    private int findDefaultCameraId() {
        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 0;
    }

    private static class BackgroundManager {
        private static BackgroundManager instance;
        private final String[] mBackgrounds;
        private final static String PREFIX = "backgrounds";
        public static BackgroundManager getInstance(Context ctx) {
            if (instance == null) { 
                instance = new BackgroundManager(ctx);
            }
            return instance;
        }

        private BackgroundManager(Context ctx) {
            String[] s = null;
            try {
                s = ctx.getAssets().list(PREFIX);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBackgrounds = s;
        }

        public String getBackground(int index) {
            return PREFIX + "/" + mBackgrounds[index];
        }

        public int getTotalNumber() {
            return mBackgrounds == null ? 0 : mBackgrounds.length;
        }
    }

    private int currentBackgroundIdx = 0;
    private void prevBackground() {
        try {
            doSetBackground(mBackgroundMgr.getBackground(getPrevIdx()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doSetBackground(String background) throws IOException {
        BitmapDrawable drawable = (BitmapDrawable) BitmapDrawable.createFromStream(
                getAssets().open(background), background);
        drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        getContainer().setBackgroundDrawable(drawable);
    }

    private int getPrevIdx() {
        if (currentBackgroundIdx > 0)
            currentBackgroundIdx--;
        return currentBackgroundIdx;
    }

    private int getNextIdx() {
        if (currentBackgroundIdx < mBackgroundMgr.getTotalNumber())
            currentBackgroundIdx++;
        return currentBackgroundIdx;
    }
    
    private void nextBackground() {
        try {
            doSetBackground(mBackgroundMgr.getBackground(getNextIdx()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(OnCameraFrameListener listener) {
        if (listener != null) {
            synchronized(mListener) {
                mListener.add(listener);
            }
        }
    }

    private void takeCameraPicture() {
        mCamera.getCurrentCamera().takePicture(null, null,
            new PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    aQuery.find(R.id.snapshot_img).image(
                            BitmapFactory.decodeByteArray(data, 0, data.length));
                    camera.startPreview();
                }
            });
    }

    private boolean replaceView(ViewGroup vg, View view, View replace_view) {
        vg.removeAllViews();
        vg.addView(replace_view);
        vg.requestLayout();
        replace_view.forceLayout();
        return true;
    }

    private void switchCamera() {
        if (numberOfCameras == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("only one camera").setNeutralButton("Close",
                    null);
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        mCamera.disableView();
        currentCameraId = (currentCameraId + 1) % numberOfCameras;
        // FIXME find out another way to switch camera
        ExtendedJavaCamera newCamera = new ExtendedJavaCamera(this,
                currentCameraId);
        newCamera.setMaxFrameSize(240, 240);
        replaceView((ViewGroup) findViewById(R.id.camera_layout), mCamera, newCamera);
        mCamera.setCvCameraViewListener(null);
        mCamera = newCamera;
        mCamera.enableView();
        mCamera.setCvCameraViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Open the default i.e. the first rear facing camera.
        currentCameraId = defaultCameraId;
        mCamera.enableView();
        mCamera.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mCamera.setCvCameraViewListener(null);
            mCamera.disableView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        synchronized(mListener) {
            for (OnCameraFrameListener l : mListener) {
                l.onFrame(inputFrame);
            }
        }
        if (isOpenFaceDetection == true) {
            return mFaceDetector.onFrame(inputFrame);
        } else {
            return inputFrame;
        }
    }

    public interface OnCameraFrameListener {
        public void onFrame(Mat inputFrame);
    }

    @Override
    public void onCameraViewStarted(int width, int height) { }

    @Override
    public void onCameraViewStopped() { }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.btnFaceDetect:
            isOpenFaceDetection = !isOpenFaceDetection;
            break;
        case R.id.btnFlashMode:
            if (currentCameraId == defaultCameraId) {
                Parameters p = mCamera.getCurrentCamera()
                        .getParameters();
                if (isFlashOn  == false)
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                else
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.getCurrentCamera().setParameters(p);
                isFlashOn = !isFlashOn;
            }
            break;
        case R.id.btnSwitch:
            switchCamera();
            break;
        case R.id.btnTaken:
            takeCameraPicture();
            break;
        case R.id.btn_next:
            nextBackground();
            break;
        case R.id.btn_prev:
            prevBackground();
            break;
        case R.id.btn_camera:
            final Bitmap bm = getScreenShot();
            aQuery.find(R.id.image_screenshot).image(bm);
            Task.callInBackground(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GalleryManager.getInstance().saveToFile(bm);
                    return null;
                }
            }).continueWith(new Continuation<Void, Void>() {
                @Override
                public Void then(Task<Void> task) throws Exception {
                    if (task.isFaulted()) {
                        Toast.makeText(BaseCameraActivity.this, "error " + task.getError(), Toast.LENGTH_LONG).show();
                        task.getError().printStackTrace();
                    }
                    if (task.isCompleted()) {
                        Toast.makeText(BaseCameraActivity.this, "save finish", Toast.LENGTH_LONG).show();
                    }
                    return null;
                }
            }, Utils.sUiThreadExecutor);
            break;
        }
    }
}
