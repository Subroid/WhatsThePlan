package platinum.whatstheplan.activities.authentications;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import platinum.whatstheplan.R;
import platinum.whatstheplan.models.UserInformation;
import platinum.whatstheplan.models.UserLocation;
import platinum.whatstheplan.models.UserProfile;

import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.concurrent.TimeUnit;


public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "VerifyPhoneActivityTag";
    private ProgressBar mVerifyPB;
    private ProgressBar mResendPB;
    private EditText mVerifyCodeET;
    private Button mVerifyBTN;
    private Button mResendBTN;
    private String mPhoneNumber;

    private AlertDialog mWaitDialog;
    private String mVerificationId;
    private boolean mResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        Log.d(TAG, "onCreate: called");
        initViewsAndVars ();
        sendVerificationCode (mPhoneNumber);

    }

        private void initViewsAndVars() {
            mVerifyPB = findViewById(R.id.verify_PB);
            mResendPB = findViewById(R.id.resend_PB);
            mVerifyCodeET = findViewById(R.id.verify_code_ET);
            mVerifyBTN = findViewById(R.id.verify_BTN);
            mResendBTN = findViewById(R.id.resend_BTN);
            mPhoneNumber = getIntent().getStringExtra("phonenumber");
            Log.d(TAG, "initViewsAndVars: phonenumber : " + mPhoneNumber);
            mResend = false;
        }

        private void sendVerificationCode(String phoneNumber) {
            Log.d(TAG, "sendVerificationCode: called");
            blockingUI();
            verifyPhoneNumber(phoneNumber);
        }

            private void blockingUI() {
                Log.d(TAG, "blockingUI: called");
                if (mResend) {
                    mResendPB.setVisibility(View.VISIBLE);
                    mVerifyPB.setVisibility(View.VISIBLE);
                } else {
                    mVerifyPB.setVisibility(View.VISIBLE);
                    mResendPB.setVisibility(View.INVISIBLE);
                }
                mVerifyCodeET.setAlpha(0.5f);
                mVerifyCodeET.setEnabled(false);
                mVerifyBTN.setAlpha(0.5f);
                mVerifyBTN.setEnabled(false);
                mResendBTN.setAlpha(0.5f);
                mResendBTN.setEnabled(false);
                Log.d(TAG, "blockingUI: finished");
                showWaitDialog();
            }

                private void showWaitDialog() {
                    Log.d(TAG, "showWaitDialog: called");
                    mWaitDialog = new AlertDialog.Builder(VerifyPhoneActivity.this)
                            .setMessage("Verifying Code. Please Wait...")
                            .create();
                    mWaitDialog.show();
                }


                private void verifyPhoneNumber(String phoneNumber) {
                    Log.d(TAG, "verifyPhoneNumber: called");
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            45, /*to-do 271118 increase duration*/
                            TimeUnit.SECONDS,
                            TaskExecutors.MAIN_THREAD,
                            onVerificationStateChangedCallbacks
                    );
                }

                    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
                            onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                            super.onCodeSent(verificationId, forceResendingToken);
                            mVerificationId = verificationId;
                            Log.d(TAG, "onCodeSent: verificationId : " + verificationId);
                            //todo 271118 timer for 60 secs /*showTimer()*/
                            unblockUI();
                        }

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            String code = phoneAuthCredential.getSmsCode();
                            Log.d(TAG, "onVerificationCompleted: code : " + code);
                            if (code != null) {
                                mVerifyCodeET.setText(code);
                                verifyCode(code);
                            }
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            FancyToast.makeText(VerifyPhoneActivity.this,
                                    e.getMessage(),
                                    FancyToast.LENGTH_LONG,
                                    FancyToast.ERROR,
                                    false)
                                    .show();
                            unblockUI();
                            /*hideTimer()*/
                        }
                    };

                        private void unblockUI() {
                            mWaitDialog.dismiss();
                            if (mResend) {
                                mResendPB.setVisibility(View.INVISIBLE);
                            } else {
                                mVerifyPB.setVisibility(View.INVISIBLE);
                            }
                            mVerifyCodeET.setAlpha(1.0f);
                            mVerifyCodeET.setEnabled(true);
                            mVerifyBTN.setEnabled(true);
                            mVerifyBTN.setAlpha(1.0f);
                            mVerifyBTN.setOnClickListener(this);
                            mResendBTN.setEnabled(true);
                            mResendBTN.setAlpha(1.0f);
                            mResendBTN.setOnClickListener(this);
                        }

                        private void verifyCode(String code) {
                            PhoneAuthCredential credential =
                                    PhoneAuthProvider.getCredential(mVerificationId, code);
                            blockingUI();
                            signInWithCredential(credential);
                        }

                        private void signInWithCredential(PhoneAuthCredential credential) {
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                                final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                                Log.d(TAG, "onComplete: current_user.getUid() = " + current_user.getUid());
                                                UserProfile userProfile = new UserProfile(mPhoneNumber, "", false, null, current_user.getUid());
                                                UserLocation userLocation = new UserLocation();
                                                UserInformation userInformation = new UserInformation(userProfile, userLocation);
                                                firestore.collection("Users").document(current_user.getUid()).set(userInformation)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Intent intent = new Intent
                                                                            (VerifyPhoneActivity.this,
                                                                                    NameSubmitActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    finish();
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });

                                            } else {
                                                FancyToast.makeText(VerifyPhoneActivity.this,
                                                        task.getException().getMessage(),
                                                        FancyToast.LENGTH_LONG,
                                                        FancyToast.ERROR,
                                                        false)
                                                        .show();
                                                unblockUI();
                                            }
                                        }
                                    });
                        }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.verify_BTN:
                String code = mVerifyCodeET.getText().toString().trim();
                if (code.isEmpty() || code.length() != 6) {
                    mVerifyCodeET.setError("Enter code..");
                    mVerifyCodeET.requestFocus();
                    return;
                }
                mVerifyPB.setVisibility(View.VISIBLE);
                verifyCode(code);
            break;

            case R.id.resend_BTN:
                mResend = true;
                sendVerificationCode (mPhoneNumber);

        }
    }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            finish();
        }
}
