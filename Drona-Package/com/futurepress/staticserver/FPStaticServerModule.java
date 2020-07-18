package com.futurepress.staticserver;

import android.content.ContextWrapper;
import android.util.Log;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import fi.iki.elonen.SimpleWebServer;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

public class FPStaticServerModule
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener
{
  private static final String LOGTAG = "FPStaticServerModule";
  private boolean keep_alive = false;
  private String localPath = "";
  private boolean localhost_only = false;
  private int port = 9999;
  private final ReactApplicationContext reactContext;
  private SimpleWebServer server = null;
  private String user = "";
  private File www_root = null;
  
  public FPStaticServerModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    reactContext = paramReactApplicationContext;
  }
  
  private String __getLocalIpAddress()
  {
    try
    {
      Object localObject1 = NetworkInterface.getNetworkInterfaces();
      boolean bool = ((Enumeration)localObject1).hasMoreElements();
      if (bool)
      {
        Object localObject2 = ((Enumeration)localObject1).nextElement();
        localObject2 = (NetworkInterface)localObject2;
        localObject2 = ((NetworkInterface)localObject2).getInetAddresses();
        Object localObject3;
        do
        {
          do
          {
            bool = ((Enumeration)localObject2).hasMoreElements();
            if (!bool) {
              break;
            }
            localObject3 = ((Enumeration)localObject2).nextElement();
            localObject3 = (InetAddress)localObject3;
            bool = ((InetAddress)localObject3).isLoopbackAddress();
          } while (bool);
          localObject3 = ((InetAddress)localObject3).getHostAddress();
          bool = InetAddressUtils.isIPv4Address((String)localObject3);
        } while (!bool);
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("local IP: ");
        ((StringBuilder)localObject1).append((String)localObject3);
        Log.w("FPStaticServerModule", ((StringBuilder)localObject1).toString());
        return localObject3;
      }
    }
    catch (SocketException localSocketException)
    {
      Log.e("FPStaticServerModule", localSocketException.toString());
    }
    return "127.0.0.1";
  }
  
  private Integer findRandomOpenPort()
    throws IOException
  {
    try
    {
      ServerSocket localServerSocket = new ServerSocket(0);
      int i = localServerSocket.getLocalPort();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("port:");
      localStringBuilder.append(i);
      Log.w("FPStaticServerModule", localStringBuilder.toString());
      localServerSocket.close();
      return Integer.valueOf(i);
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
    return Integer.valueOf(0);
  }
  
  public String getName()
  {
    return "FPStaticServer";
  }
  
  public void onHostDestroy()
  {
    stop();
  }
  
  public void onHostPause() {}
  
  public void onHostResume() {}
  
  public void origin(Promise paramPromise)
  {
    if (server != null)
    {
      paramPromise.resolve(user);
      return;
    }
    paramPromise.resolve("");
  }
  
  public void start(String paramString1, String paramString2, Boolean paramBoolean1, Boolean paramBoolean2, Promise paramPromise)
  {
    if (server != null)
    {
      paramPromise.resolve(user);
      return;
    }
    if (paramString1 != null) {}
    for (;;)
    {
      try
      {
        i = Integer.parseInt(paramString1);
        port = i;
        if (port != 0) {}
      }
      catch (NumberFormatException paramString1)
      {
        int i;
        continue;
      }
      try
      {
        i = findRandomOpenPort().intValue();
        port = i;
      }
      catch (IOException paramString1) {}catch (NumberFormatException paramString1) {}
    }
    port = 9999;
    try
    {
      i = findRandomOpenPort().intValue();
      port = i;
    }
    catch (IOException paramString1)
    {
      for (;;) {}
    }
    port = 9999;
    if ((paramString2 != null) && ((paramString2.startsWith("/")) || (paramString2.startsWith("file:///"))))
    {
      www_root = new File(paramString2);
      localPath = www_root.getAbsolutePath();
    }
    else
    {
      www_root = new File(reactContext.getFilesDir(), paramString2);
      localPath = www_root.getAbsolutePath();
    }
    if (paramBoolean1 != null) {
      localhost_only = paramBoolean1.booleanValue();
    }
    if (paramBoolean2 != null) {
      keep_alive = paramBoolean2.booleanValue();
    }
    if (localhost_only)
    {
      i = port;
      paramString1 = www_root;
    }
    try
    {
      paramString1 = new WebServer("localhost", i, paramString1);
      server = paramString1;
      break label271;
      paramString1 = __getLocalIpAddress();
      i = port;
      paramString2 = www_root;
      paramString1 = new WebServer(paramString1, i, paramString2);
      server = paramString1;
      label271:
      if (localhost_only)
      {
        paramString1 = new StringBuilder();
        paramString1.append("http://localhost:");
        i = port;
        paramString1.append(i);
        paramString1 = paramString1.toString();
        user = paramString1;
      }
      else
      {
        paramString1 = new StringBuilder();
        paramString1.append("http://");
        paramString1.append(__getLocalIpAddress());
        paramString1.append(":");
        i = port;
        paramString1.append(i);
        paramString1 = paramString1.toString();
        user = paramString1;
      }
      paramString1 = server;
      paramString1.start();
      paramString1 = user;
      paramPromise.resolve(paramString1);
      return;
    }
    catch (IOException paramString1)
    {
      paramString1 = paramString1.getMessage();
      if ((server != null) && (paramString1.equals("bind failed: EADDRINUSE (Address already in use)")))
      {
        paramPromise.resolve(user);
        return;
      }
      paramPromise.reject(null, paramString1);
      return;
    }
  }
  
  public void stop()
  {
    if (server != null)
    {
      Log.w("FPStaticServerModule", "Stopped Server");
      server.stop();
      server = null;
    }
  }
}
