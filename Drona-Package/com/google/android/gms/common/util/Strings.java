package com.google.android.gms.common.util;

import android.text.TextUtils;
import com.google.android.gms.common.annotation.KeepForSdk;
import java.util.regex.Pattern;

@KeepForSdk
@VisibleForTesting
public class Strings
{
  private static final Pattern zzhh = Pattern.compile("\\$\\{(.*?)\\}");
  
  private Strings() {}
  
  public static String emptyToNull(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    return paramString;
  }
  
  public static boolean isEmptyOrWhitespace(String paramString)
  {
    return (paramString == null) || (paramString.trim().isEmpty());
  }
}
