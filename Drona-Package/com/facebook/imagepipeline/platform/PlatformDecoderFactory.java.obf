package com.facebook.imagepipeline.platform;

import android.os.Build.VERSION;
import androidx.core.util.Pools.SynchronizedPool;
import com.facebook.imagepipeline.memory.PoolFactory;

public class PlatformDecoderFactory
{
  public PlatformDecoderFactory() {}
  
  public static PlatformDecoder buildPlatformDecoder(PoolFactory paramPoolFactory, boolean paramBoolean)
  {
    int i;
    if (Build.VERSION.SDK_INT >= 26)
    {
      i = paramPoolFactory.getFlexByteArrayPoolMaxNumThreads();
      return new OreoDecoder(paramPoolFactory.getBitmapPool(), i, new Pools.SynchronizedPool(i));
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = paramPoolFactory.getFlexByteArrayPoolMaxNumThreads();
      return new ArtDecoder(paramPoolFactory.getBitmapPool(), i, new Pools.SynchronizedPool(i));
    }
    if ((paramBoolean) && (Build.VERSION.SDK_INT < 19)) {
      return new GingerbreadPurgeableDecoder();
    }
    return new KitKatPurgeableDecoder(paramPoolFactory.getFlexByteArrayPool());
  }
}
