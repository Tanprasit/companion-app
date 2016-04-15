package example.tanprasit.com.companion_app.networks;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import example.tanprasit.com.companion_app.Constants;
import example.tanprasit.com.companion_app.tools.URLBuilder;

/**
 * Created by luketanprasit on 05/04/2016.
 */
public class RequestTask {
    private ErrorListener errorListener;
    private Listener<String> listener;
    private Context context;
    private RequestQueue queue;
    private Map<String, String> postParams;

    public RequestTask(Listener<String> listener, ErrorListener errorListener) {
        this.listener = listener;
        this.errorListener = errorListener;
    }

    public RequestTask(Listener<String> listener, ErrorListener errorListener, Context context) {
        this(listener, errorListener);
        this.context = context;
        this.queue = VolleySingleton.getInstance(this.context).getRequestQueue();
    }

    // Issue a get request and get a response.
    public void sendGetRequest(final String url) {
        final StringRequest stringRequest = new StringRequest(url, this.listener, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse nr = error.networkResponse;
                if (nr != null && nr.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    handleChallenge(error, url, Constants.GET);
                } else {
                    Log.e("Get req error", error.getMessage());
                }
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                // 60 seconds timeout
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        this.queue.add(stringRequest);
    }

    // Issue a post request and get a response.
    public void sendPostRequest(final String url, Map<String, String> params) {

        this.postParams = params;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, this.listener, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse nr = error.networkResponse;
                if (nr != null && nr.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    handleChallenge(error, url, Constants.POST);
                } else {
                    Log.e("Post req error", "Post request failed");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return postParams;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                // 60 seconds timeout
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        this.queue.add(stringRequest);
    }

    private void handleChallenge(VolleyError error, String url, String method) {
        Map<String, String> responseHeaders = error.networkResponse.headers;
        // Check if auth header exists
        if (responseHeaders.containsKey("WWW-Authenticate")) {
            String authHeader = responseHeaders.get("WWW-Authenticate");

            final Map<String, String> digestHeaders = parseHeader(authHeader);

            final Map<String, String> newHeaders = new HashMap<>();
            newHeaders.put("Authorization", generateAuthorizationHeader(digestHeaders, method, url));

            int methodInt = (method.equals(Constants.GET))
                    ? Request.Method.GET
                    : Request.Method.POST;

            StringRequest stringRequest = new StringRequest(methodInt, url, this.listener, this.errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return newHeaders;
                }

                @Override
                protected Map<String, String> getParams() {
                    return postParams;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    // 60 seconds timeout
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            this.queue.add(stringRequest);
        } else {
            Log.e("Digest Error", "WWW-Authenticate header is missing");
        }
    }

    /**
     * Gets the Authorization header string minus the "AuthType" and returns a
     * hashMap of keys and values
     *
     * @param headerString
     * @return
     */
    private HashMap<String, String> parseHeader(String headerString) {
        // Separate out the part of the string which tells you which Auth scheme
        // is it
        String headerStringWithoutScheme = headerString.substring(
                headerString.indexOf(" ") + 1).trim();
        HashMap<String, String> values = new HashMap<>();
        String keyValueArray[] = headerStringWithoutScheme.split(",");
        for (String keyval : keyValueArray) {
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                values.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }
        return values;
    }

    /**
     * The MD5 hash of the combined HA1 result, server nonce (nonce), request counter (nc),
     * client nonce (cnonce), quality of protection code (qop) and HA2 result is calculated.
     * The result is the "response" value provided by the client.
     *
     * @param digestHeaders
     * @param method
     * @param url
     * @return
     */
    private String generateAuthorizationHeader(Map<String, String> digestHeaders, String method, String url) {
        String uri = url.replace("http://" + URLBuilder.baseURL, "");
        String username = Constants.USERNAME;
        String realm = digestHeaders.get("realm");
        String nonce = digestHeaders.get("nonce");
        String secret = Constants.SECRET;
        String opaque = digestHeaders.get("opaque");
        String nc = "1";
        String cnonce = "1";
        String qop = "auth";

        String H1 = new String(Hex.encodeHex(DigestUtils.md5(username + ":" + realm + ":" + secret)));
        String H2 = new String(Hex.encodeHex(DigestUtils.md5(method + ":" + uri)));

        String hashString = new String(Hex.encodeHex(DigestUtils.md5(
                H1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + H2)));

        String header = "";
        header += "Digest username=\"" + username + "\", ";
        header += "realm=\"" + realm + "\", ";
        header += "response=\"" + hashString + "\", ";
        header += "nonce=\"" + nonce + "\", ";
        header += "uri=\"" + uri + "\", ";
        header += "nc=\"" + nc + "\", ";
        header += "qop=\"" + qop + "\", ";
        header += "cnonce=\"" + cnonce + "\", ";
        header += "opaque=\"" + opaque + "\"";
        return header;
    }
}
