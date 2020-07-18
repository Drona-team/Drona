package com.google.android.exoplayer2.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RepeatModeUtil
{
  public static final int REPEAT_TOGGLE_MODE_ALL = 2;
  public static final int REPEAT_TOGGLE_MODE_NONE = 0;
  public static final int REPEAT_TOGGLE_MODE_ONE = 1;
  
  private RepeatModeUtil() {}
  
  public static int getNextRepeatMode(int paramInt1, int paramInt2)
  {
    int i = 1;
    while (i <= 2)
    {
      int j = (paramInt1 + i) % 3;
      if (isRepeatModeEnabled(j, paramInt2)) {
        return j;
      }
      i += 1;
    }
    return paramInt1;
  }
  
  public static boolean isRepeatModeEnabled(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return false;
    case 2: 
      if ((paramInt2 & 0x2) != 0) {
        return true;
      }
      break;
    case 1: 
      if ((paramInt2 & 0x1) != 0) {
        return true;
      }
      break;
    case 0: 
      return true;
    }
    return false;
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface RepeatToggleModes {}
}
