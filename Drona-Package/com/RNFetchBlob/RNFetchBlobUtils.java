package com.RNFetchBlob;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class RNFetchBlobUtils
{
  public RNFetchBlobUtils() {}
  
  public static void emitWarningEvent(String paramString)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putString("event", "warn");
    localWritableMap.putString("detail", paramString);
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)RNFetchBlob.RCTContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("RNFetchBlobMessage", localWritableMap);
  }
  
  public static String getMD5(String paramString)
  {
    try
    {
      Object localObject = MessageDigest.getInstance("MD5");
      ((MessageDigest)localObject).update(paramString.getBytes());
      paramString = ((MessageDigest)localObject).digest();
      localObject = new StringBuilder();
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        int k = paramString[i];
        ((StringBuilder)localObject).append(String.format("%02x", new Object[] { Integer.valueOf(k & 0xFF) }));
        i += 1;
      }
      paramString = ((StringBuilder)localObject).toString();
      return paramString;
    }
    catch (Exception paramString) {}catch (Throwable paramString)
    {
      try
      {
        paramString.printStackTrace();
        return null;
      }
      catch (Throwable paramString) {}
      paramString = paramString;
      return null;
    }
    return null;
  }
  
  public static OkHttpClient.Builder getUnsafeOkHttpClient(OkHttpClient paramOkHttpClient)
  {
    try
    {
      X509TrustManager local1 = new X509TrustManager()
      {
        public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
          throws CertificateException
        {}
        
        public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
          throws CertificateException
        {}
        
        public X509Certificate[] getAcceptedIssuers()
        {
          return new X509Certificate[0];
        }
      };
      Object localObject = SSLContext.getInstance("SSL");
      SecureRandom localSecureRandom = new SecureRandom();
      TrustManager[] arrayOfTrustManager = (TrustManager[])new TrustManager[] { local1 };
      ((SSLContext)localObject).init(null, arrayOfTrustManager, localSecureRandom);
      localObject = ((SSLContext)localObject).getSocketFactory();
      paramOkHttpClient = paramOkHttpClient.newBuilder();
      paramOkHttpClient.sslSocketFactory((SSLSocketFactory)localObject, local1);
      paramOkHttpClient.hostnameVerifier(new HostnameVerifier()
      {
        public boolean verify(String paramAnonymousString, SSLSession paramAnonymousSSLSession)
        {
          return true;
        }
      });
      return paramOkHttpClient;
    }
    catch (Exception paramOkHttpClient)
    {
      throw new RuntimeException(paramOkHttpClient);
    }
  }
}
