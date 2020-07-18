package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;

abstract interface EngineJobListener
{
  public abstract void onEngineJobCancelled(EngineJob paramEngineJob, Key paramKey);
  
  public abstract void onEngineJobComplete(EngineJob paramEngineJob, Key paramKey, EngineResource paramEngineResource);
}