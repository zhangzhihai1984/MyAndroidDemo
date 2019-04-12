package com.usher.demo.other.launchmode;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.usher.demo.R;

import androidx.appcompat.app.AppCompatActivity;

public class BActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_mode_multi);

        Log.i("zzh", "B onCreate");

        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("zzh", "B onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("zzh", "B onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("zzh", "B onDestroy");
    }

    private void initView() {
        TextView textView = findViewById(R.id.textview);
        textView.setText("B");

        TextView startAStandardTextView = findViewById(R.id.start_a_standard_textview);
        startAStandardTextView.setOnClickListener(v -> {
            Intent i = new Intent(this, AActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        TextView startASingleTextView = findViewById(R.id.start_a_single_textview);
        startASingleTextView.setOnClickListener(v -> {
            Intent i = new Intent(this, AActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        TextView startAnotherStandardTextView = findViewById(R.id.start_another_standard_textview);
        startAnotherStandardTextView.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setComponent(new ComponentName("com.usher.another", "com.usher.another.MainActivity"));
            startActivity(i);
        });

        TextView startAnotherNewTaskTextView = findViewById(R.id.start_another_new_task_textview);
        startAnotherNewTaskTextView.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setComponent(new ComponentName("com.usher.another", "com.usher.another.MainActivity"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.putExtra("hello", "Hello");
            startActivity(i);

            /*Intent i = new Intent();
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setComponent(new ComponentName("com.usher.another", "com.usher.another.LoginActivity"));
            startActivity(i);*/
        });


//        Intent intent1 = new Intent("com.sc.smarthome.CUSTOM_MESSAGE_CLICKED");
//        intent1.setComponent(new ComponentNa、me("com.sc.smarthome", "com.sc.smarthome.NotificationReceiver"));
//        sendBroadcast(intent1);


    }
}
