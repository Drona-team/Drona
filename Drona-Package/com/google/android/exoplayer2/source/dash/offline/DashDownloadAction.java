package com.google.android.exoplayer2.source.dash.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadAction.Deserializer;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloadAction;
import com.google.android.exoplayer2.offline.SegmentDownloadAction.SegmentDownloadActionDeserializer;
import java.util.Collections;
import java.util.List;

public final class DashDownloadAction
  extends SegmentDownloadAction
{
  public static final DownloadAction.Deserializer DESERIALIZER = new SegmentDownloadAction.SegmentDownloadActionDeserializer("dash", 0)
  {
    protected DownloadAction createDownloadAction(Uri paramAnonymousUri, boolean paramAnonymousBoolean, byte[] paramAnonymousArrayOfByte, List paramAnonymousList)
    {
      return new DashDownloadAction(paramAnonymousUri, paramAnonymousBoolean, paramAnonymousArrayOfByte, paramAnonymousList);
    }
  };
  private static final String TYPE = "dash";
  private static final int VERSION = 0;
  
  public DashDownloadAction(Uri paramUri, boolean paramBoolean, byte[] paramArrayOfByte, List paramList)
  {
    super("dash", 0, paramUri, paramBoolean, paramArrayOfByte, paramList);
  }
  
  public static DashDownloadAction createDownloadAction(Uri paramUri, byte[] paramArrayOfByte, List paramList)
  {
    return new DashDownloadAction(paramUri, false, paramArrayOfByte, paramList);
  }
  
  public static DashDownloadAction createRemoveAction(Uri paramUri, byte[] paramArrayOfByte)
  {
    return new DashDownloadAction(paramUri, true, paramArrayOfByte, Collections.emptyList());
  }
  
  public DashDownloader createDownloader(DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    return new DashDownloader(uuid, keys, paramDownloaderConstructorHelper);
  }
}
