package com.facebook.imagepipeline.datasource;

import com.facebook.common.internal.Preconditions;
import com.facebook.datasource.AbstractDataSource;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.producers.BaseConsumer;
import com.facebook.imagepipeline.producers.BaseProducerContext;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import com.facebook.imagepipeline.request.HasImageRequest;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class AbstractProducerToDataSourceAdapter<T>
  extends AbstractDataSource<T>
  implements HasImageRequest
{
  private final RequestListener mRequestListener;
  private final SettableProducerContext mSettableProducerContext;
  
  protected AbstractProducerToDataSourceAdapter(Producer paramProducer, SettableProducerContext paramSettableProducerContext, RequestListener paramRequestListener)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractProducerToDataSourceAdapter()");
    }
    mSettableProducerContext = paramSettableProducerContext;
    mRequestListener = paramRequestListener;
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractProducerToDataSourceAdapter()->onRequestStart");
    }
    mRequestListener.onRequestStart(paramSettableProducerContext.getImageRequest(), mSettableProducerContext.getCallerContext(), mSettableProducerContext.getId(), mSettableProducerContext.isPrefetch());
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("AbstractProducerToDataSourceAdapter()->produceResult");
    }
    paramProducer.produceResults(createConsumer(), paramSettableProducerContext);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  private Consumer createConsumer()
  {
    new BaseConsumer()
    {
      protected void onCancellationImpl()
      {
        AbstractProducerToDataSourceAdapter.this.onCancellationImpl();
      }
      
      protected void onFailureImpl(Throwable paramAnonymousThrowable)
      {
        AbstractProducerToDataSourceAdapter.this.onFailureImpl(paramAnonymousThrowable);
      }
      
      protected void onNewResultImpl(Object paramAnonymousObject, int paramAnonymousInt)
      {
        AbstractProducerToDataSourceAdapter.this.onNewResultImpl(paramAnonymousObject, paramAnonymousInt);
      }
      
      protected void onProgressUpdateImpl(float paramAnonymousFloat)
      {
        setProgress(paramAnonymousFloat);
      }
    };
  }
  
  private void onCancellationImpl()
  {
    try
    {
      Preconditions.checkState(isClosed());
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void onFailureImpl(Throwable paramThrowable)
  {
    if (super.setFailure(paramThrowable)) {
      mRequestListener.onRequestFailure(mSettableProducerContext.getImageRequest(), mSettableProducerContext.getId(), paramThrowable, mSettableProducerContext.isPrefetch());
    }
  }
  
  public boolean close()
  {
    if (!super.close()) {
      return false;
    }
    if (!super.isFinished())
    {
      mRequestListener.onRequestCancellation(mSettableProducerContext.getId());
      mSettableProducerContext.cancel();
    }
    return true;
  }
  
  public ImageRequest getImageRequest()
  {
    return mSettableProducerContext.getImageRequest();
  }
  
  protected void onNewResultImpl(Object paramObject, int paramInt)
  {
    boolean bool = BaseConsumer.isLast(paramInt);
    if ((super.setResult(paramObject, bool)) && (bool)) {
      mRequestListener.onRequestSuccess(mSettableProducerContext.getImageRequest(), mSettableProducerContext.getId(), mSettableProducerContext.isPrefetch());
    }
  }
}