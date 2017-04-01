package com.nirhart.shortrain.train;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import com.nirhart.shortrain.R;

// No need for AppCompat when minSdkVersion is 25
@SuppressLint("AppCompatCustomView")
public class TrainView extends ImageView {

    public static final int ENGINE = 0;
    public static final int CAR = 1;
    private int currentRotation = 0;
    private float desiredRotation;
    private int trainHeight;

    public TrainView(Context context) {
        super(context);
    }

    public void setType(int type) {
        switch (type) {
            case ENGINE:
                setImageResource(R.drawable.train_engine);
                break;
            case CAR:
                setImageResource(R.drawable.cart);
                break;
        }
    }

    public void setRotation(float desiredRotation, float fraction) {
        this.desiredRotation = desiredRotation;
        if (Math.abs(desiredRotation - currentRotation) > 90) {
            if (currentRotation < desiredRotation) {
                currentRotation += 360;
            } else {
                currentRotation -= 360;
            }
        }
        float newRotation = currentRotation + (desiredRotation - currentRotation) * fraction;

        if (newRotation % 90 == 0) {
            this.currentRotation = (int) newRotation;
        }
        setRotation(newRotation);
    }

    public void finishAnimation(float fraction) {
        setRotation(desiredRotation, fraction);
    }

    public int getTrainHeight() {
        return this.trainHeight;
    }

    public void setTrainHeight(int trainHeight) {
        this.trainHeight = trainHeight;
    }
}
