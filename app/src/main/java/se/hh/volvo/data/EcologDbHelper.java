package se.hh.volvo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import se.hh.volvo.data.RoadContract.RoadEntry;

/**
 * Created by Maxim on 20/03/2015.
 */
public class EcologDbHelper extends SQLiteOpenHelper
{
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "ecolog.db";

    private static final String TAG = EcologDbHelper.class.getSimpleName();

    private static final String CREATE_TABLE =
            "CREATE TABLE " + RoadEntry.TABLE_NAME + " (" +
                    RoadEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RoadEntry.COLUMN_LAT + " DOUBLE NOT NULL," +
                    RoadEntry.COLUMN_LON + " DOUBLE NOT NULL," +
                    RoadEntry.COLUMN_SLOPE + " DOUBLE NOT NULL," +
                    RoadEntry.COLUMN_EXP_TIME + " DOUBLE," +
                    RoadEntry.COLUMN_EXP_FUEL + " DOUBLE NOT NULL," +
                    RoadEntry.COLUMN_SPEED + " INT" +
                    ");";

    public EcologDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Maak de databank aan adhv de query die we gedefinieert hebben
     * @param db
     */
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        Log.i(TAG, "Create table: " + RoadEntry.TABLE_NAME);
    }

    /**
     * Indien de versiecode niet meer overeenkomt is er een nieuwe databank update, dan updaten we alles
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(TAG, String.format("Upgrading from %d to %d", oldVersion, newVersion));
        db.execSQL("DROP IF EXISTS " + RoadEntry.TABLE_NAME);
        onCreate(db);
    }
}
