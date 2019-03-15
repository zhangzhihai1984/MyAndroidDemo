package com.usher.demo.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.usher.demo.R;

public class FitsSystemWindowActivity extends AppCompatActivity {
    private final String EXTRA_NAME = "EXTRA_TAG";
    private final String EXTRA_VALUE_NONE = "NONE";
    private final String EXTRA_VALUE_ROOT = "ROOT";
    private final String EXTRA_VALUE_TOP = "TOP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fitssystemwindow);

        initView();

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void initView() {
        TextView statusTextView = findViewById(R.id.status_textview);
        statusTextView.setText(getString(R.string.fitssystemwindow_status_text, "NONE"));
        String value = getIntent().getStringExtra(EXTRA_NAME);

        if (value != null) {
            switch (getIntent().getStringExtra(EXTRA_NAME)) {
                case EXTRA_VALUE_ROOT:
                    findViewById(R.id.root_layout).setFitsSystemWindows(true);
                    statusTextView.setText(getString(R.string.fitssystemwindow_status_text, "Root View"));
                    break;
                case EXTRA_VALUE_TOP:
//                    findViewById(R.id.attr_des_textview).setFitsSystemWindows(true);
                    findViewById(R.id.top_textview).setFitsSystemWindows(true);
                    statusTextView.setText(getString(R.string.fitssystemwindow_status_text, "Top View"));

                    break;
                default:
                    break;
            }
        }

        findViewById(R.id.none_view_textview).setOnClickListener(v -> {
            Intent intent = new Intent(this, FitsSystemWindowActivity.class);
            intent.putExtra(EXTRA_NAME, EXTRA_VALUE_NONE);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.root_view_textview).setOnClickListener(v -> {
            Intent intent = new Intent(this, FitsSystemWindowActivity.class);
            intent.putExtra(EXTRA_NAME, EXTRA_VALUE_ROOT);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.top_view_textview).setOnClickListener(v -> {
            Intent intent = new Intent(this, FitsSystemWindowActivity.class);
            intent.putExtra(EXTRA_NAME, EXTRA_VALUE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
