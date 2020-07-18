package com.facebook.imagepipeline.core;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.producers.AddImageTransformMetaDataProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheGetProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheKeyMultiplexProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.BitmapPrepareProducer;
import com.facebook.imagepipeline.producers.BranchOnSeparateImagesProducer;
import com.facebook.imagepipeline.producers.DataFetchProducer;
import com.facebook.imagepipeline.producers.DecodeProducer;
import com.facebook.imagepipeline.producers.DiskCacheReadProducer;
import com.facebook.imagepipeline.producers.DiskCacheWriteProducer;
import com.facebook.imagepipeline.producers.EncodedCacheKeyMultiplexProducer;
import com.facebook.imagepipeline.producers.EncodedMemoryCacheProducer;
import com.facebook.imagepipeline.producers.LocalAssetFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriThumbnailFetchProducer;
import com.facebook.imagepipeline.producers.LocalExifThumbnailProducer;
import com.facebook.imagepipeline.producers.LocalFileFetchProducer;
import com.facebook.imagepipeline.producers.LocalResourceFetchProducer;
import com.facebook.imagepipeline.producers.LocalVideoThumbnailProducer;
import com.facebook.imagepipeline.producers.NetworkFetchProducer;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.producers.NullProducer;
import com.facebook.imagepipeline.producers.PartialDiskCacheProducer;
import com.facebook.imagepipeline.producers.PostprocessedBitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.PostprocessorProducer;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.QualifiedResourceFetchProducer;
import com.facebook.imagepipeline.producers.ResizeAndRotateProducer;
import com.facebook.imagepipeline.producers.SwallowResultProducer;
import com.facebook.imagepipeline.producers.ThreadHandoffProducer;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.producers.ThrottlingProducer;
import com.facebook.imagepipeline.producers.ThumbnailBranchProducer;
import com.facebook.imagepipeline.producers.ThumbnailProducer;
import com.facebook.imagepipeline.producers.WebpTranscodeProducer;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;

public class ProducerFactory
{
  private static final int MAX_SIMULTANEOUS_REQUESTS = 5;
  private AssetManager mAssetManager;
  private final MemoryCache<CacheKey, CloseableImage> mBitmapMemoryCache;
  private boolean mBitmapPrepareToDrawForPrefetch;
  private final int mBitmapPrepareToDrawMaxSizeBytes;
  private final int mBitmapPrepareToDrawMinSizeBytes;
  private final ByteArrayPool mByteArrayPool;
  private final CacheKeyFactory mCacheKeyFactory;
  private final CloseableReferenceFactory mCloseableReferenceFactory;
  private ContentResolver mContentResolver;
  private final boolean mDecodeCancellationEnabled;
  private final BufferedDiskCache mDefaultBufferedDiskCache;
  private final boolean mDownsampleEnabled;
  private final MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;
  private final ExecutorSupplier mExecutorSupplier;
  private final ImageDecoder mImageDecoder;
  private final int mMaxBitmapSize;
  private final PlatformBitmapFactory mPlatformBitmapFactory;
  private final PooledByteBufferFactory mPooledByteBufferFactory;
  private final ProgressiveJpegConfig mProgressiveJpegConfig;
  private final boolean mResizeAndRotateEnabledForNetwork;
  private Resources mResources;
  private final BufferedDiskCache mSmallImageBufferedDiskCache;
  
