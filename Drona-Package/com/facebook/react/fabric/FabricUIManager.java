package com.facebook.react.fabric;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.view.View;
import androidx.annotation.GuardedBy;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.Printer;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.infer.annotation.ThreadConfined;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.fabric.events.EventBeatManager;
import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.fabric.events.FabricEventEmitter;
import com.facebook.react.fabric.mounting.LayoutMetricsConversions;
import com.facebook.react.fabric.mounting.MountingManager;
import com.facebook.react.fabric.mounting.mountitems.BatchMountItem;
import com.facebook.react.fabric.mounting.mountitems.CreateMountItem;
import com.facebook.react.fabric.mounting.mountitems.DeleteMountItem;
import com.facebook.react.fabric.mounting.mountitems.DispatchCommandMountItem;
import com.facebook.react.fabric.mounting.mountitems.DispatchStringCommandMountItem;
import com.facebook.react.fabric.mounting.mountitems.InsertMountItem;
import com.facebook.react.fabric.mounting.mountitems.MountItem;
import com.facebook.react.fabric.mounting.mountitems.PreAllocateViewMountItem;
import com.facebook.react.fabric.mounting.mountitems.RemoveMountItem;
import com.facebook.react.fabric.mounting.mountitems.UpdateEventEmitterMountItem;
import com.facebook.react.fabric.mounting.mountitems.UpdateLayoutMountItem;
import com.facebook.react.fabric.mounting.mountitems.UpdateLocalDataMountItem;
import com.facebook.react.fabric.mounting.mountitems.UpdatePropsMountItem;
import com.facebook.react.fabric.mounting.mountitems.UpdateStateMountItem;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.core.ReactChoreographer.CallbackType;
import com.facebook.react.uimanager.ReactRoot;
import com.facebook.react.uimanager.ReactRootViewTagGenerator;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerPropertyUpdater;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressLint({"MissingNativeLoadLibrary"})
public class FabricUIManager
  implements UIManager, LifecycleEventListener
{
  public static final boolean DEBUG;
  private static final int FRAME_TIME_MS = 16;
  private static final int MAX_TIME_IN_FRAME_FOR_NON_BATCHED_OPERATIONS_MS = 8;
  private static final int PRE_MOUNT_ITEMS_INITIAL_SIZE_ARRAY = 250;
  public static final String TAG = "FabricUIManager";
  private long mBatchedExecutionTime = 0L;
  private Binding mBinding;
  private long mCommitStartTime = 0L;
  private int mCurrentSynchronousCommitNumber = 10000;
  @ThreadConfined("UI")
  private final DispatchUIFrameCallback mDispatchUIFrameCallback;
  private long mDispatchViewUpdatesTime = 0L;
  private final EventBeatManager mEventBeatManager;
  private final EventDispatcher mEventDispatcher;
  private long mFinishTransactionCPPTime = 0L;
  private long mFinishTransactionTime = 0L;
  @ThreadConfined("UI")
  private boolean mIsMountingEnabled = true;
  private long mLayoutTime = 0L;
  @GuardedBy("mMountItemsLock")
  private List<MountItem> mMountItems = new ArrayList();
  private final Object mMountItemsLock = new Object();
  private final MountingManager mMountingManager;
  @GuardedBy("mPreMountItemsLock")
  private ArrayDeque<MountItem> mPreMountItems = new ArrayDeque(250);
  private final Object mPreMountItemsLock = new Object();
  private final ReactApplicationContext mReactApplicationContext;
  private final ConcurrentHashMap<Integer, ThemedReactContext> mReactContextForRootTag = new ConcurrentHashMap();
  private long mRunStartTime = 0L;
  
  static
  {
    boolean bool;
    if ((!ReactFeatureFlags.enableFabricLogs) && (!PrinterHolder.getPrinter().shouldDisplayLogMessage(ReactDebugOverlayTags.FABRIC_UI_MANAGER))) {
      bool = false;
    } else {
      bool = true;
    }
    DEBUG = bool;
    FabricSoLoader.staticInit();
  }
  
  public FabricUIManager(ReactApplicationContext paramReactApplicationContext, ViewManagerRegistry paramViewManagerRegistry, EventDispatcher paramEventDispatcher, EventBeatManager paramEventBeatManager)
  {
    mDispatchUIFrameCallback = new DispatchUIFrameCallback(paramReactApplicationContext, null);
    mReactApplicationContext = paramReactApplicationContext;
    mMountingManager = new MountingManager(paramViewManagerRegistry);
    mEventDispatcher = paramEventDispatcher;
    mEventBeatManager = paramEventBeatManager;
    mReactApplicationContext.addLifecycleEventListener(this);
  }
  
  private MountItem createBatchMountItem(MountItem[] paramArrayOfMountItem, int paramInt1, int paramInt2)
  {
    return new BatchMountItem(paramArrayOfMountItem, paramInt1, paramInt2);
  }
  
  private MountItem createMountItem(String paramString, ReadableMap paramReadableMap, Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    paramString = FabricComponents.getFabricComponentName(paramString);
    ThemedReactContext localThemedReactContext = (ThemedReactContext)mReactContextForRootTag.get(Integer.valueOf(paramInt1));
    if (localThemedReactContext != null) {
      return new CreateMountItem(localThemedReactContext, paramInt1, paramInt2, paramString, paramReadableMap, (StateWrapper)paramObject, paramBoolean);
    }
    paramString = new StringBuilder();
    paramString.append("Unable to find ReactContext for root: ");
    paramString.append(paramInt1);
    throw new IllegalArgumentException(paramString.toString());
  }
  
  private MountItem deleteMountItem(int paramInt)
  {
    return new DeleteMountItem(paramInt);
  }
  
  /* Error */
  private void dispatchMountItems()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 253	android/os/SystemClock:uptimeMillis	()J
    //   4: putfield 130	com/facebook/react/fabric/FabricUIManager:mRunStartTime	J
    //   7: aload_0
    //   8: getfield 112	com/facebook/react/fabric/FabricUIManager:mMountItemsLock	Ljava/lang/Object;
    //   11: astore_3
    //   12: aload_3
    //   13: monitorenter
    //   14: aload_0
    //   15: getfield 119	com/facebook/react/fabric/FabricUIManager:mMountItems	Ljava/util/List;
    //   18: invokeinterface 259 1 0
    //   23: ifeq +6 -> 29
    //   26: aload_3
    //   27: monitorexit
    //   28: return
    //   29: aload_0
    //   30: getfield 119	com/facebook/react/fabric/FabricUIManager:mMountItems	Ljava/util/List;
    //   33: astore 4
    //   35: aload_0
    //   36: new 116	java/util/ArrayList
    //   39: dup
    //   40: invokespecial 117	java/util/ArrayList:<init>	()V
    //   43: putfield 119	com/facebook/react/fabric/FabricUIManager:mMountItems	Ljava/util/List;
    //   46: aload_3
    //   47: monitorexit
    //   48: aconst_null
    //   49: astore_3
    //   50: aload_0
    //   51: getfield 114	com/facebook/react/fabric/FabricUIManager:mPreMountItemsLock	Ljava/lang/Object;
    //   54: astore 5
    //   56: aload 5
    //   58: monitorenter
    //   59: aload_0
    //   60: getfield 126	com/facebook/react/fabric/FabricUIManager:mPreMountItems	Ljava/util/ArrayDeque;
    //   63: invokevirtual 260	java/util/ArrayDeque:isEmpty	()Z
    //   66: ifne +22 -> 88
    //   69: aload_0
    //   70: getfield 126	com/facebook/react/fabric/FabricUIManager:mPreMountItems	Ljava/util/ArrayDeque;
    //   73: astore_3
    //   74: aload_0
    //   75: new 121	java/util/ArrayDeque
    //   78: dup
    //   79: sipush 250
    //   82: invokespecial 124	java/util/ArrayDeque:<init>	(I)V
    //   85: putfield 126	com/facebook/react/fabric/FabricUIManager:mPreMountItems	Ljava/util/ArrayDeque;
    //   88: aload 5
    //   90: monitorexit
    //   91: aload_3
    //   92: ifnull +70 -> 162
    //   95: new 221	java/lang/StringBuilder
    //   98: dup
    //   99: invokespecial 222	java/lang/StringBuilder:<init>	()V
    //   102: astore 5
    //   104: aload 5
    //   106: ldc_w 262
    //   109: invokevirtual 228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: pop
    //   113: aload 5
    //   115: aload_3
    //   116: invokevirtual 266	java/util/ArrayDeque:size	()I
    //   119: invokevirtual 231	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   122: pop
    //   123: lconst_0
    //   124: aload 5
    //   126: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   129: invokestatic 272	com/facebook/systrace/Systrace:beginSection	(JLjava/lang/String;)V
    //   132: aload_3
    //   133: invokevirtual 260	java/util/ArrayDeque:isEmpty	()Z
    //   136: ifne +22 -> 158
    //   139: aload_3
    //   140: invokevirtual 276	java/util/ArrayDeque:pollFirst	()Ljava/lang/Object;
    //   143: checkcast 278	com/facebook/react/fabric/mounting/mountitems/MountItem
    //   146: aload_0
    //   147: getfield 158	com/facebook/react/fabric/FabricUIManager:mMountingManager	Lcom/facebook/react/fabric/mounting/MountingManager;
    //   150: invokeinterface 282 2 0
    //   155: goto -23 -> 132
    //   158: lconst_0
    //   159: invokestatic 285	com/facebook/systrace/Systrace:endSection	(J)V
    //   162: new 221	java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial 222	java/lang/StringBuilder:<init>	()V
    //   169: astore_3
    //   170: aload_3
    //   171: ldc_w 287
    //   174: invokevirtual 228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   177: pop
    //   178: aload_3
    //   179: aload 4
    //   181: invokeinterface 288 1 0
    //   186: invokevirtual 231	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   189: pop
    //   190: lconst_0
    //   191: aload_3
    //   192: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   195: invokestatic 272	com/facebook/systrace/Systrace:beginSection	(JLjava/lang/String;)V
    //   198: invokestatic 253	android/os/SystemClock:uptimeMillis	()J
    //   201: lstore_1
    //   202: aload 4
    //   204: invokeinterface 292 1 0
    //   209: astore_3
    //   210: aload_3
    //   211: invokeinterface 297 1 0
    //   216: ifeq +75 -> 291
    //   219: aload_3
    //   220: invokeinterface 300 1 0
    //   225: checkcast 278	com/facebook/react/fabric/mounting/mountitems/MountItem
    //   228: astore 4
    //   230: getstatic 95	com/facebook/react/fabric/FabricUIManager:DEBUG	Z
    //   233: ifeq +44 -> 277
    //   236: getstatic 302	com/facebook/react/fabric/FabricUIManager:TAG	Ljava/lang/String;
    //   239: astore 5
    //   241: new 221	java/lang/StringBuilder
    //   244: dup
    //   245: invokespecial 222	java/lang/StringBuilder:<init>	()V
    //   248: astore 6
    //   250: aload 6
    //   252: ldc_w 304
    //   255: invokevirtual 228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: pop
    //   259: aload 6
    //   261: aload 4
    //   263: invokevirtual 307	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   266: pop
    //   267: aload 5
    //   269: aload 6
    //   271: invokevirtual 237	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   274: invokestatic 313	com/facebook/common/logging/FLog:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   277: aload 4
    //   279: aload_0
    //   280: getfield 158	com/facebook/react/fabric/FabricUIManager:mMountingManager	Lcom/facebook/react/fabric/mounting/MountingManager;
    //   283: invokeinterface 282 2 0
    //   288: goto -78 -> 210
    //   291: aload_0
    //   292: invokestatic 253	android/os/SystemClock:uptimeMillis	()J
    //   295: lload_1
    //   296: lsub
    //   297: putfield 132	com/facebook/react/fabric/FabricUIManager:mBatchedExecutionTime	J
    //   300: lconst_0
    //   301: invokestatic 285	com/facebook/systrace/Systrace:endSection	(J)V
    //   304: return
    //   305: astore_3
    //   306: aload 5
    //   308: monitorexit
    //   309: aload_3
    //   310: athrow
    //   311: astore 4
    //   313: aload_3
    //   314: monitorexit
    //   315: aload 4
    //   317: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	318	0	this	FabricUIManager
    //   201	95	1	l	long
    //   11	209	3	localObject1	Object
    //   305	9	3	localThrowable1	Throwable
    //   33	245	4	localObject2	Object
    //   311	5	4	localThrowable2	Throwable
    //   54	253	5	localObject3	Object
    //   248	22	6	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   59	88	305	java/lang/Throwable
    //   88	91	305	java/lang/Throwable
    //   306	309	305	java/lang/Throwable
    //   14	28	311	java/lang/Throwable
    //   29	48	311	java/lang/Throwable
    //   313	315	311	java/lang/Throwable
  }
  
  private void dispatchPreMountItems(long paramLong)
  {
    Systrace.beginSection(0L, "FabricUIManager::premountViews");
    for (;;)
    {
      Object localObject;
      if (16L - (System.nanoTime() - paramLong) / 1000000L >= 8L) {
        localObject = mPreMountItemsLock;
      }
      try
      {
        if (mPreMountItems.isEmpty())
        {
          Systrace.endSection(0L);
          return;
        }
        MountItem localMountItem = (MountItem)mPreMountItems.pollFirst();
        localMountItem.execute(mMountingManager);
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  private MountItem insertMountItem(int paramInt1, int paramInt2, int paramInt3)
  {
    return new InsertMountItem(paramInt1, paramInt2, paramInt3);
  }
  
  private long measure(String paramString, ReadableMap paramReadableMap1, ReadableMap paramReadableMap2, ReadableMap paramReadableMap3, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return mMountingManager.measure(mReactApplicationContext, paramString, paramReadableMap1, paramReadableMap2, paramReadableMap3, LayoutMetricsConversions.getYogaSize(paramFloat1, paramFloat2), LayoutMetricsConversions.getYogaMeasureMode(paramFloat1, paramFloat2), LayoutMetricsConversions.getYogaSize(paramFloat3, paramFloat4), LayoutMetricsConversions.getYogaMeasureMode(paramFloat3, paramFloat4));
  }
  
  private void preallocateView(int paramInt1, int paramInt2, String paramString, ReadableMap paramReadableMap, Object paramObject, boolean paramBoolean)
  {
    ThemedReactContext localThemedReactContext = (ThemedReactContext)mReactContextForRootTag.get(Integer.valueOf(paramInt1));
    String str = FabricComponents.getFabricComponentName(paramString);
    paramString = mPreMountItemsLock;
    try
    {
      mPreMountItems.add(new PreAllocateViewMountItem(localThemedReactContext, paramInt1, paramInt2, str, paramReadableMap, (StateWrapper)paramObject, paramBoolean));
      return;
    }
    catch (Throwable paramReadableMap)
    {
      throw paramReadableMap;
    }
  }
  
  private MountItem removeMountItem(int paramInt1, int paramInt2, int paramInt3)
  {
    return new RemoveMountItem(paramInt1, paramInt2, paramInt3);
  }
  
  private void scheduleMountItem(MountItem paramMountItem, int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7)
  {
    boolean bool = paramMountItem instanceof BatchMountItem;
    if (bool)
    {
      mCommitStartTime = paramLong1;
      mLayoutTime = (paramLong5 - paramLong4);
      mFinishTransactionCPPTime = (paramLong7 - paramLong6);
      mFinishTransactionTime = (SystemClock.uptimeMillis() - paramLong6);
      mDispatchViewUpdatesTime = SystemClock.uptimeMillis();
    }
    Object localObject = mMountItemsLock;
    try
    {
      mMountItems.add(paramMountItem);
      if (bool)
      {
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_COMMIT_START, null, paramInt, mCommitStartTime);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_FINISH_TRANSACTION_START, null, paramInt, paramLong6);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_FINISH_TRANSACTION_END, null, paramInt, paramLong7);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_DIFF_START, null, paramInt, paramLong2);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_DIFF_END, null, paramInt, paramLong3);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_LAYOUT_START, null, paramInt, paramLong4);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_LAYOUT_END, null, paramInt, paramLong5);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_COMMIT_END, null, paramInt);
      }
      if (UiThreadUtil.isOnUiThread())
      {
        dispatchMountItems();
        return;
      }
    }
    catch (Throwable paramMountItem)
    {
      throw paramMountItem;
    }
  }
  
  private MountItem updateEventEmitterMountItem(int paramInt, Object paramObject)
  {
    return new UpdateEventEmitterMountItem(paramInt, (EventEmitterWrapper)paramObject);
  }
  
  private MountItem updateLayoutMountItem(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    return new UpdateLayoutMountItem(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  private MountItem updateLocalDataMountItem(int paramInt, ReadableMap paramReadableMap)
  {
    return new UpdateLocalDataMountItem(paramInt, paramReadableMap);
  }
  
  private MountItem updatePropsMountItem(int paramInt, ReadableMap paramReadableMap)
  {
    return new UpdatePropsMountItem(paramInt, paramReadableMap);
  }
  
  private MountItem updateStateMountItem(int paramInt, Object paramObject)
  {
    return new UpdateStateMountItem(paramInt, (StateWrapper)paramObject);
  }
  
  public int addRootView(View paramView, WritableMap paramWritableMap, String paramString)
  {
    int i = ReactRootViewTagGenerator.getNextRootViewTag();
    ThemedReactContext localThemedReactContext = new ThemedReactContext(mReactApplicationContext, paramView.getContext());
    mMountingManager.addRootView(i, paramView);
    mReactContextForRootTag.put(Integer.valueOf(i), localThemedReactContext);
    paramView = ((ReactRoot)paramView).getJSModuleName();
    if (DEBUG) {
      FLog.d(TAG, "Starting surface for module: %s and reactTag: %d", paramView, Integer.valueOf(i));
    }
    mBinding.startSurface(i, paramView, (NativeMap)paramWritableMap);
    if (paramString != null) {
      mBinding.renderTemplateToSurface(i, paramString);
    }
    return i;
  }
  
  public void clearJSResponder()
  {
    Object localObject = mMountItemsLock;
    try
    {
      mMountItems.add(new MountItem()
      {
        public void execute(MountingManager paramAnonymousMountingManager)
        {
          paramAnonymousMountingManager.clearJSResponder();
        }
      });
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void dispatchCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    Object localObject = mMountItemsLock;
    try
    {
      mMountItems.add(new DispatchCommandMountItem(paramInt1, paramInt2, paramReadableArray));
      return;
    }
    catch (Throwable paramReadableArray)
    {
      throw paramReadableArray;
    }
  }
  
  public void dispatchCommand(int paramInt, String paramString, ReadableArray paramReadableArray)
  {
    Object localObject = mMountItemsLock;
    try
    {
      mMountItems.add(new DispatchStringCommandMountItem(paramInt, paramString, paramReadableArray));
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public Map getPerformanceCounters()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("CommitStartTime", Long.valueOf(mCommitStartTime));
    localHashMap.put("LayoutTime", Long.valueOf(mLayoutTime));
    localHashMap.put("DispatchViewUpdatesTime", Long.valueOf(mDispatchViewUpdatesTime));
    localHashMap.put("RunStartTime", Long.valueOf(mRunStartTime));
    localHashMap.put("BatchedExecutionTime", Long.valueOf(mBatchedExecutionTime));
    localHashMap.put("FinishFabricTransactionTime", Long.valueOf(mFinishTransactionTime));
    localHashMap.put("FinishFabricTransactionCPPTime", Long.valueOf(mFinishTransactionCPPTime));
    return localHashMap;
  }
  
  public void initialize()
  {
    mEventDispatcher.registerEventEmitter(2, new FabricEventEmitter(this));
    mEventDispatcher.addBatchEventDispatchedListener(mEventBeatManager);
  }
  
  public void onCatalystInstanceDestroy()
  {
    if (DEBUG) {
      FLog.d(TAG, "Destroying Catalyst Instance");
    }
    mEventDispatcher.removeBatchEventDispatchedListener(mEventBeatManager);
    mEventDispatcher.unregisterEventEmitter(2);
    mBinding.unregister();
    ViewManagerPropertyUpdater.clear();
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause()
  {
    ReactChoreographer.getInstance().removeFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, mDispatchUIFrameCallback);
  }
  
  public void onHostResume()
  {
    ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, mDispatchUIFrameCallback);
  }
  
  public void onRequestEventBeat()
  {
    mEventDispatcher.dispatchAllEvents();
  }
  
  public void profileNextBatch() {}
  
  public void receiveEvent(int paramInt, String paramString, WritableMap paramWritableMap)
  {
    Object localObject = mMountingManager.getEventEmitter(paramInt);
    if (localObject == null)
    {
      paramWritableMap = TAG;
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Unable to invoke event: ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(" for reactTag: ");
      ((StringBuilder)localObject).append(paramInt);
      FLog.d(paramWritableMap, ((StringBuilder)localObject).toString());
      return;
    }
    ((EventEmitterWrapper)localObject).invoke(paramString, paramWritableMap);
  }
  
  public void setBinding(Binding paramBinding)
  {
    mBinding = paramBinding;
  }
  
  public void setJSResponder(final int paramInt1, final int paramInt2, final boolean paramBoolean)
  {
    Object localObject = mMountItemsLock;
    try
    {
      mMountItems.add(new MountItem()
      {
        public void execute(MountingManager paramAnonymousMountingManager)
        {
          paramAnonymousMountingManager.setJSResponder(paramInt1, paramInt2, paramBoolean);
        }
      });
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int startSurface(View paramView, String paramString, WritableMap paramWritableMap, int paramInt1, int paramInt2)
  {
    int i = ReactRootViewTagGenerator.getNextRootViewTag();
    ThemedReactContext localThemedReactContext = new ThemedReactContext(mReactApplicationContext, paramView.getContext());
    if (DEBUG) {
      FLog.d(TAG, "Starting surface for module: %s and reactTag: %d", paramString, Integer.valueOf(i));
    }
    mMountingManager.addRootView(i, paramView);
    mReactContextForRootTag.put(Integer.valueOf(i), localThemedReactContext);
    mBinding.startSurfaceWithConstraints(i, paramString, (NativeMap)paramWritableMap, LayoutMetricsConversions.getMinSize(paramInt1), LayoutMetricsConversions.getMaxSize(paramInt1), LayoutMetricsConversions.getMinSize(paramInt2), LayoutMetricsConversions.getMaxSize(paramInt2));
    return i;
  }
  
  public void stopSurface(int paramInt)
  {
    mBinding.stopSurface(paramInt);
  }
  
  public void synchronouslyUpdateViewOnUIThread(int paramInt, ReadableMap paramReadableMap)
  {
    long l = SystemClock.uptimeMillis();
    int i = mCurrentSynchronousCommitNumber;
    mCurrentSynchronousCommitNumber = (i + 1);
    ReactMarkerConstants localReactMarkerConstants = ReactMarkerConstants.FABRIC_UPDATE_UI_MAIN_THREAD_START;
    try
    {
      try
      {
        ReactMarker.logFabricMarker(localReactMarkerConstants, null, i);
        paramReadableMap = updatePropsMountItem(paramInt, paramReadableMap);
        label65:
        paramReadableMap = ReactMarkerConstants.FABRIC_UPDATE_UI_MAIN_THREAD_END;
      }
      catch (Throwable paramReadableMap)
      {
        try
        {
          try
          {
            scheduleMountItem(paramReadableMap, i, l, 0L, 0L, 0L, 0L, 0L, 0L);
            paramReadableMap = ReactMarkerConstants.FABRIC_UPDATE_UI_MAIN_THREAD_END;
          }
          catch (Throwable paramReadableMap)
          {
            break label65;
          }
        }
        catch (Exception paramReadableMap)
        {
          for (;;) {}
        }
        paramReadableMap = paramReadableMap;
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_UPDATE_UI_MAIN_THREAD_END, null, i);
        throw paramReadableMap;
      }
    }
    catch (Exception paramReadableMap)
    {
      for (;;) {}
    }
    ReactMarker.logFabricMarker(paramReadableMap, null, i);
  }
  
  public void updateRootLayoutSpecs(int paramInt1, int paramInt2, int paramInt3)
  {
    if (DEBUG) {
      FLog.d(TAG, "Updating Root Layout Specs");
    }
    mBinding.setConstraints(paramInt1, LayoutMetricsConversions.getMinSize(paramInt2), LayoutMetricsConversions.getMaxSize(paramInt2), LayoutMetricsConversions.getMinSize(paramInt3), LayoutMetricsConversions.getMaxSize(paramInt3));
  }
  
  private class DispatchUIFrameCallback
    extends GuardedFrameCallback
  {
    private DispatchUIFrameCallback(ReactContext paramReactContext)
    {
      super();
    }
    
    /* Error */
    public void doFrameGuarded(long paramLong)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 13	com/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback:this$0	Lcom/facebook/react/fabric/FabricUIManager;
      //   4: invokestatic 30	com/facebook/react/fabric/FabricUIManager:access$100	(Lcom/facebook/react/fabric/FabricUIManager;)Z
      //   7: ifne +11 -> 18
      //   10: ldc 32
      //   12: ldc 34
      //   14: invokestatic 40	com/facebook/common/logging/FLog:warn	(Ljava/lang/String;Ljava/lang/String;)V
      //   17: return
      //   18: aload_0
      //   19: getfield 13	com/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback:this$0	Lcom/facebook/react/fabric/FabricUIManager;
      //   22: astore_3
      //   23: aload_3
      //   24: lload_1
      //   25: invokestatic 44	com/facebook/react/fabric/FabricUIManager:access$200	(Lcom/facebook/react/fabric/FabricUIManager;J)V
      //   28: aload_0
      //   29: getfield 13	com/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback:this$0	Lcom/facebook/react/fabric/FabricUIManager;
      //   32: astore_3
      //   33: aload_3
      //   34: invokestatic 48	com/facebook/react/fabric/FabricUIManager:access$300	(Lcom/facebook/react/fabric/FabricUIManager;)V
      //   37: invokestatic 54	com/facebook/react/modules/core/ReactChoreographer:getInstance	()Lcom/facebook/react/modules/core/ReactChoreographer;
      //   40: getstatic 60	com/facebook/react/modules/core/ReactChoreographer$CallbackType:DISPATCH_UI	Lcom/facebook/react/modules/core/ReactChoreographer$CallbackType;
      //   43: aload_0
      //   44: getfield 13	com/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback:this$0	Lcom/facebook/react/fabric/FabricUIManager;
      //   47: invokestatic 64	com/facebook/react/fabric/FabricUIManager:access$400	(Lcom/facebook/react/fabric/FabricUIManager;)Lcom/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback;
      //   50: invokevirtual 68	com/facebook/react/modules/core/ReactChoreographer:postFrameCallback	(Lcom/facebook/react/modules/core/ReactChoreographer$CallbackType;Lcom/facebook/react/modules/core/ChoreographerCompat$FrameCallback;)V
      //   53: return
      //   54: astore_3
      //   55: goto +23 -> 78
      //   58: astore_3
      //   59: ldc 32
      //   61: ldc 70
      //   63: aload_3
      //   64: invokestatic 74	com/facebook/common/logging/FLog:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   67: aload_0
      //   68: getfield 13	com/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback:this$0	Lcom/facebook/react/fabric/FabricUIManager;
      //   71: iconst_0
      //   72: invokestatic 78	com/facebook/react/fabric/FabricUIManager:access$102	(Lcom/facebook/react/fabric/FabricUIManager;Z)Z
      //   75: pop
      //   76: aload_3
      //   77: athrow
      //   78: invokestatic 54	com/facebook/react/modules/core/ReactChoreographer:getInstance	()Lcom/facebook/react/modules/core/ReactChoreographer;
      //   81: getstatic 60	com/facebook/react/modules/core/ReactChoreographer$CallbackType:DISPATCH_UI	Lcom/facebook/react/modules/core/ReactChoreographer$CallbackType;
      //   84: aload_0
      //   85: getfield 13	com/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback:this$0	Lcom/facebook/react/fabric/FabricUIManager;
      //   88: invokestatic 64	com/facebook/react/fabric/FabricUIManager:access$400	(Lcom/facebook/react/fabric/FabricUIManager;)Lcom/facebook/react/fabric/FabricUIManager$DispatchUIFrameCallback;
      //   91: invokevirtual 68	com/facebook/react/modules/core/ReactChoreographer:postFrameCallback	(Lcom/facebook/react/modules/core/ReactChoreographer$CallbackType;Lcom/facebook/react/modules/core/ChoreographerCompat$FrameCallback;)V
      //   94: aload_3
      //   95: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	96	0	this	DispatchUIFrameCallback
      //   0	96	1	paramLong	long
      //   22	12	3	localFabricUIManager	FabricUIManager
      //   54	1	3	localThrowable	Throwable
      //   58	37	3	localException	Exception
      // Exception table:
      //   from	to	target	type
      //   18	23	54	java/lang/Throwable
      //   23	28	54	java/lang/Throwable
      //   33	37	54	java/lang/Throwable
      //   59	78	54	java/lang/Throwable
      //   23	28	58	java/lang/Exception
      //   33	37	58	java/lang/Exception
    }
  }
}
