package com.facebook.react.modules.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.GuardedResultAsyncTask;
import com.facebook.react.bridge.ReactContext;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ForwardingCookieHandler
  extends CookieHandler
{
  private static final String COOKIE_HEADER = "Cookie";
  private static final boolean USES_LEGACY_STORE;
  private static final String VERSION_ONE_HEADER = "Set-cookie2";
  private static final String VERSION_ZERO_HEADER = "Set-cookie";
  private final ReactContext mContext;
  @Nullable
  private CookieManager mCookieManager;
  private final CookieSaver mCookieSaver;
  
  static
  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 21) {
      bool = true;
    } else {
      bool = false;
    }
    USES_LEGACY_STORE = bool;
  }
  
  public ForwardingCookieHandler(ReactContext paramReactContext)
  {
    mContext = paramReactContext;
    mCookieSaver = new CookieSaver();
  }
  
  private void addCookieAsync(String paramString1, String paramString2)
  {
    CookieManager localCookieManager = getCookieManager();
    if (localCookieManager != null) {
      localCookieManager.setCookie(paramString1, paramString2, null);
    }
  }
  
  private void addCookies(final String paramString, final List paramList)
  {
    final CookieManager localCookieManager = getCookieManager();
    if (localCookieManager == null) {
      return;
    }
    if (USES_LEGACY_STORE)
    {
      runInBackground(new Runnable()
      {
        public void run()
        {
          Iterator localIterator = paramList.iterator();
          while (localIterator.hasNext())
          {
            String str = (String)localIterator.next();
            localCookieManager.setCookie(paramString, str);
          }
          mCookieSaver.onCookiesModified();
        }
      });
      return;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      addCookieAsync(paramString, (String)paramList.next());
    }
    localCookieManager.flush();
    mCookieSaver.onCookiesModified();
  }
  
  private void clearCookiesAsync(final Callback paramCallback)
  {
    CookieManager localCookieManager = getCookieManager();
    if (localCookieManager != null) {
      localCookieManager.removeAllCookies(new ValueCallback()
      {
        public void onReceiveValue(Boolean paramAnonymousBoolean)
        {
          mCookieSaver.onCookiesModified();
          paramCallback.invoke(new Object[] { paramAnonymousBoolean });
        }
      });
    }
  }
  
  private CookieManager getCookieManager()
  {
    if (mCookieManager == null) {
      possiblyWorkaroundSyncManager(mContext);
    }
    try
    {
      try
      {
        CookieManager localCookieManager = CookieManager.getInstance();
        mCookieManager = localCookieManager;
        if (USES_LEGACY_STORE) {
          mCookieManager.removeExpiredCookie();
        }
      }
      catch (Exception localException)
      {
        String str = localException.getMessage();
        if ((str != null) && (str.contains("No WebView installed"))) {
          return null;
        }
        throw localException;
      }
      return mCookieManager;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  private static boolean isCookieHeader(String paramString)
  {
    return (paramString.equalsIgnoreCase("Set-cookie")) || (paramString.equalsIgnoreCase("Set-cookie2"));
  }
  
  private static void possiblyWorkaroundSyncManager(Context paramContext)
  {
    if (USES_LEGACY_STORE) {
      CookieSyncManager.createInstance(paramContext).sync();
    }
  }
  
  private void runInBackground(final Runnable paramRunnable)
  {
    new GuardedAsyncTask(mContext)
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        paramRunnable.run();
      }
    }.execute(new Void[0]);
  }
  
  public void clearCookies(final Callback paramCallback)
  {
    if (USES_LEGACY_STORE)
    {
      new GuardedResultAsyncTask(mContext)
      {
        protected Boolean doInBackgroundGuarded()
        {
          CookieManager localCookieManager = ForwardingCookieHandler.this.getCookieManager();
          if (localCookieManager != null) {
            localCookieManager.removeAllCookie();
          }
          mCookieSaver.onCookiesModified();
          return Boolean.valueOf(true);
        }
        
        protected void onPostExecuteGuarded(Boolean paramAnonymousBoolean)
        {
          paramCallback.invoke(new Object[] { paramAnonymousBoolean });
        }
      }.execute(new Void[0]);
      return;
    }
    clearCookiesAsync(paramCallback);
  }
  
  public void destroy()
  {
    if (USES_LEGACY_STORE)
    {
      CookieManager localCookieManager = getCookieManager();
      if (localCookieManager != null) {
        localCookieManager.removeExpiredCookie();
      }
      mCookieSaver.persistCookies();
    }
  }
  
  public Map get(URI paramURI, Map paramMap)
    throws IOException
  {
    paramMap = getCookieManager();
    if (paramMap == null) {
      return Collections.emptyMap();
    }
    paramURI = paramMap.getCookie(paramURI.toString());
    if (TextUtils.isEmpty(paramURI)) {
      return Collections.emptyMap();
    }
    return Collections.singletonMap("Cookie", Collections.singletonList(paramURI));
  }
  
  public void put(URI paramURI, Map paramMap)
    throws IOException
  {
    paramURI = paramURI.toString();
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      String str = (String)localEntry.getKey();
      if ((str != null) && (isCookieHeader(str))) {
        addCookies(paramURI, (List)localEntry.getValue());
      }
    }
  }
  
  private class CookieSaver
  {
    private static final int MSG_PERSIST_COOKIES = 1;
    private static final int TIMEOUT = 30000;
    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback()
    {
      public boolean handleMessage(Message paramAnonymousMessage)
      {
        if (what == 1)
        {
          persistCookies();
          return true;
        }
        return false;
      }
    });
    
    public CookieSaver() {}
    
    private void flush()
    {
      CookieManager localCookieManager = ForwardingCookieHandler.this.getCookieManager();
      if (localCookieManager != null) {
        localCookieManager.flush();
      }
    }
    
    public void onCookiesModified()
    {
      if (ForwardingCookieHandler.USES_LEGACY_STORE) {
        mHandler.sendEmptyMessageDelayed(1, 30000L);
      }
    }
    
    public void persistCookies()
    {
      mHandler.removeMessages(1);
      ForwardingCookieHandler.this.runInBackground(new Runnable()
      {
        public void run()
        {
          if (ForwardingCookieHandler.USES_LEGACY_STORE)
          {
            CookieSyncManager.getInstance().sync();
            return;
          }
          ForwardingCookieHandler.CookieSaver.this.flush();
        }
      });
    }
  }
}