package opencv.ar.ashwin.opcv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String Template_Data = "template_data";

    // Contacts Table Columns names
    private static final String TEMPL_NAME = "templName";
  //  private static final String IMG_NAME = "imgName";
    private static final String IMG_DESC = "img_desc";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEMPLATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS" + Template_Data + "("
                + TEMPL_NAME + " INTEGER PRIMARY KEY," + IMG_DESC + " TEXT"
                + ")";

        db.execSQL(CREATE_TEMPLATE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Template_Data);

        // Create tables again
        onCreate(db);
    }
    public void addImgData(Template img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TEMPL_NAME, img.getTemplName());
        values.put(IMG_DESC, img.getDesc());
        // Inserting Row
        db.insert(Template_Data, null, values);
        db.close(); // Closing database connection
    }

    Map<String,Template> getTemplates() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Template_Data, new String[]{TEMPL_NAME,
                        IMG_DESC}, null,
                null, null, null, null, null);

        Map<String,Template> map = new HashMap<String,Template>();
        while (cursor.moveToNext()){
                map.put(cursor.getString(0),new Template(cursor.getString(0),cursor.getString(1)));
            }


        cursor.close();
        db.close();

        return map;
    }

}
