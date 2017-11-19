package kudo.shunsuke.com.videoapp;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_VIDEOFILE = 1001;
    private TextView dcimPath;
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.videoView);
    }

    public void showGallery(View v){

        Intent intent = new Intent();
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Movie"), RESULT_PICK_VIDEOFILE);
    }

    public void startVideo(View v){
        videoView.start();
    }

    public void stopVideo(View v){
        videoView.pause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

//intentで複数の動画を持ってきた、かつ、それが成功した時、
        if (requestCode == RESULT_PICK_VIDEOFILE && resultCode == Activity.RESULT_OK){
            //何もなくする。
            Uri uri = null;

            //データが何かあった時、。
            if (data != null){


                //データの中から選択したデータを留める。
                ClipData clip = data.getClipData();

                if (clip  == null) {
                    Toast.makeText(getApplicationContext(), "Clip is nothing", Toast.LENGTH_SHORT).show();
                    return;
                }
//何かに新しいカウントをする項目を留める
                Uri[] contentUri = new Uri[clip.getItemCount()];
                for (int i = 0; i < clip.getItemCount(); i++) {
                    contentUri[i] = clip.getItemAt(i).getUri();
                }

                    //videoviewに写す
                    uri = data.getData();
                    videoView.setVideoURI(uri);
            }
        }
    }
}
