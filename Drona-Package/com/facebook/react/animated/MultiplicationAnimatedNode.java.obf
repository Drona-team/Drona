package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

class MultiplicationAnimatedNode
  extends ValueAnimatedNode
{
  private final int[] mInputNodes;
  private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
  
  public MultiplicationAnimatedNode(ReadableMap paramReadableMap, NativeAnimatedNodesManager paramNativeAnimatedNodesManager)
  {
    mNativeAnimatedNodesManager = paramNativeAnimatedNodesManager;
    paramReadableMap = paramReadableMap.getArray("input");
    mInputNodes = new int[paramReadableMap.size()];
    int i = 0;
    while (i < mInputNodes.length)
    {
      mInputNodes[i] = paramReadableMap.getInt(i);
      i += 1;
    }
  }
  
  public void update()
  {
    mValue = 1.0D;
    int i = 0;
    while (i < mInputNodes.length)
    {
      AnimatedNode localAnimatedNode = mNativeAnimatedNodesManager.getNodeById(mInputNodes[i]);
      if ((localAnimatedNode != null) && ((localAnimatedNode instanceof ValueAnimatedNode)))
      {
        mValue *= ((ValueAnimatedNode)localAnimatedNode).getValue();
        i += 1;
      }
      else
      {
        throw new JSApplicationCausedNativeException("Illegal node ID set as an input for Animated.multiply node");
      }
    }
  }
}
