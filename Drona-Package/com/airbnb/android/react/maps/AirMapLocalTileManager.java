package com.airbnb.android.react.maps;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

public class AirMapLocalTileManager
  extends ViewGroupManager<AirMapLocalTile>
{
  private DisplayMetrics metrics;
  
  public AirMapLocalTileManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapLocalTile createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapLocalTile(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AIRMapLocalTile";
  }
  
  public void setPathTemplate(AirMapLocalTile paramAirMapLocalTile, String paramString)
  {
    paramAirMapLocalTile.setPathTemplate(paramString);
  }
  
  public void setTileSize(AirMapLocalTile paramAirMapLocalTile, float paramFloat)
  {
    paramAirMapLocalTile.setTileSize(paramFloat);
  }
  
  public void setZIndex(AirMapLocalTile paramAirMapLocalTile, float paramFloat)
  {
    paramAirMapLocalTile.setZIndex(paramFloat);
  }
}
