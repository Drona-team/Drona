package com.google.android.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.offline.TrackKey;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SsDownloadHelper
  extends DownloadHelper
{
  private final Uri mOutputUri;
  private SsManifest manifest;
  private final DataSource.Factory manifestDataSourceFactory;
  
  public SsDownloadHelper(Uri paramUri, DataSource.Factory paramFactory)
  {
    mOutputUri = paramUri;
    manifestDataSourceFactory = paramFactory;
  }
  
  private static List toStreamKeys(List paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    int i = 0;
    while (i < paramList.size())
    {
      TrackKey localTrackKey = (TrackKey)paramList.get(i);
      localArrayList.add(new StreamKey(groupIndex, trackIndex));
      i += 1;
    }
    return localArrayList;
  }
  
  public SsDownloadAction getDownloadAction(byte[] paramArrayOfByte, List paramList)
  {
    return SsDownloadAction.createDownloadAction(mOutputUri, paramArrayOfByte, toStreamKeys(paramList));
  }
  
  public SsManifest getManifest()
  {
    Assertions.checkNotNull(manifest);
    return manifest;
  }
  
  public int getPeriodCount()
  {
    Assertions.checkNotNull(manifest);
    return 1;
  }
  
  public SsDownloadAction getRemoveAction(byte[] paramArrayOfByte)
  {
    return SsDownloadAction.createRemoveAction(mOutputUri, paramArrayOfByte);
  }
  
  public TrackGroupArray getTrackGroups(int paramInt)
  {
    Assertions.checkNotNull(manifest);
    SsManifest.StreamElement[] arrayOfStreamElement = manifest.streamElements;
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[arrayOfStreamElement.length];
    paramInt = 0;
    while (paramInt < arrayOfStreamElement.length)
    {
      arrayOfTrackGroup[paramInt] = new TrackGroup(formats);
      paramInt += 1;
    }
    return new TrackGroupArray(arrayOfTrackGroup);
  }
  
  protected void prepareInternal()
    throws IOException
  {
    manifest = ((SsManifest)ParsingLoadable.load(manifestDataSourceFactory.createDataSource(), new SsManifestParser(), mOutputUri, 4));
  }
}
