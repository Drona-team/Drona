package com.facebook.react.devsupport;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.common.JavascriptException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class JSDebuggerWebSocketClient
  extends WebSocketListener
{
  private static final String PAGE_KEY = "JSDebuggerWebSocketClient";
  private final ConcurrentHashMap<Integer, JSDebuggerCallback> mCallbacks = new ConcurrentHashMap();
  @Nullable
  private JSDebuggerCallback mConnectCallback;
  @Nullable
  private OkHttpClient mHttpClient;
  private final AtomicInteger mRequestID = new AtomicInteger();
  @Nullable
  private WebSocket mWebSocket;
  
  public JSDebuggerWebSocketClient() {}
  
  private void abort(String paramString, Throwable paramThrowable)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Error occurred, shutting down websocket connection: ");
    localStringBuilder.append(paramString);
    FLog.e("JSDebuggerWebSocketClient", localStringBuilder.toString(), paramThrowable);
    closeQuietly();
    if (mConnectCallback != null)
    {
      mConnectCallback.onFailure(paramThrowable);
      mConnectCallback = null;
    }
    paramString = mCallbacks.values().iterator();
    while (paramString.hasNext()) {
      ((JSDebuggerCallback)paramString.next()).onFailure(paramThrowable);
    }
    mCallbacks.clear();
  }
  
  private void sendMessage(int paramInt, String paramString)
  {
    if (mWebSocket == null)
    {
      triggerRequestFailure(paramInt, new IllegalStateException("WebSocket connection no longer valid"));
      return;
    }
    WebSocket localWebSocket = mWebSocket;
    try
    {
      localWebSocket.send(paramString);
      return;
    }
    catch (Exception paramString)
    {
      triggerRequestFailure(paramInt, paramString);
    }
  }
  
  private void triggerRequestFailure(int paramInt, Throwable paramThrowable)
  {
    JSDebuggerCallback localJSDebuggerCallback = (JSDebuggerCallback)mCallbacks.get(Integer.valueOf(paramInt));
    if (localJSDebuggerCallback != null)
    {
      mCallbacks.remove(Integer.valueOf(paramInt));
      localJSDebuggerCallback.onFailure(paramThrowable);
    }
  }
  
  private void triggerRequestSuccess(int paramInt, String paramString)
  {
    JSDebuggerCallback localJSDebuggerCallback = (JSDebuggerCallback)mCallbacks.get(Integer.valueOf(paramInt));
    if (localJSDebuggerCallback != null)
    {
      mCallbacks.remove(Integer.valueOf(paramInt));
      localJSDebuggerCallback.onSuccess(paramString);
    }
  }
  
  public void closeQuietly()
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
  
  public void connect(String paramString, JSDebuggerCallback paramJSDebuggerCallback)
  {
    if (mHttpClient == null)
    {
      mConnectCallback = paramJSDebuggerCallback;
      mHttpClient = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build();
      paramString = new Request.Builder().url(paramString).build();
      mHttpClient.newWebSocket(paramString, this);
      return;
    }
    throw new IllegalStateException("JSDebuggerWebSocketClient is already initialized.");
  }
  
  public void executeJSCall(String paramString1, String paramString2, JSDebuggerCallback paramJSDebuggerCallback)
  {
    int i = mRequestID.getAndIncrement();
    mCallbacks.put(Integer.valueOf(i), paramJSDebuggerCallback);
    try
    {
      paramJSDebuggerCallback = new StringWriter();
      JsonWriter localJsonWriter1 = new JsonWriter(paramJSDebuggerCallback);
      JsonWriter localJsonWriter2 = localJsonWriter1.beginObject().name("id");
      long l = i;
      localJsonWriter2.value(l).name("method").value(paramString1);
      paramJSDebuggerCallback.append(",\"arguments\":").append(paramString2);
      localJsonWriter1.endObject().close();
      sendMessage(i, paramJSDebuggerCallback.toString());
      return;
    }
    catch (IOException paramString1)
    {
      triggerRequestFailure(i, paramString1);
    }
  }
  
  public void loadApplicationScript(String paramString, HashMap paramHashMap, JSDebuggerCallback paramJSDebuggerCallback)
  {
    int i = mRequestID.getAndIncrement();
    mCallbacks.put(Integer.valueOf(i), paramJSDebuggerCallback);
    try
    {
      paramJSDebuggerCallback = new StringWriter();
      Object localObject1 = new JsonWriter(paramJSDebuggerCallback).beginObject().name("id");
      long l = i;
      paramString = ((JsonWriter)localObject1).value(l).name("method").value("executeApplicationScript").name("url").value(paramString).name("inject").beginObject();
      localObject1 = paramHashMap.keySet().iterator();
      for (;;)
      {
        boolean bool = ((Iterator)localObject1).hasNext();
        if (!bool) {
          break;
        }
        Object localObject2 = ((Iterator)localObject1).next();
        Object localObject3 = (String)localObject2;
        localObject2 = paramString.name((String)localObject3);
        localObject3 = paramHashMap.get(localObject3);
        localObject3 = (String)localObject3;
        ((JsonWriter)localObject2).value((String)localObject3);
      }
      paramString.endObject().endObject().close();
      sendMessage(i, paramJSDebuggerCallback.toString());
      return;
    }
    catch (IOException paramString)
    {
      triggerRequestFailure(i, paramString);
    }
  }
  
  public void onClosed(WebSocket paramWebSocket, int paramInt, String paramString)
  {
    mWebSocket = null;
  }
  
  public void onFailure(WebSocket paramWebSocket, Throwable paramThrowable, Response paramResponse)
  {
    abort("Websocket exception", paramThrowable);
  }
  
  public void onMessage(WebSocket paramWebSocket, String paramString)
  {
    String str = null;
    paramWebSocket = null;
    Object localObject = str;
    try
    {
      JsonReader localJsonReader = new JsonReader(new StringReader(paramString));
      localObject = str;
      localJsonReader.beginObject();
      paramString = null;
      for (;;)
      {
        localObject = paramWebSocket;
        boolean bool = localJsonReader.hasNext();
        if (!bool) {
          break;
        }
        localObject = paramWebSocket;
        str = localJsonReader.nextName();
        JsonToken localJsonToken1 = JsonToken.NULL;
        localObject = paramWebSocket;
        JsonToken localJsonToken2 = localJsonReader.peek();
        if (localJsonToken1 == localJsonToken2)
        {
          localObject = paramWebSocket;
          localJsonReader.skipValue();
        }
        else
        {
          localObject = paramWebSocket;
          bool = "replyID".equals(str);
          if (bool)
          {
            localObject = paramWebSocket;
            paramWebSocket = Integer.valueOf(localJsonReader.nextInt());
          }
          else
          {
            localObject = paramWebSocket;
            bool = "result".equals(str);
            if (bool)
            {
              localObject = paramWebSocket;
              paramString = localJsonReader.nextString();
            }
            else
            {
              localObject = paramWebSocket;
              bool = "error".equals(str);
              if (bool)
              {
                localObject = paramWebSocket;
                str = localJsonReader.nextString();
                localObject = paramWebSocket;
                abort(str, new JavascriptException(str));
              }
            }
          }
        }
      }
      if (paramWebSocket != null)
      {
        localObject = paramWebSocket;
        triggerRequestSuccess(paramWebSocket.intValue(), paramString);
        return;
      }
    }
    catch (IOException paramWebSocket)
    {
      if (localObject != null)
      {
        triggerRequestFailure(((Integer)localObject).intValue(), paramWebSocket);
        return;
      }
      abort("Parsing response message from websocket failed", paramWebSocket);
    }
  }
  
  public void onOpen(WebSocket paramWebSocket, Response paramResponse)
  {
    mWebSocket = paramWebSocket;
    ((JSDebuggerCallback)Assertions.assertNotNull(mConnectCallback)).onSuccess(null);
    mConnectCallback = null;
  }
  
  public void prepareJSRuntime(JSDebuggerCallback paramJSDebuggerCallback)
  {
    int i = mRequestID.getAndIncrement();
    mCallbacks.put(Integer.valueOf(i), paramJSDebuggerCallback);
    try
    {
      paramJSDebuggerCallback = new StringWriter();
      JsonWriter localJsonWriter = new JsonWriter(paramJSDebuggerCallback).beginObject().name("id");
      long l = i;
      localJsonWriter.value(l).name("method").value("prepareJSRuntime").endObject().close();
      sendMessage(i, paramJSDebuggerCallback.toString());
      return;
    }
    catch (IOException paramJSDebuggerCallback)
    {
      triggerRequestFailure(i, paramJSDebuggerCallback);
    }
  }
  
  public static abstract interface JSDebuggerCallback
  {
    public abstract void onFailure(Throwable paramThrowable);
    
    public abstract void onSuccess(String paramString);
  }
}
