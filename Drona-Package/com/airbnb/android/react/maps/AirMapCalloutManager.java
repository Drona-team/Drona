package com.airbnb.android.react.maps;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import java.util.Map;

public class AirMapCalloutManager
  extends ViewGroupManager<AirMapCallout>
{
  public AirMapCalloutManager() {}
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new SizeReportingShadowNode();
  }
  
  public AirMapCallout createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapCallout(paramThemedReactContext);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get("onPress", MapBuilder.get("registrationName", "onPress"));
  }
  
  public String getName()
  {
    return "AIRMapCallout";
  }
  
  public void setTooltip(AirMapCallout paramAirMapCallout, boolean paramBoolean)
  {
    paramAirMapCallout.setTooltip(paramBoolean);
  }
  
  public void updateExtraData(AirMapCallout paramAirMapCallout, Object paramObject)
  {
    paramObject = (Map)paramObject;
    float f1 = ((Float)paramObject.get("width")).floatValue();
    float f2 = ((Float)paramObject.get("height")).floatValue();
    width = ((int)f1);
    height = ((int)f2);
  }
}
