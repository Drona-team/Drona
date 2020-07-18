package com.bumptech.glide.request;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;

public class RequestOptions
  extends BaseRequestOptions<RequestOptions>
{
  @Nullable
  private static RequestOptions centerCropOptions;
  @Nullable
  private static RequestOptions centerInsideOptions;
  @Nullable
  private static RequestOptions circleCropOptions;
  @Nullable
  private static RequestOptions fitCenterOptions;
  @Nullable
  private static RequestOptions noAnimationOptions;
  @Nullable
  private static RequestOptions noTransformOptions;
  @Nullable
  private static RequestOptions skipMemoryCacheFalseOptions;
  @Nullable
  private static RequestOptions skipMemoryCacheTrueOptions;
  
  public RequestOptions() {}
  
  public static RequestOptions bitmapTransform(Transformation paramTransformation)
  {
    return (RequestOptions)new RequestOptions().transform(paramTransformation);
  }
  
  public static RequestOptions centerCropTransform()
  {
    if (centerCropOptions == null) {
      centerCropOptions = (RequestOptions)((RequestOptions)new RequestOptions().centerCrop()).autoClone();
    }
    return centerCropOptions;
  }
  
  public static RequestOptions centerInsideTransform()
  {
    if (centerInsideOptions == null) {
      centerInsideOptions = (RequestOptions)((RequestOptions)new RequestOptions().centerInside()).autoClone();
    }
    return centerInsideOptions;
  }
  
  public static RequestOptions circleCropTransform()
  {
    if (circleCropOptions == null) {
      circleCropOptions = (RequestOptions)((RequestOptions)new RequestOptions().circleCrop()).autoClone();
    }
    return circleCropOptions;
  }
  
  public static RequestOptions decodeTypeOf(Class paramClass)
  {
    return (RequestOptions)new RequestOptions().decode(paramClass);
  }
  
  public static RequestOptions diskCacheStrategyOf(DiskCacheStrategy paramDiskCacheStrategy)
  {
    return (RequestOptions)new RequestOptions().diskCacheStrategy(paramDiskCacheStrategy);
  }
  
  public static RequestOptions downsampleOf(DownsampleStrategy paramDownsampleStrategy)
  {
    return (RequestOptions)new RequestOptions().downsample(paramDownsampleStrategy);
  }
  
  public static RequestOptions encodeFormatOf(Bitmap.CompressFormat paramCompressFormat)
  {
    return (RequestOptions)new RequestOptions().encodeFormat(paramCompressFormat);
  }
  
  public static RequestOptions encodeQualityOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().encodeQuality(paramInt);
  }
  
  public static RequestOptions errorOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().error(paramInt);
  }
  
  public static RequestOptions errorOf(Drawable paramDrawable)
  {
    return (RequestOptions)new RequestOptions().error(paramDrawable);
  }
  
  public static RequestOptions fitCenterTransform()
  {
    if (fitCenterOptions == null) {
      fitCenterOptions = (RequestOptions)((RequestOptions)new RequestOptions().fitCenter()).autoClone();
    }
    return fitCenterOptions;
  }
  
  public static RequestOptions formatOf(DecodeFormat paramDecodeFormat)
  {
    return (RequestOptions)new RequestOptions().format(paramDecodeFormat);
  }
  
  public static RequestOptions frameOf(long paramLong)
  {
    return (RequestOptions)new RequestOptions().frame(paramLong);
  }
  
  public static RequestOptions noAnimation()
  {
    if (noAnimationOptions == null) {
      noAnimationOptions = (RequestOptions)((RequestOptions)new RequestOptions().dontAnimate()).autoClone();
    }
    return noAnimationOptions;
  }
  
  public static RequestOptions noTransformation()
  {
    if (noTransformOptions == null) {
      noTransformOptions = (RequestOptions)((RequestOptions)new RequestOptions().dontTransform()).autoClone();
    }
    return noTransformOptions;
  }
  
  public static RequestOptions option(Option paramOption, Object paramObject)
  {
    return (RequestOptions)new RequestOptions().divide(paramOption, paramObject);
  }
  
  public static RequestOptions overrideOf(int paramInt)
  {
    return overrideOf(paramInt, paramInt);
  }
  
  public static RequestOptions overrideOf(int paramInt1, int paramInt2)
  {
    return (RequestOptions)new RequestOptions().override(paramInt1, paramInt2);
  }
  
  public static RequestOptions placeholderOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().placeholder(paramInt);
  }
  
  public static RequestOptions placeholderOf(Drawable paramDrawable)
  {
    return (RequestOptions)new RequestOptions().placeholder(paramDrawable);
  }
  
  public static RequestOptions priorityOf(Priority paramPriority)
  {
    return (RequestOptions)new RequestOptions().priority(paramPriority);
  }
  
  public static RequestOptions signatureOf(Key paramKey)
  {
    return (RequestOptions)new RequestOptions().signature(paramKey);
  }
  
  public static RequestOptions sizeMultiplierOf(float paramFloat)
  {
    return (RequestOptions)new RequestOptions().sizeMultiplier(paramFloat);
  }
  
  public static RequestOptions skipMemoryCacheOf(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (skipMemoryCacheTrueOptions == null) {
        skipMemoryCacheTrueOptions = (RequestOptions)((RequestOptions)new RequestOptions().skipMemoryCache(true)).autoClone();
      }
      return skipMemoryCacheTrueOptions;
    }
    if (skipMemoryCacheFalseOptions == null) {
      skipMemoryCacheFalseOptions = (RequestOptions)((RequestOptions)new RequestOptions().skipMemoryCache(false)).autoClone();
    }
    return skipMemoryCacheFalseOptions;
  }
  
  public static RequestOptions timeoutOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().timeout(paramInt);
  }
}
