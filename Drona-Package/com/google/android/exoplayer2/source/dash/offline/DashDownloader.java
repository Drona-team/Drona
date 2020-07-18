package com.google.android.exoplayer2.source.dash.offline;

import android.net.Uri;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.offline.DownloadException;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.SegmentDownloader.Segment;
import com.google.android.exoplayer2.source.dash.DashSegmentIndex;
import com.google.android.exoplayer2.source.dash.DashUtil;
import com.google.android.exoplayer2.source.dash.DashWrappingSegmentIndex;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DashDownloader
  extends SegmentDownloader<DashManifest>
{
  public DashDownloader(Uri paramUri, List paramList, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    super(paramUri, paramList, paramDownloaderConstructorHelper);
  }
  
  private static void addSegment(long paramLong, String paramString, RangedUri paramRangedUri, ArrayList paramArrayList)
  {
    paramArrayList.add(new SegmentDownloader.Segment(paramLong, new DataSpec(paramRangedUri.resolveUri(paramString), start, length, null)));
  }
  
  private static void addSegmentsForAdaptationSet(DataSource paramDataSource, AdaptationSet paramAdaptationSet, long paramLong1, long paramLong2, boolean paramBoolean, ArrayList paramArrayList)
    throws IOException, InterruptedException
  {
    int i = 0;
    for (;;)
    {
      Object localObject1 = paramAdaptationSet;
      if (i >= representations.size()) {
        return;
      }
      Object localObject2 = (Representation)representations.get(i);
      int j = type;
      try
      {
        localObject1 = getSegmentIndex(paramDataSource, j, (Representation)localObject2);
        if (localObject1 != null)
        {
          j = ((DashSegmentIndex)localObject1).getSegmentCount(paramLong2);
          if (j != -1)
          {
            String str = baseUrl;
            RangedUri localRangedUri = ((Representation)localObject2).getInitializationUri();
            if (localRangedUri != null) {
              addSegment(paramLong1, str, localRangedUri, paramArrayList);
            }
            localObject2 = ((Representation)localObject2).getIndexUri();
            if (localObject2 != null) {
              addSegment(paramLong1, str, (RangedUri)localObject2, paramArrayList);
            }
            long l2 = ((DashSegmentIndex)localObject1).getFirstSegmentNum();
            long l1 = l2;
            long l3 = j;
            while (l1 <= l3 + l2 - 1L)
            {
              addSegment(paramLong1 + ((DashSegmentIndex)localObject1).getTimeUs(l1), str, ((DashSegmentIndex)localObject1).getSegmentUrl(l1), paramArrayList);
              l1 += 1L;
            }
          }
          throw new DownloadException("Unbounded segment index");
        }
        try
        {
          localObject1 = new DownloadException("Missing segment index");
          throw ((Throwable)localObject1);
        }
        catch (IOException localIOException1) {}
        if (!paramBoolean) {
          break;
        }
      }
      catch (IOException localIOException2) {}
      i += 1;
    }
    throw localIOException2;
  }
  
  private static DashSegmentIndex getSegmentIndex(DataSource paramDataSource, int paramInt, Representation paramRepresentation)
    throws IOException, InterruptedException
  {
    DashSegmentIndex localDashSegmentIndex = paramRepresentation.getIndex();
    if (localDashSegmentIndex != null) {
      return localDashSegmentIndex;
    }
    paramDataSource = DashUtil.loadChunkIndex(paramDataSource, paramInt, paramRepresentation);
    if (paramDataSource == null) {
      return null;
    }
    return new DashWrappingSegmentIndex(paramDataSource, presentationTimeOffsetUs);
  }
  
  protected DashManifest getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    return DashUtil.loadManifest(paramDataSource, paramUri);
  }
  
  protected List getSegments(DataSource paramDataSource, DashManifest paramDashManifest, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramDashManifest.getPeriodCount())
    {
      Object localObject = paramDashManifest.getPeriod(i);
      long l1 = IpAddress.msToUs(startMs);
      long l2 = paramDashManifest.getPeriodDurationUs(i);
      localObject = adaptationSets;
      int j = 0;
      while (j < ((List)localObject).size())
      {
        addSegmentsForAdaptationSet(paramDataSource, (AdaptationSet)((List)localObject).get(j), l1, l2, paramBoolean, localArrayList);
        j += 1;
      }
      i += 1;
    }
    return localArrayList;
  }
}
