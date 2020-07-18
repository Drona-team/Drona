package com.google.android.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.SegmentDownloader.Segment;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsUtil;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SsDownloader
  extends SegmentDownloader<SsManifest>
{
  public SsDownloader(Uri paramUri, List paramList, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    super(SsUtil.fixManifestUri(paramUri), paramList, paramDownloaderConstructorHelper);
  }
  
  protected SsManifest getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    return (SsManifest)ParsingLoadable.load(paramDataSource, new SsManifestParser(), paramUri, 4);
  }
  
  protected List getSegments(DataSource paramDataSource, SsManifest paramSsManifest, boolean paramBoolean)
  {
    paramDataSource = new ArrayList();
    paramSsManifest = streamElements;
    int m = paramSsManifest.length;
    int i = 0;
    while (i < m)
    {
      Object localObject = paramSsManifest[i];
      int j = 0;
      while (j < formats.length)
      {
        int k = 0;
        while (k < chunkCount)
        {
          paramDataSource.add(new SegmentDownloader.Segment(localObject.getStartTimeUs(k), new DataSpec(localObject.buildRequestUri(j, k))));
          k += 1;
        }
        j += 1;
      }
      i += 1;
    }
    return paramDataSource;
  }
}
