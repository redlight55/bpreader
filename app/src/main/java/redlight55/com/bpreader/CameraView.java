package redlight55.com.bpreader;

/**
 * Created by kashf on 09/05/2017.
 */

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import android.view.Surface;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraMetadata;


import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraView(Context context, Camera camera){
        super(context);

        mCamera = camera;
        mCamera.setDisplayOrientation(0);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch(Exception e){
            Log.d("ERROR","Camera error on SurfaceCreated" + e.getMessage());
        }

    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        if(mHolder.getSurface() == null)
            return;
        try{
            mCamera.stopPreview();
        }catch(Exception e) {

        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR","Camera error on SurfaceChanged" + e.getMessage());
        }


    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
    }
}
