package com.google.android.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadAction.Deserializer;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloadAction;
import com.google.android.exoplayer2.offline.SegmentDownloadAction.SegmentDownloadActionDeserializer;
import com.google.android.exoplayer2.offline.StreamKey;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class SsDownloadAction
  extends SegmentDownloadAction
{
  public static final DownloadAction.Deserializer DESERIALIZER = new SegmentDownloadAction.SegmentDownloadActionDeserializer("ss", 1)
  {
    protected DownloadAction createDownloadAction(Uri paramAnonymousUri, boolean paramAnonymousBoolean, byte[] paramAnonymousArrayOfByte, List paramAnonymousList)
    {
      return new SsDownloadAction(paramAnonymousUri, paramAnonymousBoolean, paramAnonymousArrayOfByte, paramAnonymousList);
    }
    
    protected StreamKey readKey(int paramAnonymousInt, DataInputStream paramAnonymousDataInputStream)
      throws IOException
    {
      if (paramAnonymousInt > 0) {
        return super.readKey(paramAnonymousInt, paramAnonymousDataInputStream);
      }
      return new StreamKey(paramAnonymousDataInputStream.readInt(), paramAnonymousDataInputStream.readInt());
    }
  };
  private static final String TYPE = "ss";
  private static final int VERSION = 1;
  
  public SsDownloadAction(Uri paramUri, boolean paramBoolean, byte[] paramArrayOfByte, List paramList)
  {
    super("ss", 1, paramUri, paramBoolean, paramArrayOfByte, paramList);
  }
  
  public static SsDownloadAction createDownloadAction(Uri paramUri, byte[] paramArrayOfByte, List paramList)
  {
    return new SsDownloadAction(paramUri, false, paramArrayOfByte, paramList);
  }
  
  public static SsDownloadAction createRemoveAction(Uri paramUri, byte[] paramArrayOfByte)
  {
    return new SsDownloadAction(paramUri, true, paramArrayOfByte, Collections.emptyList());
  }
  
  public SsDownloader createDownloader(DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    return new SsDownloader(uuid, keys, paramDownloaderConstructorHelper);
  }
}
