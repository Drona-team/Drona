package com.facebook.react.packagerconnection;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public final class ReconnectingWebSocket
  extends WebSocketListener
{
  private static final int RECONNECT_DELAY_MS = 2000;
  private static final String TAG = "ReconnectingWebSocket";
  private boolean mClosed = false;
  @Nullable
  private ConnectionCallback mConnectionCallback;
  private final Handler mHandler;
  @Nullable
  private MessageCallback mMessageCallback;
  private boolean mSuppressConnectionErrors;
  private final String mUrl;
  @Nullable
  private WebSocket mWebSocket;
  
  public ReconnectingWebSocket(String paramString, MessageCallback paramMessageCallback, ConnectionCallback paramConnectionCallback)
  {
    mUrl = paramString;
    mMessageCallback = paramMessageCallback;
    mConnectionCallback = paramConnectionCallback;
    mHandler = new Handler(Looper.getMainLooper());
  }
  
  private void abort(String paramString, Throwable paramThrowable)
  {
    String str = TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Error occurred, shutting down websocket connection: ");
    localStringBuilder.append(paramString);
    FLog.e(str, localStringBuilder.toString(), paramThrowable);
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
  
  private void delayedReconnect()
  {
    try
    {
      if (!mClosed) {
        connect();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void reconnect()
  {
    if (!mClosed)
    {
      if (!mSuppressConnectionErrors)
      {
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Couldn't connect to \"");
        localStringBuilder.append(mUrl);
        localStringBuilder.append("\", will silently retry");
        FLog.warn(str, localStringBuilder.toString());
        mSuppressConnectionErrors = true;
      }
      mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          ReconnectingWebSocket.this.delayedReconnect();
        }
      }, 2000L);
      return;
    }
    throw new IllegalStateException("Can't reconnect closed client");
  }
  
  public void closeQuietly()
  {
    mClosed = true;
    closeWebSocketQuietly();
    mMessageCallback = null;
    if (mConnectionCallback != null) {
      mConnectionCallback.onDisconnected();
    }
  }
  
  public void connect()
  {
    if (!mClosed)
    {
      new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build().newWebSocket(new Request.Builder().url(mUrl).build(), this);
      return;
    }
    throw new IllegalStateException("Can't connect closed client");
  }
  
  public void onClosed(WebSocket paramWebSocket, int paramInt, String paramString)
  {
    try
    {
      mWebSocket = null;
      if (!mClosed)
      {
        if (mConnectionCallback != null) {
          mConnectionCallback.onDisconnected();
        }
        reconnect();
      }
      return;
    }
    catch (Throwable paramWebSocket)
    {
      throw paramWebSocket;
    }
  }
  
  public void onFailure(WebSocket paramWebSocket, Throwable paramThrowable, Response paramResponse)
  {
    try
    {
      if (mWebSocket != null) {
        abort("Websocket exception", paramThrowable);
      }
      if (!mClosed)
      {
        if (mConnectionCallback != null) {
          mConnectionCallback.onDisconnected();
        }
        reconnect();
      }
      return;
    }
    catch (Throwable paramWebSocket)
    {
      throw paramWebSocket;
    }
  }
  
  public void onMessage(WebSocket paramWebSocket, String paramString)
  {
    try
    {
      if (mMessageCallback != null) {
        mMessageCallback.onMessage(paramString);
      }
      return;
    }
    catch (Throwable paramWebSocket)
    {
      throw paramWebSocket;
    }
  }
  
  public void onMessage(WebSocket paramWebSocket, ByteString paramByteString)
  {
    try
    {
      if (mMessageCallback != null) {
        mMessageCallback.onMessage(paramByteString);
      }
      return;
    }
    catch (Throwable paramWebSocket)
    {
      throw paramWebSocket;
    }
  }
  
  public void onOpen(WebSocket paramWebSocket, Response paramResponse)
  {
    try
    {
      mWebSocket = paramWebSocket;
      mSuppressConnectionErrors = false;
      if (mConnectionCallback != null) {
        mConnectionCallback.onConnected();
      }
      return;
    }
    catch (Throwable paramWebSocket)
    {
      throw paramWebSocket;
    }
  }
  
  public void sendMessage(String paramString)
    throws IOException
  {
    try
    {
      if (mWebSocket != null)
      {
        mWebSocket.send(paramString);
        return;
      }
      throw new ClosedChannelException();
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void sendMessage(ByteString paramByteString)
    throws IOException
  {
    try
    {
      if (mWebSocket != null)
      {
        mWebSocket.send(paramByteString);
        return;
      }
      throw new ClosedChannelException();
    }
    catch (Throwable paramByteString)
    {
      throw paramByteString;
    }
  }
  
  public static abstract interface ConnectionCallback
  {
    public abstract void onConnected();
    
    public abstract void onDisconnected();
  }
  
  public static abstract interface MessageCallback
  {
    public abstract void onMessage(String paramString);
    
    public abstract void onMessage(ByteString paramByteString);
  }
}
