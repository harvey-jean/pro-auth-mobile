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
import com.jean.productauthentifier.model.UserChangePassword;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText usernameEdit, passwordEdit, newPasswordEdit;
    String usernameVal, passwordVal, newPasswordVal;
    private Button confirmBtn;
    private TextView signinText;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //Initialise components
        componentsInitializer();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameVal = usernameEdit.getText().toString();
                passwordVal = passwordEdit.getText().toString();
                newPasswordVal = newPasswordEdit.getText().toString();

                if(checkValidation(usernameVal,passwordVal,newPasswordVal)){
                    if (CommonProvider.isNetworkAvailable(ChangePasswordActivity.this))
                        changePwdApiCaller(usernameVal,passwordVal,newPasswordVal);
                    else
                        CommonProvider.showAlertDialog(ChangePasswordActivity.this, "Connexion failure", "Please Check your Internet Connectivity", 1);
                }
            }
        });

        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignIn();
            }
        });
    }

    private void componentsInitializer(){
        usernameEdit = (EditText) findViewById(R.id.usernameEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEditText1);
        newPasswordEdit = (EditText) findViewById(R.id.passwordEditText2);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);
        loadingPB = (ProgressBar) findViewById(R.id.idLoadingPB);
        signinText = (TextView) findViewById(R.id.goToLoginTextView);
    }

    private boolean checkValidation(final String usernameVal,
                                    final String passwordVal,
                                    final String newPasswordVal) {

        if (usernameVal.trim().equals("")) {
            CommonProvider.showAlert("Username - Cannot be left blank", ChangePasswordActivity.this);
            return false;
        }

        if (passwordVal.trim().equals("")) {
            CommonProvider.showAlert("Password - Cannot be left blank", ChangePasswordActivity.this);
            return false;
        }

        if (newPasswordVal.trim().equals("")) {
            CommonProvider.showAlert("New Password - Cannot be left blank", ChangePasswordActivity.this);
            return false;
        }

        return true;
    }

    private void changePwdApiCaller(final String usernameVal,
                                    final String passwordVal,
                                    final String newPasswordVal) {

        // Displaying our progress bar.
        loadingPB.setVisibility(View.VISIBLE);

        final UserApiService userApiService = APIClient.getClient().create(UserApiService.class);

        final UserChangePassword userChangePassword = new UserChangePassword(usernameVal, passwordVal, newPasswordVal);
        Call<String> call = userApiService.userChangePassword(userChangePassword);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingPB.setVisibility(View.GONE);

                if(response.code() == 200 || response.code() == 201) {
                    //Toast.makeText(ChangePasswordActivity.this, "Password changed successfully..", Toast.LENGTH_LONG).show();
                    CommonProvider.showAlertDialog(ChangePasswordActivity.this, "SUCCESS", response.body(), 0);
                    resetFields();
                } else {
                    try {
                        CommonProvider.showAlertDialog(ChangePasswordActivity.this, "FAILED", response.errorBody().string(), 1);
                        resetFields();
                    }catch (IOException ex){
                        Toast.makeText(ChangePasswordActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                CommonProvider.showAlert("Login failed:"+ t.getMessage(), ChangePasswordActivity.this);
            }
        });
    }

    private void resetFields(){
        usernameEdit.setText("");
        passwordEdit.setText("");
        newPasswordEdit.setText("");
    }

    private void goToSignIn() {
        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
