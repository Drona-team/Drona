package com.facebook.react.views.text;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;

public final class DefaultStyleValuesUtil
{
  private DefaultStyleValuesUtil()
  {
    throw new AssertionError("Never invoke this for an Utility class!");
  }
  
  private static ColorStateList getDefaultTextAttribute(Context paramContext, int paramInt)
  {
    Object localObject = paramContext.getTheme();
    paramContext = null;
    try
    {
      localObject = ((Resources.Theme)localObject).obtainStyledAttributes(new int[] { paramInt });
      try
      {
        paramContext = ((TypedArray)localObject).getColorStateList(0);
        if (localObject == null) {
          return paramContext;
        }
        ((TypedArray)localObject).recycle();
        return paramContext;
      }
      catch (Throwable localThrowable2)
      {
        paramContext = (Context)localObject;
        localObject = localThrowable2;
      }
      if (paramContext == null) {
        break label52;
      }
    }
    catch (Throwable localThrowable1) {}
    paramContext.recycle();
    label52:
    throw localThrowable1;
    return paramContext;
  }
  
  public static ColorStateList getDefaultTextColor(Context paramContext)
  {
    return getDefaultTextAttribute(paramContext, 16842904);
  }
  
  public static int getDefaultTextColorHighlight(Context paramContext)
  {
    return getDefaultTextAttribute(paramContext, 16842905).getDefaultColor();
  }
  
  public static ColorStateList getDefaultTextColorHint(Context paramContext)
  {
    return getDefaultTextAttribute(paramContext, 16842906);
  }
}
