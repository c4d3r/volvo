package se.hh.volvo;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.content.CursorLoader;
import android.support.annotation.Nullable;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import se.hh.volvo.data.RoadContract;
import se.hh.volvo.util.CSVReader;

/**
 * Created by Maxim on 20/03/2015.
 */
public class MainFragment extends Fragment {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button btnPlan;
    private Button btnSettings;
    private Button btnSummary;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        btnPlan = (Button)view.findViewById(R.id.btn_plan);
        btnSettings = (Button)view.findViewById(R.id.btn_settings);
        btnSummary = (Button)view.findViewById(R.id.btn_summary);

        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PlanActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PreferenceActivity.class));
            }
        });

        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SummaryActivity.class));
            }
        });

        return view;

    }
}
