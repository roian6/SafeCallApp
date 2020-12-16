package com.david0926.safecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.david0926.safecall.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.bottomMain.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.action_1:
                    switchFragment(MainFragment1.newInstance());
                    break;
                case R.id.action_2:
                    switchFragment(MainFragment2.newInstance());
                    break;
                case R.id.action_3:
                    switchFragment(MainFragment3.newInstance());
                    break;
                case R.id.action_4:
                    switchFragment(MainFragment4.newInstance());
                    break;
            }

            return true;
        });

        switchFragment(MainFragment1.newInstance());

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals("safecall_call")) {
                    startActivity(new Intent(MainActivity.this, PendingActivity.class));
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("safecall_call"));

    }

    void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
