package com.google.android.exoplayer2.upstream;

import android.content.Context;
import androidx.annotation.Nullable;

public final class DefaultDataSourceFactory
  implements DataSource.Factory
{
  private final DataSource.Factory baseDataSourceFactory;
  private final Context context;
  @Nullable
  private final TransferListener listener;
  
  public DefaultDataSourceFactory(Context paramContext, DataSource.Factory paramFactory)
  {
    this(paramContext, null, paramFactory);
  }
  
  public DefaultDataSourceFactory(Context paramContext, TransferListener paramTransferListener, DataSource.Factory paramFactory)
  {
    context = paramContext.getApplicationContext();
    listener = paramTransferListener;
    baseDataSourceFactory = paramFactory;
  }
  
  public DefaultDataSourceFactory(Context paramContext, String paramString)
  {
    this(paramContext, paramString, null);
  }
  
  public DefaultDataSourceFactory(Context paramContext, String paramString, TransferListener paramTransferListener)
  {
    this(paramContext, paramTransferListener, new DefaultHttpDataSourceFactory(paramString, paramTransferListener));
  }
  
  public DefaultDataSource createDataSource()
  {
    DefaultDataSource localDefaultDataSource = new DefaultDataSource(context, baseDataSourceFactory.createDataSource());
    if (listener != null) {
      localDefaultDataSource.addTransferListener(listener);
    }
    return localDefaultDataSource;
  }
}
