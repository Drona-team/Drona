package com.facebook.react.uimanager;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.facebook.react.common.SingleThreadAsserter;

public class ShadowNodeRegistry
{
  private final SparseBooleanArray mRootTags = new SparseBooleanArray();
  private final SparseArray<ReactShadowNode> mTagsToCSSNodes = new SparseArray();
  private final SingleThreadAsserter mThreadAsserter = new SingleThreadAsserter();
  
  public ShadowNodeRegistry() {}
  
  public void addNode(ReactShadowNode paramReactShadowNode)
  {
    mThreadAsserter.assertNow();
    mTagsToCSSNodes.put(paramReactShadowNode.getReactTag(), paramReactShadowNode);
  }
  
  public void addRootNode(ReactShadowNode paramReactShadowNode)
  {
    mThreadAsserter.assertNow();
    int i = paramReactShadowNode.getReactTag();
    mTagsToCSSNodes.put(i, paramReactShadowNode);
    mRootTags.put(i, true);
  }
  
  public ReactShadowNode getNode(int paramInt)
  {
    mThreadAsserter.assertNow();
    return (ReactShadowNode)mTagsToCSSNodes.get(paramInt);
  }
  
  public int getRootNodeCount()
  {
    mThreadAsserter.assertNow();
    return mRootTags.size();
  }
  
  public int getRootTag(int paramInt)
  {
    mThreadAsserter.assertNow();
    return mRootTags.keyAt(paramInt);
  }
  
  public boolean isRootNode(int paramInt)
  {
    mThreadAsserter.assertNow();
    return mRootTags.get(paramInt);
  }
  
  public void removeNode(int paramInt)
  {
    mThreadAsserter.assertNow();
    if (!mRootTags.get(paramInt))
    {
      mTagsToCSSNodes.remove(paramInt);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Trying to remove root node ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" without using removeRootNode!");
    throw new IllegalViewOperationException(localStringBuilder.toString());
  }
  
  public void removeRootNode(int paramInt)
  {
    mThreadAsserter.assertNow();
    if (paramInt == -1) {
      return;
    }
    if (mRootTags.get(paramInt))
    {
      mTagsToCSSNodes.remove(paramInt);
      mRootTags.delete(paramInt);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("View with tag ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" is not registered as a root view");
    throw new IllegalViewOperationException(localStringBuilder.toString());
  }
}