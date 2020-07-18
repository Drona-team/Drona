package com.facebook.react.modules.websocket;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@ReactModule(hasConstants=false, name="WebSocketModule")
public final class WebSocketModule
  extends ReactContextBaseJavaModule
{
  public static final String NAME = "WebSocketModule";
  private final Map<Integer, ContentHandler> mContentHandlers = new ConcurrentHashMap();
  private ForwardingCookieHandler mCookieHandler;
  private ReactContext mReactContext;
  private final Map<Integer, WebSocket> mWebSocketConnections = new ConcurrentHashMap();
  
  public WebSocketModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    mReactContext = paramReactApplicationContext;
    mCookieHandler = new ForwardingCookieHandler(paramReactApplicationContext);
  }
  
  private String getCookie(String paramString)
  {
    try
    {
      localObject = new URI(getDefaultOrigin(paramString));
      ForwardingCookieHandler localForwardingCookieHandler = mCookieHandler;
      localObject = localForwardingCookieHandler.get((URI)localObject, new HashMap()).get("Cookie");
      localObject = (List)localObject;
      if (localObject != null)
      {
        boolean bool = ((List)localObject).isEmpty();
        if (!bool)
        {
          localObject = ((List)localObject).get(0);
          return (String)localObject;
        }
      }
      return null;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      Object localObject;
      for (;;) {}
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to get cookie from ");
    ((StringBuilder)localObject).append(paramString);
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  private static String getDefaultOrigin(String paramString)
  {
    Object localObject1 = "";
    try
    {
      Object localObject2 = new URI(paramString);
      String str = ((URI)localObject2).getScheme();
      int i = str.hashCode();
      boolean bool;
      if (i != 3804)
      {
        if (i != 118039)
        {
          if (i != 3213448)
          {
            if (i == 99617003)
            {
              bool = str.equals("https");
              if (bool)
              {
                i = 3;
                break label124;
              }
            }
          }
          else
          {
            bool = str.equals("http");
            if (bool)
            {
              i = 2;
              break label124;
            }
          }
        }
        else
        {
          bool = str.equals("wss");
          if (bool)
          {
            i = 0;
            break label124;
          }
        }
      }
      else
      {
        bool = str.equals("ws");
        if (bool)
        {
          i = 1;
          break label124;
        }
      }
      i = -1;
      switch (i)
      {
      default: 
        break;
      case 2: 
      case 3: 
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("");
        ((StringBuilder)localObject1).append(((URI)localObject2).getScheme());
        localObject1 = ((StringBuilder)localObject1).toString();
        break;
      case 1: 
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("");
        ((StringBuilder)localObject1).append("http");
        localObject1 = ((StringBuilder)localObject1).toString();
        break;
      case 0: 
        label124:
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("");
        ((StringBuilder)localObject1).append("https");
        localObject1 = ((StringBuilder)localObject1).toString();
      }
      i = ((URI)localObject2).getPort();
      if (i != -1)
      {
        str = ((URI)localObject2).getHost();
        i = ((URI)localObject2).getPort();
        localObject1 = String.format("%s://%s:%s", new Object[] { localObject1, str, Integer.valueOf(i) });
        return localObject1;
      }
      localObject2 = ((URI)localObject2).getHost();
      localObject1 = String.format("%s://%s", new Object[] { localObject1, localObject2 });
      return localObject1;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      for (;;) {}
    }
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("Unable to set ");
    ((StringBuilder)localObject1).append(paramString);
    ((StringBuilder)localObject1).append(" as default origin header");
    throw new IllegalArgumentException(((StringBuilder)localObject1).toString());
  }
  
  private void notifyWebSocketFailed(int paramInt, String paramString)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putInt("id", paramInt);
    localWritableMap.putString("message", paramString);
    sendEvent("websocketFailed", localWritableMap);
  }
  
  private void sendEvent(String paramString, WritableMap paramWritableMap)
  {
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(paramString, paramWritableMap);
  }
  
  public void close(int paramInt1, String paramString, int paramInt2)
  {
    Object localObject = (WebSocket)mWebSocketConnections.get(Integer.valueOf(paramInt2));
    if (localObject == null) {
      return;
    }
    try
    {
      ((WebSocket)localObject).close(paramInt1, paramString);
      paramString = mWebSocketConnections;
      paramString.remove(Integer.valueOf(paramInt2));
      paramString = mContentHandlers;
      paramString.remove(Integer.valueOf(paramInt2));
      return;
    }
    catch (Exception paramString)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Could not close WebSocket connection for id ");
      ((StringBuilder)localObject).append(paramInt2);
      FLog.e("ReactNative", ((StringBuilder)localObject).toString(), paramString);
    }
  }
  
  public void connect(String paramString, ReadableArray paramReadableArray, ReadableMap paramReadableMap, final int paramInt)
  {
    OkHttpClient localOkHttpClient = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build();
    Request.Builder localBuilder = new Request.Builder().tag(Integer.valueOf(paramInt)).url(paramString);
    Object localObject = getCookie(paramString);
    if (localObject != null) {
      localBuilder.addHeader("Cookie", (String)localObject);
    }
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("headers")) && (paramReadableMap.getType("headers").equals(ReadableType.GIF)))
    {
      paramReadableMap = paramReadableMap.getMap("headers");
      localObject = paramReadableMap.keySetIterator();
      if (!paramReadableMap.hasKey("origin")) {
        localBuilder.addHeader("origin", getDefaultOrigin(paramString));
      }
    }
    while (((ReadableMapKeySetIterator)localObject).hasNextKey())
    {
      paramString = ((ReadableMapKeySetIterator)localObject).nextKey();
      if (ReadableType.String.equals(paramReadableMap.getType(paramString)))
      {
        localBuilder.addHeader(paramString, paramReadableMap.getString(paramString));
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Ignoring: requested ");
        localStringBuilder.append(paramString);
        localStringBuilder.append(", value not a string");
        FLog.warn("ReactNative", localStringBuilder.toString());
        continue;
        localBuilder.addHeader("origin", getDefaultOrigin(paramString));
      }
    }
    if ((paramReadableArray != null) && (paramReadableArray.size() > 0))
    {
      paramString = new StringBuilder("");
      int i = 0;
      while (i < paramReadableArray.size())
      {
        paramReadableMap = paramReadableArray.getString(i).trim();
        if ((!paramReadableMap.isEmpty()) && (!paramReadableMap.contains(",")))
        {
          paramString.append(paramReadableMap);
          paramString.append(",");
        }
        i += 1;
      }
      if (paramString.length() > 0)
      {
        paramString.replace(paramString.length() - 1, paramString.length(), "");
        localBuilder.addHeader("Sec-WebSocket-Protocol", paramString.toString());
      }
    }
    localOkHttpClient.newWebSocket(localBuilder.build(), new WebSocketListener()
    {
      public void onClosed(WebSocket paramAnonymousWebSocket, int paramAnonymousInt, String paramAnonymousString)
      {
        paramAnonymousWebSocket = Arguments.createMap();
        paramAnonymousWebSocket.putInt("id", paramInt);
        paramAnonymousWebSocket.putInt("code", paramAnonymousInt);
        paramAnonymousWebSocket.putString("reason", paramAnonymousString);
        WebSocketModule.this.sendEvent("websocketClosed", paramAnonymousWebSocket);
      }
      
      public void onClosing(WebSocket paramAnonymousWebSocket, int paramAnonymousInt, String paramAnonymousString)
      {
        paramAnonymousWebSocket.close(paramAnonymousInt, paramAnonymousString);
      }
      
      public void onFailure(WebSocket paramAnonymousWebSocket, Throwable paramAnonymousThrowable, Response paramAnonymousResponse)
      {
        WebSocketModule.this.notifyWebSocketFailed(paramInt, paramAnonymousThrowable.getMessage());
      }
      
      public void onMessage(WebSocket paramAnonymousWebSocket, String paramAnonymousString)
      {
        paramAnonymousWebSocket = Arguments.createMap();
        paramAnonymousWebSocket.putInt("id", paramInt);
        paramAnonymousWebSocket.putString("type", "text");
        WebSocketModule.ContentHandler localContentHandler = (WebSocketModule.ContentHandler)mContentHandlers.get(Integer.valueOf(paramInt));
        if (localContentHandler != null) {
          localContentHandler.onMessage(paramAnonymousString, paramAnonymousWebSocket);
        } else {
          paramAnonymousWebSocket.putString("data", paramAnonymousString);
        }
        WebSocketModule.this.sendEvent("websocketMessage", paramAnonymousWebSocket);
      }
      
      public void onMessage(WebSocket paramAnonymousWebSocket, ByteString paramAnonymousByteString)
      {
        paramAnonymousWebSocket = Arguments.createMap();
        paramAnonymousWebSocket.putInt("id", paramInt);
        paramAnonymousWebSocket.putString("type", "binary");
        WebSocketModule.ContentHandler localContentHandler = (WebSocketModule.ContentHandler)mContentHandlers.get(Integer.valueOf(paramInt));
        if (localContentHandler != null) {
          localContentHandler.onMessage(paramAnonymousByteString, paramAnonymousWebSocket);
        } else {
          paramAnonymousWebSocket.putString("data", paramAnonymousByteString.base64());
        }
        WebSocketModule.this.sendEvent("websocketMessage", paramAnonymousWebSocket);
      }
      
      public void onOpen(WebSocket paramAnonymousWebSocket, Response paramAnonymousResponse)
      {
        mWebSocketConnections.put(Integer.valueOf(paramInt), paramAnonymousWebSocket);
        paramAnonymousWebSocket = Arguments.createMap();
        paramAnonymousWebSocket.putInt("id", paramInt);
        paramAnonymousWebSocket.putString("protocol", paramAnonymousResponse.header("Sec-WebSocket-Protocol", ""));
        WebSocketModule.this.sendEvent("websocketOpen", paramAnonymousWebSocket);
      }
    });
    localOkHttpClient.dispatcher().executorService().shutdown();
  }
  
  public String getName()
  {
    return "WebSocketModule";
  }
  
  public void ping(int paramInt)
  {
    Object localObject = (WebSocket)mWebSocketConnections.get(Integer.valueOf(paramInt));
    if (localObject == null)
    {
      localObject = Arguments.createMap();
      ((WritableMap)localObject).putInt("id", paramInt);
      ((WritableMap)localObject).putString("message", "client is null");
      sendEvent("websocketFailed", (WritableMap)localObject);
      localObject = Arguments.createMap();
      ((WritableMap)localObject).putInt("id", paramInt);
      ((WritableMap)localObject).putInt("code", 0);
      ((WritableMap)localObject).putString("reason", "client is null");
      sendEvent("websocketClosed", (WritableMap)localObject);
      mWebSocketConnections.remove(Integer.valueOf(paramInt));
      mContentHandlers.remove(Integer.valueOf(paramInt));
      return;
    }
    ByteString localByteString = ByteString.EMPTY;
    try
    {
      ((WebSocket)localObject).send(localByteString);
      return;
    }
    catch (Exception localException)
    {
      notifyWebSocketFailed(paramInt, localException.getMessage());
    }
  }
  
  public void send(String paramString, int paramInt)
  {
    WebSocket localWebSocket = (WebSocket)mWebSocketConnections.get(Integer.valueOf(paramInt));
    if (localWebSocket == null)
    {
      paramString = Arguments.createMap();
      paramString.putInt("id", paramInt);
      paramString.putString("message", "client is null");
      sendEvent("websocketFailed", paramString);
      paramString = Arguments.createMap();
      paramString.putInt("id", paramInt);
      paramString.putInt("code", 0);
      paramString.putString("reason", "client is null");
      sendEvent("websocketClosed", paramString);
      mWebSocketConnections.remove(Integer.valueOf(paramInt));
      mContentHandlers.remove(Integer.valueOf(paramInt));
      return;
    }
    try
    {
      localWebSocket.send(paramString);
      return;
    }
    catch (Exception paramString)
    {
      notifyWebSocketFailed(paramInt, paramString.getMessage());
    }
  }
  
  public void sendBinary(String paramString, int paramInt)
  {
    WebSocket localWebSocket = (WebSocket)mWebSocketConnections.get(Integer.valueOf(paramInt));
    if (localWebSocket == null)
    {
      paramString = Arguments.createMap();
      paramString.putInt("id", paramInt);
      paramString.putString("message", "client is null");
      sendEvent("websocketFailed", paramString);
      paramString = Arguments.createMap();
      paramString.putInt("id", paramInt);
      paramString.putInt("code", 0);
      paramString.putString("reason", "client is null");
      sendEvent("websocketClosed", paramString);
      mWebSocketConnections.remove(Integer.valueOf(paramInt));
      mContentHandlers.remove(Integer.valueOf(paramInt));
      return;
    }
    try
    {
      localWebSocket.send(ByteString.decodeBase64(paramString));
      return;
    }
    catch (Exception paramString)
    {
      notifyWebSocketFailed(paramInt, paramString.getMessage());
    }
  }
  
  public void sendBinary(ByteString paramByteString, int paramInt)
  {
    WebSocket localWebSocket = (WebSocket)mWebSocketConnections.get(Integer.valueOf(paramInt));
    if (localWebSocket == null)
    {
      paramByteString = Arguments.createMap();
      paramByteString.putInt("id", paramInt);
      paramByteString.putString("message", "client is null");
      sendEvent("websocketFailed", paramByteString);
      paramByteString = Arguments.createMap();
      paramByteString.putInt("id", paramInt);
      paramByteString.putInt("code", 0);
      paramByteString.putString("reason", "client is null");
      sendEvent("websocketClosed", paramByteString);
      mWebSocketConnections.remove(Integer.valueOf(paramInt));
      mContentHandlers.remove(Integer.valueOf(paramInt));
      return;
    }
    try
    {
      localWebSocket.send(paramByteString);
      return;
    }
    catch (Exception paramByteString)
    {
      notifyWebSocketFailed(paramInt, paramByteString.getMessage());
    }
  }
  
  public void setContentHandler(int paramInt, ContentHandler paramContentHandler)
  {
    if (paramContentHandler != null)
    {
      mContentHandlers.put(Integer.valueOf(paramInt), paramContentHandler);
      return;
    }
    mContentHandlers.remove(Integer.valueOf(paramInt));
  }
  
  public static abstract interface ContentHandler
  {
    public abstract void onMessage(String paramString, WritableMap paramWritableMap);
    
    public abstract void onMessage(ByteString paramByteString, WritableMap paramWritableMap);
  }
}
