package com.airbnb.android.react.maps;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider.Builder;
import com.google.maps.android.heatmaps.WeightedLatLng;
import java.util.Arrays;
import java.util.List;

public class AirMapHeatmap
  extends AirMapFeature
{
  private Gradient gradient;
  private TileOverlay heatmap;
  private TileOverlayOptions heatmapOptions;
  private HeatmapTileProvider heatmapTileProvider;
  private Double opacity;
  private List<WeightedLatLng> points;
  private Integer radius;
  
  public AirMapHeatmap(Context paramContext)
  {
    super(paramContext);
  }
  
  private TileOverlayOptions createHeatmapOptions()
  {
    TileOverlayOptions localTileOverlayOptions = new TileOverlayOptions();
    if (heatmapTileProvider == null)
    {
      HeatmapTileProvider.Builder localBuilder = new HeatmapTileProvider.Builder().weightedData(points);
      if (radius != null) {
        localBuilder.radius(radius.intValue());
      }
      if (opacity != null) {
        localBuilder.opacity(opacity.doubleValue());
      }
      if (gradient != null) {
        localBuilder.gradient(gradient);
      }
      heatmapTileProvider = localBuilder.build();
    }
    localTileOverlayOptions.tileProvider((TileProvider)heatmapTileProvider);
    return localTileOverlayOptions;
  }
  
  public void addToMap(GoogleMap paramGoogleMap)
  {
    Log.d("AirMapHeatmap", "ADD TO MAP");
    heatmap = paramGoogleMap.addTileOverlay(getHeatmapOptions());
  }
  
  public Object getFeature()
  {
    return heatmap;
  }
  
  public TileOverlayOptions getHeatmapOptions()
  {
    if (heatmapOptions == null) {
      heatmapOptions = createHeatmapOptions();
    }
    return heatmapOptions;
  }
  
  public void removeFromMap(GoogleMap paramGoogleMap)
  {
    heatmap.remove();
  }
  
  public void setGradient(Gradient paramGradient)
  {
    gradient = paramGradient;
    if (heatmapTileProvider != null) {
      heatmapTileProvider.setGradient(paramGradient);
    }
    if (heatmap != null) {
      heatmap.clearTileCache();
    }
  }
  
  public void setOpacity(double paramDouble)
  {
    opacity = new Double(paramDouble);
    if (heatmapTileProvider != null) {
      heatmapTileProvider.setOpacity(paramDouble);
    }
    if (heatmap != null) {
      heatmap.clearTileCache();
    }
  }
  
  public void setPoints(WeightedLatLng[] paramArrayOfWeightedLatLng)
  {
    points = Arrays.asList(paramArrayOfWeightedLatLng);
    if (heatmapTileProvider != null) {
      heatmapTileProvider.setWeightedData(points);
    }
    if (heatmap != null) {
      heatmap.clearTileCache();
    }
  }
  
  public void setRadius(int paramInt)
  {
    radius = new Integer(paramInt);
    if (heatmapTileProvider != null) {
      heatmapTileProvider.setRadius(paramInt);
    }
    if (heatmap != null) {
      heatmap.clearTileCache();
    }
  }
}
