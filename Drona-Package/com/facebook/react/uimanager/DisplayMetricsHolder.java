package com.facebook.react.uimanager;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.WritableNativeMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DisplayMetricsHolder
{
  @Nullable
  private static DisplayMetrics sScreenDisplayMetrics;
  @Nullable
  private static DisplayMetrics sWindowDisplayMetrics;
  
  public DisplayMetricsHolder() {}
  
  public static Map getDisplayMetricsMap(double paramDouble)
  {
    boolean bool;
    if ((sWindowDisplayMetrics == null) && (sScreenDisplayMetrics == null)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertNotNull(Boolean.valueOf(bool), "DisplayMetricsHolder must be initialized with initDisplayMetricsIfNotInitialized or initDisplayMetrics");
    HashMap localHashMap = new HashMap();
    localHashMap.put("windowPhysicalPixels", getPhysicalPixelsMap(sWindowDisplayMetrics, paramDouble));
    localHashMap.put("screenPhysicalPixels", getPhysicalPixelsMap(sScreenDisplayMetrics, paramDouble));
    return localHashMap;
  }
  
  public static WritableNativeMap getDisplayMetricsNativeMap(double paramDouble)
  {
    boolean bool;
    if ((sWindowDisplayMetrics == null) && (sScreenDisplayMetrics == null)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertNotNull(Boolean.valueOf(bool), "DisplayMetricsHolder must be initialized with initDisplayMetricsIfNotInitialized or initDisplayMetrics");
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localWritableNativeMap.putMap("windowPhysicalPixels", getPhysicalPixelsNativeMap(sWindowDisplayMetrics, paramDouble));
    localWritableNativeMap.putMap("screenPhysicalPixels", getPhysicalPixelsNativeMap(sScreenDisplayMetrics, paramDouble));
    return localWritableNativeMap;
  }
  
  private static Map getPhysicalPixelsMap(DisplayMetrics paramDisplayMetrics, double paramDouble)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("width", Integer.valueOf(widthPixels));
    localHashMap.put("height", Integer.valueOf(heightPixels));
    localHashMap.put("scale", Float.valueOf(density));
    localHashMap.put("fontScale", Double.valueOf(paramDouble));
    localHashMap.put("densityDpi", Integer.valueOf(densityDpi));
    return localHashMap;
  }
  
  private static WritableNativeMap getPhysicalPixelsNativeMap(DisplayMetrics paramDisplayMetrics, double paramDouble)
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localWritableNativeMap.putInt("width", widthPixels);
    localWritableNativeMap.putInt("height", heightPixels);
    localWritableNativeMap.putDouble("scale", density);
    localWritableNativeMap.putDouble("fontScale", paramDouble);
    localWritableNativeMap.putDouble("densityDpi", densityDpi);
    return localWritableNativeMap;
  }
  
  public static DisplayMetrics getScreenDisplayMetrics()
  {
    return sScreenDisplayMetrics;
  }
  
  public static DisplayMetrics getWindowDisplayMetrics()
  {
    return sWindowDisplayMetrics;
  }
  
  public static void initDisplayMetrics(Context paramContext)
  {
    Object localObject1 = paramContext.getResources().getDisplayMetrics();
    setWindowDisplayMetrics((DisplayMetrics)localObject1);
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    localDisplayMetrics.setTo((DisplayMetrics)localObject1);
    paramContext = (WindowManager)paramContext.getSystemService("window");
    Assertions.assertNotNull(paramContext, "WindowManager is null!");
    paramContext = paramContext.getDefaultDisplay();
    if (Build.VERSION.SDK_INT >= 17) {
      paramContext.getRealMetrics(localDisplayMetrics);
    }
    try
    {
      localObject1 = Display.class.getMethod("getRawHeight", new Class[0]);
      Object localObject2 = Display.class.getMethod("getRawWidth", new Class[0]);
      localObject2 = ((Method)localObject2).invoke(paramContext, new Object[0]);
      localObject2 = (Integer)localObject2;
      int i = ((Integer)localObject2).intValue();
      widthPixels = i;
      paramContext = ((Method)localObject1).invoke(paramContext, new Object[0]);
      paramContext = (Integer)paramContext;
      i = paramContext.intValue();
      heightPixels = i;
      setScreenDisplayMetrics(localDisplayMetrics);
      return;
    }
    catch (InvocationTargetException|IllegalAccessException|NoSuchMethodException paramContext)
    {
      throw new RuntimeException("Error getting real dimensions for API level < 17", paramContext);
    }
  }
  
  public static void initDisplayMetricsIfNotInitialized(Context paramContext)
  {
    if (getScreenDisplayMetrics() != null) {
      return;
    }
    initDisplayMetrics(paramContext);
  }
  
  public static void setScreenDisplayMetrics(DisplayMetrics paramDisplayMetrics)
  {
    sScreenDisplayMetrics = paramDisplayMetrics;
  }
  
  public static void setWindowDisplayMetrics(DisplayMetrics paramDisplayMetrics)
  {
    sWindowDisplayMetrics = paramDisplayMetrics;
  }
}
