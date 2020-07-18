package com.amplitude.api;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class Amplitude
{
  static final Map<String, AmplitudeClient> instances = new HashMap();
  
  public Amplitude() {}
  
  @Deprecated
  public static void disableLocationListening()
  {
    getInstance().disableLocationListening();
  }
  
  @Deprecated
  public static void enableLocationListening()
  {
    getInstance().enableLocationListening();
  }
  
  @Deprecated
  public static void enableNewDeviceIdPerInstall(boolean paramBoolean)
  {
    getInstance().enableNewDeviceIdPerInstall(paramBoolean);
  }
  
  @Deprecated
  public static void endSession() {}
  
  @Deprecated
  public static String getDeviceId()
  {
    return getInstance().getDeviceId();
  }
  
  public static AmplitudeClient getInstance()
  {
    return getInstance(null);
  }
  
  public static AmplitudeClient getInstance(String paramString)
  {
    try
    {
      String str = Utils.normalizeInstanceName(paramString);
      AmplitudeClient localAmplitudeClient = (AmplitudeClient)instances.get(str);
      paramString = localAmplitudeClient;
      if (localAmplitudeClient == null)
      {
        paramString = new AmplitudeClient(str);
        instances.put(str, paramString);
      }
      return paramString;
    }
    finally {}
  }
  
  @Deprecated
  public static void initialize(Context paramContext, String paramString)
  {
    getInstance().initialize(paramContext, paramString);
  }
  
  @Deprecated
  public static void initialize(Context paramContext, String paramString1, String paramString2)
  {
    getInstance().initialize(paramContext, paramString1, paramString2);
  }
  
  @Deprecated
  public static void logEvent(String paramString)
  {
    getInstance().logEvent(paramString);
  }
  
  @Deprecated
  public static void logEvent(String paramString, JSONObject paramJSONObject)
  {
    getInstance().logEvent(paramString, paramJSONObject);
  }
  
  @Deprecated
  public static void logRevenue(double paramDouble)
  {
    getInstance().logRevenue(paramDouble);
  }
  
  @Deprecated
  public static void logRevenue(String paramString, int paramInt, double paramDouble)
  {
    getInstance().logRevenue(paramString, paramInt, paramDouble);
  }
  
  @Deprecated
  public static void logRevenue(String paramString1, int paramInt, double paramDouble, String paramString2, String paramString3)
  {
    getInstance().logRevenue(paramString1, paramInt, paramDouble, paramString2, paramString3);
  }
  
  @Deprecated
  public static void setOptOut(boolean paramBoolean)
  {
    getInstance().setOptOut(paramBoolean);
  }
  
  @Deprecated
  public static void setSessionTimeoutMillis(long paramLong)
  {
    getInstance().setSessionTimeoutMillis(paramLong);
  }
  
  @Deprecated
  public static void setUserId(String paramString)
  {
    getInstance().setUserId(paramString);
  }
  
  @Deprecated
  public static void setUserProperties(JSONObject paramJSONObject)
  {
    getInstance().setUserProperties(paramJSONObject);
  }
  
  @Deprecated
  public static void setUserProperties(JSONObject paramJSONObject, boolean paramBoolean)
  {
    getInstance().setUserProperties(paramJSONObject, paramBoolean);
  }
  
  @Deprecated
  public static void startSession() {}
  
  @Deprecated
  public static void uploadEvents()
  {
    getInstance().uploadEvents();
  }
  
  @Deprecated
  public static void useAdvertisingIdForDeviceId()
  {
    getInstance().useAdvertisingIdForDeviceId();
  }
}
