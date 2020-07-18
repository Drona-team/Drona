package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import java.util.Map;

class EngineKeyFactory
{
  EngineKeyFactory() {}
  
  EngineKey buildKey(Object paramObject, Key paramKey, int paramInt1, int paramInt2, Map paramMap, Class paramClass1, Class paramClass2, Options paramOptions)
  {
    return new EngineKey(paramObject, paramKey, paramInt1, paramInt2, paramMap, paramClass1, paramClass2, paramOptions);
  }
}
