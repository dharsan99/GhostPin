package opencv.ar.ashwin.ghostpin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashwin on 23/01/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "templateDataManager";

    // Contacts table name
    private static final String T_TEMPLATE_DATA = "t_template_data";

    // Contacts Table Columns names
    private static final String V_TEMPL_NAME = "templName";
  //  private static final String IMG_NAME = "imgName";
    private static final String V_IMG_DESC = "img_desc";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TEMPLATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + T_TEMPLATE_DATA + "("
                + V_TEMPL_NAME + " TEXT," + V_IMG_DESC + " TEXT"
                + ");";


        db.execSQL(CREATE_TEMPLATE_DATA_TABLE);
    }


    public void deleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(T_TEMPLATE_DATA,null,null);
        db.close();
        db = this.getReadableDatabase();
        Cursor cursor = db.query(T_TEMPLATE_DATA, new String[]{V_TEMPL_NAME,
                        V_IMG_DESC}, null,
                null, null, null, null, null);
        Log.i("DataHandler", "After adding image cursor size::" + cursor.getCount());
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + T_TEMPLATE_DATA);

        // Create tables again
        onCreate(db);
    }

    public void addImgData(Template img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(V_TEMPL_NAME, img.getTemplName());
        values.put(V_IMG_DESC, img.getDesc());
        // Inserting Row
        long rowid = db.insert(T_TEMPLATE_DATA, null, values);
        Log.i("db rows","row id is::"+rowid);
        db.close();

        db = this.getReadableDatabase();
        Cursor cursor = db.query(T_TEMPLATE_DATA, new String[]{V_TEMPL_NAME,
                        V_IMG_DESC}, null,
                null, null, null, null, null);
        Log.i("DataHandler", "After adding image cursor size::"+cursor.getCount());

        db.close(); // Closing database connection
    }

    Map<String,Template> getTemplates() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(T_TEMPLATE_DATA, new String[]{V_TEMPL_NAME,
                        V_IMG_DESC}, null,
                null, null, null, null, null);

        Map<String,Template> map = new HashMap<String,Template>();
        Log.i("DataHandler", "cursor size::"+cursor.getCount());
        while (cursor.moveToNext()){
                map.put(cursor.getString(0),new Template(cursor.getString(0),cursor.getString(1)));
            }


        cursor.close();
        db.close();

        return map;
    }


}
