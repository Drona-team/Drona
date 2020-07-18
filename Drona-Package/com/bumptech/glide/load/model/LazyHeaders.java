package com.bumptech.glide.load.model;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class LazyHeaders
  implements Headers
{
  private volatile Map<String, String> combinedHeaders;
  private final Map<String, List<LazyHeaderFactory>> headers;
  
  LazyHeaders(Map paramMap)
  {
    headers = Collections.unmodifiableMap(paramMap);
  }
  
  private String buildHeaderValue(List paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      String str = ((LazyHeaderFactory)paramList.get(i)).buildHeader();
      if (!TextUtils.isEmpty(str))
      {
        localStringBuilder.append(str);
        if (i != paramList.size() - 1) {
          localStringBuilder.append(',');
        }
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  private Map generateHeaders()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = headers.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = buildHeaderValue((List)localEntry.getValue());
      if (!TextUtils.isEmpty(str)) {
        localHashMap.put(localEntry.getKey(), str);
      }
    }
    return localHashMap;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof LazyHeaders))
    {
      paramObject = (LazyHeaders)paramObject;
      return headers.equals(headers);
    }
    return false;
  }
  
  public Map getHeaders()
  {
    if (combinedHeaders == null) {
      try
      {
        if (combinedHeaders == null) {
          combinedHeaders = Collections.unmodifiableMap(generateHeaders());
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    return combinedHeaders;
  }
  
  public int hashCode()
  {
    return headers.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("LazyHeaders{headers=");
    localStringBuilder.append(headers);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public static final class Builder
  {
    private static final Map<String, List<LazyHeaderFactory>> DEFAULT_HEADERS;
    private static final String DEFAULT_USER_AGENT = ;
    private static final String USER_AGENT_HEADER = "User-Agent";
    private boolean copyOnModify = true;
    private Map<String, List<LazyHeaderFactory>> headers = DEFAULT_HEADERS;
    private boolean isUserAgentDefault = true;
    
    static
    {
      HashMap localHashMap = new HashMap(2);
      if (!TextUtils.isEmpty(DEFAULT_USER_AGENT)) {
        localHashMap.put("User-Agent", Collections.singletonList(new LazyHeaders.StringHeaderFactory(DEFAULT_USER_AGENT)));
      }
      DEFAULT_HEADERS = Collections.unmodifiableMap(localHashMap);
    }
    
    public Builder() {}
    
    private Map copyHeaders()
    {
      HashMap localHashMap = new HashMap(headers.size());
      Iterator localIterator = headers.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ArrayList localArrayList = new ArrayList((Collection)localEntry.getValue());
        localHashMap.put(localEntry.getKey(), localArrayList);
      }
      return localHashMap;
    }
    
    private void copyIfNecessary()
    {
      if (copyOnModify)
      {
        copyOnModify = false;
        headers = copyHeaders();
      }
    }
    
    private List getFactories(String paramString)
    {
      Object localObject = (List)headers.get(paramString);
      if (localObject == null)
      {
        localObject = new ArrayList();
        headers.put(paramString, localObject);
        return localObject;
      }
      return localObject;
    }
    
    static String getSanitizedUserAgent()
    {
      String str = System.getProperty("http.agent");
      if (TextUtils.isEmpty(str)) {
        return str;
      }
      int j = str.length();
      StringBuilder localStringBuilder = new StringBuilder(str.length());
      int i = 0;
      while (i < j)
      {
        char c = str.charAt(i);
        if (((c > '\037') || (c == '\t')) && (c < '')) {
          localStringBuilder.append(c);
        } else {
          localStringBuilder.append('?');
        }
        i += 1;
      }
      return localStringBuilder.toString();
    }
    
    public Builder addHeader(String paramString, LazyHeaderFactory paramLazyHeaderFactory)
    {
      if ((isUserAgentDefault) && ("User-Agent".equalsIgnoreCase(paramString))) {
        return setHeader(paramString, paramLazyHeaderFactory);
      }
      copyIfNecessary();
      getFactories(paramString).add(paramLazyHeaderFactory);
      return this;
    }
    
    public Builder addHeader(String paramString1, String paramString2)
    {
      return addHeader(paramString1, new LazyHeaders.StringHeaderFactory(paramString2));
    }
    
    public LazyHeaders build()
    {
      copyOnModify = true;
      return new LazyHeaders(headers);
    }
    
    public Builder setHeader(String paramString, LazyHeaderFactory paramLazyHeaderFactory)
    {
      copyIfNecessary();
      if (paramLazyHeaderFactory == null)
      {
        headers.remove(paramString);
      }
      else
      {
        List localList = getFactories(paramString);
        localList.clear();
        localList.add(paramLazyHeaderFactory);
      }
      if ((isUserAgentDefault) && ("User-Agent".equalsIgnoreCase(paramString))) {
        isUserAgentDefault = false;
      }
      return this;
    }
    
    public Builder setHeader(String paramString1, String paramString2)
    {
      if (paramString2 == null) {
        paramString2 = null;
      } else {
        paramString2 = new LazyHeaders.StringHeaderFactory(paramString2);
      }
      return setHeader(paramString1, paramString2);
    }
  }
  
  static final class StringHeaderFactory
    implements LazyHeaderFactory
  {
    @NonNull
    private final String value;
    
    StringHeaderFactory(String paramString)
    {
      value = paramString;
    }
    
    public String buildHeader()
    {
      return value;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof StringHeaderFactory))
      {
        paramObject = (StringHeaderFactory)paramObject;
        return value.equals(value);
      }
      return false;
    }
    
    public int hashCode()
    {
      return value.hashCode();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("StringHeaderFactory{value='");
      localStringBuilder.append(value);
      localStringBuilder.append('\'');
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}
