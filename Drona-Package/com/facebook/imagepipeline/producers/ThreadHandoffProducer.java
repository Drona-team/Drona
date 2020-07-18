package com.facebook.imagepipeline.producers;

import com.facebook.common.executors.StatefulRunnable;
import com.facebook.common.internal.Preconditions;

public class ThreadHandoffProducer<T>
  implements Producer<T>
{
  public static final String PRODUCER_NAME = "BackgroundThreadHandoffProducer";
  private final Producer<T> mInputProducer;
  private final ThreadHandoffProducerQueue mThreadHandoffProducerQueue;
  
  public ThreadHandoffProducer(Producer paramProducer, ThreadHandoffProducerQueue paramThreadHandoffProducerQueue)
  {
    mInputProducer = ((Producer)Preconditions.checkNotNull(paramProducer));
    mThreadHandoffProducerQueue = paramThreadHandoffProducerQueue;
  }
  
  public void produceResults(final Consumer paramConsumer, final ProducerContext paramProducerContext)
  {
    final ProducerListener localProducerListener = paramProducerContext.getListener();
    final String str = paramProducerContext.getId();
    paramConsumer = new StatefulProducerRunnable(paramConsumer, localProducerListener, "BackgroundThreadHandoffProducer", str)
    {
      protected void disposeResult(Object paramAnonymousObject) {}
      
      protected Object getResult()
        throws Exception
      {
        return null;
      }
      
      protected void onSuccess(Object paramAnonymousObject)
      {
        localProducerListener.onProducerFinishWithSuccess(str, "BackgroundThreadHandoffProducer", null);
        mInputProducer.produceResults(paramConsumer, paramProducerContext);
      }
    };
    paramProducerContext.addCallbacks(new BaseProducerContextCallbacks()
    {
      public void onCancellationRequested()
      {
        paramConsumer.cancel();
        mThreadHandoffProducerQueue.remove(paramConsumer);
      }
    });
    mThreadHandoffProducerQueue.addToQueueOrExecute(paramConsumer);
  }
}
