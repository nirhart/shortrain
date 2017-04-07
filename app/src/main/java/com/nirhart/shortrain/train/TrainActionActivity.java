package com.nirhart.shortrain.train;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nirhart.shortrain.R;
import com.nirhart.shortrain.path.PathParser;
import com.nirhart.shortrain.path.PathPoint;
import com.nirhart.shortrain.path.TrainPath;
import com.nirhart.shortrain.rail.RailInfo;
import com.nirhart.shortrain.utils.ShortcutsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainActionActivity extends Activity {

    final public static String TRAIN_ID_KEY = "id";
    final public static String TRAIN_ID_VALUE = "new_train";

    final private static int TRAIN_FRACTION = 14; // The lowest this number, the faster the train will go
    private static final float START_ROTATION_FACTOR = 0.25f; // The lowest this number, the closer the train will start rotate next to a rotation
    private static final long TIME_BETWEEN_CAR_AND_ENGINE = 1000;
    private static final int NUMBER_OF_CARS = 2;
    final private List<TrainView> carsView = new ArrayList<>();
    private TrainView engineView;
    private FrameLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        overridePendingTransition(0, 0);

        final List<RailInfo> rails = ShortcutsUtils.getRails(getSystemService(ShortcutManager.class));

        final Rect trainRect = getIntent().getSourceBounds();
        addTrainToScreen(trainRect);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        PathParser pathParser = new PathParser(width, height);

        TrainPath path = null;
        try {
            path = pathParser.parse(trainRect.left, trainRect.top, trainRect, rails);
        } catch (ArrayIndexOutOfBoundsException e) {
            // If someone clicks the "set start point" instead of dragging it to screen, the tileRect will be larger
            // the cols will not be calculated correctly and an ArrayIndexOutOfBoundsException will be thrown in here
            Toast.makeText(this, R.string.starting_point_explanation, Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        List<Animator> engineAnimators = createTrainAnimation(engineView, path);
        final List<AnimatorSet> carsAnimatorSets = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_CARS; i++) {
            final TrainView carView = carsView.get(i);
            List<Animator> carAnimators = createTrainAnimation(carView, path);
            carView.setVisibility(View.INVISIBLE);
            final AnimatorSet carSet = new AnimatorSet();
            carSet.playSequentially(carAnimators);
            carSet.setStartDelay(TIME_BETWEEN_CAR_AND_ENGINE * (i + 1));
            carSet.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carView.setVisibility(View.VISIBLE);
                }
            }, TIME_BETWEEN_CAR_AND_ENGINE * (i + 1));

            carsAnimatorSets.add(carSet);
        }

        engineAnimators.get(engineAnimators.size() - 1).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fadeOutActivity();
            }
        });

        AnimatorSet engineSet = new AnimatorSet();
        engineSet.playSequentially(engineAnimators);
        engineSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (AnimatorSet carSet : carsAnimatorSets) {
                    carSet.pause();
                }
            }
        });
        engineSet.start();
    }

    private List<Animator> createTrainAnimation(final TrainView trainView, TrainPath path) {
        List<Animator> animators = new ArrayList<>();
        List<PathPoint> pathPoints = path.getPath();

        for (int i = 1; i < pathPoints.size(); i++) {
            PathPoint lastPathPoint = pathPoints.get(i - 1);
            PathPoint pathPoint = pathPoints.get(i);

            final TrainDirection animationDirection = pathPoints.get(i).getDirection();
            final boolean isHorizontal = animationDirection.isHorizontal();

            final int animationLength = getAnimationLength(lastPathPoint, pathPoint, isHorizontal);
            long animationDuration = getAnimationDuration(animationLength);

            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, animationLength);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(animationDuration);
            final AtomicInteger lastUpdate = new AtomicInteger(0);
            final float rotationStartFraction = 1f - ((float) trainView.getTrainHeight() * START_ROTATION_FACTOR / Math.abs(animationLength));

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int update = (int) animation.getAnimatedValue();
                    if (isHorizontal) {
                        trainView.setX(trainView.getX() + (update - lastUpdate.get()));
                        if (animationDirection.isTurningUp()) {
                            animateOneTrainPath(animation, trainView, 270, rotationStartFraction);
                        } else if (animationDirection.isTurningDown()) {
                            animateOneTrainPath(animation, trainView, 90, rotationStartFraction);
                        } else {
                            animateOneTrainPath(animation, trainView, -1, rotationStartFraction);
                        }
                    } else {
                        trainView.setY(trainView.getY() + (update - lastUpdate.get()));
                        if (animationDirection.isTurningLeft()) {
                            animateOneTrainPath(animation, trainView, 180, rotationStartFraction);
                        } else if (animationDirection.isTurningRight()) {
                            animateOneTrainPath(animation, trainView, 0, rotationStartFraction);
                        } else {
                            animateOneTrainPath(animation, trainView, -1, rotationStartFraction);
                        }
                    }
                    lastUpdate.set(update);
                }
            });
            animators.add(valueAnimator);
        }
        return animators;
    }

    private void animateOneTrainPath(ValueAnimator animation, TrainView trainView, int desiredRotation, float rotationStartFraction) {
        if (animation.getAnimatedFraction() <= 1 - rotationStartFraction) {
            float fraction = 0.5f + (animation.getAnimatedFraction() / (2 * (1 - rotationStartFraction)));
            trainView.finishAnimation(fraction);
        } else if (desiredRotation != -1 && animation.getAnimatedFraction() >= rotationStartFraction) {
            float fraction = (animation.getAnimatedFraction() - rotationStartFraction) / (2 * (1 - rotationStartFraction));
            trainView.setRotation(desiredRotation, fraction);
        } else {
            trainView.finishAnimation(1f);
        }
    }

    private long getAnimationDuration(int animationLength) {
        long animationDuration;
        float oneDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        animationDuration = (long) (Math.abs(animationLength / oneDp) * TRAIN_FRACTION);
        return animationDuration;
    }

    private int getAnimationLength(PathPoint lastPathPoint, PathPoint pathPoint, boolean isHorizontal) {
        int animationLength;

        if (isHorizontal) {
            animationLength = pathPoint.getLeft() - lastPathPoint.getLeft();
        } else {
            animationLength = pathPoint.getTop() - lastPathPoint.getTop();
        }
        return animationLength;
    }

    private void addTrainToScreen(Rect trainRect) {
        rootView = new FrameLayout(this);
        engineView = addTrain(trainRect, TrainView.ENGINE, rootView);
        for (int i = 0; i < NUMBER_OF_CARS; i++) {
            carsView.add(addTrain(trainRect, TrainView.CAR, rootView));
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeOutActivity();
            }
        });

        float iconToWidthFactor = 0.34f;
        ImageView startRoof = new ImageView(this);
        startRoof.setImageResource(R.drawable.long_start_point_roof);
        int trainRectSize = (int) (trainRect.width() * (1f - iconToWidthFactor));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(trainRectSize, trainRectSize);
        startRoof.setX(trainRect.left + trainRect.width() * iconToWidthFactor / 2 - 15);
        startRoof.setY(trainRect.top + trainRect.width() * iconToWidthFactor / 2);
        rootView.addView(startRoof, lp);

        startRoof.setAlpha(0f);
        startRoof.animate()
                .alpha(1f)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(500)
                .start();

        setContentView(rootView);
    }

    private void fadeOutActivity() {
        rootView.animate()
                .alpha(0f)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finish();
                    }
                })
                .start();
    }

    @NonNull
    private TrainView addTrain(Rect trainRect, int trainType, FrameLayout frameLayout) {
        final int size = Math.min(trainRect.width(), trainRect.height());
        final TrainView trainView = new TrainView(this);
        Resources res = getResources();
        int trainWidth = (int) (trainRect.width() * 0.70);
        int trainHeight = (int) res.getFraction(R.fraction.rail_height_width_fraction, trainWidth, trainWidth);
        trainView.setType(trainType);
        trainView.setTrainHeight(trainHeight);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(trainWidth, trainHeight);
        frameLayout.addView(trainView, lp);
        trainView.setX(trainRect.left + (size - trainWidth) / 2);
        trainView.setY(trainRect.top + (size - trainHeight) / 2);
        return trainView;
    }

    @Override
    public void onBackPressed() {
        fadeOutActivity();
    }
}
