# bongloy-react-native

This is the Unofficial React Native library for Bongloy Payment Gateway API.

## Installation

```sh
npm install bongloy-react-native --save
# or
yarn add bongloy-react-native
```


## Usage

The package needs to be configured with your account's secret key, which is
available in the [Bongloy Dashboard](https://sandbox.bongloy.com/dashboard/api_keys). Require it with the key's
value:

<!-- prettier-ignore -->
```js
import bongloy from 'bongloy-react-native';

state = {
    params: {
      number: '6200000000000005',
      expMonth: 12,
      expYear: 24,
      cvc: '123',
    }
}

handlePayPress = async () => {
    try {
        bongloy.setDefaultPublishableKey({publishableKey: "pk_test_..."});
        const token = await bongloy.createToken(this.state.params)
        console.log(token);
    } catch (error) {
        console.log(error);
    }
}
```

## Documentation

See the [API docs](https://sandbox.bongloy.com/documentation).
