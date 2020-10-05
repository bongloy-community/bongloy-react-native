import { NativeModules } from 'react-native';
import errorCodes from './errorCodes'

const { BongloyModule } = NativeModules;

class Bongloy {
  bongloyInitialized = false

  setDefaultPublishableKey = (options: {}) => {
    this.bongloyInitialized = true
    return BongloyModule.init(options, errorCodes)
  }

  createToken = (params = {}) => {
    return BongloyModule.createTokenWithCard(params)
  }
}

export default new Bongloy()