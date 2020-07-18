package com.airbnb.android.react.maps;

import android.content.Context;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.ArrayList;
import java.util.List;

public class AirMapPolygon
  extends AirMapFeature
{
  private List<LatLng> coordinates;
  private int fillColor;
  private boolean geodesic;
  private List<List<LatLng>> holes;
  private Polygon polygon;
  private PolygonOptions polygonOptions;
  private int strokeColor;
  private float strokeWidth;
  private boolean tappable;
  private float zIndex;
  
  public AirMapPolygon(Context paramContext)
  {
    super(paramContext);
  }
  
  private PolygonOptions createPolygonOptions()
  {
    PolygonOptions localPolygonOptions = new PolygonOptions();
    localPolygonOptions.addAll(coordinates);
    localPolygonOptions.fillColor(fillColor);
    localPolygonOptions.strokeColor(strokeColor);
    localPolygonOptions.strokeWidth(strokeWidth);
    localPolygonOptions.geodesic(geodesic);
    localPolygonOptions.zIndex(zIndex);
    if (holes != null)
    {
      int i = 0;
      while (i < holes.size())
      {
        localPolygonOptions.addHole((Iterable)holes.get(i));
        i += 1;
      }
    }
    return localPolygonOptions;
  }
  
  public void addToMap(GoogleMap paramGoogleMap)
  {
    polygon = paramGoogleMap.addPolygon(getPolygonOptions());
    polygon.setClickable(tappable);
  }
  
  public Object getFeature()
  {
    return polygon;
  }
  
  public PolygonOptions getPolygonOptions()
  {
    if (polygonOptions == null) {
      polygonOptions = createPolygonOptions();
    }
    return polygonOptions;
  }
  
  public void removeFromMap(GoogleMap paramGoogleMap)
  {
    polygon.remove();
  }
  
  public void setCoordinates(ReadableArray paramReadableArray)
  {
    coordinates = new ArrayList(paramReadableArray.size());
    int i = 0;
    while (i < paramReadableArray.size())
    {
      ReadableMap localReadableMap = paramReadableArray.getMap(i);
      coordinates.add(i, new LatLng(localReadableMap.getDouble("latitude"), localReadableMap.getDouble("longitude")));
      i += 1;
    }
    if (polygon != null) {
      polygon.setPoints(coordinates);
    }
  }
  
  public void setFillColor(int paramInt)
  {
    fillColor = paramInt;
    if (polygon != null) {
      polygon.setFillColor(paramInt);
    }
  }
  
  public void setGeodesic(boolean paramBoolean)
  {
    geodesic = paramBoolean;
    if (polygon != null) {
      polygon.setGeodesic(paramBoolean);
    }
  }
  
  public void setHoles(ReadableArray paramReadableArray)
  {
    if (paramReadableArray == null) {
      return;
    }
    holes = new ArrayList(paramReadableArray.size());
    int i = 0;
    while (i < paramReadableArray.size())
    {
      ReadableArray localReadableArray = paramReadableArray.getArray(i);
      if (localReadableArray.size() >= 3)
      {
        ArrayList localArrayList = new ArrayList();
        int j = 0;
        while (j < localReadableArray.size())
        {
          ReadableMap localReadableMap = localReadableArray.getMap(j);
          localArrayList.add(new LatLng(localReadableMap.getDouble("latitude"), localReadableMap.getDouble("longitude")));
          j += 1;
        }
        if (localArrayList.size() == 3) {
          localArrayList.add(localArrayList.get(0));
        }
        holes.add(localArrayList);
      }
      i += 1;
    }
    if (polygon != null) {
      polygon.setHoles(holes);
    }
  }
  
  public void setStrokeColor(int paramInt)
  {
    strokeColor = paramInt;
    if (polygon != null) {
      polygon.setStrokeColor(paramInt);
    }
  }
  
  public void setStrokeWidth(float paramFloat)
  {
    strokeWidth = paramFloat;
    if (polygon != null) {
      polygon.setStrokeWidth(paramFloat);
    }
  }
  
  public void setTappable(boolean paramBoolean)
  {
    tappable = paramBoolean;
    if (polygon != null) {
      polygon.setClickable(tappable);
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    zIndex = paramFloat;
    if (polygon != null) {
      polygon.setZIndex(paramFloat);
    }
  }
}
