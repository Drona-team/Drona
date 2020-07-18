package com.facebook.react.views.view;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class ViewGroupClickEvent
  extends Event<ViewGroupClickEvent>
{
  private static final String EVENT_NAME = "topClick";
  
  public ViewGroupClickEvent(int paramInt)
  {
    super(paramInt);
  }
  
  public boolean canCoalesce()
  {
    return false;
  }
  
  public void dispatch(RCTEventEmitter paramRCTEventEmitter)
  {
    paramRCTEventEmitter.receiveEvent(getViewTag(), getEventName(), Arguments.createMap());
  }
  
  public String getEventName()
  {
    return "topClick";
  }
}
