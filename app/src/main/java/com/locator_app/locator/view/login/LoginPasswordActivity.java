package com.locator_app.locator.view.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.apiservice.errorhandling.HttpError;
import com.locator_app.locator.apiservice.users.LoginRequest;
import com.locator_app.locator.controller.MyController;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.view.LocatorHeader;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginPasswordActivity extends Activity {

    @Bind(R.id.loginPassword)
    TextView loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.login);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        loginPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String mail = getIntent().getStringExtra("mail");
                String password = loginPassword.getText().toString();
                if (password.length() < 3) {
                    Toast.makeText(this, "Das Passwort muss länger als 3 Zeichen sein", Toast.LENGTH_SHORT).show();
                    return false;
                }
                login(mail, password);
                return true;
            }
            return false;
        });
    }

    @OnClick(R.id.forgotPassword)
    void onForgotPasswordClick() {
        new AlertDialog.Builder(this)
                .setTitle("Super")
                .setMessage("Ein neues Passwort wird an die angegebene Emailadresse gesendet")
                .setPositiveButton("Okay", (dialog, which) -> {
                })
                .show();
        MyController.getInstance().forgotPassword(getIntent().getStringExtra("mail"))
                .subscribe(
                        (res) -> {
                        },
                        (err) -> {
                            if (err instanceof HttpError &&
                                    ((HttpError) err).getErrorCode() == HttpError.HttpErrorCode.notFound) {
                                Toast.makeText(this, "Deine Emailadresse ist nicht registriert", Toast.LENGTH_SHORT).show();
                            } else {
                                UiError.showError(this, err, "Da ist was schief gelaufen :/");
                            }
                        }
                );
    }

    void login(String mail, String password) {
        final Context context = getApplicationContext();

        UserController userController = UserController.getInstance();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.mail = mail;
        loginRequest.password = password;
        userController.login(loginRequest)
                .subscribe(
                        (loginResponse) -> {
                            Toast.makeText(context, "Hi " + loginResponse.name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        },
                        (error) -> {
                            if (error instanceof HttpError &&
                                ((HttpError) error).getErrorCode() == HttpError.HttpErrorCode.unauthorized) {
                                UiError.showError(this, error, "E-Mail oder Passwort falsch");
                                LoginPasswordActivity.this.finish();
                            } else {
                                UiError.showError(this, error, "Da ist was komisches passiert");
                            }
                        });
    }
}
