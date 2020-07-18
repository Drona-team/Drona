package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.UIManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class PropsAnimatedNode
  extends AnimatedNode
{
  private int mConnectedViewTag = -1;
  private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
  private final JavaOnlyMap mPropMap;
  private final Map<String, Integer> mPropNodeMapping;
  private final UIManager mUIManager;
  
  PropsAnimatedNode(ReadableMap paramReadableMap, NativeAnimatedNodesManager paramNativeAnimatedNodesManager, UIManager paramUIManager)
  {
    paramReadableMap = paramReadableMap.getMap("props");
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    mPropNodeMapping = new HashMap();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str = localReadableMapKeySetIterator.nextKey();
      int i = paramReadableMap.getInt(str);
      mPropNodeMapping.put(str, Integer.valueOf(i));
    }
    mPropMap = new JavaOnlyMap();
    mNativeAnimatedNodesManager = paramNativeAnimatedNodesManager;
    mUIManager = paramUIManager;
  }
  
  public void connectToView(int paramInt)
  {
    if (mConnectedViewTag == -1)
    {
      mConnectedViewTag = paramInt;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Animated node ");
    localStringBuilder.append(mTag);
    localStringBuilder.append(" is already attached to a view");
    throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
  }
  
  public void disconnectFromView(int paramInt)
  {
    if (mConnectedViewTag == paramInt)
    {
      mConnectedViewTag = -1;
      return;
    }
    throw new JSApplicationIllegalArgumentException("Attempting to disconnect view that has not been connected with the given animated node");
  }
  
  public void restoreDefaultValues()
  {
    ReadableMapKeySetIterator localReadableMapKeySetIterator = mPropMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey()) {
      mPropMap.putNull(localReadableMapKeySetIterator.nextKey());
    }
    mUIManager.synchronouslyUpdateViewOnUIThread(mConnectedViewTag, mPropMap);
  }
  
  public final void updateView()
  {
    if (mConnectedViewTag == -1) {
      return;
    }
    Object localObject1 = mPropNodeMapping.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject1).next();
      Object localObject2 = mNativeAnimatedNodesManager.getNodeById(((Integer)localEntry.getValue()).intValue());
      if (localObject2 != null)
      {
        if ((localObject2 instanceof StyleAnimatedNode))
        {
          ((StyleAnimatedNode)localObject2).collectViewUpdates(mPropMap);
        }
        else if ((localObject2 instanceof ValueAnimatedNode))
        {
          localObject2 = (ValueAnimatedNode)localObject2;
          Object localObject3 = ((ValueAnimatedNode)localObject2).getAnimatedObject();
          if ((localObject3 instanceof String)) {
            mPropMap.putString((String)localEntry.getKey(), (String)localObject3);
          } else {
            mPropMap.putDouble((String)localEntry.getKey(), ((ValueAnimatedNode)localObject2).getValue());
          }
        }
        else
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Unsupported type of node used in property node ");
          ((StringBuilder)localObject1).append(localObject2.getClass());
          throw new IllegalArgumentException(((StringBuilder)localObject1).toString());
        }
      }
      else {
        throw new IllegalArgumentException("Mapped property node does not exists");
      }
    }
    mUIManager.synchronouslyUpdateViewOnUIThread(mConnectedViewTag, mPropMap);
  }
}
