package platinum.whatstheplan.activities.authentications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.shashank.sony.fancytoastlib.FancyToast;

import platinum.whatstheplan.R;

public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneAuthActivityTag";
    private EditText mPhoneNumberET;
    private Button mSendOtpBTN;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        initViewsAndVars ();

    }

        private void initViewsAndVars() {
            mPhoneNumberET = findViewById(R.id.phone_number_ET);
            mSendOtpBTN = findViewById(R.id.send_otp_BTN);
            mSendOtpBTN.setOnClickListener(PhoneAuthActivity.this);

        }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_otp_BTN :
                mPhoneNumber = mPhoneNumberET.getText().toString();
                if (mPhoneNumber.isEmpty() || mPhoneNumber.length() != 10) {
                    FancyToast.makeText(getApplicationContext(),
                            "Please type valid Phone Number",
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false)
                            .show();
                } else {
                    hideSoftKeyboard(PhoneAuthActivity.this, view);
                    mPhoneNumber = "+91" + mPhoneNumber;
                    goToNewActivity (VerifyPhoneActivity.class);
                }

        }

    }

        private void goToNewActivity(Class classname) {
            Intent intent = new Intent(this, classname);
            intent.putExtra("phonenumber", mPhoneNumber);
            startActivity(intent);
        }

        public void hideSoftKeyboard(Context context, View view) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
