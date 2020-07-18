package com.google.android.exoplayer2.source.configurations.offline;

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

public final class HlsDownloadAction
  extends SegmentDownloadAction
{
  public static final DownloadAction.Deserializer DESERIALIZER = new SegmentDownloadAction.SegmentDownloadActionDeserializer("hls", 1)
  {
    protected DownloadAction createDownloadAction(Uri paramAnonymousUri, boolean paramAnonymousBoolean, byte[] paramAnonymousArrayOfByte, List paramAnonymousList)
    {
      return new HlsDownloadAction(paramAnonymousUri, paramAnonymousBoolean, paramAnonymousArrayOfByte, paramAnonymousList);
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
  private static final String TYPE = "hls";
  private static final int VERSION = 1;
  
  public HlsDownloadAction(Uri paramUri, boolean paramBoolean, byte[] paramArrayOfByte, List paramList)
  {
    super("hls", 1, paramUri, paramBoolean, paramArrayOfByte, paramList);
  }
  
  public static HlsDownloadAction createDownloadAction(Uri paramUri, byte[] paramArrayOfByte, List paramList)
  {
    return new HlsDownloadAction(paramUri, false, paramArrayOfByte, paramList);
  }
  
  public static HlsDownloadAction createRemoveAction(Uri paramUri, byte[] paramArrayOfByte)
  {
    return new HlsDownloadAction(paramUri, true, paramArrayOfByte, Collections.emptyList());
  }
  
  public HlsDownloader createDownloader(DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    return new HlsDownloader(uuid, keys, paramDownloaderConstructorHelper);
  }
}
