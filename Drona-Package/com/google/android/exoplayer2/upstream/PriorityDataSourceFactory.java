package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.PriorityTaskManager;

public final class PriorityDataSourceFactory
  implements DataSource.Factory
{
  private final int priority;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource.Factory upstreamFactory;
  
  public PriorityDataSourceFactory(DataSource.Factory paramFactory, PriorityTaskManager paramPriorityTaskManager, int paramInt)
  {
    upstreamFactory = paramFactory;
    priorityTaskManager = paramPriorityTaskManager;
    priority = paramInt;
  }
  
  public PriorityDataSource createDataSource()
  {
    return new PriorityDataSource(upstreamFactory.createDataSource(), priorityTaskManager, priority);
  }
}
