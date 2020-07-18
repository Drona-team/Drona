package com.google.android.exoplayer2.source.configurations.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.SegmentDownloader.Segment;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class HlsDownloader
  extends SegmentDownloader<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist>
{
  public HlsDownloader(Uri paramUri, List paramList, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    super(paramUri, paramList, paramDownloaderConstructorHelper);
  }
  
  private static void addResolvedUris(String paramString, List paramList1, List paramList2)
  {
    int i = 0;
    while (i < paramList1.size())
    {
      paramList2.add(UriUtil.resolveToUri(paramString, geturl));
      i += 1;
    }
  }
  
  private static void addSegment(ArrayList paramArrayList, HlsMediaPlaylist paramHlsMediaPlaylist, HlsMediaPlaylist.Segment paramSegment, HashSet paramHashSet)
  {
    long l = startTimeUs + relativeStartTimeUs;
    if (fullSegmentEncryptionKeyUri != null)
    {
      Uri localUri = UriUtil.resolveToUri(baseUri, fullSegmentEncryptionKeyUri);
      if (paramHashSet.add(localUri)) {
        paramArrayList.add(new SegmentDownloader.Segment(l, new DataSpec(localUri)));
      }
    }
    paramArrayList.add(new SegmentDownloader.Segment(l, new DataSpec(UriUtil.resolveToUri(baseUri, encryptionKeyUri), byterangeOffset, byterangeLength, null)));
  }
  
  private static com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist loadManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    return (com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist)ParsingLoadable.load(paramDataSource, new HlsPlaylistParser(), paramUri, 4);
  }
  
  protected com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    return loadManifest(paramDataSource, paramUri);
  }
  
  protected List getSegments(DataSource paramDataSource, com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist paramHlsPlaylist, boolean paramBoolean)
    throws IOException
  {
    Object localObject = new ArrayList();
    if ((paramHlsPlaylist instanceof HlsMasterPlaylist))
    {
      paramHlsPlaylist = (HlsMasterPlaylist)paramHlsPlaylist;
      addResolvedUris(baseUri, variants, (List)localObject);
      addResolvedUris(baseUri, audios, (List)localObject);
      addResolvedUris(baseUri, subtitles, (List)localObject);
    }
    else
    {
      ((ArrayList)localObject).add(Uri.parse(baseUri));
    }
    ArrayList localArrayList = new ArrayList();
    HashSet localHashSet = new HashSet();
    Iterator localIterator = ((ArrayList)localObject).iterator();
    while (localIterator.hasNext())
    {
      paramHlsPlaylist = (Uri)localIterator.next();
      try
      {
        localObject = loadManifest(paramDataSource, paramHlsPlaylist);
        HlsMediaPlaylist localHlsMediaPlaylist = (HlsMediaPlaylist)localObject;
        long l = startTimeUs;
        localArrayList.add(new SegmentDownloader.Segment(l, new DataSpec(paramHlsPlaylist)));
        paramHlsPlaylist = null;
        List localList = segments;
        int i = 0;
        while (i < localList.size())
        {
          HlsMediaPlaylist.Segment localSegment2 = (HlsMediaPlaylist.Segment)localList.get(i);
          HlsMediaPlaylist.Segment localSegment1 = initializationSegment;
          localObject = paramHlsPlaylist;
          if (localSegment1 != null)
          {
            localObject = paramHlsPlaylist;
            if (localSegment1 != paramHlsPlaylist)
            {
              addSegment(localArrayList, localHlsMediaPlaylist, localSegment1, localHashSet);
              localObject = localSegment1;
            }
          }
          addSegment(localArrayList, localHlsMediaPlaylist, localSegment2, localHashSet);
          i += 1;
          paramHlsPlaylist = (com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist)localObject;
        }
      }
      catch (IOException localIOException)
      {
        if (paramBoolean) {
          localArrayList.add(new SegmentDownloader.Segment(0L, new DataSpec(paramHlsPlaylist)));
        } else {
          throw localIOException;
        }
      }
    }
    return localArrayList;
  }
}
