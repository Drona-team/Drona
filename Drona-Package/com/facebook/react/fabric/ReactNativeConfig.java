package com.facebook.react.fabric;

import com.facebook.proguard.annotations.DoNotStrip;

@DoNotStrip
public abstract interface ReactNativeConfig
{
  public abstract boolean getBool(String paramString);
  
  public abstract double getDouble(String paramString);
  
  public abstract int getInt64(String paramString);
  
  public abstract String getString(String paramString);
}
