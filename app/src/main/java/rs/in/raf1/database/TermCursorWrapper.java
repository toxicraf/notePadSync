package rs.in.raf1.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import rs.in.raf1.Term;
import rs.in.raf1.database.TermDbSchema.TermTable.*;


public class TermCursorWrapper extends CursorWrapper {

    public TermCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Term getTerm() {
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String termUpdated = getString(getColumnIndex(Cols.UPDATED));
        String termEnglish = getString(getColumnIndex(Cols.ENG));
        String termSerbian = getString(getColumnIndex(Cols.SRB));
        String termDescription = getString(getColumnIndex(Cols.DSC));

        Term term = new Term(UUID.fromString(uuidString));

        term.setUpdated(termUpdated);
        term.setEnglish(termEnglish);
        term.setSerbian(termSerbian);
        term.setDescription(termDescription);

        return term;
    }



}
