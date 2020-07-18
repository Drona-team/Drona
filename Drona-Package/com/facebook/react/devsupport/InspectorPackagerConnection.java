package com.facebook.react.devsupport;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Inspector;
import com.facebook.react.bridge.Inspector.LocalConnection;
import com.facebook.react.bridge.Inspector.Page;
import com.facebook.react.bridge.Inspector.RemoteConnection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InspectorPackagerConnection
{
  private static final String PAGE_KEY = "InspectorPackagerConnection";
  private BundleStatusProvider mBundleStatusProvider;
  private final Connection mConnection;
  private final Map<String, Inspector.LocalConnection> mInspectorConnections;
  private final String mPackageName;
  
  public InspectorPackagerConnection(String paramString1, String paramString2, BundleStatusProvider paramBundleStatusProvider)
  {
    mConnection = new Connection(paramString1);
    mInspectorConnections = new HashMap();
    mPackageName = paramString2;
    mBundleStatusProvider = paramBundleStatusProvider;
  }
  
  private JSONArray getPages()
    throws JSONException
  {
    Object localObject = Inspector.getPages();
    JSONArray localJSONArray = new JSONArray();
    BundleStatus localBundleStatus = mBundleStatusProvider.getBundleStatus();
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Inspector.Page localPage = (Inspector.Page)((Iterator)localObject).next();
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("id", String.valueOf(localPage.getId()));
      localJSONObject.put("title", localPage.getTitle());
      localJSONObject.put("app", mPackageName);
      localJSONObject.put("vm", localPage.getVM());
      localJSONObject.put("isLastBundleDownloadSuccess", isLastDownloadSucess);
      localJSONObject.put("bundleUpdateTimestamp", updateTimestamp);
      localJSONArray.put(localJSONObject);
    }
    return localJSONArray;
  }
  
  private void handleConnect(final JSONObject paramJSONObject)
    throws JSONException
  {
    paramJSONObject = paramJSONObject.getString("pageId");
    if ((Inspector.LocalConnection)mInspectorConnections.remove(paramJSONObject) == null) {
      try
      {
        int i = Integer.parseInt(paramJSONObject);
        Inspector.LocalConnection localLocalConnection = Inspector.connect(i, new Inspector.RemoteConnection()
        {
          public void onDisconnect()
          {
            Object localObject1 = InspectorPackagerConnection.this;
            try
            {
              localObject1 = mInspectorConnections;
              Object localObject2 = paramJSONObject;
              ((Map)localObject1).remove(localObject2);
              localObject1 = InspectorPackagerConnection.this;
              localObject2 = InspectorPackagerConnection.this;
              String str = paramJSONObject;
              ((InspectorPackagerConnection)localObject1).sendEvent("disconnect", InspectorPackagerConnection.access$200((InspectorPackagerConnection)localObject2, str));
              return;
            }
            catch (JSONException localJSONException)
            {
              FLog.w("InspectorPackagerConnection", "Couldn't send event to packager", localJSONException);
            }
          }
          
          public void onMessage(String paramAnonymousString)
          {
            InspectorPackagerConnection localInspectorPackagerConnection = InspectorPackagerConnection.this;
            String str = paramJSONObject;
            try
            {
              localInspectorPackagerConnection.sendWrappedEvent(str, paramAnonymousString);
              return;
            }
            catch (JSONException paramAnonymousString)
            {
              FLog.w("InspectorPackagerConnection", "Couldn't send event to packager", paramAnonymousString);
            }
          }
        });
        localObject = mInspectorConnections;
        ((Map)localObject).put(paramJSONObject, localLocalConnection);
        return;
      }
      catch (Exception localException)
      {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Failed to open page: ");
        ((StringBuilder)localObject).append(paramJSONObject);
        FLog.w("InspectorPackagerConnection", ((StringBuilder)localObject).toString(), localException);
        sendEvent("disconnect", makePageIdPayload(paramJSONObject));
        return;
      }
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Already connected: ");
    localStringBuilder.append(paramJSONObject);
    throw new IllegalStateException(localStringBuilder.toString());
  }
  
  private void handleDisconnect(JSONObject paramJSONObject)
    throws JSONException
  {
    paramJSONObject = paramJSONObject.getString("pageId");
    paramJSONObject = (Inspector.LocalConnection)mInspectorConnections.remove(paramJSONObject);
    if (paramJSONObject == null) {
      return;
    }
    paramJSONObject.disconnect();
  }
  
  private void handleWrappedEvent(JSONObject paramJSONObject)
    throws JSONException
  {
    String str = paramJSONObject.getString("pageId");
    paramJSONObject = paramJSONObject.getString("wrappedEvent");
    Inspector.LocalConnection localLocalConnection = (Inspector.LocalConnection)mInspectorConnections.get(str);
    if (localLocalConnection != null)
    {
      localLocalConnection.sendMessage(paramJSONObject);
      return;
    }
    paramJSONObject = new StringBuilder();
    paramJSONObject.append("Not connected: ");
    paramJSONObject.append(str);
    throw new IllegalStateException(paramJSONObject.toString());
  }
  
  private JSONObject makePageIdPayload(String paramString)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("pageId", paramString);
    return localJSONObject;
  }
  
  private void sendEvent(String paramString, Object paramObject)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("event", paramString);
    localJSONObject.put("payload", paramObject);
    mConnection.send(localJSONObject);
  }
  
  private void sendWrappedEvent(String paramString1, String paramString2)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("pageId", paramString1);
    localJSONObject.put("wrappedEvent", paramString2);
    sendEvent("wrappedEvent", localJSONObject);
  }
  
  void closeAllConnections()
  {
    Iterator localIterator = mInspectorConnections.entrySet().iterator();
    while (localIterator.hasNext()) {
      ((Inspector.LocalConnection)((Map.Entry)localIterator.next()).getValue()).disconnect();
    }
    mInspectorConnections.clear();
  }
  
  public void closeQuietly()
  {
    mConnection.close();
  }
  
  public void connect()
  {
    mConnection.connect();
  }
  
  void handleProxyMessage(JSONObject paramJSONObject)
    throws JSONException, IOException
  {
    String str = paramJSONObject.getString("event");
    int i = str.hashCode();
    if (i != 530405532)
    {
      if (i != 951351530)
      {
        if (i != 1328613653)
        {
          if ((i == 1962251790) && (str.equals("getPages")))
          {
            i = 0;
            break label103;
          }
        }
        else if (str.equals("wrappedEvent"))
        {
          i = 1;
          break label103;
        }
      }
      else if (str.equals("connect"))
      {
        i = 2;
        break label103;
      }
    }
    else if (str.equals("disconnect"))
    {
      i = 3;
      break label103;
    }
    i = -1;
    switch (i)
    {
    default: 
      paramJSONObject = new StringBuilder();
      paramJSONObject.append("Unknown event: ");
      paramJSONObject.append(str);
      throw new IllegalArgumentException(paramJSONObject.toString());
    case 3: 
      handleDisconnect(paramJSONObject.getJSONObject("payload"));
      return;
    case 2: 
      handleConnect(paramJSONObject.getJSONObject("payload"));
      return;
    case 1: 
      label103:
      handleWrappedEvent(paramJSONObject.getJSONObject("payload"));
      return;
    }
    sendEvent("getPages", getPages());
  }
  
  public void sendEventToAllConnections(String paramString)
  {
    Iterator localIterator = mInspectorConnections.entrySet().iterator();
    while (localIterator.hasNext()) {
      ((Inspector.LocalConnection)((Map.Entry)localIterator.next()).getValue()).sendMessage(paramString);
    }
  }
  
  public static class BundleStatus
  {
    public Boolean isLastDownloadSucess;
    public long updateTimestamp = -1L;
    
    public BundleStatus()
    {
      this(Boolean.valueOf(false), -1L);
    }
    
    public BundleStatus(Boolean paramBoolean, long paramLong)
    {
      isLastDownloadSucess = paramBoolean;
      updateTimestamp = paramLong;
    }
  }
  
  public static abstract interface BundleStatusProvider
  {
    public abstract InspectorPackagerConnection.BundleStatus getBundleStatus();
  }
  
  private class Connection
    extends WebSocketListener
  {
    private static final int RECONNECT_DELAY_MS = 2000;
    private boolean mClosed;
    private final Handler mHandler;
    private OkHttpClient mHttpClient;
    private boolean mSuppressConnectionErrors;
    private final String mUrl;
    @Nullable
    private WebSocket mWebSocket;
    
    public Connection(String paramString)
    {
      mUrl = paramString;
      mHandler = new Handler(Looper.getMainLooper());
    }
    
    private void abort(String paramString, Throwable paramThrowable)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Error occurred, shutting down websocket connection: ");
      localStringBuilder.append(paramString);
      FLog.e("InspectorPackagerConnection", localStringBuilder.toString(), paramThrowable);
      closeAllConnections();
      closeWebSocketQuietly();
    }
    
    private void closeWebSocketQuietly()
    {
      if (mWebSocket != null)
      {
        WebSocket localWebSocket = mWebSocket;
        try
        {
          localWebSocket.close(1000, "End of session");
          mWebSocket = null;
          return;
        }
        catch (Exception localException)
        {
          for (;;) {}
        }
      }
    }
    
    private void reconnect()
    {
      if (!mClosed)
      {
        if (!mSuppressConnectionErrors)
        {
          FLog.warn("InspectorPackagerConnection", "Couldn't connect to packager, will silently retry");
          mSuppressConnectionErrors = true;
        }
        mHandler.postDelayed(new Runnable()
        {
          public void run()
          {
            if (!mClosed) {
              connect();
            }
          }
        }, 2000L);
        return;
      }
      throw new IllegalStateException("Can't reconnect closed client");
    }
    
    public void close()
    {
      mClosed = true;
      if (mWebSocket != null)
      {
        WebSocket localWebSocket = mWebSocket;
        try
        {
          localWebSocket.close(1000, "End of session");
          mWebSocket = null;
          return;
        }
        catch (Exception localException)
        {
          for (;;) {}
        }
      }
    }
    
    public void connect()
    {
      if (!mClosed)
      {
        if (mHttpClient == null) {
          mHttpClient = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build();
        }
        Request localRequest = new Request.Builder().url(mUrl).build();
        mHttpClient.newWebSocket(localRequest, this);
        return;
      }
      throw new IllegalStateException("Can't connect closed client");
    }
    
    public void onClosed(WebSocket paramWebSocket, int paramInt, String paramString)
    {
      mWebSocket = null;
      closeAllConnections();
      if (!mClosed) {
        reconnect();
      }
    }
    
    public void onFailure(WebSocket paramWebSocket, Throwable paramThrowable, Response paramResponse)
    {
      if (mWebSocket != null) {
        abort("Websocket exception", paramThrowable);
      }
      if (!mClosed) {
        reconnect();
      }
    }
    
    public void onMessage(WebSocket paramWebSocket, String paramString)
    {
      paramWebSocket = InspectorPackagerConnection.this;
      try
      {
        paramWebSocket.handleProxyMessage(new JSONObject(paramString));
        return;
      }
      catch (Exception paramWebSocket)
      {
        throw new RuntimeException(paramWebSocket);
      }
    }
    
    public void onOpen(WebSocket paramWebSocket, Response paramResponse)
    {
      mWebSocket = paramWebSocket;
    }
    
    public void send(final JSONObject paramJSONObject)
    {
      new AsyncTask()
      {
        protected Void doInBackground(WebSocket... paramAnonymousVarArgs)
        {
          if (paramAnonymousVarArgs != null)
          {
            if (paramAnonymousVarArgs.length == 0) {
              return null;
            }
            paramAnonymousVarArgs = paramAnonymousVarArgs[0];
            JSONObject localJSONObject = paramJSONObject;
            try
            {
              paramAnonymousVarArgs.send(localJSONObject.toString());
              return null;
            }
            catch (Exception paramAnonymousVarArgs)
            {
              FLog.w("InspectorPackagerConnection", "Couldn't send event to packager", paramAnonymousVarArgs);
            }
          }
          return null;
        }
      }.execute(new WebSocket[] { mWebSocket });
    }
  }
}
