package com.facebook.imagepipeline.core;

import android.content.Context;
import android.os.Build.VERSION;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.internal.AndroidPredicates;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Suppliers;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.animated.factory.AnimatedFactory;
import com.facebook.imagepipeline.animated.factory.AnimatedFactoryProvider;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactoryProvider;
import com.facebook.imagepipeline.cache.BitmapCountingMemoryCacheFactory;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheFactory;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CountingMemoryCache;
import com.facebook.imagepipeline.cache.EncodedCountingMemoryCacheFactory;
import com.facebook.imagepipeline.cache.EncodedMemoryCacheFactory;
import com.facebook.imagepipeline.cache.InstrumentedMemoryCache;
import com.facebook.imagepipeline.decoder.DefaultImageDecoder;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ImageDecoderConfig;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.platform.PlatformDecoder;
import com.facebook.imagepipeline.platform.PlatformDecoderFactory;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import com.facebook.imagepipeline.transcoder.MultiImageTranscoderFactory;
import com.facebook.imagepipeline.transcoder.SimpleImageTranscoderFactory;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ImagePipelineFactory
{
  private static final Class<?> TAG = ImagePipelineFactory.class;
  private static ImagePipelineFactory sInstance;
  private AnimatedFactory mAnimatedFactory;
  private CountingMemoryCache<CacheKey, CloseableImage> mBitmapCountingMemoryCache;
  private InstrumentedMemoryCache<CacheKey, CloseableImage> mBitmapMemoryCache;
  private final CloseableReferenceFactory mCloseableReferenceFactory;
  private final ImagePipelineConfig mConfig;
  private CountingMemoryCache<CacheKey, PooledByteBuffer> mEncodedCountingMemoryCache;
  private InstrumentedMemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;
  private ImageDecoder mImageDecoder;
  private ImagePipeline mImagePipeline;
  private ImageTranscoderFactory mImageTranscoderFactory;
  private BufferedDiskCache mMainBufferedDiskCache;
  private FileCache mMainFileCache;
  private PlatformBitmapFactory mPlatformBitmapFactory;
  private PlatformDecoder mPlatformDecoder;
  private ProducerFactory mProducerFactory;
  private ProducerSequenceFactory mProducerSequenceFactory;
  private BufferedDiskCache mSmallImageBufferedDiskCache;
  private FileCache mSmallImageFileCache;
  private final ThreadHandoffProducerQueue mThreadHandoffProducerQueue;
  
  public ImagePipelineFactory(ImagePipelineConfig paramImagePipelineConfig)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ImagePipelineConfig()");
    }
    mConfig = ((ImagePipelineConfig)Preconditions.checkNotNull(paramImagePipelineConfig));
    mThreadHandoffProducerQueue = new ThreadHandoffProducerQueue(paramImagePipelineConfig.getExecutorSupplier().forLightweightBackgroundTasks());
    mCloseableReferenceFactory = new CloseableReferenceFactory(paramImagePipelineConfig.getCloseableReferenceLeakTracker());
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  private AnimatedFactory getAnimatedFactory()
  {
    if (mAnimatedFactory == null) {
      mAnimatedFactory = AnimatedFactoryProvider.getAnimatedFactory(getPlatformBitmapFactory(), mConfig.getExecutorSupplier(), getBitmapCountingMemoryCache(), mConfig.getExperiments().shouldDownscaleFrameToDrawableDimensions());
    }
    return mAnimatedFactory;
  }
  
  private ImageDecoder getImageDecoder()
  {
    if (mImageDecoder == null) {
      if (mConfig.getImageDecoder() != null)
      {
        mImageDecoder = mConfig.getImageDecoder();
      }
      else
      {
        Object localObject = getAnimatedFactory();
        ImageDecoder localImageDecoder = null;
        if (localObject != null)
        {
          localImageDecoder = ((AnimatedFactory)localObject).getGifDecoder(mConfig.getBitmapConfig());
          localObject = ((AnimatedFactory)localObject).getWebPDecoder(mConfig.getBitmapConfig());
        }
        else
        {
          localObject = null;
        }
        if (mConfig.getImageDecoderConfig() == null)
        {
          mImageDecoder = new DefaultImageDecoder(localImageDecoder, (ImageDecoder)localObject, getPlatformDecoder());
        }
        else
        {
          mImageDecoder = new DefaultImageDecoder(localImageDecoder, (ImageDecoder)localObject, getPlatformDecoder(), mConfig.getImageDecoderConfig().getCustomImageDecoders());
          ImageFormatChecker.getInstance().setCustomImageFormatCheckers(mConfig.getImageDecoderConfig().getCustomImageFormats());
        }
      }
    }
    return mImageDecoder;
  }
  
  private ImageTranscoderFactory getImageTranscoderFactory()
  {
    if (mImageTranscoderFactory == null) {
      if ((mConfig.getImageTranscoderFactory() == null) && (mConfig.getImageTranscoderType() == null) && (mConfig.getExperiments().isNativeCodeDisabled())) {
        mImageTranscoderFactory = new SimpleImageTranscoderFactory(mConfig.getExperiments().getMaxBitmapSize());
      } else {
        mImageTranscoderFactory = new MultiImageTranscoderFactory(mConfig.getExperiments().getMaxBitmapSize(), mConfig.getExperiments().getUseDownsamplingRatioForResizing(), mConfig.getImageTranscoderFactory(), mConfig.getImageTranscoderType());
      }
    }
    return mImageTranscoderFactory;
  }
  
  public static ImagePipelineFactory getInstance()
  {
    return (ImagePipelineFactory)Preconditions.checkNotNull(sInstance, "ImagePipelineFactory was not initialized!");
  }
  
  private ProducerFactory getProducerFactory()
  {
    if (mProducerFactory == null) {
      mProducerFactory = mConfig.getExperiments().getProducerFactoryMethod().createProducerFactory(mConfig.getContext(), mConfig.getPoolFactory().getSmallByteArrayPool(), getImageDecoder(), mConfig.getProgressiveJpegConfig(), mConfig.isDownsampleEnabled(), mConfig.isResizeAndRotateEnabledForNetwork(), mConfig.getExperiments().isDecodeCancellationEnabled(), mConfig.getExecutorSupplier(), mConfig.getPoolFactory().getPooledByteBufferFactory(mConfig.getMemoryChunkType()), getBitmapMemoryCache(), getEncodedMemoryCache(), getMainBufferedDiskCache(), getSmallImageBufferedDiskCache(), mConfig.getCacheKeyFactory(), getPlatformBitmapFactory(), mConfig.getExperiments().getBitmapPrepareToDrawMinSizeBytes(), mConfig.getExperiments().getBitmapPrepareToDrawMaxSizeBytes(), mConfig.getExperiments().getBitmapPrepareToDrawForPrefetch(), mConfig.getExperiments().getMaxBitmapSize(), getCloseableReferenceFactory());
    }
    return mProducerFactory;
  }
  
  private ProducerSequenceFactory getProducerSequenceFactory()
  {
    boolean bool;
    if ((Build.VERSION.SDK_INT >= 24) && (mConfig.getExperiments().getUseBitmapPrepareToDraw())) {
      bool = true;
    } else {
      bool = false;
    }
    if (mProducerSequenceFactory == null) {
      mProducerSequenceFactory = new ProducerSequenceFactory(mConfig.getContext().getApplicationContext().getContentResolver(), getProducerFactory(), mConfig.getNetworkFetcher(), mConfig.isResizeAndRotateEnabledForNetwork(), mConfig.getExperiments().isWebpSupportEnabled(), mThreadHandoffProducerQueue, mConfig.isDownsampleEnabled(), bool, mConfig.getExperiments().isPartialImageCachingEnabled(), mConfig.isDiskCacheEnabled(), getImageTranscoderFactory());
    }
    return mProducerSequenceFactory;
  }
  
  private BufferedDiskCache getSmallImageBufferedDiskCache()
  {
    if (mSmallImageBufferedDiskCache == null) {
      mSmallImageBufferedDiskCache = new BufferedDiskCache(getSmallImageFileCache(), mConfig.getPoolFactory().getPooledByteBufferFactory(mConfig.getMemoryChunkType()), mConfig.getPoolFactory().getPooledByteStreams(), mConfig.getExecutorSupplier().forLocalStorageRead(), mConfig.getExecutorSupplier().forLocalStorageWrite(), mConfig.getImageCacheStatsTracker());
    }
    return mSmallImageBufferedDiskCache;
  }
  
  public static boolean hasBeenInitialized()
  {
    try
    {
      ImagePipelineFactory localImagePipelineFactory = sInstance;
      boolean bool;
      if (localImagePipelineFactory != null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static void initialize(Context paramContext)
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ImagePipelineFactory#initialize");
      }
      initialize(ImagePipelineConfig.newBuilder(paramContext).build());
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static void initialize(ImagePipelineConfig paramImagePipelineConfig)
  {
    try
    {
      if (sInstance != null) {
        FLog.w(TAG, "ImagePipelineFactory has already been initialized! `ImagePipelineFactory.initialize(...)` should only be called once to avoid unexpected behavior.");
      }
      sInstance = new ImagePipelineFactory(paramImagePipelineConfig);
      return;
    }
    catch (Throwable paramImagePipelineConfig)
    {
      throw paramImagePipelineConfig;
    }
  }
  
  public static void setInstance(ImagePipelineFactory paramImagePipelineFactory)
  {
    sInstance = paramImagePipelineFactory;
  }
  
  public static void shutDown()
  {
    try
    {
      if (sInstance != null)
      {
        sInstance.getBitmapMemoryCache().removeAll(AndroidPredicates.True());
        sInstance.getEncodedMemoryCache().removeAll(AndroidPredicates.True());
        sInstance = null;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public DrawableFactory getAnimatedDrawableFactory(Context paramContext)
  {
    AnimatedFactory localAnimatedFactory = getAnimatedFactory();
    if (localAnimatedFactory == null) {
      return null;
    }
    return localAnimatedFactory.getAnimatedDrawableFactory(paramContext);
  }
  
  public CountingMemoryCache getBitmapCountingMemoryCache()
  {
    if (mBitmapCountingMemoryCache == null) {
      mBitmapCountingMemoryCache = BitmapCountingMemoryCacheFactory.f(mConfig.getBitmapMemoryCacheParamsSupplier(), mConfig.getMemoryTrimmableRegistry(), mConfig.getBitmapMemoryCacheTrimStrategy());
    }
    return mBitmapCountingMemoryCache;
  }
  
  public InstrumentedMemoryCache getBitmapMemoryCache()
  {
    if (mBitmapMemoryCache == null) {
      mBitmapMemoryCache = BitmapMemoryCacheFactory.removeBody(getBitmapCountingMemoryCache(), mConfig.getImageCacheStatsTracker());
    }
    return mBitmapMemoryCache;
  }
  
  public CloseableReferenceFactory getCloseableReferenceFactory()
  {
    return mCloseableReferenceFactory;
  }
  
  public CountingMemoryCache getEncodedCountingMemoryCache()
  {
    if (mEncodedCountingMemoryCache == null) {
      mEncodedCountingMemoryCache = EncodedCountingMemoryCacheFactory.cache(mConfig.getEncodedMemoryCacheParamsSupplier(), mConfig.getMemoryTrimmableRegistry());
    }
    return mEncodedCountingMemoryCache;
  }
  
  public InstrumentedMemoryCache getEncodedMemoryCache()
  {
    if (mEncodedMemoryCache == null) {
      mEncodedMemoryCache = EncodedMemoryCacheFactory.removeBody(getEncodedCountingMemoryCache(), mConfig.getImageCacheStatsTracker());
    }
    return mEncodedMemoryCache;
  }
  
  public ImagePipeline getImagePipeline()
  {
    if (mImagePipeline == null) {
      mImagePipeline = new ImagePipeline(getProducerSequenceFactory(), mConfig.getRequestListeners(), mConfig.getIsPrefetchEnabledSupplier(), getBitmapMemoryCache(), getEncodedMemoryCache(), getMainBufferedDiskCache(), getSmallImageBufferedDiskCache(), mConfig.getCacheKeyFactory(), mThreadHandoffProducerQueue, Suppliers.cache(Boolean.valueOf(false)), mConfig.getExperiments().isLazyDataSource(), mConfig.getCallerContextVerifier());
    }
    return mImagePipeline;
  }
  
  public BufferedDiskCache getMainBufferedDiskCache()
  {
    if (mMainBufferedDiskCache == null) {
      mMainBufferedDiskCache = new BufferedDiskCache(getMainFileCache(), mConfig.getPoolFactory().getPooledByteBufferFactory(mConfig.getMemoryChunkType()), mConfig.getPoolFactory().getPooledByteStreams(), mConfig.getExecutorSupplier().forLocalStorageRead(), mConfig.getExecutorSupplier().forLocalStorageWrite(), mConfig.getImageCacheStatsTracker());
    }
    return mMainBufferedDiskCache;
  }
  
  public FileCache getMainFileCache()
  {
    if (mMainFileCache == null)
    {
      DiskCacheConfig localDiskCacheConfig = mConfig.getMainDiskCacheConfig();
      mMainFileCache = mConfig.getFileCacheFactory().getImageUrl(localDiskCacheConfig);
    }
    return mMainFileCache;
  }
  
  public PlatformBitmapFactory getPlatformBitmapFactory()
  {
    if (mPlatformBitmapFactory == null) {
      mPlatformBitmapFactory = PlatformBitmapFactoryProvider.buildPlatformBitmapFactory(mConfig.getPoolFactory(), getPlatformDecoder(), getCloseableReferenceFactory());
    }
    return mPlatformBitmapFactory;
  }
  
  public PlatformDecoder getPlatformDecoder()
  {
    if (mPlatformDecoder == null) {
      mPlatformDecoder = PlatformDecoderFactory.buildPlatformDecoder(mConfig.getPoolFactory(), mConfig.getExperiments().isGingerbreadDecoderEnabled());
    }
    return mPlatformDecoder;
  }
  
  public FileCache getSmallImageFileCache()
  {
    if (mSmallImageFileCache == null)
    {
      DiskCacheConfig localDiskCacheConfig = mConfig.getSmallImageDiskCacheConfig();
      mSmallImageFileCache = mConfig.getFileCacheFactory().getImageUrl(localDiskCacheConfig);
    }
    return mSmallImageFileCache;
  }
}