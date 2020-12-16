package com.david0926.safecall;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.safecall.databinding.ActivitySamsungPendingBinding;
import com.david0926.safecall.databinding.ActivityTcallPendingBinding;
import com.david0926.safecall.util.SharedPreferenceUtil;
import com.david0926.safecall.util.TypeCache;

public class PendingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private ActivitySamsungPendingBinding samsungBinding;
    private ActivityTcallPendingBinding tcallBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String theme = SharedPreferenceUtil.getString(this, "call_theme", "samsung");

        switch (theme) {
            default: //samsung
                samsungBinding = DataBindingUtil.setContentView(this, R.layout.activity_samsung_pending);
                samsungInit();
                break;
            case "tcall":
                tcallBinding = DataBindingUtil.setContentView(this, R.layout.activity_tcall_pending);
                tcallInit();
                break;
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.horizon);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) vibrator.vibrate(new long[]{1000, 1000}, 0);

    }

    private void samsungInit() {
        samsungBinding.btnSamsungpendingAccept.setOnClickListener(view -> {
            Intent intent = new Intent(this, CallActivity.class);
            startActivity(intent);
            finish();
        });

        samsungBinding.btnSamsungpendingRefuse.setOnClickListener(view -> {
            finish();
        });

        samsungBinding.setName(TypeCache.getType(this).getName());
    }

    private void tcallInit() {
        tcallBinding.btnTcallpendingAccept.setOnClickListener(view -> {
            Intent intent = new Intent(this, CallActivity.class);
            startActivity(intent);
            finish();
        });
        tcallBinding.btnTcallpendingRefuse.setOnClickListener(view -> {
            finish();
        });

        tcallBinding.setName(TypeCache.getType(this).getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) mediaPlayer.stop();
        mediaPlayer = null;

        if(vibrator!=null) vibrator.cancel();
        vibrator = null;
    }
}
