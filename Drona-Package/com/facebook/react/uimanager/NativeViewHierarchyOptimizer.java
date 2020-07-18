package com.facebook.react.uimanager;

import android.util.SparseBooleanArray;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

public class NativeViewHierarchyOptimizer
{
  private static final boolean ENABLED = true;
  private final ShadowNodeRegistry mShadowNodeRegistry;
  private final SparseBooleanArray mTagsWithLayoutVisited = new SparseBooleanArray();
  private final UIViewOperationQueue mUIViewOperationQueue;
  
  public NativeViewHierarchyOptimizer(UIViewOperationQueue paramUIViewOperationQueue, ShadowNodeRegistry paramShadowNodeRegistry)
  {
    mUIViewOperationQueue = paramUIViewOperationQueue;
    mShadowNodeRegistry = paramShadowNodeRegistry;
  }
  
  private void addGrandchildren(ReactShadowNode paramReactShadowNode1, ReactShadowNode paramReactShadowNode2, int paramInt)
  {
    boolean bool;
    if (paramReactShadowNode2.getNativeKind() != NativeKind.PARENT) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.assertCondition(bool);
    int j = 0;
    int i = paramInt;
    paramInt = j;
    while (paramInt < paramReactShadowNode2.getChildCount())
    {
      ReactShadowNode localReactShadowNode = paramReactShadowNode2.getChildAt(paramInt);
      if (localReactShadowNode.getNativeParent() == null) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.assertCondition(bool);
      j = paramReactShadowNode1.getNativeChildCount();
      if (localReactShadowNode.getNativeKind() == NativeKind.NONE) {
        addNonNativeChild(paramReactShadowNode1, localReactShadowNode, i);
      } else {
        addNativeChild(paramReactShadowNode1, localReactShadowNode, i);
      }
      i += paramReactShadowNode1.getNativeChildCount() - j;
      paramInt += 1;
    }
  }
  
  private void addNativeChild(ReactShadowNode paramReactShadowNode1, ReactShadowNode paramReactShadowNode2, int paramInt)
  {
    paramReactShadowNode1.addNativeChildAt(paramReactShadowNode2, paramInt);
    mUIViewOperationQueue.enqueueManageChildren(paramReactShadowNode1.getReactTag(), null, new ViewAtIndex[] { new ViewAtIndex(paramReactShadowNode2.getReactTag(), paramInt) }, null, null);
    if (paramReactShadowNode2.getNativeKind() != NativeKind.PARENT) {
      addGrandchildren(paramReactShadowNode1, paramReactShadowNode2, paramInt + 1);
    }
  }
  
  private void addNodeToNode(ReactShadowNode paramReactShadowNode1, ReactShadowNode paramReactShadowNode2, int paramInt)
  {
    int i = paramReactShadowNode1.getNativeOffsetForChild(paramReactShadowNode1.getChildAt(paramInt));
    paramInt = i;
    ReactShadowNode localReactShadowNode = paramReactShadowNode1;
    if (paramReactShadowNode1.getNativeKind() != NativeKind.PARENT)
    {
      paramReactShadowNode1 = walkUpUntilNativeKindIsParent(paramReactShadowNode1, i);
      if (paramReactShadowNode1 == null) {
        return;
      }
      paramInt = index;
      localReactShadowNode = node;
    }
    if (paramReactShadowNode2.getNativeKind() != NativeKind.NONE)
    {
      addNativeChild(localReactShadowNode, paramReactShadowNode2, paramInt);
      return;
    }
    addNonNativeChild(localReactShadowNode, paramReactShadowNode2, paramInt);
  }
  
  private void addNonNativeChild(ReactShadowNode paramReactShadowNode1, ReactShadowNode paramReactShadowNode2, int paramInt)
  {
    addGrandchildren(paramReactShadowNode1, paramReactShadowNode2, paramInt);
  }
  
  private void applyLayoutBase(ReactShadowNode paramReactShadowNode)
  {
    int i = paramReactShadowNode.getReactTag();
    if (mTagsWithLayoutVisited.get(i)) {
      return;
    }
    mTagsWithLayoutVisited.put(i, true);
    ReactShadowNode localReactShadowNode = paramReactShadowNode.getParent();
    int k = paramReactShadowNode.getScreenX();
    int j;
    for (i = paramReactShadowNode.getScreenY(); (localReactShadowNode != null) && (localReactShadowNode.getNativeKind() != NativeKind.PARENT); i = j)
    {
      int m = k;
      j = i;
      if (!localReactShadowNode.isVirtual())
      {
        m = k + Math.round(localReactShadowNode.getLayoutX());
        j = i + Math.round(localReactShadowNode.getLayoutY());
      }
      localReactShadowNode = localReactShadowNode.getParent();
      k = m;
    }
    applyLayoutRecursive(paramReactShadowNode, k, i);
  }
  
