package platinum.whatstheplan.activities.authentications;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.HomeActivity;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivityTag";
    TextInputEditText emailTIET;
    TextInputEditText passwordTIET;
    Button signUpBTN;
    Button signInBTN;
    String mEmailString;
    String mPasswordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailTIET = findViewById(R.id.sia_emailET);
        passwordTIET = findViewById(R.id.sia_passwordET);
        signUpBTN = findViewById(R.id.sia_signupBTN);
        signInBTN = findViewById(R.id.sia_signinBTN);

        signInBTN.setOnClickListener(this);
        signUpBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sia_signinBTN :
                signInUsingEmailPassword();
                break;
            case R.id.sia_signupBTN :
                    Intent signUpIntent = new android.content.Intent(
                        SignInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;
        }
    }

                private void signInUsingEmailPassword() {
                    mEmailString = emailTIET.getText().toString();
                    mPasswordString = passwordTIET.getText().toString();
                    if (mEmailString.isEmpty() || mPasswordString.isEmpty() || mPasswordString.length() < 6) {
                        FancyToast.makeText(getApplicationContext(),
                                "Please type valid Email or Password",
                                FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailString, mPasswordString)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                                Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                                            startActivity(homeIntent);
                                            finish();
                                        } else {
                                            FancyToast.makeText(getApplicationContext(),
                                                    "There is some error while signing in to your account",
                                                    FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                        }
                                    }
                                });
                        }
                }
}
