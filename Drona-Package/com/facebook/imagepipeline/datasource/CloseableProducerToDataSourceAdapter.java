package com.facebook.imagepipeline.datasource;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.AbstractDataSource;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CloseableProducerToDataSourceAdapter<T>
  extends AbstractProducerToDataSourceAdapter<CloseableReference<T>>
{
  private CloseableProducerToDataSourceAdapter(Producer paramProducer, SettableProducerContext paramSettableProducerContext, RequestListener paramRequestListener)
  {
    super(paramProducer, paramSettableProducerContext, paramRequestListener);
  }
  
  public static DataSource create(Producer paramProducer, SettableProducerContext paramSettableProducerContext, RequestListener paramRequestListener)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("CloseableProducerToDataSourceAdapter#create");
    }
    paramProducer = new CloseableProducerToDataSourceAdapter(paramProducer, paramSettableProducerContext, paramRequestListener);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return paramProducer;
  }
  
  protected void closeResult(CloseableReference paramCloseableReference)
  {
    CloseableReference.closeSafely(paramCloseableReference);
  }
  
  public CloseableReference getResult()
  {
    return CloseableReference.cloneOrNull((CloseableReference)super.getResult());
  }
  
  protected void onNewResultImpl(CloseableReference paramCloseableReference, int paramInt)
  {
    super.onNewResultImpl(CloseableReference.cloneOrNull(paramCloseableReference), paramInt);
  }
}
