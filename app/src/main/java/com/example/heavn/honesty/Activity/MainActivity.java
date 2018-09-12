package com.example.heavn.honesty.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.heavn.honesty.Fragment.DetailFragment;
import com.example.heavn.honesty.Fragment.HomeFragment;
import com.example.heavn.honesty.Fragment.HonestyFragment;
import com.example.heavn.honesty.R;

import cn.bmob.v3.Bmob;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener{
    private HomeFragment homeFragment;
    private HonestyFragment honestyFragment;
    private DetailFragment detailFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationBar bottomNavigationBar;
    private int lastSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "1b783d63f7d627975f1276778ac90c8c");

        /**
         * bottomNavigation 设置
         */
        bottomNavigationBar = findViewById(R.id.navigation);
        /** 导航基础设置 包括按钮选中效果 导航栏背景色等 */
        bottomNavigationBar
                .setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_FIXED)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                /**
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */
                .setInActiveColor(R.color.gray) //未选中颜色
                .setBarBackgroundColor(R.color.white);//导航栏背景色

        /** 添加导航按钮 */
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home, R.string.title_home).setActiveColorResource(R.color.green))
                .addItem(new BottomNavigationItem(R.drawable.sign, R.string.title_honesty).setActiveColorResource(R.color.gold))
                .addItem(new BottomNavigationItem(R.drawable.detail, R.string.title_detail).setActiveColorResource(R.color.light_blue))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise(); //initialise 一定要放在 所有设置的最后一项

        setDefaultFragment();//设置默认导航栏

    }

    /**
     * 设置默认导航栏
     */
    private void setDefaultFragment() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.framelayout, homeFragment);
        fragmentTransaction.commit();
    }
    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                transaction.replace(R.id.framelayout, homeFragment);
                break;
            case 1:
                if (honestyFragment == null) {
                    honestyFragment = new HonestyFragment();
                }
                transaction.replace(R.id.framelayout, honestyFragment);
                break;
            case 2:
                if (detailFragment == null) {
                    detailFragment = new DetailFragment();
                }
                transaction.replace(R.id.framelayout, detailFragment);
                break;
            default:
                break;
        }

        transaction.commit();// 事务提交
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

}
