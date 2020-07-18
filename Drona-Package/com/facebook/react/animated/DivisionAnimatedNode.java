package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

class DivisionAnimatedNode
  extends ValueAnimatedNode
{
  private final int[] mInputNodes;
  private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
  
  public DivisionAnimatedNode(ReadableMap paramReadableMap, NativeAnimatedNodesManager paramNativeAnimatedNodesManager)
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
      AnimatedNode localAnimatedNode = mNativeAnimatedNodesManager.getNodeById(mInputNodes[i]);
      if ((localAnimatedNode != null) && ((localAnimatedNode instanceof ValueAnimatedNode)))
      {
        double d = ((ValueAnimatedNode)localAnimatedNode).getValue();
        if (i == 0)
        {
          mValue = d;
        }
        else
        {
          if (d == 0.0D) {
            break label83;
          }
          mValue /= d;
        }
        i += 1;
        continue;
        label83:
        throw new JSApplicationCausedNativeException("Detected a division by zero in Animated.divide node");
      }
      else
      {
        throw new JSApplicationCausedNativeException("Illegal node ID set as an input for Animated.divide node");
      }
    }
  }
}
