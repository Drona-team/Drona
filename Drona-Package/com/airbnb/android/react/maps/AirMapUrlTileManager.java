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

public class AirMapUrlTileManager
  extends ViewGroupManager<AirMapUrlTile>
{
  private DisplayMetrics metrics;
  
  public AirMapUrlTileManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapUrlTile createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapUrlTile(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AIRMapUrlTile";
  }
  
  public void setFlipY(AirMapUrlTile paramAirMapUrlTile, boolean paramBoolean)
  {
    paramAirMapUrlTile.setFlipY(paramBoolean);
  }
  
  public void setMaximumZ(AirMapUrlTile paramAirMapUrlTile, float paramFloat)
  {
    paramAirMapUrlTile.setMaximumZ(paramFloat);
  }
  
  public void setMinimumZ(AirMapUrlTile paramAirMapUrlTile, float paramFloat)
  {
    paramAirMapUrlTile.setMinimumZ(paramFloat);
  }
  
  public void setUrlTemplate(AirMapUrlTile paramAirMapUrlTile, String paramString)
  {
    paramAirMapUrlTile.setUrlTemplate(paramString);
  }
  
  public void setZIndex(AirMapUrlTile paramAirMapUrlTile, float paramFloat)
  {
    paramAirMapUrlTile.setZIndex(paramFloat);
  }
}
