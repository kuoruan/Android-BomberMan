package com.kuoruan.bomberman;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import com.kuoruan.bomberman.view.GameView;

/**
 * Created by Liao on 2016/5/1 0001.
 */
public class GameActivity extends Activity {

    private GameView mGameView = null;

    private DisplayMetrics mMetrics = new DisplayMetrics();
    private float mScreenDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context mContext = getApplicationContext();

        /**
         * Get the screen density that all pixel values will be based on.
         * This allows scaling of pixel values over different screen sizes.
         *
         * See: http://developer.android.com/reference/android/util/DisplayMetrics.html
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mScreenDensity = mMetrics.density;

        /**
         * There is only one stage / level in this example.
         * In a real game, the user's chosen stage / level should be
         * passed to this activity.
         */
        int stage = 0;

        Log.d("Tile Game Example", "Starting game at stage: " + stage);
        mGameView = new GameView(mContext, this, GameView.MULTI_PLAYER_STAGE, mScreenDensity);

        setContentView(mGameView);
    }
}
