package com.bumptech.glide.signature;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.Key;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class MediaStoreSignature
  implements Key
{
  private final long dateModified;
  @NonNull
  private final String mimeType;
  private final int orientation;
  
  public MediaStoreSignature(String paramString, long paramLong, int paramInt)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    mimeType = str;
    dateModified = paramLong;
    orientation = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (MediaStoreSignature)paramObject;
      if (dateModified != dateModified) {
        return false;
      }
      if (orientation != orientation) {
        return false;
      }
      return mimeType.equals(mimeType);
    }
    return false;
  }
  
  public int hashCode()
  {
    return (mimeType.hashCode() * 31 + (int)(dateModified ^ dateModified >>> 32)) * 31 + orientation;
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(ByteBuffer.allocate(12).putLong(dateModified).putInt(orientation).array());
    paramMessageDigest.update(mimeType.getBytes(Key.CHARSET));
  }
}
