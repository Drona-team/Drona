package com.bugsnag.android;

import kotlin.Metadata;

@Metadata(bv={1, 0, 3}, d1={"\000 \n\002\030\002\n\002\020\000\n\000\n\002\020\013\n\002\b\005\n\002\020\002\n\000\n\002\030\002\n\002\b\002\bf\030\0002\0020\001J\020\020\b\032\0020\t2\006\020\n\032\0020\013H&J\b\020\f\032\0020\tH&R\030\020\002\032\0020\003X?\016?\006\f\032\004\b\004\020\005\"\004\b\006\020\007?\006\r"}, d2={"Lcom/bugsnag/android/BugsnagPlugin;", "", "loaded", "", "getLoaded", "()Z", "setLoaded", "(Z)V", "loadPlugin", "", "client", "Lcom/bugsnag/android/Client;", "unloadPlugin", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
public abstract interface BugsnagPlugin
{
  public abstract boolean getLoaded();
  
  public abstract void loadPlugin(Client paramClient);
  
  public abstract void setLoaded(boolean paramBoolean);
  
  public abstract void unloadPlugin();
}
