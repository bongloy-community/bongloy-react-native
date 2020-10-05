import React, { Component } from 'react';
import { Button, View } from 'react-native';
import bongloy from 'bongloy-react-native';

class App extends Component {

  state = {
    token: null,
    error: null,
    params: {
      number: '6200000000000005',
      expMonth: 12,
      expYear: 24,
      cvc: '123',
    }
  }

  constructor(props: any) {
    super(props);
  }

  handlePayPress = async () => {
    try {
      bongloy.setDefaultPublishableKey({publishableKey: "pk_test_69bf785ab0e264c9b6b081040ea460eaf79833ae2219e57f1cc3379c26955c1a"});
      this.setState({ token: null, error: null })
      const token = await bongloy.createToken(this.state.params)
      this.setState({ loading: false, error: undefined, token })
      console.log(token);
    } catch (error) {
      this.setState({ loading: false, error })
      console.log(error);
    }
  }

  render() {
    return (
      <View>
        <Button title="Pay"
          onPress={this.handlePayPress}
          color="#96BF63"
          accessibilityLabel="Pay button"
        />
      </View>
    )
  }
}

export default App;