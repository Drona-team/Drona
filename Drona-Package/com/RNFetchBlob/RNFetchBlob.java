package com.RNFetchBlob;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.SparseArray;
import androidx.core.content.FileProvider;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.network.CookieJarContainer;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.facebook.react.modules.network.OkHttpClientProvider;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class RNFetchBlob
  extends ReactContextBaseJavaModule
{
  private static boolean ActionViewVisible = false;
  static ReactApplicationContext RCTContext;
  static LinkedBlockingQueue<Runnable> fsTaskQueue;
  private static ThreadPoolExecutor fsThreadPool;
  private static SparseArray<Promise> promiseTable = new SparseArray();
  private static LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue();
  private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 5000L, TimeUnit.MILLISECONDS, taskQueue);
  private final OkHttpClient mClient = OkHttpClientProvider.getOkHttpClient();
  
  static
  {
    fsTaskQueue = new LinkedBlockingQueue();
    fsThreadPool = new ThreadPoolExecutor(2, 10, 5000L, TimeUnit.MILLISECONDS, taskQueue);
  }
  
  public RNFetchBlob(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    ForwardingCookieHandler localForwardingCookieHandler = new ForwardingCookieHandler(paramReactApplicationContext);
    ((CookieJarContainer)mClient.cookieJar()).setCookieJar((CookieJar)new JavaNetCookieJar(localForwardingCookieHandler));
    RCTContext = paramReactApplicationContext;
    paramReactApplicationContext.addActivityEventListener(new ActivityEventListener()
    {
      public void onActivityResult(Activity paramAnonymousActivity, int paramAnonymousInt1, int paramAnonymousInt2, Intent paramAnonymousIntent)
      {
        if ((paramAnonymousInt1 == RNFetchBlobConst.GET_CONTENT_INTENT.intValue()) && (paramAnonymousInt2 == -1))
        {
          paramAnonymousActivity = paramAnonymousIntent.getData();
          ((Promise)RNFetchBlob.promiseTable.get(RNFetchBlobConst.GET_CONTENT_INTENT.intValue())).resolve(paramAnonymousActivity.toString());
          RNFetchBlob.promiseTable.remove(RNFetchBlobConst.GET_CONTENT_INTENT.intValue());
        }
      }
      
      public void onNewIntent(Intent paramAnonymousIntent) {}
    });
  }
  
  public void actionViewIntent(String paramString1, String paramString2, final Promise paramPromise)
  {
    try
    {
      Object localObject1 = getCurrentActivity();
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append(getReactApplicationContext().getPackageName());
      ((StringBuilder)localObject2).append(".provider");
      localObject2 = ((StringBuilder)localObject2).toString();
      localObject1 = FileProvider.getUriForFile((Context)localObject1, (String)localObject2, new File(paramString1));
      if (Build.VERSION.SDK_INT >= 24)
      {
        paramString1 = new Intent("android.intent.action.VIEW").setDataAndType((Uri)localObject1, paramString2);
        paramString1.setFlags(1);
        paramString1.addFlags(268435456);
        paramString2 = paramString1.resolveActivity(getCurrentActivity().getPackageManager());
        if (paramString2 != null) {
          getReactApplicationContext().startActivity(paramString1);
        }
      }
      else
      {
        localObject1 = new Intent("android.intent.action.VIEW");
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("file://");
        ((StringBuilder)localObject2).append(paramString1);
        paramString1 = ((Intent)localObject1).setDataAndType(Uri.parse(((StringBuilder)localObject2).toString()), paramString2).setFlags(268435456);
        getReactApplicationContext().startActivity(paramString1);
      }
      ActionViewVisible = true;
      paramString1 = new LifecycleEventListener()
      {
        public void onHostDestroy() {}
        
        public void onHostPause() {}
        
        public void onHostResume()
        {
          if (RNFetchBlob.ActionViewVisible) {
            paramPromise.resolve(null);
          }
          RNFetchBlob.RCTContext.removeLifecycleEventListener(this);
        }
      };
      paramString2 = RCTContext;
      paramString2.addLifecycleEventListener(paramString1);
      return;
    }
    catch (Exception paramString1)
    {
      paramPromise.reject("EUNSPECIFIED", paramString1.getLocalizedMessage());
    }
  }
  
  public void addCompleteDownload(ReadableMap paramReadableMap, Promise paramPromise)
  {
    Object localObject1 = RCTContext;
    Object localObject2 = RCTContext;
    DownloadManager localDownloadManager = (DownloadManager)((ReactContext)localObject1).getSystemService("download");
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("path")))
    {
      String str2 = RNFetchBlobFS.normalizePath(paramReadableMap.getString("path"));
      if (str2 == null)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("RNFetchblob.addCompleteDownload can not resolve URI:");
        ((StringBuilder)localObject1).append(paramReadableMap.getString("path"));
        paramPromise.reject("EINVAL", ((StringBuilder)localObject1).toString());
        return;
      }
      try
      {
        WritableMap localWritableMap = RNFetchBlobFS.statFile(str2);
        boolean bool = paramReadableMap.hasKey("title");
        if (bool) {
          localObject1 = paramReadableMap.getString("title");
        } else {
          localObject1 = "";
        }
        bool = paramReadableMap.hasKey("description");
        if (bool) {
          localObject2 = paramReadableMap.getString("description");
        } else {
          localObject2 = "";
        }
        bool = paramReadableMap.hasKey("mime");
        String str1;
        if (bool) {
          str1 = paramReadableMap.getString("mime");
        } else {
          str1 = null;
        }
        long l = Long.valueOf(localWritableMap.getString("size")).longValue();
        bool = paramReadableMap.hasKey("showNotification");
        if (bool)
        {
          bool = paramReadableMap.getBoolean("showNotification");
          if (bool)
          {
            bool = true;
            break label262;
          }
        }
        bool = false;
        label262:
        localDownloadManager.addCompletedDownload((String)localObject1, (String)localObject2, true, str1, str2, l, bool);
        paramPromise.resolve(null);
        return;
      }
      catch (Exception paramReadableMap)
      {
        paramPromise.reject("EUNSPECIFIED", paramReadableMap.getLocalizedMessage());
        return;
      }
    }
    paramPromise.reject("EINVAL", "RNFetchblob.addCompleteDownload config or path missing.");
  }
  
  public void cancelRequest(String paramString, Callback paramCallback)
  {
    try
    {
      RNFetchBlobReq.cancelTask(paramString);
      paramCallback.invoke(new Object[] { null, paramString });
      return;
    }
    catch (Exception paramString)
    {
      paramCallback.invoke(new Object[] { paramString.getLocalizedMessage(), null });
    }
  }
  
  public void closeStream(String paramString, Callback paramCallback)
  {
    RNFetchBlobFS.closeStream(paramString, paramCallback);
  }
  
  public void createFile(final String paramString1, final String paramString2, final String paramString3, final Promise paramPromise)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.createFile(paramString1, paramString2, paramString3, paramPromise);
      }
    });
  }
  
  public void createFileASCII(final String paramString, final ReadableArray paramReadableArray, final Promise paramPromise)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.createFileASCII(paramString, paramReadableArray, paramPromise);
      }
    });
  }
  
  public void enableProgressReport(String paramString, int paramInt1, int paramInt2)
  {
    RNFetchBlobProgressConfig localRNFetchBlobProgressConfig = new RNFetchBlobProgressConfig(true, paramInt1, paramInt2, RNFetchBlobProgressConfig.ReportType.Download);
    RNFetchBlobReq.progressReport.put(paramString, localRNFetchBlobProgressConfig);
  }
  
  public void enableUploadProgressReport(String paramString, int paramInt1, int paramInt2)
  {
    RNFetchBlobProgressConfig localRNFetchBlobProgressConfig = new RNFetchBlobProgressConfig(true, paramInt1, paramInt2, RNFetchBlobProgressConfig.ReportType.Upload);
    RNFetchBlobReq.uploadProgressReport.put(paramString, localRNFetchBlobProgressConfig);
  }
  
  public void enqueue(final Callback paramCallback)
  {
    fsThreadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.update(paramCallback);
      }
    });
  }
  
  public void exists(String paramString, Callback paramCallback)
  {
    RNFetchBlobFS.exists(paramString, paramCallback);
  }
  
  public void fetch(String paramString1, String paramString2, Callback paramCallback)
  {
    RNFetchBlobFS.fetch(paramString1, paramString2, paramCallback);
  }
  
  public void fetchBlob(ReadableMap paramReadableMap1, String paramString1, String paramString2, String paramString3, ReadableMap paramReadableMap2, String paramString4, Callback paramCallback)
  {
    new RNFetchBlobReq(paramReadableMap1, paramString1, paramString2, paramString3, paramReadableMap2, paramString4, null, mClient, paramCallback).run();
  }
  
  public void fetchBlobForm(ReadableMap paramReadableMap1, String paramString1, String paramString2, String paramString3, ReadableMap paramReadableMap2, ReadableArray paramReadableArray, Callback paramCallback)
  {
    new RNFetchBlobReq(paramReadableMap1, paramString1, paramString2, paramString3, paramReadableMap2, null, paramReadableArray, mClient, paramCallback).run();
  }
  
  public Map getConstants()
  {
    return RNFetchBlobFS.getSystemfolders(getReactApplicationContext());
  }
  
  public void getContentIntent(String paramString, Promise paramPromise)
  {
    Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
    if (paramString != null) {
      localIntent.setType(paramString);
    } else {
      localIntent.setType("*/*");
    }
    promiseTable.put(RNFetchBlobConst.GET_CONTENT_INTENT.intValue(), paramPromise);
    getReactApplicationContext().startActivityForResult(localIntent, RNFetchBlobConst.GET_CONTENT_INTENT.intValue(), null);
  }
  
  public String getName()
  {
    return "RNFetchBlob";
  }
  
  public void getSDCardApplicationDir(Promise paramPromise)
  {
    RNFetchBlobFS.getSDCardApplicationDir(getReactApplicationContext(), paramPromise);
  }
  
  public void getSDCardDir(Promise paramPromise)
  {
    RNFetchBlobFS.getSDCardDir(paramPromise);
  }
  
  public void hash(final String paramString1, final String paramString2, final Promise paramPromise)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.hash(paramString1, paramString2, paramPromise);
      }
    });
  }
  
  public void lstat(String paramString, Callback paramCallback)
  {
    RNFetchBlobFS.lstat(paramString, paramCallback);
  }
  
  public void mkdir(String paramString, Promise paramPromise)
  {
    RNFetchBlobFS.mkdir(paramString, paramPromise);
  }
  
  public void readFile(final String paramString1, final String paramString2, final Promise paramPromise)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.readFile(paramString1, paramString2, paramPromise);
      }
    });
  }
  
  public void readStream(final String paramString1, final String paramString2, final int paramInt1, final int paramInt2, final String paramString3)
  {
    final ReactApplicationContext localReactApplicationContext = getReactApplicationContext();
    fsThreadPool.execute(new Runnable()
    {
      public void run()
      {
        new RNFetchBlobFS(localReactApplicationContext).readStream(paramString1, paramString2, paramInt1, paramInt2, paramString3);
      }
    });
  }
  
  public void removeSession(ReadableArray paramReadableArray, Callback paramCallback)
  {
    RNFetchBlobFS.removeSession(paramReadableArray, paramCallback);
  }
  
  public void scanFile(final ReadableArray paramReadableArray, final Callback paramCallback)
  {
    final ReactApplicationContext localReactApplicationContext = getReactApplicationContext();
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        int j = paramReadableArray.size();
        String[] arrayOfString1 = new String[j];
        String[] arrayOfString2 = new String[j];
        int i = 0;
        while (i < j)
        {
          ReadableMap localReadableMap = paramReadableArray.getMap(i);
          if (localReadableMap.hasKey("path"))
          {
            arrayOfString1[i] = localReadableMap.getString("path");
            if (localReadableMap.hasKey("mime")) {
              arrayOfString2[i] = localReadableMap.getString("mime");
            } else {
              arrayOfString2[i] = null;
            }
          }
          i += 1;
        }
        new RNFetchBlobFS(localReactApplicationContext).scanFile(arrayOfString1, arrayOfString2, paramCallback);
      }
    });
  }
  
  public void sendRequest(final String paramString1, final String paramString2, final Callback paramCallback)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.execute(paramString1, paramString2, paramCallback);
      }
    });
  }
  
  public void slice(String paramString1, String paramString2, int paramInt1, int paramInt2, Promise paramPromise)
  {
    RNFetchBlobFS.slice(paramString1, paramString2, paramInt1, paramInt2, "", paramPromise);
  }
  
  public void stat(String paramString, Callback paramCallback)
  {
    RNFetchBlobFS.stat(paramString, paramCallback);
  }
  
  public void toggleStarred(String paramString, Promise paramPromise)
  {
    RNFetchBlobFS.execute(paramString, paramPromise);
  }
  
  public void unlink(String paramString, Callback paramCallback)
  {
    RNFetchBlobFS.unlink(paramString, paramCallback);
  }
  
  public void writeArrayChunk(String paramString, ReadableArray paramReadableArray, Callback paramCallback)
  {
    RNFetchBlobFS.writeArrayChunk(paramString, paramReadableArray, paramCallback);
  }
  
  public void writeChunk(String paramString1, String paramString2, Callback paramCallback)
  {
    RNFetchBlobFS.writeChunk(paramString1, paramString2, paramCallback);
  }
  
  public void writeFile(final String paramString1, final String paramString2, final String paramString3, final boolean paramBoolean, final Promise paramPromise)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.writeFile(paramString1, paramString2, paramString3, paramBoolean, paramPromise);
      }
    });
  }
  
  public void writeFileArray(final String paramString, final ReadableArray paramReadableArray, final boolean paramBoolean, final Promise paramPromise)
  {
    threadPool.execute(new Runnable()
    {
      public void run()
      {
        RNFetchBlobFS.writeFile(paramString, paramReadableArray, paramBoolean, paramPromise);
      }
    });
  }
  
  public void writeStream(String paramString1, String paramString2, boolean paramBoolean, Callback paramCallback)
  {
    new RNFetchBlobFS(getReactApplicationContext()).writeStream(paramString1, paramString2, paramBoolean, paramCallback);
  }
}