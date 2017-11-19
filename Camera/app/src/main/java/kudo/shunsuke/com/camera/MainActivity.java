package kudo.shunsuke.com.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;





public class MainActivity extends Activity{

    SurfaceView sv;
    SurfaceHolder sh;
    FrameLayout fl;
    Camera cam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout fl = new FrameLayout(this);
        setContentView(fl);

        sv = new SurfaceView(this);
        sh = sv.getHolder();
        sh.addCallback(new SurfaceHolderCallback());

        Button btn = new Button(this);
        btn.setText("撮影");
        btn.setLayoutParams(new LayoutParams(200, 150));
        btn.setOnClickListener(new TakePictureClickListener());


        fl.addView(sv);
        fl.addView(btn);
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            cam = Camera.open();
            Parameters param = cam.getParameters();
            List<Size> ss = param.getSupportedPictureSizes();
            Size pictSize = ss.get(0);

            param.setPictureSize(pictSize.width, pictSize.height);
            cam.setParameters(param);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            try {
                cam.setDisplayOrientation(0);
                cam.setPreviewDisplay(sv.getHolder());

                Parameters param = cam.getParameters();
                List<Size> previewSizes =
                        cam.getParameters().getSupportedPreviewSizes();
                Size pre = previewSizes.get(0);
                param.setPreviewSize(pre.width, pre.height);

                LayoutParams lp = new LayoutParams(pre.width, pre.height);
                sv.setLayoutParams(lp);


                cam.setParameters(param);
                cam.startPreview();
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cam.stopPreview();
            cam.release();
        }


    }

        class TakePictureClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                cam.autoFocus(autoFocusCallback);
            }

            private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    cam.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {}
                    }, null, new TakePictureCallback());
                }
            };
        }


        class TakePictureCallback implements Camera.PictureCallback {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    File dir = new File(
                            Environment.getExternalStorageDirectory(), "Camera");
                    if(!dir.exists()) {
                        dir.mkdir();
                    }
                    Calendar calendar = Calendar.getInstance();
                    String year = String.valueOf(calendar.get(Calendar.YEAR));
                    String month = String.valueOf(calendar.get(Calendar.MONTH));
                    String date = String.valueOf(calendar.get(Calendar.DATE));
                    String hour = String.valueOf(calendar.get(Calendar.HOUR));
                    String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                    String second = String.valueOf(calendar.get(Calendar.SECOND));

                    String filePath = year + month + date + hour + minute + second + ".jpg";
                    Log.d("MainActivity", "filepath : " + filePath);

                    File f = new File(dir, filePath);
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(data);
                    Toast.makeText(getApplicationContext(),
                            "写真を保存しました", Toast.LENGTH_LONG).show();
                    registAndroidDB(f.getAbsolutePath());
                    fos.close();
                    cam.startPreview();
                } catch (Exception e) { }
            }

            private void registAndroidDB(String file) {
                ContentValues values = new ContentValues();
                ContentResolver contentResolver = MainActivity.this.getContentResolver();
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put("_data", file);
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            }

        }
    }


