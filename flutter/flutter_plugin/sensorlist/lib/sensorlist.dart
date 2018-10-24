import 'dart:async';

import 'package:flutter/services.dart';

class Sensorlist {
  static const MethodChannel _channel = const MethodChannel('sensorlist');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<List<dynamic>> get sensorsList async {
    final List<dynamic> sensors = await _channel.invokeMethod('getSensorsList');
    return sensors;
  }

}
