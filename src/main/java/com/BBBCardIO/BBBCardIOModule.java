package com.BBB.BBBCardIO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

import java.util.Map;
import java.util.HashMap;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class BBBCardIOModule extends ReactContextBaseJavaModule implements ActivityEventListener {


    ReactApplicationContext reactContext;
    private static final int MY_SCAN_REQUEST_CODE = 100;

    public static final String REACT_CLASS = "BBBCardIO";

    Promise promise;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public BBBCardIOModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("DETECTION_MODE", "DOOP");
        return constants;
    }

    @ReactMethod
    public void showScanner(Boolean requireExpiry, Boolean requireCvv, Boolean requirePostalCode, Boolean suppressManualEntry, Promise promise) {
      this.promise = promise;

      Activity currentActivity = getCurrentActivity();

      Intent scanIntent = new Intent(this.reactContext, CardIOActivity.class);

      scanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      // customize these values to suit your needs.
      scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, requireExpiry); // default: false
      scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, requireCvv); // default: false
      scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, requirePostalCode); // default: false

      // hides the manual entry button
      // if set, developers should provide their own manual entry mechanism in the app
      scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, suppressManualEntry); // default: false

      // matches the theme of your application
      scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false

      //Check that an app exists to receive the intent
      if (scanIntent.resolveActivity(this.reactContext.getPackageManager()) != null) {

          // this.reactContext.addActivityEventListener()
          currentActivity.startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
      }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == MY_SCAN_REQUEST_CODE) {

        String resultDisplayStr;
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            this.promise.resolve(scanResult.getFormattedCardNumber());
        }
        else {
        //    this.promise.reject("Scan was canceled.");
        }
      }
    }
}
