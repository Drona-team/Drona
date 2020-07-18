package com.bugsnag.android;

import com.bugsnag.android.jgit.NativeBridge;
import java.util.Observable;
import java.util.Observer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv={1, 0, 3}, d1={"\000*\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\013\n\002\b\005\n\002\030\002\n\000\n\002\020\002\n\002\b\003\n\002\030\002\n\002\b\003\b\000\030\000 \0222\0020\001:\001\022B\005?\006\002\020\002J\t\020\013\032\0020\fH? J\t\020\r\032\0020\fH? J\020\020\016\032\0020\f2\006\020\017\032\0020\020H\026J\b\020\021\032\0020\fH\026R\032\020\003\032\0020\004X?\016?\006\016\n\000\032\004\b\005\020\006\"\004\b\007\020\bR\020\020\t\032\004\030\0010\nX?\016?\006\002\n\000?\006\023"}, d2={"Lcom/bugsnag/android/NdkPlugin;", "Lcom/bugsnag/android/BugsnagPlugin;", "()V", "loaded", "", "getLoaded", "()Z", "setLoaded", "(Z)V", "nativeBridge", "Lcom/bugsnag/android/ndk/NativeBridge;", "disableCrashReporting", "", "enableCrashReporting", "loadPlugin", "client", "Lcom/bugsnag/android/Client;", "unloadPlugin", "Companion", "bugsnag-plugin-android-ndk_release"}, k=1, mv={1, 1, 16})
public final class NdkPlugin
  implements BugsnagPlugin
{
  public static final Companion Companion = new Companion(null);
  private boolean loaded;
  private NativeBridge nativeBridge;
  
  static
  {
    System.loadLibrary("bugsnag-ndk");
  }
  
  public NdkPlugin() {}
  
  private final native void disableCrashReporting();
  
  private final native void enableCrashReporting();
  
  public boolean getLoaded()
  {
    return loaded;
  }
  
  public void loadPlugin(Client paramClient)
  {
    Intrinsics.checkParameterIsNotNull(paramClient, "client");
    if (nativeBridge == null)
    {
      nativeBridge = new NativeBridge();
      paramClient.addObserver((Observer)nativeBridge);
      paramClient.sendNativeSetupNotification();
    }
    enableCrashReporting();
    Logger.info("Initialised NDK Plugin");
  }
  
  public void setLoaded(boolean paramBoolean)
  {
    loaded = paramBoolean;
  }
  
  public void unloadPlugin()
  {
    disableCrashReporting();
  }
  
  @Metadata(bv={1, 0, 3}, d1={"\000\f\n\002\030\002\n\002\020\000\n\002\b\002\b?\003\030\0002\0020\001B\007\b\002?\006\002\020\002?\006\003"}, d2={"Lcom/bugsnag/android/NdkPlugin$Companion;", "", "()V", "bugsnag-plugin-android-ndk_release"}, k=1, mv={1, 1, 16})
  public static final class Companion
  {
    private Companion() {}
  }
}
