package uriya.madmoni.mygoodpacmanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener, CheckBox.OnCheckedChangeListener {

    EditText etEmail, etPassword, etUserName;
    TextInputLayout etUserNameContainer;
    Button btnLogin;
    TextView tvForgetPassword, tvSwitchCase;
    CheckBox cbRemember;

    boolean isLogin, rememberLogin;

    AuthHelper authHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initWidgets();

        isLogin = true;
        rememberLogin = false;
        authHelper = new AuthHelper(this);

        if(authHelper.isRememberAndLoggedIn()) {
            authHelper.getCurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void initWidgets() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUserName = findViewById(R.id.etUserName);
        etUserNameContainer = findViewById(R.id.etUserNameContainer);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvForgetPassword.setOnClickListener(this);
        tvSwitchCase = findViewById(R.id.tvSwitchCase);
        tvSwitchCase.setOnClickListener(this);
        cbRemember = findViewById(R.id.cbRemember);
        cbRemember.setOnCheckedChangeListener(this);

    }
    public void switchCase() {
        isLogin = !isLogin;
        if(isLogin) {
            btnLogin.setText("LOGIN");
            tvSwitchCase.setText("Haven't account? Register!");
            etUserNameContainer.setVisibility(View.GONE);
        } else {
            btnLogin.setText("REGISTER");
            tvSwitchCase.setText("Have account? Login!");
            etUserNameContainer.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnLogin:
                if(isLogin) authHelper.login(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),rememberLogin);
                else authHelper.register(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),etUserName.getText().toString().trim(),rememberLogin);
                break;
            case R.id.tvForgetPassword:

                break;
            case R.id.tvSwitchCase:
                switchCase();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        rememberLogin = b;
    }
}