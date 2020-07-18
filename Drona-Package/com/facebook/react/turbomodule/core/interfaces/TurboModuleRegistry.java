package com.facebook.react.turbomodule.core.interfaces;

import java.util.Collection;

public abstract interface TurboModuleRegistry
{
  public abstract TurboModule getModule(String paramString);
  
  public abstract Collection getModules();
  
  public abstract boolean hasModule(String paramString);
}
