package com.google.android.exoplayer2.extractor.ClickListeners;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Track
{
  public static final int TRANSFORMATION_CEA608_CDAT = 1;
  public static final int TRANSFORMATION_NONE = 0;
  public final long durationUs;
  @Nullable
  public final long[] editListDurations;
  @Nullable
  public final long[] editListMediaTimes;
  public final Format format;
  public final int id;
  public final long movieTimescale;
  public final int nalUnitLengthFieldLength;
  @Nullable
  private final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
  public final int sampleTransformation;
  public final long timescale;
  public final int type;
  
  public Track(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, Format paramFormat, int paramInt3, TrackEncryptionBox[] paramArrayOfTrackEncryptionBox, int paramInt4, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    id = paramInt1;
    type = paramInt2;
    timescale = paramLong1;
    movieTimescale = paramLong2;
    durationUs = paramLong3;
    format = paramFormat;
    sampleTransformation = paramInt3;
    sampleDescriptionEncryptionBoxes = paramArrayOfTrackEncryptionBox;
    nalUnitLengthFieldLength = paramInt4;
    editListDurations = paramArrayOfLong1;
    editListMediaTimes = paramArrayOfLong2;
  }
  
  public TrackEncryptionBox getSampleDescriptionEncryptionBox(int paramInt)
  {
    if (sampleDescriptionEncryptionBoxes == null) {
      return null;
    }
    return sampleDescriptionEncryptionBoxes[paramInt];
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Transformation {}
}
