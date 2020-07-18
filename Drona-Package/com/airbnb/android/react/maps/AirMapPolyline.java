package com.airbnb.android.react.maps;

import android.content.Context;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import java.util.ArrayList;
import java.util.List;

public class AirMapPolyline
  extends AirMapFeature
{
  private int color;
  private List<LatLng> coordinates;
  private boolean geodesic;
  private Cap lineCap = new RoundCap();
  private List<PatternItem> pattern;
  private ReadableArray patternValues;
  private Polyline polyline;
  private PolylineOptions polylineOptions;
  private boolean tappable;
  private float width;
  private float zIndex;
  
  public AirMapPolyline(Context paramContext)
  {
    super(paramContext);
  }
  
  private void applyPattern()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a8 = a7\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  private PolylineOptions createPolylineOptions()
  {
    PolylineOptions localPolylineOptions = new PolylineOptions();
    localPolylineOptions.addAll(coordinates);
    localPolylineOptions.color(color);
    localPolylineOptions.width(width);
    localPolylineOptions.geodesic(geodesic);
    localPolylineOptions.zIndex(zIndex);
    localPolylineOptions.startCap(lineCap);
    localPolylineOptions.endCap(lineCap);
    localPolylineOptions.pattern(pattern);
    return localPolylineOptions;
  }
  
  public void addToMap(GoogleMap paramGoogleMap)
  {
    polyline = paramGoogleMap.addPolyline(getPolylineOptions());
    polyline.setClickable(tappable);
  }
  
  public Object getFeature()
  {
    return polyline;
  }
  
  public PolylineOptions getPolylineOptions()
  {
    if (polylineOptions == null) {
      polylineOptions = createPolylineOptions();
    }
    return polylineOptions;
  }
  
  public void removeFromMap(GoogleMap paramGoogleMap)
  {
    polyline.remove();
  }
  
  public void setColor(int paramInt)
  {
    color = paramInt;
    if (polyline != null) {
      polyline.setColor(paramInt);
    }
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
    if (polyline != null) {
      polyline.setPoints(coordinates);
    }
  }
  
  public void setGeodesic(boolean paramBoolean)
  {
    geodesic = paramBoolean;
    if (polyline != null) {
      polyline.setGeodesic(paramBoolean);
    }
  }
  
  public void setLineCap(Cap paramCap)
  {
    lineCap = paramCap;
    if (polyline != null)
    {
      polyline.setStartCap(paramCap);
      polyline.setEndCap(paramCap);
    }
    applyPattern();
  }
  
  public void setLineDashPattern(ReadableArray paramReadableArray)
  {
    patternValues = paramReadableArray;
    applyPattern();
  }
  
  public void setTappable(boolean paramBoolean)
  {
    tappable = paramBoolean;
    if (polyline != null) {
      polyline.setClickable(tappable);
    }
  }
  
  public void setWidth(float paramFloat)
  {
    width = paramFloat;
    if (polyline != null) {
      polyline.setWidth(paramFloat);
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    zIndex = paramFloat;
    if (polyline != null) {
      polyline.setZIndex(paramFloat);
    }
  }
}
