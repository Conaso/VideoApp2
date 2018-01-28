package kudo.shunsuke.com.videoapp;


import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.h264.H264TrackImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_VIDEOFILE = 1001;
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.videoView);
    }

    public void showGallery(View v) {

        Intent intent = new Intent();
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Movie"), RESULT_PICK_VIDEOFILE);
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


//intentで複数の動画を持ってきた、かつ、それが成功した時、
        if (requestCode == RESULT_PICK_VIDEOFILE && resultCode == Activity.RESULT_OK) {


            //何もなくする。
            Uri uri = null;

            //データが何かあった時、。
            if (data != null) {
                //データの中から選択したデータを留める。
                ClipData clip = data.getClipData();

                //データが何もなかった時、、
                if (clip == null) {
                    Toast.makeText(getApplicationContext(), "Clip is nothing", Toast.LENGTH_SHORT).show();
                    return;
                }
//何かに新しいカウントをする項目を留める
                Uri[] contentUri = new Uri[clip.getItemCount()];
                for (int i = 0; i < clip.getItemCount(); i++) {
                    contentUri[i] = clip.getItemAt(i).getUri();
                }

                //URIというファイルで再生している(データを持ってきた時の通行書(path)をもらってる。)
                videoView.setVideoURI(contentUri[0]);
                Uri uri1 = contentUri[0];
                String path = getPath(this, uri1);
                Uri uri2 = contentUri[1];
                String path1 = getPath(this, uri2);

//try文を使用したよ～　（↓ここ成功してない。↑はOK）
                try {

                    Log.e("TAG", path);
                    Log.e("TAG", path1);
                    //動画を読み込む部分
                    Movie result =new  Movie();
                    H264TrackImpl mp4 = new H264TrackImpl(new FileDataSourceImpl(path));
                    H264TrackImpl mp4_2 = new H264TrackImpl((new FileDataSourceImpl(path1)));

                    result.addTrack(mp4);
                    result.addTrack(mp4_2);


//                    //↑の部分に問題あり？
//                    List<Track> videoTracks = new LinkedList<Track>();
//                    List<Track> audioTracks = new LinkedList<Track>();
//                    for (Movie m : movie) {
//                        for (Track t : m.getTracks()) {
//                            if (t.getHandler().equals("soun")) {
//                                audioTracks.add(t);
//                            }
//                            if (t.getHandler().equals("vide")) {
//                                videoTracks.add(t);
//                            }
//                        }
//                    }
//
//                    Movie result = new Movie();
//                    if (audioTracks.size() > 0) {
//                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
//                    }
//                    if (videoTracks.size() > 0) {
//                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
//                    }
//
//                    */

                    Container out = new DefaultMp4Builder().build(result);
                    String outputFilePath = Environment.getExternalStorageDirectory() + "/output_append.mp4";
                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();

                    videoView.setVideoURI(Uri.fromFile(getFileStreamPath(fos.toString())));

                } catch (Exception e) {

                    Log.e("TAG", e.toString());
                }
            }
        }
    }

    public static String getPath(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        if (cursor == null) {
            return "";
        }
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        return path;
    }


}



