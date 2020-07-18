package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;

public class BranchOnSeparateImagesProducer
  implements Producer<EncodedImage>
{
  private final Producer<EncodedImage> mInputProducer1;
  private final Producer<EncodedImage> mInputProducer2;
  
  public BranchOnSeparateImagesProducer(Producer paramProducer1, Producer paramProducer2)
  {
    mInputProducer1 = paramProducer1;
    mInputProducer2 = paramProducer2;
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    paramConsumer = new OnFirstImageConsumer(paramConsumer, paramProducerContext, null);
    mInputProducer1.produceResults(paramConsumer, paramProducerContext);
  }
  
  private class OnFirstImageConsumer
    extends DelegatingConsumer<EncodedImage, EncodedImage>
  {
    private ProducerContext mProducerContext;
    
    private OnFirstImageConsumer(Consumer paramConsumer, ProducerContext paramProducerContext)
    {
      super();
      mProducerContext = paramProducerContext;
    }
    
    protected void onFailureImpl(Throwable paramThrowable)
    {
      mInputProducer2.produceResults(getConsumer(), mProducerContext);
    }
    
    protected void onNewResultImpl(EncodedImage paramEncodedImage, int paramInt)
    {
      ImageRequest localImageRequest = mProducerContext.getImageRequest();
      boolean bool1 = BaseConsumer.isLast(paramInt);
      boolean bool2 = ThumbnailSizeChecker.isImageBigEnough(paramEncodedImage, localImageRequest.getResizeOptions());
      if ((paramEncodedImage != null) && ((bool2) || (localImageRequest.getLocalThumbnailPreviewsEnabled()))) {
        if ((bool1) && (bool2))
        {
          getConsumer().onNewResult(paramEncodedImage, paramInt);
        }
        else
        {
          paramInt = BaseConsumer.turnOffStatusFlag(paramInt, 1);
          getConsumer().onNewResult(paramEncodedImage, paramInt);
        }
      }
      if ((bool1) && (!bool2))
      {
        EncodedImage.closeSafely(paramEncodedImage);
        mInputProducer2.produceResults(getConsumer(), mProducerContext);
      }
    }
  }
}
