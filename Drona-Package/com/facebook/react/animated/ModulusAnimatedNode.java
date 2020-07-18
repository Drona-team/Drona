package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableMap;

class ModulusAnimatedNode
  extends ValueAnimatedNode
{
  private final int mInputNode;
  private final double mModulus;
  private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
  
  public ModulusAnimatedNode(ReadableMap paramReadableMap, NativeAnimatedNodesManager paramNativeAnimatedNodesManager)
  {
    mNativeAnimatedNodesManager = paramNativeAnimatedNodesManager;
    mInputNode = paramReadableMap.getInt("input");
    mModulus = paramReadableMap.getDouble("modulus");
  }
  
  public void update()
  {
    AnimatedNode localAnimatedNode = mNativeAnimatedNodesManager.getNodeById(mInputNode);
    if ((localAnimatedNode != null) && ((localAnimatedNode instanceof ValueAnimatedNode)))
    {
      mValue = ((((ValueAnimatedNode)localAnimatedNode).getValue() % mModulus + mModulus) % mModulus);
      return;
    }
    throw new JSApplicationCausedNativeException("Illegal node ID set as an input for Animated.modulus node");
  }
}