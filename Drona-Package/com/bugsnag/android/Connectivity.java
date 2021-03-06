package com.bugsnag.android;

import kotlin.Metadata;

@Metadata(bv={1, 0, 3}, d1={"\000\036\n\002\030\002\n\002\020\000\n\000\n\002\020\013\n\000\n\002\020\002\n\000\n\002\020\016\n\002\b\002\b`\030\0002\0020\001J\b\020\002\032\0020\003H&J\b\020\004\032\0020\005H&J\b\020\006\032\0020\007H&J\b\020\b\032\0020\005H&?\006\t"}, d2={"Lcom/bugsnag/android/Connectivity;", "", "hasNetworkConnection", "", "registerForNetworkChanges", "", "retrieveNetworkAccessState", "", "unregisterForNetworkChanges", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
public abstract interface Connectivity
{
  public abstract boolean hasNetworkConnection();
  
  public abstract void registerForNetworkChanges();
  
  public abstract String retrieveNetworkAccessState();
  
  public abstract void unregisterForNetworkChanges();
}
