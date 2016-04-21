package example.tanprasit.com.companion_app.networks.responses;

import java.util.List;

import example.tanprasit.com.companion_app.models.Key;

/**
 * Created by luketanprasit on 17/04/2016.
 */
public class KeyListResponse {
    private List<Key> response;

    public KeyListResponse(List<Key> response) {
        this.response = response;
    }


    public List<Key> getKeyList() {
        return response;
    }
}
