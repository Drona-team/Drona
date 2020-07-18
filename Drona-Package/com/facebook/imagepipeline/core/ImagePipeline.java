package com.facebook.imagepipeline.core;

import android.net.Uri;
import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.callercontext.CallerContextVerifier;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Predicate;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.datasource.SimpleDataSource;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.datasource.CloseableProducerToDataSourceAdapter;
import com.facebook.imagepipeline.datasource.ProducerToDataSourceAdapter;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.listener.ForwardingRequestListener;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ImagePipeline
{
  private static final CancellationException PREFETCH_EXCEPTION = new CancellationException("Prefetching is not enabled");
  private final MemoryCache<CacheKey, CloseableImage> mBitmapMemoryCache;
  private final CacheKeyFactory mCacheKeyFactory;
  @Nullable
  private final CallerContextVerifier mCallerContextVerifier;
  private final MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;
  private AtomicLong mIdCounter = new AtomicLong();
  private final Supplier<Boolean> mIsPrefetchEnabledSupplier;
  private final Supplier<Boolean> mLazyDataSource;
  private final BufferedDiskCache mMainBufferedDiskCache;
  private final ProducerSequenceFactory mProducerSequenceFactory;
  private final RequestListener mRequestListener;
  private final BufferedDiskCache mSmallImageBufferedDiskCache;
  private final Supplier<Boolean> mSuppressBitmapPrefetchingSupplier;
  private final ThreadHandoffProducerQueue mThreadHandoffProducerQueue;
  
  public ImagePipeline(ProducerSequenceFactory paramProducerSequenceFactory, Set paramSet, Supplier paramSupplier1, MemoryCache paramMemoryCache1, MemoryCache paramMemoryCache2, BufferedDiskCache paramBufferedDiskCache1, BufferedDiskCache paramBufferedDiskCache2, CacheKeyFactory paramCacheKeyFactory, ThreadHandoffProducerQueue paramThreadHandoffProducerQueue, Supplier paramSupplier2, Supplier paramSupplier3, CallerContextVerifier paramCallerContextVerifier)
  {
    mProducerSequenceFactory = paramProducerSequenceFactory;
    mRequestListener = new ForwardingRequestListener(paramSet);
    mIsPrefetchEnabledSupplier = paramSupplier1;
    mBitmapMemoryCache = paramMemoryCache1;
    mEncodedMemoryCache = paramMemoryCache2;
    mMainBufferedDiskCache = paramBufferedDiskCache1;
    mSmallImageBufferedDiskCache = paramBufferedDiskCache2;
    mCacheKeyFactory = paramCacheKeyFactory;
    mThreadHandoffProducerQueue = paramThreadHandoffProducerQueue;
    mSuppressBitmapPrefetchingSupplier = paramSupplier2;
    mLazyDataSource = paramSupplier3;
    mCallerContextVerifier = paramCallerContextVerifier;
  }
  
  private Predicate predicateForUri(final Uri paramUri)
  {
    new Predicate()
    {
      public boolean apply(CacheKey paramAnonymousCacheKey)
      {
        return paramAnonymousCacheKey.containsUri(paramUri);
      }
    };
  }
  
  private DataSource submitFetchRequest(Producer paramProducer, ImageRequest paramImageRequest, ImageRequest.RequestLevel paramRequestLevel, Object paramObject, RequestListener paramRequestListener)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ImagePipeline#submitFetchRequest");
    }
    paramRequestListener = getRequestListenerForRequest(paramImageRequest, paramRequestListener);
    if (mCallerContextVerifier != null) {
      mCallerContextVerifier.verifyCallerContext(paramObject);
    }
    try
    {
      paramRequestLevel = ImageRequest.RequestLevel.getMax(paramImageRequest.getLowestPermittedRequestLevel(), paramRequestLevel);
      String str = generateUniqueFutureId();
      boolean bool = paramImageRequest.getProgressiveRenderingEnabled();
      if (!bool)
      {
        bool = UriUtil.isNetworkUri(paramImageRequest.getSourceUri());
        if (bool)
        {
          bool = false;
          break label90;
        }
      }
      bool = true;
      label90:
      paramImageRequest = CloseableProducerToDataSourceAdapter.create(paramProducer, new SettableProducerContext(paramImageRequest, str, paramRequestListener, paramObject, paramRequestLevel, false, bool, paramImageRequest.getPriority()), paramRequestListener);
      paramProducer = paramImageRequest;
      if (!FrescoSystrace.isTracing()) {
        return paramProducer;
      }
      FrescoSystrace.endSection();
      return paramImageRequest;
    }
    catch (Throwable paramProducer) {}catch (Exception paramProducer)
    {
      paramImageRequest = DataSources.immediateFailedDataSource(paramProducer);
      paramProducer = paramImageRequest;
      if (!FrescoSystrace.isTracing()) {
        return paramProducer;
      }
    }
    FrescoSystrace.endSection();
    return paramImageRequest;
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    throw paramProducer;
    return paramProducer;
  }
  
  private DataSource submitPrefetchRequest(Producer paramProducer, ImageRequest paramImageRequest, ImageRequest.RequestLevel paramRequestLevel, Object paramObject, Priority paramPriority)
  {
    RequestListener localRequestListener = getRequestListenerForRequest(paramImageRequest, null);
    if (mCallerContextVerifier != null) {
      mCallerContextVerifier.verifyCallerContext(paramObject);
    }
    try
    {
      paramRequestLevel = ImageRequest.RequestLevel.getMax(paramImageRequest.getLowestPermittedRequestLevel(), paramRequestLevel);
      paramProducer = ProducerToDataSourceAdapter.create(paramProducer, new SettableProducerContext(paramImageRequest, generateUniqueFutureId(), localRequestListener, paramObject, paramRequestLevel, true, false, paramPriority), localRequestListener);
      return paramProducer;
    }
    catch (Exception paramProducer) {}
    return DataSources.immediateFailedDataSource(paramProducer);
  }
  
  public void clearCaches()
  {
    clearMemoryCaches();
    clearDiskCaches();
  }
  
  public void clearDiskCaches()
  {
    mMainBufferedDiskCache.clearAll();
    mSmallImageBufferedDiskCache.clearAll();
  }
  
  public void clearMemoryCaches()
  {
    Predicate local4 = new Predicate()
    {
      public boolean apply(CacheKey paramAnonymousCacheKey)
      {
        return true;
      }
    };
    mBitmapMemoryCache.removeAll(local4);
    mEncodedMemoryCache.removeAll(local4);
  }
  
  public void evictFromCache(Uri paramUri)
  {
    evictFromMemoryCache(paramUri);
    evictFromDiskCache(paramUri);
  }
  
  public void evictFromDiskCache(Uri paramUri)
  {
    evictFromDiskCache(ImageRequest.fromUri(paramUri));
  }
  
  public void evictFromDiskCache(ImageRequest paramImageRequest)
  {
    paramImageRequest = mCacheKeyFactory.getEncodedCacheKey(paramImageRequest, null);
    mMainBufferedDiskCache.remove(paramImageRequest);
    mSmallImageBufferedDiskCache.remove(paramImageRequest);
  }
  
  public void evictFromMemoryCache(Uri paramUri)
  {
    paramUri = predicateForUri(paramUri);
    mBitmapMemoryCache.removeAll(paramUri);
    mEncodedMemoryCache.removeAll(paramUri);
  }
  
  public DataSource fetchDecodedImage(ImageRequest paramImageRequest, Object paramObject)
  {
    return fetchDecodedImage(paramImageRequest, paramObject, ImageRequest.RequestLevel.FULL_FETCH);
  }
  
  public DataSource fetchDecodedImage(ImageRequest paramImageRequest, Object paramObject, RequestListener paramRequestListener)
  {
    return fetchDecodedImage(paramImageRequest, paramObject, ImageRequest.RequestLevel.FULL_FETCH, paramRequestListener);
  }
  
  public DataSource fetchDecodedImage(ImageRequest paramImageRequest, Object paramObject, ImageRequest.RequestLevel paramRequestLevel)
  {
    return fetchDecodedImage(paramImageRequest, paramObject, paramRequestLevel, null);
  }
  
  public DataSource fetchDecodedImage(ImageRequest paramImageRequest, Object paramObject, ImageRequest.RequestLevel paramRequestLevel, RequestListener paramRequestListener)
  {
    ProducerSequenceFactory localProducerSequenceFactory = mProducerSequenceFactory;
    try
    {
      paramImageRequest = submitFetchRequest(localProducerSequenceFactory.getDecodedImageProducerSequence(paramImageRequest), paramImageRequest, paramRequestLevel, paramObject, paramRequestListener);
      return paramImageRequest;
    }
    catch (Exception paramImageRequest) {}
    return DataSources.immediateFailedDataSource(paramImageRequest);
  }
  
  public DataSource fetchEncodedImage(ImageRequest paramImageRequest, Object paramObject)
  {
    return fetchEncodedImage(paramImageRequest, paramObject, null);
  }
  
  public DataSource fetchEncodedImage(ImageRequest paramImageRequest, Object paramObject, RequestListener paramRequestListener)
  {
    Preconditions.checkNotNull(paramImageRequest.getSourceUri());
    Object localObject = mProducerSequenceFactory;
    try
    {
      Producer localProducer = ((ProducerSequenceFactory)localObject).getEncodedImageProducerSequence(paramImageRequest);
      ResizeOptions localResizeOptions = paramImageRequest.getResizeOptions();
      localObject = paramImageRequest;
      if (localResizeOptions != null) {
        localObject = ImageRequestBuilder.fromRequest(paramImageRequest).setResizeOptions(null).build();
      }
      paramImageRequest = ImageRequest.RequestLevel.FULL_FETCH;
      paramImageRequest = submitFetchRequest(localProducer, (ImageRequest)localObject, paramImageRequest, paramObject, paramRequestListener);
      return paramImageRequest;
    }
    catch (Exception paramImageRequest) {}
    return DataSources.immediateFailedDataSource(paramImageRequest);
  }
  
  public DataSource fetchImageFromBitmapCache(ImageRequest paramImageRequest, Object paramObject)
  {
    return fetchDecodedImage(paramImageRequest, paramObject, ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE);
  }
  
  public String generateUniqueFutureId()
  {
    return String.valueOf(mIdCounter.getAndIncrement());
  }
  
  public MemoryCache getBitmapMemoryCache()
  {
    return mBitmapMemoryCache;
  }
  
  public CacheKey getCacheKey(ImageRequest paramImageRequest, Object paramObject)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ImagePipeline#getCacheKey");
    }
    CacheKeyFactory localCacheKeyFactory = mCacheKeyFactory;
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (localCacheKeyFactory != null)
    {
      localObject1 = localObject2;
      if (paramImageRequest != null) {
        if (paramImageRequest.getPostprocessor() != null) {
          localObject1 = localCacheKeyFactory.getPostprocessedBitmapCacheKey(paramImageRequest, paramObject);
        } else {
          localObject1 = localCacheKeyFactory.getBitmapCacheKey(paramImageRequest, paramObject);
        }
      }
    }
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return localObject1;
  }
  
  public CacheKeyFactory getCacheKeyFactory()
  {
    return mCacheKeyFactory;
  }
  
  public CloseableReference getCachedImage(CacheKey paramCacheKey)
  {
    MemoryCache localMemoryCache = mBitmapMemoryCache;
    if (localMemoryCache != null)
    {
      if (paramCacheKey == null) {
        return null;
      }
      paramCacheKey = localMemoryCache.cache(paramCacheKey);
      if (paramCacheKey != null)
      {
        if (!((CloseableImage)paramCacheKey.get()).getQualityInfo().isOfFullQuality())
        {
          paramCacheKey.close();
          return null;
        }
      }
      else {
        return paramCacheKey;
      }
    }
    else
    {
      return null;
    }
    return paramCacheKey;
  }
  
  public Supplier getDataSourceSupplier(final ImageRequest paramImageRequest, final Object paramObject, final ImageRequest.RequestLevel paramRequestLevel)
  {
    new Supplier()
    {
      public DataSource getFolder()
      {
        return fetchDecodedImage(paramImageRequest, paramObject, paramRequestLevel);
      }
      
      public String toString()
      {
        return Objects.toStringHelper(this).addValue("uri", paramImageRequest.getSourceUri()).toString();
      }
    };
  }
  
  public Supplier getDataSourceSupplier(final ImageRequest paramImageRequest, final Object paramObject, final ImageRequest.RequestLevel paramRequestLevel, final RequestListener paramRequestListener)
  {
    new Supplier()
    {
      public DataSource getFolder()
      {
        return fetchDecodedImage(paramImageRequest, paramObject, paramRequestLevel, paramRequestListener);
      }
      
      public String toString()
      {
        return Objects.toStringHelper(this).addValue("uri", paramImageRequest.getSourceUri()).toString();
      }
    };
  }
  
  public Supplier getEncodedImageDataSourceSupplier(final ImageRequest paramImageRequest, final Object paramObject)
  {
    new Supplier()
    {
      public DataSource getFolder()
      {
        return fetchEncodedImage(paramImageRequest, paramObject);
      }
      
      public String toString()
      {
        return Objects.toStringHelper(this).addValue("uri", paramImageRequest.getSourceUri()).toString();
      }
    };
  }
  
  public ProducerSequenceFactory getProducerSequenceFactory()
  {
    return mProducerSequenceFactory;
  }
  
  public RequestListener getRequestListenerForRequest(ImageRequest paramImageRequest, RequestListener paramRequestListener)
  {
    if (paramRequestListener == null)
    {
      if (paramImageRequest.getRequestListener() == null) {
        return mRequestListener;
      }
      return new ForwardingRequestListener(new RequestListener[] { mRequestListener, paramImageRequest.getRequestListener() });
    }
    if (paramImageRequest.getRequestListener() == null) {
      return new ForwardingRequestListener(new RequestListener[] { mRequestListener, paramRequestListener });
    }
    return new ForwardingRequestListener(new RequestListener[] { mRequestListener, paramRequestListener, paramImageRequest.getRequestListener() });
  }
  
  public long getUsedDiskCacheSize()
  {
    return mMainBufferedDiskCache.getSize() + mSmallImageBufferedDiskCache.getSize();
  }
  
  public boolean hasCachedImage(CacheKey paramCacheKey)
  {
    MemoryCache localMemoryCache = mBitmapMemoryCache;
    if ((localMemoryCache != null) && (paramCacheKey != null)) {
      return localMemoryCache.contains(paramCacheKey);
    }
    return false;
  }
  
  public boolean isInBitmapMemoryCache(Uri paramUri)
  {
    if (paramUri == null) {
      return false;
    }
    paramUri = predicateForUri(paramUri);
    return mBitmapMemoryCache.contains(paramUri);
  }
  
  public boolean isInBitmapMemoryCache(ImageRequest paramImageRequest)
  {
    if (paramImageRequest == null) {
      return false;
    }
    paramImageRequest = mCacheKeyFactory.getBitmapCacheKey(paramImageRequest, null);
    paramImageRequest = mBitmapMemoryCache.cache(paramImageRequest);
    try
    {
      boolean bool = CloseableReference.isValid(paramImageRequest);
      CloseableReference.closeSafely(paramImageRequest);
      return bool;
    }
    catch (Throwable localThrowable)
    {
      CloseableReference.closeSafely(paramImageRequest);
      throw localThrowable;
    }
  }
  
  public DataSource isInDiskCache(Uri paramUri)
  {
    return isInDiskCache(ImageRequest.fromUri(paramUri));
  }
  
  public DataSource isInDiskCache(final ImageRequest paramImageRequest)
  {
    paramImageRequest = mCacheKeyFactory.getEncodedCacheKey(paramImageRequest, null);
    final SimpleDataSource localSimpleDataSource = SimpleDataSource.create();
    mMainBufferedDiskCache.contains(paramImageRequest).continueWithTask(new Continuation()
    {
      public Task then(Task paramAnonymousTask)
        throws Exception
      {
        if ((!paramAnonymousTask.isCancelled()) && (!paramAnonymousTask.isFaulted()) && (((Boolean)paramAnonymousTask.getResult()).booleanValue())) {
          return Task.forResult(Boolean.valueOf(true));
        }
        return mSmallImageBufferedDiskCache.contains(paramImageRequest);
      }
    }).continueWith(new Continuation()
    {
      public Void then(Task paramAnonymousTask)
        throws Exception
      {
        SimpleDataSource localSimpleDataSource = localSimpleDataSource;
        boolean bool;
        if ((!paramAnonymousTask.isCancelled()) && (!paramAnonymousTask.isFaulted()) && (((Boolean)paramAnonymousTask.getResult()).booleanValue())) {
          bool = true;
        } else {
          bool = false;
        }
        localSimpleDataSource.setResult(Boolean.valueOf(bool));
        return null;
      }
    });
    return localSimpleDataSource;
  }
  
  public boolean isInDiskCacheSync(Uri paramUri)
  {
    return (isInDiskCacheSync(paramUri, ImageRequest.CacheChoice.SMALL)) || (isInDiskCacheSync(paramUri, ImageRequest.CacheChoice.DEFAULT));
  }
  
  public boolean isInDiskCacheSync(Uri paramUri, ImageRequest.CacheChoice paramCacheChoice)
  {
    return isInDiskCacheSync(ImageRequestBuilder.newBuilderWithSource(paramUri).setCacheChoice(paramCacheChoice).build());
  }
  
  public boolean isInDiskCacheSync(ImageRequest paramImageRequest)
  {
    CacheKey localCacheKey = mCacheKeyFactory.getEncodedCacheKey(paramImageRequest, null);
    paramImageRequest = paramImageRequest.getCacheChoice();
    switch (8.$SwitchMap$com$facebook$imagepipeline$request$ImageRequest$CacheChoice[paramImageRequest.ordinal()])
    {
    default: 
      return false;
    case 2: 
      return mSmallImageBufferedDiskCache.diskCheckSync(localCacheKey);
    }
    return mMainBufferedDiskCache.diskCheckSync(localCacheKey);
  }
  
  public Supplier isLazyDataSource()
  {
    return mLazyDataSource;
  }
  
  public boolean isPaused()
  {
    return mThreadHandoffProducerQueue.isQueueing();
  }
  
  public void pause()
  {
    mThreadHandoffProducerQueue.startQueueing();
  }
  
  public DataSource prefetchToBitmapCache(ImageRequest paramImageRequest, Object paramObject)
  {
    if (!((Boolean)mIsPrefetchEnabledSupplier.getFolder()).booleanValue()) {
      return DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
    }
    try
    {
      Object localObject = paramImageRequest.shouldDecodePrefetches();
      boolean bool;
      if (localObject != null)
      {
        bool = ((Boolean)localObject).booleanValue();
        if (!bool) {
          bool = true;
        } else {
          bool = false;
        }
      }
      else
      {
        localObject = mSuppressBitmapPrefetchingSupplier;
        localObject = ((Supplier)localObject).getFolder();
        localObject = (Boolean)localObject;
        bool = ((Boolean)localObject).booleanValue();
      }
      if (bool)
      {
        localObject = mProducerSequenceFactory;
        localObject = ((ProducerSequenceFactory)localObject).getEncodedImagePrefetchProducerSequence(paramImageRequest);
      }
      else
      {
        localObject = mProducerSequenceFactory;
        localObject = ((ProducerSequenceFactory)localObject).getDecodedImagePrefetchProducerSequence(paramImageRequest);
      }
      ImageRequest.RequestLevel localRequestLevel = ImageRequest.RequestLevel.FULL_FETCH;
      Priority localPriority = Priority.MEDIUM;
      paramImageRequest = submitPrefetchRequest((Producer)localObject, paramImageRequest, localRequestLevel, paramObject, localPriority);
      return paramImageRequest;
    }
    catch (Exception paramImageRequest) {}
    return DataSources.immediateFailedDataSource(paramImageRequest);
  }
  
  public DataSource prefetchToDiskCache(ImageRequest paramImageRequest, Object paramObject)
  {
    return prefetchToDiskCache(paramImageRequest, paramObject, Priority.MEDIUM);
  }
  
  public DataSource prefetchToDiskCache(ImageRequest paramImageRequest, Object paramObject, Priority paramPriority)
  {
    if (!((Boolean)mIsPrefetchEnabledSupplier.getFolder()).booleanValue()) {
      return DataSources.immediateFailedDataSource(PREFETCH_EXCEPTION);
    }
    Object localObject = mProducerSequenceFactory;
    try
    {
      localObject = ((ProducerSequenceFactory)localObject).getEncodedImagePrefetchProducerSequence(paramImageRequest);
      ImageRequest.RequestLevel localRequestLevel = ImageRequest.RequestLevel.FULL_FETCH;
      paramImageRequest = submitPrefetchRequest((Producer)localObject, paramImageRequest, localRequestLevel, paramObject, paramPriority);
      return paramImageRequest;
    }
    catch (Exception paramImageRequest) {}
    return DataSources.immediateFailedDataSource(paramImageRequest);
  }
  
  public void resume()
  {
    mThreadHandoffProducerQueue.stopQueuing();
  }
  
  public DataSource submitFetchRequest(Producer paramProducer, SettableProducerContext paramSettableProducerContext, RequestListener paramRequestListener)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ImagePipeline#submitFetchRequest");
    }
    try
    {
      paramSettableProducerContext = CloseableProducerToDataSourceAdapter.create(paramProducer, paramSettableProducerContext, paramRequestListener);
      paramProducer = paramSettableProducerContext;
      if (!FrescoSystrace.isTracing()) {
        return paramProducer;
      }
      FrescoSystrace.endSection();
      return paramSettableProducerContext;
    }
    catch (Throwable paramProducer) {}catch (Exception paramProducer)
    {
      paramSettableProducerContext = DataSources.immediateFailedDataSource(paramProducer);
      paramProducer = paramSettableProducerContext;
      if (!FrescoSystrace.isTracing()) {
        return paramProducer;
      }
    }
    FrescoSystrace.endSection();
    return paramSettableProducerContext;
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    throw paramProducer;
    return paramProducer;
  }
}