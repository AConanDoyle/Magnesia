package climbberlin.de.mapapps.climbup.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "favoriteSpots";

    // Contacts table name
    private static final String TABLE_SPOTS = "spots";

    // Spots Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LONG = "long";
    private static final String KEY_USE = "use";
    private static final String KEY_TYP = "typ";
    private static final String KEY_KROUTEN = "krouten";
    private static final String KEY_BROUTEN = "brouten";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_OPENING = "opening";
    private static final String KEY_PRICE = "price";
    private static final String KEY_ADRESS = "adress";
    private static final String KEY_WEB = "web";

    public DBHandler (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "+ TABLE_SPOTS + "("
        + KEY_ID + " INTEGER PRIMARY KEY,"+ KEY_NAME + " TEXT," + KEY_LAT + " TEXT," + KEY_LONG + " TEXT,"
                + KEY_USE + " TEXT," + KEY_TYP + " TEXT," + KEY_KROUTEN +
                " TEXT," + KEY_BROUTEN + " TEXT," + KEY_MATERIAL + " TEXT," + KEY_OPENING + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_ADRESS + " TEXT," + KEY_WEB + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.close();
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPOTS);
        // Creating tables again
        onCreate(db);
    }

    // Adding new spot
    public void addSpot(Spots spots) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, spots.getName());
        values.put(KEY_LAT, spots.getLat());
        values.put(KEY_LONG, spots.getLong());
        values.put(KEY_USE, spots.getUse());
        values.put(KEY_TYP, spots.getTyp());
        values.put(KEY_KROUTEN, spots.getKrouten());
        values.put(KEY_BROUTEN, spots.getBrouten());
        values.put(KEY_MATERIAL, spots.getMaterial());
        values.put(KEY_OPENING, spots.getOpening());
        values.put(KEY_PRICE, spots.getPrice());
        values.put(KEY_ADRESS, spots.getAddress());
        values.put(KEY_WEB, spots.getWeb());

        // Inserting Row
        db.insert(TABLE_SPOTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting one spot
    public Spots getSpot (int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SPOTS, new String[]{ KEY_ID, KEY_NAME, KEY_LAT, KEY_LONG,
                        KEY_USE, KEY_TYP, KEY_KROUTEN, KEY_BROUTEN,
                        KEY_MATERIAL, KEY_OPENING, KEY_OPENING, KEY_ADRESS}, KEY_ID + " =?",
        new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Spots spots = new Spots(Integer.parseInt(cursor.getString(1)), cursor.getString(1),
                Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3)),
                cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7),
                cursor.getString(8),cursor.getString(9),cursor.getString(10), cursor.getString(11),
                cursor.getString(12));
        return spots;
    }

    // Getting All Spots
    public List<Spots> getAllSpots() {

        List<Spots> spotsList = new ArrayList<Spots>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_SPOTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Spots spots = new Spots();
                spots.setId(Integer.parseInt(cursor.getString(0)));
                spots.setName(cursor.getString(1));
                spots.setLat(Double.parseDouble(cursor.getString(2)));
                spots.setLong(Double.parseDouble(cursor.getString(3)));
                spots.setUse(cursor.getString(4));
                spots.setTyp(cursor.getString(5));
                spots.setKrouten(cursor.getString(6));
                spots.setBrouten(cursor.getString(7));
                spots.setMaterial(cursor.getString(8));
                spots.setOpening(cursor.getString(9));
                spots.setPrice(cursor.getString(10));
                spots.setAddress(cursor.getString(11));
                spots.setWeb(cursor.getString(12));

                // Adding contact to list
                spotsList.add(spots);
            } while (cursor.moveToNext());
        }

        // return contact list
        return spotsList;
    }

    // Getting spots Count
    public int getSpotsCount() {
        String countQuery = "SELECT * FROM " + TABLE_SPOTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    // Updating a spot
    public int updateSpot (Spots spots) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, spots.getName());
        values.put(KEY_LAT, spots.getLat());
        values.put(KEY_LONG, spots.getLong());
        values.put(KEY_USE, spots.getUse());
        values.put(KEY_TYP, spots.getTyp());
        values.put(KEY_KROUTEN, spots.getKrouten());
        values.put(KEY_BROUTEN, spots.getBrouten());
        values.put(KEY_MATERIAL, spots.getMaterial());
        values.put(KEY_OPENING, spots.getOpening());
        values.put(KEY_PRICE, spots.getPrice());
        values.put(KEY_ADRESS, spots.getAddress());
        values.put(KEY_WEB, spots.getWeb());

        // updating row
        return db.update(TABLE_SPOTS, values, KEY_ID + " = ?",
        new String[]{String.valueOf(spots.getId())});
    }

    // Deleting a spot
    public void deleteSpot(Spots spots) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SPOTS, KEY_ID + " = ?",
        new String[]{String.valueOf(spots.getId())});
        db.close();
    }

    // Deleting table
    public void dropDB (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPOTS);
        db.close();
    }
}