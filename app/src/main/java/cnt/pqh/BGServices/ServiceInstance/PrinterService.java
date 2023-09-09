package cnt.pqh.BGServices.ServiceInstance;

import android.content.Context;
import android.util.Log;

import cnt.pqh.BGServices.Globals;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TimerTask;

import asim.sdk.locker.DeviceInfo;
import asim.sdk.locker.SDKLocker;
import asim.sdk.printer.POSCustomed;
import asim.sdk.printer.SDKPrints;

public class PrinterService {
    public static TimerTask getTimerTask(String m_androidId, Context context) {
        return new TimerTask() {
            public void run() {
                Log.i("Supervisor", "======Collect data from Printer=====");

                SDKPrints.PrintStatus printStatus = null;
                JSONObject printData = new JSONObject();
                JSONObject body = new JSONObject();

                List<DeviceInfo> devices = SDKLocker.getAllUsbDevicesHasDriver(context);

                POSCustomed mPos = new POSCustomed();
                USBPrinting mUsb = new USBPrinting();
                mPos.Set(mUsb);
                SDKPrints printController = new  SDKPrints();
                if (Globals.printerDeviceId == -1) {
                    printStatus = printController.getStatus(Globals.printerVendorId, Globals.printerProductId, context,mPos, mUsb);
                } else {
                    printStatus = printController.getStatus(Globals.printerVendorId, Globals.printerProductId, Globals.printerDeviceId, context,mPos, mUsb);
                }
                new SDKPrints.TaskClose(mUsb);

                if (printStatus != null) {
//                                            Log.d("printstatus===", "getCutterAbnormalMsg==" + printStatus.getCutterAbnormalMsg());
//                                            Log.d("printstatus===", "getConnectionFailedMsg==" + printStatus.getConnectionFailedMsg());
//                                            Log.d("printstatus===", "getCoverOpenMsg==" + printStatus.getCoverOpenMsg());
//                                            Log.d("printstatus===", "getOutOfPaperMsg==" + printStatus.getOutOfPaperMsg());
                    try {
                        printData.put("cutterMsg", printStatus.getCutterAbnormalMsg());
                        printData.put("isIsCutterAbnormal", printStatus.isIsCutterAbnormal());
                        printData.put("connectionMsg", printStatus.getConnectionFailedMsg());
                        printData.put("isConnectionFailed", printStatus.isConnectionFailed());
                        printData.put("coverMsg", printStatus.getCoverOpenMsg());
                        printData.put("isIsCoverOpen", printStatus.isIsCoverOpen());
                        printData.put("paperMsg", printStatus.getOutOfPaperMsg());
                        printData.put("isIsOutOfPaper", printStatus.isIsOutOfPaper());
                        Log.d("BGS Printer",printData.toString());
                        body.put("printer", printData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("==Printer collecting===", "No information found for Printer");
                    try {
                        printData.put("cutterMsg", "not available");
                        printData.put("isIsCutterAbnormal", false);
                        printData.put("connectionMsg", "not available");
                        printData.put("isConnectionFailed", true);
                        printData.put("coverMsg", "not available");
                        printData.put("isIsCoverOpen", false);
                        printData.put("paperMsg", "not available");
                        printData.put("isIsOutOfPaper", false);
                        body.put("printer", printData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
