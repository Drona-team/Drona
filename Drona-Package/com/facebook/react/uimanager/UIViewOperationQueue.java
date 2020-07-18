package com.facebook.react.uimanager;

import android.os.SystemClock;
import android.view.View;
import androidx.annotation.GuardedBy;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.core.ReactChoreographer.CallbackType;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UIViewOperationQueue
{
  public static final int DEFAULT_MIN_TIME_LEFT_IN_FRAME_FOR_NONBATCHED_OPERATION_MS = 8;
  private final Object mDispatchRunnablesLock = new Object();
  private final DispatchUIFrameCallback mDispatchUIFrameCallback;
  @GuardedBy("mDispatchRunnablesLock")
  private ArrayList<Runnable> mDispatchUIRunnables = new ArrayList();
  private boolean mIsDispatchUIFrameCallbackEnqueued = false;
  private boolean mIsInIllegalUIState = false;
  private boolean mIsProfilingNextBatch = false;
  private final int[] mMeasureBuffer = new int[4];
  private final NativeViewHierarchyManager mNativeViewHierarchyManager;
  private long mNonBatchedExecutionTotalTime;
  @GuardedBy("mNonBatchedOperationsLock")
  private ArrayDeque<UIOperation> mNonBatchedOperations = new ArrayDeque();
  private final Object mNonBatchedOperationsLock = new Object();
  private ArrayList<UIOperation> mOperations = new ArrayList();
  private long mProfiledBatchBatchedExecutionTime;
  private long mProfiledBatchCommitStartTime;
  private long mProfiledBatchDispatchViewUpdatesTime;
  private long mProfiledBatchLayoutTime;
  private long mProfiledBatchNonBatchedExecutionTime;
  private long mProfiledBatchRunStartTime;
  private final ReactApplicationContext mReactApplicationContext;
  private long mThreadCpuTime;
  @Nullable
  private NotThreadSafeViewHierarchyUpdateDebugListener mViewHierarchyUpdateDebugListener;
  
  public UIViewOperationQueue(ReactApplicationContext paramReactApplicationContext, NativeViewHierarchyManager paramNativeViewHierarchyManager, int paramInt)
  {
    mNativeViewHierarchyManager = paramNativeViewHierarchyManager;
    int i = paramInt;
    if (paramInt == -1) {
      i = 8;
    }
    mDispatchUIFrameCallback = new DispatchUIFrameCallback(paramReactApplicationContext, i, null);
    mReactApplicationContext = paramReactApplicationContext;
  }
  
  private void flushPendingBatches()
  {
    if (mIsInIllegalUIState)
    {
      FLog.warn("ReactNative", "Not flushing pending UI operations because of previously thrown Exception");
      return;
    }
    Object localObject = mDispatchRunnablesLock;
    try
    {
      if (!mDispatchUIRunnables.isEmpty())
      {
        ArrayList localArrayList = mDispatchUIRunnables;
        mDispatchUIRunnables = new ArrayList();
        long l = SystemClock.uptimeMillis();
        localObject = localArrayList.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Runnable)((Iterator)localObject).next()).run();
        }
        if (mIsProfilingNextBatch)
        {
          mProfiledBatchBatchedExecutionTime = (SystemClock.uptimeMillis() - l);
          mProfiledBatchNonBatchedExecutionTime = mNonBatchedExecutionTotalTime;
          mIsProfilingNextBatch = false;
          Systrace.beginAsyncSection(0L, "batchedExecutionTime", 0, 1000000L * l);
          Systrace.endAsyncSection(0L, "batchedExecutionTime", 0);
        }
        mNonBatchedExecutionTotalTime = 0L;
        return;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void addRootView(int paramInt, View paramView)
  {
    mNativeViewHierarchyManager.addRootView(paramInt, paramView);
  }
  
  /* Error */
  public void dispatchViewUpdates(final int paramInt, final long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: lconst_0
    //   1: ldc_w 281
    //   4: invokestatic 287	com/facebook/systrace/SystraceMessage:beginSection	(JLjava/lang/String;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   7: ldc_w 289
    //   10: iload_1
    //   11: invokevirtual 295	com/facebook/systrace/SystraceMessage$Builder:getStream	(Ljava/lang/String;I)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   14: invokevirtual 298	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   17: invokestatic 235	android/os/SystemClock:uptimeMillis	()J
    //   20: lstore 6
    //   22: invokestatic 301	android/os/SystemClock:currentThreadTimeMillis	()J
    //   25: lstore 8
    //   27: aload_0
    //   28: getfield 142	com/facebook/react/uimanager/UIViewOperationQueue:mOperations	Ljava/util/ArrayList;
    //   31: invokevirtual 229	java/util/ArrayList:isEmpty	()Z
    //   34: istore 10
    //   36: aconst_null
    //   37: astore 12
    //   39: iload 10
    //   41: ifne +23 -> 64
    //   44: aload_0
    //   45: getfield 142	com/facebook/react/uimanager/UIViewOperationQueue:mOperations	Ljava/util/ArrayList;
    //   48: astore 11
    //   50: aload_0
    //   51: new 139	java/util/ArrayList
    //   54: dup
    //   55: invokespecial 140	java/util/ArrayList:<init>	()V
    //   58: putfield 142	com/facebook/react/uimanager/UIViewOperationQueue:mOperations	Ljava/util/ArrayList;
    //   61: goto +6 -> 67
    //   64: aconst_null
    //   65: astore 11
    //   67: aload_0
    //   68: getfield 137	com/facebook/react/uimanager/UIViewOperationQueue:mNonBatchedOperationsLock	Ljava/lang/Object;
    //   71: astore 13
    //   73: aload 13
    //   75: monitorenter
    //   76: aload_0
    //   77: getfield 149	com/facebook/react/uimanager/UIViewOperationQueue:mNonBatchedOperations	Ljava/util/ArrayDeque;
    //   80: invokevirtual 302	java/util/ArrayDeque:isEmpty	()Z
    //   83: ifne +20 -> 103
    //   86: aload_0
    //   87: getfield 149	com/facebook/react/uimanager/UIViewOperationQueue:mNonBatchedOperations	Ljava/util/ArrayDeque;
    //   90: astore 12
    //   92: aload_0
    //   93: new 146	java/util/ArrayDeque
    //   96: dup
    //   97: invokespecial 147	java/util/ArrayDeque:<init>	()V
    //   100: putfield 149	com/facebook/react/uimanager/UIViewOperationQueue:mNonBatchedOperations	Ljava/util/ArrayDeque;
    //   103: aload 13
    //   105: monitorexit
    //   106: aload_0
    //   107: getfield 197	com/facebook/react/uimanager/UIViewOperationQueue:mViewHierarchyUpdateDebugListener	Lcom/facebook/react/uimanager/debug/NotThreadSafeViewHierarchyUpdateDebugListener;
    //   110: astore 13
    //   112: aload 13
    //   114: ifnull +12 -> 126
    //   117: aload_0
    //   118: getfield 197	com/facebook/react/uimanager/UIViewOperationQueue:mViewHierarchyUpdateDebugListener	Lcom/facebook/react/uimanager/debug/NotThreadSafeViewHierarchyUpdateDebugListener;
    //   121: invokeinterface 307 1 0
    //   126: new 6	com/facebook/react/uimanager/UIViewOperationQueue$1
    //   129: dup
    //   130: aload_0
    //   131: iload_1
    //   132: aload 12
    //   134: aload 11
    //   136: lload_2
    //   137: lload 4
    //   139: lload 6
    //   141: lload 8
    //   143: invokespecial 310	com/facebook/react/uimanager/UIViewOperationQueue$1:<init>	(Lcom/facebook/react/uimanager/UIViewOperationQueue;ILjava/util/ArrayDeque;Ljava/util/ArrayList;JJJJ)V
    //   146: astore 12
    //   148: lconst_0
    //   149: ldc_w 312
    //   152: invokestatic 287	com/facebook/systrace/SystraceMessage:beginSection	(JLjava/lang/String;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   155: ldc_w 289
    //   158: iload_1
    //   159: invokevirtual 295	com/facebook/systrace/SystraceMessage$Builder:getStream	(Ljava/lang/String;I)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   162: invokevirtual 298	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   165: aload_0
    //   166: getfield 135	com/facebook/react/uimanager/UIViewOperationQueue:mDispatchRunnablesLock	Ljava/lang/Object;
    //   169: astore 11
    //   171: aload 11
    //   173: monitorenter
    //   174: lconst_0
    //   175: invokestatic 316	com/facebook/systrace/Systrace:endSection	(J)V
    //   178: aload_0
    //   179: getfield 144	com/facebook/react/uimanager/UIViewOperationQueue:mDispatchUIRunnables	Ljava/util/ArrayList;
    //   182: aload 12
    //   184: invokevirtual 320	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   187: pop
    //   188: aload 11
    //   190: monitorexit
    //   191: aload_0
    //   192: getfield 151	com/facebook/react/uimanager/UIViewOperationQueue:mIsDispatchUIFrameCallbackEnqueued	Z
    //   195: istore 10
    //   197: iload 10
    //   199: ifne +18 -> 217
    //   202: new 8	com/facebook/react/uimanager/UIViewOperationQueue$2
    //   205: dup
    //   206: aload_0
    //   207: aload_0
    //   208: getfield 164	com/facebook/react/uimanager/UIViewOperationQueue:mReactApplicationContext	Lcom/facebook/react/bridge/ReactApplicationContext;
    //   211: invokespecial 323	com/facebook/react/uimanager/UIViewOperationQueue$2:<init>	(Lcom/facebook/react/uimanager/UIViewOperationQueue;Lcom/facebook/react/bridge/ReactContext;)V
    //   214: invokestatic 329	com/facebook/react/bridge/UiThreadUtil:runOnUiThread	(Ljava/lang/Runnable;)V
    //   217: lconst_0
    //   218: invokestatic 316	com/facebook/systrace/Systrace:endSection	(J)V
    //   221: return
    //   222: astore 12
    //   224: aload 11
    //   226: monitorexit
    //   227: aload 12
    //   229: athrow
    //   230: astore 11
    //   232: goto +23 -> 255
    //   235: astore 11
    //   237: aload 13
    //   239: monitorexit
    //   240: aload 11
    //   242: athrow
    //   243: astore 11
    //   245: goto +10 -> 255
    //   248: astore 11
    //   250: goto -13 -> 237
    //   253: astore 11
    //   255: lconst_0
    //   256: invokestatic 316	com/facebook/systrace/Systrace:endSection	(J)V
    //   259: aload 11
    //   261: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	262	0	this	UIViewOperationQueue
    //   0	262	1	paramInt	int
    //   0	262	2	paramLong1	long
    //   0	262	4	paramLong2	long
    //   20	120	6	l1	long
    //   25	117	8	l2	long
    //   34	164	10	bool	boolean
    //   48	177	11	localObject1	Object
    //   230	1	11	localThrowable1	Throwable
    //   235	6	11	localThrowable2	Throwable
    //   243	1	11	localThrowable3	Throwable
    //   248	1	11	localThrowable4	Throwable
    //   253	7	11	localThrowable5	Throwable
    //   37	146	12	localObject2	Object
    //   222	6	12	localThrowable6	Throwable
    //   71	167	13	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   174	191	222	java/lang/Throwable
    //   224	227	222	java/lang/Throwable
    //   126	148	230	java/lang/Throwable
    //   76	103	235	java/lang/Throwable
    //   103	106	235	java/lang/Throwable
    //   148	174	243	java/lang/Throwable
    //   191	197	243	java/lang/Throwable
    //   202	217	243	java/lang/Throwable
    //   227	230	243	java/lang/Throwable
    //   240	243	243	java/lang/Throwable
    //   237	240	248	java/lang/Throwable
    //   17	36	253	java/lang/Throwable
    //   44	61	253	java/lang/Throwable
    //   67	76	253	java/lang/Throwable
    //   106	112	253	java/lang/Throwable
    //   117	126	253	java/lang/Throwable
  }
  
  public void enqueueClearJSResponder()
  {
    mOperations.add(new ChangeJSResponderOperation(0, 0, true, false));
  }
  
  public void enqueueConfigureLayoutAnimation(ReadableMap paramReadableMap, Callback paramCallback)
  {
    mOperations.add(new ConfigureLayoutAnimationOperation(paramReadableMap, paramCallback, null));
  }
  
  public void enqueueCreateView(ThemedReactContext paramThemedReactContext, int paramInt, String paramString, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    Object localObject = mNonBatchedOperationsLock;
    try
    {
      mNonBatchedOperations.addLast(new CreateViewOperation(paramThemedReactContext, paramInt, paramString, paramReactStylesDiffMap));
      return;
    }
    catch (Throwable paramThemedReactContext)
    {
      throw paramThemedReactContext;
    }
  }
  
  public void enqueueDismissPopupMenu()
  {
    mOperations.add(new DismissPopupMenuOperation(null));
  }
  
  public void enqueueDispatchCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    mOperations.add(new DispatchCommandOperation(paramInt1, paramInt2, paramReadableArray));
  }
  
  public void enqueueDispatchCommand(int paramInt, String paramString, ReadableArray paramReadableArray)
  {
    mOperations.add(new DispatchStringCommandOperation(paramInt, paramString, paramReadableArray));
  }
  
  public void enqueueFindTargetForTouch(int paramInt, float paramFloat1, float paramFloat2, Callback paramCallback)
  {
    mOperations.add(new FindTargetForTouchOperation(paramInt, paramFloat1, paramFloat2, paramCallback, null));
  }
  
  public void enqueueLayoutUpdateFinished(ReactShadowNode paramReactShadowNode, UIImplementation.LayoutUpdateListener paramLayoutUpdateListener)
  {
    mOperations.add(new LayoutUpdateFinishedOperation(paramReactShadowNode, paramLayoutUpdateListener, null));
  }
  
  public void enqueueManageChildren(int paramInt, int[] paramArrayOfInt1, ViewAtIndex[] paramArrayOfViewAtIndex, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
  {
    mOperations.add(new ManageChildrenOperation(paramInt, paramArrayOfInt1, paramArrayOfViewAtIndex, paramArrayOfInt2, paramArrayOfInt3));
  }
  
  public void enqueueMeasure(int paramInt, Callback paramCallback)
  {
    mOperations.add(new MeasureOperation(paramInt, paramCallback, null));
  }
  
  public void enqueueMeasureInWindow(int paramInt, Callback paramCallback)
  {
    mOperations.add(new MeasureInWindowOperation(paramInt, paramCallback, null));
  }
  
  public void enqueueOnLayoutEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    mOperations.add(new EmitOnLayoutEventOperation(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5));
  }
  
  public void enqueueRemoveRootView(int paramInt)
  {
    mOperations.add(new RemoveRootViewOperation(paramInt));
  }
  
  public void enqueueSendAccessibilityEvent(int paramInt1, int paramInt2)
  {
    mOperations.add(new SendAccessibilityEvent(paramInt1, paramInt2, null));
  }
  
  public void enqueueSetChildren(int paramInt, ReadableArray paramReadableArray)
  {
    mOperations.add(new SetChildrenOperation(paramInt, paramReadableArray));
  }
  
  public void enqueueSetJSResponder(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    mOperations.add(new ChangeJSResponderOperation(paramInt1, paramInt2, false, paramBoolean));
  }
  
  public void enqueueSetLayoutAnimationEnabled(boolean paramBoolean)
  {
    mOperations.add(new SetLayoutAnimationEnabledOperation(paramBoolean, null));
  }
  
  public void enqueueShowPopupMenu(int paramInt, ReadableArray paramReadableArray, Callback paramCallback1, Callback paramCallback2)
  {
    mOperations.add(new ShowPopupMenuOperation(paramInt, paramReadableArray, paramCallback1, paramCallback2));
  }
  
  public void enqueueUIBlock(UIBlock paramUIBlock)
  {
    mOperations.add(new UIBlockOperation(paramUIBlock));
  }
  
  protected void enqueueUIOperation(UIOperation paramUIOperation)
  {
    SoftAssertions.assertNotNull(paramUIOperation);
    mOperations.add(paramUIOperation);
  }
  
  public void enqueueUpdateExtraData(int paramInt, Object paramObject)
  {
    mOperations.add(new UpdateViewExtraData(paramInt, paramObject));
  }
  
  public void enqueueUpdateInstanceHandle(int paramInt, long paramLong)
  {
    mOperations.add(new UpdateInstanceHandleOperation(paramInt, paramLong, null));
  }
  
  public void enqueueUpdateLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    mOperations.add(new UpdateLayoutOperation(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void enqueueUpdateProperties(int paramInt, String paramString, ReactStylesDiffMap paramReactStylesDiffMap)
  {
    mOperations.add(new UpdatePropertiesOperation(paramInt, paramReactStylesDiffMap, null));
  }
  
  NativeViewHierarchyManager getNativeViewHierarchyManager()
  {
    return mNativeViewHierarchyManager;
  }
  
  public Map getProfiledBatchPerfCounters()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("CommitStartTime", Long.valueOf(mProfiledBatchCommitStartTime));
    localHashMap.put("LayoutTime", Long.valueOf(mProfiledBatchLayoutTime));
    localHashMap.put("DispatchViewUpdatesTime", Long.valueOf(mProfiledBatchDispatchViewUpdatesTime));
    localHashMap.put("RunStartTime", Long.valueOf(mProfiledBatchRunStartTime));
    localHashMap.put("BatchedExecutionTime", Long.valueOf(mProfiledBatchBatchedExecutionTime));
    localHashMap.put("NonBatchedExecutionTime", Long.valueOf(mProfiledBatchNonBatchedExecutionTime));
    localHashMap.put("NativeModulesThreadCpuTime", Long.valueOf(mThreadCpuTime));
    return localHashMap;
  }
  
  public boolean isEmpty()
  {
    return mOperations.isEmpty();
  }
  
  void pauseFrameCallback()
  {
    mIsDispatchUIFrameCallbackEnqueued = false;
    ReactChoreographer.getInstance().removeFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, mDispatchUIFrameCallback);
    flushPendingBatches();
  }
  
  public void prependUIBlock(UIBlock paramUIBlock)
  {
    mOperations.add(0, new UIBlockOperation(paramUIBlock));
  }
  
  public void profileNextBatch()
  {
    mIsProfilingNextBatch = true;
    mProfiledBatchCommitStartTime = 0L;
  }
  
  void resumeFrameCallback()
  {
    mIsDispatchUIFrameCallbackEnqueued = true;
    ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, mDispatchUIFrameCallback);
  }
  
  public void setViewHierarchyUpdateDebugListener(NotThreadSafeViewHierarchyUpdateDebugListener paramNotThreadSafeViewHierarchyUpdateDebugListener)
  {
    mViewHierarchyUpdateDebugListener = paramNotThreadSafeViewHierarchyUpdateDebugListener;
  }
  
  private static abstract class AnimationOperation
    implements UIViewOperationQueue.UIOperation
  {
    protected final int mAnimationID;
    
    public AnimationOperation(int paramInt)
    {
      mAnimationID = paramInt;
    }
  }
  
  private final class ChangeJSResponderOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final boolean mBlockNativeResponder;
    private final boolean mClearResponder;
    private final int mInitialTag;
    
    public ChangeJSResponderOperation(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      super(paramInt1);
      mInitialTag = paramInt2;
      mClearResponder = paramBoolean1;
      mBlockNativeResponder = paramBoolean2;
    }
    
    public void execute()
    {
      if (!mClearResponder)
      {
        mNativeViewHierarchyManager.setJSResponder(mTag, mInitialTag, mBlockNativeResponder);
        return;
      }
      mNativeViewHierarchyManager.clearJSResponder();
    }
  }
  
  private class ConfigureLayoutAnimationOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final Callback mAnimationComplete;
    private final ReadableMap mConfig;
    
    private ConfigureLayoutAnimationOperation(ReadableMap paramReadableMap, Callback paramCallback)
    {
      mConfig = paramReadableMap;
      mAnimationComplete = paramCallback;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.configureLayoutAnimation(mConfig, mAnimationComplete);
    }
  }
  
  private final class CreateViewOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final String mClassName;
    @Nullable
    private final ReactStylesDiffMap mInitialProps;
    private final ThemedReactContext mThemedContext;
    
    public CreateViewOperation(ThemedReactContext paramThemedReactContext, int paramInt, String paramString, ReactStylesDiffMap paramReactStylesDiffMap)
    {
      super(paramInt);
      mThemedContext = paramThemedReactContext;
      mClassName = paramString;
      mInitialProps = paramReactStylesDiffMap;
      Systrace.startAsyncFlow(0L, "createView", mTag);
    }
    
    public void execute()
    {
      Systrace.endAsyncFlow(0L, "createView", mTag);
      mNativeViewHierarchyManager.createView(mThemedContext, mTag, mClassName, mInitialProps);
    }
  }
  
  private final class DismissPopupMenuOperation
    implements UIViewOperationQueue.UIOperation
  {
    private DismissPopupMenuOperation() {}
    
    public void execute()
    {
      mNativeViewHierarchyManager.dismissPopupMenu();
    }
  }
  
  @Deprecated
  private final class DispatchCommandOperation
    extends UIViewOperationQueue.ViewOperation
  {
    @Nullable
    private final ReadableArray mArgs;
    private final int mCommand;
    
    public DispatchCommandOperation(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
    {
      super(paramInt1);
      mCommand = paramInt2;
      mArgs = paramReadableArray;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.dispatchCommand(mTag, mCommand, mArgs);
    }
  }
  
  private final class DispatchStringCommandOperation
    extends UIViewOperationQueue.ViewOperation
  {
    @Nullable
    private final ReadableArray mArgs;
    private final String mCommand;
    
    public DispatchStringCommandOperation(int paramInt, String paramString, ReadableArray paramReadableArray)
    {
      super(paramInt);
      mCommand = paramString;
      mArgs = paramReadableArray;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.dispatchCommand(mTag, mCommand, mArgs);
    }
  }
  
  private class DispatchUIFrameCallback
    extends GuardedFrameCallback
  {
    private static final int FRAME_TIME_MS = 16;
    private final int mMinTimeLeftInFrameForNonBatchedOperationMs;
    
    private DispatchUIFrameCallback(ReactContext paramReactContext, int paramInt)
    {
      super();
      mMinTimeLeftInFrameForNonBatchedOperationMs = paramInt;
    }
    
    /* Error */
    private void dispatchPendingNonBatchedOperations(long paramLong)
    {
      // Byte code:
      //   0: ldc2_w 33
      //   3: invokestatic 40	java/lang/System:nanoTime	()J
      //   6: lload_1
      //   7: lsub
      //   8: ldc2_w 41
      //   11: ldiv
      //   12: lsub
      //   13: aload_0
      //   14: getfield 22	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:mMinTimeLeftInFrameForNonBatchedOperationMs	I
      //   17: i2l
      //   18: lcmp
      //   19: ifge +4 -> 23
      //   22: return
      //   23: aload_0
      //   24: getfield 17	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:this$0	Lcom/facebook/react/uimanager/UIViewOperationQueue;
      //   27: invokestatic 46	com/facebook/react/uimanager/UIViewOperationQueue:access$2300	(Lcom/facebook/react/uimanager/UIViewOperationQueue;)Ljava/lang/Object;
      //   30: astore 9
      //   32: aload 9
      //   34: monitorenter
      //   35: aload_0
      //   36: getfield 17	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:this$0	Lcom/facebook/react/uimanager/UIViewOperationQueue;
      //   39: invokestatic 50	com/facebook/react/uimanager/UIViewOperationQueue:access$2400	(Lcom/facebook/react/uimanager/UIViewOperationQueue;)Ljava/util/ArrayDeque;
      //   42: invokevirtual 56	java/util/ArrayDeque:isEmpty	()Z
      //   45: ifeq +7 -> 52
      //   48: aload 9
      //   50: monitorexit
      //   51: return
      //   52: aload_0
      //   53: getfield 17	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:this$0	Lcom/facebook/react/uimanager/UIViewOperationQueue;
      //   56: invokestatic 50	com/facebook/react/uimanager/UIViewOperationQueue:access$2400	(Lcom/facebook/react/uimanager/UIViewOperationQueue;)Ljava/util/ArrayDeque;
      //   59: invokevirtual 60	java/util/ArrayDeque:pollFirst	()Ljava/lang/Object;
      //   62: checkcast 62	com/facebook/react/uimanager/UIViewOperationQueue$UIOperation
      //   65: astore 10
      //   67: aload 9
      //   69: monitorexit
      //   70: invokestatic 67	android/os/SystemClock:uptimeMillis	()J
      //   73: lstore_3
      //   74: aload 10
      //   76: invokeinterface 71 1 0
      //   81: aload_0
      //   82: getfield 17	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:this$0	Lcom/facebook/react/uimanager/UIViewOperationQueue;
      //   85: astore 9
      //   87: aload_0
      //   88: getfield 17	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:this$0	Lcom/facebook/react/uimanager/UIViewOperationQueue;
      //   91: astore 10
      //   93: aload 10
      //   95: invokestatic 75	com/facebook/react/uimanager/UIViewOperationQueue:access$2500	(Lcom/facebook/react/uimanager/UIViewOperationQueue;)J
      //   98: lstore 5
      //   100: invokestatic 67	android/os/SystemClock:uptimeMillis	()J
      //   103: lstore 7
      //   105: aload 9
      //   107: lload 5
      //   109: lload 7
      //   111: lload_3
      //   112: lsub
      //   113: ladd
      //   114: invokestatic 79	com/facebook/react/uimanager/UIViewOperationQueue:access$2502	(Lcom/facebook/react/uimanager/UIViewOperationQueue;J)J
      //   117: pop2
      //   118: goto -118 -> 0
      //   121: astore 9
      //   123: aload_0
      //   124: getfield 17	com/facebook/react/uimanager/UIViewOperationQueue$DispatchUIFrameCallback:this$0	Lcom/facebook/react/uimanager/UIViewOperationQueue;
      //   127: iconst_1
      //   128: invokestatic 83	com/facebook/react/uimanager/UIViewOperationQueue:access$2102	(Lcom/facebook/react/uimanager/UIViewOperationQueue;Z)Z
      //   131: pop
      //   132: aload 9
      //   134: athrow
      //   135: astore 10
      //   137: aload 9
      //   139: monitorexit
      //   140: aload 10
      //   142: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	143	0	this	DispatchUIFrameCallback
      //   0	143	1	paramLong	long
      //   73	39	3	l1	long
      //   98	10	5	l2	long
      //   103	7	7	l3	long
      //   30	76	9	localObject1	Object
      //   121	17	9	localException	Exception
      //   65	29	10	localObject2	Object
      //   135	6	10	localThrowable	Throwable
      // Exception table:
      //   from	to	target	type
      //   70	81	121	java/lang/Exception
      //   93	105	121	java/lang/Exception
      //   105	118	121	java/lang/Exception
      //   35	51	135	java/lang/Throwable
      //   52	70	135	java/lang/Throwable
      //   137	140	135	java/lang/Throwable
    }
    
    public void doFrameGuarded(long paramLong)
    {
      if (mIsInIllegalUIState)
      {
        FLog.warn("ReactNative", "Not flushing pending UI operations because of previously thrown Exception");
        return;
      }
      Systrace.beginSection(0L, "dispatchNonBatchedUIOperations");
      try
      {
        dispatchPendingNonBatchedOperations(paramLong);
        Systrace.endSection(0L);
        UIViewOperationQueue.this.flushPendingBatches();
        ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, this);
        return;
      }
      catch (Throwable localThrowable)
      {
        Systrace.endSection(0L);
        throw localThrowable;
      }
    }
  }
  
  private final class EmitOnLayoutEventOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final int mScreenHeight;
    private final int mScreenWidth;
    private final int mScreenX;
    private final int mScreenY;
    
    public EmitOnLayoutEventOperation(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super(paramInt1);
      mScreenX = paramInt2;
      mScreenY = paramInt3;
      mScreenWidth = paramInt4;
      mScreenHeight = paramInt5;
    }
    
    public void execute()
    {
      ((UIManagerModule)mReactApplicationContext.getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(OnLayoutEvent.obtain(mTag, mScreenX, mScreenY, mScreenWidth, mScreenHeight));
    }
  }
  
  private final class FindTargetForTouchOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final Callback mCallback;
    private final int mReactTag;
    private final float mTargetX;
    private final float mTargetY;
    
    private FindTargetForTouchOperation(int paramInt, float paramFloat1, float paramFloat2, Callback paramCallback)
    {
      mReactTag = paramInt;
      mTargetX = paramFloat1;
      mTargetY = paramFloat2;
      mCallback = paramCallback;
    }
    
    public void execute()
    {
      Object localObject = UIViewOperationQueue.this;
      FindTargetForTouchOperation localFindTargetForTouchOperation = this;
      try
      {
        localObject = mNativeViewHierarchyManager;
        i = mReactTag;
        localUIViewOperationQueue = this$0;
        ((NativeViewHierarchyManager)localObject).measure(i, mMeasureBuffer);
        f1 = this$0.mMeasureBuffer[0];
        f2 = this$0.mMeasureBuffer[1];
        i = this$0.mNativeViewHierarchyManager.findTargetTagForTouch(mReactTag, mTargetX, mTargetY);
        localObject = this$0;
      }
      catch (IllegalViewOperationException localIllegalViewOperationException1)
      {
        int i;
        UIViewOperationQueue localUIViewOperationQueue;
        float f1;
        float f2;
        float f3;
        float f4;
        label244:
        for (;;) {}
      }
      try
      {
        localObject = mNativeViewHierarchyManager;
        localUIViewOperationQueue = this$0;
        ((NativeViewHierarchyManager)localObject).measure(i, mMeasureBuffer);
        f1 = PixelUtil.toDIPFromPixel(this$0.mMeasureBuffer[0] - f1);
        f2 = PixelUtil.toDIPFromPixel(this$0.mMeasureBuffer[1] - f2);
        f3 = PixelUtil.toDIPFromPixel(this$0.mMeasureBuffer[2]);
        f4 = PixelUtil.toDIPFromPixel(this$0.mMeasureBuffer[3]);
        mCallback.invoke(new Object[] { Integer.valueOf(i), Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4) });
        return;
      }
      catch (IllegalViewOperationException localIllegalViewOperationException2)
      {
        break label244;
      }
      mCallback.invoke(new Object[0]);
      return;
      mCallback.invoke(new Object[0]);
    }
  }
  
  private final class LayoutUpdateFinishedOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final UIImplementation.LayoutUpdateListener mListener;
    private final ReactShadowNode mNode;
    
    private LayoutUpdateFinishedOperation(ReactShadowNode paramReactShadowNode, UIImplementation.LayoutUpdateListener paramLayoutUpdateListener)
    {
      mNode = paramReactShadowNode;
      mListener = paramLayoutUpdateListener;
    }
    
    public void execute()
    {
      mListener.onLayoutUpdated(mNode);
    }
  }
  
  private final class ManageChildrenOperation
    extends UIViewOperationQueue.ViewOperation
  {
    @Nullable
    private final int[] mIndicesToDelete;
    @Nullable
    private final int[] mIndicesToRemove;
    @Nullable
    private final int[] mTagsToDelete;
    @Nullable
    private final ViewAtIndex[] mViewsToAdd;
    
    public ManageChildrenOperation(int paramInt, int[] paramArrayOfInt1, ViewAtIndex[] paramArrayOfViewAtIndex, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
    {
      super(paramInt);
      mIndicesToRemove = paramArrayOfInt1;
      mViewsToAdd = paramArrayOfViewAtIndex;
      mTagsToDelete = paramArrayOfInt2;
      mIndicesToDelete = paramArrayOfInt3;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.manageChildren(mTag, mIndicesToRemove, mViewsToAdd, mTagsToDelete, mIndicesToDelete);
    }
  }
  
  private final class MeasureInWindowOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final Callback mCallback;
    private final int mReactTag;
    
    private MeasureInWindowOperation(int paramInt, Callback paramCallback)
    {
      mReactTag = paramInt;
      mCallback = paramCallback;
    }
    
    public void execute()
    {
      Object localObject = UIViewOperationQueue.this;
      try
      {
        localObject = mNativeViewHierarchyManager;
        int i = mReactTag;
        UIViewOperationQueue localUIViewOperationQueue = UIViewOperationQueue.this;
        ((NativeViewHierarchyManager)localObject).measureInWindow(i, mMeasureBuffer);
        float f1 = PixelUtil.toDIPFromPixel(mMeasureBuffer[0]);
        float f2 = PixelUtil.toDIPFromPixel(mMeasureBuffer[1]);
        float f3 = PixelUtil.toDIPFromPixel(mMeasureBuffer[2]);
        float f4 = PixelUtil.toDIPFromPixel(mMeasureBuffer[3]);
        mCallback.invoke(new Object[] { Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4) });
        return;
      }
      catch (NoSuchNativeViewException localNoSuchNativeViewException)
      {
        for (;;) {}
      }
      mCallback.invoke(new Object[0]);
    }
  }
  
  private final class MeasureOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final Callback mCallback;
    private final int mReactTag;
    
    private MeasureOperation(int paramInt, Callback paramCallback)
    {
      mReactTag = paramInt;
      mCallback = paramCallback;
    }
    
    public void execute()
    {
      Object localObject = UIViewOperationQueue.this;
      try
      {
        localObject = mNativeViewHierarchyManager;
        int i = mReactTag;
        UIViewOperationQueue localUIViewOperationQueue = UIViewOperationQueue.this;
        ((NativeViewHierarchyManager)localObject).measure(i, mMeasureBuffer);
        float f1 = PixelUtil.toDIPFromPixel(mMeasureBuffer[0]);
        float f2 = PixelUtil.toDIPFromPixel(mMeasureBuffer[1]);
        float f3 = PixelUtil.toDIPFromPixel(mMeasureBuffer[2]);
        float f4 = PixelUtil.toDIPFromPixel(mMeasureBuffer[3]);
        mCallback.invoke(new Object[] { Integer.valueOf(0), Integer.valueOf(0), Float.valueOf(f3), Float.valueOf(f4), Float.valueOf(f1), Float.valueOf(f2) });
        return;
      }
      catch (NoSuchNativeViewException localNoSuchNativeViewException)
      {
        for (;;) {}
      }
      mCallback.invoke(new Object[0]);
    }
  }
  
  private final class RemoveRootViewOperation
    extends UIViewOperationQueue.ViewOperation
  {
    public RemoveRootViewOperation(int paramInt)
    {
      super(paramInt);
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.removeRootView(mTag);
    }
  }
  
  private final class SendAccessibilityEvent
    extends UIViewOperationQueue.ViewOperation
  {
    private final int mEventType;
    
    private SendAccessibilityEvent(int paramInt1, int paramInt2)
    {
      super(paramInt1);
      mEventType = paramInt2;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.sendAccessibilityEvent(mTag, mEventType);
    }
  }
  
  private final class SetChildrenOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final ReadableArray mChildrenTags;
    
    public SetChildrenOperation(int paramInt, ReadableArray paramReadableArray)
    {
      super(paramInt);
      mChildrenTags = paramReadableArray;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.setChildren(mTag, mChildrenTags);
    }
  }
  
  private class SetLayoutAnimationEnabledOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final boolean mEnabled;
    
    private SetLayoutAnimationEnabledOperation(boolean paramBoolean)
    {
      mEnabled = paramBoolean;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.setLayoutAnimationEnabled(mEnabled);
    }
  }
  
  private final class ShowPopupMenuOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final Callback mError;
    private final ReadableArray mItems;
    private final Callback mSuccess;
    
    public ShowPopupMenuOperation(int paramInt, ReadableArray paramReadableArray, Callback paramCallback1, Callback paramCallback2)
    {
      super(paramInt);
      mItems = paramReadableArray;
      mError = paramCallback1;
      mSuccess = paramCallback2;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.showPopupMenu(mTag, mItems, mSuccess, mError);
    }
  }
  
  private class UIBlockOperation
    implements UIViewOperationQueue.UIOperation
  {
    private final UIBlock mBlock;
    
    public UIBlockOperation(UIBlock paramUIBlock)
    {
      mBlock = paramUIBlock;
    }
    
    public void execute()
    {
      mBlock.execute(mNativeViewHierarchyManager);
    }
  }
  
  public static abstract interface UIOperation
  {
    public abstract void execute();
  }
  
  private final class UpdateInstanceHandleOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final long mInstanceHandle;
    
    private UpdateInstanceHandleOperation(int paramInt, long paramLong)
    {
      super(paramInt);
      mInstanceHandle = paramLong;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.updateInstanceHandle(mTag, mInstanceHandle);
    }
  }
  
  private final class UpdateLayoutOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final int mHeight;
    private final int mParentTag;
    private final int mWidth;
    private final int mX;
    private final int mY;
    
    public UpdateLayoutOperation(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      super(paramInt2);
      mParentTag = paramInt1;
      mX = paramInt3;
      mY = paramInt4;
      mWidth = paramInt5;
      mHeight = paramInt6;
      Systrace.startAsyncFlow(0L, "updateLayout", mTag);
    }
    
    public void execute()
    {
      Systrace.endAsyncFlow(0L, "updateLayout", mTag);
      mNativeViewHierarchyManager.updateLayout(mParentTag, mTag, mX, mY, mWidth, mHeight);
    }
  }
  
  private final class UpdatePropertiesOperation
    extends UIViewOperationQueue.ViewOperation
  {
    private final ReactStylesDiffMap mProps;
    
    private UpdatePropertiesOperation(int paramInt, ReactStylesDiffMap paramReactStylesDiffMap)
    {
      super(paramInt);
      mProps = paramReactStylesDiffMap;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.updateProperties(mTag, mProps);
    }
  }
  
  private final class UpdateViewExtraData
    extends UIViewOperationQueue.ViewOperation
  {
    private final Object mExtraData;
    
    public UpdateViewExtraData(int paramInt, Object paramObject)
    {
      super(paramInt);
      mExtraData = paramObject;
    }
    
    public void execute()
    {
      mNativeViewHierarchyManager.updateViewExtraData(mTag, mExtraData);
    }
  }
  
  private abstract class ViewOperation
    implements UIViewOperationQueue.UIOperation
  {
    public int mTag;
    
    public ViewOperation(int paramInt)
    {
      mTag = paramInt;
    }
  }
}
