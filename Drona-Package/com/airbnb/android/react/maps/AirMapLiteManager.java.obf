package com.airbnb.android.react.maps;

import com.facebook.react.bridge.ReactApplicationContext;
import com.google.android.gms.maps.GoogleMapOptions;

public class AirMapLiteManager
  extends AirMapManager
{
  private static final String REACT_CLASS = "AIRMapLite";
  
  public AirMapLiteManager(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    googleMapOptions = new GoogleMapOptions().liteMode(true);
  }
  
  public String getName()
  {
    return "AIRMapLite";
  }
}
