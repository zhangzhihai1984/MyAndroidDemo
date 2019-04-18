package com.usher.demo.awesome.smarthome;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usher.demo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SmartSceneAdapter extends RecyclerView.Adapter {
    private final Context mContext;

    public SmartSceneAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_smarthome_scene_item, parent, false);
        return new SceneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class SceneViewHolder extends RecyclerView.ViewHolder {
        final TextView mEnNameTextView;

        SceneViewHolder(@NonNull View itemView) {
            super(itemView);

            mEnNameTextView = itemView.findViewById(R.id.en_name_textview);
            Shader shader = new LinearGradient(0, 0, 0, mEnNameTextView.getTextSize(), Color.parseColor("#3eac68"), Color.parseColor("#e9f6ee"), Shader.TileMode.CLAMP);
            mEnNameTextView.getPaint().setShader(shader);
        }
    }
}
