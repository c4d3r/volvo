package se.hh.volvo.data;

/**
 * Created by Maxim on 20/03/2015.
 */
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Maxim on 17/01/2015.
 */
public class RoadContract
{
    // Is een naam voor de gehele contentprovider, gelijkaardig aan de relatie domein<->website. We gebruiken vaak de pakage name --> altijd unique
    public static final String CONTENT_AUTHORITY = "se.hh.volvo";

    // Gebruik CONTENT_AUTHORITY om de basis van alle URI's te maken
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ROAD = "roads";

    public static final class RoadEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROAD).build();
        //directory --> lijst van items
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ROAD;
        //single item
        public static final String CONTENT_TOPIC_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ROAD;

        public static final String TABLE_NAME = "road";

        /* TOPICS */
        public static final String COLUMN_LAT      = "lat";
        public static final String COLUMN_LON      = "lon";
        public static final String COLUMN_SLOPE    = "slope";
        public static final String COLUMN_EXP_TIME = "exp_time";
        public static final String COLUMN_EXP_FUEL = "exp_fuel";
        public static final String COLUMN_SPEED    = "speed";

        //minder plaatsen aware van de actual URI encoding
        public static Uri buildTopicUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id); //enkel indien men id als PK gebruikt
        }
        public static Uri buildEcologRoad(String topicSetting) {
            return CONTENT_URI.buildUpon().appendPath(topicSetting).build();
        }
    }
}
