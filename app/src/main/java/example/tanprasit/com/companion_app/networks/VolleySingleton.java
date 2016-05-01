package example.tanprasit.com.companion_app.networks;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by luketanprasit on 08/03/2016.
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleySingleton(Context context) {
        VolleySingleton.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it prevents leaking the
            // Activity or BroadcastReceiver if the context was passed in accidently.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
}
