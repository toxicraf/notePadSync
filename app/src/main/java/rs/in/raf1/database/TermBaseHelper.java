package rs.in.raf1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import rs.in.raf1.Term;
import rs.in.raf1.database.TermDbSchema.TermTable;

public class TermBaseHelper extends SQLiteOpenHelper  {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "glossary.db";

    public TermBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

   public boolean ifExists(SQLiteDatabase db) {
       Cursor c = db.rawQuery("select * from terms", null, null);
       if (c.getCount() == 0) {
           return false;
       }
       return true;
   }

    @Override
    public void onCreate(SQLiteDatabase db)  {

        db.execSQL("create table " + TermTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TermTable.Cols.UUID + ", " +
                TermTable.Cols.CREATED + ", " +
                TermTable.Cols.UPDATED + ", " +
                TermTable.Cols.FLAG + ", " +
                TermTable.Cols.ENG + ", " +
                TermTable.Cols.SRB + ", " +
                TermTable.Cols.DSC  +
                ")"
        );
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from terms");
    }


    public void populate(List<Term> termListDb) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransaction();
        //database.execSQL("delete from terms");

        for (Term t : termListDb) {
            ContentValues values = new ContentValues();

            values.put("uuid", t.getId().toString());
            values.put("termUpdated", t.getUpdated());
            values.put("termEnglish", t.getEnglish());
            values.put("termSerbian", t.getSerbian());
            values.put("termDescription", t.getDescription());

            database.insert("terms", null, values);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
