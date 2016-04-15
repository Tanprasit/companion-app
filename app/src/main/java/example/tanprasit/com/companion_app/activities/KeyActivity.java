package example.tanprasit.com.companion_app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import example.tanprasit.com.companion_app.Constants;
import example.tanprasit.com.companion_app.R;
import example.tanprasit.com.companion_app.adapter.KeyAdapter;
import example.tanprasit.com.companion_app.models.Contractor;
import example.tanprasit.com.companion_app.models.Key;

public class KeyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.activity_key_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Key> keyList = getKeyListFromPreferences();

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new KeyAdapter(keyList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<Key> getKeyListFromPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Contractor contractor = new Gson().fromJson(sharedPreferences.getString(Constants.USER_OBJECT, null), Contractor.class);

        return (contractor != null)
                ? contractor.getKeys()
                : new ArrayList<Key>();
    }

}
