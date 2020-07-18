package com.bumptech.glide.util;

import android.text.TextUtils;
import java.util.Collection;

public final class Preconditions
{
  private Preconditions() {}
  
  public static void checkArgument(boolean paramBoolean, String paramString)
  {
    if (paramBoolean) {
      return;
    }
    throw new IllegalArgumentException(paramString);
  }
  
  public static String checkNotEmpty(String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {
      return paramString;
    }
    throw new IllegalArgumentException("Must not be null or empty");
  }
  
  public static Collection checkNotEmpty(Collection paramCollection)
  {
    if (!paramCollection.isEmpty()) {
      return paramCollection;
    }
    throw new IllegalArgumentException("Must not be empty.");
  }
  
  public static Object checkNotNull(Object paramObject)
  {
    return checkNotNull(paramObject, "Argument must not be null");
  }
  
  public static Object checkNotNull(Object paramObject, String paramString)
  {
    if (paramObject != null) {
      return paramObject;
    }
    throw new NullPointerException(paramString);
  }
}
