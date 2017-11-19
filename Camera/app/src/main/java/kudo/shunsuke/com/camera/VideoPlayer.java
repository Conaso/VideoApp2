package kudo.shunsuke.com.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {
    private static final int RESULT_PICK_VIDEOFILE = 1001;
    private TextView dcimPath;
         private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = (VideoView) findViewById(R.id.videoView);

        Button btn = new Button(this);


    }

    public void showGallery(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");

        startActivityForResult(intent, RESULT_PICK_VIDEOFILE);
    }
    public void startVideo(View v) {
                 videoView.start();
             }


                 public void stopVideo(View v) {
                 videoView.pause();
             }


                 @Override
         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 super.onActivityResult(requestCode, resultCode, data);


                 if (requestCode == RESULT_PICK_VIDEOFILE && resultCode == Activity.RESULT_OK) {
                         Uri uri = null;
                         if (data != null) {
                                 uri = data.getData();
                                 videoView.setVideoURI(uri);
                             }
                     }
             }


}
