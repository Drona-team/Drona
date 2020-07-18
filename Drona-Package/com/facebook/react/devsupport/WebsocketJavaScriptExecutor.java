package com.facebook.react.devsupport;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.JavaJSExecutor;
import com.facebook.react.bridge.JavaJSExecutor.ProxyExecutorException;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class WebsocketJavaScriptExecutor
  implements JavaJSExecutor
{
  private static final int CONNECT_RETRY_COUNT = 3;
  private static final long CONNECT_TIMEOUT_MS = 5000L;
  private final HashMap<String, String> mInjectedObjects = new HashMap();
  @Nullable
  private JSDebuggerWebSocketClient mWebSocketClient;
  
  public WebsocketJavaScriptExecutor() {}
  
  private void connectInternal(String paramString, final JSExecutorConnectCallback paramJSExecutorConnectCallback)
  {
    final JSDebuggerWebSocketClient localJSDebuggerWebSocketClient = new JSDebuggerWebSocketClient();
    final Handler localHandler = new Handler(Looper.getMainLooper());
    localJSDebuggerWebSocketClient.connect(paramString, new JSDebuggerWebSocketClient.JSDebuggerCallback()
    {
      private boolean didSendResult = false;
      
      public void onFailure(Throwable paramAnonymousThrowable)
      {
        localHandler.removeCallbacksAndMessages(null);
        if (!didSendResult)
        {
          paramJSExecutorConnectCallback.onFailure(paramAnonymousThrowable);
          didSendResult = true;
        }
      }
      
      public void onSuccess(String paramAnonymousString)
      {
        localJSDebuggerWebSocketClient.prepareJSRuntime(new JSDebuggerWebSocketClient.JSDebuggerCallback()
        {
          public void onFailure(Throwable paramAnonymous2Throwable)
          {
            val$timeoutHandler.removeCallbacksAndMessages(null);
            if (!didSendResult)
            {
              val$callback.onFailure(paramAnonymous2Throwable);
              WebsocketJavaScriptExecutor.2.access$202(WebsocketJavaScriptExecutor.2.this, true);
            }
          }
          
          public void onSuccess(String paramAnonymous2String)
          {
            val$timeoutHandler.removeCallbacksAndMessages(null);
            WebsocketJavaScriptExecutor.access$102(WebsocketJavaScriptExecutor.this, val$client);
            if (!didSendResult)
            {
              val$callback.onSuccess();
              WebsocketJavaScriptExecutor.2.access$202(WebsocketJavaScriptExecutor.2.this, true);
            }
          }
        });
      }
    });
    localHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        localJSDebuggerWebSocketClient.closeQuietly();
        paramJSExecutorConnectCallback.onFailure(new WebsocketJavaScriptExecutor.WebsocketExecutorTimeoutException("Timeout while connecting to remote debugger"));
      }
    }, 5000L);
  }
  
  public void close()
  {
    if (mWebSocketClient != null) {
      mWebSocketClient.closeQuietly();
    }
  }
  
  public void connect(final String paramString, final JSExecutorConnectCallback paramJSExecutorConnectCallback)
  {
    connectInternal(paramString, new JSExecutorConnectCallback()
    {
      public void onFailure(Throwable paramAnonymousThrowable)
      {
        if (val$retryCount.decrementAndGet() <= 0)
        {
          paramJSExecutorConnectCallback.onFailure(paramAnonymousThrowable);
          return;
        }
        WebsocketJavaScriptExecutor.this.connectInternal(paramString, this);
      }
      
      public void onSuccess()
      {
        paramJSExecutorConnectCallback.onSuccess();
      }
    });
  }
  
  public String executeJSCall(String paramString1, String paramString2)
    throws JavaJSExecutor.ProxyExecutorException
  {
    JSExecutorCallbackFuture localJSExecutorCallbackFuture = new JSExecutorCallbackFuture(null);
    ((JSDebuggerWebSocketClient)Assertions.assertNotNull(mWebSocketClient)).executeJSCall(paramString1, paramString2, localJSExecutorCallbackFuture);
    try
    {
      paramString1 = localJSExecutorCallbackFuture.doInBackground();
      return paramString1;
    }
    catch (Throwable paramString1)
    {
      throw new JavaJSExecutor.ProxyExecutorException(paramString1);
    }
  }
  
  public void loadApplicationScript(String paramString)
    throws JavaJSExecutor.ProxyExecutorException
  {
    JSExecutorCallbackFuture localJSExecutorCallbackFuture = new JSExecutorCallbackFuture(null);
    ((JSDebuggerWebSocketClient)Assertions.assertNotNull(mWebSocketClient)).loadApplicationScript(paramString, mInjectedObjects, localJSExecutorCallbackFuture);
    try
    {
      localJSExecutorCallbackFuture.doInBackground();
      return;
    }
    catch (Throwable paramString)
    {
      throw new JavaJSExecutor.ProxyExecutorException(paramString);
    }
  }
  
  public void setGlobalVariable(String paramString1, String paramString2)
  {
    mInjectedObjects.put(paramString1, paramString2);
  }
  
  private static class JSExecutorCallbackFuture
    implements JSDebuggerWebSocketClient.JSDebuggerCallback
  {
    @Nullable
    private Throwable mCause;
    @Nullable
    private String mResponse;
    private final Semaphore mSemaphore = new Semaphore(0);
    
    private JSExecutorCallbackFuture() {}
    
    public String doInBackground()
      throws Throwable
    {
      mSemaphore.acquire();
      if (mCause == null) {
        return mResponse;
      }
      throw mCause;
    }
    
    public void onFailure(Throwable paramThrowable)
    {
      mCause = paramThrowable;
      mSemaphore.release();
    }
    
    public void onSuccess(String paramString)
    {
      mResponse = paramString;
      mSemaphore.release();
    }
  }
  
  public static abstract interface JSExecutorConnectCallback
  {
    public abstract void onFailure(Throwable paramThrowable);
    
    public abstract void onSuccess();
  }
  
  public static class WebsocketExecutorTimeoutException
    extends Exception
  {
    public WebsocketExecutorTimeoutException(String paramString)
    {
      super();
    }
  }
}