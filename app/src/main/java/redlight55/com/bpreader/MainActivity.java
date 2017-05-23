package redlight55.com.bpreader;

import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.hardware.camera2.CameraManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private CameraView image = null;
    private CameraViewWithoutIP imagewithoutip = null;
    private FrameLayout camera_view;
    private Button btnS;
    private TextView txtTime;
    int x = 0;
    //int imgAvg = 0;   // pass imgAvg from CameraView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnS = (Button) findViewById(R.id.btnStart);
        txtTime = (TextView) findViewById(R.id.txtcount);

        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        imagewithoutip = new CameraViewWithoutIP(this, camera);
        camera_view = (FrameLayout) findViewById(R.id.camera_view);
        camera_view.addView(imagewithoutip);

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnS.setEnabled(false);

                if (camera != null && x == 0) {
                    x = 1;
                    image = new CameraView(MainActivity.this, camera);
                    camera_view.removeAllViews();
                    camera_view.addView(image);
                }

                if (x == 1) {
                    camera_view.removeAllViews();
                    camera_view.addView(image);
                }

                image.onResume();

                new CountDownTimer(30000, 1000) {
                //new CountDownTimer(5000, 1000) {
                    // 5000 is for testing
                    public void onTick(long millisUntilFinished) {
                        //imgAvg = CameraView.imgAvgTest;   // pass imgAvg
                        //txtTime.setText(Integer.toString(imgAvg));    // pass imgAvg
                        txtTime.setText("Time remaining: " + millisUntilFinished / 1000);
                    }
                    public void onFinish() {
                        txtTime.setText("Done!");
                        image.onPause();
                        camera_view.removeAllViews();
                        camera_view.addView(imagewithoutip);
                        btnS.setEnabled(true);
                    }
                }.start();
            }
        });

        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}