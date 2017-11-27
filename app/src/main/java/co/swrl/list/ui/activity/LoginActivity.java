package co.swrl.list.ui.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import co.swrl.list.R;
import co.swrl.list.SwrlPreferences;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private final String LOG_TAG = "LOGIN";
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private SwrlPreferences preferences;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = (EditText) findViewById(R.id.login_username);
        passwordText = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        TextView signupLink = (TextView) findViewById(R.id.link_signup);

        preferences = new SwrlPreferences(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        if (!validate()) {
            onLoginFailed("please check you filled the form in correctly");
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        new AsyncTask<Object, Object, LoginStatus>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
            }

            @Override
            protected LoginStatus doInBackground(Object... voids) {

                LoginStatus loginStatus = new LoginStatus();
                LoginFailResponse loginFailResponse = new LoginFailResponse();

                loginStatus.loginFailResponse = loginFailResponse;
                loginStatus.success = false;
                loginFailResponse.message = "Sorry, something went wrong when logging in.";

                OkHttpClient client =
                        new OkHttpClient.Builder()
                                .readTimeout(30, TimeUnit.SECONDS)
                                .build();

                HttpUrl loginUrl = HttpUrl.parse("https://www.swrl.co/app-login");
                String json = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
                RequestBody postBody = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(loginUrl)
                        .post(postBody)
                        .build();

                Gson gson = new Gson();

                try {
                    Response response = client.newCall(request).execute();
                    Log.d(LOG_TAG, response.toString());
                    String body = response.body().string();
                    if (response.code() == 200){
                        LoginAuthResponse authResponseFromSwrl = gson.fromJson(body, LoginAuthResponse.class);
                        Log.d(LOG_TAG, authResponseFromSwrl.auth_token + " " + authResponseFromSwrl.user_id);
                        if (authResponseFromSwrl.user_id != -1 && authResponseFromSwrl.auth_token != null) {
                            loginStatus.success = true;
                            loginStatus.loginAuthResponse = authResponseFromSwrl;
                        } else {
                            Log.i(LOG_TAG, "Login Failed. Couldn't get Auth Token From Swrl");
                            loginFailResponse.message = "Couldn't get Auth Token from Swrl";
                        }
                    } else {
                        LoginFailResponse failResponseFromSwrl = gson.fromJson(body, LoginFailResponse.class);
                        loginStatus.loginFailResponse = failResponseFromSwrl;
                        Log.i(LOG_TAG, "Login Failed. Message: " + failResponseFromSwrl.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(LOG_TAG, "Login Failed.");
                }
                return loginStatus;
            }

            @Override
            protected void onPostExecute(LoginStatus loginStatus) {
                progressDialog.dismiss();
                if (loginStatus.success){
                    onLoginSuccess(loginStatus.loginAuthResponse);
                } else {
                    onLoginFailed(loginStatus.loginFailResponse.message);
                }
            }
        }.execute();
    }

    private class LoginStatus {
        boolean success;
        LoginAuthResponse loginAuthResponse;
        LoginFailResponse loginFailResponse;
    }

    private class LoginAuthResponse {
        String auth_token;
        int user_id;
    }
    private class LoginFailResponse {
        String message;
    }

    private void onLoginSuccess(LoginAuthResponse loginAuthResponse) {
        loginButton.setEnabled(true);
        preferences.saveAuthToken(loginAuthResponse.auth_token);
        preferences.saveUserID(loginAuthResponse.user_id);
        finish();
    }

    private void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), "Login failed: " + message, Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
        emailText.setError(message);
        passwordText.setError(message);
    }

    private boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty()) {
            emailText.setError("enter your username or email");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("enter your password");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
