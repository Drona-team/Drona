package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.Preconditions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;

public class ThumbnailBranchProducer
  implements Producer<EncodedImage>
{
  private final ThumbnailProducer<EncodedImage>[] mThumbnailProducers;
  
  public ThumbnailBranchProducer(ThumbnailProducer... paramVarArgs)
  {
    mThumbnailProducers = ((ThumbnailProducer[])Preconditions.checkNotNull(paramVarArgs));
    Preconditions.checkElementIndex(0, mThumbnailProducers.length);
  }
  
  private int findFirstProducerForSize(int paramInt, ResizeOptions paramResizeOptions)
  {
    while (paramInt < mThumbnailProducers.length)
    {
      if (mThumbnailProducers[paramInt].canProvideImageForSize(paramResizeOptions)) {
        return paramInt;
      }
      paramInt += 1;
    }
    return -1;
  }
  
  private boolean produceResultsFromThumbnailProducer(int paramInt, Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    paramInt = findFirstProducerForSize(paramInt, paramProducerContext.getImageRequest().getResizeOptions());
    if (paramInt == -1) {
      return false;
    }
    mThumbnailProducers[paramInt].produceResults(new ThumbnailConsumer(paramConsumer, paramProducerContext, paramInt), paramProducerContext);
    return true;
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    if (paramProducerContext.getImageRequest().getResizeOptions() == null)
    {
      paramConsumer.onNewResult(null, 1);
      return;
    }
    if (!produceResultsFromThumbnailProducer(0, paramConsumer, paramProducerContext)) {
      paramConsumer.onNewResult(null, 1);
    }
  }
  
  private class ThumbnailConsumer
    extends DelegatingConsumer<EncodedImage, EncodedImage>
  {
    private final ProducerContext mProducerContext;
    private final int mProducerIndex;
    private final ResizeOptions mResizeOptions;
    
    public ThumbnailConsumer(Consumer paramConsumer, ProducerContext paramProducerContext, int paramInt)
    {
      super();
      mProducerContext = paramProducerContext;
      mProducerIndex = paramInt;
      mResizeOptions = mProducerContext.getImageRequest().getResizeOptions();
    }
    
    protected void onFailureImpl(Throwable paramThrowable)
    {
      if (!ThumbnailBranchProducer.this.produceResultsFromThumbnailProducer(mProducerIndex + 1, getConsumer(), mProducerContext)) {
        getConsumer().onFailure(paramThrowable);
      }
    }
    
    protected void onNewResultImpl(EncodedImage paramEncodedImage, int paramInt)
    {
      if ((paramEncodedImage != null) && ((BaseConsumer.isNotLast(paramInt)) || (ThumbnailSizeChecker.isImageBigEnough(paramEncodedImage, mResizeOptions))))
      {
        getConsumer().onNewResult(paramEncodedImage, paramInt);
        return;
      }
      if (BaseConsumer.isLast(paramInt))
      {
        EncodedImage.closeSafely(paramEncodedImage);
        if (!ThumbnailBranchProducer.this.produceResultsFromThumbnailProducer(mProducerIndex + 1, getConsumer(), mProducerContext)) {
          getConsumer().onNewResult(null, 1);
        }
      }
    }
  }
}
