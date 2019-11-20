package rs.in.raf1;

import org.json.JSONObject;

public interface OnTaskDoneListener {
    void onTaskDone(JSONObject object);
    void onError();
}