  private void applyLayoutRecursive(ReactShadowNode paramReactShadowNode, int paramInt1, int paramInt2)
  {
    if ((paramReactShadowNode.getNativeKind() != NativeKind.NONE) && (paramReactShadowNode.getNativeParent() != null))
    {
      i = paramReactShadowNode.getReactTag();
      mUIViewOperationQueue.enqueueUpdateLayout(paramReactShadowNode.getLayoutParent().getReactTag(), i, paramInt1, paramInt2, paramReactShadowNode.getScreenWidth(), paramReactShadowNode.getScreenHeight());
      return;
    }
    int i = 0;
    while (i < paramReactShadowNode.getChildCount())
    {
      ReactShadowNode localReactShadowNode = paramReactShadowNode.getChildAt(i);
      int j = localReactShadowNode.getReactTag();
      if (!mTagsWithLayoutVisited.get(j))
      {
        mTagsWithLayoutVisited.put(j, true);
        applyLayoutRecursive(localReactShadowNode, localReactShadowNode.getScreenX() + paramInt1, localReactShadowNode.getScreenY() + paramInt2);
      }
      i += 1;
    }
  }
  
  public static void assertNodeSupportedWithoutOptimizer(ReactShadowNode paramReactShadowNode)
  {
    boolean bool;
    if (paramReactShadowNode.getNativeKind() != NativeKind.LEAF) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.assertCondition(bool, "Nodes with NativeKind.LEAF are not supported when the optimizer is disabled");
  }
  
  public static void handleRemoveNode(ReactShadowNode paramReactShadowNode)
  {
    paramReactShadowNode.removeAllNativeChildren();
  }
  
