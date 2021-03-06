package com.airbnb.android.react.maps;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.WeightedLatLng;

public class AirMapHeatmapManager
  extends ViewGroupManager<AirMapHeatmap>
{
  public AirMapHeatmapManager() {}
  
  public AirMapHeatmap createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapHeatmap(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AIRMapHeatmap";
  }
  
  public void setGradient(AirMapHeatmap paramAirMapHeatmap, ReadableMap paramReadableMap)
  {
    ReadableArray localReadableArray = paramReadableMap.getArray("colors");
    int[] arrayOfInt = new int[localReadableArray.size()];
    int j = 0;
    int i = 0;
    while (i < localReadableArray.size())
    {
      arrayOfInt[i] = localReadableArray.getInt(i);
      i += 1;
    }
    localReadableArray = paramReadableMap.getArray("startPoints");
    float[] arrayOfFloat = new float[localReadableArray.size()];
    i = j;
    while (i < localReadableArray.size())
    {
      arrayOfFloat[i] = ((float)localReadableArray.getDouble(i));
      i += 1;
    }
    if (paramReadableMap.hasKey("colorMapSize"))
    {
      paramAirMapHeatmap.setGradient(new Gradient(arrayOfInt, arrayOfFloat, paramReadableMap.getInt("colorMapSize")));
      return;
    }
    paramAirMapHeatmap.setGradient(new Gradient(arrayOfInt, arrayOfFloat));
  }
  
  public void setOpacity(AirMapHeatmap paramAirMapHeatmap, double paramDouble)
  {
    paramAirMapHeatmap.setOpacity(paramDouble);
  }
  
  public void setPoints(AirMapHeatmap paramAirMapHeatmap, ReadableArray paramReadableArray)
  {
    WeightedLatLng[] arrayOfWeightedLatLng = new WeightedLatLng[paramReadableArray.size()];
    int i = 0;
    while (i < paramReadableArray.size())
    {
      Object localObject = paramReadableArray.getMap(i);
      LatLng localLatLng = new LatLng(((ReadableMap)localObject).getDouble("latitude"), ((ReadableMap)localObject).getDouble("longitude"));
      if (((ReadableMap)localObject).hasKey("weight")) {
        localObject = new WeightedLatLng(localLatLng, ((ReadableMap)localObject).getDouble("weight"));
      } else {
        localObject = new WeightedLatLng(localLatLng);
      }
      arrayOfWeightedLatLng[i] = localObject;
      i += 1;
    }
    paramAirMapHeatmap.setPoints(arrayOfWeightedLatLng);
  }
  
  public void setRadius(AirMapHeatmap paramAirMapHeatmap, int paramInt)
  {
    paramAirMapHeatmap.setRadius(paramInt);
  }
}
