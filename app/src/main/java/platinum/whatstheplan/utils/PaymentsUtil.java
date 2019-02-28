package platinum.whatstheplan.utils;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class PaymentsUtil {

    private PaymentsUtil() {};


    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#IsReadyToPayRequest">IsReadyToPayRequest</a>
     */
    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }


        private static JSONObject getBaseRequest() throws JSONException {
            return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
        }

        /**
         * Describe your app's support for the CARD payment method.
         *
         * <p>The provided properties are applicable to both an IsReadyToPayRequest and a
         * PaymentDataRequest.
         *
         * @return A CARD PaymentMethod object describing accepted cards.
         * @throws JSONException
         * @see <a
         *     href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
         */
        private static JSONObject getBaseCardPaymentMethod() throws JSONException {
            JSONObject cardPaymentMethod = new JSONObject();
            cardPaymentMethod.put("type", "CARD");

            JSONObject parameters = new JSONObject();
            parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
            parameters.put("allowedCardNetworks", getAllowedCardNetworks());
            // Optionally, you can add billing address/phone number associated with a CARD payment method.
            parameters.put("billingAddressRequired", true);

            JSONObject billingAddressParameters = new JSONObject();
            billingAddressParameters.put("format", "FULL");

            parameters.put("billingAddressParameters", billingAddressParameters);

            cardPaymentMethod.put("parameters", parameters);

            return cardPaymentMethod;
        }

            /**
             * Card authentication methods supported by your app and your gateway.
             *
             * <p>TODO: Confirm your processor supports Android device tokens on your supported card networks
             * and make updates in Constants.java.
             *
             * @return Allowed card authentication methods.
             * @see <a
             *     href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
             */
            private static JSONArray getAllowedCardAuthMethods() {
                return new JSONArray(ConstantsPayment.SUPPORTED_METHODS);
            }

            /**
             * Card networks supported by your app and your gateway.
             *
             * <p>TODO: Confirm card networks supported by your app and gateway & update in Constants.java.
             *
             * @return Allowed card networks
             * @see <a
             *     href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
             */
            private static JSONArray getAllowedCardNetworks() {
                return new JSONArray(ConstantsPayment.SUPPORTED_NETWORKS);
            }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#PaymentDataRequest">PaymentDataRequest</a>
     */
    public static Optional<JSONObject> getPaymentDataRequest(String price) {
        try {
            JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", PaymentsUtil.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
            paymentDataRequest.put("shippingAddressRequired", true);

            JSONObject shippingAddressParameters = new JSONObject();
            shippingAddressParameters.put("phoneNumberRequired", false);

            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
            return Optional.of(paymentDataRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardPaymentMethod;
    }

    /**
     * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
     *
     * <p>The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     * <p>TODO: Check with your gateway on the parameters to pass and modify them in Constants.java.
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see <a href=
     *     "https://developers.google.com/pay/api/android/reference/object#PaymentMethodTokenizationSpecification">PaymentMethodTokenizationSpecification</a>
     */
    private static JSONObject getGatewayTokenizationSpecification()
            throws JSONException, RuntimeException {
        if (ConstantsPayment.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS.isEmpty()) {
            throw new RuntimeException(
                    "Please edit the Constants.java file to add gateway name and other parameters your "
                            + "processor requires");
        }
        JSONObject tokenizationSpecification = new JSONObject();

        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        JSONObject parameters = new JSONObject(ConstantsPayment.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS);
        tokenizationSpecification.put("parameters", parameters);

        return tokenizationSpecification;
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#TransactionInfo">TransactionInfo</a>
     */
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", ConstantsPayment.CURRENCY_CODE);

        return transactionInfo;
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#MerchantInfo">MerchantInfo</a>
     */
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "What's The Plan");
    }

}
