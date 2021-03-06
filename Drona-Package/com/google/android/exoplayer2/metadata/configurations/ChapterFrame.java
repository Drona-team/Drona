package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ChapterFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.ChapterFrame> CREATOR = new Parcelable.Creator()
  {
    public ChapterFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ChapterFrame(paramAnonymousParcel);
    }
    
    public ChapterFrame[] newArray(int paramAnonymousInt)
    {
      return new ChapterFrame[paramAnonymousInt];
    }
  };
  public static final String SQL_UPDATE_6_4 = "CHAP";
  public final String chapterId;
  public final long endOffset;
  public final int endTimeMs;
  public final long startOffset;
  public final int startTimeMs;
  private final Id3Frame[] subFrames;
  
  ChapterFrame(Parcel paramParcel)
  {
    super("CHAP");
    chapterId = ((String)Util.castNonNull(paramParcel.readString()));
    startTimeMs = paramParcel.readInt();
    endTimeMs = paramParcel.readInt();
    startOffset = paramParcel.readLong();
    endOffset = paramParcel.readLong();
    int j = paramParcel.readInt();
    subFrames = new Id3Frame[j];
    int i = 0;
    while (i < j)
    {
      subFrames[i] = ((Id3Frame)paramParcel.readParcelable(com.google.android.exoplayer2.metadata.id3.Id3Frame.class.getClassLoader()));
      i += 1;
    }
  }
  
  public ChapterFrame(String paramString, int paramInt1, int paramInt2, long paramLong1, long paramLong2, Id3Frame[] paramArrayOfId3Frame)
  {
    super("CHAP");
    chapterId = paramString;
    startTimeMs = paramInt1;
    endTimeMs = paramInt2;
    startOffset = paramLong1;
    endOffset = paramLong2;
    subFrames = paramArrayOfId3Frame;
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
      paramObject = (ChapterFrame)paramObject;
      return (startTimeMs == startTimeMs) && (endTimeMs == endTimeMs) && (startOffset == startOffset) && (endOffset == endOffset) && (Util.areEqual(chapterId, chapterId)) && (Arrays.equals(subFrames, subFrames));
    }
    return false;
  }
  
  public Id3Frame getSubFrame(int paramInt)
  {
    return subFrames[paramInt];
  }
  
  public int getSubFrameCount()
  {
    return subFrames.length;
  }
  
  public int hashCode()
  {
    int j = startTimeMs;
    int k = endTimeMs;
    int m = (int)startOffset;
    int n = (int)endOffset;
    int i;
    if (chapterId != null) {
      i = chapterId.hashCode();
    } else {
      i = 0;
    }
    return ((((527 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(chapterId);
    paramParcel.writeInt(startTimeMs);
    paramParcel.writeInt(endTimeMs);
    paramParcel.writeLong(startOffset);
    paramParcel.writeLong(endOffset);
    paramParcel.writeInt(subFrames.length);
    Id3Frame[] arrayOfId3Frame = subFrames;
    int i = arrayOfId3Frame.length;
    paramInt = 0;
    while (paramInt < i)
    {
      paramParcel.writeParcelable(arrayOfId3Frame[paramInt], 0);
      paramInt += 1;
    }
  }
}
