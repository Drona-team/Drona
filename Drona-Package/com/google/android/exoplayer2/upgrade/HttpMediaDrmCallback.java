package com.google.android.exoplayer2.upgrade;

import android.annotation.TargetApi;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory;
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

@TargetApi(18)
public final class HttpMediaDrmCallback
  implements MediaDrmCallback
{
  private static final int MAX_MANUAL_REDIRECTS = 5;
  private final HttpDataSource.Factory dataSourceFactory;
  private final String defaultLicenseUrl;
  private final boolean forceDefaultLicenseUrl;
  private final Map<String, String> keyRequestProperties;
  
  public HttpMediaDrmCallback(String paramString, HttpDataSource.Factory paramFactory)
  {
    this(paramString, false, paramFactory);
  }
  
  public HttpMediaDrmCallback(String paramString, boolean paramBoolean, HttpDataSource.Factory paramFactory)
  {
    dataSourceFactory = paramFactory;
    defaultLicenseUrl = paramString;
    forceDefaultLicenseUrl = paramBoolean;
    keyRequestProperties = new HashMap();
  }
  
  private static byte[] executePost(HttpDataSource.Factory paramFactory, String paramString, byte[] paramArrayOfByte, Map paramMap)
    throws IOException
  {
    HttpDataSource localHttpDataSource = paramFactory.createDataSource();
    if (paramMap != null)
    {
      paramFactory = paramMap.entrySet().iterator();
      while (paramFactory.hasNext())
      {
        paramMap = (Map.Entry)paramFactory.next();
        localHttpDataSource.setRequestProperty((String)paramMap.getKey(), (String)paramMap.getValue());
      }
    }
    int i = 0;
    paramFactory = paramString;
    for (;;)
    {
      paramString = new DataSourceInputStream(localHttpDataSource, new DataSpec(Uri.parse(paramFactory), paramArrayOfByte, 0L, 0L, -1L, null, 1));
      try
      {
        paramFactory = Util.toByteArray(paramString);
        Util.closeQuietly(paramString);
        return paramFactory;
      }
      catch (Throwable paramFactory) {}catch (HttpDataSource.InvalidResponseCodeException paramMap)
      {
        int j = responseCode;
        if (j != 307)
        {
          j = responseCode;
          if (j != 308)
          {
            j = i;
            break label185;
          }
        }
        int k = i + 1;
        j = k;
        if (i < 5)
        {
          i = 1;
          j = k;
        }
        else
        {
          label185:
          i = 0;
        }
        if (i != 0) {
          paramFactory = getRedirectUrl(paramMap);
        } else {
          paramFactory = null;
        }
        if (paramFactory != null)
        {
          Util.closeQuietly(paramString);
          i = j;
        }
        else
        {
          throw paramMap;
        }
      }
    }
    Util.closeQuietly(paramString);
    throw paramFactory;
  }
  
  private static String getRedirectUrl(HttpDataSource.InvalidResponseCodeException paramInvalidResponseCodeException)
  {
    paramInvalidResponseCodeException = headerFields;
    if (paramInvalidResponseCodeException != null)
    {
      paramInvalidResponseCodeException = (List)paramInvalidResponseCodeException.get("Location");
      if ((paramInvalidResponseCodeException != null) && (!paramInvalidResponseCodeException.isEmpty())) {
        return (String)paramInvalidResponseCodeException.get(0);
      }
    }
    return null;
  }
  
  public void clearAllKeyRequestProperties()
  {
    Map localMap = keyRequestProperties;
    try
    {
      keyRequestProperties.clear();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void clearKeyRequestProperty(String paramString)
  {
    Assertions.checkNotNull(paramString);
    Map localMap = keyRequestProperties;
    try
    {
      keyRequestProperties.remove(paramString);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public byte[] executeKeyRequest(UUID paramUUID, ExoMediaDrm.KeyRequest paramKeyRequest)
    throws Exception
  {
    String str1 = paramKeyRequest.getLicenseServerUrl();
    String str2 = str1;
    if ((forceDefaultLicenseUrl) || (TextUtils.isEmpty(str1))) {
      str2 = defaultLicenseUrl;
    }
    HashMap localHashMap = new HashMap();
    if (IpAddress.PLAYREADY_UUID.equals(paramUUID)) {
      str1 = "text/xml";
    } else if (IpAddress.CLEARKEY_UUID.equals(paramUUID)) {
      str1 = "application/json";
    } else {
      str1 = "application/octet-stream";
    }
    localHashMap.put("Content-Type", str1);
    if (IpAddress.PLAYREADY_UUID.equals(paramUUID)) {
      localHashMap.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
    }
    paramUUID = keyRequestProperties;
    try
    {
      localHashMap.putAll(keyRequestProperties);
      return executePost(dataSourceFactory, str2, paramKeyRequest.getData(), localHashMap);
    }
    catch (Throwable paramKeyRequest)
    {
      throw paramKeyRequest;
    }
  }
  
  public byte[] executeProvisionRequest(UUID paramUUID, ExoMediaDrm.ProvisionRequest paramProvisionRequest)
    throws IOException
  {
    paramUUID = new StringBuilder();
    paramUUID.append(paramProvisionRequest.getDefaultUrl());
    paramUUID.append("&signedRequest=");
    paramUUID.append(Util.fromUtf8Bytes(paramProvisionRequest.getData()));
    paramUUID = paramUUID.toString();
    return executePost(dataSourceFactory, paramUUID, Util.EMPTY_BYTE_ARRAY, null);
  }
  
  public void setKeyRequestProperty(String paramString1, String paramString2)
  {
    Assertions.checkNotNull(paramString1);
    Assertions.checkNotNull(paramString2);
    Map localMap = keyRequestProperties;
    try
    {
      keyRequestProperties.put(paramString1, paramString2);
      return;
    }
    catch (Throwable paramString1)
    {
      throw paramString1;
    }
  }
}
