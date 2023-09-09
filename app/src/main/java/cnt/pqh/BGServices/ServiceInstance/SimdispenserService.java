package cnt.pqh.BGServices.ServiceInstance;

import android.content.Context;
import android.util.Log;
import asim.sdk.sdksimdispenser.SimdispenserMain;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimerTask;
public class SimdispenserService {
    public static TimerTask getTimerTask(String m_androidId, Context context, String simdispenserTimeToRecyleCard, String simdispenserComName, String simdispenserBaurate, int index) {
        return new TimerTask() {
            public void run() {
                Log.i("Supervisor", "======Collect data from Simdispenser=====");
                JSONObject body = new JSONObject();
                JSONObject savedSimdispenserData = new JSONObject();

                SimdispenserMain simdispenserMain = new SimdispenserMain();
                simdispenserMain.Init(simdispenserTimeToRecyleCard, simdispenserComName, simdispenserBaurate);
                HashMap<String, Object> getStatusResult = null;
                getStatusResult = simdispenserMain.m_control.controlCheckSensorStatus(simdispenserMain);
                // close simdispenser if need

                // save data to server
                if (getStatusResult != null) {
                    try {
                        savedSimdispenserData.put("simdispenserId", index);
                        savedSimdispenserData.put("status", getStatusResult.get("status"));
                        savedSimdispenserData.put("camBien1", getStatusResult.get("sensor1"));
                        savedSimdispenserData.put("camBien2", getStatusResult.get("sensor2"));
                        savedSimdispenserData.put("camBien3", getStatusResult.get("sensor3"));
                        savedSimdispenserData.put("camBien4", getStatusResult.get("sensor4"));
                        savedSimdispenserData.put("camBien5", getStatusResult.get("sensor5"));
                        savedSimdispenserData.put("camBien6", getStatusResult.get("sensor6"));
                        savedSimdispenserData.put("camBien7", getStatusResult.get("sensor7"));
                        savedSimdispenserData.put("cardBox", getStatusResult.get("card box"));
                        savedSimdispenserData.put("carBox", getStatusResult.get("car box"));
                        savedSimdispenserData.put("frontSensor", getStatusResult.get("front sensor locator hook"));
                        savedSimdispenserData.put("rearSensor", getStatusResult.get("the rear sensor locates the hook"));
                        Log.d("BGS Simdispender",savedSimdispenserData.toString());
                        body.put("simdispenser", savedSimdispenserData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("==Simdisp collecting===", "No information found for Simdisp");
                    try {
                        savedSimdispenserData.put("simdispenserId", index);
                        savedSimdispenserData.put("status", false);
                        savedSimdispenserData.put("camBien1", "not available");
                        savedSimdispenserData.put("camBien2", "not available");
                        savedSimdispenserData.put("camBien3", "not available");
                        savedSimdispenserData.put("camBien4", "not available");
                        savedSimdispenserData.put("camBien5", "not available");
                        savedSimdispenserData.put("camBien6", "not available");
                        savedSimdispenserData.put("camBien7", "not available");
                        savedSimdispenserData.put("cardBox", "not available");
                        savedSimdispenserData.put("carBox", "not available");
                        savedSimdispenserData.put("frontSensor", "not available");
                        savedSimdispenserData.put("rearSensor", "not available");
                        body.put("simdispenser", savedSimdispenserData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }
}
