package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public final class DataSpec
{
  public static final int FLAG_ALLOW_CACHING_UNKNOWN_LENGTH = 2;
  public static final int FLAG_ALLOW_GZIP = 1;
  public static final int HTTP_METHOD_GET = 1;
  public static final int HTTP_METHOD_HEAD = 3;
  public static final int HTTP_METHOD_POST = 2;
  public final long absoluteStreamPosition;
  public final int flags;
  @Nullable
  public final byte[] httpBody;
  public final int httpMethod;
  @Nullable
  public final String key;
  public final long length;
  public final long position;
  @Deprecated
  @Nullable
  public final byte[] postBody;
  public final Uri uri;
  
  public DataSpec(Uri paramUri)
  {
    this(paramUri, 0);
  }
  
  public DataSpec(Uri paramUri, int paramInt)
  {
    this(paramUri, 0L, -1L, null, paramInt);
  }
  
  public DataSpec(Uri paramUri, int paramInt1, byte[] paramArrayOfByte, long paramLong1, long paramLong2, long paramLong3, String paramString, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1;
    if (paramLong1 >= 0L) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    if (paramLong2 >= 0L) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkArgument(bool1);
    if (paramLong3 <= 0L)
    {
      bool1 = bool2;
      if (paramLong3 != -1L) {}
    }
    else
    {
      bool1 = true;
    }
    Assertions.checkArgument(bool1);
    uri = paramUri;
    httpMethod = paramInt1;
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      arrayOfByte = null;
    }
    httpBody = arrayOfByte;
    postBody = httpBody;
    absoluteStreamPosition = paramLong1;
    position = paramLong2;
    length = paramLong3;
    key = paramString;
    flags = paramInt2;
  }
  
  public DataSpec(Uri paramUri, long paramLong1, long paramLong2, long paramLong3, String paramString, int paramInt)
  {
    this(paramUri, null, paramLong1, paramLong2, paramLong3, paramString, paramInt);
  }
  
  public DataSpec(Uri paramUri, long paramLong1, long paramLong2, String paramString)
  {
    this(paramUri, paramLong1, paramLong1, paramLong2, paramString, 0);
  }
  
  public DataSpec(Uri paramUri, long paramLong1, long paramLong2, String paramString, int paramInt)
  {
    this(paramUri, paramLong1, paramLong1, paramLong2, paramString, paramInt);
  }
  
  public DataSpec(Uri paramUri, byte[] paramArrayOfByte, long paramLong1, long paramLong2, long paramLong3, String paramString, int paramInt)
  {
    this(paramUri, i, paramArrayOfByte, paramLong1, paramLong2, paramLong3, paramString, paramInt);
  }
  
  public static String getStringForHttpMethod(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new AssertionError(paramInt);
    case 3: 
      return "HEAD";
    case 2: 
      return "POST";
    }
    return "GET";
  }
  
  public final String getHttpMethodString()
  {
    return getStringForHttpMethod(httpMethod);
  }
  
  public boolean isFlagSet(int paramInt)
  {
    return (flags & paramInt) == paramInt;
  }
  
  public DataSpec subrange(long paramLong)
  {
    long l2 = length;
    long l1 = -1L;
    if (l2 != -1L) {
      l1 = length - paramLong;
    }
    return subrange(paramLong, l1);
  }
  
  public DataSpec subrange(long paramLong1, long paramLong2)
  {
    if ((paramLong1 == 0L) && (length == paramLong2)) {
      return this;
    }
    return new DataSpec(uri, httpMethod, httpBody, absoluteStreamPosition + paramLong1, position + paramLong1, paramLong2, key, flags);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DataSpec[");
    localStringBuilder.append(getHttpMethodString());
    localStringBuilder.append(" ");
    localStringBuilder.append(uri);
    localStringBuilder.append(", ");
    localStringBuilder.append(Arrays.toString(httpBody));
    localStringBuilder.append(", ");
    localStringBuilder.append(absoluteStreamPosition);
    localStringBuilder.append(", ");
    localStringBuilder.append(position);
    localStringBuilder.append(", ");
    localStringBuilder.append(length);
    localStringBuilder.append(", ");
    localStringBuilder.append(key);
    localStringBuilder.append(", ");
    localStringBuilder.append(flags);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public DataSpec withUri(Uri paramUri)
  {
    return new DataSpec(paramUri, httpMethod, httpBody, absoluteStreamPosition, position, length, key, flags);
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface HttpMethod {}
}
