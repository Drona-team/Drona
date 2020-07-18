package com.facebook.imagepipeline.core;

import android.content.Context;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.common.webp.WebpBitmapFactory.WebpErrorLogger;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;

public class ImagePipelineExperiments
{
  private boolean mBitmapPrepareToDrawForPrefetch;
  private final int mBitmapPrepareToDrawMaxSizeBytes;
  private final int mBitmapPrepareToDrawMinSizeBytes;
  private final boolean mDecodeCancellationEnabled;
  private final boolean mDownscaleFrameToDrawableDimensions;
  private final boolean mGingerbreadDecoderEnabled;
  private final Supplier<Boolean> mLazyDataSource;
  private final int mMaxBitmapSize;
  private final boolean mNativeCodeDisabled;
  private final boolean mPartialImageCachingEnabled;
  private final ProducerFactoryMethod mProducerFactoryMethod;
  private final boolean mUseBitmapPrepareToDraw;
  private final boolean mUseDownsamplingRatioForResizing;
  private final WebpBitmapFactory mWebpBitmapFactory;
  private final WebpBitmapFactory.WebpErrorLogger mWebpErrorLogger;
  private final boolean mWebpSupportEnabled;
  
  private ImagePipelineExperiments(Builder paramBuilder)
  {
    mWebpSupportEnabled = mWebpSupportEnabled;
    mWebpErrorLogger = mWebpErrorLogger;
    mDecodeCancellationEnabled = mDecodeCancellationEnabled;
    mWebpBitmapFactory = mWebpBitmapFactory;
    mUseDownsamplingRatioForResizing = mUseDownsamplingRatioForResizing;
    mUseBitmapPrepareToDraw = mUseBitmapPrepareToDraw;
    mBitmapPrepareToDrawMinSizeBytes = mBitmapPrepareToDrawMinSizeBytes;
    mBitmapPrepareToDrawMaxSizeBytes = mBitmapPrepareToDrawMaxSizeBytes;
    mBitmapPrepareToDrawForPrefetch = mBitmapPrepareToDrawForPrefetch;
    mMaxBitmapSize = mMaxBitmapSize;
    mNativeCodeDisabled = mNativeCodeDisabled;
    mPartialImageCachingEnabled = mPartialImageCachingEnabled;
    if (mProducerFactoryMethod == null) {
      mProducerFactoryMethod = new DefaultProducerFactoryMethod();
    } else {
      mProducerFactoryMethod = mProducerFactoryMethod;
    }
    mLazyDataSource = mLazyDataSource;
    mGingerbreadDecoderEnabled = mGingerbreadDecoderEnabled;
    mDownscaleFrameToDrawableDimensions = mDownscaleFrameToDrawableDimensions;
  }
  
  public static Builder newBuilder(ImagePipelineConfig.Builder paramBuilder)
  {
    return new Builder(paramBuilder);
  }
  
  public boolean getBitmapPrepareToDrawForPrefetch()
  {
    return mBitmapPrepareToDrawForPrefetch;
  }
  
  public int getBitmapPrepareToDrawMaxSizeBytes()
  {
    return mBitmapPrepareToDrawMaxSizeBytes;
  }
  
  public int getBitmapPrepareToDrawMinSizeBytes()
  {
    return mBitmapPrepareToDrawMinSizeBytes;
  }
  
  public int getMaxBitmapSize()
  {
    return mMaxBitmapSize;
  }
  
  public ProducerFactoryMethod getProducerFactoryMethod()
  {
    return mProducerFactoryMethod;
  }
  
  public boolean getUseBitmapPrepareToDraw()
  {
    return mUseBitmapPrepareToDraw;
  }
  
  public boolean getUseDownsamplingRatioForResizing()
  {
    return mUseDownsamplingRatioForResizing;
  }
  
  public WebpBitmapFactory getWebpBitmapFactory()
  {
    return mWebpBitmapFactory;
  }
  
  public WebpBitmapFactory.WebpErrorLogger getWebpErrorLogger()
  {
    return mWebpErrorLogger;
  }
  
  public boolean isDecodeCancellationEnabled()
  {
    return mDecodeCancellationEnabled;
  }
  
  public boolean isGingerbreadDecoderEnabled()
  {
    return mGingerbreadDecoderEnabled;
  }
  
  public Supplier isLazyDataSource()
  {
    return mLazyDataSource;
  }
  
  public boolean isNativeCodeDisabled()
  {
    return mNativeCodeDisabled;
  }
  
  public boolean isPartialImageCachingEnabled()
  {
    return mPartialImageCachingEnabled;
  }
  
  public boolean isWebpSupportEnabled()
  {
    return mWebpSupportEnabled;
  }
  
  public boolean shouldDownscaleFrameToDrawableDimensions()
  {
    return mDownscaleFrameToDrawableDimensions;
  }
  
  public static class Builder
  {
    public boolean mBitmapPrepareToDrawForPrefetch = false;
    private int mBitmapPrepareToDrawMaxSizeBytes = 0;
    private int mBitmapPrepareToDrawMinSizeBytes = 0;
    private final ImagePipelineConfig.Builder mConfigBuilder;
    private boolean mDecodeCancellationEnabled = false;
    public boolean mDownscaleFrameToDrawableDimensions;
    public boolean mGingerbreadDecoderEnabled;
    public Supplier<Boolean> mLazyDataSource;
    private int mMaxBitmapSize = 2048;
    private boolean mNativeCodeDisabled = false;
    private boolean mPartialImageCachingEnabled = false;
    private ImagePipelineExperiments.ProducerFactoryMethod mProducerFactoryMethod;
    private boolean mUseBitmapPrepareToDraw = false;
    private boolean mUseDownsamplingRatioForResizing = false;
    private WebpBitmapFactory mWebpBitmapFactory;
    private WebpBitmapFactory.WebpErrorLogger mWebpErrorLogger;
    private boolean mWebpSupportEnabled = false;
    
