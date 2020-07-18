package com.facebook.hermes.instrumentation;

public abstract interface HermesMemoryDumper
{
  public abstract String getId();
  
  public abstract String getInternalStorage();
  
  public abstract void setMetaData(String paramString);
  
  public abstract boolean shouldSaveSnapshot();
}
