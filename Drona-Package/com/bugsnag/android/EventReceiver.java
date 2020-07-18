package com.bugsnag.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BaseBundle;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EventReceiver
  extends BroadcastReceiver
{
  private static final String INTENT_ACTION_KEY = "Intent Action";
  @NonNull
  private static final Map<String, BreadcrumbType> actions = ;
  private final Client client;
  
  public EventReceiver(Client paramClient)
  {
    client = paramClient;
  }
  
  private static Map buildActions()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("android.appwidget.action.APPWIDGET_DELETED", BreadcrumbType.USER);
    localHashMap.put("android.appwidget.action.APPWIDGET_DISABLED", BreadcrumbType.USER);
    localHashMap.put("android.appwidget.action.APPWIDGET_ENABLED", BreadcrumbType.USER);
    localHashMap.put("android.appwidget.action.APPWIDGET_HOST_RESTORED", BreadcrumbType.STATE);
    localHashMap.put("android.appwidget.action.APPWIDGET_RESTORED", BreadcrumbType.STATE);
    localHashMap.put("android.appwidget.action.APPWIDGET_UPDATE", BreadcrumbType.STATE);
    localHashMap.put("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.ACTION_POWER_CONNECTED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.ACTION_POWER_DISCONNECTED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.ACTION_SHUTDOWN", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.AIRPLANE_MODE", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.BATTERY_LOW", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.BATTERY_OKAY", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.BOOT_COMPLETED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.CAMERA_BUTTON", BreadcrumbType.USER);
    localHashMap.put("android.intent.action.CLOSE_SYSTEM_DIALOGS", BreadcrumbType.USER);
    localHashMap.put("android.intent.action.CONFIGURATION_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.CONTENT_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.DATE_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.DEVICE_STORAGE_LOW", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.DEVICE_STORAGE_OK", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.DOCK_EVENT", BreadcrumbType.USER);
    localHashMap.put("android.intent.action.DREAMING_STARTED", BreadcrumbType.NAVIGATION);
    localHashMap.put("android.intent.action.DREAMING_STOPPED", BreadcrumbType.NAVIGATION);
    localHashMap.put("android.intent.action.INPUT_METHOD_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.LOCALE_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.REBOOT", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.SCREEN_OFF", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.SCREEN_ON", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.TIMEZONE_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.intent.action.TIME_SET", BreadcrumbType.STATE);
    localHashMap.put("android.os.action.DEVICE_IDLE_MODE_CHANGED", BreadcrumbType.STATE);
    localHashMap.put("android.os.action.POWER_SAVE_MODE_CHANGED", BreadcrumbType.STATE);
    return localHashMap;
  }
  
  public static IntentFilter getIntentFilter()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    Iterator localIterator = actions.keySet().iterator();
    while (localIterator.hasNext()) {
      localIntentFilter.addAction((String)localIterator.next());
    }
    return localIntentFilter;
  }
  
  static boolean isAndroidKey(String paramString)
  {
    return paramString.startsWith("android.");
  }
  
  static String shortenActionNameIfNeeded(String paramString)
  {
    String str = paramString;
    if (isAndroidKey(paramString)) {
      str = paramString.substring(paramString.lastIndexOf(".") + 1);
    }
    return str;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    try
    {
      HashMap localHashMap = new HashMap();
      paramContext = paramIntent.getAction();
      String str1 = shortenActionNameIfNeeded(paramIntent.getAction());
      localHashMap.put("Intent Action", paramContext);
      paramIntent = paramIntent.getExtras();
      if (paramIntent != null)
      {
        Iterator localIterator = paramIntent.keySet().iterator();
        for (;;)
        {
          bool = localIterator.hasNext();
          if (!bool) {
            break;
          }
          Object localObject = localIterator.next();
          localObject = (String)localObject;
          String str2 = paramIntent.get((String)localObject).toString();
          bool = isAndroidKey((String)localObject);
          if (bool) {
            localHashMap.put("Extra", String.format("%s: %s", new Object[] { str1, str2 }));
          } else {
            localHashMap.put(localObject, str2);
          }
        }
      }
      paramIntent = actions;
      boolean bool = paramIntent.containsKey(paramContext);
      if (bool)
      {
        paramIntent = actions;
        paramContext = paramIntent.get(paramContext);
        paramContext = (BreadcrumbType)paramContext;
      }
      else
      {
        paramContext = BreadcrumbType.DEFAULT;
      }
      paramIntent = client;
      bool = paramIntent.getConfig().isAutomaticallyCollectingBreadcrumbs();
      if (bool)
      {
        paramIntent = client;
        paramIntent.leaveBreadcrumb(str1, paramContext, localHashMap);
        return;
      }
    }
    catch (Exception paramContext)
    {
      paramIntent = new StringBuilder();
      paramIntent.append("Failed to leave breadcrumb in EventReceiver: ");
      paramIntent.append(paramContext.getMessage());
      Logger.warn(paramIntent.toString());
    }
  }
}
