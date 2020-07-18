package com.facebook.imagepipeline.cache;

import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.time.RealtimeSinceBootClock;
import com.facebook.common.util.HashCodeUtil;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BitmapMemoryCacheKey
  implements CacheKey
{
  private final long mCacheTime;
  private final Object mCallerContext;
  private final int mHash;
  private final ImageDecodeOptions mImageDecodeOptions;
  @Nullable
  private final CacheKey mPostprocessorCacheKey;
  @Nullable
  private final String mPostprocessorName;
  @Nullable
  private final ResizeOptions mResizeOptions;
  private final RotationOptions mRotationOptions;
  private final String mSourceString;
  
  public BitmapMemoryCacheKey(String paramString1, ResizeOptions paramResizeOptions, RotationOptions paramRotationOptions, ImageDecodeOptions paramImageDecodeOptions, CacheKey paramCacheKey, String paramString2, Object paramObject)
  {
    mSourceString = ((String)Preconditions.checkNotNull(paramString1));
    mResizeOptions = paramResizeOptions;
    mRotationOptions = paramRotationOptions;
    mImageDecodeOptions = paramImageDecodeOptions;
    mPostprocessorCacheKey = paramCacheKey;
    mPostprocessorName = paramString2;
    int j = paramString1.hashCode();
    int i;
    if (paramResizeOptions != null) {
      i = paramResizeOptions.hashCode();
    } else {
      i = 0;
    }
    mHash = HashCodeUtil.hashCode(Integer.valueOf(j), Integer.valueOf(i), Integer.valueOf(paramRotationOptions.hashCode()), mImageDecodeOptions, mPostprocessorCacheKey, paramString2);
    mCallerContext = paramObject;
    mCacheTime = RealtimeSinceBootClock.notNull().now();
  }
  
  public boolean containsUri(Uri paramUri)
  {
    return getUriString().contains(paramUri.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof BitmapMemoryCacheKey)) {
      return false;
    }
    paramObject = (BitmapMemoryCacheKey)paramObject;
    return (mHash == mHash) && (mSourceString.equals(mSourceString)) && (Objects.equal(mResizeOptions, mResizeOptions)) && (Objects.equal(mRotationOptions, mRotationOptions)) && (Objects.equal(mImageDecodeOptions, mImageDecodeOptions)) && (Objects.equal(mPostprocessorCacheKey, mPostprocessorCacheKey)) && (Objects.equal(mPostprocessorName, mPostprocessorName));
  }
  
  public Object getCallerContext()
  {
    return mCallerContext;
  }
  
  public long getInBitmapCacheSince()
  {
    return mCacheTime;
  }
  
  public String getPostprocessorName()
  {
    return mPostprocessorName;
  }
  
  public String getUriString()
  {
    return mSourceString;
  }
  
  public int hashCode()
  {
    return mHash;
  }
  
  public String toString()
  {
    return String.format(null, "%s_%s_%s_%s_%s_%s_%d", new Object[] { mSourceString, mResizeOptions, mRotationOptions, mImageDecodeOptions, mPostprocessorCacheKey, mPostprocessorName, Integer.valueOf(mHash) });
  }
}
