package cnt.pqh.BGServices.ServiceInstance;

import android.content.Context;
import android.util.Log;
import asim.sdk.common.Utils;
import asim.sdk.ups.SDKUPS;
import asim.sdk.ups.UPSModel;
import cnt.pqh.BGServices.Globals;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

public class UPSService {

    public static  TimerTask getTimerTask(String m_androidId, Context context) {
        return new TimerTask() {
            public void run() {
                Log.i("Supervisor", "======Collect data from UPS=====");
                SDKUPS sdkups = new SDKUPS(Globals.upsComName, Globals.upsBaurate);
                UPSModel upsData = null;
                if (sdkups.connect()) {
                    int maxTry = 0;
                    while (maxTry < 5 && (upsData == null || upsData.totalInfo.equals(""))) {
                        upsData = sdkups.getInfo();
                        upsData.batteryLevel = sdkups.getBatteryLevel();
                        maxTry += 1;
                        Utils.sleep(2);
                    }
                }

                JSONObject savedUpsData = new JSONObject();
                JSONObject body = new JSONObject();

                if (upsData != null) {
                    try {
                        savedUpsData.put("totalInfo", upsData.totalInfo);
                        savedUpsData.put("batteryLevel", upsData.batteryLevel);
                        savedUpsData.put("inputVoltage", upsData.inputVoltage);
                        savedUpsData.put("outputVoltage", upsData.outputVoltage);
                        savedUpsData.put("frequencyOutput", upsData.frequencyOutput);
                        savedUpsData.put("consumedLoad", upsData.consumedLoad);
                        savedUpsData.put("batteryVoltage", upsData.batteryVoltage);
                        Log.d("BGS UPS",savedUpsData.toString());
                        body.put("ups", savedUpsData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("==UPS collecting===", "No information found for UPS");
                    try {
                        savedUpsData.put("totalInfo", "No information found for UPS");
                        savedUpsData.put("batteryLevel", 0);
                        savedUpsData.put("inputVoltage", 0);
                        savedUpsData.put("outputVoltage", 0);
                        savedUpsData.put("frequencyOutput", 0);
                        savedUpsData.put("consumedLoad", 0);
                        savedUpsData.put("batteryVoltage", 0);
                        body.put("ups", savedUpsData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
    public static TimerTask getTimerTask30(String m_androidId, Context context) {
        return new TimerTask() {
            @Override
            public void run() {
                Log.i("Supervisor", "======Collect data from UPS30=====");
                SDKUPS sdkups = new SDKUPS(Globals.upsComName, Globals.upsBaurate);
                UPSModel upsData = null;
                if (sdkups.connect()) {
                    upsData = sdkups.getInfo();
                    upsData.batteryLevel = sdkups.getBatteryLevel();
                }

                JSONObject savedUpsData = new JSONObject();
                JSONObject body = new JSONObject();

                if (upsData != null && upsData.inputVoltage == 0) {
                    try {
                        savedUpsData.put("totalInfo", upsData.totalInfo);
                        savedUpsData.put("batteryLevel", upsData.batteryLevel);
                        savedUpsData.put("inputVoltage", upsData.inputVoltage);
                        savedUpsData.put("outputVoltage", upsData.outputVoltage);
                        savedUpsData.put("frequencyOutput", upsData.frequencyOutput);
                        savedUpsData.put("consumedLoad", upsData.consumedLoad);
                        savedUpsData.put("batteryVoltage", upsData.batteryVoltage);
                        Log.d("BGS UPS",savedUpsData.toString());
                        body.put("ups", savedUpsData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
