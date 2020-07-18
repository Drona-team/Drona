package org.json.social;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build.VERSION;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;

public class TargetChosenReceiver
  extends BroadcastReceiver
{
  private static final String EXTRA_RECEIVER_TOKEN = "receiver_token";
  private static final Object LOCK = new Object();
  private static Callback failureCallback;
  private static TargetChosenReceiver sLastRegisteredReceiver;
  private static String sTargetChosenReceiveAction;
  private static Callback successCallback;
  
  public TargetChosenReceiver() {}
  
  public static IntentSender getSharingSenderIntent(ReactContext paramReactContext)
  {
    Object localObject1 = LOCK;
    try
    {
      if (sTargetChosenReceiveAction == null)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(paramReactContext.getPackageName());
        ((StringBuilder)localObject2).append("/");
        ((StringBuilder)localObject2).append(cl.json.social.TargetChosenReceiver.class.getName());
        ((StringBuilder)localObject2).append("_ACTION");
        sTargetChosenReceiveAction = ((StringBuilder)localObject2).toString();
      }
      Object localObject2 = paramReactContext.getApplicationContext();
      if (sLastRegisteredReceiver != null) {
        ((Context)localObject2).unregisterReceiver(sLastRegisteredReceiver);
      }
      sLastRegisteredReceiver = new TargetChosenReceiver();
      ((Context)localObject2).registerReceiver(sLastRegisteredReceiver, new IntentFilter(sTargetChosenReceiveAction));
      localObject1 = new Intent(sTargetChosenReceiveAction);
      ((Intent)localObject1).setPackage(paramReactContext.getPackageName());
      ((Intent)localObject1).putExtra("receiver_token", sLastRegisteredReceiver.hashCode());
      return PendingIntent.getBroadcast(paramReactContext, 0, (Intent)localObject1, 1342177280).getIntentSender();
    }
    catch (Throwable paramReactContext)
    {
      throw paramReactContext;
    }
  }
  
  public static boolean isSupported()
  {
    return Build.VERSION.SDK_INT >= 22;
  }
  
  public static void registerCallbacks(Callback paramCallback1, Callback paramCallback2)
  {
    successCallback = paramCallback1;
    failureCallback = paramCallback2;
  }
  
  public static void sendCallback(boolean paramBoolean, Object... paramVarArgs)
  {
    if (paramBoolean)
    {
      if (successCallback != null) {
        successCallback.invoke(paramVarArgs);
      }
    }
    else if (failureCallback != null) {
      failureCallback.invoke(paramVarArgs);
    }
    successCallback = null;
    failureCallback = null;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Object localObject = LOCK;
    try
    {
      if (sLastRegisteredReceiver != this) {
        return;
      }
      paramContext.getApplicationContext().unregisterReceiver(sLastRegisteredReceiver);
      sLastRegisteredReceiver = null;
      if (paramIntent.hasExtra("receiver_token"))
      {
        if (paramIntent.getIntExtra("receiver_token", 0) != hashCode()) {
          return;
        }
        paramContext = (ComponentName)paramIntent.getParcelableExtra("android.intent.extra.CHOSEN_COMPONENT");
        if (paramContext != null)
        {
          sendCallback(true, new Object[] { Boolean.valueOf(true), paramContext.flattenToString() });
          return;
        }
        sendCallback(true, new Object[] { Boolean.valueOf(true), "OK" });
      }
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
}
