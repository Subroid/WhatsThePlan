package platinum.whatstheplan.activities.authentications;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private TextView mResetPwTV;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailTIET = findViewById(R.id.sia_emailET);
        passwordTIET = findViewById(R.id.sia_passwordET);
        signUpBTN = findViewById(R.id.sia_signupBTN);
        signInBTN = findViewById(R.id.sia_signinBTN);
        mResetPwTV = findViewById(R.id.reset_pw_TV);

        signInBTN.setOnClickListener(this);
        signUpBTN.setOnClickListener(this);
        mResetPwTV.setOnClickListener(this);
        progressBar = findViewById(R.id.progressbarPB);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sia_signinBTN :
                progressBar.setVisibility(View.VISIBLE);
                signInUsingEmailPassword();
                break;
            case R.id.sia_signupBTN :
                    Intent signUpIntent = new android.content.Intent(
                        SignInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;
            case R.id.reset_pw_TV :
                showPasswordResetDialog ();
        }
    }

    private void navigateToAnotherActivity(Class className) {
        Intent intent = new Intent(SignInActivity.this, className);
        startActivity(intent);
    }

    private void showPasswordResetDialog() {
        new AlertDialog.Builder(SignInActivity.this)
                .setTitle("Reset Password")
                .setMessage("Do you want to reset the Password?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        navigateToAnotherActivity(PasswordResetActivity.class);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void signInUsingEmailPassword() {
                    mEmailString = emailTIET.getText().toString();
                    mPasswordString = passwordTIET.getText().toString();
                    if (mEmailString.isEmpty() || mPasswordString.isEmpty() || mPasswordString.length() < 6) {
                        progressBar.setVisibility(View.INVISIBLE);
                        FancyToast.makeText(getApplicationContext(),
                                "Please type valid Email or Password",
                                FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailString, mPasswordString)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                                Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                                            startActivity(homeIntent);
                                            finish();
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            FancyToast.makeText(getApplicationContext(),
                                                    "There is some error while signing in to your account",
                                                    FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                        }
                                    }
                                });
                        }
                }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
