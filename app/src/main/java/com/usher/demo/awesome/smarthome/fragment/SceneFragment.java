package com.usher.demo.awesome.smarthome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usher.demo.R;
import com.usher.demo.awesome.drag.ChannelAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SceneFragment extends Fragment {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

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
            ButterKnife.bind(this, mFragmentView);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(new ChannelAdapter(requireContext(), new ArrayList<>(), new ArrayList<>()));
    }
}