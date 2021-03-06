package com.bugsnag.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv={1, 0, 3}, d1={"\000B\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\002\020\013\n\002\030\002\n\002\b\002\n\002\020\002\n\002\030\002\n\002\b\002\n\002\030\002\n\002\b\003\n\002\020\016\n\002\b\003\b\000\030\0002\0020\001:\001\026B@\022\006\020\002\032\0020\003\022\006\020\004\032\0020\005\022)\020\006\032%\022\023\022\0210\b?\006\f\b\t\022\b\b\n\022\004\b\b(\013\022\004\022\0020\f\030\0010\007j\004\030\001`\r?\006\002\020\016J\b\020\021\032\0020\bH\026J\b\020\022\032\0020\fH\026J\b\020\023\032\0020\024H\026J\b\020\025\032\0020\fH\026R\022\020\017\032\0060\020R\0020\000X?\004?\006\002\n\000R\016\020\004\032\0020\005X?\004?\006\002\n\000R\016\020\002\032\0020\003X?\004?\006\002\n\000?\006\027"}, d2={"Lcom/bugsnag/android/ConnectivityLegacy;", "Lcom/bugsnag/android/Connectivity;", "context", "Landroid/content/Context;", "cm", "Landroid/net/ConnectivityManager;", "callback", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "connected", "", "Lcom/bugsnag/android/NetworkChangeCallback;", "(Landroid/content/Context;Landroid/net/ConnectivityManager;Lkotlin/jvm/functions/Function1;)V", "changeReceiver", "Lcom/bugsnag/android/ConnectivityLegacy$ConnectivityChangeReceiver;", "hasNetworkConnection", "registerForNetworkChanges", "retrieveNetworkAccessState", "", "unregisterForNetworkChanges", "ConnectivityChangeReceiver", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
public final class ConnectivityLegacy
  implements Connectivity
{
  private final ConnectivityChangeReceiver changeReceiver;
  private final ConnectivityManager cm;
  private final Context context;
  
  public ConnectivityLegacy(Context paramContext, ConnectivityManager paramConnectivityManager, Function1 paramFunction1)
  {
    context = paramContext;
    cm = paramConnectivityManager;
    changeReceiver = new ConnectivityChangeReceiver(paramFunction1);
  }
  
  public boolean hasNetworkConnection()
  {
    NetworkInfo localNetworkInfo = cm.getActiveNetworkInfo();
    if (localNetworkInfo != null) {
      return localNetworkInfo.isConnectedOrConnecting();
    }
    return false;
  }
  
  public void registerForNetworkChanges()
  {
    IntentFilter localIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    context.registerReceiver((BroadcastReceiver)changeReceiver, localIntentFilter);
  }
  
  public String retrieveNetworkAccessState()
  {
    Object localObject = cm.getActiveNetworkInfo();
    if (localObject != null) {
      localObject = Integer.valueOf(((NetworkInfo)localObject).getType());
    } else {
      localObject = null;
    }
    if (localObject == null) {
      return "none";
    }
    if (((Integer)localObject).intValue() == 1) {
      return "wifi";
    }
    if (((Integer)localObject).intValue() == 9) {
      return "ethernet";
    }
    return "cellular";
  }
  
  public void unregisterForNetworkChanges()
  {
    context.unregisterReceiver((BroadcastReceiver)changeReceiver);
  }
  
  @Metadata(bv={1, 0, 3}, d1={"\0002\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\002\020\013\n\002\030\002\n\002\b\002\n\002\020\002\n\002\030\002\n\002\b\003\n\002\030\002\n\000\n\002\030\002\n\000\b?\004\030\0002\0020\001B0\022)\020\002\032%\022\023\022\0210\004?\006\f\b\005\022\b\b\006\022\004\b\b(\007\022\004\022\0020\b\030\0010\003j\004\030\001`\t?\006\002\020\nJ\030\020\013\032\0020\b2\006\020\f\032\0020\r2\006\020\016\032\0020\017H\026R1\020\002\032%\022\023\022\0210\004?\006\f\b\005\022\b\b\006\022\004\b\b(\007\022\004\022\0020\b\030\0010\003j\004\030\001`\tX?\004?\006\002\n\000?\006\020"}, d2={"Lcom/bugsnag/android/ConnectivityLegacy$ConnectivityChangeReceiver;", "Landroid/content/BroadcastReceiver;", "cb", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "connected", "", "Lcom/bugsnag/android/NetworkChangeCallback;", "(Lcom/bugsnag/android/ConnectivityLegacy;Lkotlin/jvm/functions/Function1;)V", "onReceive", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "bugsnag-android-core_release"}, k=1, mv={1, 1, 16})
  private final class ConnectivityChangeReceiver
    extends BroadcastReceiver
  {
    private final Function1<Boolean, Unit> key;
    
    public ConnectivityChangeReceiver(Function1 paramFunction1)
    {
      key = paramFunction1;
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      Intrinsics.checkParameterIsNotNull(paramContext, "context");
      Intrinsics.checkParameterIsNotNull(paramIntent, "intent");
      paramContext = key;
      if (paramContext != null) {
        paramContext = (Unit)paramContext.invoke(Boolean.valueOf(hasNetworkConnection()));
      }
    }
  }
}
