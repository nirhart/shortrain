package com.nirhart.shortrain.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;

import com.nirhart.shortrain.R;
import com.nirhart.shortrain.rail.RailActionActivity;
import com.nirhart.shortrain.rail.RailInfo;
import com.nirhart.shortrain.train.TrainActionActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nirhart.shortrain.rail.RailActionActivity.RAIL_RECT_KEY;
import static com.nirhart.shortrain.rail.RailActionActivity.RAIL_ROTATION_KEY;

public class ShortcutsUtils {

    public static ShortcutInfo createTrainShortcut(Context context) {
        Intent trainIntent = new Intent();
        trainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        trainIntent.setAction(Intent.ACTION_VIEW);
        trainIntent.setComponent(new ComponentName(context.getPackageName(), TrainActionActivity.class.getName()));
        trainIntent.putExtra(TrainActionActivity.TRAIN_ID_KEY, TrainActionActivity.TRAIN_ID_VALUE);

        return new ShortcutInfo.Builder(context, TrainActionActivity.TRAIN_ID_VALUE)
                .setShortLabel(" ")
                .setLongLabel(context.getString(R.string.start_point))
                .setRank(1)
                .setIcon(Icon.createWithResource(context, R.drawable.start_point))
                .setIntent(trainIntent)
                .build();
    }

    public static ShortcutInfo createRailShortcut(Context context, int railNumber) {
        return createRailShortcut(context, railNumber, R.drawable.not_ready_rail, RailInfo.NOT_SET, null);
    }

    public static ShortcutInfo createRailShortcut(Context context, int railNumber, int iconResId, int rotation, Rect iconRect) {
        Intent railIntent = new Intent();
        railIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        railIntent.setAction(Intent.ACTION_VIEW);
        railIntent.setComponent(new ComponentName(context.getPackageName(), RailActionActivity.class.getName()));
        railIntent.putExtra(RailActionActivity.RAIL_ID_KEY, RailActionActivity.NEW_RAIL_ID_VALUE + railNumber);
        railIntent.putExtra(RailActionActivity.RAIL_ROTATION_KEY, rotation);


        PersistableBundle persistableBundle = null;
        if (iconRect != null) {
            persistableBundle = new PersistableBundle();
            persistableBundle.putIntArray(RAIL_RECT_KEY, new int[]{iconRect.left, iconRect.top});
            persistableBundle.putInt(RAIL_ROTATION_KEY, rotation);
        }

        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(context, RailActionActivity.NEW_RAIL_ID_VALUE + railNumber)
                .setShortLabel(" ")
                .setLongLabel(context.getString(R.string.add_rail))
                .setRank(2)
                .setIcon(Icon.createWithResource(context, iconResId))
                .setIntent(railIntent);

        if (persistableBundle != null) {
            builder.setExtras(persistableBundle);
        }

        return builder.build();
    }

    private static boolean isRailShortcut(String shortcutId) {
        return shortcutId.startsWith(RailActionActivity.NEW_RAIL_ID_VALUE);
    }

    public static int getRailNumber(String railId) {
        return Integer.parseInt(railId.substring(RailActionActivity.NEW_RAIL_ID_VALUE.length()));
    }

    public static int getNextRailNumber(ShortcutManager shortcutManager) {
        List<ShortcutInfo> shortcuts = shortcutManager.getPinnedShortcuts();

        int newRailId = -1;

        for (ShortcutInfo shortcutInfo : shortcuts) {
            String id = shortcutInfo.getId();
            if (isRailShortcut(id)) {
                int railId = getRailNumber(id);
                if (railId > newRailId) {
                    newRailId = railId;
                }
            }
        }

        newRailId++;

        return newRailId;
    }

    public static List<RailInfo> getRails(ShortcutManager shortcutManager) {
        List<ShortcutInfo> shortcuts = shortcutManager.getPinnedShortcuts();

        List<RailInfo> rails = new ArrayList<>();
        for (ShortcutInfo shortcutInfo : shortcuts) {
            String id = shortcutInfo.getId();
            if (isRailShortcut(id)) {
                PersistableBundle extras = shortcutInfo.getExtras();
                if (extras != null) {
                    int[] posArray = extras.getIntArray(RailActionActivity.RAIL_RECT_KEY);
                    int rotation = extras.getInt(RailActionActivity.RAIL_ROTATION_KEY);
                    assert posArray != null;
                    rails.add(new RailInfo(posArray[0], posArray[1], rotation));
                }
            }
        }

        return rails;
    }
}
