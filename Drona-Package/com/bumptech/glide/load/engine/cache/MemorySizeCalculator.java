package com.bumptech.glide.load.engine.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.util.Preconditions;

public final class MemorySizeCalculator
{
  @VisibleForTesting
  static final int BYTES_PER_ARGB_8888_PIXEL = 4;
  private static final int LOW_MEMORY_BYTE_ARRAY_POOL_DIVISOR = 2;
  private static final String TAG = "MemorySizeCalculator";
  private final int arrayPoolSize;
  private final int bitmapPoolSize;
  private final Context context;
  private final int memoryCacheSize;
  
  MemorySizeCalculator(Builder paramBuilder)
  {
    context = context;
    if (isLowMemoryDevice(activityManager)) {
      i = arrayPoolSizeBytes / 2;
    } else {
      i = arrayPoolSizeBytes;
    }
    arrayPoolSize = i;
    int i = getMaxSize(activityManager, maxSizeMultiplier, lowMemoryMaxSizeMultiplier);
    float f = screenDimensions.getWidthPixels() * screenDimensions.getHeightPixels() * 4;
    int j = Math.round(bitmapPoolScreens * f);
    int k = Math.round(f * memoryCacheScreens);
    int m = i - arrayPoolSize;
    int n = k + j;
    if (n <= m)
    {
      memoryCacheSize = k;
      bitmapPoolSize = j;
    }
    else
    {
      f = m / (bitmapPoolScreens + memoryCacheScreens);
      memoryCacheSize = Math.round(memoryCacheScreens * f);
      bitmapPoolSize = Math.round(f * bitmapPoolScreens);
    }
    if (Log.isLoggable("MemorySizeCalculator", 3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Calculation complete, Calculated memory cache size: ");
      localStringBuilder.append(toMb(memoryCacheSize));
      localStringBuilder.append(", pool size: ");
      localStringBuilder.append(toMb(bitmapPoolSize));
      localStringBuilder.append(", byte array size: ");
      localStringBuilder.append(toMb(arrayPoolSize));
      localStringBuilder.append(", memory class limited? ");
      boolean bool;
      if (n > i) {
        bool = true;
      } else {
        bool = false;
      }
      localStringBuilder.append(bool);
      localStringBuilder.append(", max size: ");
      localStringBuilder.append(toMb(i));
      localStringBuilder.append(", memoryClass: ");
      localStringBuilder.append(activityManager.getMemoryClass());
      localStringBuilder.append(", isLowMemoryDevice: ");
      localStringBuilder.append(isLowMemoryDevice(activityManager));
      Log.d("MemorySizeCalculator", localStringBuilder.toString());
    }
  }
  
  private static int getMaxSize(ActivityManager paramActivityManager, float paramFloat1, float paramFloat2)
  {
    int i = paramActivityManager.getMemoryClass();
    boolean bool = isLowMemoryDevice(paramActivityManager);
    float f = i * 1024 * 1024;
    if (bool) {
      paramFloat1 = paramFloat2;
    }
    return Math.round(f * paramFloat1);
  }
  
  static boolean isLowMemoryDevice(ActivityManager paramActivityManager)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramActivityManager.isLowRamDevice();
    }
    return true;
  }
  
  private String toMb(int paramInt)
  {
    return Formatter.formatFileSize(context, paramInt);
  }
  
  public int getArrayPoolSizeInBytes()
  {
    return arrayPoolSize;
  }
  
  public int getBitmapPoolSize()
  {
    return bitmapPoolSize;
  }
  
  public int getMemoryCacheSize()
  {
    return memoryCacheSize;
  }
  
  public static final class Builder
  {
    static final int ARRAY_POOL_SIZE_BYTES = 4194304;
    static final int BITMAP_POOL_TARGET_SCREENS;
    static final float LOW_MEMORY_MAX_SIZE_MULTIPLIER = 0.33F;
    static final float MAX_SIZE_MULTIPLIER = 0.4F;
    @VisibleForTesting
    static final int MEMORY_CACHE_TARGET_SCREENS = 2;
    ActivityManager activityManager;
    int arrayPoolSizeBytes = 4194304;
    float bitmapPoolScreens = BITMAP_POOL_TARGET_SCREENS;
    final Context context;
    float lowMemoryMaxSizeMultiplier = 0.33F;
    float maxSizeMultiplier = 0.4F;
    float memoryCacheScreens = 2.0F;
    MemorySizeCalculator.ScreenDimensions screenDimensions;
    
    static
    {
      int i;
      if (Build.VERSION.SDK_INT < 26) {
        i = 4;
      } else {
        i = 1;
      }
      BITMAP_POOL_TARGET_SCREENS = i;
    }
    
    public Builder(Context paramContext)
    {
      context = paramContext;
      activityManager = ((ActivityManager)paramContext.getSystemService("activity"));
      screenDimensions = new MemorySizeCalculator.DisplayMetricsScreenDimensions(paramContext.getResources().getDisplayMetrics());
      if ((Build.VERSION.SDK_INT >= 26) && (MemorySizeCalculator.isLowMemoryDevice(activityManager))) {
        bitmapPoolScreens = 0.0F;
      }
    }
    
    public MemorySizeCalculator build()
    {
      return new MemorySizeCalculator(this);
    }
    
    Builder setActivityManager(ActivityManager paramActivityManager)
    {
      activityManager = paramActivityManager;
      return this;
    }
    
    public Builder setArrayPoolSize(int paramInt)
    {
      arrayPoolSizeBytes = paramInt;
      return this;
    }
    
    public Builder setBitmapPoolScreens(float paramFloat)
    {
      boolean bool;
      if (paramFloat >= 0.0F) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "Bitmap pool screens must be greater than or equal to 0");
      bitmapPoolScreens = paramFloat;
      return this;
    }
    
    public Builder setLowMemoryMaxSizeMultiplier(float paramFloat)
    {
      boolean bool;
      if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "Low memory max size multiplier must be between 0 and 1");
      lowMemoryMaxSizeMultiplier = paramFloat;
      return this;
    }
    
    public Builder setMaxSizeMultiplier(float paramFloat)
    {
      boolean bool;
      if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "Size multiplier must be between 0 and 1");
      maxSizeMultiplier = paramFloat;
      return this;
    }
    
    public Builder setMemoryCacheScreens(float paramFloat)
    {
      boolean bool;
      if (paramFloat >= 0.0F) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "Memory cache screens must be greater than or equal to 0");
      memoryCacheScreens = paramFloat;
      return this;
    }
    
    Builder setScreenDimensions(MemorySizeCalculator.ScreenDimensions paramScreenDimensions)
    {
      screenDimensions = paramScreenDimensions;
      return this;
    }
  }
  
  private static final class DisplayMetricsScreenDimensions
    implements MemorySizeCalculator.ScreenDimensions
  {
    private final DisplayMetrics displayMetrics;
    
    DisplayMetricsScreenDimensions(DisplayMetrics paramDisplayMetrics)
    {
      displayMetrics = paramDisplayMetrics;
    }
    
    public int getHeightPixels()
    {
      return displayMetrics.heightPixels;
    }
    
    public int getWidthPixels()
    {
      return displayMetrics.widthPixels;
    }
  }
  
  static abstract interface ScreenDimensions
  {
    public abstract int getHeightPixels();
    
    public abstract int getWidthPixels();
  }
}
