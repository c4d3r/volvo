package se.hh.volvo.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import se.hh.volvo.R;

/**
 * Created by MaytAram on 2015-03-20.
 */
public class RecommendActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recommendation);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        View decorView = getWindow().getDecorView();
        super.onWindowFocusChanged(hasFocus);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
