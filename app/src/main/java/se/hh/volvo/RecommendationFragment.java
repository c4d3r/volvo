package se.hh.volvo;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import se.hh.volvo.data.RoadContract;
import se.hh.volvo.util.CSVReader;

/**
 * Created by Maxim on 20/03/2015.
 */
public class RecommendationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView txtRecSpeed;
    private TextView txtCurrentSpeed;
    private TextView txtDistance;

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

    private double distanceTraveled = 0.0;
    private double maxDistance = 5800.00;

    private double currentVelocity = 10; // 1m/s
    private double newVelocity = 10;
    private double distToNewVelocity = 0;

    private double currentExpFuel = 0;
    private double currentLat = 57.0;
    private double currentLon = 12.0;
    private double heading = 45.0;
    private double distToCP;

    private int countIterations = 0;

    private static final double FREQUENCY = 100.0;

    private LinkedList<Double[]> criticalPoints = new LinkedList<Double[]>();
    private Double[] currentCriticalPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);

        txtCurrentSpeed = (TextView)view.findViewById(R.id.txtCurrentSpeed);
        txtRecSpeed = (TextView)view.findViewById(R.id.txtRecommendedSpeed);
        txtDistance = (TextView)view.findViewById(R.id.txtDistance);

        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        criticalPoints.add(new Double[]{57.00459, 12.00459, 90.0});
        criticalPoints.add(new Double[]{57.01191, 12.01191, 70.0});
        criticalPoints.add(new Double[]{57.02065, 12.02065, 70.0});
        criticalPoints.add(new Double[]{57.02940, 12.02940, 90.0});
        criticalPoints.add(new Double[]{57.03496, 12.03496, 90.0});

        currentCriticalPoint = criticalPoints.poll();

        //heading: 45 degrees
        new Thread(new Runnable() {
            @Override
            public void run() {
                //0 seconds
                while(distanceTraveled <= maxDistance)
                {
                    try {
                        Thread.sleep(Double.valueOf(FREQUENCY).longValue());

                        double deltaDistance = (currentVelocity * FREQUENCY / 1000.0);
                        distanceTraveled += deltaDistance;

                        Double[] newPos = addMeterToGPS(currentLat, currentLon, deltaDistance, heading);
                        currentLat = newPos[0];
                        currentLon = newPos[1];

                        distToCP = gpsToMeter(currentLat, currentLon, currentCriticalPoint[0],  currentCriticalPoint[1]);
                        if(distToCP <= 50) {
                            Log.d(TAG, "CHANGE point");

                            //go to summary
                            if (!(criticalPoints.size() > 0)) {
                                break;
                            }

                            currentCriticalPoint = criticalPoints.poll();
                            //recalculate because update distance to current critical point to use in linear interpolation
                            distToCP = gpsToMeter(currentLat, currentLon, currentCriticalPoint[0],  currentCriticalPoint[1]);
                        }

                        currentVelocity += (currentCriticalPoint[2] / 3.6 - currentVelocity) / (distToCP / (currentVelocity * 0.5 + currentCriticalPoint[2] / 3.6 * 0.5)) / 10;
                        Log.d(TAG, Double.toString(currentVelocity));

                        countIterations++;

                        //update UI
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(countIterations % 10 == 0) {
                                    txtCurrentSpeed.setText(Integer.toString((int)Math.round(currentVelocity * 3.6)));
                                    txtRecSpeed.setText(Integer.toString((int)Math.round(currentCriticalPoint[2])));
                                    txtDistance.setText(Integer.toString((int)Math.round(distToCP)) + "m");
                                }
                            }
                        });

                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                sendSummary();
            }
        }).start();

    }

    public void sendSummary()
    {
        //Show summary, looping done
        Bundle args = new Bundle();
        double time = (countIterations * 0.1 - 232) / 60;
        args.putDouble("DISTANCE", maxDistance / 1000);
        args.putDouble("TIME", time);

        if(time > 0) {
            args.putDouble("FUEL", -3.0);
        } else {
            args.putDouble("FUEL", 3.0);
        }
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    public Double[] addMeterToGPS(double lat, double lon, double distance, double heading) {
        double headingRad = heading / 180 * Math.PI;
        double R = 6371000;
        double dLat = deg2rad(lat);
        double dLon = deg2rad(lon);

        double ddLat = distance * Math.sin(headingRad);
        double ddLon = distance * Math.cos(headingRad);

        double latOut = (dLat + ddLat / R) / Math.PI * 180;
        double lonOut = (dLon + ddLon / R) / Math.PI * 180;

        return new Double[] { latOut, lonOut };
    }

    /*public double gpsToMeter(double lat1, double lat2, double lon1, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        double miles = dist * 60 * 1.1515;
        double kms = miles * 1.609344;
        return kms / 1000;
    }*/

    private double gpsToMeter(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat =  deg2rad(lat2 - lat1);
        double dLon = deg2rad(lon2 - lon1);

        lat1 = deg2rad(lat1);
        lat2 = deg2rad(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;

    }

    public double deg2rad(double degrees) {
        return degrees * (Math.PI / 180);
    }

    public double rad2deg(double radians) {
        return radians * (180 / Math.PI);
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