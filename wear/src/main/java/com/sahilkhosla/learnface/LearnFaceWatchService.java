package com.sahilkhosla.learnface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

public class LearnFaceWatchService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        Paint mTextPaint;
        Paint mBackgroundPaint;
        Float mTextXOffset;
        Float mTextYOffset;
        boolean mIsRound;
        private int mBackgroundColor = Color.WHITE;

        @Override
        public void onCreate(SurfaceHolder holder) {
            System.out.println("OnCreate....");
            super.onCreate(holder);

            // Create the Paint for later use - text
            mTextPaint = new Paint();
            mTextPaint.setTextSize(40);
            mTextPaint.setColor(Color.RED);
            mTextPaint.setAntiAlias(true);

            // Create the Paint for later use - background
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(mBackgroundColor);

            // In order to make text in the center, we need adjust its position
            mTextXOffset = mTextPaint.measureText("12:00") / 2;
            mTextYOffset = (mTextPaint.ascent() + mTextPaint.descent()) / 2;

            setWatchFaceStyle(new WatchFaceStyle.Builder(LearnFaceWatchService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            System.out.println("OnDraw....");

            // Draw the background.
            canvas.drawRect(bounds, mBackgroundPaint);

            canvas.drawText("12:00",
                    bounds.centerX() - mTextXOffset,
                    bounds.centerY() - mTextYOffset,
                    mTextPaint);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            System.out.println("OnAppyWindowInsets....");
            super.onApplyWindowInsets(insets);
            mIsRound = insets.isRound();
            if (mIsRound){
                mTextPaint.setTextSize(30);
            } else{
                mTextPaint.setTextSize(25);
            }
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            System.out.println("OnAmbientModeChanged....");
            super.onAmbientModeChanged(inAmbientMode);
            // when Ambient Mode changes, we changes the color of the background paint.
            if (inAmbientMode){
                mBackgroundPaint.setColor(Color.BLACK);
                mTextPaint.setColor(Color.WHITE);
                mTextPaint.setAntiAlias(false);
                //mWeatherConditionDrawable = mGrayWeatherConditionDrawable;
            }
            else {
                mBackgroundPaint.setColor(mBackgroundColor);
                mTextPaint.setColor(Color.RED);
                mTextPaint.setAntiAlias(true);
                //mWeatherConditionDrawable = mColorWeatherConditionDrawable;
            }

            //Call invalidate() to cause the onDraw is invoked.
            invalidate();
        }


    }

}
