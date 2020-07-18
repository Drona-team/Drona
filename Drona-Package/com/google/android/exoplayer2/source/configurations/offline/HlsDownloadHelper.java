package com.google.android.exoplayer2.source.configurations.offline;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.offline.TrackKey;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.configurations.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.configurations.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HlsDownloadHelper
  extends DownloadHelper
{
  private final DataSource.Factory manifestDataSourceFactory;
  private HlsPlaylist playlist;
  private int[] renditionGroups;
  private final Uri webUrl;
  
  public HlsDownloadHelper(Uri paramUri, DataSource.Factory paramFactory)
  {
    webUrl = paramUri;
    manifestDataSourceFactory = paramFactory;
  }
  
  private static Format[] toFormats(List paramList)
  {
    Format[] arrayOfFormat = new Format[paramList.size()];
    int i = 0;
    while (i < paramList.size())
    {
      arrayOfFormat[i] = getformat;
      i += 1;
    }
    return arrayOfFormat;
  }
  
  private static List toStreamKeys(List paramList, int[] paramArrayOfInt)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    int i = 0;
    while (i < paramList.size())
    {
      TrackKey localTrackKey = (TrackKey)paramList.get(i);
      localArrayList.add(new StreamKey(paramArrayOfInt[groupIndex], trackIndex));
      i += 1;
    }
    return localArrayList;
  }
  
  public HlsDownloadAction getDownloadAction(byte[] paramArrayOfByte, List paramList)
  {
    Assertions.checkNotNull(renditionGroups);
    return HlsDownloadAction.createDownloadAction(webUrl, paramArrayOfByte, toStreamKeys(paramList, renditionGroups));
  }
  
  public int getPeriodCount()
  {
    Assertions.checkNotNull(playlist);
    return 1;
  }
  
  public HlsPlaylist getPlaylist()
  {
    Assertions.checkNotNull(playlist);
    return playlist;
  }
  
  public HlsDownloadAction getRemoveAction(byte[] paramArrayOfByte)
  {
    return HlsDownloadAction.createRemoveAction(webUrl, paramArrayOfByte);
  }
  
  public TrackGroupArray getTrackGroups(int paramInt)
  {
    Assertions.checkNotNull(playlist);
    boolean bool = playlist instanceof HlsMediaPlaylist;
    int i = 0;
    if (bool)
    {
      renditionGroups = new int[0];
      return TrackGroupArray.EMPTY;
    }
    HlsMasterPlaylist localHlsMasterPlaylist = (HlsMasterPlaylist)playlist;
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[3];
    renditionGroups = new int[3];
    if (!variants.isEmpty())
    {
      renditionGroups[0] = 0;
      arrayOfTrackGroup[0] = new TrackGroup(toFormats(variants));
      i = 1;
    }
    paramInt = i;
    if (!audios.isEmpty())
    {
      renditionGroups[i] = 1;
      arrayOfTrackGroup[i] = new TrackGroup(toFormats(audios));
      paramInt = i + 1;
    }
    i = paramInt;
    if (!subtitles.isEmpty())
    {
      renditionGroups[paramInt] = 2;
      arrayOfTrackGroup[paramInt] = new TrackGroup(toFormats(subtitles));
      i = paramInt + 1;
    }
    return new TrackGroupArray((TrackGroup[])Arrays.copyOf(arrayOfTrackGroup, i));
  }
  
  protected void prepareInternal()
    throws IOException
  {
    playlist = ((HlsPlaylist)ParsingLoadable.load(manifestDataSourceFactory.createDataSource(), new HlsPlaylistParser(), webUrl, 4));
  }
}
