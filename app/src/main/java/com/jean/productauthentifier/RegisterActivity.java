package com.jean.productauthentifier;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jean.productauthentifier.api.APIClient;
import com.jean.productauthentifier.api.UserApiService;
import com.jean.productauthentifier.common.CommonProvider;
import com.jean.productauthentifier.model.UserRegistration;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstnameEdit, lastnameEdit, usernameEdit, passwordEdit, addressEdit, emailEdit, phoneEdit;
    private String firstnameVal, lastnameVal, usernameVal, passwordVal, addressVal, emailVal, phoneVal, countryVal;
    private AutoCompleteTextView countriesDropdown;
    private Switch emailNotificationSwitch;
    private boolean emailNotificationVal;
    private Button registerBtn;
    private TextView goToLogin;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        componentsInitializer();

        final ArrayAdapter<String> countries_dropdown_adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_popup_countries,
                getResources().getStringArray(R.array.countries));
        countriesDropdown.setAdapter(countries_dropdown_adapter);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstnameVal = firstnameEdit.getText().toString();
                lastnameVal = lastnameEdit.getText().toString();
                usernameVal = usernameEdit.getText().toString();
                passwordVal = passwordEdit.getText().toString();
                addressVal = addressEdit.getText().toString();
                emailVal = emailEdit.getText().toString();
                phoneVal = phoneEdit.getText().toString();
                countryVal = countriesDropdown.getText().toString();
                emailNotificationVal = (emailNotificationSwitch.isChecked()) ? true : false;

                if (checkValidation(firstnameVal,lastnameVal,usernameVal,passwordVal,addressVal,emailVal,phoneVal,countryVal,emailNotificationVal)) {
                    if (CommonProvider.isNetworkAvailable(RegisterActivity.this))
                        registrationApiCaller(firstnameVal,lastnameVal,usernameVal,passwordVal,addressVal,emailVal,phoneVal,countryVal, emailNotificationVal);
                    else
                        CommonProvider.showAlert("Please Check your Internet Connectivity", RegisterActivity.this);
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginActivity();
            }
        });
    }

    private void componentsInitializer(){
        firstnameEdit = (EditText) findViewById(R.id.firstNameEditText);
        lastnameEdit = (EditText) findViewById(R.id.lastNameEditText);
        usernameEdit = (EditText) findViewById(R.id.usernameEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEditText);
        addressEdit = (EditText) findViewById(R.id.addressEditText);
        emailEdit = (EditText) findViewById(R.id.emailEditText);
        phoneEdit = (EditText) findViewById(R.id.phoneEditText);
        countriesDropdown = (AutoCompleteTextView) findViewById(R.id.countries_dropdown);
        emailNotificationSwitch = (Switch) findViewById(R.id.notificationSwitch);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        goToLogin = (TextView) findViewById(R.id.goToLoginTextView);
        loadingPB = (ProgressBar) findViewById(R.id.idLoadingPB);
    }
    private boolean checkValidation(final String firstnameVal,
                                    final String lastnameVal,
                                    final String usernameVal,
                                    final String passwordVal,
                                    final String addressVal,
                                    final String emailVal,
                                    final String phoneVal,
                                    final String countryVal,
                                    final boolean emailNotificationVal) {

        if (firstnameVal.trim().equals("")) {
            CommonProvider.showAlert("First name - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (lastnameVal.trim().equals("")) {
            CommonProvider.showAlert("Last name - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (usernameVal.trim().equals("")) {
            CommonProvider.showAlert("Username - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (passwordVal.trim().equals("")) {
            CommonProvider.showAlert("Password - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (addressVal.trim().equals("")) {
            CommonProvider.showAlert("Address - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (emailVal.trim().equals("")) {
            CommonProvider.showAlert("Email - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (phoneVal.trim().equals("")) {
            CommonProvider.showAlert("Phone number - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if (countryVal.trim().equals("") || countryVal.equals("--Select country--")) {
            CommonProvider.showAlert("Country - Cannot be left blank", RegisterActivity.this);
            return false;
        }

        if(emailNotificationVal == true && !emailVal.contains("@gmail")){
            CommonProvider.showAlert("Email - Must be gmail to be eligible for the notifications system", RegisterActivity.this);
            return false;
        }

        return true;
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void registrationApiCaller(final String firstnameVal,
                                        final String lastnameVal,
                                        final String usernameVal,
                                        final String passwordVal,
                                        final String addressVal,
                                        final String emailVal,
                                        final String phoneVal,
                                        final String countryVal,
                                        final boolean emailNotificationVal) {

        // Displaying our progress bar.
        loadingPB.setVisibility(View.VISIBLE);

        final UserApiService userApiService = APIClient.getClient().create(UserApiService.class);

        final UserRegistration userRegistration = new UserRegistration(firstnameVal,
                lastnameVal,
                usernameVal,
                passwordVal,
                addressVal,
                emailVal,
                phoneVal,
                countryVal,
                emailNotificationVal);

        Call<String> call = userApiService.registerNewUser(userRegistration);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingPB.setVisibility(View.GONE);

                if(response.code() == 200 || response.code() == 201) {
                    //CommonProvider.showAlert("Success:" + response.body(), RegisterActivity.this);
                    String success = "\n\nTry to signin with your new account..";
                    showAlertRegistrationSuccess("Success:" + response.body()+ success, RegisterActivity.this);
                    resetFields();
                } else {
                        try {
                            CommonProvider.showAlert("Failed:" + response.errorBody().string(), RegisterActivity.this);
                        }catch (IOException ex){
                            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                CommonProvider.showAlert("Failed:"+ t.getMessage(), RegisterActivity.this);
            }
        });
    }

    private void resetFields(){
        firstnameEdit.setText("");
        lastnameEdit.setText("");
        usernameEdit.setText("");
        passwordEdit.setText("");
        addressEdit.setText("");
        emailEdit.setText("");
        phoneEdit.setText("");

        countriesDropdown.setText(countriesDropdown.getAdapter().getItem(0).toString(), false);
    }

    private void showAlertRegistrationSuccess(String message, Activity context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setIcon(R.drawable.mark_tick)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goToLoginActivity();
                    }
                });
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
