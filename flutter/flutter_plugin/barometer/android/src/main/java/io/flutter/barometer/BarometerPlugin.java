package io.flutter.barometer;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import java.lang.IllegalArgumentException;

/** BarometerPlugin */
public class BarometerPlugin implements MethodCallHandler {
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "barometer");
    channel.setMethodCallHandler(new BarometerPlugin());
  }

  double getBarometer() {
    return 750.0;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      return;
    } else if (call.method.equals("getBarometer"))  {
      throw new IllegalArgumentException();
//      double reading = getBarometer();
//      result.success(reading);
//      return;

    } else {
      result.notImplemented();
    }
  }
}
