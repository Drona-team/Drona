package com.google.android.exoplayer2;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.ColorInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Format
  implements Parcelable
{
  public static final Parcelable.Creator<Format> CREATOR = new Parcelable.Creator()
  {
    public Format createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Format(paramAnonymousParcel);
    }
    
    public Format[] newArray(int paramAnonymousInt)
    {
      return new Format[paramAnonymousInt];
    }
  };
  public static final int NO_VALUE = -1;
  public static final long OFFSET_SAMPLE_RELATIVE = Long.MAX_VALUE;
  public final int accessibilityChannel;
  public final int bitrate;
  public final int channelCount;
  @Nullable
  public final String codecs;
  @Nullable
  public final ColorInfo colorInfo;
  @Nullable
  public final String containerMimeType;
  @Nullable
  public final com.google.android.exoplayer2.upgrade.DrmInitData drmInitData;
  public final int encoderDelay;
  public final int encoderPadding;
  public final float frameRate;
  private int hashCode;
  public final int height;
  public final List<byte[]> initializationData;
  @Nullable
  public final String label;
  @Nullable
  public final String language;
  public final int maxInputSize;
  @Nullable
  public final Metadata metadata;
  @Nullable
  public final String mimeType;
  public final int pcmEncoding;
  public final float pixelWidthHeightRatio;
  @Nullable
  public final byte[] projectionData;
  public final int rotationDegrees;
  @Nullable
  public final String sampleMimeType;
  public final int sampleRate;
  public final int selectionFlags;
  public final int stereoMode;
  public final long subsampleOffsetUs;
  public final int width;
  
  Format(Parcel paramParcel)
  {
    mimeType = paramParcel.readString();
    label = paramParcel.readString();
    containerMimeType = paramParcel.readString();
    sampleMimeType = paramParcel.readString();
    codecs = paramParcel.readString();
    bitrate = paramParcel.readInt();
    maxInputSize = paramParcel.readInt();
    width = paramParcel.readInt();
    height = paramParcel.readInt();
    frameRate = paramParcel.readFloat();
    rotationDegrees = paramParcel.readInt();
    pixelWidthHeightRatio = paramParcel.readFloat();
    byte[] arrayOfByte;
    if (Util.readBoolean(paramParcel)) {
      arrayOfByte = paramParcel.createByteArray();
    } else {
      arrayOfByte = null;
    }
    projectionData = arrayOfByte;
    stereoMode = paramParcel.readInt();
    colorInfo = ((ColorInfo)paramParcel.readParcelable(ColorInfo.class.getClassLoader()));
    channelCount = paramParcel.readInt();
    sampleRate = paramParcel.readInt();
    pcmEncoding = paramParcel.readInt();
    encoderDelay = paramParcel.readInt();
    encoderPadding = paramParcel.readInt();
    selectionFlags = paramParcel.readInt();
    language = paramParcel.readString();
    accessibilityChannel = paramParcel.readInt();
    subsampleOffsetUs = paramParcel.readLong();
    int j = paramParcel.readInt();
    initializationData = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      initializationData.add(paramParcel.createByteArray());
      i += 1;
    }
    drmInitData = ((com.google.android.exoplayer2.upgrade.DrmInitData)paramParcel.readParcelable(com.google.android.exoplayer2.drm.DrmInitData.class.getClassLoader()));
    metadata = ((Metadata)paramParcel.readParcelable(Metadata.class.getClassLoader()));
  }
  
  Format(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, int paramInt5, float paramFloat2, byte[] paramArrayOfByte, int paramInt6, ColorInfo paramColorInfo, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, String paramString6, int paramInt13, long paramLong, List paramList, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData, Metadata paramMetadata)
  {
    mimeType = paramString1;
    label = paramString2;
    containerMimeType = paramString3;
    sampleMimeType = paramString4;
    codecs = paramString5;
    bitrate = paramInt1;
    maxInputSize = paramInt2;
    width = paramInt3;
    height = paramInt4;
    frameRate = paramFloat1;
    paramInt1 = paramInt5;
    if (paramInt5 == -1) {
      paramInt1 = 0;
    }
    rotationDegrees = paramInt1;
    if (paramFloat2 == -1.0F) {
      paramFloat2 = 1.0F;
    }
    pixelWidthHeightRatio = paramFloat2;
    projectionData = paramArrayOfByte;
    stereoMode = paramInt6;
    colorInfo = paramColorInfo;
    channelCount = paramInt7;
    sampleRate = paramInt8;
    pcmEncoding = paramInt9;
    paramInt1 = paramInt10;
    if (paramInt10 == -1) {
      paramInt1 = 0;
    }
    encoderDelay = paramInt1;
    paramInt1 = paramInt11;
    if (paramInt11 == -1) {
      paramInt1 = 0;
    }
    encoderPadding = paramInt1;
    selectionFlags = paramInt12;
    language = paramString6;
    accessibilityChannel = paramInt13;
    subsampleOffsetUs = paramLong;
    paramString1 = paramList;
    if (paramList == null) {
      paramString1 = Collections.emptyList();
    }
    initializationData = paramString1;
    drmInitData = paramDrmInitData;
    metadata = paramMetadata;
  }
  
  public static Format createAudioContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, List paramList, int paramInt4, String paramString5)
  {
    return createAudioContainerFormat(paramString1, null, paramString2, paramString3, paramString4, paramInt1, paramInt2, paramInt3, paramList, paramInt4, paramString5);
  }
  
  public static Format createAudioContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, int paramInt3, List paramList, int paramInt4, String paramString6)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramString5, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, paramInt2, paramInt3, -1, -1, -1, paramInt4, paramString6, -1, Long.MAX_VALUE, paramList, null, null);
  }
  
  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, List paramList, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData, int paramInt8, String paramString4, Metadata paramMetadata)
  {
    return new Format(paramString1, null, null, paramString2, paramString3, paramInt1, paramInt2, -1, -1, -1.0F, -1, -1.0F, null, -1, null, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramString4, -1, Long.MAX_VALUE, paramList, paramDrmInitData, paramMetadata);
  }
  
  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List paramList, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData, int paramInt6, String paramString4)
  {
    return createAudioSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, -1, -1, paramList, paramDrmInitData, paramInt6, paramString4, null);
  }
  
  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, List paramList, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData, int paramInt5, String paramString4)
  {
    return createAudioSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, -1, paramList, paramDrmInitData, paramInt5, paramString4);
  }
  
  public static Format createContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5)
  {
    return createContainerFormat(paramString1, null, paramString2, paramString3, paramString4, paramInt1, paramInt2, paramString5);
  }
  
  public static Format createContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, String paramString6)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramString5, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString6, -1, Long.MAX_VALUE, null, null, null);
  }
  
  public static Format createImageSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, List paramList, String paramString4, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, null, paramString2, paramString3, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString4, -1, Long.MAX_VALUE, paramList, paramDrmInitData, null);
  }
  
  public static Format createSampleFormat(String paramString1, String paramString2, long paramLong)
  {
    return new Format(paramString1, null, null, paramString2, null, -1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, paramLong, null, null, null);
  }
  
  public static Format createSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, null, paramString2, paramString3, paramInt, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, null, paramDrmInitData, null);
  }
  
  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5)
  {
    return createTextContainerFormat(paramString1, null, paramString2, paramString3, paramString4, paramInt1, paramInt2, paramString5);
  }
  
  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, String paramString6)
  {
    return createTextContainerFormat(paramString1, paramString2, paramString3, paramString4, paramString5, paramInt1, paramInt2, paramString6, -1);
  }
  
  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, String paramString6, int paramInt3)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramString5, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString6, paramInt3, Long.MAX_VALUE, null, null, null);
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, int paramInt, String paramString3)
  {
    return createTextSampleFormat(paramString1, paramString2, paramInt, paramString3, null);
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, int paramInt, String paramString3, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return createTextSampleFormat(paramString1, paramString2, null, -1, paramInt, paramString3, -1, paramDrmInitData, Long.MAX_VALUE, Collections.emptyList());
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, int paramInt3, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, paramInt3, paramDrmInitData, Long.MAX_VALUE, Collections.emptyList());
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, int paramInt3, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData, long paramLong, List paramList)
  {
    return new Format(paramString1, null, null, paramString2, paramString3, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString4, paramInt3, paramLong, paramList, paramDrmInitData, null);
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData, long paramLong)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, -1, paramDrmInitData, paramLong, Collections.emptyList());
  }
  
  public static Format createVideoContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, float paramFloat, List paramList, int paramInt4)
  {
    return createVideoContainerFormat(paramString1, null, paramString2, paramString3, paramString4, paramInt1, paramInt2, paramInt3, paramFloat, paramList, paramInt4);
  }
  
  public static Format createVideoContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, int paramInt3, float paramFloat, List paramList, int paramInt4)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramString5, paramInt1, -1, paramInt2, paramInt3, paramFloat, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt4, null, -1, Long.MAX_VALUE, paramList, null, null);
  }
  
  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, List paramList, int paramInt5, float paramFloat2, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return createVideoSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramList, paramInt5, paramFloat2, null, -1, null, paramDrmInitData);
  }
  
  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, List paramList, int paramInt5, float paramFloat2, byte[] paramArrayOfByte, int paramInt6, ColorInfo paramColorInfo, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, null, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramInt5, paramFloat2, paramArrayOfByte, paramInt6, paramColorInfo, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, paramList, paramDrmInitData, null);
  }
  
  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, List paramList, com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return createVideoSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat, paramList, -1, -1.0F, paramDrmInitData);
  }
  
  public static String toLogString(Format paramFormat)
  {
    if (paramFormat == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("id=");
    localStringBuilder.append(mimeType);
    localStringBuilder.append(", mimeType=");
    localStringBuilder.append(sampleMimeType);
    if (bitrate != -1)
    {
      localStringBuilder.append(", bitrate=");
      localStringBuilder.append(bitrate);
    }
    if (codecs != null)
    {
      localStringBuilder.append(", codecs=");
      localStringBuilder.append(codecs);
    }
    if ((width != -1) && (height != -1))
    {
      localStringBuilder.append(", res=");
      localStringBuilder.append(width);
      localStringBuilder.append("x");
      localStringBuilder.append(height);
    }
    if (frameRate != -1.0F)
    {
      localStringBuilder.append(", fps=");
      localStringBuilder.append(frameRate);
    }
    if (channelCount != -1)
    {
      localStringBuilder.append(", channels=");
      localStringBuilder.append(channelCount);
    }
    if (sampleRate != -1)
    {
      localStringBuilder.append(", sample_rate=");
      localStringBuilder.append(sampleRate);
    }
    if (language != null)
    {
      localStringBuilder.append(", language=");
      localStringBuilder.append(language);
    }
    if (label != null)
    {
      localStringBuilder.append(", label=");
      localStringBuilder.append(label);
    }
    return localStringBuilder.toString();
  }
  
  public Format copyWithContainerInfo(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString5)
  {
    return new Format(paramString1, paramString2, containerMimeType, paramString3, paramString4, paramInt1, maxInputSize, paramInt2, paramInt3, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, paramInt4, paramString5, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, metadata);
  }
  
  public Format copyWithDrmInitData(com.google.android.exoplayer2.upgrade.DrmInitData paramDrmInitData)
  {
    return new Format(mimeType, label, containerMimeType, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, paramDrmInitData, metadata);
  }
  
  public Format copyWithGaplessInfo(int paramInt1, int paramInt2)
  {
    return new Format(mimeType, label, containerMimeType, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, paramInt1, paramInt2, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, metadata);
  }
  
  public Format copyWithManifestFormatInfo(Format paramFormat)
  {
    if (this == paramFormat) {
      return this;
    }
    int j = MimeTypes.getTrackType(sampleMimeType);
    String str4 = mimeType;
    if (label != null) {}
    for (String str1 = label;; str1 = label) {
      break;
    }
    Object localObject2 = language;
    Object localObject1;
    if (j != 3)
    {
      localObject1 = localObject2;
      if (j != 1) {}
    }
    else
    {
      localObject1 = localObject2;
      if (language != null) {
        localObject1 = language;
      }
    }
    if (bitrate == -1) {}
    for (int i = bitrate;; i = bitrate) {
      break;
    }
    String str2 = codecs;
    localObject2 = str2;
    if (str2 == null)
    {
      String str3 = Util.getCodecsOfType(codecs, j);
      localObject2 = str2;
      if (Util.splitCodecs(str3).length == 1) {
        localObject2 = str3;
      }
    }
    float f = frameRate;
    if ((f == -1.0F) && (j == 2)) {
      f = frameRate;
    }
    j = selectionFlags;
    int k = selectionFlags;
    paramFormat = com.google.android.exoplayer2.upgrade.DrmInitData.createSessionCreationData(drmInitData, drmInitData);
    return new Format(str4, str1, containerMimeType, sampleMimeType, (String)localObject2, i, maxInputSize, width, height, f, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, j | k, (String)localObject1, accessibilityChannel, subsampleOffsetUs, initializationData, paramFormat, metadata);
  }
  
  public Format copyWithMaxInputSize(int paramInt)
  {
    return new Format(mimeType, label, containerMimeType, sampleMimeType, codecs, bitrate, paramInt, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, metadata);
  }
  
  public Format copyWithMetadata(Metadata paramMetadata)
  {
    return new Format(mimeType, label, containerMimeType, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, paramMetadata);
  }
  
  public Format copyWithRotationDegrees(int paramInt)
  {
    return new Format(mimeType, label, containerMimeType, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, paramInt, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, metadata);
  }
  
  public Format copyWithSubsampleOffsetUs(long paramLong)
  {
    return new Format(mimeType, label, containerMimeType, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, accessibilityChannel, paramLong, initializationData, drmInitData, metadata);
  }
  
  public int describeContents()
  {
    return 0;
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
      paramObject = (Format)paramObject;
      if ((hashCode != 0) && (hashCode != 0) && (hashCode != hashCode)) {
        return false;
      }
      return (bitrate == bitrate) && (maxInputSize == maxInputSize) && (width == width) && (height == height) && (Float.compare(frameRate, frameRate) == 0) && (rotationDegrees == rotationDegrees) && (Float.compare(pixelWidthHeightRatio, pixelWidthHeightRatio) == 0) && (stereoMode == stereoMode) && (channelCount == channelCount) && (sampleRate == sampleRate) && (pcmEncoding == pcmEncoding) && (encoderDelay == encoderDelay) && (encoderPadding == encoderPadding) && (subsampleOffsetUs == subsampleOffsetUs) && (selectionFlags == selectionFlags) && (Util.areEqual(mimeType, mimeType)) && (Util.areEqual(label, label)) && (Util.areEqual(language, language)) && (accessibilityChannel == accessibilityChannel) && (Util.areEqual(containerMimeType, containerMimeType)) && (Util.areEqual(sampleMimeType, sampleMimeType)) && (Util.areEqual(codecs, codecs)) && (Util.areEqual(drmInitData, drmInitData)) && (Util.areEqual(metadata, metadata)) && (Util.areEqual(colorInfo, colorInfo)) && (Arrays.equals(projectionData, projectionData)) && (initializationDataEquals(paramObject));
    }
    return false;
  }
  
  public int getPixelCount()
  {
    if (width != -1)
    {
      if (height == -1) {
        return -1;
      }
      int i = width;
      return height * i;
    }
    return -1;
  }
  
  public int hashCode()
  {
    if (hashCode == 0)
    {
      String str = mimeType;
      int i3 = 0;
      int i;
      if (str == null) {
        i = 0;
      } else {
        i = mimeType.hashCode();
      }
      int j;
      if (containerMimeType == null) {
        j = 0;
      } else {
        j = containerMimeType.hashCode();
      }
      int k;
      if (sampleMimeType == null) {
        k = 0;
      } else {
        k = sampleMimeType.hashCode();
      }
      int m;
      if (codecs == null) {
        m = 0;
      } else {
        m = codecs.hashCode();
      }
      int i4 = bitrate;
      int i5 = width;
      int i6 = height;
      int i7 = channelCount;
      int i8 = sampleRate;
      int n;
      if (language == null) {
        n = 0;
      } else {
        n = language.hashCode();
      }
      int i9 = accessibilityChannel;
      int i1;
      if (drmInitData == null) {
        i1 = 0;
      } else {
        i1 = drmInitData.hashCode();
      }
      int i2;
      if (metadata == null) {
        i2 = 0;
      } else {
        i2 = metadata.hashCode();
      }
      if (label != null) {
        i3 = label.hashCode();
      }
      hashCode = ((((((((((((((((((((((((527 + i) * 31 + j) * 31 + k) * 31 + m) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + n) * 31 + i9) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + maxInputSize) * 31 + (int)subsampleOffsetUs) * 31 + Float.floatToIntBits(frameRate)) * 31 + Float.floatToIntBits(pixelWidthHeightRatio)) * 31 + rotationDegrees) * 31 + stereoMode) * 31 + pcmEncoding) * 31 + encoderDelay) * 31 + encoderPadding) * 31 + selectionFlags);
    }
    return hashCode;
  }
  
  public boolean initializationDataEquals(Format paramFormat)
  {
    if (initializationData.size() != initializationData.size()) {
      return false;
    }
    int i = 0;
    while (i < initializationData.size())
    {
      if (!Arrays.equals((byte[])initializationData.get(i), (byte[])initializationData.get(i))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Format(");
    localStringBuilder.append(mimeType);
    localStringBuilder.append(", ");
    localStringBuilder.append(label);
    localStringBuilder.append(", ");
    localStringBuilder.append(containerMimeType);
    localStringBuilder.append(", ");
    localStringBuilder.append(sampleMimeType);
    localStringBuilder.append(", ");
    localStringBuilder.append(codecs);
    localStringBuilder.append(", ");
    localStringBuilder.append(bitrate);
    localStringBuilder.append(", ");
    localStringBuilder.append(language);
    localStringBuilder.append(", [");
    localStringBuilder.append(width);
    localStringBuilder.append(", ");
    localStringBuilder.append(height);
    localStringBuilder.append(", ");
    localStringBuilder.append(frameRate);
    localStringBuilder.append("], [");
    localStringBuilder.append(channelCount);
    localStringBuilder.append(", ");
    localStringBuilder.append(sampleRate);
    localStringBuilder.append("])");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(mimeType);
    paramParcel.writeString(label);
    paramParcel.writeString(containerMimeType);
    paramParcel.writeString(sampleMimeType);
    paramParcel.writeString(codecs);
    paramParcel.writeInt(bitrate);
    paramParcel.writeInt(maxInputSize);
    paramParcel.writeInt(width);
    paramParcel.writeInt(height);
    paramParcel.writeFloat(frameRate);
    paramParcel.writeInt(rotationDegrees);
    paramParcel.writeFloat(pixelWidthHeightRatio);
    boolean bool;
    if (projectionData != null) {
      bool = true;
    } else {
      bool = false;
    }
    Util.writeBoolean(paramParcel, bool);
    if (projectionData != null) {
      paramParcel.writeByteArray(projectionData);
    }
    paramParcel.writeInt(stereoMode);
    paramParcel.writeParcelable(colorInfo, paramInt);
    paramParcel.writeInt(channelCount);
    paramParcel.writeInt(sampleRate);
    paramParcel.writeInt(pcmEncoding);
    paramParcel.writeInt(encoderDelay);
    paramParcel.writeInt(encoderPadding);
    paramParcel.writeInt(selectionFlags);
    paramParcel.writeString(language);
    paramParcel.writeInt(accessibilityChannel);
    paramParcel.writeLong(subsampleOffsetUs);
    int i = initializationData.size();
    paramParcel.writeInt(i);
    paramInt = 0;
    while (paramInt < i)
    {
      paramParcel.writeByteArray((byte[])initializationData.get(paramInt));
      paramInt += 1;
    }
    paramParcel.writeParcelable(drmInitData, 0);
    paramParcel.writeParcelable(metadata, 0);
  }
}
