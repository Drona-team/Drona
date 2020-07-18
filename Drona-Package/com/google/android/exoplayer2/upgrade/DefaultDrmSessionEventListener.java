package com.google.android.exoplayer2.upgrade;

public abstract interface DefaultDrmSessionEventListener
{
  public abstract void onDrmKeysLoaded();
  
  public abstract void onDrmKeysRemoved();
  
  public abstract void onDrmKeysRestored();
  
  public abstract void onDrmSessionAcquired();
  
  public abstract void onDrmSessionManagerError(Exception paramException);
  
  public abstract void onDrmSessionReleased();
}
