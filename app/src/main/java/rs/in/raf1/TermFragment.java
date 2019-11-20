package rs.in.raf1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class TermFragment extends Fragment {

    private static final String ARG_TERM_ID = "term_id";

    private Term mTerm;
    private EditText mTermEnglish;
    private EditText mTermSerbian;
    private EditText mTermDescription;

    public Button btnAdd;
    public Button btnDelete;
    public Button btnUpdate;

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onTermUpdated(Term term);
        void onTermDeleted(UUID termId);
    }

    public static TermFragment newInstance(UUID termId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TERM_ID, termId);

        TermFragment fragment = new TermFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();

      //  TermLab.get(getActivity())
      //          .updateTerm(mTerm);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID termId = (UUID) getArguments().getSerializable(ARG_TERM_ID);
        mTerm = TermLab.get(getActivity()).getTerm(termId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_term, container, false);

        mTermEnglish = (EditText) v.findViewById(R.id.termEnglish);
        mTermSerbian = (EditText) v.findViewById(R.id.termSerbian);
        mTermDescription = (EditText) v.findViewById(R.id.termDescription);

        mTermEnglish.setText(mTerm.getEnglish());
        mTermSerbian.setText((mTerm.getSerbian()));
        mTermDescription.setText((mTerm.getDescription()));

        btnUpdate = (Button) v.findViewById(R.id.btnUpdate);
        btnDelete = (Button) v.findViewById(R.id.btnDelete);

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateTerm();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermLab.get(getActivity()).removeTerm(mTerm.getId());
                deleteTerm(mTerm.getId());

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                ft.remove(TermFragment.this);
                ft.commit();
            }
        });

        return v;

    }

    private void updateTerm() {
        mTerm.setEnglish(mTermEnglish.getText().toString());
        mTerm.setSerbian(mTermSerbian.getText().toString());
        mTerm.setDescription(mTermDescription.getText().toString());
        TermLab.get(getActivity()).updateTerm(mTerm);
        mCallbacks.onTermUpdated(mTerm);
    }

    private void deleteTerm(UUID termId) {
        TermLab.get(getActivity()).removeTerm(termId);
        mCallbacks.onTermDeleted(termId);
    }

}






