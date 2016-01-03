package com.locator_app.locator.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.locator_app.locator.R;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterPasswordActivity extends AppCompatActivity {

    @Bind(R.id.registerPassword)
    EditText registerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
        ButterKnife.bind(this);
        setCustomActionBar();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        registerPassword.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                HashMap<String, String> registerValues =
                        (HashMap<String, String>)getIntent().getSerializableExtra("registerValues");
                registerValues.put("password", registerPassword.getText().toString());
                Intent intent = new Intent(v1.getContext(), RegisterProfilePictureActivity.class);
                intent.putExtra("registerValues", registerValues);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.register));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorRegister);
    }
}