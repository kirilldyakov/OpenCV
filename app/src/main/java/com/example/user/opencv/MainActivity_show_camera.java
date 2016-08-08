package com.example.user.opencv;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Matrix2f;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.*;
import org.opencv.videoio.*;


import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.hardware.camera2.*;

import static java.lang.System.currentTimeMillis;

// OpenCV Classes

public class MainActivity_show_camera extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;


    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;
    Mat mGray;
    Mat mGauss;
    Mat mEdges;
    Mat mHierarcy;
    Mat mBina;
    Mat mKernel;
    //Mat firstframe, foreground, foregroundThresh, mHierarchy;
 int threshold1 =55;

    boolean isfirstframe =false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity_show_camera() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.show_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        mGray = new Mat(width, width, CvType.CV_8UC4);
        mGauss = new Mat(width, width, CvType.CV_8UC4);
        mEdges = new Mat(width, width, CvType.CV_8UC4);

        mBina = new Mat(width, width, CvType.CV_8UC4);
        mKernel= new Mat(width, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    /*public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


        mRgba = inputFrame.rgba();
        // Rotate mRgba 90 degrees
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Core.flip(mRgbaF, mRgba, 1 );

        Log.d("LOG_TAG","Frame "+currentTimeMillis());

        return mRgba; // This function must return
    }*/


    public Mat onCameraFrame2(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        // Rotate mRgba 90 degrees

        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Core.flip(mRgbaF, mRgba, 1 );

        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_BGR2GRAY);
        Size sz = new Size(7,7);
        Imgproc.GaussianBlur(mGray, mGauss, sz, 0);

        Imgproc.Canny(mGauss,mEdges, 50, 100);
        //Imgproc.dilate(mEdges, mEdges, new Point(0,0), 1);

        List<MatOfPoint> Contours = new List<MatOfPoint>(){
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<MatOfPoint> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }

            @Override
            public boolean add(MatOfPoint matOfPoint) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends MatOfPoint> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, Collection<? extends MatOfPoint> collection) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public MatOfPoint get(int i) {
                return null;
            }

            @Override
            public MatOfPoint set(int i, MatOfPoint matOfPoint) {
                return null;
            }

            @Override
            public void add(int i, MatOfPoint matOfPoint) {

            }

            @Override
            public MatOfPoint remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<MatOfPoint> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<MatOfPoint> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<MatOfPoint> subList(int i, int i1) {
                return null;
            }
        };
        Imgproc.findContours(mEdges
                ,Contours
                ,mHierarcy
                ,Imgproc.RETR_EXTERNAL
                ,Imgproc.CHAIN_APPROX_SIMPLE);

        Imgproc.drawContours(mRgba
                , Contours
                , -1
                , new Scalar(0,255,0));//, 2, 8, hierarchy, 0, new Point());

        Log.d("LOG_TAG","Найдено контуров: "+Contours.size());
        //Core.transpose(mRgba, mRgbaT);
        //Scalar CONTOUR_COLOR = new Scalar(255, 0, 0, 255);

        //    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();


        //    Imgproc.findContours(foregroundThresh, contours,mHierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //    Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);


        return mRgba;
}



    public Mat onCameraFrame3(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        // Rotate mRgba 90 degrees

        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Core.flip(mRgbaF, mRgba, 1 );

        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_BGR2GRAY);


       // Imgproc.dilate(mGray, mGray, mKernel, new Point(1,1),1);
        //Imgproc.erode(mGray, mGray, mKernel);


        Size sz = new Size(7,7);
        Imgproc.GaussianBlur(mGray, mGauss, sz, 0);

        Imgproc.Canny(mGauss,mEdges,50, threshold1);
        //Imgproc.dilate(mEdges, mEdges, new Point(0,0), 1);


        List<MatOfPoint> Contours = new List<MatOfPoint>(){
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<MatOfPoint> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }

            @Override
            public boolean add(MatOfPoint matOfPoint) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends MatOfPoint> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, Collection<? extends MatOfPoint> collection) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public MatOfPoint get(int i) {
                return null;
            }

            @Override
            public MatOfPoint set(int i, MatOfPoint matOfPoint) {
                return null;
            }

            @Override
            public void add(int i, MatOfPoint matOfPoint) {

            }

            @Override
            public MatOfPoint remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<MatOfPoint> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<MatOfPoint> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<MatOfPoint> subList(int i, int i1) {
                return null;
            }
        };

        Imgproc.findContours(mEdges
                ,Contours
                ,mHierarcy
                ,Imgproc.RETR_LIST
                ,Imgproc.CHAIN_APPROX_TC89_L1);

        Imgproc.drawContours(mRgba
                , Contours
                , -1
                , new Scalar(0,255,0));//, 2, 8, hierarchy, 0, new Point());

        Log.d("LOG_TAG","Найдено контуров: "+Contours.size()+ "  th1="+threshold1);
        //Core.transpose(mRgba, mRgbaT);
        //Scalar CONTOUR_COLOR = new Scalar(255, 0, 0, 255);

        //    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();


        //    Imgproc.findContours(foregroundThresh, contours,mHierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //    Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

        return mEdges;
    }


    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();


        Scalar CONTOUR_COLOR = new Scalar(255,0,0,255);

        // Rotate mRgba 90 degrees

        Core.transpose(mRgba, mRgbaT);
        int w = mRgbaT.width();
        int h = mRgbaT.height();

        Log.d("LOG_TAG1", w+"/"+h);
        //Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        //Core.flip(mRgbaF, mRgba, 1 );

        Imgproc.cvtColor(mRgbaT, mGray, Imgproc.COLOR_RGB2GRAY);

        Core.inRange(mGray,new Scalar(100), new Scalar(220), mBina);

        List<MatOfPoint> Contours = new ArrayList<MatOfPoint>();

        mHierarcy = new Mat();
        Imgproc.findContours(mBina
                ,Contours
                ,mHierarcy
                ,Imgproc.RETR_TREE
                ,Imgproc.CHAIN_APPROX_SIMPLE
                , new Point(0,0));

        Imgproc.drawContours(mRgba, Contours, -1, CONTOUR_COLOR);
        //Imgproc.minAreaRect()

        Log.d("LOG_TAG","Найдено контуров: "+Contours.size());

        for (MatOfPoint contour: Contours)
        {

            if (Imgproc.contourArea(contour) <300) continue;
            // Minimum size allowed for consideration
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            //Rect rect = Imgproc.boundingRect(points);
            RotatedRect rect = Imgproc.minAreaRect(approxCurve);


            Point[] rect_points = new Point[4];
            rect.points( rect_points );
            Scalar color = new Scalar(0,200,0);
            for( int j = 0; j < 4; j++ )
                Imgproc.line(mRgba, rect_points[j], rect_points[(j+1)%4], color , 1);
        }


            //Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);


        return mRgba;
        }



}