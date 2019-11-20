package rs.in.raf1.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rs.in.raf1.Term;

public class ParseJSON {

    public static ArrayList<Term> jsonParse(JSONObject jObj) throws JSONException {

        ArrayList<Term> termsListWeb = new ArrayList<>();
        JSONArray termsArr = jObj.getJSONArray("terms");

        for (int i = 0; i < termsArr.length(); i++) {
            JSONObject terms = termsArr.getJSONObject(i);

            String id = UUID.randomUUID().toString();
            String termUpdated = terms.getString("termUpdated");
            String termEnglish = terms.getString("termEnglish");
            String termSerbian = terms.getString("termSerbian");
            String termDescription = terms.getString("termDescription");

            Term term = new Term();

            term.setId(UUID.fromString(id));
            term.setUpdated(termUpdated);
            term.setEnglish(termEnglish);
            term.setSerbian(termSerbian);
            term.setDescription(termDescription);

            termsListWeb.add(term);
        }
        return termsListWeb;
    }
}
