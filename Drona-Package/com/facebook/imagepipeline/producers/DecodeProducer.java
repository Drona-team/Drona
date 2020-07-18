package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ExceptionWithNoStacktrace;
import com.facebook.common.util.UriUtil;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.CloseableReferenceFactory;
import com.facebook.imagepipeline.decoder.DecodeException;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegParser;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.DownsampleUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

public class DecodeProducer
  implements Producer<CloseableReference<CloseableImage>>
{
  public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
  public static final String EXTRA_BITMAP_SIZE = "bitmapSize";
  public static final String EXTRA_HAS_GOOD_QUALITY = "hasGoodQuality";
  public static final String EXTRA_IMAGE_FORMAT_NAME = "imageFormat";
  public static final String EXTRA_IS_FINAL = "isFinal";
  public static final String PRODUCER_NAME = "DecodeProducer";
  public static final String REQUESTED_IMAGE_SIZE = "requestedImageSize";
  public static final String SAMPLE_SIZE = "sampleSize";
  private final ByteArrayPool mByteArrayPool;
  private final CloseableReferenceFactory mCloseableReferenceFactory;
  private final boolean mDecodeCancellationEnabled;
  private final boolean mDownsampleEnabled;
  private final boolean mDownsampleEnabledForNetwork;
  private final Executor mExecutor;
  private final ImageDecoder mImageDecoder;
  private final Producer<EncodedImage> mInputProducer;
  private final int mMaxBitmapSize;
  private final ProgressiveJpegConfig mProgressiveJpegConfig;
  
  public DecodeProducer(ByteArrayPool paramByteArrayPool, Executor paramExecutor, ImageDecoder paramImageDecoder, ProgressiveJpegConfig paramProgressiveJpegConfig, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Producer paramProducer, int paramInt, CloseableReferenceFactory paramCloseableReferenceFactory)
  {
    mByteArrayPool = ((ByteArrayPool)Preconditions.checkNotNull(paramByteArrayPool));
    mExecutor = ((Executor)Preconditions.checkNotNull(paramExecutor));
    mImageDecoder = ((ImageDecoder)Preconditions.checkNotNull(paramImageDecoder));
    mProgressiveJpegConfig = ((ProgressiveJpegConfig)Preconditions.checkNotNull(paramProgressiveJpegConfig));
    mDownsampleEnabled = paramBoolean1;
    mDownsampleEnabledForNetwork = paramBoolean2;
    mInputProducer = ((Producer)Preconditions.checkNotNull(paramProducer));
    mDecodeCancellationEnabled = paramBoolean3;
    mMaxBitmapSize = paramInt;
    mCloseableReferenceFactory = paramCloseableReferenceFactory;
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a6 = a5\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private class LocalImagesProgressiveDecoder
    extends DecodeProducer.ProgressiveDecoder
  {
    public LocalImagesProgressiveDecoder(Consumer paramConsumer, ProducerContext paramProducerContext, boolean paramBoolean, int paramInt)
    {
      super(paramConsumer, paramProducerContext, paramBoolean, paramInt);
    }
    
    protected int getIntermediateImageEndOffset(EncodedImage paramEncodedImage)
    {
      return paramEncodedImage.getSize();
    }
    
    protected QualityInfo getQualityInfo()
    {
      return ImmutableQualityInfo.refresh(0, false, false);
    }
    
    protected boolean updateDecodeJob(EncodedImage paramEncodedImage, int paramInt)
    {
      try
      {
        boolean bool = BaseConsumer.isNotLast(paramInt);
        if (bool) {
          return false;
        }
        bool = super.updateDecodeJob(paramEncodedImage, paramInt);
        return bool;
      }
      catch (Throwable paramEncodedImage)
      {
        throw paramEncodedImage;
      }
    }
  }
  
  private class NetworkImagesProgressiveDecoder
    extends DecodeProducer.ProgressiveDecoder
  {
    private int mLastScheduledScanNumber;
    private final ProgressiveJpegConfig mProgressiveJpegConfig;
    private final ProgressiveJpegParser mProgressiveJpegParser;
    
    public NetworkImagesProgressiveDecoder(Consumer paramConsumer, ProducerContext paramProducerContext, ProgressiveJpegParser paramProgressiveJpegParser, ProgressiveJpegConfig paramProgressiveJpegConfig, boolean paramBoolean, int paramInt)
    {
      super(paramConsumer, paramProducerContext, paramBoolean, paramInt);
      mProgressiveJpegParser = ((ProgressiveJpegParser)Preconditions.checkNotNull(paramProgressiveJpegParser));
      mProgressiveJpegConfig = ((ProgressiveJpegConfig)Preconditions.checkNotNull(paramProgressiveJpegConfig));
      mLastScheduledScanNumber = 0;
    }
    
    protected int getIntermediateImageEndOffset(EncodedImage paramEncodedImage)
    {
      return mProgressiveJpegParser.getBestScanEndOffset();
    }
    
    protected QualityInfo getQualityInfo()
    {
      return mProgressiveJpegConfig.getQualityInfo(mProgressiveJpegParser.getBestScanNumber());
    }
    
    protected boolean updateDecodeJob(EncodedImage paramEncodedImage, int paramInt)
    {
      try
      {
        boolean bool1 = super.updateDecodeJob(paramEncodedImage, paramInt);
        if (((BaseConsumer.isNotLast(paramInt)) || (BaseConsumer.statusHasFlag(paramInt, 8))) && (!BaseConsumer.statusHasFlag(paramInt, 4)) && (EncodedImage.isValid(paramEncodedImage)) && (paramEncodedImage.getImageFormat() == DefaultImageFormats.JPEG))
        {
          boolean bool2 = mProgressiveJpegParser.parseMoreData(paramEncodedImage);
          if (!bool2) {
            return false;
          }
          paramInt = mProgressiveJpegParser.getBestScanNumber();
          int i = mLastScheduledScanNumber;
          if (paramInt <= i) {
            return false;
          }
          if (paramInt < mProgressiveJpegConfig.getNextScanNumberToDecode(mLastScheduledScanNumber))
          {
            bool2 = mProgressiveJpegParser.isEndMarkerRead();
            if (!bool2) {
              return false;
            }
          }
          mLastScheduledScanNumber = paramInt;
        }
        return bool1;
      }
      catch (Throwable paramEncodedImage)
      {
        throw paramEncodedImage;
      }
    }
  }
  
  private abstract class ProgressiveDecoder
    extends DelegatingConsumer<EncodedImage, CloseableReference<CloseableImage>>
  {
    private static final int DECODE_EXCEPTION_MESSAGE_NUM_HEADER_BYTES = 10;
    private final String engineName = "ProgressiveDecoder";
    private final ImageDecodeOptions mImageDecodeOptions;
    @GuardedBy("this")
    private boolean mIsFinished;
    private final JobScheduler mJobScheduler;
    private final ProducerContext mProducerContext;
    private final ProducerListener mProducerListener;
    
    public ProgressiveDecoder(Consumer paramConsumer, final ProducerContext paramProducerContext, final boolean paramBoolean, final int paramInt)
    {
      super();
      mProducerContext = paramProducerContext;
      mProducerListener = paramProducerContext.getListener();
      mImageDecodeOptions = paramProducerContext.getImageRequest().getImageDecodeOptions();
      mIsFinished = false;
      paramConsumer = new JobScheduler.JobRunnable()
      {
        public void remainder(EncodedImage paramAnonymousEncodedImage, int paramAnonymousInt)
        {
          if (paramAnonymousEncodedImage != null)
          {
            if ((mDownsampleEnabled) || (!BaseConsumer.statusHasFlag(paramAnonymousInt, 16)))
            {
              ImageRequest localImageRequest = paramProducerContext.getImageRequest();
              if ((mDownsampleEnabledForNetwork) || (!UriUtil.isNetworkUri(localImageRequest.getSourceUri()))) {
                paramAnonymousEncodedImage.setSampleSize(DownsampleUtil.determineSampleSize(localImageRequest.getRotationOptions(), localImageRequest.getResizeOptions(), paramAnonymousEncodedImage, paramInt));
              }
            }
            DecodeProducer.ProgressiveDecoder.this.doDecode(paramAnonymousEncodedImage, paramAnonymousInt);
          }
        }
      };
      mJobScheduler = new JobScheduler(mExecutor, paramConsumer, mImageDecodeOptions.minDecodeIntervalMs);
      mProducerContext.addCallbacks(new BaseProducerContextCallbacks()
      {
        public void onCancellationRequested()
        {
          if (paramBoolean) {
            DecodeProducer.ProgressiveDecoder.this.handleCancellation();
          }
        }
        
        public void onIsIntermediateResultExpectedChanged()
        {
          if (mProducerContext.isIntermediateResultExpected()) {
            mJobScheduler.scheduleJob();
          }
        }
      });
    }
    
    private void doDecode(EncodedImage paramEncodedImage, int paramInt)
    {
      int i = paramInt;
      if ((paramEncodedImage.getImageFormat() != DefaultImageFormats.JPEG) && (BaseConsumer.isNotLast(paramInt))) {
        return;
      }
      if (!isFinished())
      {
        if (!EncodedImage.isValid(paramEncodedImage)) {
          return;
        }
        Object localObject1 = paramEncodedImage.getImageFormat();
        if (localObject1 != null) {}
        for (localObject1 = ((ImageFormat)localObject1).getName();; localObject1 = "unknown") {
          break;
        }
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(paramEncodedImage.getWidth());
        ((StringBuilder)localObject2).append("x");
        ((StringBuilder)localObject2).append(paramEncodedImage.getHeight());
        String str2 = ((StringBuilder)localObject2).toString();
        String str3 = String.valueOf(paramEncodedImage.getSampleSize());
        boolean bool1 = BaseConsumer.isLast(paramInt);
        int j;
        if ((bool1) && (!BaseConsumer.statusHasFlag(paramInt, 8))) {
          j = 1;
        } else {
          j = 0;
        }
        boolean bool2 = BaseConsumer.statusHasFlag(paramInt, 4);
        localObject2 = mProducerContext.getImageRequest().getResizeOptions();
        Object localObject3;
        if (localObject2 != null)
        {
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append(width);
          ((StringBuilder)localObject3).append("x");
          ((StringBuilder)localObject3).append(height);
          localObject2 = ((StringBuilder)localObject3).toString();
        }
        else
        {
          localObject2 = "unknown";
        }
        try
        {
          long l = mJobScheduler.getQueuedTime();
          String str1 = String.valueOf(mProducerContext.getImageRequest().getSourceUri());
          int k;
          if ((j == 0) && (!bool2)) {
            k = getIntermediateImageEndOffset(paramEncodedImage);
          } else {
            k = paramEncodedImage.getSize();
          }
          if ((j == 0) && (!bool2)) {}
          for (localObject3 = getQualityInfo();; localObject3 = ImmutableQualityInfo.FULL_QUALITY) {
            break;
          }
          mProducerListener.onProducerStart(mProducerContext.getId(), "DecodeProducer");
          Object localObject4 = DecodeProducer.this;
          try
          {
            localObject4 = mImageDecoder;
            Object localObject5 = mImageDecodeOptions;
            localObject4 = ((ImageDecoder)localObject4).decode(paramEncodedImage, k, (QualityInfo)localObject3, (ImageDecodeOptions)localObject5);
            try
            {
              j = paramEncodedImage.getSampleSize();
              if (j != 1) {
                i = paramInt | 0x10;
              }
              localObject1 = getExtraMap((CloseableImage)localObject4, l, (QualityInfo)localObject3, bool1, (String)localObject1, str2, (String)localObject2, str3);
              mProducerListener.onProducerFinishWithSuccess(mProducerContext.getId(), "DecodeProducer", (Map)localObject1);
              handleResult((CloseableImage)localObject4, i);
              EncodedImage.closeSafely(paramEncodedImage);
              return;
            }
            catch (Exception localException1) {}
            String str4;
            String str5;
            localObject1 = getExtraMap(localDecodeException, l, (QualityInfo)localObject3, bool1, (String)localObject1, str2, (String)localObject2, str3);
          }
          catch (Exception localException2)
          {
            localObject4 = null;
          }
          catch (DecodeException localDecodeException)
          {
            localObject5 = localDecodeException.getEncodedImage();
            str4 = localDecodeException.getMessage();
            str5 = ((EncodedImage)localObject5).getFirstBytesAsHexString(10);
            paramInt = ((EncodedImage)localObject5).getSize();
            FLog.w("ProgressiveDecoder", "%s, {uri: %s, firstEncodedBytes: %s, length: %d}", new Object[] { str4, localException2, str5, Integer.valueOf(paramInt) });
            throw localDecodeException;
          }
          mProducerListener.onProducerFinishWithFailure(mProducerContext.getId(), "DecodeProducer", localException2, (Map)localObject1);
          handleError(localException2);
          EncodedImage.closeSafely(paramEncodedImage);
          return;
        }
        catch (Throwable localThrowable)
        {
          EncodedImage.closeSafely(paramEncodedImage);
          throw localThrowable;
        }
      }
    }
    
    private Map getExtraMap(CloseableImage paramCloseableImage, long paramLong, QualityInfo paramQualityInfo, boolean paramBoolean, String paramString1, String paramString2, String paramString3, String paramString4)
    {
      if (!mProducerListener.requiresExtraMap(mProducerContext.getId())) {
        return null;
      }
      String str1 = String.valueOf(paramLong);
      paramQualityInfo = String.valueOf(paramQualityInfo.isOfGoodEnoughQuality());
      String str2 = String.valueOf(paramBoolean);
      if ((paramCloseableImage instanceof CloseableStaticBitmap))
      {
        paramCloseableImage = ((CloseableStaticBitmap)paramCloseableImage).getUnderlyingBitmap();
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append(paramCloseableImage.getWidth());
        ((StringBuilder)localObject).append("x");
        ((StringBuilder)localObject).append(paramCloseableImage.getHeight());
        paramCloseableImage = ((StringBuilder)localObject).toString();
        localObject = new HashMap(8);
        ((Map)localObject).put("bitmapSize", paramCloseableImage);
        ((Map)localObject).put("queueTime", str1);
        ((Map)localObject).put("hasGoodQuality", paramQualityInfo);
        ((Map)localObject).put("isFinal", str2);
        ((Map)localObject).put("encodedImageSize", paramString2);
        ((Map)localObject).put("imageFormat", paramString1);
        ((Map)localObject).put("requestedImageSize", paramString3);
        ((Map)localObject).put("sampleSize", paramString4);
        return ImmutableMap.copyOf((Map)localObject);
      }
      paramCloseableImage = new HashMap(7);
      paramCloseableImage.put("queueTime", str1);
      paramCloseableImage.put("hasGoodQuality", paramQualityInfo);
      paramCloseableImage.put("isFinal", str2);
      paramCloseableImage.put("encodedImageSize", paramString2);
      paramCloseableImage.put("imageFormat", paramString1);
      paramCloseableImage.put("requestedImageSize", paramString3);
      paramCloseableImage.put("sampleSize", paramString4);
      return ImmutableMap.copyOf(paramCloseableImage);
    }
    
    private void handleCancellation()
    {
      maybeFinish(true);
      getConsumer().onCancellation();
    }
    
    private void handleError(Throwable paramThrowable)
    {
      maybeFinish(true);
      getConsumer().onFailure(paramThrowable);
    }
    
    private void handleResult(CloseableImage paramCloseableImage, int paramInt)
    {
      paramCloseableImage = mCloseableReferenceFactory.create(paramCloseableImage);
      try
      {
        maybeFinish(BaseConsumer.isLast(paramInt));
        getConsumer().onNewResult(paramCloseableImage, paramInt);
        CloseableReference.closeSafely(paramCloseableImage);
        return;
      }
      catch (Throwable localThrowable)
      {
        CloseableReference.closeSafely(paramCloseableImage);
        throw localThrowable;
      }
    }
    
    private boolean isFinished()
    {
      try
      {
        boolean bool = mIsFinished;
        return bool;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    private void maybeFinish(boolean paramBoolean)
    {
      if (paramBoolean) {
        try
        {
          if (!mIsFinished)
          {
            getConsumer().onProgressUpdate(1.0F);
            mIsFinished = true;
            mJobScheduler.clearJob();
            return;
          }
        }
        catch (Throwable localThrowable) {}
      }
      return;
      throw localThrowable;
    }
    
    protected abstract int getIntermediateImageEndOffset(EncodedImage paramEncodedImage);
    
    protected abstract QualityInfo getQualityInfo();
    
    public void onCancellationImpl()
    {
      handleCancellation();
    }
    
    public void onFailureImpl(Throwable paramThrowable)
    {
      handleError(paramThrowable);
    }
    
    public void onNewResultImpl(EncodedImage paramEncodedImage, int paramInt)
    {
      try
      {
        boolean bool1 = FrescoSystrace.isTracing();
        if (bool1) {
          FrescoSystrace.beginSection("DecodeProducer#onNewResultImpl");
        }
        bool1 = BaseConsumer.isLast(paramInt);
        if (bool1)
        {
          bool2 = EncodedImage.isValid(paramEncodedImage);
          if (!bool2)
          {
            handleError(new ExceptionWithNoStacktrace("Encoded image is not valid."));
            if (!FrescoSystrace.isTracing()) {
              return;
            }
            FrescoSystrace.endSection();
            return;
          }
        }
        boolean bool2 = updateDecodeJob(paramEncodedImage, paramInt);
        if (!bool2)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
        }
        else
        {
          bool2 = BaseConsumer.statusHasFlag(paramInt, 4);
          if ((!bool1) && (!bool2))
          {
            bool1 = mProducerContext.isIntermediateResultExpected();
            if (!bool1) {}
          }
          else
          {
            mJobScheduler.scheduleJob();
          }
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return;
          }
        }
      }
      catch (Throwable paramEncodedImage)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
        throw paramEncodedImage;
      }
    }
    
    protected void onProgressUpdateImpl(float paramFloat)
    {
      super.onProgressUpdateImpl(paramFloat * 0.99F);
    }
    
    protected boolean updateDecodeJob(EncodedImage paramEncodedImage, int paramInt)
    {
      return mJobScheduler.updateJob(paramEncodedImage, paramInt);
    }
  }
}
