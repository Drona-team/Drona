package androidx.browser.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback.Stub;
import android.support.customtabs.ICustomTabsService;
import android.text.TextUtils;
import java.util.List;

public class CustomTabsClient
{
  private final ICustomTabsService mService;
  private final ComponentName mServiceComponentName;
  
  CustomTabsClient(ICustomTabsService paramICustomTabsService, ComponentName paramComponentName)
  {
    mService = paramICustomTabsService;
    mServiceComponentName = paramComponentName;
  }
  
  public static boolean bindCustomTabsService(Context paramContext, String paramString, CustomTabsServiceConnection paramCustomTabsServiceConnection)
  {
    Intent localIntent = new Intent("android.support.customtabs.action.CustomTabsService");
    if (!TextUtils.isEmpty(paramString)) {
      localIntent.setPackage(paramString);
    }
    return paramContext.bindService(localIntent, paramCustomTabsServiceConnection, 33);
  }
  
  public static boolean connectAndInitialize(Context paramContext, String paramString)
  {
    if (paramString == null) {
      return false;
    }
    paramContext = paramContext.getApplicationContext();
    CustomTabsServiceConnection local1 = new CustomTabsServiceConnection()
    {
      public final void onCustomTabsServiceConnected(ComponentName paramAnonymousComponentName, CustomTabsClient paramAnonymousCustomTabsClient)
      {
        paramAnonymousCustomTabsClient.warmup(0L);
        val$applicationContext.unbindService(this);
      }
      
      public final void onServiceDisconnected(ComponentName paramAnonymousComponentName) {}
    };
    try
    {
      boolean bool = bindCustomTabsService(paramContext, paramString, local1);
      return bool;
    }
    catch (SecurityException paramContext) {}
    return false;
  }
  
  public static String getPackageName(Context paramContext, List paramList)
  {
    return getPackageName(paramContext, paramList, false);
  }
  
  public static String getPackageName(Context paramContext, List paramList, boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  public Bundle extraCommand(String paramString, Bundle paramBundle)
  {
    ICustomTabsService localICustomTabsService = mService;
    try
    {
      paramString = localICustomTabsService.extraCommand(paramString, paramBundle);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      for (;;) {}
    }
    return null;
  }
  
  public CustomTabsSession newSession(final CustomTabsCallback paramCustomTabsCallback)
  {
    paramCustomTabsCallback = new ICustomTabsCallback.Stub()
    {
      private Handler mHandler = new Handler(Looper.getMainLooper());
      
      public void extraCallback(final String paramAnonymousString, final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {
          return;
        }
        mHandler.post(new Runnable()
        {
          public void run()
          {
            val$callback.extraCallback(paramAnonymousString, paramAnonymousBundle);
          }
        });
      }
      
      public void onMessageChannelReady(final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {
          return;
        }
        mHandler.post(new Runnable()
        {
          public void run()
          {
            val$callback.onMessageChannelReady(paramAnonymousBundle);
          }
        });
      }
      
      public void onNavigationEvent(final int paramAnonymousInt, final Bundle paramAnonymousBundle)
      {
        if (paramCustomTabsCallback == null) {
          return;
        }
        mHandler.post(new Runnable()
        {
          public void run()
          {
            val$callback.onNavigationEvent(paramAnonymousInt, paramAnonymousBundle);
          }
        });
      }
      
      public void onPostMessage(final String paramAnonymousString, final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {
          return;
        }
        mHandler.post(new Runnable()
        {
          public void run()
          {
            val$callback.onPostMessage(paramAnonymousString, paramAnonymousBundle);
          }
        });
      }
      
      public void onRelationshipValidationResult(final int paramAnonymousInt, final Uri paramAnonymousUri, final boolean paramAnonymousBoolean, final Bundle paramAnonymousBundle)
        throws RemoteException
      {
        if (paramCustomTabsCallback == null) {
          return;
        }
        mHandler.post(new Runnable()
        {
          public void run()
          {
            val$callback.onRelationshipValidationResult(paramAnonymousInt, paramAnonymousUri, paramAnonymousBoolean, paramAnonymousBundle);
          }
        });
      }
    };
    ICustomTabsService localICustomTabsService = mService;
    try
    {
      boolean bool = localICustomTabsService.newSession(paramCustomTabsCallback);
      if (!bool) {
        return null;
      }
      return new CustomTabsSession(mService, paramCustomTabsCallback, mServiceComponentName);
    }
    catch (RemoteException paramCustomTabsCallback) {}
    return null;
  }
  
  public boolean warmup(long paramLong)
  {
    ICustomTabsService localICustomTabsService = mService;
    try
    {
      boolean bool = localICustomTabsService.warmup(paramLong);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
    return false;
  }
}
