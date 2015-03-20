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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import se.hh.volvo.data.RoadContract;
import se.hh.volvo.util.CSVReader;

/**
 * Created by Maxim on 20/03/2015.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button btnPlan;
    private Button btnSettings;
    private Button btnSummary;

    File file = null;

    private static final int ROAD_LOADER = 0;
    private static final String[] ROAD_COLUMNS = {
            RoadContract.RoadEntry.TABLE_NAME + "." + RoadContract.RoadEntry._ID,
            RoadContract.RoadEntry.COLUMN_LAT,
            RoadContract.RoadEntry.COLUMN_LON,
            RoadContract.RoadEntry.COLUMN_SLOPE,
            RoadContract.RoadEntry.COLUMN_EXP_TIME,
            RoadContract.RoadEntry.COLUMN_EXP_FUEL,
            RoadContract.RoadEntry.COLUMN_SPEED
    };

    public static final int COL_ROAD_ID = 0;
    public static final int COL_ROAD_LAT = 1;
    public static final int COL_ROAD_LON = 2;
    public static final int COL_ROAD_SLOPE = 3;
    public static final int COL_ROAD_EXP_TIME = 4;
    public static final int COL_ROAD_EXP_FUEL = 5;
    public static final int COL_ROAD_SPEED = 6;

    private Context context;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(ROAD_LOADER, null, this);
        loadData();
        super.onActivityCreated(savedInstanceState);
    }

    public void loadData()
    {
        Vector<ContentValues> cvVector = new Vector<>();

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SimData.csv");

        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            String [] nextLine;
            try {
                while ((nextLine = reader.readNext()) != null) {

                    // nextLine[] is an array of values from the line

                    String lat = nextLine[0];
                    String lon = nextLine[1];
                    String slope = nextLine[2];
                    String expTime = nextLine[3];
                    String expFuel = nextLine[4];
                    String speed = nextLine[5];

                    ContentValues roadValue = new ContentValues();
                    roadValue.put(RoadContract.RoadEntry.COLUMN_LAT, lat);
                    roadValue.put(RoadContract.RoadEntry.COLUMN_LON, lon);
                    roadValue.put(RoadContract.RoadEntry.COLUMN_SLOPE, slope);
                    roadValue.put(RoadContract.RoadEntry.COLUMN_EXP_TIME, expTime);
                    roadValue.put(RoadContract.RoadEntry.COLUMN_EXP_FUEL, expFuel);
                    roadValue.put(RoadContract.RoadEntry.COLUMN_SPEED, speed);

                    cvVector.add(roadValue);
                }

                if(cvVector.size() > 0) {
                    context.getContentResolver().delete(RoadContract.RoadEntry.CONTENT_URI, null, null);

                    ContentValues[] cvArray = new ContentValues[cvVector.size()];
                    cvVector.toArray(cvArray);
                    int rowsInserted = context.getContentResolver().bulkInsert(RoadContract.RoadEntry.CONTENT_URI, cvArray);
                    Log.v(TAG, "Inserted " + rowsInserted + " rows of topic data");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                context,
                RoadContract.RoadEntry.CONTENT_URI,
                ROAD_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
