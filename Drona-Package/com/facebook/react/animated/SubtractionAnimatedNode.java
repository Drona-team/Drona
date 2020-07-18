package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

class SubtractionAnimatedNode
  extends ValueAnimatedNode
{
  private final int[] mInputNodes;
  private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
  
  public SubtractionAnimatedNode(ReadableMap paramReadableMap, NativeAnimatedNodesManager paramNativeAnimatedNodesManager)
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
    int i = 0;
    while (i < mInputNodes.length)
    {
      Object localObject = mNativeAnimatedNodesManager.getNodeById(mInputNodes[i]);
      if ((localObject != null) && ((localObject instanceof ValueAnimatedNode)))
      {
        localObject = (ValueAnimatedNode)localObject;
        double d = ((ValueAnimatedNode)localObject).getValue();
        if (i == 0) {
          mValue = d;
        } else {
          mValue -= ((ValueAnimatedNode)localObject).getValue();
        }
        i += 1;
      }
      else
      {
        throw new JSApplicationCausedNativeException("Illegal node ID set as an input for Animated.subtract node");
      }
    }
  }
}