  public ProducerFactory(Context paramContext, ByteArrayPool paramByteArrayPool, ImageDecoder paramImageDecoder, ProgressiveJpegConfig paramProgressiveJpegConfig, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, ExecutorSupplier paramExecutorSupplier, PooledByteBufferFactory paramPooledByteBufferFactory, MemoryCache paramMemoryCache1, MemoryCache paramMemoryCache2, BufferedDiskCache paramBufferedDiskCache1, BufferedDiskCache paramBufferedDiskCache2, CacheKeyFactory paramCacheKeyFactory, PlatformBitmapFactory paramPlatformBitmapFactory, int paramInt1, int paramInt2, boolean paramBoolean4, int paramInt3, CloseableReferenceFactory paramCloseableReferenceFactory)
  {
    mContentResolver = paramContext.getApplicationContext().getContentResolver();
    mResources = paramContext.getApplicationContext().getResources();
    mAssetManager = paramContext.getApplicationContext().getAssets();
    mByteArrayPool = paramByteArrayPool;
    mImageDecoder = paramImageDecoder;
    mProgressiveJpegConfig = paramProgressiveJpegConfig;
    mDownsampleEnabled = paramBoolean1;
    mResizeAndRotateEnabledForNetwork = paramBoolean2;
    mDecodeCancellationEnabled = paramBoolean3;
    mExecutorSupplier = paramExecutorSupplier;
    mPooledByteBufferFactory = paramPooledByteBufferFactory;
    mBitmapMemoryCache = paramMemoryCache1;
    mEncodedMemoryCache = paramMemoryCache2;
    mDefaultBufferedDiskCache = paramBufferedDiskCache1;
    mSmallImageBufferedDiskCache = paramBufferedDiskCache2;
    mCacheKeyFactory = paramCacheKeyFactory;
    mPlatformBitmapFactory = paramPlatformBitmapFactory;
    mBitmapPrepareToDrawMinSizeBytes = paramInt1;
    mBitmapPrepareToDrawMaxSizeBytes = paramInt2;
    mBitmapPrepareToDrawForPrefetch = paramBoolean4;
    mMaxBitmapSize = paramInt3;
    mCloseableReferenceFactory = paramCloseableReferenceFactory;
  }
  
  public static AddImageTransformMetaDataProducer newAddImageTransformMetaDataProducer(Producer paramProducer)
  {
    return new AddImageTransformMetaDataProducer(paramProducer);
  }
  
  public static BranchOnSeparateImagesProducer newBranchOnSeparateImagesProducer(Producer paramProducer1, Producer paramProducer2)
  {
    return new BranchOnSeparateImagesProducer(paramProducer1, paramProducer2);
  }
  
  public static NullProducer newNullProducer()
  {
    return new NullProducer();
  }
  
  public static SwallowResultProducer newSwallowResultProducer(Producer paramProducer)
  {
    return new SwallowResultProducer(paramProducer);
  }
  
  public ThreadHandoffProducer newBackgroundThreadHandoffProducer(Producer paramProducer, ThreadHandoffProducerQueue paramThreadHandoffProducerQueue)
  {
    return new ThreadHandoffProducer(paramProducer, paramThreadHandoffProducerQueue);
  }
  
  public BitmapMemoryCacheGetProducer newBitmapMemoryCacheGetProducer(Producer paramProducer)
  {
    return new BitmapMemoryCacheGetProducer(mBitmapMemoryCache, mCacheKeyFactory, paramProducer);
  }
  
  public BitmapMemoryCacheKeyMultiplexProducer newBitmapMemoryCacheKeyMultiplexProducer(Producer paramProducer)
  {
    return new BitmapMemoryCacheKeyMultiplexProducer(mCacheKeyFactory, paramProducer);
  }
  
  public BitmapMemoryCacheProducer newBitmapMemoryCacheProducer(Producer paramProducer)
  {
    return new BitmapMemoryCacheProducer(mBitmapMemoryCache, mCacheKeyFactory, paramProducer);
  }
  
  public BitmapPrepareProducer newBitmapPrepareProducer(Producer paramProducer)
  {
    return new BitmapPrepareProducer(paramProducer, mBitmapPrepareToDrawMinSizeBytes, mBitmapPrepareToDrawMaxSizeBytes, mBitmapPrepareToDrawForPrefetch);
  }
  
  public DataFetchProducer newDataFetchProducer()
  {
    return new DataFetchProducer(mPooledByteBufferFactory);
  }
  
  public DecodeProducer newDecodeProducer(Producer paramProducer)
  {
    return new DecodeProducer(mByteArrayPool, mExecutorSupplier.forDecode(), mImageDecoder, mProgressiveJpegConfig, mDownsampleEnabled, mResizeAndRotateEnabledForNetwork, mDecodeCancellationEnabled, paramProducer, mMaxBitmapSize, mCloseableReferenceFactory);
  }
  
  public DiskCacheReadProducer newDiskCacheReadProducer(Producer paramProducer)
  {
    return new DiskCacheReadProducer(mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache, mCacheKeyFactory, paramProducer);
  }
  
