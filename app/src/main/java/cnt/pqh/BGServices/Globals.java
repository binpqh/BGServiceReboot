/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package cnt.pqh.BGServices;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class Globals {
    public static final String RESTART_INTENT = "cnt.pqh.BGServices.Restarter";
    public static boolean isInitiate = false;

    public static int protectTimeVal = 2000;
    public static int periodOfCollectionTime = 10000;
    public static int delayTimeBeforCollecting = 5000;
    public static int leapTime = 2000;
    // specific device has specific timer of collecting information.
    public static int lockerTimeSpanOfCollection = 3600000;
    public static int printerTimeSpanOfCollection = 3600000;
    public static int upsTimeSpanOfCollection = 3600000;
    public static int upsTimeSpanOfFetch = 30000;
    public static int temphuTimeSpanOfCollection = 3600000;
    // for simdispenser
    public static  String simdispenser1ComName = "/dev/ttyXR1";
    public static  String simdispenser1Baurate = "115200";
    public static  String simdispenser1TimeToRecyleCard = "60000";

    public static  String simdispenser2ComName = "/dev/ttyXR2";
    public static  String simdispenser2Baurate = "115200";
    public static  String simdispenser2TimeToRecyleCard = "60000";

    public static  String simdispenser3ComName = "/dev/ttyXR3";
    public static  String simdispenser3Baurate = "115200";
    public static  String simdispenser3TimeToRecyleCard = "60000";

    public static  String simdispenser4ComName = "/dev/ttyXR4";
    public static  String simdispenser4Baurate = "115200";
    public static  String simdispenser4TimeToRecyleCard = "60000";

    public static int simdispenser1TimeSpanOfCollection = 3600000;
    public static int simdispenser2TimeSpanOfCollection = 3600000;
    public static int simdispenser3TimeSpanOfCollection = 3600000;
    public static int simdispenser4TimeSpanOfCollection = 3600000;

    // for printer
    public static int printerProductId = 33054;
    public static int printerVendorId = 4070;
    public static int printerDeviceId = 1;
    // for locker
    public static int lockerProductId = 29987;
    public static int lockerVendorId = 6790;
    public static int lockerDeviceId = 1;
    public static int lockerRelayNumber = 1;
    public static int lockerTimeToClose = 1;
    public static int lockerBaurate = 9600;

    // for temphu

    public static int temphuProductId = 29987;
    public static int temphuVendorId = 6790;
    public static int temphuDeviceId = 1009;
    public static int temphuBaurate = 9600;

    // for ups

    public  static String upsComName = "/dev/ttyXR6";
    public  static int upsBaurate = 2400;

    // for configuration
    public static JSONArray configData;

    public static String configUrl = "https://kioskadmin.mylocal.vn:7102/asim/getConfig?kioskId=";
    public static String collectInfoUrl = "https://kioskadmin.mylocal.vn:7102/asim/collectInfo";

    public static Map<String, Object> getDefaultLockerInfo() {
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put("relayNumber", 1);
        defaultValue.put("status", false);
        defaultValue.put("timeToClose", 2);
        defaultValue.put("vendorId", 6790);
        defaultValue.put("productId", 29987);

        return defaultValue;
    }
}
