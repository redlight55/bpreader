package redlight55.com.bpreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.hardware.camera2.CameraManager;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static CameraView image = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
           camera = Camera.open();
        }catch (Exception e)
        {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(camera != null){
            image = new CameraView(this, camera);
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(image);
        }

        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }
}
