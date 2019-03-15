package com.usher.demo.pager;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.twigcodes.ui.indicator.PageIndicatorView;
import com.twigcodes.ui.pager.LoopViewPager;
import com.usher.demo.R;

import java.util.ArrayList;
import java.util.List;

public class PagerActivity extends AppCompatActivity {
    private final List<String> mUrlList = new ArrayList<>();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private FragmentAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534943035723&di=52cd92a75bc7939284a1180d1ad45e16&imgtype=0&src=http%3A%2F%2Fattachments.gfan.com%2Fforum%2F201707%2F21%2F1751135o5ta40wolwottw5.png");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534943236734&di=aedb53d84eb4fb75695a1c415a4b59cd&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201502%2F17%2F20150217093254_acRPa.jpeg");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534942509719&di=3248b1e2c4ab810cc71453086b5e579f&imgtype=jpg&src=http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D3748600783%2C1996173154%26fm%3D214%26gp%3D0.jpg");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534938793944&di=aca46413eba1f53a91a7518153940f37&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201603%2F03%2F20160303024913_ivGtf.thumb.700_0.jpeg");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534943171294&di=a4902e10a79b7aaed3e75c4b898d335f&imgtype=0&src=http%3A%2F%2Fimg16.3lian.com%2Fgif2016%2Fq25%2F58%2F44.jpg");

        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.indicator_animator).setDuration(2000);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), mUrlList);

        // Set up the ViewPager with the sections adapter.
        LoopViewPager mViewPager = findViewById(R.id.viewpager);
//        mViewPager.setCacheEnabled(true);
        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.enableAutoPage(1000);

        PageIndicatorView mIndicatorView = findViewById(R.id.indicatorview);
        mIndicatorView.setViewPager(mViewPager);
//        mViewPager.setCurrentItem(5);
        mViewPager.postDelayed(() -> {
//            mViewPager.setCurrentItem(1);

//            mUrlList.remove(0);
//            mPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), mUrlList);
//            mViewPager.setAdapter(mPagerAdapter);

        }, 3000);
        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
//            ObjectAnimator animator = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 0.5f, 1.5f, 1.0f);
//            animator.setDuration(1000);
//            animator.start();

            animator.setTarget(addButton);
            animator.start();

            if (mViewPager.isAutoPageEnabled()) {
                mViewPager.disableAutoPage();
            } else {
                mViewPager.enableAutoPage(1000);
            }
        });
        Button removeButton = findViewById(R.id.remove);
        removeButton.setOnClickListener(v -> {
            if (!mUrlList.isEmpty()) {
                mUrlList.remove(0);
                mUrlList.remove(0);

                mPagerAdapter.setData(mUrlList);
                mPagerAdapter.notifyDataSetChanged();

                animator.setTarget(removeButton);
                animator.start();
            }
        });

        ViewAdapter mPagerAdapter2 = new ViewAdapter(this);
        ViewPager mViewPager2 = findViewById(R.id.viewpager2);
//        mViewPager2.cacheBothEnds(true);
        mViewPager2.setAdapter(mPagerAdapter2);
//        mViewPager2.setCurrentItem(1);

        PageIndicatorView mIndicatorView2 = findViewById(R.id.indicatorview2);
        mIndicatorView2.setViewPager(mViewPager2);

        findViewById(R.id.add2).setOnClickListener(v -> {
            mPagerAdapter2.addItem();
        });
        findViewById(R.id.remove2).setOnClickListener(v -> {
            mPagerAdapter2.removeItem();
        });

        new PageIndicatorView(this);
    }
}
