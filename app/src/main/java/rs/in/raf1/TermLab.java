package rs.in.raf1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import rs.in.raf1.database.TermBaseHelper;
import rs.in.raf1.database.TermCursorWrapper;
import rs.in.raf1.database.TermDbSchema;


import static rs.in.raf1.database.TermDbSchema.TermTable.Cols.*;

public class TermLab {

    private static TermLab sTermLab;
    private static Context mContext;
    private SQLiteDatabase mDatabase;

    public static TermLab get(Context context) {
        if(sTermLab == null) {
            sTermLab = new TermLab(context);
        }
        return sTermLab;
    }

    private TermLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TermBaseHelper(mContext).getWritableDatabase();
    }

    public void addTerm(Term t) {
        ContentValues values = getContentValues(t);
        mDatabase.insert(TermDbSchema.TermTable.NAME, null, values);
    }

    public List<Term>  getTerms(String filter) {
        List<Term> termsList = new ArrayList<>();
        TermCursorWrapper cursor;

        if (filter != "") {
            cursor = queryTerms("termEnglish like \"%" + filter + "%\"", null);
        } else {
            cursor = queryTerms(null, null);
        }

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                termsList.add(cursor.getTerm());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return termsList;
    }


    public Term getTerm(UUID id) {
        TermCursorWrapper cursor = queryTerms(
                TermDbSchema.TermTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTerm();
        } finally {
            cursor.close();
        }
    }
    public void removeTerm(UUID termId) {
        String uuidString = termId.toString();
        mDatabase.delete(TermDbSchema.TermTable.NAME,TermDbSchema.TermTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void deleteAll() {
        mDatabase.execSQL("delete from terms");
    }


    public void updateTerm(Term term) {
        String uuidString = term.getId().toString();
        ContentValues values = getContentValues(term);
        values.put("termUpdated", getDateTime());
        mDatabase.update(TermDbSchema.TermTable.NAME, values,
                TermDbSchema.TermTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private TermCursorWrapper queryTerms(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TermDbSchema.TermTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                "termEnglish"  // orderBy
        );
        return new TermCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Term t) {
        ContentValues values = new ContentValues();
        values.put(UUID, String.valueOf(t.getId()));
        values.put(UPDATED, t.getUpdated());
        values.put(ENG, t.getEnglish());
        values.put(SRB, t.getSerbian());
        values.put(DSC, t.getDescription());

        return values;
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

