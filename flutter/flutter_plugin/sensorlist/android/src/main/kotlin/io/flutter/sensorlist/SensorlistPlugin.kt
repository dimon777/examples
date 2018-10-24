package io.flutter.sensorlist

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class SensorlistPlugin private constructor(//? = null
        private var activity: Activity) : MethodCallHandler {

  private lateinit var mSensorManager: SensorManager

  // Get activity into pluging code: https://github.com/flutter/flutter/issues/10769
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "sensorlist")
      //channel.setMethodCallHandler(SensorlistPlugin())
      channel.setMethodCallHandler(SensorlistPlugin(registrar.activity()))
    }
  }

//  private constructor(activity: Activity) {
//    this.activity = activity
//  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "getSensorsList") {

      val ctx = activity.applicationContext
      mSensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
      val deviceSensors: List<Sensor> = mSensorManager.getSensorList(Sensor.TYPE_ALL)
      val sensors = mutableListOf<String>()
      print ("Device sensors $deviceSensors")
      for (v in deviceSensors) {
//        val x: String? = v as? String
        sensors.add(v.name) //K2HH Acceleration , STM // v.stringType android.sensor.accelerometer
        print("Plaftorm Sensor: $v")
      }
      result.success(sensors) //"got whole list")
    } else {
      result.notImplemented()
    }
  }
}
