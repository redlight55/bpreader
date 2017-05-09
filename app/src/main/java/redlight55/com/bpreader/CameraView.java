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

    private Camera mCamera;

    public CameraView(Context context, Camera camera){
        super(context);

        mCamera = camera;
        mCamera.setDisplayOrientation(90); // 90 gives my phone portrait (victor)
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // Do nothing

        /*  CRASH on re-running app
        mCamera.stopPreview();
        mCamera.release()
        */
    }
}
