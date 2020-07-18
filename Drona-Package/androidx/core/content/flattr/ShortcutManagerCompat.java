package androidx.core.content.flattr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build.VERSION;
import android.text.TextUtils;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShortcutManagerCompat
{
  @VisibleForTesting
  static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
  public static final String EXTRA_SHORTCUT_ID = "android.intent.extra.shortcut.ID";
  @VisibleForTesting
  static final String INSTALL_SHORTCUT_PERMISSION = "com.android.launcher.permission.INSTALL_SHORTCUT";
  private static volatile androidx.core.content.pm.ShortcutInfoCompatSaver<?> sShortcutInfoCompatSaver;
  
  private ShortcutManagerCompat() {}
  
  public static boolean addDynamicShortcuts(Context paramContext, List paramList)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        localArrayList.add(((ShortcutInfoCompat)localIterator.next()).toShortcutInfo());
      }
      if (!((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).addDynamicShortcuts(localArrayList)) {
        return false;
      }
    }
    getShortcutInfoSaverInstance(paramContext).addShortcuts(paramList);
    return true;
  }
  
  public static Intent createShortcutResultIntent(Context paramContext, ShortcutInfoCompat paramShortcutInfoCompat)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramContext = ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).createShortcutResultIntent(paramShortcutInfoCompat.toShortcutInfo());
    } else {
      paramContext = null;
    }
    Object localObject = paramContext;
    if (paramContext == null) {
      localObject = new Intent();
    }
    return paramShortcutInfoCompat.addToIntent((Intent)localObject);
  }
  
  public static List getDynamicShortcuts(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      Object localObject = ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).getDynamicShortcuts();
      ArrayList localArrayList = new ArrayList(((List)localObject).size());
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        localArrayList.add(new ShortcutInfoCompat.Builder(paramContext, (ShortcutInfo)((Iterator)localObject).next()).build());
      }
      return localArrayList;
    }
    try
    {
      paramContext = getShortcutInfoSaverInstance(paramContext).getShortcuts();
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
    return new ArrayList();
  }
  
  public static int getMaxShortcutCountPerActivity(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 25) {
      return ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).getMaxShortcutCountPerActivity();
    }
    return 0;
  }
  
  private static ShortcutInfoCompatSaver getShortcutInfoSaverInstance(Context paramContext)
  {
    if ((sShortcutInfoCompatSaver != null) || (Build.VERSION.SDK_INT >= 23)) {}
    try
    {
      Object localObject = Class.forName("androidx.sharetarget.ShortcutInfoCompatSaverImpl", false, androidx.core.content.pm.ShortcutManagerCompat.class.getClassLoader());
      localObject = ((Class)localObject).getMethod("getInstance", new Class[] { Context.class });
      paramContext = ((Method)localObject).invoke(null, new Object[] { paramContext });
      sShortcutInfoCompatSaver = (ShortcutInfoCompatSaver)paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
    if (sShortcutInfoCompatSaver == null) {
      sShortcutInfoCompatSaver = new ShortcutInfoCompatSaver.NoopImpl();
    }
    return sShortcutInfoCompatSaver;
  }
  
  public static boolean isRequestPinShortcutSupported(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).isRequestPinShortcutSupported();
    }
    if (ContextCompat.checkSelfPermission(paramContext, "com.android.launcher.permission.INSTALL_SHORTCUT") != 0) {
      return false;
    }
    paramContext = paramContext.getPackageManager().queryBroadcastReceivers(new Intent("com.android.launcher.action.INSTALL_SHORTCUT"), 0).iterator();
    while (paramContext.hasNext())
    {
      String str = nextactivityInfo.permission;
      if ((TextUtils.isEmpty(str)) || ("com.android.launcher.permission.INSTALL_SHORTCUT".equals(str))) {
        return true;
      }
    }
    return false;
  }
  
  public static void removeAllDynamicShortcuts(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 25) {
      ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).removeAllDynamicShortcuts();
    }
    getShortcutInfoSaverInstance(paramContext).removeAllShortcuts();
  }
  
  public static boolean requestPinShortcut(Context paramContext, ShortcutInfoCompat paramShortcutInfoCompat, IntentSender paramIntentSender)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).requestPinShortcut(paramShortcutInfoCompat.toShortcutInfo(), paramIntentSender);
    }
    if (!isRequestPinShortcutSupported(paramContext)) {
      return false;
    }
    paramShortcutInfoCompat = paramShortcutInfoCompat.addToIntent(new Intent("com.android.launcher.action.INSTALL_SHORTCUT"));
    if (paramIntentSender == null)
    {
      paramContext.sendBroadcast(paramShortcutInfoCompat);
      return true;
    }
    paramContext.sendOrderedBroadcast(paramShortcutInfoCompat, null, new ShortcutManagerCompat.1(paramIntentSender), null, -1, null, null);
    return true;
  }
  
  public static boolean updateShortcuts(Context paramContext, List paramList)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        localArrayList.add(((ShortcutInfoCompat)localIterator.next()).toShortcutInfo());
      }
      if (!((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).updateShortcuts(localArrayList)) {
        return false;
      }
    }
    getShortcutInfoSaverInstance(paramContext).addShortcuts(paramList);
    return true;
  }
  
  public void removeDynamicShortcuts(Context paramContext, List paramList)
  {
    if (Build.VERSION.SDK_INT >= 25) {
      ((ShortcutManager)paramContext.getSystemService(ShortcutManager.class)).removeDynamicShortcuts(paramList);
    }
    getShortcutInfoSaverInstance(paramContext).removeShortcuts(paramList);
  }
}
