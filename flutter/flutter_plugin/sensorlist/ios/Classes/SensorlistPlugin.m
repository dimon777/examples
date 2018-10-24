#import "SensorlistPlugin.h"
#import <sensorlist/sensorlist-Swift.h>

@implementation SensorlistPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSensorlistPlugin registerWithRegistrar:registrar];
}
@end
