package com.airbnb.android.react.maps;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import java.util.HashMap;
import java.util.Map;

public class AirMapManager
  extends ViewGroupManager<AirMapView>
{
  private static final int ANIMATE_CAMERA = 12;
  private static final int ANIMATE_TO_BEARING = 4;
  private static final int ANIMATE_TO_COORDINATE = 2;
  private static final int ANIMATE_TO_NAVIGATION = 9;
  private static final int ANIMATE_TO_REGION = 1;
  private static final int ANIMATE_TO_VIEWING_ANGLE = 3;
  private static final int FIT_TO_COORDINATES = 7;
  private static final int FIT_TO_ELEMENTS = 5;
  private static final int FIT_TO_SUPPLIED_MARKERS = 6;
  private static final String REACT_CLASS = "AIRMap";
  private static final int SET_CAMERA = 11;
  private static final int SET_INDOOR_ACTIVE_LEVEL_INDEX = 10;
  private static final int SET_MAP_BOUNDARIES = 8;
  private final Map<String, Integer> MAP_TYPES = MapBuilder.of("standard", Integer.valueOf(1), "satellite", Integer.valueOf(2), "hybrid", Integer.valueOf(4), "terrain", Integer.valueOf(3), "none", Integer.valueOf(0));
  private final ReactApplicationContext appContext;
  protected GoogleMapOptions googleMapOptions;
  private AirMapMarkerManager markerManager;
  
  public AirMapManager(ReactApplicationContext paramReactApplicationContext)
  {
    appContext = paramReactApplicationContext;
    googleMapOptions = new GoogleMapOptions();
  }
  
  public static Map CreateMap(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put(paramObject1, paramObject2);
    localHashMap.put(paramObject3, paramObject4);
    localHashMap.put(paramObject5, paramObject6);
    localHashMap.put(paramObject7, paramObject8);
    localHashMap.put(paramObject9, paramObject10);
    localHashMap.put(paramObject11, paramObject12);
    localHashMap.put(paramObject13, paramObject14);
    localHashMap.put(paramObject15, paramObject16);
    localHashMap.put(paramObject17, paramObject18);
    localHashMap.put(paramObject19, paramObject20);
    return localHashMap;
  }
  
  private void emitMapError(ThemedReactContext paramThemedReactContext, String paramString1, String paramString2)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putString("message", paramString1);
    localWritableMap.putString("type", paramString2);
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)paramThemedReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("onError", localWritableMap);
  }
  
  public void addView(AirMapView paramAirMapView, View paramView, int paramInt)
  {
    paramAirMapView.addFeature(paramView, paramInt);
  }
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new SizeReportingShadowNode();
  }
  
  protected AirMapView createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapView(paramThemedReactContext, appContext, this, googleMapOptions);
  }
  
  public View getChildAt(AirMapView paramAirMapView, int paramInt)
  {
    return paramAirMapView.getFeatureAt(paramInt);
  }
  
  public int getChildCount(AirMapView paramAirMapView)
  {
    return paramAirMapView.getFeatureCount();
  }
  
  public Map getCommandsMap()
  {
    Map localMap = CreateMap("setCamera", Integer.valueOf(11), "animateCamera", Integer.valueOf(12), "animateToRegion", Integer.valueOf(1), "animateToCoordinate", Integer.valueOf(2), "animateToViewingAngle", Integer.valueOf(3), "animateToBearing", Integer.valueOf(4), "fitToElements", Integer.valueOf(5), "fitToSuppliedMarkers", Integer.valueOf(6), "fitToCoordinates", Integer.valueOf(7), "animateToNavigation", Integer.valueOf(9));
    localMap.putAll(MapBuilder.get("setMapBoundaries", Integer.valueOf(8), "setIndoorActiveLevelIndex", Integer.valueOf(10)));
    return localMap;
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    Map localMap = MapBuilder.get("onMapReady", MapBuilder.get("registrationName", "onMapReady"), "onPress", MapBuilder.get("registrationName", "onPress"), "onLongPress", MapBuilder.get("registrationName", "onLongPress"), "onMarkerPress", MapBuilder.get("registrationName", "onMarkerPress"), "onMarkerSelect", MapBuilder.get("registrationName", "onMarkerSelect"), "onMarkerDeselect", MapBuilder.get("registrationName", "onMarkerDeselect"), "onCalloutPress", MapBuilder.get("registrationName", "onCalloutPress"));
    localMap.putAll(MapBuilder.get("onUserLocationChange", MapBuilder.get("registrationName", "onUserLocationChange"), "onMarkerDragStart", MapBuilder.get("registrationName", "onMarkerDragStart"), "onMarkerDrag", MapBuilder.get("registrationName", "onMarkerDrag"), "onMarkerDragEnd", MapBuilder.get("registrationName", "onMarkerDragEnd"), "onPanDrag", MapBuilder.get("registrationName", "onPanDrag"), "onKmlReady", MapBuilder.get("registrationName", "onKmlReady"), "onPoiClick", MapBuilder.get("registrationName", "onPoiClick")));
    localMap.putAll(MapBuilder.get("onIndoorLevelActivated", MapBuilder.get("registrationName", "onIndoorLevelActivated"), "onIndoorBuildingFocused", MapBuilder.get("registrationName", "onIndoorBuildingFocused"), "onDoublePress", MapBuilder.get("registrationName", "onDoublePress"), "onMapLoaded", MapBuilder.get("registrationName", "onMapLoaded")));
    return localMap;
  }
  
  public AirMapMarkerManager getMarkerManager()
  {
    return markerManager;
  }
  
  public String getName()
  {
    return "AIRMap";
  }
  
  public void onDropViewInstance(AirMapView paramAirMapView)
  {
    paramAirMapView.doDestroy();
    super.onDropViewInstance((View)paramAirMapView);
  }
  
  void pushEvent(ThemedReactContext paramThemedReactContext, View paramView, String paramString, WritableMap paramWritableMap)
  {
    ((RCTEventEmitter)paramThemedReactContext.getJSModule(RCTEventEmitter.class)).receiveEvent(paramView.getId(), paramString, paramWritableMap);
  }
  
  public void receiveCommand(AirMapView paramAirMapView, int paramInt, ReadableArray paramReadableArray)
  {
    double d;
    switch (paramInt)
    {
    default: 
      return;
    case 12: 
      paramAirMapView.animateToCamera(paramReadableArray.getMap(0), Integer.valueOf(paramReadableArray.getInt(1)).intValue());
      return;
    case 11: 
      paramAirMapView.animateToCamera(paramReadableArray.getMap(0), 0);
      return;
    case 10: 
      paramAirMapView.setIndoorActiveLevelIndex(paramReadableArray.getInt(0));
      return;
    case 9: 
      localObject = paramReadableArray.getMap(0);
      d = ((ReadableMap)localObject).getDouble("longitude");
      paramAirMapView.animateToNavigation(new LatLng(Double.valueOf(((ReadableMap)localObject).getDouble("latitude")).doubleValue(), Double.valueOf(d).doubleValue()), (float)paramReadableArray.getDouble(1), (float)paramReadableArray.getDouble(2), Integer.valueOf(paramReadableArray.getInt(3)).intValue());
      return;
    case 8: 
      paramAirMapView.setMapBoundaries(paramReadableArray.getMap(0), paramReadableArray.getMap(1));
      return;
    case 7: 
      paramAirMapView.fitToCoordinates(paramReadableArray.getArray(0), paramReadableArray.getMap(1), paramReadableArray.getBoolean(2));
      return;
    case 6: 
      paramAirMapView.fitToSuppliedMarkers(paramReadableArray.getArray(0), paramReadableArray.getMap(1), paramReadableArray.getBoolean(2));
      return;
    case 5: 
      paramAirMapView.fitToElements(paramReadableArray.getBoolean(0));
      return;
    case 4: 
      paramAirMapView.animateToBearing((float)paramReadableArray.getDouble(0), Integer.valueOf(paramReadableArray.getInt(1)).intValue());
      return;
    case 3: 
      paramAirMapView.animateToViewingAngle((float)paramReadableArray.getDouble(0), Integer.valueOf(paramReadableArray.getInt(1)).intValue());
      return;
    case 2: 
      localObject = paramReadableArray.getMap(0);
      paramInt = paramReadableArray.getInt(1);
      d = ((ReadableMap)localObject).getDouble("longitude");
      paramAirMapView.animateToCoordinate(new LatLng(Double.valueOf(((ReadableMap)localObject).getDouble("latitude")).doubleValue(), Double.valueOf(d).doubleValue()), Integer.valueOf(paramInt).intValue());
      return;
    }
    Object localObject = paramReadableArray.getMap(0);
    paramInt = paramReadableArray.getInt(1);
    paramReadableArray = Double.valueOf(((ReadableMap)localObject).getDouble("longitude"));
    Double localDouble1 = Double.valueOf(((ReadableMap)localObject).getDouble("latitude"));
    Double localDouble2 = Double.valueOf(((ReadableMap)localObject).getDouble("longitudeDelta"));
    localObject = Double.valueOf(((ReadableMap)localObject).getDouble("latitudeDelta"));
    paramAirMapView.animateToRegion(new LatLngBounds(new LatLng(localDouble1.doubleValue() - ((Double)localObject).doubleValue() / 2.0D, paramReadableArray.doubleValue() - localDouble2.doubleValue() / 2.0D), new LatLng(localDouble1.doubleValue() + ((Double)localObject).doubleValue() / 2.0D, paramReadableArray.doubleValue() + localDouble2.doubleValue() / 2.0D)), Integer.valueOf(paramInt).intValue());
  }
  
  public void removeViewAt(AirMapView paramAirMapView, int paramInt)
  {
    paramAirMapView.removeFeatureAt(paramInt);
  }
  
  public void setCacheEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.setCacheEnabled(paramBoolean);
  }
  
  public void setCamera(AirMapView paramAirMapView, ReadableMap paramReadableMap)
  {
    paramAirMapView.setCamera(paramReadableMap);
  }
  
  public void setHandlePanDrag(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.setHandlePanDrag(paramBoolean);
  }
  
  public void setInitialCamera(AirMapView paramAirMapView, ReadableMap paramReadableMap)
  {
    paramAirMapView.setInitialCamera(paramReadableMap);
  }
  
  public void setInitialRegion(AirMapView paramAirMapView, ReadableMap paramReadableMap)
  {
    paramAirMapView.setInitialRegion(paramReadableMap);
  }
  
  public void setKmlSrc(AirMapView paramAirMapView, String paramString)
  {
    if (paramString != null) {
      paramAirMapView.setKmlSrc(paramString);
    }
  }
  
  public void setLoadingBackgroundColor(AirMapView paramAirMapView, Integer paramInteger)
  {
    paramAirMapView.setLoadingBackgroundColor(paramInteger);
  }
  
  public void setLoadingEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.enableMapLoading(paramBoolean);
  }
  
  public void setLoadingIndicatorColor(AirMapView paramAirMapView, Integer paramInteger)
  {
    paramAirMapView.setLoadingIndicatorColor(paramInteger);
  }
  
  public void setMapPadding(AirMapView paramAirMapView, ReadableMap paramReadableMap)
  {
    double d = getResourcesgetDisplayMetricsdensity;
    int i2 = 0;
    int i1;
    int n;
    int m;
    if (paramReadableMap != null)
    {
      int i;
      if (paramReadableMap.hasKey("left")) {
        i = (int)(paramReadableMap.getDouble("left") * d);
      } else {
        i = 0;
      }
      int j;
      if (paramReadableMap.hasKey("top")) {
        j = (int)(paramReadableMap.getDouble("top") * d);
      } else {
        j = 0;
      }
      int k;
      if (paramReadableMap.hasKey("right")) {
        k = (int)(paramReadableMap.getDouble("right") * d);
      } else {
        k = 0;
      }
      i1 = i;
      n = j;
      m = k;
      if (paramReadableMap.hasKey("bottom"))
      {
        i2 = (int)(paramReadableMap.getDouble("bottom") * d);
        i1 = i;
        n = j;
        m = k;
      }
    }
    else
    {
      i1 = 0;
      n = 0;
      m = 0;
    }
    record.setPadding(i1, n, m, i2);
  }
  
  public void setMapStyle(AirMapView paramAirMapView, String paramString)
  {
    record.setMapStyle(new MapStyleOptions(paramString));
  }
  
  public void setMapType(AirMapView paramAirMapView, String paramString)
  {
    int i = ((Integer)MAP_TYPES.get(paramString)).intValue();
    record.setMapType(i);
  }
  
  public void setMarkerManager(AirMapMarkerManager paramAirMapMarkerManager)
  {
    markerManager = paramAirMapMarkerManager;
  }
  
  public void setMaxZoomLevel(AirMapView paramAirMapView, float paramFloat)
  {
    record.setMaxZoomPreference(paramFloat);
  }
  
  public void setMinZoomLevel(AirMapView paramAirMapView, float paramFloat)
  {
    record.setMinZoomPreference(paramFloat);
  }
  
  public void setMoveOnMarkerPress(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.setMoveOnMarkerPress(paramBoolean);
  }
  
  public void setPitchEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setTiltGesturesEnabled(paramBoolean);
  }
  
  public void setRegion(AirMapView paramAirMapView, ReadableMap paramReadableMap)
  {
    paramAirMapView.setRegion(paramReadableMap);
  }
  
  public void setRotateEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setRotateGesturesEnabled(paramBoolean);
  }
  
  public void setScrollEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setScrollGesturesEnabled(paramBoolean);
  }
  
  public void setShowBuildings(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.setBuildingsEnabled(paramBoolean);
  }
  
  public void setShowIndoors(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.setIndoorEnabled(paramBoolean);
  }
  
  public void setShowTraffic(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.setTrafficEnabled(paramBoolean);
  }
  
  public void setShowsCompass(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setCompassEnabled(paramBoolean);
  }
  
  public void setShowsIndoorLevelPicker(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setIndoorLevelPickerEnabled(paramBoolean);
  }
  
  public void setShowsMyLocationButton(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.setShowsMyLocationButton(paramBoolean);
  }
  
  public void setShowsUserLocation(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.setShowsUserLocation(paramBoolean);
  }
  
  public void setToolbarEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    paramAirMapView.setToolbarEnabled(paramBoolean);
  }
  
  public void setZoomControlEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setZoomControlsEnabled(paramBoolean);
  }
  
  public void setZoomEnabled(AirMapView paramAirMapView, boolean paramBoolean)
  {
    record.getUiSettings().setZoomGesturesEnabled(paramBoolean);
  }
  
  public void updateExtraData(AirMapView paramAirMapView, Object paramObject)
  {
    paramAirMapView.updateExtraData(paramObject);
  }
}
