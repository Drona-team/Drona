package com.facebook.imagepipeline.common;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.util.HashCodeUtil;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class BytesRange
{
  public static final int TO_END_OF_CONTENT = Integer.MAX_VALUE;
  @Nullable
  private static Pattern sHeaderParsingRegEx;
  public final int from;
  public final int type;
  
  public BytesRange(int paramInt1, int paramInt2)
  {
    from = paramInt1;
    type = paramInt2;
  }
  
  public static BytesRange from(int paramInt)
  {
    boolean bool;
    if (paramInt >= 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    return new BytesRange(paramInt, Integer.MAX_VALUE);
  }
  
  public static BytesRange fromContentRangeHeader(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      return null;
    }
    if (sHeaderParsingRegEx == null) {
      sHeaderParsingRegEx = Pattern.compile("[-/ ]");
    }
    Object localObject = sHeaderParsingRegEx;
    try
    {
      localObject = ((Pattern)localObject).split(paramString);
      boolean bool;
      if (localObject.length == 4) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      String str = localObject[0];
      Preconditions.checkArgument(str.equals("bytes"));
      str = localObject[1];
      int i = Integer.parseInt(str);
      str = localObject[2];
      int j = Integer.parseInt(str);
      localObject = localObject[3];
      int k = Integer.parseInt((String)localObject);
      if (j > i) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      if (k > j) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      if (j < k - 1)
      {
        localObject = new BytesRange(i, j);
        return localObject;
      }
      localObject = new BytesRange(i, Integer.MAX_VALUE);
      return localObject;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IllegalArgumentException(String.format(null, "Invalid Content-Range header value: \"%s\"", new Object[] { paramString }), localIllegalArgumentException);
    }
  }
  
  public static BytesRange toMax(int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    return new BytesRange(0, paramInt);
  }
  
  private static String valueOrEmpty(int paramInt)
  {
    if (paramInt == Integer.MAX_VALUE) {
      return "";
    }
    return Integer.toString(paramInt);
  }
  
  public boolean contains(BytesRange paramBytesRange)
  {
    if (paramBytesRange == null) {
      return false;
    }
    return (from <= from) && (type >= type);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof BytesRange)) {
      return false;
    }
    paramObject = (BytesRange)paramObject;
    return (from == from) && (type == type);
  }
  
  public int hashCode()
  {
    return HashCodeUtil.hashCode(from, type);
  }
  
  public String toHttpRangeHeaderValue()
  {
    return String.format(null, "bytes=%s-%s", new Object[] { valueOrEmpty(from), valueOrEmpty(type) });
  }
  
  public String toString()
  {
    return String.format(null, "%s-%s", new Object[] { valueOrEmpty(from), valueOrEmpty(type) });
  }
}
