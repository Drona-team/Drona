package com.google.android.exoplayer2.util;

import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class UriUtil
{
  private static final int FRAGMENT = 3;
  private static final int INDEX_COUNT = 4;
  private static final int PATH = 1;
  private static final int QUERY = 2;
  private static final int SCHEME_COLON = 0;
  
  private UriUtil() {}
  
  private static int[] getUriIndices(String paramString)
  {
    int[] arrayOfInt = new int[4];
    if (TextUtils.isEmpty(paramString))
    {
      arrayOfInt[0] = -1;
      return arrayOfInt;
    }
    int j = paramString.length();
    int i = paramString.indexOf('#');
    if (i != -1) {
      j = i;
    }
    int k = paramString.indexOf('?');
    i = k;
    if ((k == -1) || (k > j)) {
      i = j;
    }
    int m = paramString.indexOf('/');
    k = m;
    if ((m == -1) || (m > i)) {
      k = i;
    }
    int n = paramString.indexOf(':');
    m = n;
    if (n > k) {
      m = -1;
    }
    k = m + 2;
    if ((k < i) && (paramString.charAt(m + 1) == '/') && (paramString.charAt(k) == '/')) {
      k = 1;
    } else {
      k = 0;
    }
    if (k != 0)
    {
      n = paramString.indexOf('/', m + 3);
      k = n;
      if ((n == -1) || (n > i)) {
        k = i;
      }
    }
    else
    {
      k = m + 1;
    }
    arrayOfInt[0] = m;
    arrayOfInt[1] = k;
    arrayOfInt[2] = i;
    arrayOfInt[3] = j;
    return arrayOfInt;
  }
  
  private static String removeDotSegments(StringBuilder paramStringBuilder, int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      return paramStringBuilder.toString();
    }
    int i = paramInt1;
    if (paramStringBuilder.charAt(paramInt1) == '/') {
      i = paramInt1 + 1;
    }
    paramInt1 = i;
    int j = paramInt1;
    for (;;)
    {
      if (j > paramInt2) {
        break label199;
      }
      int k;
      if (j == paramInt2)
      {
        k = j;
      }
      else
      {
        if (paramStringBuilder.charAt(j) != '/') {
          break label190;
        }
        k = j + 1;
      }
      int m = paramInt1 + 1;
      if ((j == m) && (paramStringBuilder.charAt(paramInt1) == '.'))
      {
        paramStringBuilder.delete(paramInt1, k);
        paramInt2 -= k - paramInt1;
        break;
      }
      if ((j == paramInt1 + 2) && (paramStringBuilder.charAt(paramInt1) == '.') && (paramStringBuilder.charAt(m) == '.'))
      {
        paramInt1 = paramStringBuilder.lastIndexOf("/", paramInt1 - 2) + 1;
        if (paramInt1 > i) {
          j = paramInt1;
        } else {
          j = i;
        }
        paramStringBuilder.delete(j, k);
        paramInt2 -= k - j;
      }
      else
      {
        paramInt1 = j + 1;
      }
      break;
      label190:
      j += 1;
    }
    label199:
    return paramStringBuilder.toString();
  }
  
  public static Uri removeQueryParameter(Uri paramUri, String paramString)
  {
    Uri.Builder localBuilder = paramUri.buildUpon();
    localBuilder.clearQuery();
    Iterator localIterator1 = paramUri.getQueryParameterNames().iterator();
    while (localIterator1.hasNext())
    {
      String str = (String)localIterator1.next();
      if (!str.equals(paramString))
      {
        Iterator localIterator2 = paramUri.getQueryParameters(str).iterator();
        while (localIterator2.hasNext()) {
          localBuilder.appendQueryParameter(str, (String)localIterator2.next());
        }
      }
    }
    return localBuilder.build();
  }
  
  public static String resolve(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = paramString1;
    if (paramString1 == null) {
      str = "";
    }
    paramString1 = paramString2;
    if (paramString2 == null) {
      paramString1 = "";
    }
    paramString2 = getUriIndices(paramString1);
    if (paramString2[0] != -1)
    {
      localStringBuilder.append(paramString1);
      removeDotSegments(localStringBuilder, paramString2[1], paramString2[2]);
      return localStringBuilder.toString();
    }
    int[] arrayOfInt = getUriIndices(str);
    if (paramString2[3] == 0)
    {
      localStringBuilder.append(str, 0, arrayOfInt[3]);
      localStringBuilder.append(paramString1);
      return localStringBuilder.toString();
    }
    if (paramString2[2] == 0)
    {
      localStringBuilder.append(str, 0, arrayOfInt[2]);
      localStringBuilder.append(paramString1);
      return localStringBuilder.toString();
    }
    if (paramString2[1] != 0)
    {
      i = arrayOfInt[0] + 1;
      localStringBuilder.append(str, 0, i);
      localStringBuilder.append(paramString1);
      return removeDotSegments(localStringBuilder, paramString2[1] + i, i + paramString2[2]);
    }
    if (paramString1.charAt(paramString2[1]) == '/')
    {
      localStringBuilder.append(str, 0, arrayOfInt[1]);
      localStringBuilder.append(paramString1);
      return removeDotSegments(localStringBuilder, arrayOfInt[1], arrayOfInt[1] + paramString2[2]);
    }
    if ((arrayOfInt[0] + 2 < arrayOfInt[1]) && (arrayOfInt[1] == arrayOfInt[2]))
    {
      localStringBuilder.append(str, 0, arrayOfInt[1]);
      localStringBuilder.append('/');
      localStringBuilder.append(paramString1);
      return removeDotSegments(localStringBuilder, arrayOfInt[1], arrayOfInt[1] + paramString2[2] + 1);
    }
    int i = str.lastIndexOf('/', arrayOfInt[2] - 1);
    if (i == -1) {
      i = arrayOfInt[1];
    } else {
      i += 1;
    }
    localStringBuilder.append(str, 0, i);
    localStringBuilder.append(paramString1);
    return removeDotSegments(localStringBuilder, arrayOfInt[1], i + paramString2[2]);
  }
  
  public static Uri resolveToUri(String paramString1, String paramString2)
  {
    return Uri.parse(resolve(paramString1, paramString2));
  }
}
