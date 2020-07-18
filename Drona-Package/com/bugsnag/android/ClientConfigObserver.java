package com.bugsnag.android;

import java.util.Observable;
import java.util.Observer;
import kotlin.Metadata;
import kotlin.TypeCastException;

@Metadata(bv={1, 0, 3}, d1={"\000,\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\020\002\n\002\b\002\n\002\030\002\n\000\n\002\020\000\n\000\b\000\030\0002\0020\001B\025\022\006\020\002\032\0020\003\022\006\020\004\032\0020\005?\006\002\020\006J\b\020\007\032\0020\bH\002J\034\020\t\032\0020\b2\b\020\n\032\004\030\0010\0132\b\020\f\032\004\030\0010\rH\026R\016\020\002\032\0020\003X?\004?\006\002\n\000R\016\020\004\032\0020\005X?\004?\006\002\n\000?\006\016"}, d2={"Lcom/bugsnag/android/ClientConfigObserver;", "Ljava/util/Observer;", "client", "Lcom/bugsnag/android/Client;", "config", "Lcom/bugsnag/android/Configuration;", "(Lcom/bugsnag/android/Client;Lcom/bugsnag/android/Configuration;)V", "handleNotifyReleaseStages", "", "update", "o", "Ljava/util/Observable;", "arg", "", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
public final class ClientConfigObserver
  implements Observer
{
  private final Client client;
  private final Configuration config;
  
  public ClientConfigObserver(Client paramClient, Configuration paramConfiguration)
  {
    client = paramClient;
    config = paramConfiguration;
  }
  
  private final void handleNotifyReleaseStages()
  {
    if (config.shouldNotifyForReleaseStage(config.getReleaseStage()))
    {
      if (config.getDetectAnrs()) {
        client.enableAnrReporting();
      }
      if (config.getDetectNdkCrashes()) {
        client.enableNdkCrashReporting();
      }
    }
    else
    {
      client.disableAnrReporting();
      client.disableNdkCrashReporting();
    }
  }
  
  public void update(Observable paramObservable, Object paramObject)
  {
    if (paramObject != null)
    {
      paramObservable = (NativeInterface.Message)paramObject;
      if (type == NativeInterface.MessageType.UPDATE_NOTIFY_RELEASE_STAGES)
      {
        handleNotifyReleaseStages();
        return;
      }
      if (type == NativeInterface.MessageType.UPDATE_RELEASE_STAGE) {
        handleNotifyReleaseStages();
      }
    }
    else
    {
      throw ((Throwable)new TypeCastException("null cannot be cast to non-null type com.bugsnag.android.NativeInterface.Message"));
    }
  }
}
