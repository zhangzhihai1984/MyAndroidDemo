package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;
import com.usher.demo.awesome.drag.ChannelAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SceneFragment extends Fragment {
    private View mFragmentView;

    private final PublishSubject<Boolean> mVisible$ = PublishSubject.create();
    private final PublishSubject<Boolean> mCreated$ = PublishSubject.create();

    public SceneFragment() {
        Observable.combineLatest(mVisible$, mCreated$, (visible, created) -> true)
                .take(1)
                .subscribe();
    }

    public static SceneFragment newInstance() {
        Bundle args = new Bundle();
        SceneFragment fragment = new SceneFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mFragmentView) {
            mFragmentView = inflater.inflate(R.layout.fragment_smarthome_scene, container, false);
            initView();

            mCreated$.onNext(true);
        } else {
            ViewGroup parent = (ViewGroup) mFragmentView.getParent();
            if (null != parent)
                parent.removeView(mFragmentView);
        }

        return mFragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mVisible$.onNext(isVisibleToUser);
    }

    private void initView() {

        RecyclerView recyclerView = mFragmentView.findViewById(R.id.recyclerview);
        List<String> a = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.selected_channels)));
        List<String> b = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.recommended_channels)));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new ChannelAdapter(requireContext(), a, b));

    }
}
