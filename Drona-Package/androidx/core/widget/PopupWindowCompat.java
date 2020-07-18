package androidx.core.widget;

import android.os.Build.VERSION;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PopupWindowCompat
{
  private static final String TAG = "PopupWindowCompatApi21";
  private static Method sGetWindowLayoutTypeMethod;
  private static boolean sGetWindowLayoutTypeMethodAttempted;
  private static Field sOverlapAnchorField;
  private static boolean sOverlapAnchorFieldAttempted;
  private static Method sSetWindowLayoutTypeMethod;
  private static boolean sSetWindowLayoutTypeMethodAttempted;
  
  private PopupWindowCompat() {}
  
  public static boolean getOverlapAnchor(PopupWindow paramPopupWindow)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return paramPopupWindow.getOverlapAnchor();
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (!sOverlapAnchorFieldAttempted)
      {
        try
        {
          Field localField1 = PopupWindow.class.getDeclaredField("mOverlapAnchor");
          sOverlapAnchorField = localField1;
          localField1 = sOverlapAnchorField;
          localField1.setAccessible(true);
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", localNoSuchFieldException);
        }
        sOverlapAnchorFieldAttempted = true;
      }
      if (sOverlapAnchorField != null)
      {
        Field localField2 = sOverlapAnchorField;
        try
        {
          paramPopupWindow = localField2.get(paramPopupWindow);
          paramPopupWindow = (Boolean)paramPopupWindow;
          boolean bool = paramPopupWindow.booleanValue();
          return bool;
        }
        catch (IllegalAccessException paramPopupWindow)
        {
          Log.i("PopupWindowCompatApi21", "Could not get overlap anchor field in PopupWindow", paramPopupWindow);
        }
      }
    }
    return false;
  }
  
  public static int getWindowLayoutType(PopupWindow paramPopupWindow)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return paramPopupWindow.getWindowLayoutType();
    }
    if (!sGetWindowLayoutTypeMethodAttempted) {}
    try
    {
      localMethod = PopupWindow.class.getDeclaredMethod("getWindowLayoutType", new Class[0]);
      sGetWindowLayoutTypeMethod = localMethod;
      localMethod = sGetWindowLayoutTypeMethod;
      localMethod.setAccessible(true);
    }
    catch (Exception localException)
    {
      Method localMethod;
      for (;;) {}
    }
    sGetWindowLayoutTypeMethodAttempted = true;
    if (sGetWindowLayoutTypeMethod != null) {
      localMethod = sGetWindowLayoutTypeMethod;
    }
    try
    {
      paramPopupWindow = localMethod.invoke(paramPopupWindow, new Object[0]);
      paramPopupWindow = (Integer)paramPopupWindow;
      int i = paramPopupWindow.intValue();
      return i;
    }
    catch (Exception paramPopupWindow) {}
    return 0;
    return 0;
  }
  
  public static void setOverlapAnchor(PopupWindow paramPopupWindow, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramPopupWindow.setOverlapAnchor(paramBoolean);
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (!sOverlapAnchorFieldAttempted)
      {
        try
        {
          Field localField1 = PopupWindow.class.getDeclaredField("mOverlapAnchor");
          sOverlapAnchorField = localField1;
          localField1 = sOverlapAnchorField;
          localField1.setAccessible(true);
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", localNoSuchFieldException);
        }
        sOverlapAnchorFieldAttempted = true;
      }
      if (sOverlapAnchorField != null)
      {
        Field localField2 = sOverlapAnchorField;
        try
        {
          localField2.set(paramPopupWindow, Boolean.valueOf(paramBoolean));
          return;
        }
        catch (IllegalAccessException paramPopupWindow)
        {
          Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", paramPopupWindow);
        }
      }
    }
  }
  
  public static void setWindowLayoutType(PopupWindow paramPopupWindow, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramPopupWindow.setWindowLayoutType(paramInt);
      return;
    }
    Object localObject;
    if (!sSetWindowLayoutTypeMethodAttempted) {
      localObject = Integer.TYPE;
    }
    try
    {
      localObject = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", new Class[] { localObject });
      sSetWindowLayoutTypeMethod = (Method)localObject;
      localObject = sSetWindowLayoutTypeMethod;
      ((Method)localObject).setAccessible(true);
    }
    catch (Exception localException)
    {
      for (;;)
      {
        try
        {
          ((Method)localObject).invoke(paramPopupWindow, new Object[] { Integer.valueOf(paramInt) });
          return;
        }
        catch (Exception paramPopupWindow) {}
        localException = localException;
      }
    }
    sSetWindowLayoutTypeMethodAttempted = true;
    if (sSetWindowLayoutTypeMethod != null) {
      localObject = sSetWindowLayoutTypeMethod;
    }
  }
  
  public static void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      paramPopupWindow.showAsDropDown(paramView, paramInt1, paramInt2, paramInt3);
      return;
    }
    int i = paramInt1;
    if ((GravityCompat.getAbsoluteGravity(paramInt3, ViewCompat.getLayoutDirection(paramView)) & 0x7) == 5) {
      i = paramInt1 - (paramPopupWindow.getWidth() - paramView.getWidth());
    }
    paramPopupWindow.showAsDropDown(paramView, i, paramInt2);
  }
}
