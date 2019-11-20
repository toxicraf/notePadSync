package rs.in.raf1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.UUID;

import rs.in.raf1.database.TermBaseHelper;

public class TermListActivity extends SingleFragmentActivity
        implements TermListFragment.Callbacks, TermFragment.Callbacks {

    public static String sFilter = "";
    public TermBaseHelper db;

    @Override
    protected Fragment createFragment() {
        return new TermListFragment();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db = new TermBaseHelper(this);

    }


    @Override
    public void onTermSelected(Term term) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = TermPagerActivity.newIntent(this, term.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = TermFragment.newInstance(term.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

   public void onTermUpdated(Term term) {
        updateListFragment(sFilter);
    }

    public void onTermDeleted(UUID termId) {
        updateListFragment(sFilter);
    }

    private void updateListFragment(String filter) {
        TermListFragment listFragment = (TermListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI(filter);
    }

}
