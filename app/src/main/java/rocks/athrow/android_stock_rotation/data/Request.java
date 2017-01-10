package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by joselopez on 1/10/17.
 */

public class Request extends RealmObject {
    public final static String ID = "id";
    @PrimaryKey
    private String id;
    private Date requestDate;
    private String requestURL;
    private int APIResponseCode;
    private String APIResponseText;

    public Request() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public int getAPIResponseCode() {
        return APIResponseCode;
    }

    public void setAPIResponseCode(int APIResponseCode) {
        this.APIResponseCode = APIResponseCode;
    }

    public String getAPIResponseText() {
        return APIResponseText;
    }

    public void setAPIResponseText(String APIResponseText) {
        this.APIResponseText = APIResponseText;
    }
}
