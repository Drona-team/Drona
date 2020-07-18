package com.facebook.react.modules.network;

import android.content.Context;
import android.os.Build.VERSION;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import java.io.File;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.ConnectionSpec.Builder;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.TlsVersion;

public class OkHttpClientProvider
{
  @Nullable
  private static OkHttpClient sClient;
  @Nullable
  private static OkHttpClientFactory sFactory;
  
  public OkHttpClientProvider() {}
  
  public static OkHttpClient createClient()
  {
    if (sFactory != null) {
      return sFactory.createNewNetworkModuleClient();
    }
    return createClientBuilder().build();
  }
  
  public static OkHttpClient createClient(Context paramContext)
  {
    if (sFactory != null) {
      return sFactory.createNewNetworkModuleClient();
    }
    return createClientBuilder(paramContext).build();
  }
  
  public static OkHttpClient.Builder createClientBuilder()
  {
    OkHttpClient.Builder localBuilder = new OkHttpClient.Builder().connectTimeout(0L, TimeUnit.MILLISECONDS).readTimeout(0L, TimeUnit.MILLISECONDS).writeTimeout(0L, TimeUnit.MILLISECONDS).cookieJar(new ReactCookieJarContainer());
    try
    {
      Object localObject = Class.forName("org.conscrypt.OpenSSLProvider").newInstance();
      localObject = (Provider)localObject;
      Security.insertProviderAt((Provider)localObject, 1);
      return localBuilder;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    return enableTls12OnPreLollipop(localBuilder);
  }
  
  public static OkHttpClient.Builder createClientBuilder(Context paramContext)
  {
    return createClientBuilder(paramContext, 10485760);
  }
  
  public static OkHttpClient.Builder createClientBuilder(Context paramContext, int paramInt)
  {
    OkHttpClient.Builder localBuilder = createClientBuilder();
    if (paramInt == 0) {
      return localBuilder;
    }
    return localBuilder.cache(new Cache(new File(paramContext.getCacheDir(), "http-cache"), paramInt));
  }
  
  public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder paramBuilder)
  {
    if (Build.VERSION.SDK_INT <= 19) {
      try
      {
        paramBuilder.sslSocketFactory(new TLSSocketFactory());
        Object localObject1 = ConnectionSpec.MODERN_TLS;
        localObject1 = new ConnectionSpec.Builder((ConnectionSpec)localObject1);
        Object localObject2 = TlsVersion.TLS_1_2;
        localObject2 = ((ConnectionSpec.Builder)localObject1).tlsVersions(new TlsVersion[] { localObject2 }).build();
        localObject1 = new ArrayList();
        ((List)localObject1).add(localObject2);
        localObject2 = ConnectionSpec.COMPATIBLE_TLS;
        ((List)localObject1).add(localObject2);
        localObject2 = ConnectionSpec.CLEARTEXT;
        ((List)localObject1).add(localObject2);
        paramBuilder.connectionSpecs((List)localObject1);
        return paramBuilder;
      }
      catch (Exception localException)
      {
        FLog.e("OkHttpClientProvider", "Error while enabling TLS 1.2", localException);
      }
    }
    return paramBuilder;
  }
  
  public static OkHttpClient getOkHttpClient()
  {
    if (sClient == null) {
      sClient = createClient();
    }
    return sClient;
  }
  
  public static void setOkHttpClientFactory(OkHttpClientFactory paramOkHttpClientFactory)
  {
    sFactory = paramOkHttpClientFactory;
  }
}