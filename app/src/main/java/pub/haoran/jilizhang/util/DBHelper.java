package pub.haoran.jilizhang.util;

/**
 * Created by zhaohaoran on 6/10/15.
 */
import android.content.ContentValues;
        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteDatabase.CursorFactory;
        import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TBNAME = "records";
    public static final String ID = "_id";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";
    public static final String IFFROMME = "iffromme";


    public DBHelper(Context context, String name,
                    CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.getWritableDatabase();
    }

    /**
     * should be invoke when you never use DBhelper
     * To release the database and etc.
     */
    public void Close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TBNAME + " (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PERSON+ " TEXT not null, " +
                DATE+" TEXT not null, " +
                AMOUNT+" TEXT not null, " +
                IFFROMME+" Integer)");
    }

    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TBNAME);
        onCreate(db);
    }

    public void addRecord(String person, String date, String amount, int iffromme) {
        ContentValues values = new ContentValues();
        //values.put(DBHelper.PERSON, person);
        values.put(DBHelper.PERSON, person);
        values.put(DBHelper.DATE, date);
        values.put(DBHelper.AMOUNT, amount);
        values.put(DBHelper.IFFROMME, iffromme);

        this.getWritableDatabase().insert(
                DBHelper.TBNAME, DBHelper.ID, values);
    }




    public void delRecord(int id) {
        this.getWritableDatabase().delete(
                DBHelper.TBNAME, this.ID + " = " + id, null);
    }

    public void delAllRecords() {
        this.getWritableDatabase().delete(
                DBHelper.TBNAME, null, null);
    }
}