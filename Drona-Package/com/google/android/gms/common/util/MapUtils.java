package com.google.android.gms.common.util;

import com.google.android.gms.common.annotation.KeepForSdk;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@KeepForSdk
public class MapUtils
{
  public MapUtils() {}
  
  public static void writeStringMapToJson(StringBuilder paramStringBuilder, HashMap paramHashMap)
  {
    paramStringBuilder.append("{");
    Iterator localIterator = paramHashMap.keySet().iterator();
    int i = 1;
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (i == 0) {
        paramStringBuilder.append(",");
      } else {
        i = 0;
      }
      String str2 = (String)paramHashMap.get(str1);
      paramStringBuilder.append("\"");
      paramStringBuilder.append(str1);
      paramStringBuilder.append("\":");
      if (str2 == null)
      {
        paramStringBuilder.append("null");
      }
      else
      {
        paramStringBuilder.append("\"");
        paramStringBuilder.append(str2);
        paramStringBuilder.append("\"");
      }
    }
    paramStringBuilder.append("}");
  }
}
