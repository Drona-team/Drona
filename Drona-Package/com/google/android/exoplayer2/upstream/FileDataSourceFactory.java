package com.google.android.exoplayer2.upstream;

import androidx.annotation.Nullable;

public final class FileDataSourceFactory
  implements DataSource.Factory
{
  @Nullable
  private final TransferListener listener;
  
  public FileDataSourceFactory()
  {
    this(null);
  }
  
  public FileDataSourceFactory(TransferListener paramTransferListener)
  {
    listener = paramTransferListener;
  }
  
  public DataSource createDataSource()
  {
    FileDataSource localFileDataSource = new FileDataSource();
    if (listener != null) {
      localFileDataSource.addTransferListener(listener);
    }
    return localFileDataSource;
  }
}
