package com.facebook.react.fabric.mounting.mountitems;

import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.fabric.FabricUIManager;
import com.facebook.react.fabric.mounting.MountingManager;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;

public class PreAllocateViewMountItem
  implements MountItem
{
  private final String mComponent;
  private final ThemedReactContext mContext;
  private final boolean mIsLayoutable;
  @Nullable
  private final ReadableMap mProps;
  private final int mReactTag;
  private final int mRootTag;
  @Nullable
  private final StateWrapper mStateWrapper;
  
  public PreAllocateViewMountItem(ThemedReactContext paramThemedReactContext, int paramInt1, int paramInt2, String paramString, ReadableMap paramReadableMap, StateWrapper paramStateWrapper, boolean paramBoolean)
  {
    mContext = paramThemedReactContext;
    mComponent = paramString;
    mRootTag = paramInt1;
    mProps = paramReadableMap;
    mStateWrapper = paramStateWrapper;
    mReactTag = paramInt2;
    mIsLayoutable = paramBoolean;
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    if (FabricUIManager.DEBUG)
    {
      String str = FabricUIManager.TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Executing pre-allocation of: ");
      localStringBuilder.append(toString());
      FLog.d(str, localStringBuilder.toString());
    }
    paramMountingManager.preallocateView(mContext, mComponent, mReactTag, mProps, mStateWrapper, mIsLayoutable);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PreAllocateViewMountItem [");
    localStringBuilder.append(mReactTag);
    localStringBuilder.append("] - component: ");
    localStringBuilder.append(mComponent);
    localStringBuilder.append(" rootTag: ");
    localStringBuilder.append(mRootTag);
    localStringBuilder.append(" isLayoutable: ");
    localStringBuilder.append(mIsLayoutable);
    localStringBuilder.append(" props: ");
    localStringBuilder.append(mProps);
    return localStringBuilder.toString();
  }
}
