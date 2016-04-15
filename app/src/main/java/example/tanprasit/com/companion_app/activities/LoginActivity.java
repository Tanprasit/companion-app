package example.tanprasit.com.companion_app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import example.tanprasit.com.companion_app.R;
import example.tanprasit.com.companion_app.network.RequestTask;
import example.tanprasit.com.companion_app.network.VolleySingleton;
import example.tanprasit.com.companion_app.tools.URLBuilder;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login() {
        EditText usernameEditText = (EditText) findViewById(R.id.login_username_field);
        EditText passwordEditText = (EditText) findViewById(R.id.login_password_field);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());

        String url = new URLBuilder().getLoginUrl();

        new RequestTask(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }).sendPostRequest(url, params);


    }
}
