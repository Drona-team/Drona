package com.amplitude.upgrade;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class Amplitude
{
  static final Map<String, com.amplitude.api.AmplitudeClient> instances = new HashMap();
  
  public Amplitude() {}
  
  public static void disableLocationListening()
  {
    getInstance().disableLocationListening();
  }
  
  public static void enableLocationListening()
  {
    getInstance().enableLocationListening();
  }
  
  public static void enableNewDeviceIdPerInstall(boolean paramBoolean)
  {
    getInstance().enableNewDeviceIdPerInstall(paramBoolean);
  }
  
  public static void endSession() {}
  
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
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public static void initialize(Context paramContext, String paramString)
  {
    getInstance().initialize(paramContext, paramString);
  }
  
  public static void initialize(Context paramContext, String paramString1, String paramString2)
  {
    getInstance().initialize(paramContext, paramString1, paramString2);
  }
  
  public static void logEvent(String paramString)
  {
    getInstance().logEvent(paramString);
  }
  
  public static void logEvent(String paramString, JSONObject paramJSONObject)
  {
    getInstance().logEvent(paramString, paramJSONObject);
  }
  
  public static void logRevenue(double paramDouble)
  {
    getInstance().logRevenue(paramDouble);
  }
  
  public static void logRevenue(String paramString, int paramInt, double paramDouble)
  {
    getInstance().logRevenue(paramString, paramInt, paramDouble);
  }
  
  public static void logRevenue(String paramString1, int paramInt, double paramDouble, String paramString2, String paramString3)
  {
    getInstance().logRevenue(paramString1, paramInt, paramDouble, paramString2, paramString3);
  }
  
  public static void setOptOut(boolean paramBoolean)
  {
    getInstance().setOptOut(paramBoolean);
  }
  
  public static void setSessionTimeoutMillis(long paramLong)
  {
    getInstance().setSessionTimeoutMillis(paramLong);
  }
  
  public static void setUserId(String paramString)
  {
    getInstance().setUserId(paramString);
  }
  
  public static void setUserProperties(JSONObject paramJSONObject)
  {
    getInstance().setUserProperties(paramJSONObject);
  }
  
  public static void setUserProperties(JSONObject paramJSONObject, boolean paramBoolean)
  {
    getInstance().setUserProperties(paramJSONObject, paramBoolean);
  }
  
  public static void startSession() {}
  
  public static void uploadEvents()
  {
    getInstance().uploadEvents();
  }
  
  public static void useAdvertisingIdForDeviceId()
  {
    getInstance().useAdvertisingIdForDeviceId();
  }
}
