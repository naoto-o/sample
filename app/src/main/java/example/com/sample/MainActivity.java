package example.com.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;





import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;





public class MainActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private MediaRecorder mRecorder;;
    private ImageButton recordButton;
    private boolean mRecordingFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoView);
        mVideoView.setVideoURI(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.nc155765));

        recordButton = findViewById(R.id.imageButton);

        // MediaRecorderのインスタンスを作成
        mRecorder = new MediaRecorder();


        // クリック時の処理
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // 録音状態判定
                if(!mRecordingFlag) {

                    mVideoView.start();


                    try {
                        // マイクからの入力設定
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                        // 記録フォーマットをデフォルトに設定
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

                        // 音声コーデックをデフォルトに設定
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                        // 出力ファイルパスを設定
                        //保存先
                        String filePath = Environment.getExternalStorageDirectory() + "/sample.wav";
                        mRecorder.setOutputFile(filePath);

                        // レコーダーを準備
                        mRecorder.prepare();


                    } catch(IllegalStateException e) {
                        e.printStackTrace();

                    } catch(IOException e) {
                        e.printStackTrace();
                    }


                    // 録音開始
                    mRecorder.start();

                    // ボタンテキスト変更
                    recordButton.setImageResource(R.drawable.stop);

                    // フラグを変更
                    mRecordingFlag = true;


                } else {


                    try {
                        // ボタンテキスト変更
                        recordButton.setImageResource(R.drawable.rec);

                        mVideoView.stopPlayback();

                        //mVideoViewの表示を初期化
                        clearCurrentFrame();

                        // 録音停止
                        mRecorder.stop();

                        // 再使用に備えてレコーダーの状態をリセット
                        mRecorder.reset();

                        // フラグを変更
                        mRecordingFlag = false;

                        //テキスト入力を受け付けるビューを作成します。
                        final EditText editView = new EditText(MainActivity.this);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("ファイル名を入力")
                                //setViewにてビューを設定します。
                                .setView(editView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //入力した文字をトースト出力する
                                        Toast.makeText(MainActivity.this,
                                                "ファイル名    "+editView.getText().toString()+"    保存しました",
                                                Toast.LENGTH_LONG).show();
                                        //Fileオブジェクトを生成する
                                        File fOld = new File(Environment.getExternalStorageDirectory() + "/sample.wav");
                                        File fNew = new File(Environment.getExternalStorageDirectory() + "/"+editView.getText().toString()+".wav");

                                        if (fOld.exists()) {
                                            //ファイル名変更実行
                                            fOld.renameTo(fNew);
                                        }

                                    }
                                })
                                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        File file = new File(Environment.getExternalStorageDirectory() + "/sample.wav");

                                        //deleteメソッドを使用してファイルを削除する
                                        file.delete();

                                    }
                                })
                                .show();

                    } catch(IllegalStateException e) {
                        e.printStackTrace();
                    }

                }
            }






        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 使わなくなった時点でレコーダーリソースを解放
        mRecorder.release();
    }
    public void clearCurrentFrame() {
        mVideoView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);
    }








}
