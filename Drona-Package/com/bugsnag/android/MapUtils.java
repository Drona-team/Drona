package com.bugsnag.android;

import java.util.Map;

final class MapUtils
{
  MapUtils() {}
  
  static String getStringFromMap(String paramString, Map paramMap)
  {
    paramString = paramMap.get(paramString);
    if ((paramString instanceof String)) {
      return (String)paramString;
    }
    return null;
  }
}
