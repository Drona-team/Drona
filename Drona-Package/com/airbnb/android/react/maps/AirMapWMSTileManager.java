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

public class AirMapWMSTileManager
  extends ViewGroupManager<AirMapWMSTile>
{
  private DisplayMetrics metrics;
  
  public AirMapWMSTileManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapWMSTile createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapWMSTile(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AIRMapWMSTile";
  }
  
  public void setMaximumZ(AirMapWMSTile paramAirMapWMSTile, float paramFloat)
  {
    paramAirMapWMSTile.setMaximumZ(paramFloat);
  }
  
  public void setMinimumZ(AirMapWMSTile paramAirMapWMSTile, float paramFloat)
  {
    paramAirMapWMSTile.setMinimumZ(paramFloat);
  }
  
  public void setOpacity(AirMapWMSTile paramAirMapWMSTile, float paramFloat)
  {
    paramAirMapWMSTile.setOpacity(paramFloat);
  }
  
  public void setTileSize(AirMapWMSTile paramAirMapWMSTile, int paramInt)
  {
    paramAirMapWMSTile.setTileSize(paramInt);
  }
  
  public void setUrlTemplate(AirMapWMSTile paramAirMapWMSTile, String paramString)
  {
    paramAirMapWMSTile.setUrlTemplate(paramString);
  }
  
  public void setZIndex(AirMapWMSTile paramAirMapWMSTile, float paramFloat)
  {
    paramAirMapWMSTile.setZIndex(paramFloat);
  }
}
