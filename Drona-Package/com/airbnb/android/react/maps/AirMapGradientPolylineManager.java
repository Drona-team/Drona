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
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class AirMapGradientPolylineManager
  extends ViewGroupManager<AirMapGradientPolyline>
{
  private final DisplayMetrics metrics;
  
  public AirMapGradientPolylineManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapGradientPolyline createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapGradientPolyline(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AIRMapGradientPolyline";
  }
  
  public void setCoordinates(AirMapGradientPolyline paramAirMapGradientPolyline, ReadableArray paramReadableArray)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramReadableArray.size())
    {
      ReadableMap localReadableMap = paramReadableArray.getMap(i);
      localArrayList.add(new LatLng(localReadableMap.getDouble("latitude"), localReadableMap.getDouble("longitude")));
      i += 1;
    }
    paramAirMapGradientPolyline.setCoordinates(localArrayList);
  }
  
  public void setStrokeColors(AirMapGradientPolyline paramAirMapGradientPolyline, ReadableArray paramReadableArray)
  {
    if (paramReadableArray != null)
    {
      if (paramReadableArray.size() == 0)
      {
        paramAirMapGradientPolyline.setStrokeColors(new int[] { 0, 0 });
        return;
      }
      int j = paramReadableArray.size();
      int i = 0;
      if (j == 1)
      {
        paramAirMapGradientPolyline.setStrokeColors(new int[] { paramReadableArray.getInt(0), paramReadableArray.getInt(0) });
        return;
      }
      int[] arrayOfInt = new int[paramReadableArray.size()];
      while (i < paramReadableArray.size())
      {
        arrayOfInt[i] = paramReadableArray.getInt(i);
        i += 1;
      }
      paramAirMapGradientPolyline.setStrokeColors(arrayOfInt);
      return;
    }
    paramAirMapGradientPolyline.setStrokeColors(new int[] { 0, 0 });
  }
  
  public void setStrokeWidth(AirMapGradientPolyline paramAirMapGradientPolyline, float paramFloat)
  {
    paramAirMapGradientPolyline.setWidth(metrics.density * paramFloat);
  }
  
  public void setZIndex(AirMapGradientPolyline paramAirMapGradientPolyline, float paramFloat)
  {
    paramAirMapGradientPolyline.setZIndex(paramFloat);
  }
}
