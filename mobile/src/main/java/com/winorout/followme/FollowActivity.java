package com.winorout.followme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.winorout.followme.barrage.BarrageFragment;
import com.winorout.followme.personalCenter.PersonalCenterFragment;
import com.winorout.followme.sports.SportsFragment;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager mviewPager;
    private FragmentPagerAdapter madapter;
    private List<Fragment> mfragments;
    private LinearLayout mtabSports;
    private LinearLayout mtabBarrage;
    private LinearLayout mtabPersonal;
    private ImageView mtabSportsImg;
    private ImageView mtabBarrageImg;
    private ImageView mtabPersonalImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_follow);
        //wanxuedong
        initView();
        initEvent();
        setSelect(0);
    }

    private void initEvent() {
        mtabSports.setOnClickListener(this);
        mtabBarrage.setOnClickListener(this);
        mtabPersonal.setOnClickListener(this);
        mviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mtabSports = (LinearLayout) findViewById(R.id.tab_sports);
        mtabBarrage = (LinearLayout) findViewById(R.id.tab_barrage);
        mtabPersonal = (LinearLayout) findViewById(R.id.tab_personal);
        mtabSportsImg = (ImageView) findViewById(R.id.tab_sports_img);
        mtabBarrageImg = (ImageView) findViewById(R.id.tab_barrage_img);
        mtabPersonalImg = (ImageView) findViewById(R.id.tab_personal_img);
        mviewPager = (ViewPager) findViewById(R.id.viewPager);
        mfragments = new ArrayList<Fragment>();
        Fragment msportsFragment = new SportsFragment();
        Fragment mbarrageFragment = new BarrageFragment();
        Fragment mPersonalCenterFragment = new PersonalCenterFragment();
        mfragments.add(msportsFragment);
        mfragments.add(mbarrageFragment);
        mfragments.add(mPersonalCenterFragment);
        madapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mfragments.get(position);
            }

            @Override
            public int getCount() {
                return mfragments.size();
            }
        };
        mviewPager.setAdapter(madapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_sports:
                setSelect(0);
                break;
            case R.id.tab_barrage:
                setSelect(1);
                break;
            case R.id.tab_personal:
                setSelect(2);
                break;

        }
    }

    private void setSelect(int i) {
        setTab(i);
        mviewPager.setCurrentItem(i);
    }

    private void setTab(int i) {
        resertImg();
        switch (i) {
            case 0:
                mtabSportsImg.setImageResource(R.drawable.img_sports_pressed);
                break;
            case 1:
                mtabBarrageImg.setImageResource(R.drawable.img_barrage_pressed);
                break;
            case 2:
                mtabPersonalImg.setImageResource(R.drawable.img_personal_pressed);
                break;
        }
    }

    /**
     * 将所有Tab图片重置为暗色
     */
    private void resertImg() {
        mtabSportsImg.setImageResource(R.drawable.img_sports_nomal);
        mtabBarrageImg.setImageResource(R.drawable.img_barrage_nomal);
        mtabPersonalImg.setImageResource(R.drawable.img_personal_nomal);
    }
}
