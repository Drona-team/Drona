package com.google.android.exoplayer2.offline;

import java.io.IOException;

public abstract interface Downloader
{
  public abstract void cancel();
  
  public abstract void download()
    throws InterruptedException, IOException;
  
  public abstract float getDownloadPercentage();
  
  public abstract long getDownloadedBytes();
  
  public abstract void remove()
    throws InterruptedException;
}
