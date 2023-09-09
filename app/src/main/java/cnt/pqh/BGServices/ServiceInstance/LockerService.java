package cnt.pqh.BGServices.ServiceInstance;

import android.content.Context;
import android.util.Log;
import cnt.pqh.BGServices.Globals;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import asim.sdk.common.Utils;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.locker.SDKLocker;
public class LockerService {
    public static TimerTask getTimerTask(Context context) {
        return new TimerTask() {
            public void run() {
                Log.i("Supervisor", "======Collect data from Locker=====");
                SDKLocker locker = new SDKLocker();
                Map<String, Boolean> isLocked = null;

                JSONObject lockData = new JSONObject();
                JSONObject body = new JSONObject();

                List<DeviceInfo> devices = SDKLocker.getAllUsbDevicesHasDriver(context);

                for (DeviceInfo each : devices) {
                    Log.d("deviceTest==", String.valueOf(each.device.getDeviceId()));
                    if (Globals.lockerDeviceId == -1 && each.device.getVendorId() == Globals.lockerVendorId && each.device.getProductId() == Globals.lockerProductId) {
                        boolean connect = locker.connect(context, each, Globals.lockerBaurate);
                        if (connect) {
                            isLocked = locker.isLocked();
                            locker.disconnect();
                        }

                        continue;
                    }

                    if (Globals.lockerDeviceId != -1 && each.device.getVendorId() == Globals.lockerVendorId && each.device.getProductId() == Globals.lockerProductId && Utils.compareTwoDeviceId(each.device.getDeviceId(), Globals.lockerDeviceId)) {
                        boolean connect = locker.connect(context, each, Globals.lockerBaurate);
                        if (connect) {
                            isLocked = locker.isLocked();
                            locker.disconnect();
                        }
                    }
                }

                if (isLocked != null) {
//                                            Log.d("===Relay1====", isLocked.get("Relay1").toString());
//                                            Log.d("===Relay2====", isLocked.get("Relay2").toString());
                    try {
                        lockData.put("Relay1", isLocked.get("Relay1"));
                        lockData.put("Relay2", isLocked.get("Relay2"));
                        lockData.put("ghiChu", "available");
                        Log.d("BGS Locker",lockData.toString());
                        body.put("locker", lockData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else {
                    Log.d("==Locker collecting===", "No information found for Locker");
                    try {
                        lockData.put("Relay1", false);
                        lockData.put("Relay2", false);
                        lockData.put("ghiChu", "Not available");
                        body.put("locker", lockData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }
        };
    }
}
