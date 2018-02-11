package kudo.shunsuke.com.videoapp;


import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
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
                    Toast.makeText(getApplicationContext(), "2つ以上、動画を選んでね♥", Toast.LENGTH_SHORT).show();
                    return;
                }
//何かに新しいカウントをする項目を留める
                Uri[] contentUri = new Uri[clip.getItemCount()];
                for (int i = 0; i < clip.getItemCount(); i++) {
                    //Clipdateの配列からuriの配列にfor文で変えた。
                    contentUri[i] = clip.getItemAt(i).getUri();
                }

                String[] contentpath = new String[clip.getItemCount()];
                for (int i = 0;  i < clip.getItemCount() ; i++){
                    //uriの配列からString(path)の配列に変えた。
                    contentpath[i] = getPath(this,contentUri[i]);
                }

                //URIというファイルで再生している(データを持ってきた時の通行書(path)をもらってる。)
                Uri uri1 = contentUri[0];
                String path = getPath(this, uri1);
                Uri uri2 = contentUri[1];
                String path1 = getPath(this, uri2);

//try文を使用したよ～　（↓ここ成功してない。↑はOK）
                try {

                    Log.d("TAG", path);
                    Log.d("TAG", path1);
                    //動画を読み込む部分

                   // Movie[] movies = new Movie[]{
                           // MovieCreator.build(path),
                           // MovieCreator.build(path1)};
                    Movie[] movies = new Movie[clip.getItemCount()];
                    for (int i = 0; i < clip.getItemCount(); i++){
                        movies[i] = MovieCreator.build(contentpath[i]);
                    }


                    List<Track> videoTracks = new LinkedList<Track>();
                    for (Movie m : movies) {
//                        Track audioTrack = m.getTracks().get(1);
//                        result.addTrack(audioTrack);
                        Track videoTrack = m.getTracks().get(0);
                        videoTracks.add(videoTrack);
                    }
                    Movie result = new Movie();
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }

                    // 出力
                    Container out = new DefaultMp4Builder().build(result);
                    String outputFilePath = Environment.getExternalStorageDirectory() + "/output_append.mp4";
                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();
                    videoView.setVideoURI(Uri.fromFile(new File(outputFilePath)));

                } catch (Exception e) {

                    Log.e("TAG", e.toString());
                }
            }
        }
    }

//    public static String getPath(Context context, Uri uri) {
//        ContentResolver contentResolver = context.getContentResolver();
//        String[] columns = {MediaStore.Images.Media.DATA};
//        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
//        if (cursor == null) {
//            return "";
//        }
//        cursor.moveToFirst();
//        String path = cursor.getString(0);
//        cursor.close();
//        return path;
//    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }




}



