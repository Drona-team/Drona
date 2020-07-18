package com.airbnb.android.react.maps;

import android.app.Activity;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapsPackage
  implements ReactPackage
{
  public MapsPackage() {}
  
  public MapsPackage(Activity paramActivity) {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    return Arrays.asList(new NativeModule[] { new AirMapModule(paramReactApplicationContext) });
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    AirMapCalloutManager localAirMapCalloutManager = new AirMapCalloutManager();
    AirMapMarkerManager localAirMapMarkerManager = new AirMapMarkerManager();
    AirMapPolylineManager localAirMapPolylineManager = new AirMapPolylineManager(paramReactApplicationContext);
    AirMapGradientPolylineManager localAirMapGradientPolylineManager = new AirMapGradientPolylineManager(paramReactApplicationContext);
    AirMapPolygonManager localAirMapPolygonManager = new AirMapPolygonManager(paramReactApplicationContext);
    AirMapCircleManager localAirMapCircleManager = new AirMapCircleManager(paramReactApplicationContext);
    AirMapManager localAirMapManager = new AirMapManager(paramReactApplicationContext);
    AirMapLiteManager localAirMapLiteManager = new AirMapLiteManager(paramReactApplicationContext);
    AirMapUrlTileManager localAirMapUrlTileManager = new AirMapUrlTileManager(paramReactApplicationContext);
    AirMapWMSTileManager localAirMapWMSTileManager = new AirMapWMSTileManager(paramReactApplicationContext);
    AirMapLocalTileManager localAirMapLocalTileManager = new AirMapLocalTileManager(paramReactApplicationContext);
    paramReactApplicationContext = new AirMapOverlayManager(paramReactApplicationContext);
    AirMapHeatmapManager localAirMapHeatmapManager = new AirMapHeatmapManager();
    localAirMapManager.setMarkerManager(localAirMapMarkerManager);
    return Arrays.asList(new ViewManager[] { localAirMapCalloutManager, localAirMapMarkerManager, localAirMapPolylineManager, localAirMapGradientPolylineManager, localAirMapPolygonManager, localAirMapCircleManager, localAirMapManager, localAirMapLiteManager, localAirMapUrlTileManager, localAirMapWMSTileManager, localAirMapLocalTileManager, paramReactApplicationContext, localAirMapHeatmapManager });
  }
}