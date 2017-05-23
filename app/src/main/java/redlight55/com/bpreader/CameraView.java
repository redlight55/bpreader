package redlight55.com.bpreader;

/**
 * Created by kashf on 09/05/2017.
 */

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Surface;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.view.View;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraMetadata;


import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "CameraView";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static View image = null;

    //private TextView textFPS;
    private static TextView textHB = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE{
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent(){
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static int valueY = 0;

    public CameraView(Context context, Camera camera){
        super(context);

        //textFPS = (TextView) findViewById(R.id.textFPS);

        mCamera = camera;

        mCamera.setDisplayOrientation(270);
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);



        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);



    }

    @Override
    public void onPreviewFrame(byte[] data, Camera cam){
        if (data == null)
            throw new NullPointerException();

        Camera.Size size = cam.getParameters().getPreviewSize();

        if(size == null)
            throw new NullPointerException();

        if(!processing.compareAndSet(false, true))
            return;

        int width = size.width;
        int height = size.height;

        int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);

        TextView valueOfY = (TextView)getRootView().findViewById(R.id.valueY);
        valueY = imgAvg;
        valueOfY.setText(String.valueOf(valueY));

        if(imgAvg == 0 || imgAvg == 255){
            processing.set(false);
            return;
        }

        int averageArrayAvg = 0;
        int averageArrayCnt = 0;

        for(int i = 0; i < averageArray.length; i++){
            if(averageArray[i] > 0){
                averageArrayAvg += averageArray[i];
                averageArrayCnt++;
            }
        }

        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
        TYPE newType = currentType;
        if(imgAvg < rollingAverage){
            newType = TYPE.RED;
            if(newType != currentType){
                beats++;
            }
        }else if(imgAvg > rollingAverage){
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize)
            averageIndex = 0;
        averageArray[averageIndex] = imgAvg;
        averageIndex++;


        if(newType != currentType){
            currentType = newType;
            //image.postInvalidate();
        }

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;
        if(totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int)(bps * 60d);
            if(dpm < 30 || dpm > 180){
                startTime = System.currentTimeMillis();
                beats = 0;
                processing.set(false);
                return;
            }

            if(beatsIndex == beatsArraySize)
                beatsIndex = 0;
            beatsArray[beatsIndex] = dpm;
            beatsIndex++;

            int beatsArrayAvg = 0;
            int beatsArrayCnt = 0;
            for(int i = 0; i < beatsArray.length; i++){
                if(beatsArray[i] > 0){
                    beatsArrayAvg += beatsArray[i];
                    beatsArrayCnt++;
                }
            }

            int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
            textHB = (TextView)getRootView().findViewById(R.id.textHeartRate);
            textHB.setText(String.valueOf(beatsAvg));
            Log.e(TAG, "onPreviewFrame: " + textHB);
            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            //mCamera.setPreviewCallback(this);
        }catch(Exception e){
            Log.d("ERROR","Camera error on SurfaceCreated" + e.getMessage());
        }

    }
    @Override
    protected  void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        final int width = resolveSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if(mSupportedPreviewSizes != null){
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
       //if(mPreviewSize != null){
         //   float ratio;
         //   if(mPreviewSize.height >= mPreviewSize.width)
         //       ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
         //   else
          //      ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

          //  setMeasuredDimension(width, (int)(width * ratio));
          //  setMeasuredDimension((int)(width * ratio), height);
       // }
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        //Log.e(TAG, "surfaceChanged => w = " + i2 + ", h = " + i3);
        TextView textMinFPS = (TextView) getRootView().findViewById(R.id.minFPS);
        TextView textMaxFPS = (TextView) getRootView().findViewById(R.id.maxFPS);

        if(mHolder.getSurface() == null)
            return;
        try{
            mCamera.stopPreview();
        }catch(Exception e) {

        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            //mCamera.setDisplayOrientation(270);

            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);


            //List<Camera.Size> a = mCamera.getParameters().getSupportedPreviewSizes();
            //List<int[]> b = mCamera.getParameters().getSupportedPreviewFpsRange();
            //int[] c = new int[2];
            //parameters.getPreviewFpsRange(c);

            //Gets min and max FPS of the camera
            List<int[]> frameRates = parameters.getSupportedPreviewFpsRange();
            //textFPS = Integer.valueOf(frameRates);
            int last = frameRates.size() - 1;
            int minFPS = (frameRates.get(last))[parameters.PREVIEW_FPS_MIN_INDEX];
            int maxFPS = (frameRates.get(last))[parameters.PREVIEW_FPS_MAX_INDEX];
            parameters.setPreviewFpsRange(minFPS, maxFPS);
            Log.d(TAG, "preview FPS: "+ minFPS + " " + maxFPS);
            textMinFPS.setText(String.valueOf(minFPS/1000));
            textMaxFPS.setText(String.valueOf(maxFPS/1000));

            TextView textFPS = (TextView)getRootView().findViewById(R.id.textFPS);
            float timeDiff = SystemClock.elapsedRealtime();
            float fps = 1000 / timeDiff;
            textFPS.setText(String.format("%.2f", fps));

            //Apparently mCamera.setParameters(parameters) has to be below here to work. All calculations must be done first before calling this. Strange though because
            //Other tutorials can call it before. :/
            //mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

            //startTime = System.currentTimeMillis();



        } catch (IOException e) {
            Log.d("ERROR","Camera error on SurfaceChanged" + e.getMessage());
        }


    }



    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
      //  mCamera.release();
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h){
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes){
            double ratio = (double) size.width  / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff){
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null){
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes){
                if (Math.abs(size.height - targetHeight) < minDiff){
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
