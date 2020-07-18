package com.facebook.react.modules.network;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers.Builder;
import okhttp3.HttpUrl;

public class ReactCookieJarContainer
  implements CookieJarContainer
{
  @Nullable
  private CookieJar cookieJar = null;
  
  public ReactCookieJarContainer() {}
  
  public List loadForRequest(HttpUrl paramHttpUrl)
  {
    Object localObject;
    if (cookieJar != null)
    {
      localObject = cookieJar.loadForRequest(paramHttpUrl);
      paramHttpUrl = new ArrayList();
      localObject = ((List)localObject).iterator();
    }
    for (;;)
    {
      Cookie localCookie;
      if (((Iterator)localObject).hasNext()) {
        localCookie = (Cookie)((Iterator)localObject).next();
      }
      try
      {
        new Headers.Builder().add(localCookie.name(), localCookie.value());
        paramHttpUrl.add(localCookie);
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return paramHttpUrl;
      return Collections.emptyList();
    }
  }
  
  public void removeCookieJar()
  {
    cookieJar = null;
  }
  
  public void saveFromResponse(HttpUrl paramHttpUrl, List paramList)
  {
    if (cookieJar != null) {
      cookieJar.saveFromResponse(paramHttpUrl, paramList);
    }
  }
  
  public void setCookieJar(CookieJar paramCookieJar)
  {
    cookieJar = paramCookieJar;
  }
}
