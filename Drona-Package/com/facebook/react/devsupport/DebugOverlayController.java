package com.facebook.react.devsupport;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UiThreadUtil;

class DebugOverlayController
{
  @Nullable
  private FrameLayout mFPSDebugViewContainer;
  private final ReactContext mReactContext;
  private final WindowManager mWindowManager;
  
  public DebugOverlayController(ReactContext paramReactContext)
  {
    mReactContext = paramReactContext;
    mWindowManager = ((WindowManager)paramReactContext.getSystemService("window"));
  }
  
  private static boolean canHandleIntent(Context paramContext, Intent paramIntent)
  {
    return paramIntent.resolveActivity(paramContext.getPackageManager()) != null;
  }
  
  private static boolean hasPermission(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 4096);
      if (requestedPermissions != null)
      {
        paramContext = requestedPermissions;
        int j = paramContext.length;
        int i = 0;
        while (i < j)
        {
          Object localObject = paramContext[i];
          boolean bool = localObject.equals(paramString);
          if (bool) {
            return true;
          }
          i += 1;
        }
      }
      return false;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      FLog.e("ReactNative", "Error while retrieving package info", paramContext);
    }
  }
  
  private static boolean permissionCheck(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return Settings.canDrawOverlays(paramContext);
    }
    return hasPermission(paramContext, "android.permission.SYSTEM_ALERT_WINDOW");
  }
  
  public static void requestPermission(Context paramContext)
  {
    if ((Build.VERSION.SDK_INT >= 23) && (!Settings.canDrawOverlays(paramContext)))
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("package:");
      ((StringBuilder)localObject).append(paramContext.getPackageName());
      localObject = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(((StringBuilder)localObject).toString()));
      ((Intent)localObject).setFlags(268435456);
      FLog.warn("ReactNative", "Overlay permissions needs to be granted in order for react native apps to run in dev mode");
      if (canHandleIntent(paramContext, (Intent)localObject)) {
        paramContext.startActivity((Intent)localObject);
      }
    }
  }
  
  public void setFpsDebugViewVisible(final boolean paramBoolean)
  {
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        if ((paramBoolean) && (mFPSDebugViewContainer == null))
        {
          if (!DebugOverlayController.permissionCheck(mReactContext))
          {
            FLog.d("ReactNative", "Wait for overlay permission to be set");
            return;
          }
          DebugOverlayController.access$002(DebugOverlayController.this, new FpsView(mReactContext));
          WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-1, -1, WindowOverlayCompat.TYPE_SYSTEM_OVERLAY, 24, -3);
          mWindowManager.addView(mFPSDebugViewContainer, localLayoutParams);
          return;
        }
        if ((!paramBoolean) && (mFPSDebugViewContainer != null))
        {
          mFPSDebugViewContainer.removeAllViews();
          mWindowManager.removeView(mFPSDebugViewContainer);
          DebugOverlayController.access$002(DebugOverlayController.this, null);
        }
      }
    });
  }
}
