package net.haoranzhao.jilizhang.util;

/**
 * Created by zhaohaoran on 6/10/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = "DBHELPER";

    public static final String TBNAME = "records";
    public static final String ID = "_id";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";
    public static final String IFFROMME = "iffromme";

    public static final String EXPORT_SUCCESS = "SUCCESS";


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

    /**
     * @author: Haoran Zhao
     * added 4/5/16
     */
    public String exportCSV(SQLiteDatabase db, Context mContext){
        String infotext = "";
        Cursor c;

        try {
            c = db.rawQuery("select * from "+this.TBNAME, null);
            int rowcount = 0;
            int colcount = 0;
            //File outputDir = mContext.getFilesDir();
            File outputDir = new File(mContext.getFilesDir(), "exported");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            long time= System.currentTimeMillis();
            Date datetime = new Date(time);

            String filename = "TransactionExports-"+datetime+".csv";
            // the name of the file to export with
            File saveFile = new File(outputDir, filename);
            FileWriter fw = new FileWriter(saveFile);
            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();
                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {
                        bw.write(c.getColumnName(i) + ",");
                    } else {
                        bw.write(c.getColumnName(i));
                    }
                }
                bw.newLine();
                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);
                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                infotext= this.EXPORT_SUCCESS+":"+filename;
            }
        } catch (Exception ex) {
            if (db.isOpen()) {
                db.close();
                infotext=ex.getMessage().toString();
                Log.d(TAG,infotext);
            }
        } finally {
        }
        return infotext;

    }



}