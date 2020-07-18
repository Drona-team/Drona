package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.upstream.DataSink.Factory;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;

public final class DownloaderConstructorHelper
{
  private final Cache cache;
  private final DataSource.Factory cacheReadDataSourceFactory;
  private final DataSink.Factory cacheWriteDataSinkFactory;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource.Factory upstreamDataSourceFactory;
  
  public DownloaderConstructorHelper(Cache paramCache, DataSource.Factory paramFactory)
  {
    this(paramCache, paramFactory, null, null, null);
  }
  
  public DownloaderConstructorHelper(Cache paramCache, DataSource.Factory paramFactory1, DataSource.Factory paramFactory2, DataSink.Factory paramFactory, PriorityTaskManager paramPriorityTaskManager)
  {
    Assertions.checkNotNull(paramFactory1);
    cache = paramCache;
    upstreamDataSourceFactory = paramFactory1;
    cacheReadDataSourceFactory = paramFactory2;
    cacheWriteDataSinkFactory = paramFactory;
    priorityTaskManager = paramPriorityTaskManager;
  }
  
  public CacheDataSource buildCacheDataSource(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a9 = a8\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public Cache getCache()
  {
    return cache;
  }
  
  public PriorityTaskManager getPriorityTaskManager()
  {
    if (priorityTaskManager != null) {
      return priorityTaskManager;
    }
    return new PriorityTaskManager();
  }
}
