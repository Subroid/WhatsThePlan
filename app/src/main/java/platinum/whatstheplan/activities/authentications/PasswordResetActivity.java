package platinum.whatstheplan.activities.authentications;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.HomeActivity;

public class PasswordResetActivity extends AppCompatActivity {

    private Button resetPWBTN;
    private TextInputEditText emailTIET;
    private String emailSTRING;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        emailTIET = findViewById(R.id.emailTIET);

        resetPWBTN = findViewById(R.id.reset_pw_BTN);
        resetPWBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailSTRING = emailTIET.getText().toString();

                if (emailSTRING.isEmpty()) {
                    FancyToast.makeText(getApplicationContext(),
                            "Please type valid Email Address",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailSTRING).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent signInIntent = new Intent(PasswordResetActivity.this, SignInActivity.class);
                                startActivity(signInIntent);
                                finish();
                            } else {
                                FancyToast.makeText(getApplicationContext(),
                                        "There is some error while resetting your account",
                                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                        }
                    });

                }
            }
        });

    }
}
