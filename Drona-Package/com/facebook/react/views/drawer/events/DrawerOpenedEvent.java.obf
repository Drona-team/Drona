package com.facebook.react.views.drawer.events;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class DrawerOpenedEvent
  extends Event<DrawerOpenedEvent>
{
  public static final String EVENT_NAME = "topDrawerOpen";
  
  public DrawerOpenedEvent(int paramInt)
  {
    super(paramInt);
  }
  
  public void dispatch(RCTEventEmitter paramRCTEventEmitter)
  {
    paramRCTEventEmitter.receiveEvent(getViewTag(), getEventName(), Arguments.createMap());
  }
  
  public short getCoalescingKey()
  {
    return 0;
  }
  
  public String getEventName()
  {
    return "topDrawerOpen";
  }
}
