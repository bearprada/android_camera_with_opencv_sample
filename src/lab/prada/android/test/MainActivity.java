package lab.prada.android.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements CvCameraViewListener {
    private static final String TAG = "test";

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("detection_based_tracker");
    }

    /*
     * private Preview mPreview; private Camera mCamera;
     */
    private int numberOfCameras;
    private int DEFAULT_CAMERA_ID;
    private int cameraCurrentlyLocked;
    private ImageView snapshot;
    protected boolean isOpenFaceDetection;
    private ExtendedJavaCamera mCamera;
    private Mat mGray;
    private Mat mRgba;
    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize;
    private SurfaceView mSubSurfaceView;
    private SurfaceHolder mSubHolder;
    private Bitmap mCacheBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        this.setContentView(R.layout.activity_main);

        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                DEFAULT_CAMERA_ID = i;
            }
        }
        mCamera = new ExtendedJavaCamera(this, DEFAULT_CAMERA_ID);
        ((RelativeLayout) findViewById(R.id.camera_layout)).addView(mCamera);

        ((Button) findViewById(R.id.btnSwitch))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchCamera();
                    }
                });
        ((Button) findViewById(R.id.btnTaken))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeImg();
                    }
                });

        ((Button) findViewById(R.id.btnFlashMode))
                .setOnClickListener(new OnClickListener() {
                    private boolean isFlashOn = false;

                    @Override
                    public void onClick(View v) {
                        if (cameraCurrentlyLocked == DEFAULT_CAMERA_ID) {
                            Parameters p = mCamera.getCurrentCamera()
                                    .getParameters();
                            if (isFlashOn == false)
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                            else
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.getCurrentCamera().setParameters(p);
                            isFlashOn = !isFlashOn;
                        }
                    }
                });

        ((Button) findViewById(R.id.btnFaceDetect))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isOpenFaceDetection = !isOpenFaceDetection;
                        // (isOpenFaceDetection==false) ?
                        // mPreview.mCamera.startFaceDetection() :
                        // mPreview.mCamera.stopFaceDetection();

                    }
                });
        this.snapshot = ((ImageView) findViewById(R.id.snapshot_img));

        mSubSurfaceView = (SurfaceView)findViewById(R.id.my_surface_view);
        mSubHolder = mSubSurfaceView.getHolder();
        mSubHolder.addCallback(new Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
            }
        });

        setMinFaceSize(0.2f);
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    protected void takeImg() {
        mCamera.getCurrentCamera().takePicture(null, null,
                new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        snapshot.setImageBitmap(BitmapFactory.decodeByteArray(
                                data, 0, data.length));
                        camera.startPreview();
                    }
                });
    }

    boolean replaceView(ViewGroup vg, View view, View replace_view) {
        vg.removeAllViews();
        vg.addView(replace_view);
        vg.requestLayout();
        replace_view.forceLayout();
        return true;
    }

    protected void switchCamera() {
        if (numberOfCameras == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("only one camera").setNeutralButton("Close",
                    null);
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        mCamera.disableView();
        cameraCurrentlyLocked = (cameraCurrentlyLocked + 1) % numberOfCameras;
        ExtendedJavaCamera nc = new ExtendedJavaCamera(this,
                cameraCurrentlyLocked);
        replaceView((ViewGroup) findViewById(R.id.camera_layout), mCamera, nc);
        mCamera.setCvCameraViewListener(null);
        mCamera = nc;
        mCamera.enableView();
        mCamera.setCvCameraViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        cameraCurrentlyLocked = DEFAULT_CAMERA_ID;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        drawOnSubSurface(inputFrame);
        if (isOpenFaceDetection == true) {
            inputFrame.copyTo(mRgba);
            Imgproc.cvtColor(inputFrame, mGray, Imgproc.COLOR_RGBA2GRAY);
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);

            MatOfRect faces = new MatOfRect();
            // if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1,
                        2,
                        2, // TODO:
                           // objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
                        new Size());
            /*
             * } else if (mDetectorType == NATIVE_DETECTOR) { if
             * (mNativeDetector != null) mNativeDetector.detect(mGray, faces); }
             * else { Log.e(TAG, "Detection method is not selected!"); }
             */

            
            org.opencv.core.Rect[] facesArray = faces.toArray();
            Log.d(TAG, " main acitivity  face number = " + faces.size());
            for (int i = 0; i < facesArray.length; i++)
                Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(),
                        FACE_RECT_COLOR, 3);
            return mRgba;
        } else {
            return inputFrame;
        }
    }

    private void drawOnSubSurface(Mat inputFrame) {
        if (mCacheBitmap == null) {
            Parameters params = mCamera.getCurrentCamera().getParameters();
            int frameWidth = params.getPreviewSize().width;
            int frameHeight = params.getPreviewSize().height;
            mCacheBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
        }
        Utils.matToBitmap(inputFrame, mCacheBitmap);
        if (mCacheBitmap != null) {
            Canvas canvas = mSubHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                Rect src = new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight());
                Rect dst = new Rect((canvas.getWidth() - mCacheBitmap.getWidth()) / 2,
                        (canvas.getHeight() - mCacheBitmap.getHeight()) / 2,
                        (canvas.getWidth() - mCacheBitmap.getWidth()) / 2 + mCacheBitmap.getWidth(),
                        (canvas.getHeight() - mCacheBitmap.getHeight()) / 2 + mCacheBitmap.getHeight()); 
                canvas.drawBitmap(mCacheBitmap, src, dst, null);
                mSubHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

    @Override
    public void onCameraViewStarted(int arg0, int arg1) {
        mGray = new Mat();
        mRgba = new Mat();
        initFacedetector();
    }

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private void initFacedetector() {
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(
                    R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(
                    mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG,
                        "Loaded cascade classifier from "
                                + mCascadeFile.getAbsolutePath());

            mNativeDetector = new DetectionBasedTracker(
                    mCascadeFile.getAbsolutePath(), 0);

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

}
