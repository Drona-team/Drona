package com.airbnb.android.react.maps;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import java.util.Map;

public class AirMapPolylineManager
  extends ViewGroupManager<AirMapPolyline>
{
  private final DisplayMetrics metrics;
  
  public AirMapPolylineManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapPolyline createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapPolyline(paramThemedReactContext);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get("onPress", MapBuilder.get("registrationName", "onPress"));
  }
  
  public String getName()
  {
    return "AIRMapPolyline";
  }
  
  public void setCoordinate(AirMapPolyline paramAirMapPolyline, ReadableArray paramReadableArray)
  {
    paramAirMapPolyline.setCoordinates(paramReadableArray);
  }
  
  public void setGeodesic(AirMapPolyline paramAirMapPolyline, boolean paramBoolean)
  {
    paramAirMapPolyline.setGeodesic(paramBoolean);
  }
  
  public void setLineDashPattern(AirMapPolyline paramAirMapPolyline, ReadableArray paramReadableArray)
  {
    paramAirMapPolyline.setLineDashPattern(paramReadableArray);
  }
  
  public void setStrokeColor(AirMapPolyline paramAirMapPolyline, int paramInt)
  {
    paramAirMapPolyline.setColor(paramInt);
  }
  
  public void setStrokeWidth(AirMapPolyline paramAirMapPolyline, float paramFloat)
  {
    paramAirMapPolyline.setWidth(metrics.density * paramFloat);
  }
  
  public void setTappable(AirMapPolyline paramAirMapPolyline, boolean paramBoolean)
  {
    paramAirMapPolyline.setTappable(paramBoolean);
  }
  
  public void setZIndex(AirMapPolyline paramAirMapPolyline, float paramFloat)
  {
    paramAirMapPolyline.setZIndex(paramFloat);
  }
  
  public void setlineCap(AirMapPolyline paramAirMapPolyline, String paramString)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a10 = a9\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
}
