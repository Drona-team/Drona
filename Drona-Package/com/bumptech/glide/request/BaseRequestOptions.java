package com.bumptech.glide.request;

import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.DrawableTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.signature.EmptySignature;
import com.bumptech.glide.util.CachedHashCodeArrayMap;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.Map;

public abstract class BaseRequestOptions<T extends BaseRequestOptions<T>>
  implements Cloneable
{
  private static final int DISK_CACHE_STRATEGY = 4;
  private static final int ERROR_ID = 32;
  private static final int ERROR_PLACEHOLDER = 16;
  private static final int FALLBACK = 8192;
  private static final int FALLBACK_ID = 16384;
  private static final int IS_CACHEABLE = 256;
  private static final int ONLY_RETRIEVE_FROM_CACHE = 524288;
  private static final int OVERRIDE = 512;
  private static final int PLACEHOLDER = 64;
  private static final int PLACEHOLDER_ID = 128;
  private static final int PRIORITY = 8;
  private static final int RESOURCE_CLASS = 4096;
  private static final int SIGNATURE = 1024;
  private static final int SIZE_MULTIPLIER = 2;
  private static final int THEME = 32768;
  private static final int TRANSFORMATION = 2048;
  private static final int TRANSFORMATION_ALLOWED = 65536;
  private static final int TRANSFORMATION_REQUIRED = 131072;
  private static final int UNSET = -1;
  private static final int USE_ANIMATION_POOL = 1048576;
  private static final int USE_UNLIMITED_SOURCE_GENERATORS_POOL = 262144;
  @NonNull
  private DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.AUTOMATIC;
  private int errorId;
  @Nullable
  private Drawable errorPlaceholder;
  @Nullable
  private Drawable fallbackDrawable;
  private int fallbackId;
  private int fields;
  private boolean isAutoCloneEnabled;
  private boolean isCacheable = true;
  private boolean isLocked;
  private boolean isScaleOnlyOrNoTransform = true;
  private boolean isTransformationAllowed = true;
  private boolean isTransformationRequired;
  private boolean onlyRetrieveFromCache;
  @NonNull
  private Options options = new Options();
  private int overrideHeight = -1;
  private int overrideWidth = -1;
  @Nullable
  private Drawable placeholderDrawable;
  private int placeholderId;
  @NonNull
  private Priority priority = Priority.NORMAL;
  @NonNull
  private Class<?> resourceClass = Object.class;
  @NonNull
  private Key signature = EmptySignature.obtain();
  private float sizeMultiplier = 1.0F;
  @Nullable
  private Resources.Theme theme;
  @NonNull
  private Map<Class<?>, Transformation<?>> transformations = new CachedHashCodeArrayMap();
  private boolean useAnimationPool;
  private boolean useUnlimitedSourceGeneratorsPool;
  
  public BaseRequestOptions() {}
  
  private boolean isSet(int paramInt)
  {
    return isSet(fields, paramInt);
  }
  
  private static boolean isSet(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) != 0;
  }
  
  private BaseRequestOptions optionalScaleOnlyTransform(DownsampleStrategy paramDownsampleStrategy, Transformation paramTransformation)
  {
    return scaleOnlyTransform(paramDownsampleStrategy, paramTransformation, false);
  }
  
  private BaseRequestOptions scaleOnlyTransform(DownsampleStrategy paramDownsampleStrategy, Transformation paramTransformation)
  {
    return scaleOnlyTransform(paramDownsampleStrategy, paramTransformation, true);
  }
  
  private BaseRequestOptions scaleOnlyTransform(DownsampleStrategy paramDownsampleStrategy, Transformation paramTransformation, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramDownsampleStrategy = transform(paramDownsampleStrategy, paramTransformation);
    } else {
      paramDownsampleStrategy = optionalTransform(paramDownsampleStrategy, paramTransformation);
    }
    isScaleOnlyOrNoTransform = true;
    return paramDownsampleStrategy;
  }
  
  private BaseRequestOptions self()
  {
    return this;
  }
  
  private BaseRequestOptions selfOrThrowIfLocked()
  {
    if (!isLocked) {
      return self();
    }
    throw new IllegalStateException("You cannot modify locked T, consider clone()");
  }
  
  public BaseRequestOptions apply(BaseRequestOptions paramBaseRequestOptions)
  {
    if (isAutoCloneEnabled) {
      return clone().apply(paramBaseRequestOptions);
    }
    if (isSet(fields, 2)) {
      sizeMultiplier = sizeMultiplier;
    }
    if (isSet(fields, 262144)) {
      useUnlimitedSourceGeneratorsPool = useUnlimitedSourceGeneratorsPool;
    }
    if (isSet(fields, 1048576)) {
      useAnimationPool = useAnimationPool;
    }
    if (isSet(fields, 4)) {
      diskCacheStrategy = diskCacheStrategy;
    }
    if (isSet(fields, 8)) {
      priority = priority;
    }
    if (isSet(fields, 16))
    {
      errorPlaceholder = errorPlaceholder;
      errorId = 0;
      fields &= 0xFFFFFFDF;
    }
    if (isSet(fields, 32))
    {
      errorId = errorId;
      errorPlaceholder = null;
      fields &= 0xFFFFFFEF;
    }
    if (isSet(fields, 64))
    {
      placeholderDrawable = placeholderDrawable;
      placeholderId = 0;
      fields &= 0xFF7F;
    }
    if (isSet(fields, 128))
    {
      placeholderId = placeholderId;
      placeholderDrawable = null;
      fields &= 0xFFFFFFBF;
    }
    if (isSet(fields, 256)) {
      isCacheable = isCacheable;
    }
    if (isSet(fields, 512))
    {
      overrideWidth = overrideWidth;
      overrideHeight = overrideHeight;
    }
    if (isSet(fields, 1024)) {
      signature = signature;
    }
    if (isSet(fields, 4096)) {
      resourceClass = resourceClass;
    }
    if (isSet(fields, 8192))
    {
      fallbackDrawable = fallbackDrawable;
      fallbackId = 0;
      fields &= 0xBFFF;
    }
    if (isSet(fields, 16384))
    {
      fallbackId = fallbackId;
      fallbackDrawable = null;
      fields &= 0xDFFF;
    }
    if (isSet(fields, 32768)) {
      theme = theme;
    }
    if (isSet(fields, 65536)) {
      isTransformationAllowed = isTransformationAllowed;
    }
    if (isSet(fields, 131072)) {
      isTransformationRequired = isTransformationRequired;
    }
    if (isSet(fields, 2048))
    {
      transformations.putAll(transformations);
      isScaleOnlyOrNoTransform = isScaleOnlyOrNoTransform;
    }
    if (isSet(fields, 524288)) {
      onlyRetrieveFromCache = onlyRetrieveFromCache;
    }
    if (!isTransformationAllowed)
    {
      transformations.clear();
      fields &= 0xF7FF;
      isTransformationRequired = false;
      fields &= 0xFFFDFFFF;
      isScaleOnlyOrNoTransform = true;
    }
    fields |= fields;
    options.putAll(options);
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions autoClone()
  {
    if ((isLocked) && (!isAutoCloneEnabled)) {
      throw new IllegalStateException("You cannot auto lock an already locked options object, try clone() first");
    }
    isAutoCloneEnabled = true;
    return lock();
  }
  
  public BaseRequestOptions centerCrop()
  {
    return transform(DownsampleStrategy.CENTER_OUTSIDE, new CenterCrop());
  }
  
  public BaseRequestOptions centerInside()
  {
    return scaleOnlyTransform(DownsampleStrategy.CENTER_INSIDE, new CenterInside());
  }
  
  public BaseRequestOptions circleCrop()
  {
    return transform(DownsampleStrategy.CENTER_INSIDE, new CircleCrop());
  }
  
  public BaseRequestOptions clone()
  {
    try
    {
      Object localObject1 = super.clone();
      localObject1 = (BaseRequestOptions)localObject1;
      Object localObject2 = new Options();
      options = ((Options)localObject2);
      localObject2 = options;
      Object localObject3 = options;
      ((Options)localObject2).putAll((Options)localObject3);
      localObject2 = new CachedHashCodeArrayMap();
      transformations = ((Map)localObject2);
      localObject2 = transformations;
      localObject3 = transformations;
      ((Map)localObject2).putAll((Map)localObject3);
      isLocked = false;
      isAutoCloneEnabled = false;
      return localObject1;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  public BaseRequestOptions decode(Class paramClass)
  {
    if (isAutoCloneEnabled) {
      return clone().decode(paramClass);
    }
    resourceClass = ((Class)Preconditions.checkNotNull(paramClass));
    fields |= 0x1000;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions disallowHardwareConfig()
  {
    return divide(Downsampler.ALLOW_HARDWARE_CONFIG, Boolean.valueOf(false));
  }
  
  public BaseRequestOptions diskCacheStrategy(DiskCacheStrategy paramDiskCacheStrategy)
  {
    if (isAutoCloneEnabled) {
      return clone().diskCacheStrategy(paramDiskCacheStrategy);
    }
    diskCacheStrategy = ((DiskCacheStrategy)Preconditions.checkNotNull(paramDiskCacheStrategy));
    fields |= 0x4;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions divide(Option paramOption, Object paramObject)
  {
    if (isAutoCloneEnabled) {
      return clone().divide(paramOption, paramObject);
    }
    Preconditions.checkNotNull(paramOption);
    Preconditions.checkNotNull(paramObject);
    options.add(paramOption, paramObject);
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions dontAnimate()
  {
    return divide(GifOptions.DISABLE_ANIMATION, Boolean.valueOf(true));
  }
  
  public BaseRequestOptions dontTransform()
  {
    if (isAutoCloneEnabled) {
      return clone().dontTransform();
    }
    transformations.clear();
    fields &= 0xF7FF;
    isTransformationRequired = false;
    fields &= 0xFFFDFFFF;
    isTransformationAllowed = false;
    fields |= 0x10000;
    isScaleOnlyOrNoTransform = true;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions downsample(DownsampleStrategy paramDownsampleStrategy)
  {
    return divide(DownsampleStrategy.OPTION, Preconditions.checkNotNull(paramDownsampleStrategy));
  }
  
  public BaseRequestOptions encodeFormat(Bitmap.CompressFormat paramCompressFormat)
  {
    return divide(BitmapEncoder.COMPRESSION_FORMAT, Preconditions.checkNotNull(paramCompressFormat));
  }
  
  public BaseRequestOptions encodeQuality(int paramInt)
  {
    return divide(BitmapEncoder.COMPRESSION_QUALITY, Integer.valueOf(paramInt));
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof BaseRequestOptions))
    {
      paramObject = (BaseRequestOptions)paramObject;
      if ((Float.compare(sizeMultiplier, sizeMultiplier) == 0) && (errorId == errorId) && (Util.bothNullOrEqual(errorPlaceholder, errorPlaceholder)) && (placeholderId == placeholderId) && (Util.bothNullOrEqual(placeholderDrawable, placeholderDrawable)) && (fallbackId == fallbackId) && (Util.bothNullOrEqual(fallbackDrawable, fallbackDrawable)) && (isCacheable == isCacheable) && (overrideHeight == overrideHeight) && (overrideWidth == overrideWidth) && (isTransformationRequired == isTransformationRequired) && (isTransformationAllowed == isTransformationAllowed) && (useUnlimitedSourceGeneratorsPool == useUnlimitedSourceGeneratorsPool) && (onlyRetrieveFromCache == onlyRetrieveFromCache) && (diskCacheStrategy.equals(diskCacheStrategy)) && (priority == priority) && (options.equals(options)) && (transformations.equals(transformations)) && (resourceClass.equals(resourceClass)) && (Util.bothNullOrEqual(signature, signature)) && (Util.bothNullOrEqual(theme, theme))) {
        return true;
      }
    }
    return false;
  }
  
  public BaseRequestOptions error(int paramInt)
  {
    if (isAutoCloneEnabled) {
      return clone().error(paramInt);
    }
    errorId = paramInt;
    fields |= 0x20;
    errorPlaceholder = null;
    fields &= 0xFFFFFFEF;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions error(Drawable paramDrawable)
  {
    if (isAutoCloneEnabled) {
      return clone().error(paramDrawable);
    }
    errorPlaceholder = paramDrawable;
    fields |= 0x10;
    errorId = 0;
    fields &= 0xFFFFFFDF;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions fallback(int paramInt)
  {
    if (isAutoCloneEnabled) {
      return clone().fallback(paramInt);
    }
    fallbackId = paramInt;
    fields |= 0x4000;
    fallbackDrawable = null;
    fields &= 0xDFFF;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions fallback(Drawable paramDrawable)
  {
    if (isAutoCloneEnabled) {
      return clone().fallback(paramDrawable);
    }
    fallbackDrawable = paramDrawable;
    fields |= 0x2000;
    fallbackId = 0;
    fields &= 0xBFFF;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions fitCenter()
  {
    return scaleOnlyTransform(DownsampleStrategy.FIT_CENTER, new FitCenter());
  }
  
  public BaseRequestOptions format(DecodeFormat paramDecodeFormat)
  {
    Preconditions.checkNotNull(paramDecodeFormat);
    return divide(Downsampler.DECODE_FORMAT, paramDecodeFormat).divide(GifOptions.DECODE_FORMAT, paramDecodeFormat);
  }
  
  public BaseRequestOptions frame(long paramLong)
  {
    return divide(VideoDecoder.TARGET_FRAME, Long.valueOf(paramLong));
  }
  
  public final DiskCacheStrategy getDiskCacheStrategy()
  {
    return diskCacheStrategy;
  }
  
  public final int getErrorId()
  {
    return errorId;
  }
  
  public final Drawable getErrorPlaceholder()
  {
    return errorPlaceholder;
  }
  
  public final Drawable getFallbackDrawable()
  {
    return fallbackDrawable;
  }
  
  public final int getFallbackId()
  {
    return fallbackId;
  }
  
  public final boolean getOnlyRetrieveFromCache()
  {
    return onlyRetrieveFromCache;
  }
  
  public final Options getOptions()
  {
    return options;
  }
  
  public final int getOverrideHeight()
  {
    return overrideHeight;
  }
  
  public final int getOverrideWidth()
  {
    return overrideWidth;
  }
  
  public final Drawable getPlaceholderDrawable()
  {
    return placeholderDrawable;
  }
  
  public final int getPlaceholderId()
  {
    return placeholderId;
  }
  
  public final Priority getPriority()
  {
    return priority;
  }
  
  public final Class getResourceClass()
  {
    return resourceClass;
  }
  
  public final Key getSignature()
  {
    return signature;
  }
  
  public final float getSizeMultiplier()
  {
    return sizeMultiplier;
  }
  
  public final Resources.Theme getTheme()
  {
    return theme;
  }
  
  public final Map getTransformations()
  {
    return transformations;
  }
  
  public final boolean getUseAnimationPool()
  {
    return useAnimationPool;
  }
  
  public final boolean getUseUnlimitedSourceGeneratorsPool()
  {
    return useUnlimitedSourceGeneratorsPool;
  }
  
  public int hashCode()
  {
    int i = Util.hashCode(sizeMultiplier);
    i = Util.hashCode(errorId, i);
    i = Util.hashCode(errorPlaceholder, i);
    i = Util.hashCode(placeholderId, i);
    i = Util.hashCode(placeholderDrawable, i);
    i = Util.hashCode(fallbackId, i);
    i = Util.hashCode(fallbackDrawable, i);
    i = Util.hashCode(isCacheable, i);
    i = Util.hashCode(overrideHeight, i);
    i = Util.hashCode(overrideWidth, i);
    i = Util.hashCode(isTransformationRequired, i);
    i = Util.hashCode(isTransformationAllowed, i);
    i = Util.hashCode(useUnlimitedSourceGeneratorsPool, i);
    i = Util.hashCode(onlyRetrieveFromCache, i);
    i = Util.hashCode(diskCacheStrategy, i);
    i = Util.hashCode(priority, i);
    i = Util.hashCode(options, i);
    i = Util.hashCode(transformations, i);
    i = Util.hashCode(resourceClass, i);
    i = Util.hashCode(signature, i);
    return Util.hashCode(theme, i);
  }
  
  protected boolean isAutoCloneEnabled()
  {
    return isAutoCloneEnabled;
  }
  
  public final boolean isDiskCacheStrategySet()
  {
    return isSet(4);
  }
  
  public final boolean isLocked()
  {
    return isLocked;
  }
  
  public final boolean isMemoryCacheable()
  {
    return isCacheable;
  }
  
  public final boolean isPrioritySet()
  {
    return isSet(8);
  }
  
  boolean isScaleOnlyOrNoTransform()
  {
    return isScaleOnlyOrNoTransform;
  }
  
  public final boolean isSkipMemoryCacheSet()
  {
    return isSet(256);
  }
  
  public final boolean isTransformationAllowed()
  {
    return isTransformationAllowed;
  }
  
  public final boolean isTransformationRequired()
  {
    return isTransformationRequired;
  }
  
  public final boolean isTransformationSet()
  {
    return isSet(2048);
  }
  
  public final boolean isValidOverride()
  {
    return Util.isValidDimensions(overrideWidth, overrideHeight);
  }
  
  public BaseRequestOptions lock()
  {
    isLocked = true;
    return self();
  }
  
  public BaseRequestOptions onlyRetrieveFromCache(boolean paramBoolean)
  {
    if (isAutoCloneEnabled) {
      return clone().onlyRetrieveFromCache(paramBoolean);
    }
    onlyRetrieveFromCache = paramBoolean;
    fields |= 0x80000;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions optionalCenterCrop()
  {
    return optionalTransform(DownsampleStrategy.CENTER_OUTSIDE, new CenterCrop());
  }
  
  public BaseRequestOptions optionalCenterInside()
  {
    return optionalScaleOnlyTransform(DownsampleStrategy.CENTER_INSIDE, new CenterInside());
  }
  
  public BaseRequestOptions optionalCircleCrop()
  {
    return optionalTransform(DownsampleStrategy.CENTER_OUTSIDE, new CircleCrop());
  }
  
  public BaseRequestOptions optionalFitCenter()
  {
    return optionalScaleOnlyTransform(DownsampleStrategy.FIT_CENTER, new FitCenter());
  }
  
  public BaseRequestOptions optionalTransform(Transformation paramTransformation)
  {
    return transform(paramTransformation, false);
  }
  
  final BaseRequestOptions optionalTransform(DownsampleStrategy paramDownsampleStrategy, Transformation paramTransformation)
  {
    if (isAutoCloneEnabled) {
      return clone().optionalTransform(paramDownsampleStrategy, paramTransformation);
    }
    downsample(paramDownsampleStrategy);
    return transform(paramTransformation, false);
  }
  
  public BaseRequestOptions optionalTransform(Class paramClass, Transformation paramTransformation)
  {
    return transform(paramClass, paramTransformation, false);
  }
  
  public BaseRequestOptions override(int paramInt)
  {
    return override(paramInt, paramInt);
  }
  
  public BaseRequestOptions override(int paramInt1, int paramInt2)
  {
    if (isAutoCloneEnabled) {
      return clone().override(paramInt1, paramInt2);
    }
    overrideWidth = paramInt1;
    overrideHeight = paramInt2;
    fields |= 0x200;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions placeholder(int paramInt)
  {
    if (isAutoCloneEnabled) {
      return clone().placeholder(paramInt);
    }
    placeholderId = paramInt;
    fields |= 0x80;
    placeholderDrawable = null;
    fields &= 0xFFFFFFBF;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions placeholder(Drawable paramDrawable)
  {
    if (isAutoCloneEnabled) {
      return clone().placeholder(paramDrawable);
    }
    placeholderDrawable = paramDrawable;
    fields |= 0x40;
    placeholderId = 0;
    fields &= 0xFF7F;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions priority(Priority paramPriority)
  {
    if (isAutoCloneEnabled) {
      return clone().priority(paramPriority);
    }
    priority = ((Priority)Preconditions.checkNotNull(paramPriority));
    fields |= 0x8;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions signature(Key paramKey)
  {
    if (isAutoCloneEnabled) {
      return clone().signature(paramKey);
    }
    signature = ((Key)Preconditions.checkNotNull(paramKey));
    fields |= 0x400;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions sizeMultiplier(float paramFloat)
  {
    if (isAutoCloneEnabled) {
      return clone().sizeMultiplier(paramFloat);
    }
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F))
    {
      sizeMultiplier = paramFloat;
      fields |= 0x2;
      return selfOrThrowIfLocked();
    }
    throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
  }
  
  public BaseRequestOptions skipMemoryCache(boolean paramBoolean)
  {
    if (isAutoCloneEnabled) {
      return clone().skipMemoryCache(true);
    }
    isCacheable = (paramBoolean ^ true);
    fields |= 0x100;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions theme(Resources.Theme paramTheme)
  {
    if (isAutoCloneEnabled) {
      return clone().theme(paramTheme);
    }
    theme = paramTheme;
    fields |= 0x8000;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions timeout(int paramInt)
  {
    return divide(HttpGlideUrlLoader.TIMEOUT, Integer.valueOf(paramInt));
  }
  
  public BaseRequestOptions transform(Transformation paramTransformation)
  {
    return transform(paramTransformation, true);
  }
  
  BaseRequestOptions transform(Transformation paramTransformation, boolean paramBoolean)
  {
    if (isAutoCloneEnabled) {
      return clone().transform(paramTransformation, paramBoolean);
    }
    DrawableTransformation localDrawableTransformation = new DrawableTransformation(paramTransformation, paramBoolean);
    transform(Bitmap.class, paramTransformation, paramBoolean);
    transform(Drawable.class, localDrawableTransformation, paramBoolean);
    transform(BitmapDrawable.class, localDrawableTransformation.asBitmapDrawable(), paramBoolean);
    transform(GifDrawable.class, new GifDrawableTransformation(paramTransformation), paramBoolean);
    return selfOrThrowIfLocked();
  }
  
  final BaseRequestOptions transform(DownsampleStrategy paramDownsampleStrategy, Transformation paramTransformation)
  {
    if (isAutoCloneEnabled) {
      return clone().transform(paramDownsampleStrategy, paramTransformation);
    }
    downsample(paramDownsampleStrategy);
    return transform(paramTransformation);
  }
  
  public BaseRequestOptions transform(Class paramClass, Transformation paramTransformation)
  {
    return transform(paramClass, paramTransformation, true);
  }
  
  BaseRequestOptions transform(Class paramClass, Transformation paramTransformation, boolean paramBoolean)
  {
    if (isAutoCloneEnabled) {
      return clone().transform(paramClass, paramTransformation, paramBoolean);
    }
    Preconditions.checkNotNull(paramClass);
    Preconditions.checkNotNull(paramTransformation);
    transformations.put(paramClass, paramTransformation);
    fields |= 0x800;
    isTransformationAllowed = true;
    fields |= 0x10000;
    isScaleOnlyOrNoTransform = false;
    if (paramBoolean)
    {
      fields |= 0x20000;
      isTransformationRequired = true;
    }
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions transform(Transformation... paramVarArgs)
  {
    if (paramVarArgs.length > 1) {
      return transform(new MultiTransformation(paramVarArgs), true);
    }
    if (paramVarArgs.length == 1) {
      return transform(paramVarArgs[0]);
    }
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions transforms(Transformation... paramVarArgs)
  {
    return transform(new MultiTransformation(paramVarArgs), true);
  }
  
  public BaseRequestOptions useAnimationPool(boolean paramBoolean)
  {
    if (isAutoCloneEnabled) {
      return clone().useAnimationPool(paramBoolean);
    }
    useAnimationPool = paramBoolean;
    fields |= 0x100000;
    return selfOrThrowIfLocked();
  }
  
  public BaseRequestOptions useUnlimitedSourceGeneratorsPool(boolean paramBoolean)
  {
    if (isAutoCloneEnabled) {
      return clone().useUnlimitedSourceGeneratorsPool(paramBoolean);
    }
    useUnlimitedSourceGeneratorsPool = paramBoolean;
    fields |= 0x40000;
    return selfOrThrowIfLocked();
  }
}
