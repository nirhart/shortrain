package com.nirhart.shortrain.rail;

import android.app.Activity;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.WindowManager;

import com.nirhart.shortrain.R;
import com.nirhart.shortrain.utils.ShortcutsUtils;

import java.util.Collections;

public class RailActionActivity extends Activity {

    final public static String RAIL_ROTATION_KEY = "rotation";
    final public static String RAIL_ID_KEY = "id";
    final public static String RAIL_RECT_KEY = "rect";
    final public static String NEW_RAIL_ID_VALUE = "new_rail_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String id = getIntent().getExtras().getString(RAIL_ID_KEY);
        int railNumber = ShortcutsUtils.getRailNumber(id);

        int rotation = getIntent().getExtras().getInt(RAIL_ROTATION_KEY);

        int nextIcon = 0;
        int nextRotation = 0;

        switch (rotation) {
            case RailInfo.NOT_SET:
                nextIcon = R.drawable.horizontal;
                nextRotation = RailInfo.HORIZONTAL;
                break;
            case RailInfo.HORIZONTAL:
                nextIcon = R.drawable.vertical;
                nextRotation = RailInfo.VERTICAL;
                break;
            case RailInfo.VERTICAL:
                nextIcon = R.drawable.bottom_left_corner;
                nextRotation = RailInfo.BOTTOM_LEFT_CORNER;
                break;
            case RailInfo.BOTTOM_LEFT_CORNER:
                nextIcon = R.drawable.bottom_right_corner;
                nextRotation = RailInfo.BOTTOM_RIGHT_CORNER;
                break;
            case RailInfo.BOTTOM_RIGHT_CORNER:
                nextIcon = R.drawable.top_left_corner;
                nextRotation = RailInfo.TOP_LEFT_CORNER;
                break;
            case RailInfo.TOP_LEFT_CORNER:
                nextIcon = R.drawable.top_right_corner;
                nextRotation = RailInfo.TOP_RIGHT_CORNER;
                break;
            case RailInfo.TOP_RIGHT_CORNER:
                nextIcon = R.drawable.horizontal;
                nextRotation = RailInfo.HORIZONTAL;
                break;
        }

        Rect r = getIntent().getSourceBounds();
        ShortcutInfo thisRailShortcut = ShortcutsUtils.createRailShortcut(this, railNumber, nextIcon, nextRotation, r);

        shortcutManager.updateShortcuts(Collections.singletonList(thisRailShortcut));
        shortcutManager.removeDynamicShortcuts(Collections.singletonList(id));

        int newRailId = ShortcutsUtils.getNextRailNumber(shortcutManager);
        ShortcutInfo railShortcut = ShortcutsUtils.createRailShortcut(this, newRailId);
        shortcutManager.addDynamicShortcuts(Collections.singletonList(railShortcut));
        finish();

        super.onCreate(savedInstanceState);
    }
}
