package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by qiuzhexin on 12/17/16.
 */

@ParseClassName("TransferJob")
public class TransferJob extends ParseObject {

    public static final String PIN_TAG = "ALL_TRANSFER_JOBS";

    public void setJobType(String jobType) {
        put("job_type", jobType);
    }

    public String getJobTyoe() {
        return getString("job_type");
    }

    public void setUserId(String userId) {
        put("user_id", userId);
    }

    public String getUserId() {
        return  getString("user_id");
    }

    public static ParseQuery<TransferJob> getQuery() {
        ParseQuery<TransferJob> parseQuery = ParseQuery.getQuery(TransferJob.class);
        parseQuery.setLimit(1000);
        return parseQuery;
    }
}
