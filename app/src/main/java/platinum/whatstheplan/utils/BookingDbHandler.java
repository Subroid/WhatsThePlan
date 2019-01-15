package platinum.whatstheplan.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.models.Event;

public class BookingDbHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Bookings.sqLiteDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_BOOKINGS_PARTIES = "BookingsParties";
    private static final String TABLE_BOOKINGS_FOODS = "BookingsFoods";
    private static final String TABLE_BOOKINGS_SPORTS = "BookingsSports";
    private static final String TABLE_BOOKINGS_OPENEVENTS = "BookingsOpenEvents";

    private static final String COLUMN_EVENTNO = "EventNum";
    private static final String COLUMN_EVENTID = "EventId";
    private static final String COLUMN_EVENTNAME = "EventName";
    private static final String COLUMN_EVENTDATE = "EventDate";
    private static final String COLUMN_EVENTTIME = "EventTime";
    private static final String COLUMN_EVENTIMAGE = "EventImage";
    private static final String COLUMN_VENUENAME = "VenueName";
    private static final String COLUMN_VENUEADDRESS = "VenueAddress";
    private static final String COLUMN_VENUEIMAGE = "VenueImage";




    public BookingDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_BOOKINGS_PARTIES = "CREATE TABLE " +
                TABLE_BOOKINGS_PARTIES +
                "(" + COLUMN_EVENTNO + " INTEGER PRIMARY KEY," +
                COLUMN_EVENTID + " TEXT," +
                COLUMN_EVENTNAME + " TEXT," +
                COLUMN_EVENTDATE + " TEXT," +
                COLUMN_EVENTTIME + " TEXT," +
                COLUMN_EVENTIMAGE + " TEXT," +
                COLUMN_VENUENAME + " TEXT," +
                COLUMN_VENUEADDRESS + " TEXT," +
                COLUMN_VENUEIMAGE + " TEXT" +")";

        sqLiteDatabase.execSQL(CREATE_TABLE_BOOKINGS_PARTIES);

        String CREATE_TABLE_BOOKINGS_FOODS = "CREATE TABLE " +
                TABLE_BOOKINGS_FOODS +
                "(" + COLUMN_EVENTNO + " INTEGER PRIMARY KEY," +
                COLUMN_EVENTID + " TEXT," +
                COLUMN_EVENTNAME + " TEXT," +
                COLUMN_EVENTDATE + " TEXT," +
                COLUMN_EVENTTIME + " TEXT," +
                COLUMN_EVENTIMAGE + " TEXT," +
                COLUMN_VENUENAME + " TEXT," +
                COLUMN_VENUEADDRESS + " TEXT," +
                COLUMN_VENUEIMAGE + " TEXT" +")";

        sqLiteDatabase.execSQL(CREATE_TABLE_BOOKINGS_FOODS);

        String CREATE_TABLE_BOOKINGS_OPENEVENTS = "CREATE TABLE " +
                TABLE_BOOKINGS_OPENEVENTS +
                "(" + COLUMN_EVENTNO + " INTEGER PRIMARY KEY," +
                COLUMN_EVENTID + " TEXT," +
                COLUMN_EVENTNAME + " TEXT," +
                COLUMN_EVENTDATE + " TEXT," +
                COLUMN_EVENTTIME + " TEXT," +
                COLUMN_EVENTIMAGE + " TEXT," +
                COLUMN_VENUENAME + " TEXT," +
                COLUMN_VENUEADDRESS + " TEXT," +
                COLUMN_VENUEIMAGE + " TEXT" +")";

        sqLiteDatabase.execSQL(CREATE_TABLE_BOOKINGS_OPENEVENTS);

        String CREATE_TABLE_BOOKINGS_SPORTS = "CREATE TABLE " +
                TABLE_BOOKINGS_SPORTS +
                "(" + COLUMN_EVENTNO + " INTEGER PRIMARY KEY," +
                COLUMN_EVENTID + " TEXT," +
                COLUMN_EVENTNAME + " TEXT," +
                COLUMN_EVENTDATE + " TEXT," +
                COLUMN_EVENTTIME + " TEXT," +
                COLUMN_EVENTIMAGE + " TEXT," +
                COLUMN_VENUENAME + " TEXT," +
                COLUMN_VENUEADDRESS + " TEXT," +
                COLUMN_VENUEIMAGE + " TEXT" +")";

        sqLiteDatabase.execSQL(CREATE_TABLE_BOOKINGS_SPORTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_PARTIES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_FOODS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_SPORTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_OPENEVENTS);
    }

    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        switch (event.getEvent_type()) {
            case "Parties" :
                db.insert(TABLE_BOOKINGS_PARTIES, null, getContentValues (event));
                break;
            case "Restaurants" :
                db.insert(TABLE_BOOKINGS_FOODS, null, getContentValues (event));
                break;
            case "Sports" :
                db.insert(TABLE_BOOKINGS_SPORTS, null, getContentValues (event));
                break;
            case "Opne Events" :
                db.insert(TABLE_BOOKINGS_OPENEVENTS, null, getContentValues (event));
                break;
        }

        db.close();

    }

        private ContentValues getContentValues(Event event) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_EVENTID, event.getEvent_id());
            contentValues.put(COLUMN_EVENTNAME, event.getEvent_name());
            contentValues.put(COLUMN_EVENTDATE, event.getEvent_date());
            contentValues.put(COLUMN_EVENTTIME, event.getEvent_time());
            contentValues.put(COLUMN_EVENTIMAGE, event.getEvent_image());
            contentValues.put(COLUMN_VENUENAME, event.getVenue_name());
            contentValues.put(COLUMN_VENUEADDRESS, event.getVenue_address());
            contentValues.put(COLUMN_VENUEIMAGE, event.getVenue_image());

            return contentValues;
        }

    public void removeEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        switch (event.getEvent_type()) {
            case "Parties" :
                db.execSQL("DELETE FROM " + TABLE_BOOKINGS_PARTIES + " WHERE " + COLUMN_EVENTID + "= '" + event.getEvent_id() + "'");
                break;
            case "Restaurants" :
                db.execSQL("DELETE FROM " + TABLE_BOOKINGS_FOODS + " WHERE " + COLUMN_EVENTID + "= '" + event.getEvent_id() + "'");
                break;
            case "Sports" :
                db.execSQL("DELETE FROM " + TABLE_BOOKINGS_SPORTS + " WHERE " + COLUMN_EVENTID + "= '" + event.getEvent_id() + "'");
                break;
            case "Opne Events" :
                db.execSQL("DELETE FROM " + TABLE_BOOKINGS_OPENEVENTS + " WHERE " + COLUMN_EVENTID + "= '" + event.getEvent_id() + "'");
                break;
        }

        db.close();
    }

    public List<Event> findEvents(String date) {
        List<Event> eventList = new ArrayList<>();

        findEventsFromTable (date, TABLE_BOOKINGS_PARTIES, eventList);
        findEventsFromTable (date, TABLE_BOOKINGS_FOODS, eventList);
        findEventsFromTable (date, TABLE_BOOKINGS_SPORTS, eventList);
        findEventsFromTable (date, TABLE_BOOKINGS_OPENEVENTS, eventList);

       return eventList;

    }

        private List<Event> findEventsFromTable(String date, String tableName, List<Event> eventList) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + tableName + " WHERE " + COLUMN_EVENTDATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{date});

            while (cursor.moveToNext()) {
                String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTID));
                String eventName = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTNAME));
                String eventDate = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTDATE));
                String eventTime = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTTIME));
                String eventImage = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTIMAGE));
                String venueName = cursor.getString(cursor.getColumnIndex(COLUMN_VENUENAME));
                String venueAdderess = cursor.getString(cursor.getColumnIndex(COLUMN_VENUEADDRESS));
                String venueImage = cursor.getString(cursor.getColumnIndex(COLUMN_VENUEIMAGE));

                Event event = new Event(eventName, null, null, venueName, null, venueAdderess, venueImage, eventDate, eventTime, eventId, eventImage, null, null, 0);
                eventList.add(event);
            }

            cursor.close();
            db.close();

            return eventList;
        }

}
