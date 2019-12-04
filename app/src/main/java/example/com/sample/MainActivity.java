package example.com.sample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

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

    private static final String TAG = "M Permission";
    private int REQUEST_CODE_RECORD_AUDIO_PERMISSION = 0x01;



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


                if ((PermissionChecker.checkSelfPermission(
                        MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED )||
                        (PermissionChecker.checkSelfPermission(
                                MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)) {
                    // パーミッションをリクエストする
                    requestRECORDAUDIOPermission();
                    return;
                }

                // 録音状態判定
                if(!mRecordingFlag) {

                    mVideoView.start();


                    try {
                        // マイクからの入力設定-
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

    private void requestRECORDAUDIOPermission(){
        if (
                (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,Manifest.permission.RECORD_AUDIO))||
                        (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,Manifest.permission.WRITE_EXTERNAL_STORAGE))){

            Log.d(TAG, "shouldShowRequestPermissionRationale:追加説明");
            // 権限チェックした結果、持っていない場合はダイアログを出す
            new AlertDialog.Builder(this)
                    .setTitle("パーミッションの追加説明")
                    .setMessage("このアプリを使用するにはパーミッションが必要です")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_CODE_RECORD_AUDIO_PERMISSION);
                        }
                    })
                    .create()
                    .show();
            return;
        }
        // 権限を取得する
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_RECORD_AUDIO_PERMISSION);
        return;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length != 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult:DENYED");

                if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) ||
                        (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE))){
                    Log.d(TAG, "[show error]");
                    new AlertDialog.Builder(this)
                            .setTitle("パーミッション取得エラー ")
                            .setMessage("再試行する場合は、再度Requestボタンを押してください")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // サンプルのため、今回はもう一度操作をはさんでいますが
                                    // ここでrequestCameraPermissionメソッドの実行でもよい
                                }
                            })
                            .create()
                            .show();

                } else {
                    Log.d(TAG, "[show app settings guide]");
                    new AlertDialog.Builder(this)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("今後は許可しないが選択されています。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    openSettings();
                                }
                            })
                            .create()
                            .show();
                }
            } else {
                Log.d(TAG, "onRequestPermissionsResult:GRANTED");
                // TODO 許可されたのでカメラにアクセスする
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Fragmentの場合はgetContext().getPackageName()
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }







}
