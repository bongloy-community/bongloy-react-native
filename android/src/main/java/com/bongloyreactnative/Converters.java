package com.bongloyreactnative;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

/**
 * Created by khom sovon on 02/10/2020.
 */

public class Converters {
    public static WritableMap convertTokenToWritableMap(Token token) {
        WritableMap newToken = Arguments.createMap();

        if (token == null) return newToken;

        newToken.putString("id", token.getId());
        newToken.putBoolean("livemode", token.getLivemode());
        newToken.putDouble("created", token.getCreated().getTime());
        newToken.putMap("card", convertCardToWritableMap(token.getCard()));

        return newToken;
    }

    private static WritableMap convertCardToWritableMap(final Card card) {
        WritableMap result = Arguments.createMap();

        if (card == null) return result;

        result.putString("id", card.getId());
        result.putInt("expMonth", card.getExpMonth() );
        result.putInt("expYear", card.getExpYear() );
        result.putString("name", card.getName() );
        result.putString("addressLine1", card.getAddressLine1() );
        result.putString("addressLine2", card.getAddressLine2() );
        result.putString("addressCity", card.getAddressCity() );
        result.putString("addressState", card.getAddressState() );
        result.putString("addressZip", card.getAddressZip() );
        result.putString("addressCountry", card.getAddressCountry() );
        result.putString("last4", card.getLast4() );
        result.putString("brand", card.getBrand() );
        result.putString("funding", card.getFunding() );
        result.putString("currency", card.getCurrency() );

        return result;
    }

    public static String getValue(final ReadableMap map, final String key, final String def) {
        if (map.hasKey(key)) {
            return map.getString(key);
        } else {
            // If map don't have some key - we must pass to constructor default value.
            return def;
        }
    }

    public static String getValue(final ReadableMap map, final String key) {
        return getValue(map, key, (String) null);
    }

    public static Card createCard(final ReadableMap cardData) {
        return new Card(
                // required fields
                cardData.getString("number"),
                cardData.getInt("expMonth"),
                cardData.getInt("expYear"),
                // additional fields
                getValue(cardData, "cvc"),
                getValue(cardData, "name"),
                getValue(cardData, "addressLine1"),
                getValue(cardData, "addressLine2"),
                getValue(cardData, "addressCity"),
                getValue(cardData, "addressState"),
                getValue(cardData, "addressZip"),
                getValue(cardData, "addressCountry"),
                getValue(cardData, "brand"),
                getValue(cardData, "last4"),
                getValue(cardData, "fingerprint"),
                getValue(cardData, "funding"),
                getValue(cardData, "country"),
                getValue(cardData, "currency"),
                getValue(cardData, "id")
        );
    }

    public static String getStringOrNull(@NonNull ReadableMap map, @NonNull String key) {
        return map.hasKey(key) ? map.getString(key) : null;
    }
}
