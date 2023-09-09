package cnt.pqh.BGServices.ServiceInstance;

import android.content.Context;
import android.util.Log;

import cnt.pqh.BGServices.Globals;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TimerTask;

import asim.sdk.common.Utils;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.locker.SDKLocker;
import asim.sdk.tempandhum.SDKTemperatureAndHumidity;
import asim.sdk.tempandhum.TempHumiData;

public class SensorService {
    public static TimerTask getTimerTask(String m_androidId, Context context) {
        return new TimerTask() {
            public void run() {
                Log.i("Supervisor", "======Collect data from Sensor=====");
                SDKTemperatureAndHumidity tempHuSDK = new SDKTemperatureAndHumidity();

                TempHumiData temphuData = null;

                JSONObject body = new JSONObject();
                JSONObject savedTempHuData = new JSONObject();

                List<DeviceInfo> devices = SDKLocker.getAllUsbDevicesHasDriver(context);
                int maxTry = 0;
                while(maxTry < 5 && temphuData == null) {
                    for (DeviceInfo each : devices) {
                        Log.d("USBDriverDevice",each.device.getDeviceName()+" " + each.port);
                        if (Globals.temphuDeviceId == -1 && each.device.getVendorId() == Globals.temphuVendorId && each.device.getProductId() == Globals.temphuProductId) {
                            boolean connect = tempHuSDK.connect(context, each, Globals.temphuBaurate);
                            if (connect) {
                                temphuData = tempHuSDK.getTempHumiData();
                                tempHuSDK.disconnect();
                            }

                            continue;
                        }

                        if (Globals.temphuDeviceId != -1 && each.device.getVendorId() == Globals.temphuVendorId && each.device.getProductId() == Globals.temphuProductId && Utils.compareTwoDeviceId(each.device.getDeviceId(), Globals.temphuDeviceId)) {
                            boolean connect = tempHuSDK.connect(context, each, Globals.temphuBaurate);
                            if (connect) {
                                temphuData = tempHuSDK.getTempHumiData();
                            }
                        }
                    }
                    Utils.sleep(2);
                    maxTry += 1;
                }


                if (temphuData != null) {
                    try {
                        savedTempHuData.put("temperature", temphuData.temperature);
                        savedTempHuData.put("humidity", temphuData.humidity);
                        savedTempHuData.put("dewdrop", temphuData.dewdrop);
                        Log.d("BGS Temperature",savedTempHuData.toString());
                        body.put("temphu", savedTempHuData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("==Sensor collecting===", "No information found for Sensor");
                    try {
                        savedTempHuData.put("temperature", 0);
                        savedTempHuData.put("humidity", 0);
                        savedTempHuData.put("dewdrop", 0);
                        body.put("temphu", savedTempHuData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
