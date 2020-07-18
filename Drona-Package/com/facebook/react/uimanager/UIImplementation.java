package com.facebook.react.uimanager;

import android.os.SystemClock;
import android.view.View;
import android.view.View.MeasureSpec;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import com.facebook.yoga.YogaDirection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UIImplementation
{
  protected final EventDispatcher mEventDispatcher;
  private long mLastCalculateLayoutTime = 0L;
  @Nullable
  protected LayoutUpdateListener mLayoutUpdateListener;
  private final int[] mMeasureBuffer = new int[4];
  private final NativeViewHierarchyOptimizer mNativeViewHierarchyOptimizer;
  private final UIViewOperationQueue mOperationsQueue;
  protected final ReactApplicationContext mReactContext;
  protected final ShadowNodeRegistry mShadowNodeRegistry = new ShadowNodeRegistry();
  private final ViewManagerRegistry mViewManagers;
  protected Object uiImplementationThreadLock = new Object();
  
  public UIImplementation(ReactApplicationContext paramReactApplicationContext, UIManagerModule.ViewManagerResolver paramViewManagerResolver, EventDispatcher paramEventDispatcher, int paramInt)
  {
    this(paramReactApplicationContext, new ViewManagerRegistry(paramViewManagerResolver), paramEventDispatcher, paramInt);
  }
  
  protected UIImplementation(ReactApplicationContext paramReactApplicationContext, ViewManagerRegistry paramViewManagerRegistry, UIViewOperationQueue paramUIViewOperationQueue, EventDispatcher paramEventDispatcher)
  {
    mReactContext = paramReactApplicationContext;
    mViewManagers = paramViewManagerRegistry;
    mOperationsQueue = paramUIViewOperationQueue;
    mNativeViewHierarchyOptimizer = new NativeViewHierarchyOptimizer(mOperationsQueue, mShadowNodeRegistry);
    mEventDispatcher = paramEventDispatcher;
  }
  
  UIImplementation(ReactApplicationContext paramReactApplicationContext, ViewManagerRegistry paramViewManagerRegistry, EventDispatcher paramEventDispatcher, int paramInt)
  {
    this(paramReactApplicationContext, paramViewManagerRegistry, new UIViewOperationQueue(paramReactApplicationContext, new NativeViewHierarchyManager(paramViewManagerRegistry), paramInt), paramEventDispatcher);
  }
  
  public UIImplementation(ReactApplicationContext paramReactApplicationContext, List paramList, EventDispatcher paramEventDispatcher, int paramInt)
  {
    this(paramReactApplicationContext, new ViewManagerRegistry(paramList), paramEventDispatcher, paramInt);
  }
  
  private void assertNodeDoesNotNeedCustomLayoutForChildren(ReactShadowNode paramReactShadowNode)
  {
    Object localObject = (ViewManager)Assertions.assertNotNull(mViewManagers.loadClass(paramReactShadowNode.getViewClass()));
    if ((localObject instanceof IViewManagerWithChildren))
    {
      localObject = (IViewManagerWithChildren)localObject;
      if (localObject != null)
      {
        if (!((IViewManagerWithChildren)localObject).needsCustomLayoutForChildren()) {
          return;
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Trying to measure a view using measureLayout/measureLayoutRelativeToParent relative to an ancestor that requires custom layout for it's children (");
        ((StringBuilder)localObject).append(paramReactShadowNode.getViewClass());
        ((StringBuilder)localObject).append("). Use measure instead.");
        throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
      }
    }
    else
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Trying to use view ");
      ((StringBuilder)localObject).append(paramReactShadowNode.getViewClass());
      ((StringBuilder)localObject).append(" as a parent, but its Manager doesn't extends ViewGroupManager");
      throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
    }
  }
  
  private void assertViewExists(int paramInt, String paramString)
  {
    if (mShadowNodeRegistry.getNode(paramInt) != null) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unable to execute operation ");
    localStringBuilder.append(paramString);
    localStringBuilder.append(" on view with tag: ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(", since the view does not exists");
    throw new IllegalViewOperationException(localStringBuilder.toString());
  }
  
  private void dispatchViewUpdatesIfNeeded()
  {
    if (mOperationsQueue.isEmpty()) {
      dispatchViewUpdates(-1);
    }
  }
  
  private void measureLayout(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    ReactShadowNode localReactShadowNode2 = mShadowNodeRegistry.getNode(paramInt1);
    ReactShadowNode localReactShadowNode3 = mShadowNodeRegistry.getNode(paramInt2);
    if ((localReactShadowNode2 != null) && (localReactShadowNode3 != null))
    {
      if (localReactShadowNode2 != localReactShadowNode3)
      {
        ReactShadowNode localReactShadowNode1 = localReactShadowNode2.getParent();
        while (localReactShadowNode1 != localReactShadowNode3) {
          if (localReactShadowNode1 != null)
          {
            localReactShadowNode1 = localReactShadowNode1.getParent();
          }
          else
          {
            paramArrayOfInt = new StringBuilder();
            paramArrayOfInt.append("Tag ");
            paramArrayOfInt.append(paramInt2);
            paramArrayOfInt.append(" is not an ancestor of tag ");
            paramArrayOfInt.append(paramInt1);
            throw new IllegalViewOperationException(paramArrayOfInt.toString());
          }
        }
      }
      measureLayoutRelativeToVerifiedAncestor(localReactShadowNode2, localReactShadowNode3, paramArrayOfInt);
      return;
    }
    paramArrayOfInt = new StringBuilder();
    paramArrayOfInt.append("Tag ");
    if (localReactShadowNode2 != null) {
      paramInt1 = paramInt2;
    }
    paramArrayOfInt.append(paramInt1);
    paramArrayOfInt.append(" does not exist");
    throw new IllegalViewOperationException(paramArrayOfInt.toString());
  }
  
  private void measureLayoutRelativeToParent(int paramInt, int[] paramArrayOfInt)
  {
    ReactShadowNode localReactShadowNode1 = mShadowNodeRegistry.getNode(paramInt);
    if (localReactShadowNode1 != null)
    {
      ReactShadowNode localReactShadowNode2 = localReactShadowNode1.getParent();
      if (localReactShadowNode2 != null)
      {
        measureLayoutRelativeToVerifiedAncestor(localReactShadowNode1, localReactShadowNode2, paramArrayOfInt);
        return;
      }
      paramArrayOfInt = new StringBuilder();
      paramArrayOfInt.append("View with tag ");
      paramArrayOfInt.append(paramInt);
      paramArrayOfInt.append(" doesn't have a parent!");
      throw new IllegalViewOperationException(paramArrayOfInt.toString());
    }
    paramArrayOfInt = new StringBuilder();
    paramArrayOfInt.append("No native view for tag ");
    paramArrayOfInt.append(paramInt);
    paramArrayOfInt.append(" exists!");
    throw new IllegalViewOperationException(paramArrayOfInt.toString());
  }
  
  private void measureLayoutRelativeToVerifiedAncestor(ReactShadowNode paramReactShadowNode1, ReactShadowNode paramReactShadowNode2, int[] paramArrayOfInt)
  {
    int j;
    int i;
    if (paramReactShadowNode1 != paramReactShadowNode2)
    {
      j = Math.round(paramReactShadowNode1.getLayoutX());
      i = Math.round(paramReactShadowNode1.getLayoutY());
      for (ReactShadowNode localReactShadowNode = paramReactShadowNode1.getParent(); localReactShadowNode != paramReactShadowNode2; localReactShadowNode = localReactShadowNode.getParent())
      {
        Assertions.assertNotNull(localReactShadowNode);
        assertNodeDoesNotNeedCustomLayoutForChildren(localReactShadowNode);
        j += Math.round(localReactShadowNode.getLayoutX());
        i += Math.round(localReactShadowNode.getLayoutY());
      }
      assertNodeDoesNotNeedCustomLayoutForChildren(paramReactShadowNode2);
    }
    else
    {
      j = 0;
      i = 0;
    }
    paramArrayOfInt[0] = j;
    paramArrayOfInt[1] = i;
    paramArrayOfInt[2] = paramReactShadowNode1.getScreenWidth();
    paramArrayOfInt[3] = paramReactShadowNode1.getScreenHeight();
  }
  
  private void notifyOnBeforeLayoutRecursive(ReactShadowNode paramReactShadowNode)
  {
    if (!paramReactShadowNode.hasUpdates()) {
      return;
    }
    int i = 0;
    while (i < paramReactShadowNode.getChildCount())
    {
      notifyOnBeforeLayoutRecursive(paramReactShadowNode.getChildAt(i));
      i += 1;
    }
    paramReactShadowNode.onBeforeLayout(mNativeViewHierarchyOptimizer);
  }
  
  private void removeShadowNodeRecursive(ReactShadowNode paramReactShadowNode)
  {
    NativeViewHierarchyOptimizer.handleRemoveNode(paramReactShadowNode);
    mShadowNodeRegistry.removeNode(paramReactShadowNode.getReactTag());
    int i = paramReactShadowNode.getChildCount() - 1;
    while (i >= 0)
    {
      removeShadowNodeRecursive(paramReactShadowNode.getChildAt(i));
      i -= 1;
    }
    paramReactShadowNode.removeAndDisposeAllChildren();
  }
  
  public void addUIBlock(UIBlock paramUIBlock)
  {
    mOperationsQueue.enqueueUIBlock(paramUIBlock);
  }
  
  protected void applyUpdatesRecursive(ReactShadowNode paramReactShadowNode, float paramFloat1, float paramFloat2)
  {
    if (!paramReactShadowNode.hasUpdates()) {
      return;
    }
    Object localObject = paramReactShadowNode.calculateLayoutOnChildren();
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        applyUpdatesRecursive((ReactShadowNode)((Iterator)localObject).next(), paramReactShadowNode.getLayoutX() + paramFloat1, paramReactShadowNode.getLayoutY() + paramFloat2);
      }
    }
    int i = paramReactShadowNode.getReactTag();
    if ((!mShadowNodeRegistry.isRootNode(i)) && (paramReactShadowNode.dispatchUpdates(paramFloat1, paramFloat2, mOperationsQueue, mNativeViewHierarchyOptimizer)) && (paramReactShadowNode.shouldNotifyOnLayout())) {
      mEventDispatcher.dispatchEvent(OnLayoutEvent.obtain(i, paramReactShadowNode.getScreenX(), paramReactShadowNode.getScreenY(), paramReactShadowNode.getScreenWidth(), paramReactShadowNode.getScreenHeight()));
    }
    paramReactShadowNode.markUpdateSeen();
  }
  
  protected void calculateRootLayout(ReactShadowNode paramReactShadowNode)
  {
    SystraceMessage.beginSection(0L, "cssRoot.calculateLayout").getStream("rootTag", paramReactShadowNode.getReactTag()).flush();
    long l = SystemClock.uptimeMillis();
    try
    {
      int j = paramReactShadowNode.getWidthMeasureSpec().intValue();
      int i = paramReactShadowNode.getHeightMeasureSpec().intValue();
      int k = View.MeasureSpec.getMode(j);
      float f2 = NaN.0F;
      float f1;
      if (k == 0) {
        f1 = NaN.0F;
      } else {
        f1 = View.MeasureSpec.getSize(j);
      }
      j = View.MeasureSpec.getMode(i);
      if (j != 0) {
        f2 = View.MeasureSpec.getSize(i);
      }
      paramReactShadowNode.calculateLayout(f1, f2);
      Systrace.endSection(0L);
      mLastCalculateLayoutTime = (SystemClock.uptimeMillis() - l);
      return;
    }
    catch (Throwable paramReactShadowNode)
    {
      Systrace.endSection(0L);
      mLastCalculateLayoutTime = (SystemClock.uptimeMillis() - l);
      throw paramReactShadowNode;
    }
  }
  
  public void clearJSResponder()
  {
    mOperationsQueue.enqueueClearJSResponder();
  }
  
  public void configureNextLayoutAnimation(ReadableMap paramReadableMap, Callback paramCallback)
  {
    mOperationsQueue.enqueueConfigureLayoutAnimation(paramReadableMap, paramCallback);
  }
  
  protected ReactShadowNode createRootShadowNode()
  {
    ReactShadowNodeImpl localReactShadowNodeImpl = new ReactShadowNodeImpl();
    if (I18nUtil.getInstance().isRTL(mReactContext)) {
      localReactShadowNodeImpl.setLayoutDirection(YogaDirection.RIGHT);
    }
    localReactShadowNodeImpl.setViewClassName("Root");
    return localReactShadowNodeImpl;
  }
  
  protected ReactShadowNode createShadowNode(String paramString)
  {
    return mViewManagers.loadClass(paramString).createShadowNodeInstance(mReactContext);
  }
  
  public void createView(int paramInt1, String paramString, int paramInt2, ReadableMap paramReadableMap)
  {
    Object localObject = uiImplementationThreadLock;
    try
    {
      ReactShadowNode localReactShadowNode1 = createShadowNode(paramString);
      ReactShadowNode localReactShadowNode2 = mShadowNodeRegistry.getNode(paramInt2);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Root node with tag ");
      localStringBuilder.append(paramInt2);
      localStringBuilder.append(" doesn't exist");
      Assertions.assertNotNull(localReactShadowNode2, localStringBuilder.toString());
      localReactShadowNode1.setReactTag(paramInt1);
      localReactShadowNode1.setViewClassName(paramString);
      localReactShadowNode1.setRootTag(localReactShadowNode2.getReactTag());
      localReactShadowNode1.setThemedContext(localReactShadowNode2.getThemedContext());
      mShadowNodeRegistry.addNode(localReactShadowNode1);
      paramString = null;
      if (paramReadableMap != null)
      {
        paramString = new ReactStylesDiffMap(paramReadableMap);
        localReactShadowNode1.updateProperties(paramString);
      }
      handleCreateView(localReactShadowNode1, paramInt2, paramString);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void dismissPopupMenu()
  {
    mOperationsQueue.enqueueDismissPopupMenu();
  }
  
  public void dispatchViewManagerCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    assertViewExists(paramInt1, "dispatchViewManagerCommand");
    mOperationsQueue.enqueueDispatchCommand(paramInt1, paramInt2, paramReadableArray);
  }
  
  public void dispatchViewManagerCommand(int paramInt, String paramString, ReadableArray paramReadableArray)
  {
    assertViewExists(paramInt, "dispatchViewManagerCommand");
    mOperationsQueue.enqueueDispatchCommand(paramInt, paramString, paramReadableArray);
  }
  
  public void dispatchViewUpdates(int paramInt)
  {
    SystraceMessage.beginSection(0L, "UIImplementation.dispatchViewUpdates").getStream("batchId", paramInt).flush();
    long l = SystemClock.uptimeMillis();
    try
    {
      updateViewHierarchy();
      mNativeViewHierarchyOptimizer.onBatchComplete();
      mOperationsQueue.dispatchViewUpdates(paramInt, l, mLastCalculateLayoutTime);
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable localThrowable)
    {
      Systrace.endSection(0L);
      throw localThrowable;
    }
  }
  
  public void findSubviewIn(int paramInt, float paramFloat1, float paramFloat2, Callback paramCallback)
  {
    mOperationsQueue.enqueueFindTargetForTouch(paramInt, paramFloat1, paramFloat2, paramCallback);
  }
  
  public Map getProfiledBatchPerfCounters()
  {
    return mOperationsQueue.getProfiledBatchPerfCounters();
  }
  
  UIViewOperationQueue getUIViewOperationQueue()
  {
    return mOperationsQueue;
  }
  
  protected void handleCreateView(ReactShadowNode paramReactShadowNode, int paramInt, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    if (!paramReactShadowNode.isVirtual()) {
      mNativeViewHierarchyOptimizer.handleCreateView(paramReactShadowNode, paramReactShadowNode.getThemedContext(), paramReactStylesDiffMap);
    }
  }
  
  protected void handleUpdateView(ReactShadowNode paramReactShadowNode, String paramString, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    if (!paramReactShadowNode.isVirtual()) {
      mNativeViewHierarchyOptimizer.handleUpdateView(paramReactShadowNode, paramString, paramReactStylesDiffMap);
    }
  }
  
  public void manageChildren(int paramInt, ReadableArray paramReadableArray1, ReadableArray paramReadableArray2, ReadableArray paramReadableArray3, ReadableArray paramReadableArray4, ReadableArray paramReadableArray5)
  {
    Object localObject = uiImplementationThreadLock;
    try
    {
      localReactShadowNode = mShadowNodeRegistry.getNode(paramInt);
      if (paramReadableArray1 == null) {
        i = 0;
      } else {
        i = paramReadableArray1.size();
      }
    }
    catch (Throwable paramReadableArray1)
    {
      ReactShadowNode localReactShadowNode;
      int i;
      int k;
      int j;
      ViewAtIndex[] arrayOfViewAtIndex;
      int[] arrayOfInt1;
      int[] arrayOfInt2;
      int[] arrayOfInt3;
      for (;;) {}
    }
    k = paramReadableArray3.size();
    break label686;
    label52:
    j = paramReadableArray5.size();
    label61:
    if ((i != 0) && ((paramReadableArray2 == null) || (i != paramReadableArray2.size()))) {
      throw new IllegalViewOperationException("Size of moveFrom != size of moveTo!");
    }
    if ((k != 0) && ((paramReadableArray4 == null) || (k != paramReadableArray4.size()))) {
      throw new IllegalViewOperationException("Size of addChildTags != size of addAtIndices!");
    }
    arrayOfViewAtIndex = new ViewAtIndex[i + k];
    arrayOfInt1 = new int[i + j];
    arrayOfInt2 = new int[arrayOfInt1.length];
    arrayOfInt3 = new int[j];
    for (;;)
    {
      int[] arrayOfInt4;
      try
      {
        arrayOfInt4 = new int[j];
        int m;
        int n;
        int i1;
        if (i > 0)
        {
          Assertions.assertNotNull(paramReadableArray1);
          Assertions.assertNotNull(paramReadableArray2);
          m = 0;
          if (m < i)
          {
            n = paramReadableArray1.getInt(m);
            i1 = localReactShadowNode.getChildAt(n).getReactTag();
            arrayOfViewAtIndex[m] = new ViewAtIndex(i1, paramReadableArray2.getInt(m));
            arrayOfInt1[m] = n;
            arrayOfInt2[m] = i1;
            m += 1;
            continue;
          }
        }
        if (k > 0)
        {
          Assertions.assertNotNull(paramReadableArray3);
          Assertions.assertNotNull(paramReadableArray4);
          m = 0;
          if (m < k)
          {
            arrayOfViewAtIndex[(i + m)] = new ViewAtIndex(paramReadableArray3.getInt(m), paramReadableArray4.getInt(m));
            m += 1;
            continue;
          }
        }
        if (j > 0)
        {
          Assertions.assertNotNull(paramReadableArray5);
          k = 0;
          if (k < j)
          {
            m = paramReadableArray5.getInt(k);
            n = localReactShadowNode.getChildAt(m).getReactTag();
            i1 = i + k;
            arrayOfInt1[i1] = m;
            arrayOfInt2[i1] = n;
            arrayOfInt3[k] = n;
            arrayOfInt4[k] = m;
            k += 1;
            continue;
          }
        }
        Arrays.sort(arrayOfViewAtIndex, ViewAtIndex.COMPARATOR);
        Arrays.sort(arrayOfInt1);
        i = arrayOfInt1.length - 1;
        j = -1;
        if (i < 0) {
          break label697;
        }
        if (arrayOfInt1[i] != j)
        {
          localReactShadowNode.removeChildAt(arrayOfInt1[i]);
          j = arrayOfInt1[i];
          i -= 1;
          continue;
        }
        paramReadableArray1 = new StringBuilder();
        paramReadableArray1.append("Repeated indices in Removal list for view tag: ");
        paramReadableArray1.append(paramInt);
        throw new IllegalViewOperationException(paramReadableArray1.toString());
      }
      catch (Throwable paramReadableArray1) {}
      if (paramInt < arrayOfViewAtIndex.length) {
        paramReadableArray1 = arrayOfViewAtIndex[paramInt];
      }
      try
      {
        paramReadableArray2 = mShadowNodeRegistry.getNode(mTag);
        if (paramReadableArray2 != null)
        {
          localReactShadowNode.addChildAt(paramReadableArray2, mIndex);
          paramInt += 1;
          continue;
        }
        paramReadableArray2 = new StringBuilder();
        paramReadableArray2.append("Trying to add unknown view tag: ");
        paramReadableArray2.append(mTag);
        throw new IllegalViewOperationException(paramReadableArray2.toString());
      }
      catch (Throwable paramReadableArray1)
      {
        for (;;) {}
      }
      mNativeViewHierarchyOptimizer.handleManageChildren(localReactShadowNode, arrayOfInt1, arrayOfInt2, arrayOfViewAtIndex, arrayOfInt3, arrayOfInt4);
      paramInt = 0;
      if (paramInt < arrayOfInt3.length)
      {
        removeShadowNode(mShadowNodeRegistry.getNode(arrayOfInt3[paramInt]));
        paramInt += 1;
      }
      else
      {
        return;
        throw paramReadableArray1;
        if (paramReadableArray3 != null) {
          break;
        }
        k = 0;
        label686:
        if (paramReadableArray5 != null) {
          break label52;
        }
        j = 0;
        break label61;
        label697:
        paramInt = 0;
      }
    }
  }
  
  public void measure(int paramInt, Callback paramCallback)
  {
    mOperationsQueue.enqueueMeasure(paramInt, paramCallback);
  }
  
  public void measureInWindow(int paramInt, Callback paramCallback)
  {
    mOperationsQueue.enqueueMeasureInWindow(paramInt, paramCallback);
  }
  
  public void measureLayout(int paramInt1, int paramInt2, Callback paramCallback1, Callback paramCallback2)
  {
    int[] arrayOfInt = mMeasureBuffer;
    try
    {
      measureLayout(paramInt1, paramInt2, arrayOfInt);
      float f1 = mMeasureBuffer[0];
      f1 = PixelUtil.toDIPFromPixel(f1);
      float f2 = mMeasureBuffer[1];
      f2 = PixelUtil.toDIPFromPixel(f2);
      float f3 = mMeasureBuffer[2];
      f3 = PixelUtil.toDIPFromPixel(f3);
      float f4 = mMeasureBuffer[3];
      f4 = PixelUtil.toDIPFromPixel(f4);
      paramCallback2.invoke(new Object[] { Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4) });
      return;
    }
    catch (IllegalViewOperationException paramCallback2)
    {
      paramCallback1.invoke(new Object[] { paramCallback2.getMessage() });
    }
  }
  
  public void measureLayoutRelativeToParent(int paramInt, Callback paramCallback1, Callback paramCallback2)
  {
    int[] arrayOfInt = mMeasureBuffer;
    try
    {
      measureLayoutRelativeToParent(paramInt, arrayOfInt);
      float f1 = mMeasureBuffer[0];
      f1 = PixelUtil.toDIPFromPixel(f1);
      float f2 = mMeasureBuffer[1];
      f2 = PixelUtil.toDIPFromPixel(f2);
      float f3 = mMeasureBuffer[2];
      f3 = PixelUtil.toDIPFromPixel(f3);
      float f4 = mMeasureBuffer[3];
      f4 = PixelUtil.toDIPFromPixel(f4);
      paramCallback2.invoke(new Object[] { Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4) });
      return;
    }
    catch (IllegalViewOperationException paramCallback2)
    {
      paramCallback1.invoke(new Object[] { paramCallback2.getMessage() });
    }
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause()
  {
    mOperationsQueue.pauseFrameCallback();
  }
  
  public void onHostResume()
  {
    mOperationsQueue.resumeFrameCallback();
  }
  
  public void prependUIBlock(UIBlock paramUIBlock)
  {
    mOperationsQueue.prependUIBlock(paramUIBlock);
  }
  
  public void profileNextBatch()
  {
    mOperationsQueue.profileNextBatch();
  }
  
  public void registerRootView(View paramView, int paramInt, ThemedReactContext paramThemedReactContext)
  {
    Object localObject = uiImplementationThreadLock;
    try
    {
      final ReactShadowNode localReactShadowNode = createRootShadowNode();
      localReactShadowNode.setReactTag(paramInt);
      localReactShadowNode.setThemedContext(paramThemedReactContext);
      paramThemedReactContext.runOnNativeModulesQueueThread(new Runnable()
      {
        public void run()
        {
          mShadowNodeRegistry.addRootNode(localReactShadowNode);
        }
      });
      mOperationsQueue.addRootView(paramInt, paramView);
      return;
    }
    catch (Throwable paramView)
    {
      throw paramView;
    }
  }
  
  public void removeLayoutUpdateListener()
  {
    mLayoutUpdateListener = null;
  }
  
  public void removeRootShadowNode(int paramInt)
  {
    Object localObject = uiImplementationThreadLock;
    try
    {
      mShadowNodeRegistry.removeRootNode(paramInt);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void removeRootView(int paramInt)
  {
    removeRootShadowNode(paramInt);
    mOperationsQueue.enqueueRemoveRootView(paramInt);
  }
  
  protected final void removeShadowNode(ReactShadowNode paramReactShadowNode)
  {
    removeShadowNodeRecursive(paramReactShadowNode);
    paramReactShadowNode.dispose();
  }
  
  public void removeSubviewsFromContainerWithID(int paramInt)
  {
    Object localObject = mShadowNodeRegistry.getNode(paramInt);
    if (localObject != null)
    {
      WritableArray localWritableArray = Arguments.createArray();
      int i = 0;
      while (i < ((ReactShadowNode)localObject).getChildCount())
      {
        localWritableArray.pushInt(i);
        i += 1;
      }
      manageChildren(paramInt, null, null, null, null, localWritableArray);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Trying to remove subviews of an unknown view tag: ");
    ((StringBuilder)localObject).append(paramInt);
    throw new IllegalViewOperationException(((StringBuilder)localObject).toString());
  }
  
  public void replaceExistingNonRootView(int paramInt1, int paramInt2)
  {
    if ((!mShadowNodeRegistry.isRootNode(paramInt1)) && (!mShadowNodeRegistry.isRootNode(paramInt2)))
    {
      Object localObject2 = mShadowNodeRegistry.getNode(paramInt1);
      if (localObject2 != null)
      {
        localObject1 = ((ReactShadowNode)localObject2).getParent();
        if (localObject1 != null)
        {
          paramInt1 = ((ReactShadowNode)localObject1).indexOf((ReactShadowNode)localObject2);
          if (paramInt1 >= 0)
          {
            localObject2 = Arguments.createArray();
            ((WritableArray)localObject2).pushInt(paramInt2);
            WritableArray localWritableArray1 = Arguments.createArray();
            localWritableArray1.pushInt(paramInt1);
            WritableArray localWritableArray2 = Arguments.createArray();
            localWritableArray2.pushInt(paramInt1);
            manageChildren(((ReactShadowNode)localObject1).getReactTag(), null, null, (ReadableArray)localObject2, localWritableArray1, localWritableArray2);
            return;
          }
          throw new IllegalStateException("Didn't find child tag in parent");
        }
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Node is not attached to a parent: ");
        ((StringBuilder)localObject1).append(paramInt1);
        throw new IllegalViewOperationException(((StringBuilder)localObject1).toString());
      }
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Trying to replace unknown view tag: ");
      ((StringBuilder)localObject1).append(paramInt1);
      throw new IllegalViewOperationException(((StringBuilder)localObject1).toString());
    }
    throw new IllegalViewOperationException("Trying to add or replace a root tag!");
  }
  
  public int resolveRootTagFromReactTag(int paramInt)
  {
    if (mShadowNodeRegistry.isRootNode(paramInt)) {
      return paramInt;
    }
    Object localObject = resolveShadowNode(paramInt);
    if (localObject != null) {
      return ((ReactShadowNode)localObject).getRootTag();
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Warning : attempted to resolve a non-existent react shadow node. reactTag=");
    ((StringBuilder)localObject).append(paramInt);
    FLog.warn("ReactNative", ((StringBuilder)localObject).toString());
    return 0;
  }
  
  public final ReactShadowNode resolveShadowNode(int paramInt)
  {
    return mShadowNodeRegistry.getNode(paramInt);
  }
  
  protected final ViewManager resolveViewManager(String paramString)
  {
    return mViewManagers.loadClass(paramString);
  }
  
  public void sendAccessibilityEvent(int paramInt1, int paramInt2)
  {
    mOperationsQueue.enqueueSendAccessibilityEvent(paramInt1, paramInt2);
  }
  
  public void setChildren(int paramInt, ReadableArray paramReadableArray)
  {
    Object localObject1 = uiImplementationThreadLock;
    try
    {
      Object localObject2 = mShadowNodeRegistry.getNode(paramInt);
      paramInt = 0;
      while (paramInt < paramReadableArray.size())
      {
        ReactShadowNode localReactShadowNode = mShadowNodeRegistry.getNode(paramReadableArray.getInt(paramInt));
        if (localReactShadowNode != null)
        {
          ((ReactShadowNode)localObject2).addChildAt(localReactShadowNode, paramInt);
          paramInt += 1;
        }
        else
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Trying to add unknown view tag: ");
          ((StringBuilder)localObject2).append(paramReadableArray.getInt(paramInt));
          throw new IllegalViewOperationException(((StringBuilder)localObject2).toString());
        }
      }
      mNativeViewHierarchyOptimizer.handleSetChildren((ReactShadowNode)localObject2, paramReadableArray);
      return;
    }
    catch (Throwable paramReadableArray)
    {
      throw paramReadableArray;
    }
  }
  
  public void setJSResponder(int paramInt, boolean paramBoolean)
  {
    ReactShadowNode localReactShadowNode2 = mShadowNodeRegistry.getNode(paramInt);
    ReactShadowNode localReactShadowNode1 = localReactShadowNode2;
    if (localReactShadowNode2 == null) {
      return;
    }
    while (localReactShadowNode1.getNativeKind() == NativeKind.NONE) {
      localReactShadowNode1 = localReactShadowNode1.getParent();
    }
    mOperationsQueue.enqueueSetJSResponder(localReactShadowNode1.getReactTag(), paramInt, paramBoolean);
  }
  
  public void setLayoutAnimationEnabledExperimental(boolean paramBoolean)
  {
    mOperationsQueue.enqueueSetLayoutAnimationEnabled(paramBoolean);
  }
  
  public void setLayoutUpdateListener(LayoutUpdateListener paramLayoutUpdateListener)
  {
    mLayoutUpdateListener = paramLayoutUpdateListener;
  }
  
  public void setViewHierarchyUpdateDebugListener(NotThreadSafeViewHierarchyUpdateDebugListener paramNotThreadSafeViewHierarchyUpdateDebugListener)
  {
    mOperationsQueue.setViewHierarchyUpdateDebugListener(paramNotThreadSafeViewHierarchyUpdateDebugListener);
  }
  
  public void setViewLocalData(int paramInt, Object paramObject)
  {
    ReactShadowNode localReactShadowNode = mShadowNodeRegistry.getNode(paramInt);
    if (localReactShadowNode == null)
    {
      paramObject = new StringBuilder();
      paramObject.append("Attempt to set local data for view with unknown tag: ");
      paramObject.append(paramInt);
      FLog.warn("ReactNative", paramObject.toString());
      return;
    }
    localReactShadowNode.setLocalData(paramObject);
    dispatchViewUpdatesIfNeeded();
  }
  
  public void showPopupMenu(int paramInt, ReadableArray paramReadableArray, Callback paramCallback1, Callback paramCallback2)
  {
    assertViewExists(paramInt, "showPopupMenu");
    mOperationsQueue.enqueueShowPopupMenu(paramInt, paramReadableArray, paramCallback1, paramCallback2);
  }
  
  public void synchronouslyUpdateViewOnUIThread(int paramInt, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    UiThreadUtil.assertOnUiThread();
    mOperationsQueue.getNativeViewHierarchyManager().updateProperties(paramInt, paramReactStylesDiffMap);
  }
  
  public void updateNodeSize(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = mShadowNodeRegistry.getNode(paramInt1);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Tried to update size of non-existent tag: ");
      ((StringBuilder)localObject).append(paramInt1);
      FLog.warn("ReactNative", ((StringBuilder)localObject).toString());
      return;
    }
    ((ReactShadowNode)localObject).setStyleWidth(paramInt2);
    ((ReactShadowNode)localObject).setStyleHeight(paramInt3);
    dispatchViewUpdatesIfNeeded();
  }
  
  public void updateRootView(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = mShadowNodeRegistry.getNode(paramInt1);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Tried to update non-existent root tag: ");
      ((StringBuilder)localObject).append(paramInt1);
      FLog.warn("ReactNative", ((StringBuilder)localObject).toString());
      return;
    }
    updateRootView((ReactShadowNode)localObject, paramInt2, paramInt3);
  }
  
  public void updateRootView(ReactShadowNode paramReactShadowNode, int paramInt1, int paramInt2)
  {
    paramReactShadowNode.setMeasureSpecs(paramInt1, paramInt2);
  }
  
  public void updateView(int paramInt, String paramString, ReadableMap paramReadableMap)
  {
    if (mViewManagers.loadClass(paramString) != null)
    {
      ReactShadowNode localReactShadowNode = mShadowNodeRegistry.getNode(paramInt);
      if (localReactShadowNode != null)
      {
        if (paramReadableMap != null)
        {
          paramReadableMap = new ReactStylesDiffMap(paramReadableMap);
          localReactShadowNode.updateProperties(paramReadableMap);
          handleUpdateView(localReactShadowNode, paramString, paramReadableMap);
        }
      }
      else
      {
        paramString = new StringBuilder();
        paramString.append("Trying to update non-existent view with tag ");
        paramString.append(paramInt);
        throw new IllegalViewOperationException(paramString.toString());
      }
    }
    else
    {
      paramReadableMap = new StringBuilder();
      paramReadableMap.append("Got unknown view type: ");
      paramReadableMap.append(paramString);
      throw new IllegalViewOperationException(paramReadableMap.toString());
    }
  }
  
  protected void updateViewHierarchy()
  {
    Systrace.beginSection(0L, "UIImplementation.updateViewHierarchy");
    int i = 0;
    try
    {
      for (;;)
      {
        int j = mShadowNodeRegistry.getRootNodeCount();
        if (i >= j) {
          break;
        }
        j = mShadowNodeRegistry.getRootTag(i);
        ReactShadowNode localReactShadowNode = mShadowNodeRegistry.getNode(j);
        Object localObject = localReactShadowNode.getWidthMeasureSpec();
        if (localObject != null)
        {
          localObject = localReactShadowNode.getHeightMeasureSpec();
          if (localObject != null)
          {
            SystraceMessage.beginSection(0L, "UIImplementation.notifyOnBeforeLayoutRecursive").getStream("rootTag", localReactShadowNode.getReactTag()).flush();
            try
            {
              notifyOnBeforeLayoutRecursive(localReactShadowNode);
              Systrace.endSection(0L);
              calculateRootLayout(localReactShadowNode);
              SystraceMessage.beginSection(0L, "UIImplementation.applyUpdatesRecursive").getStream("rootTag", localReactShadowNode.getReactTag()).flush();
              try
              {
                applyUpdatesRecursive(localReactShadowNode, 0.0F, 0.0F);
                Systrace.endSection(0L);
                localObject = mLayoutUpdateListener;
                if (localObject != null) {
                  mOperationsQueue.enqueueLayoutUpdateFinished(localReactShadowNode, mLayoutUpdateListener);
                }
              }
              catch (Throwable localThrowable1)
              {
                Systrace.endSection(0L);
                throw localThrowable1;
              }
              i += 1;
            }
            catch (Throwable localThrowable2)
            {
              Systrace.endSection(0L);
              throw localThrowable2;
            }
          }
        }
      }
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable localThrowable3)
    {
      Systrace.endSection(0L);
      throw localThrowable3;
    }
  }
  
  public void viewIsDescendantOf(int paramInt1, int paramInt2, Callback paramCallback)
  {
    ReactShadowNode localReactShadowNode1 = mShadowNodeRegistry.getNode(paramInt1);
    ReactShadowNode localReactShadowNode2 = mShadowNodeRegistry.getNode(paramInt2);
    if ((localReactShadowNode1 != null) && (localReactShadowNode2 != null))
    {
      paramCallback.invoke(new Object[] { Boolean.valueOf(localReactShadowNode1.isDescendantOf(localReactShadowNode2)) });
      return;
    }
    paramCallback.invoke(new Object[] { Boolean.valueOf(false) });
  }
  
  public static abstract interface LayoutUpdateListener
  {
    public abstract void onLayoutUpdated(ReactShadowNode paramReactShadowNode);
  }
}
