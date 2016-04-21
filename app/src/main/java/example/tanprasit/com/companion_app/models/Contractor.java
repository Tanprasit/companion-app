package example.tanprasit.com.companion_app.models;

import java.util.List;

/**
 * Created by luketanprasit on 09/04/2016.
 */
public class Contractor {
    private int id;
    private String fullName;
    private String email;
    private String mobile;
    private String status;
    private List<Key> keys;

    public Contractor(int id, String fullName, String email, String mobile, String status, List<Key> key) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.status = status;
        this.keys = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
