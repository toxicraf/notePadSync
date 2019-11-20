package rs.in.raf1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class TermPagerActivity extends AppCompatActivity
        implements TermFragment.Callbacks {

    private static final String EXTRA_TERM_ID =
            "rs.in.raf1.term_id";

    private ViewPager mViewPager;
    private List<Term> mTerms;

    public static Intent newIntent(Context packageContext, UUID termId) {
        Intent intent = new Intent(packageContext, TermPagerActivity.class);
        intent.putExtra(EXTRA_TERM_ID, termId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_pager);

        UUID termId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_TERM_ID);

        mViewPager = (ViewPager) findViewById(R.id.term_view_pager);

        mTerms = TermLab.get(this).getTerms(TermListActivity.sFilter);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Term term = mTerms.get(position);
                return TermFragment.newInstance(term.getId());
            }

            @Override
            public int getCount() {
                return mTerms.size();
            }
        });

        for (int i = 0; i < mTerms.size(); i++) {
            if (mTerms.get(i).getId().equals(termId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onTermUpdated(Term term) {
        finish();
    }

    @Override
    public void onTermDeleted(UUID termId) {
        finish();
    }


}
