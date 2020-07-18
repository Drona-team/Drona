package com.google.android.exoplayer2.offline;

import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.source.TrackGroupArray;
import java.io.IOException;
import java.util.List;

public abstract class DownloadHelper
{
  public DownloadHelper() {}
  
  public abstract DownloadAction getDownloadAction(byte[] paramArrayOfByte, List paramList);
  
  public abstract int getPeriodCount();
  
  public abstract DownloadAction getRemoveAction(byte[] paramArrayOfByte);
  
  public abstract TrackGroupArray getTrackGroups(int paramInt);
  
  public void prepare(final Callback paramCallback)
  {
    Looper localLooper;
    if (Looper.myLooper() != null) {
      localLooper = Looper.myLooper();
    } else {
      localLooper = Looper.getMainLooper();
    }
    new Thread()
    {
      public void run()
      {
        Object localObject = DownloadHelper.this;
        try
        {
          ((DownloadHelper)localObject).prepareInternal();
          localObject = val$handler;
          DownloadHelper.Callback localCallback = paramCallback;
          ((Handler)localObject).post(new -..Lambda.DownloadHelper.1.NJSYwfqmwQZzGoahNhNZN-HV0Ik(this, localCallback));
          return;
        }
        catch (IOException localIOException)
        {
          val$handler.post(new -..Lambda.DownloadHelper.1.wpC3MI-R3oiOTWYi5AbWzFrvt8g(this, paramCallback, localIOException));
        }
      }
    }.start();
  }
  
  protected abstract void prepareInternal()
    throws IOException;
  
  public static abstract interface Callback
  {
    public abstract void onPrepareError(DownloadHelper paramDownloadHelper, IOException paramIOException);
    
    public abstract void onPrepared(DownloadHelper paramDownloadHelper);
  }
}
