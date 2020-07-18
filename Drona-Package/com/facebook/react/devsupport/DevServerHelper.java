package com.facebook.react.devsupport;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.R.string;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.network.OkHttpCallUtil;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.devsupport.interfaces.StackFrame;
import com.facebook.react.modules.systeminfo.AndroidInfoHelpers;
import com.facebook.react.packagerconnection.FileIoHandler;
import com.facebook.react.packagerconnection.JSPackagerClient;
import com.facebook.react.packagerconnection.NotificationOnlyHandler;
import com.facebook.react.packagerconnection.PackagerConnectionSettings;
import com.facebook.react.packagerconnection.ReconnectingWebSocket.ConnectionCallback;
import com.facebook.react.packagerconnection.RequestOnlyHandler;
import com.facebook.react.packagerconnection.Responder;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DevServerHelper
{
  private static final String DEBUGGER_MSG_DISABLE = "{ \"id\":1,\"method\":\"Debugger.disable\" }";
  private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
  private static final int LONG_POLL_FAILURE_DELAY_MS = 5000;
  private static final int LONG_POLL_KEEP_ALIVE_DURATION_MS = 120000;
  private static final String PACKAGER_OK_STATUS = "packager-status:running";
  public static final String RELOAD_APP_EXTRA_JS_PROXY = "jsproxy";
  private final BundleDownloader mBundleDownloader;
  private InspectorPackagerConnection.BundleStatusProvider mBundlerStatusProvider;
  private final OkHttpClient mClient;
  @Nullable
  private InspectorPackagerConnection mInspectorPackagerConnection;
  @Nullable
  private OkHttpClient mOnChangePollingClient;
  private boolean mOnChangePollingEnabled;
  @Nullable
  private OnServerContentChangeListener mOnServerContentChangeListener;
  private final String mPackageName;
  @Nullable
  private JSPackagerClient mPackagerClient;
  private final Handler mRestartOnChangePollingHandler;
  private final DevInternalSettings mSettings;
  
  public DevServerHelper(DevInternalSettings paramDevInternalSettings, String paramString, InspectorPackagerConnection.BundleStatusProvider paramBundleStatusProvider)
  {
    mSettings = paramDevInternalSettings;
    mBundlerStatusProvider = paramBundleStatusProvider;
    mClient = new OkHttpClient.Builder().connectTimeout(5000L, TimeUnit.MILLISECONDS).readTimeout(0L, TimeUnit.MILLISECONDS).writeTimeout(0L, TimeUnit.MILLISECONDS).build();
    mBundleDownloader = new BundleDownloader(mClient);
    mRestartOnChangePollingHandler = new Handler(Looper.getMainLooper());
    mPackageName = paramString;
  }
  
  private String createBundleURL(String paramString, BundleType paramBundleType)
  {
    return createBundleURL(paramString, paramBundleType, mSettings.getPackagerConnectionSettings().getDebugServerHost());
  }
  
  private String createBundleURL(String paramString1, BundleType paramBundleType, String paramString2)
  {
    return String.format(Locale.US, "http://%s/%s.%s?platform=android&dev=%s&minify=%s", new Object[] { paramString2, paramString1, paramBundleType.typeID(), Boolean.valueOf(getDevMode()), Boolean.valueOf(getJSMinifyMode()) });
  }
  
  private String createLaunchJSDevtoolsCommandUrl()
  {
    return String.format(Locale.US, "http://%s/launch-js-devtools", new Object[] { mSettings.getPackagerConnectionSettings().getDebugServerHost() });
  }
  
  private String createOnChangeEndpointUrl()
  {
    return String.format(Locale.US, "http://%s/onchange", new Object[] { mSettings.getPackagerConnectionSettings().getDebugServerHost() });
  }
  
  private static String createOpenStackFrameURL(String paramString)
  {
    return String.format(Locale.US, "http://%s/open-stack-frame", new Object[] { paramString });
  }
  
  private static String createPackagerStatusURL(String paramString)
  {
    return String.format(Locale.US, "http://%s/status", new Object[] { paramString });
  }
  
  private static String createResourceURL(String paramString1, String paramString2)
  {
    return String.format(Locale.US, "http://%s/%s", new Object[] { paramString1, paramString2 });
  }
  
  private static String createSymbolicateURL(String paramString)
  {
    return String.format(Locale.US, "http://%s/symbolicate", new Object[] { paramString });
  }
  
  private void enqueueOnChangeEndpointLongPolling()
  {
    Request localRequest = new Request.Builder().url(createOnChangeEndpointUrl()).tag(this).build();
    ((OkHttpClient)Assertions.assertNotNull(mOnChangePollingClient)).newCall(localRequest).enqueue(new Callback()
    {
      public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
      {
        if (mOnChangePollingEnabled)
        {
          FLog.d("ReactNative", "Error while requesting /onchange endpoint", paramAnonymousIOException);
          mRestartOnChangePollingHandler.postDelayed(new Runnable()
          {
            public void run()
            {
              DevServerHelper.this.handleOnChangePollingResponse(false);
            }
          }, 5000L);
        }
      }
      
      public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
        throws IOException
      {
        paramAnonymousCall = DevServerHelper.this;
        boolean bool;
        if (paramAnonymousResponse.code() == 205) {
          bool = true;
        } else {
          bool = false;
        }
        paramAnonymousCall.handleOnChangePollingResponse(bool);
      }
    });
  }
  
  private BundleDeltaClient.ClientType getDeltaClientType()
  {
    if (mSettings.isBundleDeltasCppEnabled()) {
      return BundleDeltaClient.ClientType.NATIVE;
    }
    if (mSettings.isBundleDeltasEnabled()) {
      return BundleDeltaClient.ClientType.DEV_SUPPORT;
    }
    return BundleDeltaClient.ClientType.NONE;
  }
  
  private boolean getDevMode()
  {
    return mSettings.isJSDevModeEnabled();
  }
  
  private String getHostForJSProxy()
  {
    String str = (String)Assertions.assertNotNull(mSettings.getPackagerConnectionSettings().getDebugServerHost());
    int i = str.lastIndexOf(':');
    if (i > -1)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("localhost");
      localStringBuilder.append(str.substring(i));
      return localStringBuilder.toString();
    }
    return "localhost";
  }
  
  private String getInspectorAttachUrl(Context paramContext, String paramString)
  {
    return String.format(Locale.US, "http://%s/nuclide/attach-debugger-nuclide?title=%s&app=%s&device=%s", new Object[] { AndroidInfoHelpers.getServerHost(paramContext), paramString, mPackageName, AndroidInfoHelpers.getFriendlyDeviceName() });
  }
  
  private String getInspectorDeviceUrl()
  {
    return String.format(Locale.US, "http://%s/inspector/device?name=%s&app=%s", new Object[] { mSettings.getPackagerConnectionSettings().getInspectorServerHost(), AndroidInfoHelpers.getFriendlyDeviceName(), mPackageName });
  }
  
  private boolean getJSMinifyMode()
  {
    return mSettings.isJSMinifyEnabled();
  }
  
  private void handleOnChangePollingResponse(boolean paramBoolean)
  {
    if (mOnChangePollingEnabled)
    {
      if (paramBoolean) {
        UiThreadUtil.runOnUiThread(new Runnable()
        {
          public void run()
          {
            if (mOnServerContentChangeListener != null) {
              mOnServerContentChangeListener.onServerContentChanged();
            }
          }
        });
      }
      enqueueOnChangeEndpointLongPolling();
    }
  }
  
  public void attachDebugger(final Context paramContext, final String paramString)
  {
    new AsyncTask()
    {
      protected Boolean doInBackground(Void... paramAnonymousVarArgs)
      {
        return Boolean.valueOf(doSync());
      }
      
      public boolean doSync()
      {
        Object localObject1 = DevServerHelper.this;
        Object localObject2 = paramContext;
        String str = paramString;
        try
        {
          localObject1 = ((DevServerHelper)localObject1).getInspectorAttachUrl((Context)localObject2, str);
          localObject2 = new OkHttpClient();
          ((OkHttpClient)localObject2).newCall(new Request.Builder().url((String)localObject1).build()).execute();
          return true;
        }
        catch (IOException localIOException)
        {
          FLog.e("ReactNative", "Failed to send attach request to Inspector", localIOException);
        }
        return false;
      }
      
      protected void onPostExecute(Boolean paramAnonymousBoolean)
      {
        if (!paramAnonymousBoolean.booleanValue())
        {
          paramAnonymousBoolean = paramContext.getString(R.string.catalyst_debug_nuclide_error);
          Toast.makeText(paramContext, paramAnonymousBoolean, 1).show();
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void closeInspectorConnection()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        if (mInspectorPackagerConnection != null)
        {
          mInspectorPackagerConnection.closeQuietly();
          DevServerHelper.access$202(DevServerHelper.this, null);
        }
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void closePackagerConnection()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        if (mPackagerClient != null)
        {
          mPackagerClient.close();
          DevServerHelper.access$002(DevServerHelper.this, null);
        }
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void disableDebugger()
  {
    if (mInspectorPackagerConnection != null) {
      mInspectorPackagerConnection.sendEventToAllConnections("{ \"id\":1,\"method\":\"Debugger.disable\" }");
    }
  }
  
  public void downloadBundleFromURL(DevBundleDownloadListener paramDevBundleDownloadListener, File paramFile, String paramString, BundleDownloader.BundleInfo paramBundleInfo)
  {
    mBundleDownloader.downloadBundleFromURL(paramDevBundleDownloadListener, paramFile, paramString, paramBundleInfo, getDeltaClientType());
  }
  
  public void downloadBundleFromURL(DevBundleDownloadListener paramDevBundleDownloadListener, File paramFile, String paramString, BundleDownloader.BundleInfo paramBundleInfo, Request.Builder paramBuilder)
  {
    mBundleDownloader.downloadBundleFromURL(paramDevBundleDownloadListener, paramFile, paramString, paramBundleInfo, getDeltaClientType(), paramBuilder);
  }
  
  public File downloadBundleResourceFromUrlSync(String paramString, File paramFile)
  {
    Object localObject1 = createResourceURL(mSettings.getPackagerConnectionSettings().getDebugServerHost(), paramString);
    localObject1 = new Request.Builder().url((String)localObject1).build();
    Object localObject2 = mClient;
    try
    {
      Response localResponse = ((OkHttpClient)localObject2).newCall((Request)localObject1).execute();
      try
      {
        boolean bool = localResponse.isSuccessful();
        if (!bool)
        {
          if (localResponse != null)
          {
            localResponse.close();
            return null;
          }
        }
        else
        {
          try
          {
            localObject2 = Okio.sink(paramFile);
            localObject1 = localObject2;
            try
            {
              Okio.buffer((Source)localResponse.body().source()).readAll((Sink)localObject2);
              if (localObject2 != null) {
                ((Sink)localObject2).close();
              }
              if (localResponse == null) {
                return paramFile;
              }
              localResponse.close();
              return paramFile;
            }
            catch (Throwable localThrowable2) {}
            if (localObject1 == null) {
              break label153;
            }
          }
          catch (Throwable localThrowable3)
          {
            localObject1 = null;
          }
          ((Sink)localObject1).close();
          label153:
          throw localThrowable3;
        }
      }
      catch (Throwable localThrowable1)
      {
        try
        {
          throw localThrowable1;
        }
        catch (Throwable localThrowable4)
        {
          if (localResponse != null) {
            if (localThrowable1 != null) {
              try
              {
                localResponse.close();
              }
              catch (Throwable localThrowable5)
              {
                localThrowable1.addSuppressed(localThrowable5);
              }
            } else {
              localThrowable5.close();
            }
          }
          throw localThrowable4;
        }
      }
      return null;
    }
    catch (Exception localException)
    {
      FLog.e("ReactNative", "Failed to fetch resource synchronously - resourcePath: \"%s\", outputFile: \"%s\"", new Object[] { paramString, paramFile.getAbsolutePath(), localException });
    }
    return paramFile;
  }
  
  public String getDevServerBundleURL(String paramString)
  {
    BundleType localBundleType;
    if (mSettings.isBundleDeltasEnabled()) {
      localBundleType = BundleType.DELTA;
    } else {
      localBundleType = BundleType.BUNDLE;
    }
    return createBundleURL(paramString, localBundleType, mSettings.getPackagerConnectionSettings().getDebugServerHost());
  }
  
  public String getJSBundleURLForRemoteDebugging(String paramString)
  {
    return createBundleURL(paramString, BundleType.BUNDLE, getHostForJSProxy());
  }
  
  public String getSourceMapUrl(String paramString)
  {
    return createBundleURL(paramString, BundleType.RECOVERY);
  }
  
  public String getSourceUrl(String paramString)
  {
    BundleType localBundleType;
    if (mSettings.isBundleDeltasEnabled()) {
      localBundleType = BundleType.DELTA;
    } else {
      localBundleType = BundleType.BUNDLE;
    }
    return createBundleURL(paramString, localBundleType);
  }
  
  public String getWebsocketProxyURL()
  {
    return String.format(Locale.US, "ws://%s/debugger-proxy?role=client", new Object[] { mSettings.getPackagerConnectionSettings().getDebugServerHost() });
  }
  
  public void isPackagerRunning(final PackagerStatusCallback paramPackagerStatusCallback)
  {
    Object localObject = createPackagerStatusURL(mSettings.getPackagerConnectionSettings().getDebugServerHost());
    localObject = new Request.Builder().url((String)localObject).build();
    mClient.newCall((Request)localObject).enqueue(new Callback()
    {
      public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
      {
        paramAnonymousCall = new StringBuilder();
        paramAnonymousCall.append("The packager does not seem to be running as we got an IOException requesting its status: ");
        paramAnonymousCall.append(paramAnonymousIOException.getMessage());
        FLog.warn("ReactNative", paramAnonymousCall.toString());
        paramPackagerStatusCallback.onPackagerStatusFetched(false);
      }
      
      public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
        throws IOException
      {
        if (!paramAnonymousResponse.isSuccessful())
        {
          paramAnonymousCall = new StringBuilder();
          paramAnonymousCall.append("Got non-success http code from packager when requesting status: ");
          paramAnonymousCall.append(paramAnonymousResponse.code());
          FLog.e("ReactNative", paramAnonymousCall.toString());
          paramPackagerStatusCallback.onPackagerStatusFetched(false);
          return;
        }
        paramAnonymousCall = paramAnonymousResponse.body();
        if (paramAnonymousCall == null)
        {
          FLog.e("ReactNative", "Got null body response from packager when requesting status");
          paramPackagerStatusCallback.onPackagerStatusFetched(false);
          return;
        }
        paramAnonymousCall = paramAnonymousCall.string();
        if (!"packager-status:running".equals(paramAnonymousCall))
        {
          paramAnonymousResponse = new StringBuilder();
          paramAnonymousResponse.append("Got unexpected response from packager when requesting status: ");
          paramAnonymousResponse.append(paramAnonymousCall);
          FLog.e("ReactNative", paramAnonymousResponse.toString());
          paramPackagerStatusCallback.onPackagerStatusFetched(false);
          return;
        }
        paramPackagerStatusCallback.onPackagerStatusFetched(true);
      }
    });
  }
  
  public void launchJSDevtools()
  {
    Request localRequest = new Request.Builder().url(createLaunchJSDevtoolsCommandUrl()).build();
    mClient.newCall(localRequest).enqueue(new Callback()
    {
      public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {}
      
      public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
        throws IOException
      {}
    });
  }
  
  public void openInspectorConnection()
  {
    if (mInspectorPackagerConnection != null)
    {
      FLog.warn("ReactNative", "Inspector connection already open, nooping.");
      return;
    }
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        DevServerHelper.access$202(DevServerHelper.this, new InspectorPackagerConnection(DevServerHelper.this.getInspectorDeviceUrl(), mPackageName, mBundlerStatusProvider));
        mInspectorPackagerConnection.connect();
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void openPackagerConnection(final String paramString, final PackagerCommandListener paramPackagerCommandListener)
  {
    if (mPackagerClient != null)
    {
      FLog.warn("ReactNative", "Packager connection already open, nooping.");
      return;
    }
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new HashMap();
        paramAnonymousVarArgs.put("reload", new NotificationOnlyHandler()
        {
          public void onNotification(Object paramAnonymous2Object)
          {
            val$commandListener.onPackagerReloadCommand();
          }
        });
        paramAnonymousVarArgs.put("devMenu", new NotificationOnlyHandler()
        {
          public void onNotification(Object paramAnonymous2Object)
          {
            val$commandListener.onPackagerDevMenuCommand();
          }
        });
        paramAnonymousVarArgs.put("captureHeap", new RequestOnlyHandler()
        {
          public void onRequest(Object paramAnonymous2Object, Responder paramAnonymous2Responder)
          {
            val$commandListener.onCaptureHeapCommand(paramAnonymous2Responder);
          }
        });
        Object localObject = paramPackagerCommandListener.customCommandHandlers();
        if (localObject != null) {
          paramAnonymousVarArgs.putAll((Map)localObject);
        }
        paramAnonymousVarArgs.putAll(new FileIoHandler().handlers());
        localObject = new ReconnectingWebSocket.ConnectionCallback()
        {
          public void onConnected()
          {
            val$commandListener.onPackagerConnected();
          }
          
          public void onDisconnected()
          {
            val$commandListener.onPackagerDisconnected();
          }
        };
        DevServerHelper.access$002(DevServerHelper.this, new JSPackagerClient(paramString, mSettings.getPackagerConnectionSettings(), paramAnonymousVarArgs, (ReconnectingWebSocket.ConnectionCallback)localObject));
        mPackagerClient.init();
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  public void openStackFrameCall(StackFrame paramStackFrame)
  {
    String str = createOpenStackFrameURL(mSettings.getPackagerConnectionSettings().getDebugServerHost());
    paramStackFrame = new Request.Builder().url(str).post(RequestBody.create(MediaType.parse("application/json"), paramStackFrame.toJSON().toString())).build();
    ((Call)Assertions.assertNotNull(mClient.newCall(paramStackFrame))).enqueue(new Callback()
    {
      public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
      {
        paramAnonymousCall = new StringBuilder();
        paramAnonymousCall.append("Got IOException when attempting to open stack frame: ");
        paramAnonymousCall.append(paramAnonymousIOException.getMessage());
        FLog.warn("ReactNative", paramAnonymousCall.toString());
      }
      
      public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
        throws IOException
      {}
    });
  }
  
  public void startPollingOnChangeEndpoint(OnServerContentChangeListener paramOnServerContentChangeListener)
  {
    if (mOnChangePollingEnabled) {
      return;
    }
    mOnChangePollingEnabled = true;
    mOnServerContentChangeListener = paramOnServerContentChangeListener;
    mOnChangePollingClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(1, 120000L, TimeUnit.MILLISECONDS)).connectTimeout(5000L, TimeUnit.MILLISECONDS).build();
    enqueueOnChangeEndpointLongPolling();
  }
  
  public void stopPollingOnChangeEndpoint()
  {
    mOnChangePollingEnabled = false;
    mRestartOnChangePollingHandler.removeCallbacksAndMessages(null);
    if (mOnChangePollingClient != null)
    {
      OkHttpCallUtil.cancelTag(mOnChangePollingClient, this);
      mOnChangePollingClient = null;
    }
    mOnServerContentChangeListener = null;
  }
  
  public void symbolicateStackTrace(Iterable paramIterable, final SymbolicationListener paramSymbolicationListener)
  {
    Object localObject1 = mSettings;
    try
    {
      Object localObject2 = createSymbolicateURL(((DevInternalSettings)localObject1).getPackagerConnectionSettings().getDebugServerHost());
      localObject1 = new JSONArray();
      paramIterable = paramIterable.iterator();
      for (;;)
      {
        boolean bool = paramIterable.hasNext();
        if (!bool) {
          break;
        }
        Object localObject3 = paramIterable.next();
        localObject3 = (StackFrame)localObject3;
        ((JSONArray)localObject1).put(((StackFrame)localObject3).toJSON());
      }
      paramIterable = new Request.Builder().url((String)localObject2);
      localObject2 = MediaType.parse("application/json");
      paramIterable = paramIterable.post(RequestBody.create((MediaType)localObject2, new JSONObject().put("stack", localObject1).toString())).build();
      localObject1 = mClient;
      paramIterable = Assertions.assertNotNull(((OkHttpClient)localObject1).newCall(paramIterable));
      paramIterable = (Call)paramIterable;
      paramIterable.enqueue(new Callback()
      {
        public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
        {
          paramAnonymousCall = new StringBuilder();
          paramAnonymousCall.append("Got IOException when attempting symbolicate stack trace: ");
          paramAnonymousCall.append(paramAnonymousIOException.getMessage());
          FLog.warn("ReactNative", paramAnonymousCall.toString());
          paramSymbolicationListener.onSymbolicationComplete(null);
        }
        
        public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
          throws IOException
        {
          paramAnonymousCall = paramSymbolicationListener;
          try
          {
            paramAnonymousCall.onSymbolicationComplete(Arrays.asList(StackTraceHelper.convertJsStackTrace(new JSONObject(paramAnonymousResponse.body().string()).getJSONArray("stack"))));
            return;
          }
          catch (JSONException paramAnonymousCall)
          {
            for (;;) {}
          }
          paramSymbolicationListener.onSymbolicationComplete(null);
        }
      });
      return;
    }
    catch (JSONException paramIterable)
    {
      paramSymbolicationListener = new StringBuilder();
      paramSymbolicationListener.append("Got JSONException when attempting symbolicate stack trace: ");
      paramSymbolicationListener.append(paramIterable.getMessage());
      FLog.warn("ReactNative", paramSymbolicationListener.toString());
    }
  }
  
  private static enum BundleType
  {
    BUNDLE("bundle"),  DELTA("delta"),  RECOVERY("map");
    
    private final String mTypeID;
    
    private BundleType(String paramString)
    {
      mTypeID = paramString;
    }
    
    public String typeID()
    {
      return mTypeID;
    }
  }
  
  public static abstract interface OnServerContentChangeListener
  {
    public abstract void onServerContentChanged();
  }
  
  public static abstract interface PackagerCommandListener
  {
    public abstract Map customCommandHandlers();
    
    public abstract void onCaptureHeapCommand(Responder paramResponder);
    
    public abstract void onPackagerConnected();
    
    public abstract void onPackagerDevMenuCommand();
    
    public abstract void onPackagerDisconnected();
    
    public abstract void onPackagerReloadCommand();
  }
  
  public static abstract interface PackagerCustomCommandProvider {}
  
  public static abstract interface SymbolicationListener
  {
    public abstract void onSymbolicationComplete(Iterable paramIterable);
  }
}
