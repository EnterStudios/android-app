package com.locator_app.locator.view.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.locator_app.locator.R;
import com.locator_app.locator.view.login.LoginCustomActionBar;
import com.locator_app.locator.view.login.LoginRegisterStartActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterResidenceActivity extends AppCompatActivity {

    private static final int MIN_NAME_LENGTH = 3;

    @Bind(R.id.registerResidence)
    EditText registerResidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_residence);
        ButterKnife.bind(this);
        setCustomActionBar();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        registerResidence.setOnKeyListener((v1, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                HashMap<String, String> registerValues =
                        (HashMap<String, String>)getIntent().getSerializableExtra("registerValues");
                registerValues.put("residence", registerResidence.getText().toString());
                Intent intent = new Intent(v1.getContext(), RegisterMailActivity.class);
                intent.putExtra("registerValues", registerValues);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setCustomActionBar() {
        LoginCustomActionBar customActionBar = new LoginCustomActionBar(getSupportActionBar(), this);
        customActionBar.setTitle(getResources().getString(R.string.where_do_you_live));
        customActionBar.setCrossButtonJumpScreen(LoginRegisterStartActivity.class);
        customActionBar.setColor(R.color.colorRegister);
    }
}
