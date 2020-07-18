package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class Jobs
{
  private final Map<Key, EngineJob<?>> jobs = new HashMap();
  private final Map<Key, EngineJob<?>> onlyCacheJobs = new HashMap();
  
  Jobs() {}
  
  private Map getJobMap(boolean paramBoolean)
  {
    if (paramBoolean) {
      return onlyCacheJobs;
    }
    return jobs;
  }
  
  EngineJob build(Key paramKey, boolean paramBoolean)
  {
    return (EngineJob)getJobMap(paramBoolean).get(paramKey);
  }
  
  Map getAll()
  {
    return Collections.unmodifiableMap(jobs);
  }
  
  void put(Key paramKey, EngineJob paramEngineJob)
  {
    getJobMap(paramEngineJob.onlyRetrieveFromCache()).put(paramKey, paramEngineJob);
  }
  
  void removeIfCurrent(Key paramKey, EngineJob paramEngineJob)
  {
    Map localMap = getJobMap(paramEngineJob.onlyRetrieveFromCache());
    if (paramEngineJob.equals(localMap.get(paramKey))) {
      localMap.remove(paramKey);
    }
  }
}
