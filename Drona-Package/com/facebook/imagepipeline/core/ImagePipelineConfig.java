package com.facebook.imagepipeline.core;

import android.content.Context;
import android.graphics.Bitmap.Config;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.cache.disk.DiskCacheConfig.Builder;
import com.facebook.callercontext.CallerContextVerifier;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.webp.BitmapCreator;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.CountingMemoryCache.CacheTrimStrategy;
import com.facebook.imagepipeline.cache.ImageCacheStatsTracker;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.debug.CloseableReferenceLeakTracker;
import com.facebook.imagepipeline.debug.NoOpCloseableReferenceLeakTracker;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ImageDecoderConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

public class ImagePipelineConfig
{
  private static DefaultImageRequestConfig sDefaultImageRequestConfig = new DefaultImageRequestConfig(null);
  private final Bitmap.Config mBitmapConfig;
  private final Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier;
  private final CountingMemoryCache.CacheTrimStrategy mBitmapMemoryCacheTrimStrategy;
  private final CacheKeyFactory mCacheKeyFactory;
  @Nullable
  private final CallerContextVerifier mCallerContextVerifier;
  private final CloseableReferenceLeakTracker mCloseableReferenceLeakTracker;
  private final Context mContext;
  private final boolean mDiskCacheEnabled;
  private final boolean mDownsampleEnabled;
  private final Supplier<MemoryCacheParams> mEncodedMemoryCacheParamsSupplier;
  private final ExecutorSupplier mExecutorSupplier;
  private final FileCacheFactory mFileCacheFactory;
  private final int mHttpNetworkTimeout;
  private final ImageCacheStatsTracker mImageCacheStatsTracker;
  @Nullable
  private final ImageDecoder mImageDecoder;
  @Nullable
  private final ImageDecoderConfig mImageDecoderConfig;
  private final ImagePipelineExperiments mImagePipelineExperiments;
  @Nullable
  private final ImageTranscoderFactory mImageTranscoderFactory;
  @Nullable
  private final Integer mImageTranscoderType;
  private final Supplier<Boolean> mIsPrefetchEnabledSupplier;
  private final DiskCacheConfig mMainDiskCacheConfig;
  private final int mMemoryChunkType;
  private final MemoryTrimmableRegistry mMemoryTrimmableRegistry;
  private final NetworkFetcher mNetworkFetcher;
  @Nullable
  private final PlatformBitmapFactory mPlatformBitmapFactory;
  private final PoolFactory mPoolFactory;
  private final ProgressiveJpegConfig mProgressiveJpegConfig;
  private final Set<RequestListener> mRequestListeners;
  private final boolean mResizeAndRotateEnabledForNetwork;
  private final DiskCacheConfig mSmallImageDiskCacheConfig;
  
  private ImagePipelineConfig(Builder paramBuilder) {}
  
  public static DefaultImageRequestConfig getDefaultImageRequestConfig()
  {
    return sDefaultImageRequestConfig;
  }
  
