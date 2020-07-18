package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.react.fabric.mounting.MountingManager;
import com.facebook.react.uimanager.StateWrapper;

public class UpdateStateMountItem
  implements MountItem
{
  private final int mReactTag;
  private final StateWrapper mStateWrapper;
  
  public UpdateStateMountItem(int paramInt, StateWrapper paramStateWrapper)
  {
    mReactTag = paramInt;
    mStateWrapper = paramStateWrapper;
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    paramMountingManager.updateState(mReactTag, mStateWrapper);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UpdateStateMountItem [");
    localStringBuilder.append(mReactTag);
    localStringBuilder.append("] - stateWrapper: ");
    localStringBuilder.append(mStateWrapper);
    return localStringBuilder.toString();
  }
}
