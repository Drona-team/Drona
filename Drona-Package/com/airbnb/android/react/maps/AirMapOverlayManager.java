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

public class AirMapOverlayManager
  extends ViewGroupManager<AirMapOverlay>
{
  private final DisplayMetrics metrics;
  
  public AirMapOverlayManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapOverlay createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapOverlay(paramThemedReactContext);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get("onPress", MapBuilder.get("registrationName", "onPress"));
  }
  
  public String getName()
  {
    return "AIRMapOverlay";
  }
  
  public void setBounds(AirMapOverlay paramAirMapOverlay, ReadableArray paramReadableArray)
  {
    paramAirMapOverlay.setBounds(paramReadableArray);
  }
  
  public void setImage(AirMapOverlay paramAirMapOverlay, String paramString)
  {
    paramAirMapOverlay.setImage(paramString);
  }
  
  public void setTappable(AirMapOverlay paramAirMapOverlay, boolean paramBoolean)
  {
    paramAirMapOverlay.setTappable(paramBoolean);
  }
  
  public void setZIndex(AirMapOverlay paramAirMapOverlay, float paramFloat)
  {
    paramAirMapOverlay.setZIndex(paramFloat);
  }
}
