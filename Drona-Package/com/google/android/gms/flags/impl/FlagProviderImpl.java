package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.flags.IExtensionHost.Stub;

@DynamiteApi
public class FlagProviderImpl
  extends IExtensionHost.Stub
{
  private SharedPreferences methodName;
  private boolean transport_udp = false;
  
  public FlagProviderImpl() {}
  
  public boolean getBooleanFlagValue(String paramString, boolean paramBoolean, int paramInt)
  {
    if (!transport_udp) {
      return paramBoolean;
    }
    return OperatorSwitch.call(methodName, paramString, Boolean.valueOf(paramBoolean)).booleanValue();
  }
  
  public int getIntFlagValue(String paramString, int paramInt1, int paramInt2)
  {
    if (!transport_udp) {
      return paramInt1;
    }
    return InjectorImpl.2.1.call(methodName, paramString, Integer.valueOf(paramInt1)).intValue();
  }
  
  public long getLongFlagValue(String paramString, long paramLong, int paramInt)
  {
    if (!transport_udp) {
      return paramLong;
    }
    return CallableReference.call(methodName, paramString, Long.valueOf(paramLong)).longValue();
  }
  
  public String getStringFlagValue(String paramString1, String paramString2, int paramInt)
  {
    if (!transport_udp) {
      return paramString2;
    }
    return HttpUnsuccessfulResponseHandler.handleResponse(methodName, paramString1, paramString2);
  }
  
  public void init(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    if (transport_udp) {
      return;
    }
    try
    {
      paramIObjectWrapper = ImageCache.getInstance(paramIObjectWrapper.createPackageContext("com.google.android.gms", 0));
      methodName = paramIObjectWrapper;
      transport_udp = true;
      return;
    }
    catch (Exception paramIObjectWrapper)
    {
      paramIObjectWrapper = String.valueOf(paramIObjectWrapper.getMessage());
      if (paramIObjectWrapper.length() != 0) {
        paramIObjectWrapper = "Could not retrieve sdk flags, continuing with defaults: ".concat(paramIObjectWrapper);
      } else {
        paramIObjectWrapper = new String("Could not retrieve sdk flags, continuing with defaults: ");
      }
      Log.w("FlagProviderImpl", paramIObjectWrapper);
      return;
    }
    catch (PackageManager.NameNotFoundException paramIObjectWrapper) {}
  }
}
