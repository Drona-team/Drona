package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.fabric.mounting.MountingManager;

public class UpdateLocalDataMountItem
  implements MountItem
{
  private final ReadableMap mNewLocalData;
  private final int mReactTag;
  
  public UpdateLocalDataMountItem(int paramInt, ReadableMap paramReadableMap)
  {
    mReactTag = paramInt;
    mNewLocalData = paramReadableMap;
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    paramMountingManager.updateLocalData(mReactTag, mNewLocalData);
  }
  
  public ReadableMap getNewLocalData()
  {
    return mNewLocalData;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UpdateLocalDataMountItem [");
    localStringBuilder.append(mReactTag);
    localStringBuilder.append("] - localData: ");
    localStringBuilder.append(mNewLocalData);
    return localStringBuilder.toString();
  }
}
