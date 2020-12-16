package com.david0926.safecall;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

import com.david0926.safecall.api.RetrofitAPI;
import com.david0926.safecall.databinding.ActivityChatBinding;
import com.david0926.safecall.databinding.ActivityMainBinding;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity {
    private SpeechRecognizer mRecognizer;
    private TextToSpeech textToSpeech;
    private Intent sttIntent;
    private String lastSpeech="";

    private RetrofitAPI mRetrofitDialogAPI;
    private Call<ResponseBody> mCallDialogResponse;

    private RetrofitAPI mRetrofitClovaAPI;
    private Call<ResponseBody> mCallClovaResponse;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        Retrofit mRetrofitDialog = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).build();
        mRetrofitDialogAPI = mRetrofitDialog.create(RetrofitAPI.class);

        binding.setIsListening(false);
        binding.btnMainMic.setOnClickListener(view -> {
            if (binding.getIsListening()) {
                mRecognizer.stopListening();
                binding.setIsListening(false);
            } else {
                //setTapTarget(binding.btnMainMic);
                mRecognizer.startListening(sttIntent);
                binding.setIsListening(true);
            }
        });

        textToSpeech = new TextToSpeech(this, i -> {
            if (i != TextToSpeech.ERROR) textToSpeech.setLanguage(Locale.KOREAN);
        });

        Retrofit mRetrofitClova = new Retrofit.Builder().baseUrl(getString(R.string.base_url_ncp)).build();
        mRetrofitClovaAPI = mRetrofitClova.create(RetrofitAPI.class);
    }

    private void setTapTarget(View view) {
        TapTargetView.showFor(this,
                TapTarget.forView(view, "")
                        .outerCircleAlpha(0)
                        .transparentTarget(true)
                        .targetCircleColor(R.color.colorPrimary)
                        .targetRadius(32),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                    }
                });
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
            binding.setIsListening(false);
        }

        @Override
        public void onError(int i) {
            //binding.setIsListening(false);
            Toast.makeText(ChatActivity.this, "Recognition Error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            String speech = rs[0];

            //stt issue fix
            if(!lastSpeech.equals(speech)){
                lastSpeech = speech;
                binding.setSpeech(speech);
                sendDialogRequest(speech);
            }


        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    private void readText(String text) {
        //textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        mCallClovaResponse = mRetrofitClovaAPI.getSpeech("jinho", 0, text);
        mCallClovaResponse.enqueue(mRetrofitClovaCallBack);
    }

    private void sendDialogRequest(String text) {
        mCallDialogResponse = mRetrofitDialogAPI.getText(text, binding.getType());
        mCallDialogResponse.enqueue(mRetrofitDialogCallBack);
    }

    private Callback<ResponseBody> mRetrofitDialogCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                String body = response.body().string();
                JSONObject bodyObject = new JSONObject(body);
                String result = bodyObject.getString("data");
                binding.setResponse(result);
                readText(result);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        if (mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }
}
