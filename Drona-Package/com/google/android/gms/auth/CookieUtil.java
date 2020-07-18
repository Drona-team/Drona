package com.google.android.gms.auth;

import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;

public final class CookieUtil
{
  private CookieUtil() {}
  
  private static boolean booleanToString(Boolean paramBoolean)
  {
    return (paramBoolean != null) && (paramBoolean.booleanValue());
  }
  
  public static String getCookieUrl(String paramString, Boolean paramBoolean)
  {
    Preconditions.checkNotEmpty(paramString);
    if (booleanToString(paramBoolean)) {
      paramBoolean = "https";
    } else {
      paramBoolean = "http";
    }
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramBoolean).length() + 3 + String.valueOf(paramString).length());
    localStringBuilder.append(paramBoolean);
    localStringBuilder.append("://");
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  public static String getCookieValue(String paramString1, String paramString2, String paramString3, String paramString4, Boolean paramBoolean1, Boolean paramBoolean2, Long paramLong)
  {
    paramString1 = new StringBuilder(paramString1);
    paramString1.append('=');
    if (!TextUtils.isEmpty(paramString2)) {
      paramString1.append(paramString2);
    }
    if (booleanToString(paramBoolean1)) {
      paramString1.append(";HttpOnly");
    }
    if (booleanToString(paramBoolean2)) {
      paramString1.append(";Secure");
    }
    if (!TextUtils.isEmpty(paramString3))
    {
      paramString1.append(";Domain=");
      paramString1.append(paramString3);
    }
    if (!TextUtils.isEmpty(paramString4))
    {
      paramString1.append(";Path=");
      paramString1.append(paramString4);
    }
    if ((paramLong != null) && (paramLong.longValue() > 0L))
    {
      paramString1.append(";Max-Age=");
      paramString1.append(paramLong);
    }
    return paramString1.toString();
  }
}
