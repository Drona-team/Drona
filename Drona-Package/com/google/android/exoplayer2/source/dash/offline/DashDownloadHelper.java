package com.google.android.exoplayer2.source.dash.offline;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.offline.TrackKey;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DashDownloadHelper
  extends DownloadHelper
{
  private final Uri mOutputUri;
  private DashManifest manifest;
  private final DataSource.Factory manifestDataSourceFactory;
  
  public DashDownloadHelper(Uri paramUri, DataSource.Factory paramFactory)
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
      localArrayList.add(new StreamKey(periodIndex, groupIndex, trackIndex));
      i += 1;
    }
    return localArrayList;
  }
  
  public DashDownloadAction getDownloadAction(byte[] paramArrayOfByte, List paramList)
  {
    return DashDownloadAction.createDownloadAction(mOutputUri, paramArrayOfByte, toStreamKeys(paramList));
  }
  
  public DashManifest getManifest()
  {
    Assertions.checkNotNull(manifest);
    return manifest;
  }
  
  public int getPeriodCount()
  {
    Assertions.checkNotNull(manifest);
    return manifest.getPeriodCount();
  }
  
  public DashDownloadAction getRemoveAction(byte[] paramArrayOfByte)
  {
    return DashDownloadAction.createRemoveAction(mOutputUri, paramArrayOfByte);
  }
  
  public TrackGroupArray getTrackGroups(int paramInt)
  {
    Assertions.checkNotNull(manifest);
    List localList1 = manifest.getPeriod(paramInt).adaptationSets;
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[localList1.size()];
    paramInt = 0;
    while (paramInt < arrayOfTrackGroup.length)
    {
      List localList2 = getrepresentations;
      Format[] arrayOfFormat = new Format[localList2.size()];
      int j = localList2.size();
      int i = 0;
      while (i < j)
      {
        arrayOfFormat[i] = getformat;
        i += 1;
      }
      arrayOfTrackGroup[paramInt] = new TrackGroup(arrayOfFormat);
      paramInt += 1;
    }
    return new TrackGroupArray(arrayOfTrackGroup);
  }
  
  protected void prepareInternal()
    throws IOException
  {
    manifest = ((DashManifest)ParsingLoadable.load(manifestDataSourceFactory.createDataSource(), new DashManifestParser(), mOutputUri, 4));
  }
}
