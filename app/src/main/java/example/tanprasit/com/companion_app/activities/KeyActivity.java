package example.tanprasit.com.companion_app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import example.tanprasit.com.companion_app.Constants;
import example.tanprasit.com.companion_app.R;
import example.tanprasit.com.companion_app.adapter.KeyAdapter;
import example.tanprasit.com.companion_app.models.Contractor;
import example.tanprasit.com.companion_app.models.Key;
import example.tanprasit.com.companion_app.networks.RequestTask;
import example.tanprasit.com.companion_app.networks.responses.KeyListResponse;
import example.tanprasit.com.companion_app.tools.URLBuilder;

public class KeyActivity extends AppCompatActivity {

    private Contractor contractor;
    private KeyAdapter keyAdapter;

    // Prevents back button from returning to empty back activity.
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Key List");
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.activity_key_recycler_view);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_key_refresh_layout);

        contractor = getContractorFromPreference();

        // Set listener for keys manual refresh.
        setRefreshListener(swipeRefreshLayout);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final List<Key> keyList = contractor.getKeys();
        Collections.reverse(contractor.getKeys());

        // specify an adapter (see also next example)
        keyAdapter = new KeyAdapter(keyList);
        mRecyclerView.setAdapter(keyAdapter);

        if (keyList.size() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "No keys to be displayed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Contractor getContractorFromPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userObj = sharedPreferences.getString(Constants.USER_OBJECT, null);

        if (userObj == null) {
            throw new RuntimeException("Failed to load user object");
        }

        return new Gson().fromJson(userObj,Contractor.class);
    }

    private void setRefreshListener(final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int userId = contractor.getId();

                if (userId != 0) {
                    String url = new URLBuilder().getKeysUrl(userId);

                    new RequestTask(new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            KeyListResponse keyKeyListResponse = new Gson().fromJson(response, KeyListResponse.class);
                            List<Key> keyList = keyKeyListResponse.getKeyList();

                            if (keyList.size() > 0) {
                                Collections.reverse(keyList);
                                keyAdapter.swap(keyList);
                            } else {
                                keyAdapter.clear();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "No keys to be displayed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String responseBody = "";
                            try {
                                responseBody = new String(error.networkResponse.data, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.e("UnsupportedEncoding", e.getMessage());
                            }

                            Toast.makeText(getBaseContext(), responseBody, Toast.LENGTH_SHORT).show();
                        }
                    }, getBaseContext()).sendGetRequest(url);
                } else {
                    throw new RuntimeException("Missing local user object.");
                }
            }
        });
    }
}
