package rs.in.raf1;

import java.util.UUID;

public class Term {

    private UUID mId;

    private String mUpdated;
    private String mEnglish;
    private String mSerbian;
    private String mDescription;

   public Term(UUID id) {
        mId = id;
    }

    public Term() {
        this(UUID.randomUUID());
    }

    public void setId(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setUpdated(String updated) {
        mUpdated = updated;
    }

    public String getUpdated() {
        return mUpdated;
    }

   public String getEnglish() {
        return mEnglish;
    }

    public void setEnglish(String english) {
        mEnglish = english;
    }

    public String getSerbian() {
        return mSerbian;
    }

    public void setSerbian(String serbian) {
        mSerbian = serbian;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }


}
