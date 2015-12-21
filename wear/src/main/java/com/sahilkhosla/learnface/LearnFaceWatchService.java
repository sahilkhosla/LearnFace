package com.sahilkhosla.learnface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
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

        protected Time mTime;

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

            mTime = new Time();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            System.out.println("OnDraw....");

            // Draw the background.
            canvas.drawRect(bounds, mBackgroundPaint);

            mTime.setToNow();

            boolean mShouldDrawColon = (System.currentTimeMillis() % 1000) < 500;

            String hourString = String.format("%02d", mTime.hour);
            String minString = String.format("%02d", mTime.minute);
            String colon = ":";

            float hourWidth = mTextPaint.measureText(hourString);
            float minWidth = mTextPaint.measureText(minString);
            float colonWidth = mTextPaint.measureText(colon);

            float timeXOffset = (hourWidth + colonWidth + minWidth) / 2;

            float x = bounds.centerX() - timeXOffset;
            float y = bounds.centerY() - mTextYOffset;

            canvas.drawText(hourString, x, y, mTextPaint);
            x += hourWidth;

            // draw colon when in ambient mode or the mShouldDrawColon is true.
            if (isInAmbientMode() || mShouldDrawColon) {
                canvas.drawText(":", x, y, mTextPaint);
            }

            x += colonWidth;
            canvas.drawText(minString, x, y, mTextPaint);

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

            // remove the messages which are already in the queue.
            mUpdateTimeHandler.removeMessages(0);

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

        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                // when the handler is active, call invalidate() to make the screen redraw.
                invalidate();

                // only visible and interaction mode, we active the handler
                // AmbientMode use another time.
                if (isVisible() && !isInAmbientMode()) {

                    // run again in next 500 milliseconds
                    // the first parameter is message code, we don't use it here, so put 0.
                    mUpdateTimeHandler.sendEmptyMessageDelayed(0, 500);
                }
            }
        };

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            // remove the messages which are already in the queue.
            mUpdateTimeHandler.removeMessages(0);

            if (isVisible() && !isInAmbientMode()) {
                // send the instant message into the queue.
                mUpdateTimeHandler.sendEmptyMessage(0);
            }
        }

        public void onTimeTick() {
            super.onTimeTick();

            // the event is only invoked once a minute when the wear is in ambient mode
            invalidate();
        }



    }

}
