package platinum.whatstheplan.activities.authentications;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.shashank.sony.fancytoastlib.FancyToast;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.UserInformation;
import platinum.whatstheplan.models.UserLocation;
import platinum.whatstheplan.models.UserProfile;

public class SignUpActivity extends AppCompatActivity {

    //todo progressbar
    //todo signup button pink selector color when clicked

    private static final String TAG = "SignUpActivityTag";
    private TextInputEditText emailTIET;
    private TextInputEditText passwordTIET;
    private Button signUpBTN;
    private ProgressBar progressBar;

    String mEmailString;
    String mPasswordString;

       @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailTIET = findViewById(R.id.sua_emailET);
        passwordTIET = findViewById(R.id.sua_passwordET);
        signUpBTN = findViewById(R.id.sua_signupBTN);
        progressBar = findViewById(R.id.progressbarPB);

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                mEmailString = emailTIET.getText().toString();
                mPasswordString = passwordTIET.getText().toString();
                if (mEmailString.isEmpty() || mPasswordString.isEmpty() || mPasswordString.length() < 6) {
                    progressBar.setVisibility(View.INVISIBLE);
                    FancyToast.makeText(getApplicationContext(),
                            "Please type valid Email or Password",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(mEmailString, mPasswordString)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                            final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                        Log.d(TAG, "onComplete: current_user.getUid() = " + current_user.getUid());
                                            UserProfile userProfile = new UserProfile(mEmailString, mPasswordString, false, null, current_user.getUid());
                                            UserLocation userLocation = new UserLocation();
                                            UserInformation userInformation = new UserInformation(userProfile, userLocation);
                                        firestore.collection("Users").document(current_user.getUid()).set(userInformation)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Intent nameSubmitIntent = new Intent(
                                                                    SignUpActivity.this, NameSubmitActivity.class);
                                                            startActivity(nameSubmitIntent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        progressBar.setVisibility(View.INVISIBLE);
                                        FancyToast.makeText(getApplicationContext(),
                                                "There is some error while creating new account",
                                                FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                    }
                                }
                            });
                    }
            }
        });


    }
}
