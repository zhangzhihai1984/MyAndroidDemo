package com.usher.demo.launchmode;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.usher.demo.R;

public class AActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_mode);

        Log.i("zzh", "A onCreate");

        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("zzh", "A onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("zzh", "A onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("zzh", "A onDestroy");
    }

    private void initView() {
        TextView textView = findViewById(R.id.textview);
        textView.setText("A");

        TextView goTextView = findViewById(R.id.go_textview);
        goTextView.setText("Start B");
        goTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, BActivity.class));
        });
//        goTextView.setOnClickListener(v -> startService(new Intent(this, MyService.class)));

        goTextView.postDelayed(() -> {
//            Intent intent = getPackageManager().getLaunchIntentForPackage("com.usher.demo");
//            startActivity(intent);

            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(new ComponentName("com.usher.demo", "com.usher.demo.MainActivity"));
            startActivity(i);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, MyService.class));
            } else {
                startService(new Intent(this, MyService.class));
            }*/
            startService(new Intent(this, MyService.class));

        }, 3000);
    }
}
