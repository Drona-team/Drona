package com.bumptech.glide.util;

import com.bumptech.glide.ListPreloader.PreloadSizeProvider;

public class FixedPreloadSizeProvider<T>
  implements ListPreloader.PreloadSizeProvider<T>
{
  private final int[] size;
  
  public FixedPreloadSizeProvider(int paramInt1, int paramInt2)
  {
    size = new int[] { paramInt1, paramInt2 };
  }
  
  public int[] getPreloadSize(Object paramObject, int paramInt1, int paramInt2)
  {
    return size;
  }
}
