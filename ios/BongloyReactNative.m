#import "BongloyReactNative.h"

NSString * const kErrorKeyCode = @"errorCode";
NSString * const kErrorKeyDescription = @"description";
NSString * const kErrorKeyApi = @"api";
NSString * const kErrorKeyBusy = @"busy";

@implementation BongloyModule {
    NSString *publishableKey;
    NSDictionary *errorCodes;
    BOOL requestIsCompleted;
    RCTPromiseResolveBlock promiseResolver;
    RCTPromiseRejectBlock promiseRejector;
}

- (instancetype)init {
    if ((self = [super init])) {
        requestIsCompleted = YES;
    }
    return self;
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(init:(NSDictionary *)options errorCodes:(NSDictionary *)errors) {
    publishableKey = options[@"publishableKey"];
    errorCodes = errors;
    [Stripe setDefaultPublishableKey:publishableKey];
}

RCT_EXPORT_METHOD(createTokenWithCard:(NSDictionary *)params
                                resolver:(RCTPromiseResolveBlock)resolve
                                rejecter:(RCTPromiseRejectBlock)reject) {
    if(!requestIsCompleted) {
        NSDictionary *error = [errorCodes valueForKey:kErrorKeyBusy];
        reject(error[kErrorKeyCode], error[kErrorKeyDescription], nil);
        return;
    }

    requestIsCompleted = NO;
    promiseResolver = resolve;
    promiseRejector = reject;

    STPCardParams *cardParams = [self createCard:params];

    BongloyAPIClient *bongloyAPIClient = [self newAPIClient];

    [bongloyAPIClient createTokenWithCard:cardParams completion:^(STPToken *token, NSError *error) {
        requestIsCompleted = YES;

        if (error) {
            NSDictionary *jsError = [errorCodes valueForKey:kErrorKeyApi];
            [self rejectPromiseWithCode:jsError[kErrorKeyCode] message:error.localizedDescription];
        } else {
            resolve([self convertTokenObject:token]);
        }
    }];
}

#pragma mark - Private

- (STPCardParams *)createCard:(NSDictionary *)params {
    STPCardParams *cardParams = [[STPCardParams alloc] init];

    [cardParams setNumber: params[@"number"]];
    [cardParams setExpMonth: [params[@"expMonth"] integerValue]];
    [cardParams setExpYear: [params[@"expYear"] integerValue]];
    [cardParams setCvc: params[@"cvc"]];

    [cardParams setCurrency: params[@"currency"]];
    [cardParams setName: params[@"name"]];
    [cardParams setAddress: params[@"addressLine1"]];

    return cardParams;
}

- (void)rejectPromiseWithCode:(NSString *)code message:(NSString *)message {
    if (promiseRejector) {
        promiseRejector(code, message, nil);
    }
    [self resetPromiseCallbacks];
}

- (void)resetPromiseCallbacks {
    promiseResolver = nil;
    promiseRejector = nil;
}

#pragma mark PKPaymentAuthorizationViewControllerDelegate

- (BongloyAPIClient *)newAPIClient {
    return [[BongloyAPIClient alloc] initWithPublishableKey:[Stripe defaultPublishableKey]];
}

- (NSDictionary *)convertTokenObject:(STPToken*)token {
    NSMutableDictionary *result = [@{} mutableCopy];

    // Token
    [result setValue:token.tokenId forKey:@"id"];
    [result setValue:@([token.created timeIntervalSince1970]) forKey:@"created"];
    [result setValue:@(token.livemode) forKey:@"livemode"];

    NSMutableDictionary *card = [@{} mutableCopy];
    [result setValue:card forKey:@"card"];

    [card setValue:token.card.stripeID forKey:@"id"];

    [card setValue:[self cardBrand:token.card.brand] forKey:@"brand"];
    [card setValue:token.card.last4 forKey:@"last4"];
    [card setValue:@(token.card.expMonth) forKey:@"expMonth"];
    [card setValue:@(token.card.expYear) forKey:@"expYear"];
    [card setValue:token.card.currency forKey:@"currency"];
    [card setValue:token.card.name forKey:@"name"];
    [card setValue:token.card.address forKey:@"address"];

    return result;
}

- (NSString *)cardBrand:(STPCardBrand)inputBrand {
    switch (inputBrand) {
        case STPCardBrandJCB:
            return @"JCB";
        case STPCardBrandAmex:
            return @"American Express";
        case STPCardBrandVisa:
            return @"Visa";
        case STPCardBrandUnionPay:
            return @"UnionPay";
        case STPCardBrandDiscover:
            return @"Discover";
        case STPCardBrandDinersClub:
            return @"Diners Club";
        case STPCardBrandMasterCard:
            return @"MasterCard";
        default:
            return @"Unknown";
    }
}

@end
