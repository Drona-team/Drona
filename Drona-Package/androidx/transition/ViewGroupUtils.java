package androidx.transition;

import android.os.Build.VERSION;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ViewGroupUtils
{
  private static Method sGetChildDrawingOrderMethod;
  private static boolean sGetChildDrawingOrderMethodFetched;
  private static boolean sTryHiddenSuppressLayout;
  
  private ViewGroupUtils() {}
  
  static int getChildDrawingOrder(ViewGroup paramViewGroup, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 29) {
      return paramViewGroup.getChildDrawingOrder(paramInt);
    }
    Object localObject;
    Class localClass;
    if (!sGetChildDrawingOrderMethodFetched)
    {
      localObject = Integer.TYPE;
      localClass = Integer.TYPE;
    }
    try
    {
      localObject = ViewGroup.class.getDeclaredMethod("getChildDrawingOrder", new Class[] { localObject, localClass });
      sGetChildDrawingOrderMethod = (Method)localObject;
      localObject = sGetChildDrawingOrderMethod;
      ((Method)localObject).setAccessible(true);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        try
        {
          int i = paramViewGroup.getChildCount();
          paramViewGroup = ((Method)localObject).invoke(paramViewGroup, new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt) });
          paramViewGroup = (Integer)paramViewGroup;
          i = paramViewGroup.intValue();
          return i;
        }
        catch (IllegalAccessException paramViewGroup)
        {
          return paramInt;
        }
        catch (InvocationTargetException paramViewGroup) {}
        localNoSuchMethodException = localNoSuchMethodException;
      }
    }
    sGetChildDrawingOrderMethodFetched = true;
    i = paramInt;
    if (sGetChildDrawingOrderMethod != null) {
      localObject = sGetChildDrawingOrderMethod;
    }
    return paramInt;
  }
  
  static ViewGroupOverlayImpl getOverlay(ViewGroup paramViewGroup)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return new ViewGroupOverlayApi18(paramViewGroup);
    }
    return ViewGroupOverlayApi14.createFrom(paramViewGroup);
  }
  
  private static void hiddenSuppressLayout(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    if (sTryHiddenSuppressLayout)
    {
      try
      {
        paramViewGroup.suppressLayout(paramBoolean);
        return;
      }
      catch (NoSuchMethodError paramViewGroup)
      {
        for (;;) {}
      }
      sTryHiddenSuppressLayout = false;
      return;
    }
  }
  
  static void suppressLayout(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 29)
    {
      paramViewGroup.suppressLayout(paramBoolean);
      return;
    }
    if (Build.VERSION.SDK_INT >= 18)
    {
      hiddenSuppressLayout(paramViewGroup, paramBoolean);
      return;
    }
    ViewGroupUtilsApi14.suppressLayout(paramViewGroup, paramBoolean);
  }
}
