package com.usher.another;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    Intent intent = new Intent(MainActivity.this, MyReceiver.class);
                    intent.setAction("TestAction");
                    sendBroadcast(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("zzh", "Another onCreate");

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*mTextMessage.postDelayed(() -> {
            Log.i("zzh", "sendBroadcast");
            Intent intent = new Intent("TestAction");
            intent.setComponent(new ComponentName("com.usher.another", "com.usher.another.MyReceiver"));
            sendBroadcast(intent);
        }, 2000);*/

        Log.i("zzh", "Another Extra: " + getIntent().getStringExtra("hello"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("zzh", "Another onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("zzh", "Another onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("zzh", "Another onDestroy");
    }
}
