package rs.in.raf1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rs.in.raf1.database.TermBaseHelper;
import rs.in.raf1.web.ParseJSON;

import static rs.in.raf1.web.ParseJSON.jsonParse;

public class TermListFragment extends Fragment {

    // private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    ProgressDialog pDialog;

    private RecyclerView mTermRecyclerView;
    private TermAdapter mAdapter;
    // private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private JsonObjectRequest mJsonObjectRequest;
    private String url = "http://www.raf1.in.rs/terms1/get_new_terms.php";
    private String url1 = "http://www.raf1.in.rs/terms1/volley.php";
    private String urlSync = "http://www.raf1.in.rs/terms1/sync.php";

    ArrayList<Term> termsListWeb;
    private ArrayList<Term> insertList;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onTermSelected(Term term);
        void onTermDeleted(UUID termId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        termsListWeb = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term_list, container, false);

        mTermRecyclerView = (RecyclerView) view
                .findViewById(R.id.term_recycler_view);
        mTermRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




        updateUI(TermListActivity.sFilter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(TermListActivity.sFilter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void setUpSearchView(SearchView sv) {
        sv.setMaxWidth(350);
        sv.setQuery(TermListActivity.sFilter, true);

        sv.setIconified(false);
        sv.clearFocus();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                //updateUI(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                //sv.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String filter) {
                TermListActivity.sFilter = filter;
                updateUI(TermListActivity.sFilter);
                updateSubtitle(TermListActivity.sFilter);

                return true;
            }
        });

        /*
        sv.setOnCloseListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                TermListActivity.sFilter = "";
                sv.setQuery("", true);
                return false;
            }
        });

        */


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_term_list, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        menu.findItem(R.id.new_term).setVisible(true);
        // menu.findItem(R.id.show_subtitle).setVisible(true);
        menu.findItem(R.id.search).setVisible(true);

        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);

        setUpSearchView(sv);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView txtPoruka = (TextView) getActivity().findViewById(R.id.textPoruka);

        switch (item.getItemId()) {

            case R.id.new_term:
                Term term = new Term();
                TermLab.get(getActivity()).addTerm(term);
                updateUI(TermListActivity.sFilter);
                mCallbacks.onTermSelected(term);
                TermListActivity.sFilter = "";
                return true;

            case R.id.sync:

                ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                // da li je dostupan internet ?
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (!(networkInfo != null && networkInfo.isConnected())) {
                     Toast.makeText(getActivity().getApplicationContext(),"Not connected to internet!",Toast.LENGTH_LONG).show();
                     return true;
                }

                // iscitaj sve termine sa moba
                TermLab termLab1 = TermLab.get(getActivity());
                ArrayList<Term> listDbTerms = (ArrayList) termLab1.getTerms("");

                // napravi od toga json objekat
                JSONArray jsonArrayTest = new JSONArray();
                for (Term termTest : listDbTerms) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("termUpdated", termTest.getUpdated());
                        jsonObject.put("termEnglish", termTest.getEnglish());
                        jsonObject.put("termSerbian", termTest.getSerbian());
                        jsonObject.put("termDescription", termTest.getDescription());
                        jsonArrayTest.put(jsonObject);

                    } catch (JSONException e) {
                        txtPoruka.setText(e.toString());
                    }
                }

                JSONObject jsonTerms = new JSONObject();
                try {
                    jsonTerms.put("terms", jsonArrayTest);
                } catch (JSONException e) {
                    txtPoruka.setText(e.toString());
                }

                // posalji json na web
                syncJsonRequest(jsonTerms);

                 return true;

            case R.id.delete:
                TermLab termLab = TermLab.get(getActivity());
                termLab.deleteAll();
                updateUI("");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void syncJsonRequest(JSONObject jsonTerms) {
        final TextView txtPoruka = (TextView) getActivity().findViewById(R.id.textPoruka);

        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        mJsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, urlSync, (JSONObject) jsonTerms,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            txtPoruka.setText("Response: " + response.get("message").toString());
                            JSONObject json = response;
                            ArrayList<Term> termListDb = new ArrayList();
                            termListDb = jsonParse(json);
                            TermBaseHelper db = new TermBaseHelper(getActivity().getApplicationContext());
                            db.deleteAll();

                            db.populate(termListDb);

                            updateUI("");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView txtPoruka = (TextView) getActivity().findViewById(R.id.textPoruka);
                txtPoruka.setText("Greska: " + error.toString());                          }
        });

        mRequestQueue.add(mJsonObjectRequest);

    }

    private void updateSubtitle(String filter) {
        TermLab termLab = TermLab.get(getActivity());
        int termCount = termLab.getTerms(filter).size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, termCount, termCount);

        /*
         if (!mSubtitleVisible) {
            subtitle = null;
        }
        */

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI(String filter) {
        TermLab termLab = TermLab.get(getActivity());
        List<Term> terms;

       terms = termLab.getTerms(filter);

        if (mAdapter == null) {
            mAdapter = new TermAdapter(terms);
            mTermRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTerms(terms);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle(filter);
    }

    private class TermHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public Term mTerm;

        private TextView mEnglishTextView;
        private TextView mSerbianTextView;
        private TextView mDescriptionTextView;

        public TermHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_term, parent, false));
            itemView.setOnClickListener(this);

            mEnglishTextView = (TextView) itemView.findViewById(R.id.termEnglish);
            mSerbianTextView = (TextView) itemView.findViewById(R.id.termSerbian);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.termDescription);
        }


        public void bind(Term term) {
            mTerm = term;

            mEnglishTextView.setText(mTerm.getEnglish());
            mSerbianTextView.setText(mTerm.getSerbian());
            mDescriptionTextView.setText(mTerm.getDescription());
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onTermSelected(mTerm);
            mCallbacks.onTermDeleted(mTerm.getId());
        }
    }

    private class TermAdapter extends RecyclerView.Adapter<TermHolder>{

        private List<Term> mTerms;

        public TermAdapter(List<Term> terms) {
            mTerms = terms;
        }

        @Override
        public TermHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TermHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(final TermHolder holder, final int position) {
            Term term = mTerms.get(position);
            holder.bind(term);
        }

        @Override
        public int getItemCount() {
            return mTerms.size();
        }

        public void setTerms(List<Term> terms) {
            mTerms = terms;
        }

    }
}