  public DiskCacheWriteProducer newDiskCacheWriteProducer(Producer paramProducer)
  {
    return new DiskCacheWriteProducer(mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache, mCacheKeyFactory, paramProducer);
  }
  
  public EncodedCacheKeyMultiplexProducer newEncodedCacheKeyMultiplexProducer(Producer paramProducer)
  {
    return new EncodedCacheKeyMultiplexProducer(mCacheKeyFactory, paramProducer);
  }
  
  public EncodedMemoryCacheProducer newEncodedMemoryCacheProducer(Producer paramProducer)
  {
    return new EncodedMemoryCacheProducer(mEncodedMemoryCache, mCacheKeyFactory, paramProducer);
  }
  
  public LocalAssetFetchProducer newLocalAssetFetchProducer()
  {
    return new LocalAssetFetchProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory, mAssetManager);
  }
  
  public LocalContentUriFetchProducer newLocalContentUriFetchProducer()
  {
    return new LocalContentUriFetchProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory, mContentResolver);
  }
  
  public LocalContentUriThumbnailFetchProducer newLocalContentUriThumbnailFetchProducer()
  {
    return new LocalContentUriThumbnailFetchProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory, mContentResolver);
  }
  
  public LocalExifThumbnailProducer newLocalExifThumbnailProducer()
  {
    return new LocalExifThumbnailProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory, mContentResolver);
  }
  
  public LocalFileFetchProducer newLocalFileFetchProducer()
  {
    return new LocalFileFetchProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory);
  }
  
  public LocalResourceFetchProducer newLocalResourceFetchProducer()
  {
    return new LocalResourceFetchProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory, mResources);
  }
  
  public LocalVideoThumbnailProducer newLocalVideoThumbnailProducer()
  {
    return new LocalVideoThumbnailProducer(mExecutorSupplier.forLocalStorageRead(), mContentResolver);
  }
  
  public NetworkFetchProducer newNetworkFetchProducer(NetworkFetcher paramNetworkFetcher)
  {
    return new NetworkFetchProducer(mPooledByteBufferFactory, mByteArrayPool, paramNetworkFetcher);
  }
  
  public PartialDiskCacheProducer newPartialDiskCacheProducer(Producer paramProducer)
  {
    return new PartialDiskCacheProducer(mDefaultBufferedDiskCache, mCacheKeyFactory, mPooledByteBufferFactory, mByteArrayPool, paramProducer);
  }
  
  public PostprocessedBitmapMemoryCacheProducer newPostprocessorBitmapMemoryCacheProducer(Producer paramProducer)
  {
    return new PostprocessedBitmapMemoryCacheProducer(mBitmapMemoryCache, mCacheKeyFactory, paramProducer);
  }
  
  public PostprocessorProducer newPostprocessorProducer(Producer paramProducer)
  {
    return new PostprocessorProducer(paramProducer, mPlatformBitmapFactory, mExecutorSupplier.forBackgroundTasks());
  }
  
  public QualifiedResourceFetchProducer newQualifiedResourceFetchProducer()
  {
    return new QualifiedResourceFetchProducer(mExecutorSupplier.forLocalStorageRead(), mPooledByteBufferFactory, mContentResolver);
  }
  
  public ResizeAndRotateProducer newResizeAndRotateProducer(Producer paramProducer, boolean paramBoolean, ImageTranscoderFactory paramImageTranscoderFactory)
  {
    return new ResizeAndRotateProducer(mExecutorSupplier.forBackgroundTasks(), mPooledByteBufferFactory, paramProducer, paramBoolean, paramImageTranscoderFactory);
  }
  
  public ThrottlingProducer newThrottlingProducer(Producer paramProducer)
  {
    return new ThrottlingProducer(5, mExecutorSupplier.forLightweightBackgroundTasks(), paramProducer);
  }
  
  public ThumbnailBranchProducer newThumbnailBranchProducer(ThumbnailProducer[] paramArrayOfThumbnailProducer)
  {
    return new ThumbnailBranchProducer(paramArrayOfThumbnailProducer);
  }
  
  public WebpTranscodeProducer newWebpTranscodeProducer(Producer paramProducer)
  {
    return new WebpTranscodeProducer(mExecutorSupplier.forBackgroundTasks(), mPooledByteBufferFactory, paramProducer);
  }
}
