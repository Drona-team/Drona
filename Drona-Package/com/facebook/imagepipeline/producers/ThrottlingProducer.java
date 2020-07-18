package com.facebook.imagepipeline.producers;

import android.util.Pair;
import com.facebook.common.internal.Preconditions;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

public class ThrottlingProducer<T>
  implements Producer<T>
{
  public static final String PRODUCER_NAME = "ThrottlingProducer";
  private final Executor mExecutor;
  private final Producer<T> mInputProducer;
  private final int mMaxSimultaneousRequests;
  @GuardedBy("this")
  private int mNumCurrentRequests;
  @GuardedBy("this")
  private final ConcurrentLinkedQueue<Pair<Consumer<T>, ProducerContext>> mPendingRequests;
  
  public ThrottlingProducer(int paramInt, Executor paramExecutor, Producer paramProducer)
  {
    mMaxSimultaneousRequests = paramInt;
    mExecutor = ((Executor)Preconditions.checkNotNull(paramExecutor));
    mInputProducer = ((Producer)Preconditions.checkNotNull(paramProducer));
    mPendingRequests = new ConcurrentLinkedQueue();
    mNumCurrentRequests = 0;
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    paramProducerContext.getListener().onProducerStart(paramProducerContext.getId(), "ThrottlingProducer");
    try
    {
      int j = mNumCurrentRequests;
      int k = mMaxSimultaneousRequests;
      int i = 1;
      if (j >= k)
      {
        mPendingRequests.add(Pair.create(paramConsumer, paramProducerContext));
      }
      else
      {
        mNumCurrentRequests += 1;
        i = 0;
      }
      if (i == 0)
      {
        produceResultsInternal(paramConsumer, paramProducerContext);
        return;
      }
    }
    catch (Throwable paramConsumer)
    {
      throw paramConsumer;
    }
  }
  
  void produceResultsInternal(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    paramProducerContext.getListener().onProducerFinishWithSuccess(paramProducerContext.getId(), "ThrottlingProducer", null);
    mInputProducer.produceResults(new ThrottlerConsumer(paramConsumer, null), paramProducerContext);
  }
  
  private class ThrottlerConsumer
    extends DelegatingConsumer<T, T>
  {
    private ThrottlerConsumer(Consumer paramConsumer)
    {
      super();
    }
    
    private void onRequestFinished()
    {
      ThrottlingProducer localThrottlingProducer = ThrottlingProducer.this;
      try
      {
        final Pair localPair = (Pair)mPendingRequests.poll();
        if (localPair == null) {
          ThrottlingProducer.access$210(ThrottlingProducer.this);
        }
        if (localPair != null)
        {
          mExecutor.execute(new Runnable()
          {
            public void run()
            {
              produceResultsInternal((Consumer)localPairfirst, (ProducerContext)localPairsecond);
            }
          });
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    protected void onCancellationImpl()
    {
      getConsumer().onCancellation();
      onRequestFinished();
    }
    
    protected void onFailureImpl(Throwable paramThrowable)
    {
      getConsumer().onFailure(paramThrowable);
      onRequestFinished();
    }
    
    protected void onNewResultImpl(Object paramObject, int paramInt)
    {
      getConsumer().onNewResult(paramObject, paramInt);
      if (BaseConsumer.isLast(paramInt)) {
        onRequestFinished();
      }
    }
  }
}
