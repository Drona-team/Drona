package com.google.android.exoplayer2.offline;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.TrackGroupArray;
import java.util.List;

public final class ProgressiveDownloadHelper
  extends DownloadHelper
{
  @Nullable
  private final String customCacheKey;
  private final Uri mOutputUri;
  
  public ProgressiveDownloadHelper(Uri paramUri)
  {
    this(paramUri, null);
  }
  
  public ProgressiveDownloadHelper(Uri paramUri, String paramString)
  {
    mOutputUri = paramUri;
    customCacheKey = paramString;
  }
  
  public ProgressiveDownloadAction getDownloadAction(byte[] paramArrayOfByte, List paramList)
  {
    return ProgressiveDownloadAction.createDownloadAction(mOutputUri, paramArrayOfByte, customCacheKey);
  }
  
  public int getPeriodCount()
  {
    return 1;
  }
  
  public ProgressiveDownloadAction getRemoveAction(byte[] paramArrayOfByte)
  {
    return ProgressiveDownloadAction.createRemoveAction(mOutputUri, paramArrayOfByte, customCacheKey);
  }
  
  public TrackGroupArray getTrackGroups(int paramInt)
  {
    return TrackGroupArray.EMPTY;
  }
  
  protected void prepareInternal() {}
}
