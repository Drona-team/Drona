package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaFormat;
import com.google.android.exoplayer2.video.ColorInfo;
import java.nio.ByteBuffer;
import java.util.List;

@TargetApi(16)
public final class MediaFormatUtil
{
  private MediaFormatUtil() {}
  
  public static void maybeSetByteBuffer(MediaFormat paramMediaFormat, String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {
      paramMediaFormat.setByteBuffer(paramString, ByteBuffer.wrap(paramArrayOfByte));
    }
  }
  
  public static void maybeSetColorInfo(MediaFormat paramMediaFormat, ColorInfo paramColorInfo)
  {
    if (paramColorInfo != null)
    {
      maybeSetInteger(paramMediaFormat, "color-transfer", colorTransfer);
      maybeSetInteger(paramMediaFormat, "color-standard", colorSpace);
      maybeSetInteger(paramMediaFormat, "color-range", colorRange);
      maybeSetByteBuffer(paramMediaFormat, "hdr-static-info", hdrStaticInfo);
    }
  }
  
  public static void maybeSetFloat(MediaFormat paramMediaFormat, String paramString, float paramFloat)
  {
    if (paramFloat != -1.0F) {
      paramMediaFormat.setFloat(paramString, paramFloat);
    }
  }
  
  public static void maybeSetInteger(MediaFormat paramMediaFormat, String paramString, int paramInt)
  {
    if (paramInt != -1) {
      paramMediaFormat.setInteger(paramString, paramInt);
    }
  }
  
  public static void setCsdBuffers(MediaFormat paramMediaFormat, List paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("csd-");
      localStringBuilder.append(i);
      paramMediaFormat.setByteBuffer(localStringBuilder.toString(), ByteBuffer.wrap((byte[])paramList.get(i)));
      i += 1;
    }
  }
  
  public static void setString(MediaFormat paramMediaFormat, String paramString1, String paramString2)
  {
    paramMediaFormat.setString(paramString1, paramString2);
  }
}
