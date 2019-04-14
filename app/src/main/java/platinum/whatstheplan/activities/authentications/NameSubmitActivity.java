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

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.HomeActivity;
import platinum.whatstheplan.models.UserInformation;
import platinum.whatstheplan.models.UserLocation;
import platinum.whatstheplan.models.UserProfile;

public class NameSubmitActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "NameSubmitActivityTag";
    TextInputEditText firstnameTIET;
    TextInputEditText lastnameTIET;
    Button submitBTN;
    String mFirstNameString;
    String mLastNameString;
    String mDocumentId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_submit);

        firstnameTIET = findViewById(R.id.fistnameTIET);
        lastnameTIET = findViewById(R.id.lastnameTIET);
        submitBTN = findViewById(R.id.submitBTN);
        submitBTN.setOnClickListener(this);
        progressBar = findViewById(R.id.progressbarPB);

    }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.submitBTN :
                    mFirstNameString = firstnameTIET.getText().toString();
                    mLastNameString = lastnameTIET.getText().toString();
                    if (mFirstNameString.isEmpty() || mLastNameString.isEmpty()) {
                        FancyToast.makeText(getApplicationContext(),
                                "Please type First/Last name",
                                FancyToast.LENGTH_LONG,
                                FancyToast.ERROR,
                                false)
                                .show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mFirstNameString + " "  + mLastNameString)
                                .build();
                        current_user.updateProfile(userProfileChangeRequest);
                        Log.d(TAG, "onClick: current_user.getUid() = " + current_user.getUid());

                    /*FirebaseFirestore.getInstance().collection("Users")
                            .document(current_user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            *//*if (task.isSuccessful() && task.getResult() != null) {
                                UserInformation userInformationResult = task.getResult().toObject(UserInformation.class);
                                UserProfile userProfile = new UserProfile();
                                userProfile.setEvent_name(userInformationResult.getUserProfile().getEvent_name());
                                userProfile.setEmail(userInformationResult.getUserProfile().getEmail());
                                userProfile.setAdmin(userInformationResult.getUserProfile().isAdmin());
                                userProfile.setPassword(userInformationResult.getUserProfile().getPassword());
                                userProfile.setUid(userInformationResult.getUserProfile().getUid());
                                UserLocation userLocation = new UserLocation();
                                userLocation.setGeoPoint(userInformationResult.getUserLocation().getGeoPoint());
                                userLocation.setTimeStamp(userInformationResult.getUserLocation().getTimeStamp());*//*

//                                UserInformation userInformation = new UserInformation(userProfile, userLocation);

//                            }
                        }
                    });*/

                        FirebaseFirestore.getInstance().collection("Users")
                                .document(current_user.getUid()).update(
                                "userProfile.name",
                                mFirstNameString + " " + mLastNameString).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent homeIntent = new Intent(NameSubmitActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                                finish();
                            }
                        });
                    }
                    break;
            }
        }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
