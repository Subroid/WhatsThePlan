package platinum.whatstheplan.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.Guest;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.models.Venue;
import platinum.whatstheplan.utils.BookingDbHandler;
import platinum.whatstheplan.utils.PaymentsUtil;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivityTag";
    private PaymentsClient mPaymentsClient;
    private Button mBhimPaymentButton;
    private TextView mGooglePayStatusText;
    private RestaurantVenue mVenue;
    private Event mEvent;
    private EditText mPaymentET;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private static final int BHIM_PAYMENT_REQUEST_CODE = 992;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        // initialize a Google Pay API client for an environment suitable for testing
        mPaymentsClient =
                Wallet.getPaymentsClient(
                        this,
                        new Wallet.WalletOptions.Builder()
                                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());

        possiblyShowGooglePayButton();

        mVenue = getIntent().getParcelableExtra("venue");
        mEvent = getIntent().getParcelableExtra("event");
        mBhimPaymentButton = findViewById(R.id.google_pay_button);
        mGooglePayStatusText = findViewById(R.id.google_pay_status_TV);
        mPaymentET = findViewById(R.id.payment_ET);


        mBhimPaymentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String payableAmount = mPaymentET.getText().toString();
                        if (payableAmount.isEmpty()) {
                            FancyToast.makeText(getApplicationContext(),
                                    "Amount cannot be blank",
                                    FancyToast.LENGTH_LONG,
                                    FancyToast.ERROR,
                                    false)
                                    .show();
                        } else {
                            requestPaymentUsingBhim(payableAmount);
                        }
