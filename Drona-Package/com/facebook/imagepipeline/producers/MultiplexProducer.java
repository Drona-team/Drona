package com.facebook.imagepipeline.producers;

import android.util.Pair;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class MultiplexProducer<K, T extends Closeable>
  implements Producer<T>
{
  private final Producer<T> mInputProducer;
  @VisibleForTesting
  @GuardedBy("this")
  final Map<K, MultiplexProducer<K, T>.Multiplexer> mMultiplexers;
  
  protected MultiplexProducer(Producer paramProducer)
  {
    mInputProducer = paramProducer;
    mMultiplexers = new HashMap();
  }
  
  private Multiplexer createAndPutNewMultiplexer(Object paramObject)
  {
    try
    {
      Multiplexer localMultiplexer = new Multiplexer(paramObject);
      mMultiplexers.put(paramObject, localMultiplexer);
      return localMultiplexer;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  private Multiplexer getExistingMultiplexer(Object paramObject)
  {
    try
    {
      paramObject = (Multiplexer)mMultiplexers.get(paramObject);
      return paramObject;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  private void removeMultiplexer(Object paramObject, Multiplexer paramMultiplexer)
  {
    try
    {
      if (mMultiplexers.get(paramObject) == paramMultiplexer) {
        mMultiplexers.remove(paramObject);
      }
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  protected abstract Closeable cloneOrNull(Closeable paramCloseable);
  
  protected abstract Object getKey(ProducerContext paramProducerContext);
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("MultiplexProducer#produceResults");
      }
      Object localObject = getKey(paramProducerContext);
      for (;;)
      {
        int i = 0;
        try
        {
          Multiplexer localMultiplexer2 = getExistingMultiplexer(localObject);
          Multiplexer localMultiplexer1 = localMultiplexer2;
          if (localMultiplexer2 == null)
          {
            localMultiplexer1 = createAndPutNewMultiplexer(localObject);
            i = 1;
          }
          bool = localMultiplexer1.addNewConsumer(paramConsumer, paramProducerContext);
          if (bool)
          {
            if (i != 0) {
              localMultiplexer1.startInputProducerIfHasAttachedConsumers();
            }
            if (FrescoSystrace.isTracing())
            {
              FrescoSystrace.endSection();
              return;
            }
          }
        }
        catch (Throwable paramConsumer)
        {
          throw paramConsumer;
        }
      }
      return;
    }
    catch (Throwable paramConsumer)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramConsumer;
    }
  }
  
  @VisibleForTesting
  class Multiplexer
  {
    private final CopyOnWriteArraySet<Pair<Consumer<T>, ProducerContext>> mConsumerContextPairs = Sets.newCopyOnWriteArraySet();
    @Nullable
    @GuardedBy("Multiplexer.this")
    private MultiplexProducer<K, T>.Multiplexer.ForwardingConsumer mForwardingConsumer;
    private final K mKey;
    @Nullable
    @GuardedBy("Multiplexer.this")
    private T mLastIntermediateResult;
    @GuardedBy("Multiplexer.this")
    private float mLastProgress;
    @GuardedBy("Multiplexer.this")
    private int mLastStatus;
    @Nullable
    @GuardedBy("Multiplexer.this")
    private BaseProducerContext mMultiplexProducerContext;
    
    public Multiplexer(Object paramObject)
    {
      mKey = paramObject;
    }
    
    private void addCallbacks(final Pair paramPair, ProducerContext paramProducerContext)
    {
      paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
      {
        public void onCancellationRequested()
        {
          MultiplexProducer.Multiplexer localMultiplexer = MultiplexProducer.Multiplexer.this;
          for (;;)
          {
            try
            {
              boolean bool = mConsumerContextPairs.remove(paramPair);
              List localList1 = null;
              if (!bool) {
                break label137;
              }
              if (mConsumerContextPairs.isEmpty())
              {
                localBaseProducerContext = mMultiplexProducerContext;
                break label139;
              }
              localList1 = MultiplexProducer.Multiplexer.this.updateIsPrefetch();
              localList2 = MultiplexProducer.Multiplexer.this.updatePriority();
              localList3 = MultiplexProducer.Multiplexer.this.updateIsIntermediateResultExpected();
              BaseProducerContext localBaseProducerContext = null;
              BaseProducerContext.callOnIsPrefetchChanged(localList1);
              BaseProducerContext.callOnPriorityChanged(localList2);
              BaseProducerContext.callOnIsIntermediateResultExpectedChanged(localList3);
              if (localBaseProducerContext != null) {
                localBaseProducerContext.cancel();
              }
              if (bool)
              {
                ((Consumer)paramPairfirst).onCancellation();
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              throw localThrowable;
            }
            return;
            label137:
            Object localObject = null;
            label139:
            List localList2 = null;
            List localList3 = null;
          }
        }
        
        public void onIsIntermediateResultExpectedChanged()
        {
          BaseProducerContext.callOnIsIntermediateResultExpectedChanged(MultiplexProducer.Multiplexer.this.updateIsIntermediateResultExpected());
        }
        
        public void onIsPrefetchChanged()
        {
          BaseProducerContext.callOnIsPrefetchChanged(MultiplexProducer.Multiplexer.this.updateIsPrefetch());
        }
        
        public void onPriorityChanged()
        {
          BaseProducerContext.callOnPriorityChanged(MultiplexProducer.Multiplexer.this.updatePriority());
        }
      });
    }
    
    private void closeSafely(Closeable paramCloseable)
    {
      if (paramCloseable != null) {
        try
        {
          paramCloseable.close();
          return;
        }
        catch (IOException paramCloseable)
        {
          throw new RuntimeException(paramCloseable);
        }
      }
    }
    
    private boolean computeIsIntermediateResultExpected()
    {
      try
      {
        Iterator localIterator = mConsumerContextPairs.iterator();
        while (localIterator.hasNext())
        {
          boolean bool = ((ProducerContext)nextsecond).isIntermediateResultExpected();
          if (bool) {
            return true;
          }
        }
        return false;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private boolean computeIsPrefetch()
    {
      try
      {
        Iterator localIterator = mConsumerContextPairs.iterator();
        while (localIterator.hasNext())
        {
          boolean bool = ((ProducerContext)nextsecond).isPrefetch();
          if (!bool) {
            return false;
          }
        }
        return true;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private Priority computePriority()
    {
      try
      {
        Priority localPriority = Priority.LOW;
        Iterator localIterator = mConsumerContextPairs.iterator();
        while (localIterator.hasNext()) {
          localPriority = Priority.getHigherPriority(localPriority, ((ProducerContext)nextsecond).getPriority());
        }
        return localPriority;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void startInputProducerIfHasAttachedConsumers()
    {
      for (;;)
      {
        try
        {
          Object localObject = mMultiplexProducerContext;
          boolean bool2 = false;
          if (localObject == null)
          {
            bool1 = true;
            Preconditions.checkArgument(bool1);
            bool1 = bool2;
            if (mForwardingConsumer == null) {
              bool1 = true;
            }
            Preconditions.checkArgument(bool1);
            if (mConsumerContextPairs.isEmpty())
            {
              MultiplexProducer.this.removeMultiplexer(mKey, this);
              return;
            }
            localObject = (ProducerContext)mConsumerContextPairs.iterator().next()).second;
            mMultiplexProducerContext = new BaseProducerContext(((ProducerContext)localObject).getImageRequest(), ((ProducerContext)localObject).getId(), ((ProducerContext)localObject).getListener(), ((ProducerContext)localObject).getCallerContext(), ((ProducerContext)localObject).getLowestPermittedRequestLevel(), computeIsPrefetch(), computeIsIntermediateResultExpected(), computePriority());
            mForwardingConsumer = new ForwardingConsumer(null);
            localObject = mMultiplexProducerContext;
            ForwardingConsumer localForwardingConsumer = mForwardingConsumer;
            mInputProducer.produceResults(localForwardingConsumer, (ProducerContext)localObject);
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
        boolean bool1 = false;
      }
    }
    
    private List updateIsIntermediateResultExpected()
    {
      try
      {
        Object localObject = mMultiplexProducerContext;
        if (localObject == null) {
          return null;
        }
        localObject = mMultiplexProducerContext.setIsIntermediateResultExpectedNoCallbacks(computeIsIntermediateResultExpected());
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private List updateIsPrefetch()
    {
      try
      {
        Object localObject = mMultiplexProducerContext;
        if (localObject == null) {
          return null;
        }
        localObject = mMultiplexProducerContext.setIsPrefetchNoCallbacks(computeIsPrefetch());
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private List updatePriority()
    {
      try
      {
        Object localObject = mMultiplexProducerContext;
        if (localObject == null) {
          return null;
        }
        localObject = mMultiplexProducerContext.setPriorityNoCallbacks(computePriority());
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    /* Error */
    public boolean addNewConsumer(Consumer paramConsumer, ProducerContext paramProducerContext)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_2
      //   2: invokestatic 225	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
      //   5: astore 7
      //   7: aload_0
      //   8: monitorenter
      //   9: aload_0
      //   10: getfield 41	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:this$0	Lcom/facebook/imagepipeline/producers/MultiplexProducer;
      //   13: aload_0
      //   14: getfield 54	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:mKey	Ljava/lang/Object;
      //   17: invokestatic 229	com/facebook/imagepipeline/producers/MultiplexProducer:access$100	(Lcom/facebook/imagepipeline/producers/MultiplexProducer;Ljava/lang/Object;)Lcom/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer;
      //   20: aload_0
      //   21: if_acmpeq +7 -> 28
      //   24: aload_0
      //   25: monitorexit
      //   26: iconst_0
      //   27: ireturn
      //   28: aload_0
      //   29: getfield 52	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:mConsumerContextPairs	Ljava/util/concurrent/CopyOnWriteArraySet;
      //   32: aload 7
      //   34: invokevirtual 233	java/util/concurrent/CopyOnWriteArraySet:add	(Ljava/lang/Object;)Z
      //   37: pop
      //   38: aload_0
      //   39: invokespecial 72	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:updateIsPrefetch	()Ljava/util/List;
      //   42: astore 5
      //   44: aload_0
      //   45: invokespecial 76	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:updatePriority	()Ljava/util/List;
      //   48: astore 8
      //   50: aload_0
      //   51: invokespecial 80	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:updateIsIntermediateResultExpected	()Ljava/util/List;
      //   54: astore 9
      //   56: aload_0
      //   57: getfield 235	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:mLastIntermediateResult	Ljava/io/Closeable;
      //   60: astore 6
      //   62: aload_0
      //   63: getfield 237	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:mLastProgress	F
      //   66: fstore_3
      //   67: aload_0
      //   68: getfield 239	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:mLastStatus	I
      //   71: istore 4
      //   73: aload_0
      //   74: monitorexit
      //   75: aload 5
      //   77: invokestatic 243	com/facebook/imagepipeline/producers/BaseProducerContext:callOnIsPrefetchChanged	(Ljava/util/List;)V
      //   80: aload 8
      //   82: invokestatic 246	com/facebook/imagepipeline/producers/BaseProducerContext:callOnPriorityChanged	(Ljava/util/List;)V
      //   85: aload 9
      //   87: invokestatic 249	com/facebook/imagepipeline/producers/BaseProducerContext:callOnIsIntermediateResultExpectedChanged	(Ljava/util/List;)V
      //   90: aload 7
      //   92: monitorenter
      //   93: aload_0
      //   94: monitorenter
      //   95: aload 6
      //   97: aload_0
      //   98: getfield 235	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:mLastIntermediateResult	Ljava/io/Closeable;
      //   101: if_acmpeq +9 -> 110
      //   104: aconst_null
      //   105: astore 5
      //   107: goto +23 -> 130
      //   110: aload 6
      //   112: astore 5
      //   114: aload 6
      //   116: ifnull +14 -> 130
      //   119: aload_0
      //   120: getfield 41	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:this$0	Lcom/facebook/imagepipeline/producers/MultiplexProducer;
      //   123: aload 6
      //   125: invokevirtual 253	com/facebook/imagepipeline/producers/MultiplexProducer:cloneOrNull	(Ljava/io/Closeable;)Ljava/io/Closeable;
      //   128: astore 5
      //   130: aload_0
      //   131: monitorexit
      //   132: aload 5
      //   134: ifnull +32 -> 166
      //   137: fload_3
      //   138: fconst_0
      //   139: fcmpl
      //   140: ifle +10 -> 150
      //   143: aload_1
      //   144: fload_3
      //   145: invokeinterface 259 2 0
      //   150: aload_1
      //   151: aload 5
      //   153: iload 4
      //   155: invokeinterface 263 3 0
      //   160: aload_0
      //   161: aload 5
      //   163: invokespecial 265	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:closeSafely	(Ljava/io/Closeable;)V
      //   166: aload 7
      //   168: monitorexit
      //   169: aload_0
      //   170: aload 7
      //   172: aload_2
      //   173: invokespecial 267	com/facebook/imagepipeline/producers/MultiplexProducer$Multiplexer:addCallbacks	(Landroid/util/Pair;Lcom/facebook/imagepipeline/producers/ProducerContext;)V
      //   176: iconst_1
      //   177: ireturn
      //   178: astore_1
      //   179: aload_0
      //   180: monitorexit
      //   181: aload_1
      //   182: athrow
      //   183: astore_1
      //   184: aload 7
      //   186: monitorexit
      //   187: aload_1
      //   188: athrow
      //   189: astore_1
      //   190: aload_0
      //   191: monitorexit
      //   192: aload_1
      //   193: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	194	0	this	Multiplexer
      //   0	194	1	paramConsumer	Consumer
      //   0	194	2	paramProducerContext	ProducerContext
      //   66	79	3	f	float
      //   71	83	4	i	int
      //   42	120	5	localObject	Object
      //   60	64	6	localCloseable	Closeable
      //   5	180	7	localPair	Pair
      //   48	33	8	localList1	List
      //   54	32	9	localList2	List
      // Exception table:
      //   from	to	target	type
      //   95	104	178	java/lang/Throwable
      //   119	130	178	java/lang/Throwable
      //   130	132	178	java/lang/Throwable
      //   179	181	178	java/lang/Throwable
      //   93	95	183	java/lang/Throwable
      //   143	150	183	java/lang/Throwable
      //   150	166	183	java/lang/Throwable
      //   166	169	183	java/lang/Throwable
      //   181	183	183	java/lang/Throwable
      //   184	187	183	java/lang/Throwable
      //   9	26	189	java/lang/Throwable
      //   28	75	189	java/lang/Throwable
      //   190	192	189	java/lang/Throwable
    }
    
    public void onCancelled(ForwardingConsumer paramForwardingConsumer)
    {
      try
      {
        if (mForwardingConsumer != paramForwardingConsumer) {
          return;
        }
        mForwardingConsumer = null;
        mMultiplexProducerContext = null;
        closeSafely(mLastIntermediateResult);
        mLastIntermediateResult = null;
        startInputProducerIfHasAttachedConsumers();
        return;
      }
      catch (Throwable paramForwardingConsumer)
      {
        throw paramForwardingConsumer;
      }
    }
    
    public void onFailure(ForwardingConsumer paramForwardingConsumer, Throwable paramThrowable)
    {
      try
      {
        if (mForwardingConsumer != paramForwardingConsumer) {
          return;
        }
        Iterator localIterator = mConsumerContextPairs.iterator();
        mConsumerContextPairs.clear();
        MultiplexProducer.this.removeMultiplexer(mKey, this);
        closeSafely(mLastIntermediateResult);
        mLastIntermediateResult = null;
        for (;;)
        {
          if (localIterator.hasNext())
          {
            paramForwardingConsumer = (Pair)localIterator.next();
            try
            {
              ((Consumer)first).onFailure(paramThrowable);
            }
            catch (Throwable paramThrowable)
            {
              throw paramThrowable;
            }
          }
        }
        return;
      }
      catch (Throwable paramForwardingConsumer)
      {
        throw paramForwardingConsumer;
      }
    }
    
    public void onNextResult(ForwardingConsumer paramForwardingConsumer, Closeable paramCloseable, int paramInt)
    {
      try
      {
        if (mForwardingConsumer != paramForwardingConsumer) {
          return;
        }
        closeSafely(mLastIntermediateResult);
        mLastIntermediateResult = null;
        Iterator localIterator = mConsumerContextPairs.iterator();
        if (BaseConsumer.isNotLast(paramInt))
        {
          mLastIntermediateResult = cloneOrNull(paramCloseable);
          mLastStatus = paramInt;
        }
        else
        {
          mConsumerContextPairs.clear();
          MultiplexProducer.this.removeMultiplexer(mKey, this);
        }
        for (;;)
        {
          if (localIterator.hasNext())
          {
            paramForwardingConsumer = (Pair)localIterator.next();
            try
            {
              ((Consumer)first).onNewResult(paramCloseable, paramInt);
            }
            catch (Throwable paramCloseable)
            {
              throw paramCloseable;
            }
          }
        }
        return;
      }
      catch (Throwable paramForwardingConsumer)
      {
        throw paramForwardingConsumer;
      }
    }
    
    public void onProgressUpdate(ForwardingConsumer paramForwardingConsumer, float paramFloat)
    {
      try
      {
        if (mForwardingConsumer != paramForwardingConsumer) {
          return;
        }
        mLastProgress = paramFloat;
        Iterator localIterator = mConsumerContextPairs.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            paramForwardingConsumer = (Pair)localIterator.next();
            try
            {
              ((Consumer)first).onProgressUpdate(paramFloat);
            }
            catch (Throwable localThrowable)
            {
              throw localThrowable;
            }
          }
        }
        return;
      }
      catch (Throwable paramForwardingConsumer)
      {
        throw paramForwardingConsumer;
      }
    }
    
    private class ForwardingConsumer
      extends BaseConsumer<T>
    {
      private ForwardingConsumer() {}
      
      protected void onCancellationImpl()
      {
        try
        {
          boolean bool = FrescoSystrace.isTracing();
          if (bool) {
            FrescoSystrace.beginSection("MultiplexProducer#onCancellation");
          }
          onCancelled(this);
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
          throw localThrowable;
        }
      }
      
      protected void onFailureImpl(Throwable paramThrowable)
      {
        try
        {
          boolean bool = FrescoSystrace.isTracing();
          if (bool) {
            FrescoSystrace.beginSection("MultiplexProducer#onFailure");
          }
          onFailure(this, paramThrowable);
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return;
          }
        }
        catch (Throwable paramThrowable)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
          throw paramThrowable;
        }
      }
      
      protected void onNewResultImpl(Closeable paramCloseable, int paramInt)
      {
        try
        {
          boolean bool = FrescoSystrace.isTracing();
          if (bool) {
            FrescoSystrace.beginSection("MultiplexProducer#onNewResult");
          }
          onNextResult(this, paramCloseable, paramInt);
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return;
          }
        }
        catch (Throwable paramCloseable)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
          throw paramCloseable;
        }
      }
      
      protected void onProgressUpdateImpl(float paramFloat)
      {
        try
        {
          boolean bool = FrescoSystrace.isTracing();
          if (bool) {
            FrescoSystrace.beginSection("MultiplexProducer#onProgressUpdate");
          }
          onProgressUpdate(this, paramFloat);
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
          throw localThrowable;
        }
      }
    }
  }
}
