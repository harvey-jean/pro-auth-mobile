package com.jean.productauthentifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jean.productauthentifier.api.APIClient;
import com.jean.productauthentifier.api.UserApiService;
import com.jean.productauthentifier.common.CommonProvider;
import com.jean.productauthentifier.model.UserLogin;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEdit, passwordEdit;
    String usernameVal, passwordVal;
    private Button loginBtn;
    private TextView changePasswordText, signupText;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Initialise components
        componentsInitializer();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameVal = usernameEdit.getText().toString();
                passwordVal = passwordEdit.getText().toString();

                if(checkValidation(usernameVal,passwordVal)){
                    if (CommonProvider.isNetworkAvailable(LoginActivity.this))
                        loginApiCaller(usernameVal,passwordVal);
                    else
                        CommonProvider.showAlertDialog(LoginActivity.this, "Connexion failure", "Please Check your Internet Connectivity", 1);
                }
            }
        });

        changePasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangePassword();
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignup();
            }
        });
    }

    private void componentsInitializer(){
        usernameEdit = (EditText) findViewById(R.id.editTextUsername);
        passwordEdit = (EditText) findViewById(R.id.editTextPassword);
        loginBtn = (Button) findViewById(R.id.cirLoginButton);
        loadingPB = (ProgressBar) findViewById(R.id.idLoadingPB);
        changePasswordText = (TextView) findViewById(R.id.changePasswordTxtView);
        signupText = (TextView) findViewById(R.id.viewRegisterClick);
    }

    private boolean checkValidation(final String usernameVal,
                                    final String passwordVal) {

        if (usernameVal.trim().equals("")) {
            CommonProvider.showAlert("Username - Cannot be left blank", LoginActivity.this);
            return false;
        }else if (passwordVal.trim().equals("")) {
            CommonProvider.showAlert("Password - Cannot be left blank", LoginActivity.this);
            return false;
        }
        return true;
    }

    private void loginApiCaller(final String usernameVal,
                                final String passwordVal) {

        // Displaying our progress bar.
        loadingPB.setVisibility(View.VISIBLE);

        final UserApiService userApiService = APIClient.getClient().create(UserApiService.class);

        final UserLogin userLogin = new UserLogin(usernameVal, passwordVal);
        Call<String> call = userApiService.userLogin(userLogin);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingPB.setVisibility(View.GONE);

                if(response.code() == 200 || response.code() == 201) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully..", Toast.LENGTH_LONG).show();
                    resetFields();
                    goToHome();
                } else {
                    try {
                        CommonProvider.showAlertDialog(LoginActivity.this, "Login failed:", response.errorBody().string(), 1);
                        resetFields();
                    }catch (IOException ex){
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                CommonProvider.showAlert("Login failed:"+ t.getMessage(), LoginActivity.this);
            }
        });
    }

    private void resetFields(){
        usernameEdit.setText("");
        passwordEdit.setText("");
    }

    private void goToHome(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("username", usernameVal);
        intent.putExtra("isLoggedIn", true);
        startActivity(intent);
    }

    private void goToChangePassword(){
        Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void goToSignup() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
