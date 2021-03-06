package com.bumptech.glide.load.model;

import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Map;

public class GlideUrl
  implements Key
{
  private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%;$";
  @Nullable
  private volatile byte[] cacheKeyBytes;
  private int hashCode;
  private final Headers headers;
  @Nullable
  private String safeStringUrl;
  @Nullable
  private URL safeUrl;
  @Nullable
  private final String stringUrl;
  @Nullable
  private final URL url;
  
  public GlideUrl(String paramString)
  {
    this(paramString, Headers.DEFAULT);
  }
  
  public GlideUrl(String paramString, Headers paramHeaders)
  {
    url = null;
    stringUrl = Preconditions.checkNotEmpty(paramString);
    headers = ((Headers)Preconditions.checkNotNull(paramHeaders));
  }
  
  public GlideUrl(URL paramURL)
  {
    this(paramURL, Headers.DEFAULT);
  }
  
  public GlideUrl(URL paramURL, Headers paramHeaders)
  {
    url = ((URL)Preconditions.checkNotNull(paramURL));
    stringUrl = null;
    headers = ((Headers)Preconditions.checkNotNull(paramHeaders));
  }
  
  private byte[] getCacheKeyBytes()
  {
    if (cacheKeyBytes == null) {
      cacheKeyBytes = getCacheKey().getBytes(Key.CHARSET);
    }
    return cacheKeyBytes;
  }
  
  private String getSafeStringUrl()
  {
    if (TextUtils.isEmpty(safeStringUrl))
    {
      String str2 = stringUrl;
      String str1 = str2;
      if (TextUtils.isEmpty(str2)) {
        str1 = ((URL)Preconditions.checkNotNull(url)).toString();
      }
      safeStringUrl = Uri.encode(str1, "@#&=*+-_.,:!?()/~'%;$");
    }
    return safeStringUrl;
  }
  
  private URL getSafeUrl()
    throws MalformedURLException
  {
    if (safeUrl == null) {
      safeUrl = new URL(getSafeStringUrl());
    }
    return safeUrl;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof GlideUrl))
    {
      paramObject = (GlideUrl)paramObject;
      if ((getCacheKey().equals(paramObject.getCacheKey())) && (headers.equals(headers))) {
        return true;
      }
    }
    return false;
  }
  
  public String getCacheKey()
  {
    if (stringUrl != null) {
      return stringUrl;
    }
    return ((URL)Preconditions.checkNotNull(url)).toString();
  }
  
  public Map getHeaders()
  {
    return headers.getHeaders();
  }
  
  public int hashCode()
  {
    if (hashCode == 0)
    {
      hashCode = getCacheKey().hashCode();
      hashCode = (hashCode * 31 + headers.hashCode());
    }
    return hashCode;
  }
  
  public String toString()
  {
    return getCacheKey();
  }
  
  public String toStringUrl()
  {
    return getSafeStringUrl();
  }
  
  public URL toURL()
    throws MalformedURLException
  {
    return getSafeUrl();
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(getCacheKeyBytes());
  }
}
