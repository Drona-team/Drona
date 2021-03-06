package com.bugsnag.android;

import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import androidx.annotation.RequiresApi;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.Nullable;

@Metadata(bv={1, 0, 3}, d1={"\000B\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\002\020\013\n\002\030\002\n\002\b\002\n\002\020\002\n\002\030\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\002\b\003\n\002\020\016\n\002\b\003\b\001\030\0002\0020\001:\001\026B8\022\006\020\002\032\0020\003\022)\020\004\032%\022\023\022\0210\006?\006\f\b\007\022\b\b\b\022\004\b\b(\t\022\004\022\0020\n\030\0010\005j\004\030\001`\013?\006\002\020\fJ\b\020\021\032\0020\006H\026J\b\020\022\032\0020\nH\026J\b\020\023\032\0020\024H\026J\b\020\025\032\0020\nH\026R\024\020\r\032\004\030\0010\0168\000@\000X?\016?\006\002\n\000R\016\020\002\032\0020\003X?\004?\006\002\n\000R\022\020\017\032\0060\020R\0020\000X?\004?\006\002\n\000?\006\027"}, d2={"Lcom/bugsnag/android/ConnectivityApi24;", "Lcom/bugsnag/android/Connectivity;", "cm", "Landroid/net/ConnectivityManager;", "callback", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "connected", "", "Lcom/bugsnag/android/NetworkChangeCallback;", "(Landroid/net/ConnectivityManager;Lkotlin/jvm/functions/Function1;)V", "activeNetwork", "Landroid/net/Network;", "networkCallback", "Lcom/bugsnag/android/ConnectivityApi24$ConnectivityTrackerCallback;", "hasNetworkConnection", "registerForNetworkChanges", "retrieveNetworkAccessState", "", "unregisterForNetworkChanges", "ConnectivityTrackerCallback", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
@RequiresApi(24)
public final class ConnectivityApi24
  implements Connectivity
{
  @JvmField
  @Nullable
  public volatile Network activeNetwork;
  private final ConnectivityManager cm;
  private final ConnectivityTrackerCallback networkCallback;
  
  public ConnectivityApi24(ConnectivityManager paramConnectivityManager, Function1 paramFunction1)
  {
    cm = paramConnectivityManager;
    networkCallback = new ConnectivityTrackerCallback(paramFunction1);
  }
  
  public boolean hasNetworkConnection()
  {
    return activeNetwork != null;
  }
  
  public void registerForNetworkChanges()
  {
    cm.registerDefaultNetworkCallback((ConnectivityManager.NetworkCallback)networkCallback);
  }
  
  public String retrieveNetworkAccessState()
  {
    Object localObject = cm.getActiveNetwork();
    if (localObject != null) {
      localObject = cm.getNetworkCapabilities((Network)localObject);
    } else {
      localObject = null;
    }
    if (localObject == null) {
      return "none";
    }
    if (((NetworkCapabilities)localObject).hasTransport(1)) {
      return "wifi";
    }
    if (((NetworkCapabilities)localObject).hasTransport(3)) {
      return "ethernet";
    }
    if (((NetworkCapabilities)localObject).hasTransport(0)) {
      return "cellular";
    }
    return "unknown";
  }
  
  public void unregisterForNetworkChanges()
  {
    cm.unregisterNetworkCallback((ConnectivityManager.NetworkCallback)networkCallback);
  }
  
  @Metadata(bv={1, 0, 3}, d1={"\000.\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\002\020\013\n\002\030\002\n\002\b\002\n\002\020\002\n\002\030\002\n\002\b\003\n\002\030\002\n\002\b\002\b?\004\030\0002\0020\001B0\022)\020\002\032%\022\023\022\0210\004?\006\f\b\005\022\b\b\006\022\004\b\b(\007\022\004\022\0020\b\030\0010\003j\004\030\001`\t?\006\002\020\nJ\022\020\013\032\0020\b2\b\020\f\032\004\030\0010\rH\026J\b\020\016\032\0020\bH\026R1\020\002\032%\022\023\022\0210\004?\006\f\b\005\022\b\b\006\022\004\b\b(\007\022\004\022\0020\b\030\0010\003j\004\030\001`\tX?\004?\006\002\n\000?\006\017"}, d2={"Lcom/bugsnag/android/ConnectivityApi24$ConnectivityTrackerCallback;", "Landroid/net/ConnectivityManager$NetworkCallback;", "cb", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "connected", "", "Lcom/bugsnag/android/NetworkChangeCallback;", "(Lcom/bugsnag/android/ConnectivityApi24;Lkotlin/jvm/functions/Function1;)V", "onAvailable", "network", "Landroid/net/Network;", "onUnavailable", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
  private final class ConnectivityTrackerCallback
    extends ConnectivityManager.NetworkCallback
  {
    private final Function1<Boolean, Unit> keySelector;
    
    public ConnectivityTrackerCallback(Function1 paramFunction1)
    {
      keySelector = paramFunction1;
    }
    
    public void onAvailable(Network paramNetwork)
    {
      super.onAvailable(paramNetwork);
      activeNetwork = paramNetwork;
      paramNetwork = keySelector;
      if (paramNetwork != null) {
        paramNetwork = (Unit)paramNetwork.invoke(Boolean.valueOf(true));
      }
    }
    
    public void onUnavailable()
    {
      super.onUnavailable();
      activeNetwork = null;
      Object localObject = keySelector;
      if (localObject != null) {
        localObject = (Unit)((Function1)localObject).invoke(Boolean.valueOf(false));
      }
    }
  }
}
