package com.nirhart.shortrain;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.nirhart.shortrain.tutorial.TutorialFragment;
import com.nirhart.shortrain.tutorial.TutorialViewPagerAdapter;
import com.nirhart.shortrain.utils.ShortcutsUtils;

import java.util.Arrays;

public class MainActivity extends Activity implements TutorialFragment.OnNextSlideClicked {

    private TutorialViewPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        shortcutManager.removeAllDynamicShortcuts();
        int newWallId = ShortcutsUtils.getNextRailNumber(shortcutManager);
        ShortcutInfo ballShortcut = ShortcutsUtils.createTrainShortcut(this);
        ShortcutInfo wallShortcut = ShortcutsUtils.createRailShortcut(this, newWallId);
        shortcutManager.setDynamicShortcuts(Arrays.asList(ballShortcut, wallShortcut));

        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.activity_main_root);

        viewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        pagerAdapter = new TutorialViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void finishActivity() {
        rootView.animate()
                .alpha(0f)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finish();
                    }
                })
                .start();
    }

    @Override
    public void onClick(View v) {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem + 1 < pagerAdapter.getCount()) {
            viewPager.setCurrentItem(currentItem + 1);
        } else {
            finishActivity();
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }
}
