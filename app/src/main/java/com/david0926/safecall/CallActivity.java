package com.david0926.safecall;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.safecall.api.RetrofitAPI;
import com.david0926.safecall.databinding.ActivitySamsungCallingBinding;
import com.david0926.safecall.databinding.ActivityTcallCallingBinding;
import com.david0926.safecall.util.SharedPreferenceUtil;
import com.david0926.safecall.util.TypeCache;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CallActivity extends AppCompatActivity {

    private SpeechRecognizer mRecognizer;
    private Intent sttIntent;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private RetrofitAPI mRetrofitDialogAPI;
    private Call<ResponseBody> mCallDialogResponse;

    private RetrofitAPI mRetrofitClovaAPI;
    private Call<ResponseBody> mCallClovaResponse;

    private Boolean isListening = false;

    private String type, speaker;

    private ActivitySamsungCallingBinding samsungBinding;
    private ActivityTcallCallingBinding tcallBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String theme = SharedPreferenceUtil.getString(this, "call_theme", "samsung");

        switch (theme) {
            default: //samsung
                samsungBinding = DataBindingUtil.setContentView(this, R.layout.activity_samsung_calling);
                samsungInit();
                break;
            case "tcall":
                tcallBinding = DataBindingUtil.setContentView(this, R.layout.activity_tcall_calling);
                tcallInit();
                break;
        }

        switch (TypeCache.getType(this).getName()) {
            case "엄마":
                type = "parents";
                speaker = "mijin";
                break;

            case "아빠":
                type = "parents";
                speaker = "jinho";
                break;

            case "친구(여성)":
                type = "couple";
                speaker = "mijin";
                break;

            default:
                type = "couple";
                speaker = "jinho";
                break;

        }

        //intent 초기화
        sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        Retrofit mRetrofitDialog = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).build();
        mRetrofitDialogAPI = mRetrofitDialog.create(RetrofitAPI.class);

        Retrofit mRetrofitClova = new Retrofit.Builder().baseUrl(getString(R.string.base_url_ncp)).build();
        mRetrofitClovaAPI = mRetrofitClova.create(RetrofitAPI.class);

        startListen();

    }

    private void samsungInit() {
        samsungBinding.btnSamsungcallingRefuse.setOnClickListener(view -> {
            finish();
        });
        samsungBinding.setName(TypeCache.getType(this).getName());
    }

    private void tcallInit() {
        tcallBinding.btnTcallcallingRefuse.setOnClickListener(view -> {
            finish();
        });
        tcallBinding.setName(TypeCache.getType(this).getName());
    }

    private void startListen() {
        isListening = true;
        mRecognizer.stopListening();
        mRecognizer.startListening(sttIntent);
    }

    private void sendDialogRequest(String text) {
        mCallDialogResponse = mRetrofitDialogAPI.getText(text, type);
        mCallDialogResponse.enqueue(mRetrofitDialogCallBack);
    }

    private void startSpeak(String text) {
        mCallClovaResponse = mRetrofitClovaAPI.getSpeech(speaker, 0, text);
        mCallClovaResponse.enqueue(mRetrofitClovaCallBack);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            isListening = false;
        }

        @Override
        public void onError(int i) {
            //Toast.makeText(CallActivity.this, "Recognition Error", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                if (!isListening) startListen();
            }, 1000);
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            String speech = rs[0];
//            Toast.makeText(CallActivity.this, speech, Toast.LENGTH_SHORT).show();

            String keyword = SharedPreferenceUtil.getString(CallActivity.this, "keyword", "");
            boolean isKeyword = false;

            for (String s : keyword.split(",")) {
                if (speech.contains(s)) {
                    isKeyword = true;
                    break;
                }
            }

            if (!keyword.isEmpty() && isKeyword) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(intent);
            } else sendDialogRequest(speech);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    private Callback<ResponseBody> mRetrofitDialogCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                String body = response.body().string();
                JSONObject bodyObject = new JSONObject(body);
                String result = bodyObject.getString("data");
                startSpeak(result);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

    private Callback<ResponseBody> mRetrofitClovaCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                byte[] result = response.body().bytes();
                playMp3(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            File tempMp3 = File.createTempFile("clova_speech", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            mediaPlayer.reset();

            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                startListen();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }
}
