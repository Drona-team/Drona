package com.facebook.react.animated;

import android.util.SparseArray;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIManagerModule.CustomEventNamesResolver;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcherListener;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;

class NativeAnimatedNodesManager
  implements EventDispatcherListener
{
  private final SparseArray<AnimationDriver> mActiveAnimations = new SparseArray();
  private int mAnimatedGraphBFSColor = 0;
  private final SparseArray<AnimatedNode> mAnimatedNodes = new SparseArray();
  private final UIManagerModule.CustomEventNamesResolver mCustomEventNamesResolver;
  private final Map<String, List<EventAnimationDriver>> mEventDrivers = new HashMap();
  private final List<AnimatedNode> mRunUpdateNodeList = new LinkedList();
  private final UIManagerModule mUIManagerModule;
  private final SparseArray<AnimatedNode> mUpdatedNodes = new SparseArray();
  
  public NativeAnimatedNodesManager(UIManagerModule paramUIManagerModule)
  {
    mUIManagerModule = paramUIManagerModule;
    paramUIManagerModule.getEventDispatcher().addListener(this);
    mCustomEventNamesResolver = paramUIManagerModule.getDirectEventNamesResolver();
  }
  
  private void handleEvent(Event paramEvent)
  {
    if (!mEventDrivers.isEmpty())
    {
      Object localObject1 = mCustomEventNamesResolver.resolveCustomEventName(paramEvent.getEventName());
      Object localObject2 = mEventDrivers;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramEvent.getViewTag());
      localStringBuilder.append((String)localObject1);
      localObject1 = (List)((Map)localObject2).get(localStringBuilder.toString());
      if (localObject1 != null)
      {
        localObject1 = ((List)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (EventAnimationDriver)((Iterator)localObject1).next();
          stopAnimationsForNode(mValueNode);
          paramEvent.dispatch((RCTEventEmitter)localObject2);
          mRunUpdateNodeList.add(mValueNode);
        }
        updateNodes(mRunUpdateNodeList);
        mRunUpdateNodeList.clear();
      }
    }
  }
  
  private void stopAnimationsForNode(AnimatedNode paramAnimatedNode)
  {
    int j;
    for (int i = 0; i < mActiveAnimations.size(); i = j + 1)
    {
      AnimationDriver localAnimationDriver = (AnimationDriver)mActiveAnimations.valueAt(i);
      j = i;
      if (paramAnimatedNode.equals(mAnimatedValue))
      {
        if (mEndCallback != null)
        {
          WritableMap localWritableMap = Arguments.createMap();
          localWritableMap.putBoolean("finished", false);
          mEndCallback.invoke(new Object[] { localWritableMap });
        }
        mActiveAnimations.removeAt(i);
        j = i - 1;
      }
    }
  }
  
  private void updateNodes(List paramList)
  {
    mAnimatedGraphBFSColor += 1;
    if (mAnimatedGraphBFSColor == 0) {
      mAnimatedGraphBFSColor += 1;
    }
    ArrayDeque localArrayDeque = new ArrayDeque();
    Object localObject = paramList.iterator();
    int i = 0;
    int j;
    AnimatedNode localAnimatedNode2;
    for (;;)
    {
      j = i;
      if (!((Iterator)localObject).hasNext()) {
        break;
      }
      localAnimatedNode2 = (AnimatedNode)((Iterator)localObject).next();
      if (mBFSColor != mAnimatedGraphBFSColor)
      {
        mBFSColor = mAnimatedGraphBFSColor;
        i += 1;
        localArrayDeque.add(localAnimatedNode2);
      }
    }
    int k;
    while (!localArrayDeque.isEmpty())
    {
      localObject = (AnimatedNode)localArrayDeque.poll();
      if (mChildren != null)
      {
        i = 0;
        while (i < mChildren.size())
        {
          localAnimatedNode2 = (AnimatedNode)mChildren.get(i);
          mActiveIncomingNodes += 1;
          k = j;
          if (mBFSColor != mAnimatedGraphBFSColor)
          {
            mBFSColor = mAnimatedGraphBFSColor;
            k = j + 1;
            localArrayDeque.add(localAnimatedNode2);
          }
          i += 1;
          j = k;
        }
      }
    }
    mAnimatedGraphBFSColor += 1;
    if (mAnimatedGraphBFSColor == 0) {
      mAnimatedGraphBFSColor += 1;
    }
    paramList = paramList.iterator();
    i = 0;
    for (;;)
    {
      k = i;
      if (!paramList.hasNext()) {
        break;
      }
      localObject = (AnimatedNode)paramList.next();
      if ((mActiveIncomingNodes == 0) && (mBFSColor != mAnimatedGraphBFSColor))
      {
        mBFSColor = mAnimatedGraphBFSColor;
        i += 1;
        localArrayDeque.add(localObject);
      }
    }
    while (!localArrayDeque.isEmpty())
    {
      paramList = (AnimatedNode)localArrayDeque.poll();
      paramList.update();
      if ((paramList instanceof PropsAnimatedNode))
      {
        localObject = (PropsAnimatedNode)paramList;
        try
        {
          ((PropsAnimatedNode)localObject).updateView();
        }
        catch (IllegalViewOperationException localIllegalViewOperationException)
        {
          FLog.e("ReactNative", "Native animation workaround, frame lost as result of race condition", localIllegalViewOperationException);
        }
      }
      if ((paramList instanceof ValueAnimatedNode)) {
        ((ValueAnimatedNode)paramList).onValueUpdate();
      }
      if (mChildren != null)
      {
        i = 0;
        while (i < mChildren.size())
        {
          AnimatedNode localAnimatedNode1 = (AnimatedNode)mChildren.get(i);
          mActiveIncomingNodes -= 1;
          int m = k;
          if (mBFSColor != mAnimatedGraphBFSColor)
          {
            m = k;
            if (mActiveIncomingNodes == 0)
            {
              mBFSColor = mAnimatedGraphBFSColor;
              m = k + 1;
              localArrayDeque.add(localAnimatedNode1);
            }
          }
          i += 1;
          k = m;
        }
      }
    }
    if (j == k) {
      return;
    }
    paramList = new StringBuilder();
    paramList.append("Looks like animated nodes graph has cycles, there are ");
    paramList.append(j);
    paramList.append(" but toposort visited only ");
    paramList.append(k);
    throw new IllegalStateException(paramList.toString());
  }
  
  public void addAnimatedEventToView(int paramInt, String paramString, ReadableMap paramReadableMap)
  {
    int i = paramReadableMap.getInt("animatedValueTag");
    Object localObject = (AnimatedNode)mAnimatedNodes.get(i);
    if (localObject != null)
    {
      if ((localObject instanceof ValueAnimatedNode))
      {
        paramReadableMap = paramReadableMap.getArray("nativeEventPath");
        ArrayList localArrayList = new ArrayList(paramReadableMap.size());
        i = 0;
        while (i < paramReadableMap.size())
        {
          localArrayList.add(paramReadableMap.getString(i));
          i += 1;
        }
        paramReadableMap = new EventAnimationDriver(localArrayList, (ValueAnimatedNode)localObject);
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(paramInt);
        ((StringBuilder)localObject).append(paramString);
        paramString = ((StringBuilder)localObject).toString();
        if (mEventDrivers.containsKey(paramString))
        {
          ((List)mEventDrivers.get(paramString)).add(paramReadableMap);
          return;
        }
        localObject = new ArrayList(1);
        ((List)localObject).add(paramReadableMap);
        mEventDrivers.put(paramString, localObject);
        return;
      }
      paramString = new StringBuilder();
      paramString.append("Animated node connected to event should beof type ");
      paramString.append(ValueAnimatedNode.class.getName());
      throw new JSApplicationIllegalArgumentException(paramString.toString());
    }
    paramString = new StringBuilder();
    paramString.append("Animated node with tag ");
    paramString.append(i);
    paramString.append(" does not exists");
    throw new JSApplicationIllegalArgumentException(paramString.toString());
  }
  
  public void connectAnimatedNodeToView(int paramInt1, int paramInt2)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt1);
    if (localObject != null)
    {
      if ((localObject instanceof PropsAnimatedNode))
      {
        ((PropsAnimatedNode)localObject).connectToView(paramInt2);
        mUpdatedNodes.put(paramInt1, localObject);
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Animated node connected to view should beof type ");
      ((StringBuilder)localObject).append(PropsAnimatedNode.class.getName());
      throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append(" does not exists");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void connectAnimatedNodes(int paramInt1, int paramInt2)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt1);
    if (localObject != null)
    {
      AnimatedNode localAnimatedNode = (AnimatedNode)mAnimatedNodes.get(paramInt2);
      if (localAnimatedNode != null)
      {
        ((AnimatedNode)localObject).addChild(localAnimatedNode);
        mUpdatedNodes.put(paramInt2, localAnimatedNode);
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Animated node with tag ");
      ((StringBuilder)localObject).append(paramInt2);
      ((StringBuilder)localObject).append(" does not exists");
      throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append(" does not exists");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void createAnimatedNode(int paramInt, ReadableMap paramReadableMap)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void disconnectAnimatedNodeFromView(int paramInt1, int paramInt2)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt1);
    if (localObject != null)
    {
      if ((localObject instanceof PropsAnimatedNode))
      {
        ((PropsAnimatedNode)localObject).disconnectFromView(paramInt2);
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Animated node connected to view should beof type ");
      ((StringBuilder)localObject).append(PropsAnimatedNode.class.getName());
      throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append(" does not exists");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void disconnectAnimatedNodes(int paramInt1, int paramInt2)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt1);
    if (localObject != null)
    {
      AnimatedNode localAnimatedNode = (AnimatedNode)mAnimatedNodes.get(paramInt2);
      if (localAnimatedNode != null)
      {
        ((AnimatedNode)localObject).removeChild(localAnimatedNode);
        mUpdatedNodes.put(paramInt2, localAnimatedNode);
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Animated node with tag ");
      ((StringBuilder)localObject).append(paramInt2);
      ((StringBuilder)localObject).append(" does not exists");
      throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append(" does not exists");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void dropAnimatedNode(int paramInt)
  {
    mAnimatedNodes.remove(paramInt);
    mUpdatedNodes.remove(paramInt);
  }
  
  public void extractAnimatedNodeOffset(int paramInt)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt);
    if ((localObject != null) && ((localObject instanceof ValueAnimatedNode)))
    {
      ((ValueAnimatedNode)localObject).extractOffset();
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" does not exists or is not a 'value' node");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void flattenAnimatedNodeOffset(int paramInt)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt);
    if ((localObject != null) && ((localObject instanceof ValueAnimatedNode)))
    {
      ((ValueAnimatedNode)localObject).flattenOffset();
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" does not exists or is not a 'value' node");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  AnimatedNode getNodeById(int paramInt)
  {
    return (AnimatedNode)mAnimatedNodes.get(paramInt);
  }
  
  public boolean hasActiveAnimations()
  {
    return (mActiveAnimations.size() > 0) || (mUpdatedNodes.size() > 0);
  }
  
  public void onEventDispatch(final Event paramEvent)
  {
    if (UiThreadUtil.isOnUiThread())
    {
      handleEvent(paramEvent);
      return;
    }
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        NativeAnimatedNodesManager.this.handleEvent(paramEvent);
      }
    });
  }
  
  public void removeAnimatedEventFromView(int paramInt1, String paramString, int paramInt2)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append(paramString);
    localObject = ((StringBuilder)localObject).toString();
    if (mEventDrivers.containsKey(localObject))
    {
      localObject = (List)mEventDrivers.get(localObject);
      if (((List)localObject).size() == 1)
      {
        localObject = mEventDrivers;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramInt1);
        localStringBuilder.append(paramString);
        ((Map)localObject).remove(localStringBuilder.toString());
        return;
      }
      paramString = ((List)localObject).listIterator();
      while (paramString.hasNext()) {
        if (nextmValueNode.mTag == paramInt2) {
          paramString.remove();
        }
      }
    }
  }
  
  public void restoreDefaultValues(int paramInt1, int paramInt2)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt1);
    if (localObject == null) {
      return;
    }
    if ((localObject instanceof PropsAnimatedNode))
    {
      ((PropsAnimatedNode)localObject).restoreDefaultValues();
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node connected to view should beof type ");
    ((StringBuilder)localObject).append(PropsAnimatedNode.class.getName());
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void runUpdates(long paramLong)
  {
    UiThreadUtil.assertOnUiThread();
    int i = 0;
    Object localObject1;
    while (i < mUpdatedNodes.size())
    {
      localObject1 = (AnimatedNode)mUpdatedNodes.valueAt(i);
      mRunUpdateNodeList.add(localObject1);
      i += 1;
    }
    mUpdatedNodes.clear();
    i = 0;
    int j = 0;
    Object localObject2;
    while (i < mActiveAnimations.size())
    {
      localObject1 = (AnimationDriver)mActiveAnimations.valueAt(i);
      ((AnimationDriver)localObject1).runAnimationStep(paramLong);
      localObject2 = mAnimatedValue;
      mRunUpdateNodeList.add(localObject2);
      if (mHasFinished) {
        j = 1;
      }
      i += 1;
    }
    updateNodes(mRunUpdateNodeList);
    mRunUpdateNodeList.clear();
    if (j != 0)
    {
      i = mActiveAnimations.size() - 1;
      while (i >= 0)
      {
        localObject1 = (AnimationDriver)mActiveAnimations.valueAt(i);
        if (mHasFinished)
        {
          if (mEndCallback != null)
          {
            localObject2 = Arguments.createMap();
            ((WritableMap)localObject2).putBoolean("finished", true);
            mEndCallback.invoke(new Object[] { localObject2 });
          }
          mActiveAnimations.removeAt(i);
        }
        i -= 1;
      }
    }
  }
  
  public void setAnimatedNodeOffset(int paramInt, double paramDouble)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt);
    if ((localObject != null) && ((localObject instanceof ValueAnimatedNode)))
    {
      mOffset = paramDouble;
      mUpdatedNodes.put(paramInt, localObject);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" does not exists or is not a 'value' node");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void setAnimatedNodeValue(int paramInt, double paramDouble)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt);
    if ((localObject != null) && ((localObject instanceof ValueAnimatedNode)))
    {
      stopAnimationsForNode((AnimatedNode)localObject);
      mValue = paramDouble;
      mUpdatedNodes.put(paramInt, localObject);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" does not exists or is not a 'value' node");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void startAnimatingNode(int paramInt1, int paramInt2, ReadableMap paramReadableMap, Callback paramCallback)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a9 = a8\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void startListeningToAnimatedNodeValue(int paramInt, AnimatedNodeValueListener paramAnimatedNodeValueListener)
  {
    AnimatedNode localAnimatedNode = (AnimatedNode)mAnimatedNodes.get(paramInt);
    if ((localAnimatedNode != null) && ((localAnimatedNode instanceof ValueAnimatedNode)))
    {
      ((ValueAnimatedNode)localAnimatedNode).setValueListener(paramAnimatedNodeValueListener);
      return;
    }
    paramAnimatedNodeValueListener = new StringBuilder();
    paramAnimatedNodeValueListener.append("Animated node with tag ");
    paramAnimatedNodeValueListener.append(paramInt);
    paramAnimatedNodeValueListener.append(" does not exists or is not a 'value' node");
    throw new JSApplicationIllegalArgumentException(paramAnimatedNodeValueListener.toString());
  }
  
  public void stopAnimation(int paramInt)
  {
    int i = 0;
    while (i < mActiveAnimations.size())
    {
      AnimationDriver localAnimationDriver = (AnimationDriver)mActiveAnimations.valueAt(i);
      if (animations == paramInt)
      {
        if (mEndCallback != null)
        {
          WritableMap localWritableMap = Arguments.createMap();
          localWritableMap.putBoolean("finished", false);
          mEndCallback.invoke(new Object[] { localWritableMap });
        }
        mActiveAnimations.removeAt(i);
        return;
      }
      i += 1;
    }
  }
  
  public void stopListeningToAnimatedNodeValue(int paramInt)
  {
    Object localObject = (AnimatedNode)mAnimatedNodes.get(paramInt);
    if ((localObject != null) && ((localObject instanceof ValueAnimatedNode)))
    {
      ((ValueAnimatedNode)localObject).setValueListener(null);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Animated node with tag ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" does not exists or is not a 'value' node");
    throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString());
  }
}
