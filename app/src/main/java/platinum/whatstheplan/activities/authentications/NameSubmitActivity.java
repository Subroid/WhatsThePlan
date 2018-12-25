package platinum.whatstheplan.activities.authentications;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.HomeActivity;
import platinum.whatstheplan.models.User;

public class NameSubmitActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "NameSubmitActivityTag";
    TextInputEditText firstnameTIET;
    TextInputEditText lastnameTIET;
    Button submitBTN;
    String mFirstNameString;
    String mLastNameString;
    String mDocumentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_submit);

        firstnameTIET = findViewById(R.id.fistnameTIET);
        lastnameTIET = findViewById(R.id.lastnameTIET);
        submitBTN = findViewById(R.id.submitBTN);
        submitBTN.setOnClickListener(this);

        if (getIntent() != null) {
           mDocumentId = getIntent().getStringExtra("documentId");
        }
    }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.submitBTN :
                        mFirstNameString = firstnameTIET.getText().toString();
                        mLastNameString = lastnameTIET.getText().toString();
                        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG, "onClick: current_user.getUid() = " + current_user.getUid());
                    FirebaseFirestore.getInstance().collection("Users")
                            .document(mDocumentId).update("name", mFirstNameString + " " + mLastNameString);
                        Intent homeIntent = new Intent(NameSubmitActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                    break;
            }
        }
}
