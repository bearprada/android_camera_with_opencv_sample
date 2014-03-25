package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lab.prada.android.test.R;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.util.Log;

import com.androidquery.util.AQUtility;

public class FaceDetector {
    private static final String TAG = "face_detector";

    private Mat mGray;
    private Mat mRgba;

    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    public FaceDetector(Context context) {
        initFacedetector(context);
        mGray = new Mat();
        mRgba = new Mat();
        mRelativeFaceSize = 0.2f;
        mAbsoluteFaceSize = 0;
    }

    private void initFacedetector(Context context) {
        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(
                    R.raw.lbpcascade_frontalface);
            File cascadeFile = new File(context.getDir("cascade", Context.MODE_PRIVATE),
                    "lbpcascade_frontalface.xml");
            FileOutputStream fos = new FileOutputStream(cascadeFile);
            AQUtility.copy(is, fos);

            mJavaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG,
                        "Loaded cascade classifier from "
                                + cascadeFile.getAbsolutePath());
            mNativeDetector = new DetectionBasedTracker(
                    cascadeFile.getAbsolutePath(), 0);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    public Mat onFrame(Mat inputFrame) {
        inputFrame.copyTo(mRgba);
        Imgproc.cvtColor(inputFrame, mGray, Imgproc.COLOR_RGBA2GRAY);
        int height = mGray.rows();
        if (Math.round(height * mRelativeFaceSize) > 0) {
            mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
        }
        mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);

        MatOfRect faces = new MatOfRect();
        // if (mDetectorType == JAVA_DETECTOR) {
        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, faces, 1.1,
                    2,
                    2, // TODO:
                       // objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
                    new Size());
        }
        org.opencv.core.Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(),
                    FACE_RECT_COLOR, 3);
        }
        return mRgba;
    }

    public void release() {
        mGray.release();
        mRgba.release();
    }
}
