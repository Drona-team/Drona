package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.util.TriState;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.transcoder.ImageTranscodeResult;
import com.facebook.imagepipeline.transcoder.ImageTranscoder;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import com.facebook.imagepipeline.transcoder.JpegTranscoderUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class ResizeAndRotateProducer
  implements Producer<EncodedImage>
{
  private static final String INPUT_IMAGE_FORMAT = "Image format";
  @VisibleForTesting
  static final int MIN_TRANSFORM_INTERVAL_MS = 100;
  private static final String ORIGINAL_SIZE_KEY = "Original size";
  private static final String PRODUCER_NAME = "ResizeAndRotateProducer";
  private static final String REQUESTED_SIZE_KEY = "Requested size";
  private static final String TRANSCODER_ID = "Transcoder id";
  private static final String TRANSCODING_RESULT = "Transcoding result";
  private final Executor mExecutor;
  private final ImageTranscoderFactory mImageTranscoderFactory;
  private final Producer<EncodedImage> mInputProducer;
  private final boolean mIsResizingEnabled;
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  
  public ResizeAndRotateProducer(Executor paramExecutor, PooledByteBufferFactory paramPooledByteBufferFactory, Producer paramProducer, boolean paramBoolean, ImageTranscoderFactory paramImageTranscoderFactory)
  {
    mExecutor = ((Executor)Preconditions.checkNotNull(paramExecutor));
    mPooledByteBufferFactory = ((PooledByteBufferFactory)Preconditions.checkNotNull(paramPooledByteBufferFactory));
    mInputProducer = ((Producer)Preconditions.checkNotNull(paramProducer));
    mImageTranscoderFactory = ((ImageTranscoderFactory)Preconditions.checkNotNull(paramImageTranscoderFactory));
    mIsResizingEnabled = paramBoolean;
  }
  
  private static boolean shouldRotate(RotationOptions paramRotationOptions, EncodedImage paramEncodedImage)
  {
    return (!paramRotationOptions.canDeferUntilRendered()) && ((JpegTranscoderUtils.getRotationAngle(paramRotationOptions, paramEncodedImage) != 0) || (shouldRotateUsingExifOrientation(paramRotationOptions, paramEncodedImage)));
  }
  
  private static boolean shouldRotateUsingExifOrientation(RotationOptions paramRotationOptions, EncodedImage paramEncodedImage)
  {
    if ((paramRotationOptions.rotationEnabled()) && (!paramRotationOptions.canDeferUntilRendered())) {
      return JpegTranscoderUtils.INVERTED_EXIF_ORIENTATIONS.contains(Integer.valueOf(paramEncodedImage.getExifOrientation()));
    }
    paramEncodedImage.setExifOrientation(0);
    return false;
  }
  
  private static TriState shouldTransform(ImageRequest paramImageRequest, EncodedImage paramEncodedImage, ImageTranscoder paramImageTranscoder)
  {
    if ((paramEncodedImage != null) && (paramEncodedImage.getImageFormat() != ImageFormat.UNKNOWN))
    {
      if (!paramImageTranscoder.canTranscode(paramEncodedImage.getImageFormat())) {
        return TriState.NO;
      }
      boolean bool;
      if ((!shouldRotate(paramImageRequest.getRotationOptions(), paramEncodedImage)) && (!paramImageTranscoder.canResize(paramEncodedImage, paramImageRequest.getRotationOptions(), paramImageRequest.getResizeOptions()))) {
        bool = false;
      } else {
        bool = true;
      }
      return TriState.valueOf(bool);
    }
    return TriState.UNSET;
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    mInputProducer.produceResults(new TransformingConsumer(paramConsumer, paramProducerContext, mIsResizingEnabled, mImageTranscoderFactory), paramProducerContext);
  }
  
  private class TransformingConsumer
    extends DelegatingConsumer<EncodedImage, EncodedImage>
  {
    private final ImageTranscoderFactory mImageTranscoderFactory;
    private boolean mIsCancelled = false;
    private final boolean mIsResizingEnabled;
    private final JobScheduler mJobScheduler;
    private final ProducerContext mProducerContext;
    
    TransformingConsumer(final Consumer paramConsumer, ProducerContext paramProducerContext, boolean paramBoolean, ImageTranscoderFactory paramImageTranscoderFactory)
    {
      super();
      mProducerContext = paramProducerContext;
      paramProducerContext = mProducerContext.getImageRequest().getResizingAllowedOverride();
      if (paramProducerContext != null) {
        paramBoolean = paramProducerContext.booleanValue();
      }
      mIsResizingEnabled = paramBoolean;
      mImageTranscoderFactory = paramImageTranscoderFactory;
      paramProducerContext = new JobScheduler.JobRunnable()
      {
        public void remainder(EncodedImage paramAnonymousEncodedImage, int paramAnonymousInt)
        {
          ResizeAndRotateProducer.TransformingConsumer.this.doTransform(paramAnonymousEncodedImage, paramAnonymousInt, (ImageTranscoder)Preconditions.checkNotNull(mImageTranscoderFactory.createImageTranscoder(paramAnonymousEncodedImage.getImageFormat(), mIsResizingEnabled)));
        }
      };
      mJobScheduler = new JobScheduler(mExecutor, paramProducerContext, 100);
      mProducerContext.addCallbacks(new BaseProducerContextCallbacks()
      {
        public void onCancellationRequested()
        {
          mJobScheduler.clearJob();
          ResizeAndRotateProducer.TransformingConsumer.access$602(ResizeAndRotateProducer.TransformingConsumer.this, true);
          paramConsumer.onCancellation();
        }
        
        public void onIsIntermediateResultExpectedChanged()
        {
          if (mProducerContext.isIntermediateResultExpected()) {
            mJobScheduler.scheduleJob();
          }
        }
      });
    }
    
    /* Error */
    private void doTransform(EncodedImage paramEncodedImage, int paramInt, ImageTranscoder paramImageTranscoder)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   4: invokeinterface 102 1 0
      //   9: aload_0
      //   10: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   13: invokeinterface 106 1 0
      //   18: ldc 108
      //   20: invokeinterface 114 3 0
      //   25: aload_0
      //   26: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   29: invokeinterface 40 1 0
      //   34: astore 11
      //   36: aload_0
      //   37: getfield 27	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:this$0	Lcom/facebook/imagepipeline/producers/ResizeAndRotateProducer;
      //   40: invokestatic 118	com/facebook/imagepipeline/producers/ResizeAndRotateProducer:access$800	(Lcom/facebook/imagepipeline/producers/ResizeAndRotateProducer;)Lcom/facebook/common/memory/PooledByteBufferFactory;
      //   43: invokeinterface 124 1 0
      //   48: astore 9
      //   50: aconst_null
      //   51: astore 8
      //   53: aload_3
      //   54: aload_1
      //   55: aload 9
      //   57: aload 11
      //   59: invokevirtual 128	com/facebook/imagepipeline/request/ImageRequest:getRotationOptions	()Lcom/facebook/imagepipeline/common/RotationOptions;
      //   62: aload 11
      //   64: invokevirtual 132	com/facebook/imagepipeline/request/ImageRequest:getResizeOptions	()Lcom/facebook/imagepipeline/common/ResizeOptions;
      //   67: aconst_null
      //   68: bipush 85
      //   70: invokestatic 138	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   73: invokeinterface 144 7 0
      //   78: astore 10
      //   80: aload 10
      //   82: invokevirtual 150	com/facebook/imagepipeline/transcoder/ImageTranscodeResult:getTranscodeStatus	()I
      //   85: istore 4
      //   87: iload 4
      //   89: iconst_2
      //   90: if_icmpeq +198 -> 288
      //   93: aload_0
      //   94: aload_1
      //   95: aload 11
      //   97: invokevirtual 132	com/facebook/imagepipeline/request/ImageRequest:getResizeOptions	()Lcom/facebook/imagepipeline/common/ResizeOptions;
      //   100: aload 10
      //   102: aload_3
      //   103: invokeinterface 153 1 0
      //   108: invokespecial 157	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:getExtraMap	(Lcom/facebook/imagepipeline/image/EncodedImage;Lcom/facebook/imagepipeline/common/ResizeOptions;Lcom/facebook/imagepipeline/transcoder/ImageTranscodeResult;Ljava/lang/String;)Ljava/util/Map;
      //   111: astore_1
      //   112: iload_2
      //   113: istore 4
      //   115: aload 9
      //   117: invokevirtual 163	com/facebook/common/memory/PooledByteBufferOutputStream:toByteBuffer	()Lcom/facebook/common/memory/PooledByteBuffer;
      //   120: invokestatic 169	com/facebook/common/references/CloseableReference:of	(Ljava/io/Closeable;)Lcom/facebook/common/references/CloseableReference;
      //   123: astore_3
      //   124: iload_2
      //   125: istore 5
      //   127: new 171	com/facebook/imagepipeline/image/EncodedImage
      //   130: dup
      //   131: aload_3
      //   132: invokespecial 174	com/facebook/imagepipeline/image/EncodedImage:<init>	(Lcom/facebook/common/references/CloseableReference;)V
      //   135: astore 8
      //   137: iload_2
      //   138: istore 5
      //   140: aload 8
      //   142: getstatic 180	com/facebook/imageformat/DefaultImageFormats:JPEG	Lcom/facebook/imageformat/ImageFormat;
      //   145: invokevirtual 184	com/facebook/imagepipeline/image/EncodedImage:setImageFormat	(Lcom/facebook/imageformat/ImageFormat;)V
      //   148: iload_2
      //   149: istore 6
      //   151: aload 8
      //   153: invokevirtual 188	com/facebook/imagepipeline/image/EncodedImage:parseMetaData	()V
      //   156: iload_2
      //   157: istore 6
      //   159: aload_0
      //   160: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   163: invokeinterface 102 1 0
      //   168: aload_0
      //   169: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   172: invokeinterface 106 1 0
      //   177: ldc 108
      //   179: aload_1
      //   180: invokeinterface 192 4 0
      //   185: iload_2
      //   186: istore 6
      //   188: aload 10
      //   190: invokevirtual 150	com/facebook/imagepipeline/transcoder/ImageTranscodeResult:getTranscodeStatus	()I
      //   193: istore 5
      //   195: iload_2
      //   196: istore 4
      //   198: iload 5
      //   200: iconst_1
      //   201: if_icmpeq +9 -> 210
      //   204: iload_2
      //   205: bipush 16
      //   207: ior
      //   208: istore 4
      //   210: iload 4
      //   212: istore 6
      //   214: aload_0
      //   215: invokevirtual 196	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   218: aload 8
      //   220: iload 4
      //   222: invokeinterface 202 3 0
      //   227: iload 4
      //   229: istore 5
      //   231: aload 8
      //   233: invokestatic 206	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
      //   236: aload_3
      //   237: invokestatic 208	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   240: aload 9
      //   242: invokevirtual 211	com/facebook/common/memory/PooledByteBufferOutputStream:close	()V
      //   245: return
      //   246: astore 10
      //   248: iload 6
      //   250: istore 5
      //   252: aload 8
      //   254: invokestatic 206	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
      //   257: iload 6
      //   259: istore 5
      //   261: aload 10
      //   263: athrow
      //   264: astore 8
      //   266: iload 5
      //   268: istore 4
      //   270: aload_3
      //   271: invokestatic 208	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   274: iload 5
      //   276: istore 4
      //   278: aload 8
      //   280: athrow
      //   281: astore_3
      //   282: iload 4
      //   284: istore_2
      //   285: goto +23 -> 308
      //   288: new 213	java/lang/RuntimeException
      //   291: dup
      //   292: ldc -41
      //   294: invokespecial 218	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
      //   297: astore_1
      //   298: aload_1
      //   299: athrow
      //   300: astore_1
      //   301: goto +61 -> 362
      //   304: astore_3
      //   305: aload 8
      //   307: astore_1
      //   308: aload_0
      //   309: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   312: invokeinterface 102 1 0
      //   317: aload_0
      //   318: getfield 34	com/facebook/imagepipeline/producers/ResizeAndRotateProducer$TransformingConsumer:mProducerContext	Lcom/facebook/imagepipeline/producers/ProducerContext;
      //   321: invokeinterface 106 1 0
      //   326: ldc 108
      //   328: aload_3
      //   329: aload_1
      //   330: invokeinterface 222 5 0
      //   335: iload_2
      //   336: invokestatic 228	com/facebook/imagepipeline/producers/BaseConsumer:isLast	(I)Z
      //   339: istore 7
      //   341: iload 7
      //   343: ifeq +13 -> 356
      //   346: aload_0
      //   347: invokevirtual 196	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   350: aload_3
      //   351: invokeinterface 232 2 0
      //   356: aload 9
      //   358: invokevirtual 211	com/facebook/common/memory/PooledByteBufferOutputStream:close	()V
      //   361: return
      //   362: aload 9
      //   364: invokevirtual 211	com/facebook/common/memory/PooledByteBufferOutputStream:close	()V
      //   367: aload_1
      //   368: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	369	0	this	TransformingConsumer
      //   0	369	1	paramEncodedImage	EncodedImage
      //   0	369	2	paramInt	int
      //   0	369	3	paramImageTranscoder	ImageTranscoder
      //   85	198	4	i	int
      //   125	150	5	j	int
      //   149	109	6	k	int
      //   339	3	7	bool	boolean
      //   51	202	8	localEncodedImage	EncodedImage
      //   264	42	8	localThrowable1	Throwable
      //   48	315	9	localPooledByteBufferOutputStream	com.facebook.common.memory.PooledByteBufferOutputStream
      //   78	111	10	localImageTranscodeResult	ImageTranscodeResult
      //   246	16	10	localThrowable2	Throwable
      //   34	62	11	localImageRequest	ImageRequest
      // Exception table:
      //   from	to	target	type
      //   151	156	246	java/lang/Throwable
      //   159	185	246	java/lang/Throwable
      //   188	195	246	java/lang/Throwable
      //   214	227	246	java/lang/Throwable
      //   127	137	264	java/lang/Throwable
      //   140	148	264	java/lang/Throwable
      //   231	236	264	java/lang/Throwable
      //   252	257	264	java/lang/Throwable
      //   261	264	264	java/lang/Throwable
      //   115	124	281	java/lang/Exception
      //   236	240	281	java/lang/Exception
      //   270	274	281	java/lang/Exception
      //   278	281	281	java/lang/Exception
      //   53	87	300	java/lang/Throwable
      //   93	112	300	java/lang/Throwable
      //   115	124	300	java/lang/Throwable
      //   236	240	300	java/lang/Throwable
      //   270	274	300	java/lang/Throwable
      //   278	281	300	java/lang/Throwable
      //   288	298	300	java/lang/Throwable
      //   298	300	300	java/lang/Throwable
      //   308	341	300	java/lang/Throwable
      //   346	356	300	java/lang/Throwable
      //   53	87	304	java/lang/Exception
      //   93	112	304	java/lang/Exception
      //   288	298	304	java/lang/Exception
    }
    
    private void forwardNewResult(EncodedImage paramEncodedImage, int paramInt, ImageFormat paramImageFormat)
    {
      if ((paramImageFormat != DefaultImageFormats.JPEG) && (paramImageFormat != DefaultImageFormats.HEIF)) {
        paramEncodedImage = getNewResultForImagesWithoutExifData(paramEncodedImage);
      } else {
        paramEncodedImage = getNewResultsForJpegOrHeif(paramEncodedImage);
      }
      getConsumer().onNewResult(paramEncodedImage, paramInt);
    }
    
    private EncodedImage getCloneWithRotationApplied(EncodedImage paramEncodedImage, int paramInt)
    {
      EncodedImage localEncodedImage = EncodedImage.cloneOrNull(paramEncodedImage);
      paramEncodedImage.close();
      if (localEncodedImage != null) {
        localEncodedImage.setRotationAngle(paramInt);
      }
      return localEncodedImage;
    }
    
    private Map getExtraMap(EncodedImage paramEncodedImage, ResizeOptions paramResizeOptions, ImageTranscodeResult paramImageTranscodeResult, String paramString)
    {
      if (!mProducerContext.getListener().requiresExtraMap(mProducerContext.getId())) {
        return null;
      }
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append(paramEncodedImage.getWidth());
      ((StringBuilder)localObject1).append("x");
      ((StringBuilder)localObject1).append(paramEncodedImage.getHeight());
      localObject1 = ((StringBuilder)localObject1).toString();
      if (paramResizeOptions != null)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(width);
        ((StringBuilder)localObject2).append("x");
        ((StringBuilder)localObject2).append(height);
        paramResizeOptions = ((StringBuilder)localObject2).toString();
      }
      else
      {
        paramResizeOptions = "Unspecified";
      }
      Object localObject2 = new HashMap();
      ((Map)localObject2).put("Image format", String.valueOf(paramEncodedImage.getImageFormat()));
      ((Map)localObject2).put("Original size", localObject1);
      ((Map)localObject2).put("Requested size", paramResizeOptions);
      ((Map)localObject2).put("queueTime", String.valueOf(mJobScheduler.getQueuedTime()));
      ((Map)localObject2).put("Transcoder id", paramString);
      ((Map)localObject2).put("Transcoding result", String.valueOf(paramImageTranscodeResult));
      return ImmutableMap.copyOf((Map)localObject2);
    }
    
    private EncodedImage getNewResultForImagesWithoutExifData(EncodedImage paramEncodedImage)
    {
      RotationOptions localRotationOptions = mProducerContext.getImageRequest().getRotationOptions();
      EncodedImage localEncodedImage = paramEncodedImage;
      if (!localRotationOptions.useImageMetadata())
      {
        localEncodedImage = paramEncodedImage;
        if (localRotationOptions.rotationEnabled()) {
          localEncodedImage = getCloneWithRotationApplied(paramEncodedImage, localRotationOptions.getForcedAngle());
        }
      }
      return localEncodedImage;
    }
    
    private EncodedImage getNewResultsForJpegOrHeif(EncodedImage paramEncodedImage)
    {
      EncodedImage localEncodedImage = paramEncodedImage;
      if (!mProducerContext.getImageRequest().getRotationOptions().canDeferUntilRendered())
      {
        localEncodedImage = paramEncodedImage;
        if (paramEncodedImage.getRotationAngle() != 0)
        {
          localEncodedImage = paramEncodedImage;
          if (paramEncodedImage.getRotationAngle() != -1) {
            localEncodedImage = getCloneWithRotationApplied(paramEncodedImage, 0);
          }
        }
      }
      return localEncodedImage;
    }
    
    protected void onNewResultImpl(EncodedImage paramEncodedImage, int paramInt)
    {
      if (mIsCancelled) {
        return;
      }
      boolean bool = BaseConsumer.isLast(paramInt);
      if (paramEncodedImage == null)
      {
        if (bool) {
          getConsumer().onNewResult(null, 1);
        }
      }
      else
      {
        ImageFormat localImageFormat = paramEncodedImage.getImageFormat();
        TriState localTriState = ResizeAndRotateProducer.shouldTransform(mProducerContext.getImageRequest(), paramEncodedImage, (ImageTranscoder)Preconditions.checkNotNull(mImageTranscoderFactory.createImageTranscoder(localImageFormat, mIsResizingEnabled)));
        if ((!bool) && (localTriState == TriState.UNSET)) {
          return;
        }
        if (localTriState != TriState.YES)
        {
          forwardNewResult(paramEncodedImage, paramInt, localImageFormat);
          return;
        }
        if (!mJobScheduler.updateJob(paramEncodedImage, paramInt)) {
          return;
        }
        if ((bool) || (mProducerContext.isIntermediateResultExpected())) {
          mJobScheduler.scheduleJob();
        }
      }
    }
  }
}
