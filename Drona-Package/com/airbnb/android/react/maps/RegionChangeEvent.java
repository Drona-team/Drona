package com.airbnb.android.react.maps;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class RegionChangeEvent
  extends Event<RegionChangeEvent>
{
  private final LatLngBounds bounds;
  private final boolean continuous;
  
  public RegionChangeEvent(int paramInt, LatLngBounds paramLatLngBounds, boolean paramBoolean)
  {
    super(paramInt);
    bounds = paramLatLngBounds;
    continuous = paramBoolean;
  }
  
  public boolean canCoalesce()
  {
    return false;
  }
  
  public void dispatch(RCTEventEmitter paramRCTEventEmitter)
  {
    WritableNativeMap localWritableNativeMap1 = new WritableNativeMap();
    localWritableNativeMap1.putBoolean("continuous", continuous);
    WritableNativeMap localWritableNativeMap2 = new WritableNativeMap();
    LatLng localLatLng = bounds.getCenter();
    localWritableNativeMap2.putDouble("latitude", latitude);
    localWritableNativeMap2.putDouble("longitude", longitude);
    localWritableNativeMap2.putDouble("latitudeDelta", bounds.northeast.latitude - bounds.southwest.latitude);
    localWritableNativeMap2.putDouble("longitudeDelta", bounds.northeast.longitude - bounds.southwest.longitude);
    localWritableNativeMap1.putMap("region", localWritableNativeMap2);
    paramRCTEventEmitter.receiveEvent(getViewTag(), getEventName(), localWritableNativeMap1);
  }
  
  public String getEventName()
  {
    return "topChange";
  }
}
