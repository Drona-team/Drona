package com.facebook.react.fabric.mounting.mountitems;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.fabric.mounting.MountingManager;

public class DispatchCommandMountItem
  implements MountItem
{
  @Nullable
  private final ReadableArray mCommandArgs;
  private final int mCommandId;
  private final int mReactTag;
  
  public DispatchCommandMountItem(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    mReactTag = paramInt1;
    mCommandId = paramInt2;
    mCommandArgs = paramReadableArray;
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    paramMountingManager.receiveCommand(mReactTag, mCommandId, mCommandArgs);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DispatchCommandMountItem [");
    localStringBuilder.append(mReactTag);
    localStringBuilder.append("] ");
    localStringBuilder.append(mCommandId);
    return localStringBuilder.toString();
  }
}
