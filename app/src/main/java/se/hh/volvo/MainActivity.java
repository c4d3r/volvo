package se.hh.volvo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


    private Button btnPlan;
    private Button btnSettings;
    private Button btnSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnPlan = (Button)findViewById(R.id.btn_plan);
        btnSettings = (Button)findViewById(R.id.btn_settings);
        btnSummary = (Button)findViewById(R.id.btn_summary);

        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlanActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            }
        });

        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
            }
        });
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
