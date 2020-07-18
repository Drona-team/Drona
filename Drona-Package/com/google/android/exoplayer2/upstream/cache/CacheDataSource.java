package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TeeDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Map;

public final class CacheDataSource
  implements DataSource
{
  public static final int CACHE_IGNORED_REASON_ERROR = 0;
  public static final int CACHE_IGNORED_REASON_UNSET_LENGTH = 1;
  private static final int CACHE_NOT_IGNORED = -1;
  public static final long DEFAULT_MAX_CACHE_FILE_SIZE = 2097152L;
  public static final int FLAG_BLOCK_ON_CACHE = 1;
  public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
  public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
  private static final long MIN_READ_BEFORE_CHECKING_CACHE = 102400L;
  @Nullable
  private Uri actualUri;
  private final boolean blockOnCache;
  private long bytesRemaining;
  private final Cache cache;
  private final CacheKeyFactory cacheKeyFactory;
  private final DataSource cacheReadDataSource;
  @Nullable
  private final DataSource cacheWriteDataSource;
  private long checkCachePosition;
  @Nullable
  private DataSource currentDataSource;
  private boolean currentDataSpecLengthUnset;
  @Nullable
  private CacheSpan currentHoleSpan;
  private boolean currentRequestIgnoresCache;
  @Nullable
  private final EventListener eventListener;
  private int flags;
  private int httpMethod;
  private final boolean ignoreCacheForUnsetLengthRequests;
  private final boolean ignoreCacheOnError;
  @Nullable
  private String key;
  private long readPosition;
  private boolean seenCacheError;
  private long totalCachedBytesRead;
  private final DataSource upstreamDataSource;
  @Nullable
  private Uri uri;
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource)
  {
    this(paramCache, paramDataSource, 0, 2097152L);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource, int paramInt)
  {
    this(paramCache, paramDataSource, paramInt, 2097152L);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource, int paramInt, long paramLong)
  {
    this(paramCache, paramDataSource, new FileDataSource(), new CacheDataSink(paramCache, paramLong), paramInt, null);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource1, DataSource paramDataSource2, DataSink paramDataSink, int paramInt, EventListener paramEventListener)
  {
    this(paramCache, paramDataSource1, paramDataSource2, paramDataSink, paramInt, paramEventListener, null);
  }
  
  public CacheDataSource(Cache paramCache, DataSource paramDataSource1, DataSource paramDataSource2, DataSink paramDataSink, int paramInt, EventListener paramEventListener, CacheKeyFactory paramCacheKeyFactory)
  {
    cache = paramCache;
    cacheReadDataSource = paramDataSource2;
    if (paramCacheKeyFactory == null) {
      paramCacheKeyFactory = CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
    }
    cacheKeyFactory = paramCacheKeyFactory;
    boolean bool2 = false;
    if ((paramInt & 0x1) != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    blockOnCache = bool1;
    if ((paramInt & 0x2) != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    ignoreCacheOnError = bool1;
    boolean bool1 = bool2;
    if ((paramInt & 0x4) != 0) {
      bool1 = true;
    }
    ignoreCacheForUnsetLengthRequests = bool1;
    upstreamDataSource = paramDataSource1;
    if (paramDataSink != null) {
      cacheWriteDataSource = new TeeDataSource(paramDataSource1, paramDataSink);
    } else {
      cacheWriteDataSource = null;
    }
    eventListener = paramEventListener;
  }
  
  private void closeCurrentSource()
    throws IOException
  {
    if (currentDataSource == null) {
      return;
    }
    try
    {
      currentDataSource.close();
      currentDataSource = null;
      currentDataSpecLengthUnset = false;
      if (currentHoleSpan != null)
      {
        cache.releaseHoleSpan(currentHoleSpan);
        currentHoleSpan = null;
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      currentDataSource = null;
      currentDataSpecLengthUnset = false;
      if (currentHoleSpan != null)
      {
        cache.releaseHoleSpan(currentHoleSpan);
        currentHoleSpan = null;
      }
      throw localThrowable;
    }
  }
  
  private static Uri getRedirectedUriOrDefault(Cache paramCache, String paramString, Uri paramUri)
  {
    paramCache = ContentMetadataInternal.getRedirectedUri(paramCache.getContentMetadata(paramString));
    if (paramCache == null) {
      return paramUri;
    }
    return paramCache;
  }
  
  private void handleBeforeThrow(IOException paramIOException)
  {
    if ((isReadingFromCache()) || ((paramIOException instanceof Cache.CacheException))) {
      seenCacheError = true;
    }
  }
  
  private boolean isBypassingCache()
  {
    return currentDataSource == upstreamDataSource;
  }
  
  private static boolean isCausedByPositionOutOfRange(IOException paramIOException)
  {
    while (paramIOException != null)
    {
      if (((paramIOException instanceof DataSourceException)) && (reason == 0)) {
        return true;
      }
      paramIOException = ((Throwable)paramIOException).getCause();
    }
    return false;
  }
  
  private boolean isReadingFromCache()
  {
    return currentDataSource == cacheReadDataSource;
  }
  
  private boolean isReadingFromUpstream()
  {
    return isReadingFromCache() ^ true;
  }
  
  private boolean isWritingToCache()
  {
    return currentDataSource == cacheWriteDataSource;
  }
  
  private void notifyBytesRead()
  {
    if ((eventListener != null) && (totalCachedBytesRead > 0L))
    {
      eventListener.onCachedBytesRead(cache.getCacheSpace(), totalCachedBytesRead);
      totalCachedBytesRead = 0L;
    }
  }
  
  private void notifyCacheIgnored(int paramInt)
  {
    if (eventListener != null) {
      eventListener.onCacheIgnored(paramInt);
    }
  }
  
  private void openNextSource(boolean paramBoolean)
    throws IOException
  {
    Object localObject2;
    if (currentRequestIgnoresCache)
    {
      localObject1 = null;
    }
    else if (blockOnCache)
    {
      localObject1 = cache;
      localObject2 = key;
      l1 = readPosition;
    }
    try
    {
      localObject1 = ((Cache)localObject1).startReadWrite((String)localObject2, l1);
    }
    catch (InterruptedException localInterruptedException)
    {
      DataSource localDataSource;
      Object localObject3;
      long l3;
      long l2;
      ContentMetadataMutations localContentMetadataMutations;
      for (;;) {}
    }
    Thread.currentThread().interrupt();
    throw new InterruptedIOException();
    Object localObject1 = cache.startReadWriteNonBlocking(key, readPosition);
    if (localObject1 == null)
    {
      localDataSource = upstreamDataSource;
      localObject2 = new DataSpec(uri, httpMethod, null, readPosition, readPosition, bytesRemaining, key, flags);
      localObject3 = localObject1;
      localObject1 = localDataSource;
    }
    else if (isCached)
    {
      localObject2 = Uri.fromFile(file);
      l3 = readPosition - position;
      l2 = length - l3;
      l1 = l2;
      if (bytesRemaining != -1L) {
        l1 = Math.min(l2, bytesRemaining);
      }
      localObject2 = new DataSpec((Uri)localObject2, readPosition, l3, l1, key, flags);
      localDataSource = cacheReadDataSource;
      localObject3 = localObject1;
      localObject1 = localDataSource;
    }
    else
    {
      if (((CacheSpan)localObject1).isOpenEnded()) {
        l1 = bytesRemaining;
      }
      for (;;)
      {
        break;
        l2 = length;
        l1 = l2;
        if (bytesRemaining != -1L) {
          l1 = Math.min(l2, bytesRemaining);
        }
      }
      localObject2 = new DataSpec(uri, httpMethod, null, readPosition, readPosition, l1, key, flags);
      if (cacheWriteDataSource != null)
      {
        localDataSource = cacheWriteDataSource;
        localObject3 = localObject1;
        localObject1 = localDataSource;
      }
      else
      {
        localObject3 = upstreamDataSource;
        cache.releaseHoleSpan((CacheSpan)localObject1);
        localDataSource = null;
        localObject1 = localObject3;
        localObject3 = localDataSource;
      }
    }
    if ((!currentRequestIgnoresCache) && (localObject1 == upstreamDataSource)) {
      l1 = readPosition + 102400L;
    } else {
      l1 = Long.MAX_VALUE;
    }
    checkCachePosition = l1;
    if (paramBoolean)
    {
      Assertions.checkState(isBypassingCache());
      if (localObject1 == upstreamDataSource) {
        return;
      }
      try
      {
        closeCurrentSource();
      }
      catch (Throwable localThrowable)
      {
        if (((CacheSpan)localObject3).isHoleSpan()) {
          cache.releaseHoleSpan((CacheSpan)localObject3);
        }
        throw localThrowable;
      }
    }
    if ((localObject3 != null) && (((CacheSpan)localObject3).isHoleSpan())) {
      currentHoleSpan = ((CacheSpan)localObject3);
    }
    currentDataSource = localThrowable;
    if (length == -1L) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    currentDataSpecLengthUnset = paramBoolean;
    long l1 = localThrowable.open((DataSpec)localObject2);
    localContentMetadataMutations = new ContentMetadataMutations();
    if ((currentDataSpecLengthUnset) && (l1 != -1L))
    {
      bytesRemaining = l1;
      ContentMetadataInternal.setContentLength(localContentMetadataMutations, readPosition + bytesRemaining);
    }
    if (isReadingFromUpstream())
    {
      actualUri = currentDataSource.getUri();
      if ((uri.equals(actualUri) ^ true)) {
        ContentMetadataInternal.setRedirectedUri(localContentMetadataMutations, actualUri);
      } else {
        ContentMetadataInternal.removeRedirectedUri(localContentMetadataMutations);
      }
    }
    if (isWritingToCache())
    {
      cache.applyContentMetadataMutations(key, localContentMetadataMutations);
      return;
    }
  }
  
  private void setNoBytesRemainingAndMaybeStoreLength()
    throws IOException
  {
    bytesRemaining = 0L;
    if (isWritingToCache()) {
      cache.setContentLength(key, readPosition);
    }
  }
  
  private int shouldIgnoreCacheForRequest(DataSpec paramDataSpec)
  {
    if ((ignoreCacheOnError) && (seenCacheError)) {
      return 0;
    }
    if ((ignoreCacheForUnsetLengthRequests) && (length == -1L)) {
      return 1;
    }
    return -1;
  }
  
  public void addTransferListener(TransferListener paramTransferListener)
  {
    cacheReadDataSource.addTransferListener(paramTransferListener);
    upstreamDataSource.addTransferListener(paramTransferListener);
  }
  
  public void close()
    throws IOException
  {
    uri = null;
    actualUri = null;
    httpMethod = 1;
    notifyBytesRead();
    try
    {
      closeCurrentSource();
      return;
    }
    catch (IOException localIOException)
    {
      handleBeforeThrow(localIOException);
      throw localIOException;
    }
  }
  
  public Map getResponseHeaders()
  {
    if (isReadingFromUpstream()) {
      return upstreamDataSource.getResponseHeaders();
    }
    return Collections.emptyMap();
  }
  
  public Uri getUri()
  {
    return actualUri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    Object localObject = cacheKeyFactory;
    try
    {
      localObject = ((CacheKeyFactory)localObject).buildCacheKey(paramDataSpec);
      key = ((String)localObject);
      uri = uri;
      localObject = cache;
      String str = key;
      Uri localUri = uri;
      localObject = getRedirectedUriOrDefault((Cache)localObject, str, localUri);
      actualUri = ((Uri)localObject);
      httpMethod = httpMethod;
      flags = flags;
      readPosition = position;
      int i = shouldIgnoreCacheForRequest(paramDataSpec);
      boolean bool;
      if (i != -1) {
        bool = true;
      } else {
        bool = false;
      }
      currentRequestIgnoresCache = bool;
      if (currentRequestIgnoresCache) {
        notifyCacheIgnored(i);
      }
      if ((length == -1L) && (!currentRequestIgnoresCache))
      {
        localObject = cache;
        str = key;
        long l = ((Cache)localObject).getContentLength(str);
        bytesRemaining = l;
        if (bytesRemaining != -1L)
        {
          bytesRemaining -= position;
          if (bytesRemaining <= 0L)
          {
            paramDataSpec = new DataSourceException(0);
            throw paramDataSpec;
          }
        }
      }
      else
      {
        bytesRemaining = length;
      }
      openNextSource(false);
      return bytesRemaining;
    }
    catch (IOException paramDataSpec)
    {
      handleBeforeThrow(paramDataSpec);
      throw paramDataSpec;
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (bytesRemaining == 0L) {
      return -1;
    }
    if (readPosition >= checkCachePosition) {}
    int i;
    try
    {
      openNextSource(true);
      DataSource localDataSource = currentDataSource;
      i = localDataSource.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i != -1)
      {
        boolean bool = isReadingFromCache();
        if (bool) {
          totalCachedBytesRead += i;
        }
        long l1 = readPosition;
        long l2 = i;
        readPosition = (l1 + l2);
        if (bytesRemaining != -1L)
        {
          bytesRemaining -= l2;
          return i;
        }
      }
      else
      {
        if (currentDataSpecLengthUnset)
        {
          setNoBytesRemainingAndMaybeStoreLength();
          return i;
        }
        if (bytesRemaining > 0L) {
          break label166;
        }
        if (bytesRemaining != -1L) {
          break label213;
        }
        break label166;
      }
      return i;
      label166:
      closeCurrentSource();
      openNextSource(false);
      paramInt1 = read(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      if ((currentDataSpecLengthUnset) && (isCausedByPositionOutOfRange(paramArrayOfByte)))
      {
        setNoBytesRemainingAndMaybeStoreLength();
        return -1;
      }
      handleBeforeThrow(paramArrayOfByte);
      throw paramArrayOfByte;
    }
    label213:
    return i;
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface CacheIgnoredReason {}
  
  public static abstract interface EventListener
  {
    public abstract void onCacheIgnored(int paramInt);
    
    public abstract void onCachedBytesRead(long paramLong1, long paramLong2);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
}
