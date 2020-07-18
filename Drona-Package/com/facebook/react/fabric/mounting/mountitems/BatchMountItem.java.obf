package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.common.logging.FLog;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.fabric.FabricUIManager;
import com.facebook.react.fabric.mounting.MountingManager;
import com.facebook.systrace.Systrace;

@DoNotStrip
public class BatchMountItem
  implements MountItem
{
  private final int mCommitNumber;
  private final MountItem[] mMountItems;
  private final int mSize;
  
  public BatchMountItem(MountItem[] paramArrayOfMountItem, int paramInt1, int paramInt2)
  {
    if (paramArrayOfMountItem != null)
    {
      if ((paramInt1 >= 0) && (paramInt1 <= paramArrayOfMountItem.length))
      {
        mMountItems = paramArrayOfMountItem;
        mSize = paramInt1;
        mCommitNumber = paramInt2;
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid size received by parameter size: ");
      localStringBuilder.append(paramInt1);
      localStringBuilder.append(" items.size = ");
      localStringBuilder.append(paramArrayOfMountItem.length);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    throw new NullPointerException();
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("FabricUIManager::mountViews - ");
    ((StringBuilder)localObject).append(mSize);
    ((StringBuilder)localObject).append(" items");
    Systrace.beginSection(0L, ((StringBuilder)localObject).toString());
    if (mCommitNumber > 0) {
      ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_BATCH_EXECUTION_START, null, mCommitNumber);
    }
    int i = 0;
    while (i < mSize)
    {
      localObject = mMountItems[i];
      if (FabricUIManager.DEBUG)
      {
        String str = FabricUIManager.TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Executing mountItem: ");
        localStringBuilder.append(localObject);
        FLog.d(str, localStringBuilder.toString());
      }
      ((MountItem)localObject).execute(paramMountingManager);
      i += 1;
    }
    if (mCommitNumber > 0) {
      ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_BATCH_EXECUTION_END, null, mCommitNumber);
    }
    Systrace.endSection(0L);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("BatchMountItem - size ");
    localStringBuilder.append(mMountItems.length);
    return localStringBuilder.toString();
  }
}