  private static DiskCacheConfig getDefaultMainDiskCacheConfig(Context paramContext)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("DiskCacheConfig.getDefaultMainDiskCacheConfig");
      }
      paramContext = DiskCacheConfig.newBuilder(paramContext).build();
      if (FrescoSystrace.isTracing())
      {
        FrescoSystrace.endSection();
        return paramContext;
      }
    }
    catch (Throwable paramContext)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramContext;
    }
    return paramContext;
  }
  
  private static ImageTranscoderFactory getImageTranscoderFactory(Builder paramBuilder)
  {
    if ((mImageTranscoderFactory != null) && (mImageTranscoderType != null)) {
      throw new IllegalStateException("You can't define a custom ImageTranscoderFactory and provide an ImageTranscoderType");
    }
    if (mImageTranscoderFactory != null) {
      return mImageTranscoderFactory;
    }
    return null;
  }
  
  private static int getMemoryChunkType(Builder paramBuilder, ImagePipelineExperiments paramImagePipelineExperiments)
  {
    if (mMemoryChunkType != null) {
      return mMemoryChunkType.intValue();
    }
    if (paramImagePipelineExperiments.isNativeCodeDisabled()) {
      return 1;
    }
    return 0;
  }
  
  public static Builder newBuilder(Context paramContext)
  {
    return new Builder(paramContext, null);
  }
  
  static void resetDefaultRequestConfig()
  {
    sDefaultImageRequestConfig = new DefaultImageRequestConfig(null);
  }
  
  private static void setWebpBitmapFactory(WebpBitmapFactory paramWebpBitmapFactory, ImagePipelineExperiments paramImagePipelineExperiments, BitmapCreator paramBitmapCreator)
  {
    com.facebook.common.webp.WebpSupportStatus.sWebpBitmapFactory = paramWebpBitmapFactory;
    paramImagePipelineExperiments = paramImagePipelineExperiments.getWebpErrorLogger();
    if (paramImagePipelineExperiments != null) {
      paramWebpBitmapFactory.setWebpErrorLogger(paramImagePipelineExperiments);
    }
    if (paramBitmapCreator != null) {
      paramWebpBitmapFactory.setBitmapCreator(paramBitmapCreator);
    }
  }
  
  public Bitmap.Config getBitmapConfig()
  {
    return mBitmapConfig;
  }
  
  public Supplier getBitmapMemoryCacheParamsSupplier()
  {
    return mBitmapMemoryCacheParamsSupplier;
  }
  
  public CountingMemoryCache.CacheTrimStrategy getBitmapMemoryCacheTrimStrategy()
  {
    return mBitmapMemoryCacheTrimStrategy;
  }
  
  public CacheKeyFactory getCacheKeyFactory()
  {
    return mCacheKeyFactory;
  }
  
  public CallerContextVerifier getCallerContextVerifier()
  {
    return mCallerContextVerifier;
  }
  
  public CloseableReferenceLeakTracker getCloseableReferenceLeakTracker()
  {
    return mCloseableReferenceLeakTracker;
  }
  
  public Context getContext()
  {
    return mContext;
  }
  
  public Supplier getEncodedMemoryCacheParamsSupplier()
  {
    return mEncodedMemoryCacheParamsSupplier;
  }
  
  public ExecutorSupplier getExecutorSupplier()
  {
    return mExecutorSupplier;
  }
  
  public ImagePipelineExperiments getExperiments()
  {
    return mImagePipelineExperiments;
  }
  
  public FileCacheFactory getFileCacheFactory()
  {
    return mFileCacheFactory;
  }
  
  public ImageCacheStatsTracker getImageCacheStatsTracker()
  {
    return mImageCacheStatsTracker;
  }
  
  public ImageDecoder getImageDecoder()
  {
    return mImageDecoder;
  }
  
  public ImageDecoderConfig getImageDecoderConfig()
  {
    return mImageDecoderConfig;
  }
  
  public ImageTranscoderFactory getImageTranscoderFactory()
  {
    return mImageTranscoderFactory;
  }
  
  public Integer getImageTranscoderType()
  {
    return mImageTranscoderType;
  }
  
  public Supplier getIsPrefetchEnabledSupplier()
  {
    return mIsPrefetchEnabledSupplier;
  }
  
  public DiskCacheConfig getMainDiskCacheConfig()
  {
    return mMainDiskCacheConfig;
  }
  
  public int getMemoryChunkType()
  {
    return mMemoryChunkType;
  }
  
  public MemoryTrimmableRegistry getMemoryTrimmableRegistry()
  {
    return mMemoryTrimmableRegistry;
  }
  
  public NetworkFetcher getNetworkFetcher()
  {
    return mNetworkFetcher;
  }
  
  public PlatformBitmapFactory getPlatformBitmapFactory()
  {
    return mPlatformBitmapFactory;
  }
  
  public PoolFactory getPoolFactory()
  {
    return mPoolFactory;
  }
  
  public ProgressiveJpegConfig getProgressiveJpegConfig()
  {
    return mProgressiveJpegConfig;
  }
  
  public Set getRequestListeners()
  {
    return Collections.unmodifiableSet(mRequestListeners);
  }
  
  public DiskCacheConfig getSmallImageDiskCacheConfig()
  {
    return mSmallImageDiskCacheConfig;
  }
  
  public boolean isDiskCacheEnabled()
  {
    return mDiskCacheEnabled;
  }
  
  public boolean isDownsampleEnabled()
  {
    return mDownsampleEnabled;
  }
  
  public boolean isResizeAndRotateEnabledForNetwork()
  {
    return mResizeAndRotateEnabledForNetwork;
  }
  
  public static class Builder
  {
    private Bitmap.Config mBitmapConfig;
    private Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier;
    private CountingMemoryCache.CacheTrimStrategy mBitmapMemoryCacheTrimStrategy;
    private CacheKeyFactory mCacheKeyFactory;
    private CallerContextVerifier mCallerContextVerifier;
    private CloseableReferenceLeakTracker mCloseableReferenceLeakTracker = new NoOpCloseableReferenceLeakTracker();
    private final Context mContext;
    private boolean mDiskCacheEnabled = true;
    private boolean mDownsampleEnabled = false;
    private Supplier<MemoryCacheParams> mEncodedMemoryCacheParamsSupplier;
    private ExecutorSupplier mExecutorSupplier;
    private final ImagePipelineExperiments.Builder mExperimentsBuilder = new ImagePipelineExperiments.Builder(this);
    private FileCacheFactory mFileCacheFactory;
    private int mHttpConnectionTimeout = -1;
    private ImageCacheStatsTracker mImageCacheStatsTracker;
    private ImageDecoder mImageDecoder;
    private ImageDecoderConfig mImageDecoderConfig;
    private ImageTranscoderFactory mImageTranscoderFactory;
    @Nullable
    private Integer mImageTranscoderType = null;
    private Supplier<Boolean> mIsPrefetchEnabledSupplier;
    private DiskCacheConfig mMainDiskCacheConfig;
    @Nullable
    private Integer mMemoryChunkType = null;
    private MemoryTrimmableRegistry mMemoryTrimmableRegistry;
    private NetworkFetcher mNetworkFetcher;
    private PlatformBitmapFactory mPlatformBitmapFactory;
    private PoolFactory mPoolFactory;
    private ProgressiveJpegConfig mProgressiveJpegConfig;
    private Set<RequestListener> mRequestListeners;
    private boolean mResizeAndRotateEnabledForNetwork = true;
    private DiskCacheConfig mSmallImageDiskCacheConfig;
    
    private Builder(Context paramContext)
    {
      mContext = ((Context)Preconditions.checkNotNull(paramContext));
    }
    
    public ImagePipelineConfig build()
    {
      return new ImagePipelineConfig(this, null);
    }
    
    public ImagePipelineExperiments.Builder experiment()
    {
      return mExperimentsBuilder;
    }
    
    public Integer getImageTranscoderType()
    {
      return mImageTranscoderType;
    }
    
    public Integer getMemoryChunkType()
    {
      return mMemoryChunkType;
    }
    
    public boolean isDiskCacheEnabled()
    {
      return mDiskCacheEnabled;
    }
    
    public boolean isDownsampleEnabled()
    {
      return mDownsampleEnabled;
    }
    
    public Builder setBitmapMemoryCacheParamsSupplier(Supplier paramSupplier)
    {
      mBitmapMemoryCacheParamsSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
      return this;
    }
    
    public Builder setBitmapMemoryCacheTrimStrategy(CountingMemoryCache.CacheTrimStrategy paramCacheTrimStrategy)
    {
      mBitmapMemoryCacheTrimStrategy = paramCacheTrimStrategy;
      return this;
    }
    
    public Builder setBitmapsConfig(Bitmap.Config paramConfig)
    {
      mBitmapConfig = paramConfig;
      return this;
    }
    
    public Builder setCacheKeyFactory(CacheKeyFactory paramCacheKeyFactory)
    {
      mCacheKeyFactory = paramCacheKeyFactory;
      return this;
    }
    
    public Builder setCallerContextVerifier(CallerContextVerifier paramCallerContextVerifier)
    {
      mCallerContextVerifier = paramCallerContextVerifier;
      return this;
    }
    
    public Builder setCloseableReferenceLeakTracker(CloseableReferenceLeakTracker paramCloseableReferenceLeakTracker)
    {
      mCloseableReferenceLeakTracker = paramCloseableReferenceLeakTracker;
      return this;
    }
    
    public Builder setDiskCacheEnabled(boolean paramBoolean)
    {
      mDiskCacheEnabled = paramBoolean;
      return this;
    }
    
    public Builder setDownsampleEnabled(boolean paramBoolean)
    {
      mDownsampleEnabled = paramBoolean;
      return this;
    }
    
    public Builder setEncodedMemoryCacheParamsSupplier(Supplier paramSupplier)
    {
      mEncodedMemoryCacheParamsSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
      return this;
    }
    
    public Builder setExecutorSupplier(ExecutorSupplier paramExecutorSupplier)
    {
      mExecutorSupplier = paramExecutorSupplier;
      return this;
    }
    
    public Builder setFileCacheFactory(FileCacheFactory paramFileCacheFactory)
    {
      mFileCacheFactory = paramFileCacheFactory;
      return this;
    }
    
    public Builder setHttpConnectionTimeout(int paramInt)
    {
      mHttpConnectionTimeout = paramInt;
      return this;
    }
    
    public Builder setImageCacheStatsTracker(ImageCacheStatsTracker paramImageCacheStatsTracker)
    {
      mImageCacheStatsTracker = paramImageCacheStatsTracker;
      return this;
    }
    
    public Builder setImageDecoder(ImageDecoder paramImageDecoder)
    {
      mImageDecoder = paramImageDecoder;
      return this;
    }
    
    public Builder setImageDecoderConfig(ImageDecoderConfig paramImageDecoderConfig)
    {
      mImageDecoderConfig = paramImageDecoderConfig;
      return this;
    }
    
    public Builder setImageTranscoderFactory(ImageTranscoderFactory paramImageTranscoderFactory)
    {
      mImageTranscoderFactory = paramImageTranscoderFactory;
      return this;
    }
    
    public Builder setImageTranscoderType(int paramInt)
    {
      mImageTranscoderType = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder setIsPrefetchEnabledSupplier(Supplier paramSupplier)
    {
      mIsPrefetchEnabledSupplier = paramSupplier;
      return this;
    }
    
    public Builder setMainDiskCacheConfig(DiskCacheConfig paramDiskCacheConfig)
    {
      mMainDiskCacheConfig = paramDiskCacheConfig;
      return this;
    }
    
    public Builder setMemoryChunkType(int paramInt)
    {
      mMemoryChunkType = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder setMemoryTrimmableRegistry(MemoryTrimmableRegistry paramMemoryTrimmableRegistry)
    {
      mMemoryTrimmableRegistry = paramMemoryTrimmableRegistry;
      return this;
    }
    
    public Builder setNetworkFetcher(NetworkFetcher paramNetworkFetcher)
    {
      mNetworkFetcher = paramNetworkFetcher;
      return this;
    }
    
    public Builder setPlatformBitmapFactory(PlatformBitmapFactory paramPlatformBitmapFactory)
    {
      mPlatformBitmapFactory = paramPlatformBitmapFactory;
      return this;
    }
    
    public Builder setPoolFactory(PoolFactory paramPoolFactory)
    {
      mPoolFactory = paramPoolFactory;
      return this;
    }
    
    public Builder setProgressiveJpegConfig(ProgressiveJpegConfig paramProgressiveJpegConfig)
    {
      mProgressiveJpegConfig = paramProgressiveJpegConfig;
      return this;
    }
    
    public Builder setRequestListeners(Set paramSet)
    {
      mRequestListeners = paramSet;
      return this;
    }
    
    public Builder setResizeAndRotateEnabledForNetwork(boolean paramBoolean)
    {
      mResizeAndRotateEnabledForNetwork = paramBoolean;
      return this;
    }
    
    public Builder setSmallImageDiskCacheConfig(DiskCacheConfig paramDiskCacheConfig)
    {
      mSmallImageDiskCacheConfig = paramDiskCacheConfig;
      return this;
    }
  }
  
  public static class DefaultImageRequestConfig
  {
    private boolean mProgressiveRenderingEnabled = false;
    
    private DefaultImageRequestConfig() {}
    
    public boolean isProgressiveRenderingEnabled()
    {
      return mProgressiveRenderingEnabled;
    }
    
    public void setProgressiveRenderingEnabled(boolean paramBoolean)
    {
      mProgressiveRenderingEnabled = paramBoolean;
    }
  }
}