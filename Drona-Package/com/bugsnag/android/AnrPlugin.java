package com.bugsnag.android;

import android.os.Handler;
import android.os.Looper;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv={1, 0, 3}, d1={"\000(\n\002\030\002\n\002\030\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\000\n\002\020\013\n\002\b\005\n\002\020\002\n\002\b\007\b\000\030\000 \0242\0020\001:\001\024B\005?\006\002\020\002J\t\020\r\032\0020\016H? J\021\020\017\032\0020\0162\006\020\020\032\0020\bH? J\020\020\021\032\0020\0162\006\020\003\032\0020\004H\026J\b\020\022\032\0020\016H\002J\b\020\023\032\0020\016H\026R\016\020\003\032\0020\004X?.?\006\002\n\000R\016\020\005\032\0020\006X?\004?\006\002\n\000R\032\020\007\032\0020\bX?\016?\006\016\n\000\032\004\b\t\020\n\"\004\b\013\020\f?\006\025"}, d2={"Lcom/bugsnag/android/AnrPlugin;", "Lcom/bugsnag/android/BugsnagPlugin;", "()V", "client", "Lcom/bugsnag/android/Client;", "collector", "Lcom/bugsnag/android/AnrDetailsCollector;", "loaded", "", "getLoaded", "()Z", "setLoaded", "(Z)V", "disableAnrReporting", "", "enableAnrReporting", "callPreviousSigquitHandler", "loadPlugin", "notifyAnrDetected", "unloadPlugin", "Companion", "bugsnag-plugin-android-anr_release"}, k=1, mv={1, 1, 16})
public final class AnrPlugin
  implements BugsnagPlugin
{
  public static final Companion Companion = new Companion(null);
  private Client client;
  private final AnrDetailsCollector collector = new AnrDetailsCollector();
  private boolean loaded;
  
  static
  {
    System.loadLibrary("bugsnag-plugin-android-anr");
  }
  
  public AnrPlugin() {}
  
  private final native void disableAnrReporting();
  
  private final native void enableAnrReporting(boolean paramBoolean);
  
  private final void notifyAnrDetected()
  {
    Object localObject1 = Looper.getMainLooper();
    Intrinsics.checkExpressionValueIsNotNull(localObject1, "Looper.getMainLooper()");
    localObject1 = ((Looper)localObject1).getThread();
    Intrinsics.checkExpressionValueIsNotNull(localObject1, "thread");
    Object localObject3 = new BugsnagException("ANR", "Application did not respond to UI input", ((Thread)localObject1).getStackTrace());
    Object localObject2 = client;
    if (localObject2 == null) {
      Intrinsics.throwUninitializedPropertyAccessException("client");
    }
    localObject2 = config;
    localObject3 = (Throwable)localObject3;
    Client localClient = client;
    if (localClient == null) {
      Intrinsics.throwUninitializedPropertyAccessException("client");
    }
    localObject1 = new Error.Builder((Configuration)localObject2, (Throwable)localObject3, sessionTracker, (Thread)localObject1, true).severity(Severity.ERROR).severityReasonType("anrError").build();
    localObject2 = collector;
    localObject3 = client;
    if (localObject3 == null) {
      Intrinsics.throwUninitializedPropertyAccessException("client");
    }
    Intrinsics.checkExpressionValueIsNotNull(localObject1, "error");
    ((AnrDetailsCollector)localObject2).collectAnrErrorDetails$bugsnag_plugin_android_anr_release((Client)localObject3, (Error)localObject1);
  }
  
  public boolean getLoaded()
  {
    return loaded;
  }
  
  public void loadPlugin(final Client paramClient)
  {
    Intrinsics.checkParameterIsNotNull(paramClient, "client");
    new Handler(Looper.getMainLooper()).post((Runnable)new Runnable()
    {
      public final void run()
      {
        AnrPlugin.access$setClient$p(AnrPlugin.this, paramClient);
        AnrPlugin localAnrPlugin = AnrPlugin.this;
        Configuration localConfiguration = paramClientconfig;
        Intrinsics.checkExpressionValueIsNotNull(localConfiguration, "client.config");
        AnrPlugin.access$enableAnrReporting(localAnrPlugin, localConfiguration.getCallPreviousSigquitHandler());
        Logger.warn("Initialised ANR Plugin");
      }
    });
  }
  
  public void setLoaded(boolean paramBoolean)
  {
    loaded = paramBoolean;
  }
  
  public void unloadPlugin()
  {
    disableAnrReporting();
  }
  
  @Metadata(bv={1, 0, 3}, d1={"\000\f\n\002\030\002\n\002\020\000\n\002\b\002\b?\003\030\0002\0020\001B\007\b\002?\006\002\020\002?\006\003"}, d2={"Lcom/bugsnag/android/AnrPlugin$Companion;", "", "()V", "bugsnag-plugin-android-anr_release"}, k=1, mv={1, 1, 16})
  public static final class Companion
  {
    private Companion() {}
  }
}
