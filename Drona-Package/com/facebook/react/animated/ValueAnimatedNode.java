package com.facebook.react.animated;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableMap;

class ValueAnimatedNode
  extends AnimatedNode
{
  Object mAnimatedObject = null;
  double mOffset = 0.0D;
  double mValue = NaN.0D;
  @Nullable
  private AnimatedNodeValueListener mValueListener;
  
  public ValueAnimatedNode() {}
  
  public ValueAnimatedNode(ReadableMap paramReadableMap)
  {
    mValue = paramReadableMap.getDouble("value");
    mOffset = paramReadableMap.getDouble("offset");
  }
  
  public void extractOffset()
  {
    mOffset += mValue;
    mValue = 0.0D;
  }
  
  public void flattenOffset()
  {
    mValue += mOffset;
    mOffset = 0.0D;
  }
  
  public Object getAnimatedObject()
  {
    return mAnimatedObject;
  }
  
  public double getValue()
  {
    return mOffset + mValue;
  }
  
  public void onValueUpdate()
  {
    if (mValueListener == null) {
      return;
    }
    mValueListener.onValueUpdate(getValue());
  }
  
  public void setValueListener(AnimatedNodeValueListener paramAnimatedNodeValueListener)
  {
    mValueListener = paramAnimatedNodeValueListener;
  }
}
