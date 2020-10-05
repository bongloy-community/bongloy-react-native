package com.bongloyreactnative;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.stripe.android.Bongloy;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

import static com.bongloyreactnative.Converters.convertTokenToWritableMap;
import static com.bongloyreactnative.Converters.createCard;
import static com.bongloyreactnative.Errors.toErrorCode;

/**
 * Created by khom sovon on 02/10/2020.
 */

class BongloyModule extends ReactContextBaseJavaModule {
    private String mPublicKey;
    private ReadableMap mErrorCodes;

    public static final String PUBLISHABLE_KEY = "publishableKey";
    private static final String MODULE_NAME = BongloyModule.class.getSimpleName();

    private static ReactApplicationContext reactContext;

    BongloyModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @ReactMethod
    public void init(@NonNull ReadableMap options, @NonNull ReadableMap errorCodes) {

        String newPubKey = Converters.getStringOrNull(options, PUBLISHABLE_KEY);

        if (newPubKey != null && !TextUtils.equals(newPubKey, mPublicKey)) {
            mPublicKey = newPubKey;
            new Bongloy(reactContext).setDefaultPublishableKey(mPublicKey);
        }

        if (mErrorCodes == null) {
            mErrorCodes = errorCodes;
        }
    }

    @ReactMethod
    public void createTokenWithCard(final ReadableMap cardData, final Promise promise) {
        try {
            new Bongloy(reactContext).createToken(
                createCard(cardData),
                mPublicKey,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        promise.resolve(convertTokenToWritableMap(token));
                    }
                    public void onError(Exception error) {
                        error.printStackTrace();
                        promise.reject(toErrorCode(error), error.getMessage());
                    }
                });
        }catch (Exception e){
           promise.reject(toErrorCode(e), e.getMessage());
        }
    }

    @NonNull
    @Override
    public String getName() {
        return MODULE_NAME;
    }
}
