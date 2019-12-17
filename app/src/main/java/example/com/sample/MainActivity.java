package example.com.sample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private MediaRecorder mRecorder;;
    private ImageButton recordButton;
    private boolean mRecordingFlag = false;

    private static final String TAG = "M Permission";
    private int REQUEST_CODE_RECORD_AUDIO_PERMISSION = 0x01;
    //保存先 
    String filePath = Environment.getExternalStorageDirectory() + "/sample.wav";
    String sdPath = Environment.getExternalStorageDirectory().getPath();

    //日付時刻の書式定義
    SimpleDateFormat simpleDateFormat;
    private MediaPlayer mp=null;

    private ArrayList<Rec> recList = new ArrayList<>();
    private ListView lv;
    private File[] files;
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoView);
        mVideoView.setVideoURI(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.nc155765));

        recordButton = findViewById(R.id.imageButton);

        // MediaRecorderのインスタンスを作成
        mRecorder = new MediaRecorder();

        myAdapter= new MyAdapter(MainActivity.this);

        simpleDateFormat= new SimpleDateFormat("yyyy/MM/dd");


        files = new File(sdPath).listFiles();
        if(files != null){
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile() && files[i].getName().endsWith(".wav")){
                    //更新日時取得するのにlastModified()を使う。
                    Long lastModified = files[i].lastModified();
                    //longで来た値をsimpleDateFormatで整形
                    String upDateTime = simpleDateFormat.format(lastModified);
                    Rec rec=new Rec();
                    rec.setUpdateTime(upDateTime);
                    rec.setName(files[i].getName());
                    recList.add(rec);
                }
            }

            lv =findViewById(R.id.scrollView);
            myAdapter.setVoiceList(recList);
            lv.setAdapter(myAdapter);
        }

        // クリック時の処理
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if ((ContextCompat.checkSelfPermission(
                        MainActivity.this,Manifest.permission.RECORD_AUDIO)
                        !=PackageManager.PERMISSION_GRANTED )||
                        (ContextCompat.checkSelfPermission(
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

                        alertdialogshow();

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
                if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) ||
                        (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE))){
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
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    public void alertdialogshow(){
        mp = new MediaPlayer();
        //テキスト入力を受け付けるビューを作成
        final EditText editView = new EditText(MainActivity.this);
        final AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);

        alert.setCancelable(false);
        alert.setTitle("ファイル名を入力");
        //setViewにてビューを設定します。
        alert.setView(editView);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if(!editView.getText().toString().equals("")){

                    //入力した文字をトースト出力する
                    Toast.makeText(MainActivity.this,
                            "ファイル名    "+editView.getText().toString()+"    保存しました",
                            Toast.LENGTH_LONG).show();

                    //Fileオブジェクトを生成する
                    File fOld = new File(filePath);
                    File fNew = new File(Environment.getExternalStorageDirectory() + "/"+editView.getText().toString()+".wav");

                    if (fOld.exists()) {
                        //ファイル名変更実行
                        fOld.renameTo(fNew);
                        //更新日時取得するのにlastModified()を使う。
                        Long lastModified =fNew.lastModified();
                        //longで来た値をsimpleDateFormatで整形
                        String upDateTime = simpleDateFormat.format(lastModified);

                        Rec rec=new Rec();
                        rec.setUpdateTime(upDateTime);
                        rec.setName(fNew.getName());
                        recList.add(rec);

                        //ファイル保存時にリストビューへ即時反映
                        myAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,
                            "ファイル名を入力して下さい",
                            Toast.LENGTH_LONG).show();
                    alertdialogshow();
                }
            }
        })
                .setNeutralButton("再生", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ローカルファイルを再生-
                        try {
                            mp.setDataSource(filePath);
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //再生開始
                        mp.start();

                        Toast.makeText(MainActivity.this,
                                "再生中",
                                Toast.LENGTH_LONG).show();

                        final EditText editView = new EditText(MainActivity.this);
                        final AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);

                        alert.setCancelable(false);
                        alert.setTitle("ファイル名を入力");
                        //setViewにてビューを設定します。
                        alert.setView(editView);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if(!editView.getText().toString().equals("")){

                                    //入力した文字をトースト出力する
                                    Toast.makeText(MainActivity.this,
                                            "ファイル名    "+editView.getText().toString()+"    保存しました",
                                            Toast.LENGTH_LONG).show();

                                    //Fileオブジェクトを生成する
                                    File fOld = new File(filePath);
                                    File fNew = new File(Environment.getExternalStorageDirectory() + "/"+editView.getText().toString()+".wav");

                                    if (fOld.exists()) {

                                        //ファイル名変更実行
                                        fOld.renameTo(fNew);
                                        //更新日時取得するのにlastModified()を使う。
                                        Long lastModified =fNew.lastModified();
                                        //longで来た値をsimpleDateFormatで整形
                                        String upDateTime = simpleDateFormat.format(lastModified);

                                        Rec rec=new Rec();
                                        rec.setUpdateTime(upDateTime);
                                        rec.setName(fNew.getName());
                                        recList.add(rec);

                                        //ファイル保存時にリストビューへ即時反映
                                        myAdapter.notifyDataSetChanged();
                                    }

                                }
                                else{
                                    Toast.makeText(MainActivity.this,
                                            "ファイル名を入力して下さい",
                                            Toast.LENGTH_LONG).show();
                                    alertdialogshow();
                                }
                            }
                        })
                                .setNeutralButton("停止", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        mp.stop();
                                        mp.release();

                                        alertdialogshow();
                                    }
                                })
                                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        File file = new File(filePath);

                                        //deleteメソッドを使用してファイルを削除する
                                        file.delete();
                                    }
                                })
                                .show();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File file = new File(filePath);

                        //deleteメソッドを使用してファイルを削除する
                        file.delete();
                    }
                });
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        // 使わなくなった時点でレコーダーリソースを解放
        mRecorder.release();
    }
}