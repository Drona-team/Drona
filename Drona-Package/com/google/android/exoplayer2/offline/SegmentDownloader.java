package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.CacheUtil.CachingCounters;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SegmentDownloader<M extends FilterableManifest<M>>
  implements Downloader
{
  private static final int BUFFER_SIZE_BYTES = 131072;
  private final Cache cache;
  private final CacheDataSource dataSource;
  private volatile long downloadedBytes;
  private volatile int downloadedSegments;
  private final AtomicBoolean isCanceled;
  private final Uri manifestUri;
  private final CacheDataSource offlineDataSource;
  private final PriorityTaskManager priorityTaskManager;
  private final ArrayList<StreamKey> streamKeys;
  private volatile int totalSegments;
  
  public SegmentDownloader(Uri paramUri, List paramList, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    manifestUri = paramUri;
    streamKeys = new ArrayList(paramList);
    cache = paramDownloaderConstructorHelper.getCache();
    dataSource = paramDownloaderConstructorHelper.buildCacheDataSource(false);
    offlineDataSource = paramDownloaderConstructorHelper.buildCacheDataSource(true);
    priorityTaskManager = paramDownloaderConstructorHelper.getPriorityTaskManager();
    totalSegments = -1;
    isCanceled = new AtomicBoolean();
  }
  
  private List initDownload()
    throws IOException, InterruptedException
  {
    Object localObject2 = getManifest(dataSource, manifestUri);
    Object localObject1 = localObject2;
    if (!streamKeys.isEmpty()) {
      localObject1 = (FilterableManifest)((FilterableManifest)localObject2).copy(streamKeys);
    }
    localObject1 = getSegments(dataSource, (FilterableManifest)localObject1, false);
    localObject2 = new CacheUtil.CachingCounters();
    totalSegments = ((List)localObject1).size();
    downloadedSegments = 0;
    downloadedBytes = 0L;
    int i = ((List)localObject1).size() - 1;
    while (i >= 0)
    {
      CacheUtil.getCached(getdataSpec, cache, (CacheUtil.CachingCounters)localObject2);
      downloadedBytes += alreadyCachedBytes;
      if (alreadyCachedBytes == contentLength)
      {
        downloadedSegments += 1;
        ((List)localObject1).remove(i);
      }
      i -= 1;
    }
    return localObject1;
  }
  
  private void removeUri(Uri paramUri)
  {
    CacheUtil.remove(cache, CacheUtil.generateKey(paramUri));
  }
  
  public void cancel()
  {
    isCanceled.set(true);
  }
  
  public final void download()
    throws IOException, InterruptedException
  {
    priorityTaskManager.add(64536);
    try
    {
      List localList = initDownload();
      Collections.sort(localList);
      byte[] arrayOfByte = new byte[131072];
      CacheUtil.CachingCounters localCachingCounters = new CacheUtil.CachingCounters();
      int i = 0;
      for (;;)
      {
        int j = localList.size();
        if (i < j) {
          try
          {
            CacheUtil.cache(getdataSpec, cache, dataSource, arrayOfByte, priorityTaskManager, 64536, localCachingCounters, isCanceled, true);
            j = downloadedSegments;
            downloadedSegments = (j + 1);
            l1 = downloadedBytes;
            l2 = newlyCachedBytes;
            downloadedBytes = (l1 + l2);
            i += 1;
          }
          catch (Throwable localThrowable2)
          {
            long l1 = downloadedBytes;
            long l2 = newlyCachedBytes;
            downloadedBytes = (l1 + l2);
            throw localThrowable2;
          }
        }
      }
      priorityTaskManager.remove(64536);
      return;
    }
    catch (Throwable localThrowable1)
    {
      priorityTaskManager.remove(64536);
      throw localThrowable1;
    }
  }
  
  public final float getDownloadPercentage()
  {
    int i = totalSegments;
    int j = downloadedSegments;
    if ((i != -1) && (j != -1))
    {
      if (i == 0) {
        return 100.0F;
      }
      return j * 100.0F / i;
    }
    return -1.0F;
  }
  
  public final long getDownloadedBytes()
  {
    return downloadedBytes;
  }
  
  protected abstract FilterableManifest getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException;
  
  protected abstract List getSegments(DataSource paramDataSource, FilterableManifest paramFilterableManifest, boolean paramBoolean)
    throws InterruptedException, IOException;
  
  /* Error */
  public final void remove()
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 61	com/google/android/exoplayer2/offline/SegmentDownloader:offlineDataSource	Lcom/google/android/exoplayer2/upstream/cache/CacheDataSource;
    //   4: astore_3
    //   5: aload_0
    //   6: getfield 38	com/google/android/exoplayer2/offline/SegmentDownloader:manifestUri	Landroid/net/Uri;
    //   9: astore 4
    //   11: aload_0
    //   12: aload_3
    //   13: aload 4
    //   15: invokevirtual 85	com/google/android/exoplayer2/offline/SegmentDownloader:getManifest	(Lcom/google/android/exoplayer2/upstream/DataSource;Landroid/net/Uri;)Lcom/google/android/exoplayer2/offline/FilterableManifest;
    //   18: astore_3
    //   19: aload_0
    //   20: getfield 61	com/google/android/exoplayer2/offline/SegmentDownloader:offlineDataSource	Lcom/google/android/exoplayer2/upstream/cache/CacheDataSource;
    //   23: astore 4
    //   25: aload_0
    //   26: aload 4
    //   28: aload_3
    //   29: iconst_1
    //   30: invokevirtual 99	com/google/android/exoplayer2/offline/SegmentDownloader:getSegments	(Lcom/google/android/exoplayer2/upstream/DataSource;Lcom/google/android/exoplayer2/offline/FilterableManifest;Z)Ljava/util/List;
    //   33: astore_3
    //   34: iconst_0
    //   35: istore_1
    //   36: aload_3
    //   37: invokeinterface 108 1 0
    //   42: istore_2
    //   43: iload_1
    //   44: iload_2
    //   45: if_icmpge +49 -> 94
    //   48: aload_3
    //   49: iload_1
    //   50: invokeinterface 116 2 0
    //   55: astore 4
    //   57: aload 4
    //   59: checkcast 9	com/google/android/exoplayer2/offline/SegmentDownloader$Segment
    //   62: getfield 120	com/google/android/exoplayer2/offline/SegmentDownloader$Segment:dataSpec	Lcom/google/android/exoplayer2/upstream/DataSpec;
    //   65: getfield 186	com/google/android/exoplayer2/upstream/DataSpec:uri	Landroid/net/Uri;
    //   68: astore 4
    //   70: aload_0
    //   71: aload 4
    //   73: invokespecial 188	com/google/android/exoplayer2/offline/SegmentDownloader:removeUri	(Landroid/net/Uri;)V
    //   76: iload_1
    //   77: iconst_1
    //   78: iadd
    //   79: istore_1
    //   80: goto -44 -> 36
    //   83: astore_3
    //   84: aload_0
    //   85: aload_0
    //   86: getfield 38	com/google/android/exoplayer2/offline/SegmentDownloader:manifestUri	Landroid/net/Uri;
    //   89: invokespecial 188	com/google/android/exoplayer2/offline/SegmentDownloader:removeUri	(Landroid/net/Uri;)V
    //   92: aload_3
    //   93: athrow
    //   94: aload_0
    //   95: aload_0
    //   96: getfield 38	com/google/android/exoplayer2/offline/SegmentDownloader:manifestUri	Landroid/net/Uri;
    //   99: invokespecial 188	com/google/android/exoplayer2/offline/SegmentDownloader:removeUri	(Landroid/net/Uri;)V
    //   102: return
    //   103: astore_3
    //   104: goto -10 -> 94
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	this	SegmentDownloader
    //   35	45	1	i	int
    //   42	4	2	j	int
    //   4	45	3	localObject1	Object
    //   83	10	3	localThrowable	Throwable
    //   103	1	3	localIOException	IOException
    //   9	63	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	11	83	java/lang/Throwable
    //   11	19	83	java/lang/Throwable
    //   25	34	83	java/lang/Throwable
    //   36	43	83	java/lang/Throwable
    //   48	57	83	java/lang/Throwable
    //   70	76	83	java/lang/Throwable
    //   11	19	103	java/io/IOException
    //   25	34	103	java/io/IOException
    //   36	43	103	java/io/IOException
    //   48	57	103	java/io/IOException
    //   70	76	103	java/io/IOException
  }
  
  protected static class Segment
    implements Comparable<Segment>
  {
    public final DataSpec dataSpec;
    public final long startTimeUs;
    
    public Segment(long paramLong, DataSpec paramDataSpec)
    {
      startTimeUs = paramLong;
      dataSpec = paramDataSpec;
    }
    
    public int compareTo(Segment paramSegment)
    {
      return Util.compareLong(startTimeUs, startTimeUs);
    }
  }
}