  private static boolean isLayoutOnlyAndCollapsable(ReactStylesDiffMap paramReactStylesDiffMap)
  {
    if (paramReactStylesDiffMap == null) {
      return true;
    }
    if ((paramReactStylesDiffMap.hasKey("collapsable")) && (!paramReactStylesDiffMap.getBoolean("collapsable", true))) {
      return false;
    }
    ReadableMapKeySetIterator localReadableMapKeySetIterator = mBackingMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey()) {
      if (!ViewProps.isLayoutOnly(mBackingMap, localReadableMapKeySetIterator.nextKey())) {
        return false;
      }
    }
    return true;
  }
  
  private void removeNodeFromParent(ReactShadowNode paramReactShadowNode, boolean paramBoolean)
  {
    int i;
    if (paramReactShadowNode.getNativeKind() != NativeKind.PARENT)
    {
      i = paramReactShadowNode.getChildCount() - 1;
      while (i >= 0)
      {
        removeNodeFromParent(paramReactShadowNode.getChildAt(i), paramBoolean);
        i -= 1;
      }
    }
    Object localObject = paramReactShadowNode.getNativeParent();
    if (localObject != null)
    {
      i = ((ReactShadowNode)localObject).indexOfNativeChild(paramReactShadowNode);
      ((ReactShadowNode)localObject).removeNativeChildAt(i);
      UIViewOperationQueue localUIViewOperationQueue = mUIViewOperationQueue;
      int j = ((ReactShadowNode)localObject).getReactTag();
      if (paramBoolean)
      {
        localObject = new int[1];
        localObject[0] = paramReactShadowNode.getReactTag();
        paramReactShadowNode = (ReactShadowNode)localObject;
      }
      else
      {
        paramReactShadowNode = null;
      }
      if (paramBoolean)
      {
        localObject = new int[1];
        localObject[0] = i;
      }
      else
      {
        localObject = null;
      }
      localUIViewOperationQueue.enqueueManageChildren(j, new int[] { i }, null, paramReactShadowNode, (int[])localObject);
    }
  }
  
  private void transitionLayoutOnlyViewToNativeView(ReactShadowNode paramReactShadowNode, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    ReactShadowNode localReactShadowNode = paramReactShadowNode.getParent();
    int j = 0;
    if (localReactShadowNode == null)
    {
      paramReactShadowNode.setIsLayoutOnly(false);
      return;
    }
    int i = localReactShadowNode.indexOf(paramReactShadowNode);
    localReactShadowNode.removeChildAt(i);
    removeNodeFromParent(paramReactShadowNode, false);
    paramReactShadowNode.setIsLayoutOnly(false);
    mUIViewOperationQueue.enqueueCreateView(paramReactShadowNode.getThemedContext(), paramReactShadowNode.getReactTag(), paramReactShadowNode.getViewClass(), paramReactStylesDiffMap);
    localReactShadowNode.addChildAt(paramReactShadowNode, i);
    addNodeToNode(localReactShadowNode, paramReactShadowNode, i);
    i = 0;
    while (i < paramReactShadowNode.getChildCount())
    {
      addNodeToNode(paramReactShadowNode, paramReactShadowNode.getChildAt(i), i);
      i += 1;
    }
    boolean bool;
    if (mTagsWithLayoutVisited.size() == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.assertCondition(bool);
    applyLayoutBase(paramReactShadowNode);
    i = j;
    while (i < paramReactShadowNode.getChildCount())
    {
      applyLayoutBase(paramReactShadowNode.getChildAt(i));
      i += 1;
    }
    mTagsWithLayoutVisited.clear();
  }
  
  private NodeIndexPair walkUpUntilNativeKindIsParent(ReactShadowNode paramReactShadowNode, int paramInt)
  {
    while (paramReactShadowNode.getNativeKind() != NativeKind.PARENT)
    {
      ReactShadowNode localReactShadowNode = paramReactShadowNode.getParent();
      if (localReactShadowNode == null) {
        return null;
      }
      int i;
      if (paramReactShadowNode.getNativeKind() == NativeKind.LEAF) {
        i = 1;
      } else {
        i = 0;
      }
      paramInt = paramInt + i + localReactShadowNode.getNativeOffsetForChild(paramReactShadowNode);
      paramReactShadowNode = localReactShadowNode;
    }
    return new NodeIndexPair(paramReactShadowNode, paramInt);
  }
  
  public void handleCreateView(ReactShadowNode paramReactShadowNode, ThemedReactContext paramThemedReactContext, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    boolean bool;
    if ((paramReactShadowNode.getViewClass().equals("RCTView")) && (isLayoutOnlyAndCollapsable(paramReactStylesDiffMap))) {
      bool = true;
    } else {
      bool = false;
    }
    paramReactShadowNode.setIsLayoutOnly(bool);
    if (paramReactShadowNode.getNativeKind() != NativeKind.NONE) {
      mUIViewOperationQueue.enqueueCreateView(paramThemedReactContext, paramReactShadowNode.getReactTag(), paramReactShadowNode.getViewClass(), paramReactStylesDiffMap);
    }
  }
  
  public void handleForceViewToBeNonLayoutOnly(ReactShadowNode paramReactShadowNode)
  {
    if (paramReactShadowNode.isLayoutOnly()) {
      transitionLayoutOnlyViewToNativeView(paramReactShadowNode, null);
    }
  }
  
  public void handleManageChildren(ReactShadowNode paramReactShadowNode, int[] paramArrayOfInt1, int[] paramArrayOfInt2, ViewAtIndex[] paramArrayOfViewAtIndex, int[] paramArrayOfInt3, int[] paramArrayOfInt4)
  {
    int k = 0;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      if (i >= paramArrayOfInt2.length) {
        break;
      }
      int m = paramArrayOfInt2[i];
      j = 0;
      while (j < paramArrayOfInt3.length)
      {
        if (paramArrayOfInt3[j] == m)
        {
          bool = true;
          break label62;
        }
        j += 1;
      }
      boolean bool = false;
      label62:
      removeNodeFromParent(mShadowNodeRegistry.getNode(m), bool);
      i += 1;
    }
    while (j < paramArrayOfViewAtIndex.length)
    {
      paramArrayOfInt1 = paramArrayOfViewAtIndex[j];
      addNodeToNode(paramReactShadowNode, mShadowNodeRegistry.getNode(mTag), mIndex);
      j += 1;
    }
  }
  
  public void handleSetChildren(ReactShadowNode paramReactShadowNode, ReadableArray paramReadableArray)
  {
    int i = 0;
    while (i < paramReadableArray.size())
    {
      addNodeToNode(paramReactShadowNode, mShadowNodeRegistry.getNode(paramReadableArray.getInt(i)), i);
      i += 1;
    }
  }
  
  public void handleUpdateLayout(ReactShadowNode paramReactShadowNode)
  {
    applyLayoutBase(paramReactShadowNode);
  }
  
  public void handleUpdateView(ReactShadowNode paramReactShadowNode, String paramString, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    int i;
    if ((paramReactShadowNode.isLayoutOnly()) && (!isLayoutOnlyAndCollapsable(paramReactStylesDiffMap))) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      transitionLayoutOnlyViewToNativeView(paramReactShadowNode, paramReactStylesDiffMap);
      return;
    }
    if (!paramReactShadowNode.isLayoutOnly()) {
      mUIViewOperationQueue.enqueueUpdateProperties(paramReactShadowNode.getReactTag(), paramString, paramReactStylesDiffMap);
    }
  }
  
  public void onBatchComplete()
  {
    mTagsWithLayoutVisited.clear();
  }
  
  private static class NodeIndexPair
  {
    public final int index;
    public final ReactShadowNode node;
    
    NodeIndexPair(ReactShadowNode paramReactShadowNode, int paramInt)
    {
      node = paramReactShadowNode;
      index = paramInt;
    }
  }
}
