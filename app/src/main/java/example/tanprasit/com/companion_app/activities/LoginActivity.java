package example.tanprasit.com.companion_app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import example.tanprasit.com.companion_app.Constants;
import example.tanprasit.com.companion_app.R;
import example.tanprasit.com.companion_app.models.Contractor;
import example.tanprasit.com.companion_app.networks.RequestTask;
import example.tanprasit.com.companion_app.tools.URLBuilder;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setDefaultEmail();
    }

    private void setDefaultEmail() {
        EditText editText = (EditText) findViewById(R.id.login_username_field);

        if (this.sharedPreferences.getBoolean(Constants.REMEMBER_ME, false)) {
            editText.setText(this.sharedPreferences.getString(Constants.CURRENT_EMAIL, ""));

            CheckBox checkBox = (CheckBox) findViewById(R.id.login_checkbox);
            checkBox.setChecked(true);
        }
    }

    public void login(View view) {
        // Hide keyboard on credential submission.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        EditText usernameEditText = (EditText) findViewById(R.id.login_username_field);
        EditText passwordEditText = (EditText) findViewById(R.id.login_password_field);

        final String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!email.equals("") && !password.equals("")) {

            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            String url = new URLBuilder().getLoginUrl();

            setLoadingAnimation(true);
            new RequestTask(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Editor editor = sharedPreferences.edit();

                    Contractor contractor = new Gson().fromJson(response, Contractor.class);

                    editor.putString(Constants.USER_ID, String.valueOf(contractor.getId()));
                    editor.putString(Constants.USER_OBJECT, response);

                    saveUserEmail(editor, email);

                    editor.apply();
                    setLoadingAnimation(false);

                    Intent intent = new Intent(getBaseContext(), KeyActivity.class);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse nr = error.networkResponse;

                    Toast.makeText(getBaseContext(), (nr != null && nr.data != null)
                            ? "Incorrect credentials."
                            : "Unexpected error had occurred.", Toast.LENGTH_LONG).show();

                    setLoadingAnimation(false);
                }
            }, getBaseContext()).sendPostRequest(url, params);
        } else {
            Toast.makeText(getBaseContext(), "Missing email or password.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserEmail(Editor editor, String email) {
        // Check if user wanted to save their email.
        CheckBox checkBox = (CheckBox) findViewById(R.id.login_checkbox);
        if (checkBox.isChecked()) {
            editor.putString(Constants.CURRENT_EMAIL, email);
            editor.putBoolean(Constants.REMEMBER_ME, true);

        }
    }

    private void setLoadingAnimation(final boolean b) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (b) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // Prevents back button from returning to empty back activity.
    @Override
    public void onBackPressed() {

    }
}