    public Builder(ImagePipelineConfig.Builder paramBuilder)
    {
      mConfigBuilder = paramBuilder;
    }
    
    public ImagePipelineExperiments build()
    {
      return new ImagePipelineExperiments(this, null);
    }
    
    public boolean isPartialImageCachingEnabled()
    {
      return mPartialImageCachingEnabled;
    }
    
    public ImagePipelineConfig.Builder setBitmapPrepareToDraw(boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2)
    {
      mUseBitmapPrepareToDraw = paramBoolean1;
      mBitmapPrepareToDrawMinSizeBytes = paramInt1;
      mBitmapPrepareToDrawMaxSizeBytes = paramInt2;
      mBitmapPrepareToDrawForPrefetch = paramBoolean2;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setDecodeCancellationEnabled(boolean paramBoolean)
    {
      mDecodeCancellationEnabled = paramBoolean;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setGingerbreadDecoderEnabled(boolean paramBoolean)
    {
      mGingerbreadDecoderEnabled = paramBoolean;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setLazyDataSource(Supplier paramSupplier)
    {
      mLazyDataSource = paramSupplier;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setMaxBitmapSize(int paramInt)
    {
      mMaxBitmapSize = paramInt;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setNativeCodeDisabled(boolean paramBoolean)
    {
      mNativeCodeDisabled = paramBoolean;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setPartialImageCachingEnabled(boolean paramBoolean)
    {
      mPartialImageCachingEnabled = paramBoolean;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setProducerFactoryMethod(ImagePipelineExperiments.ProducerFactoryMethod paramProducerFactoryMethod)
    {
      mProducerFactoryMethod = paramProducerFactoryMethod;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setShouldDownscaleFrameToDrawableDimensions(boolean paramBoolean)
    {
      mDownscaleFrameToDrawableDimensions = paramBoolean;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setUseDownsampligRatioForResizing(boolean paramBoolean)
    {
      mUseDownsamplingRatioForResizing = paramBoolean;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setWebpBitmapFactory(WebpBitmapFactory paramWebpBitmapFactory)
    {
      mWebpBitmapFactory = paramWebpBitmapFactory;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setWebpErrorLogger(WebpBitmapFactory.WebpErrorLogger paramWebpErrorLogger)
    {
      mWebpErrorLogger = paramWebpErrorLogger;
      return mConfigBuilder;
    }
    
    public ImagePipelineConfig.Builder setWebpSupportEnabled(boolean paramBoolean)
    {
      mWebpSupportEnabled = paramBoolean;
      return mConfigBuilder;
    }
  }
  
  public static class DefaultProducerFactoryMethod
    implements ImagePipelineExperiments.ProducerFactoryMethod
  {
    public DefaultProducerFactoryMethod() {}
    
    public ProducerFactory createProducerFactory(Context paramContext, ByteArrayPool paramByteArrayPool, ImageDecoder paramImageDecoder, ProgressiveJpegConfig paramProgressiveJpegConfig, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, ExecutorSupplier paramExecutorSupplier, PooledByteBufferFactory paramPooledByteBufferFactory, MemoryCache paramMemoryCache1, MemoryCache paramMemoryCache2, BufferedDiskCache paramBufferedDiskCache1, BufferedDiskCache paramBufferedDiskCache2, CacheKeyFactory paramCacheKeyFactory, PlatformBitmapFactory paramPlatformBitmapFactory, int paramInt1, int paramInt2, boolean paramBoolean4, int paramInt3, CloseableReferenceFactory paramCloseableReferenceFactory)
    {
      return new ProducerFactory(paramContext, paramByteArrayPool, paramImageDecoder, paramProgressiveJpegConfig, paramBoolean1, paramBoolean2, paramBoolean3, paramExecutorSupplier, paramPooledByteBufferFactory, paramMemoryCache1, paramMemoryCache2, paramBufferedDiskCache1, paramBufferedDiskCache2, paramCacheKeyFactory, paramPlatformBitmapFactory, paramInt1, paramInt2, paramBoolean4, paramInt3, paramCloseableReferenceFactory);
    }
  }
  
  public static abstract interface ProducerFactoryMethod
  {
    public abstract ProducerFactory createProducerFactory(Context paramContext, ByteArrayPool paramByteArrayPool, ImageDecoder paramImageDecoder, ProgressiveJpegConfig paramProgressiveJpegConfig, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, ExecutorSupplier paramExecutorSupplier, PooledByteBufferFactory paramPooledByteBufferFactory, MemoryCache paramMemoryCache1, MemoryCache paramMemoryCache2, BufferedDiskCache paramBufferedDiskCache1, BufferedDiskCache paramBufferedDiskCache2, CacheKeyFactory paramCacheKeyFactory, PlatformBitmapFactory paramPlatformBitmapFactory, int paramInt1, int paramInt2, boolean paramBoolean4, int paramInt3, CloseableReferenceFactory paramCloseableReferenceFactory);
  }
}
