package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.fabric.mounting.MountingManager;

public class UpdateEventEmitterMountItem
  implements MountItem
{
  private final EventEmitterWrapper mEventHandler;
  private final int mReactTag;
  
  public UpdateEventEmitterMountItem(int paramInt, EventEmitterWrapper paramEventEmitterWrapper)
  {
    mReactTag = paramInt;
    mEventHandler = paramEventEmitterWrapper;
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    paramMountingManager.updateEventEmitter(mReactTag, mEventHandler);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UpdateEventEmitterMountItem [");
    localStringBuilder.append(mReactTag);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}