//                        requestPayment(view);
                    }
                }
        );
    }

        /**
         * Determine the viewer's ability to pay with a payment method supported by your app and display a
         * Google Pay payment button.
         *
         * @see <a href=
         *     "https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
         */
        private void possiblyShowGooglePayButton() {
            final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
            if (!isReadyToPayJson.isPresent()) {
                return;
            }
            IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
            if (request == null) {
                return;
            }

            // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
            // OnCompleteListener to be triggered when the result of the call is known.
            Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
            task.addOnCompleteListener(
                    new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            try {
                                boolean result = task.getResult(ApiException.class);
                                setGooglePayAvailable(result);
                            } catch (ApiException exception) {
                                // Process error
                                Log.w("isReadyToPay failed", exception);
                            }
                        }
                    });
        }

            /**
             * If isReadyToPay returned {@code true}, show the button and hide the "checking" text. Otherwise,
             * notify the user that Google Pay is not available. Please adjust to fit in with your current
             * user flow. You are not required to explicitly let the user know if isReadyToPay returns {@code
             * false}.
             *
             * @param available isReadyToPay API response.
             */
            private void setGooglePayAvailable(boolean available) {
                if (available) {
                    mGooglePayStatusText.setVisibility(View.GONE);
                    mBhimPaymentButton.setVisibility(View.VISIBLE);
                } else {
                    mGooglePayStatusText.setText(R.string.googlepay_status_unavailable);
                }
            }

        // This method is called when the Pay with Google button is clicked.
        public void requestPayment(View view) {
            // Disables the button to prevent multiple clicks.
            mBhimPaymentButton.setClickable(false);

            // The price provided to the API should include taxes and shipping.
            // This price is not displayed to the user.
            String price = "₹500";

            // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
            Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
            if (!paymentDataRequestJson.isPresent()) {
                return;
            }
            PaymentDataRequest request =
                    PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
            // AutoResolveHelper to wait for the user interacting with it. Once completed,
            // onActivityResult will be called with the result.
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        }

    private void requestPaymentUsingBhim(String amount) {
        Uri uri = Uri.parse("upi://pay?pa=swatisurjuse137@okicici&pn=Whats%20The%20Plan&tn=Payment%20for%20Booking&am=" + amount + "&cu=INR&url=https://mystar.co"); // missing 'http://' will cause crashed
        Log.d(TAG, "onClick: uri: "+uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivityForResult(intent,BHIM_PAYMENT_REQUEST_CODE);
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode Result code returned by the Google Pay API.
     * @param data Intent from the Google Pay API containing payment or error data.
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     *     from an Activity</a>
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: isResultCodeOK = " + (resultCode == RESULT_OK));
        Log.d(TAG, "onActivityResult: data.getExtras().toString() = " + data.getExtras().toString());
        switch (requestCode) {

            case BHIM_PAYMENT_REQUEST_CODE :
                if (resultCode == RESULT_OK && data.getExtras().toString().equals("Bundle[mParcelledData.dataSize=212]")) {
//                    saveVenueBookingLocally (mVenue);
                    if (mVenue != null){
                        saveVenueBookingRemotely(mVenue);
                    } else if (mEvent != null) {
                        saveEventBookingRemotely (mEvent);
                    }
                } else {
                    FancyToast.makeText(this, "Payment Failed", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
                break;
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                mBhimPaymentButton.setClickable(true);
                break;

        }
    }

    private void saveEventBookingRemotely(final Event event) {
        final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();

        final CollectionReference dbRefEventType = dbFirestore.collection("Parties");
        Log.d(TAG, "saveEventBookingRemotely: path : " + dbRefEventType.getPath());
        Log.d(TAG, "saveEventBookingRemotely: eventid : " + event.getEvent_id());
        Log.d(TAG, "saveEventBookingRemotely: adminid : " + event.getAdmin_id());
        Log.d(TAG, "saveEventBookingRemotely: dbRefEvent Path : " + dbRefEventType.document(event.getEvent_id()).toString());
        final DocumentReference dbRefEvent = dbRefEventType.document(event.getEvent_id());
        dbRefEvent.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final DocumentReference dbRefAdmin = dbFirestore.collection("Admins")
                        .document(event.getAdmin_id());
                CollectionReference dbRefBookingsAdminSide = dbRefAdmin.collection("Bookings");
                CollectionReference dbRefGuests = dbRefBookingsAdminSide.document(event.getEvent_id()).collection("Guests");
                dbRefGuests.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(getGuestInstance())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final CollectionReference dbRefBookingsClientSide = dbFirestore.collection("Users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Bookings");
                                dbRefBookingsClientSide.document(event.getEvent_id()).set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        final CollectionReference dbRefBookingsByDateClientSide = dbFirestore.collection("Users")
                                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .collection("Bookings By Date");
                                        final Map<String, Object> data = new HashMap<>();
                                        data.put("bookings", true);
                                        dbRefBookingsClientSide.document(event.getEvent_date()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dbRefBookingsByDateClientSide.document(event.getEvent_date()).collection("Parties Bookings on " + event.getEvent_date()).document(event.getEvent_id()).set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Bookings by Date").child(event.getEvent_date()).child("Parties Bookings on " + event.getEvent_date()).child(event.getEvent_id()).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                FancyToast.makeText(PaymentActivity.this, "Event booked successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });

                                    }

                                });
                            }
                        });

            }

            private Guest getGuestInstance() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Guest guest =  new Guest(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid());
                return guest;
            }

        });
    }

    private void saveVenueBookingLocally(RestaurantVenue venue) {

        BookingDbHandler bookingDbHandler = new BookingDbHandler(this);
        bookingDbHandler.addRestaurantVenue(venue);
    }

    private void saveVenueBookingRemotely(final RestaurantVenue venue) {
        final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();

        final CollectionReference dbRefVenueType = dbFirestore.collection("Foods DrinksVenues");
        Log.d(TAG, "saveVenueBookingRemotely: path : " + dbRefVenueType.getPath());
        Log.d(TAG, "saveVenueBookingRemotely: venueid : " + venue.getVenue_id());
        Log.d(TAG, "saveVenueBookingRemotely: adminid : " + venue.getAdmin_id());
        Log.d(TAG, "saveVenueBookingRemotely: dbRefVenue Path : " + dbRefVenueType.document(venue.getVenue_id()).toString());
        final DocumentReference dbRefVenue = dbRefVenueType.document(venue.getVenue_id());
        dbRefVenue.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final DocumentReference dbRefAdmin = dbFirestore.collection("Admins")
                        .document(venue.getAdmin_id());
                CollectionReference dbRefBookingsAdminSide = dbRefAdmin.collection("Bookings");
                CollectionReference dbRefGuests = dbRefBookingsAdminSide.document(venue.getVenue_id()).collection("Guests");
                dbRefGuests.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(getGuestInstance())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final CollectionReference dbRefBookingsClientSide = dbFirestore.collection("Users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Bookings");
                                dbRefBookingsClientSide.document(venue.getVenue_id()).set(venue).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        final CollectionReference dbRefBookingsByDateClientSide = dbFirestore.collection("Users")
                                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .collection("Bookings By Date");
                                        final Map<String, Object> data = new HashMap<>();
                                        data.put("bookings", true);
                                        dbRefBookingsClientSide.document(venue.getVenue_date()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dbRefBookingsByDateClientSide.document(venue.getVenue_date()).collection("Restaurants Bookings on " + venue.getVenue_date()).document(venue.getVenue_id()).set(venue).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Bookings by Date").child(venue.getVenue_date()).child("Restaurants Bookings on " + venue.getVenue_date()).child(venue.getVenue_id()).setValue(venue).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                FancyToast.makeText(PaymentActivity.this, "Restaurant booked successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });

                                    }

                                });
                            }
                        });

            }

            private Guest getGuestInstance() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Guest guest =  new Guest(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid());
                return guest;
            }

        });
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#PaymentData">Payment
     *     Data</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            FancyToast.makeText(this, getString(R.string.payments_show_name) + ", " + billingName, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false)
                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }


    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     *     WalletConstants.ERROR_CODE_* constants.
     * @see <a
     *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants#constant-summary">
     *     Wallet Constants Library</a>
     */
    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }



}
