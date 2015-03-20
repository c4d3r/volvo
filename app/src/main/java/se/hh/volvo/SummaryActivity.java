package se.hh.volvo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SummaryActivity extends Activity {

    private TextView txtTime;
    private TextView txtFuel;
    private TextView txtDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_summary);

        txtTime = (TextView)findViewById(R.id.txtTime);
        txtFuel = (TextView)findViewById(R.id.txtFuel);
        txtDistance = (TextView)findViewById(R.id.txtDistance);

        Bundle args = getIntent().getExtras();
        if(args.containsKey("TIME") && args.containsKey("FUEL") && args.containsKey("DISTANCE"))
        {
            final double time = args.getDouble("TIME");
            final double fuel = args.getDouble("FUEL");
            final double distance = args.getDouble("DISTANCE");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtTime.setText(Integer.toString(Math.round((int)time)) + " min");
                    txtFuel.setText(Integer.toString(Math.round((int)fuel)) + "%");
                    txtDistance.setText(Double.toString(distance) + " km");
                }
            });
        }
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
