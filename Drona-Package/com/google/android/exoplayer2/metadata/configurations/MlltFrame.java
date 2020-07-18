package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class MlltFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.MlltFrame> CREATOR = new Parcelable.Creator()
  {
    public MlltFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MlltFrame(paramAnonymousParcel);
    }
    
    public MlltFrame[] newArray(int paramAnonymousInt)
    {
      return new MlltFrame[paramAnonymousInt];
    }
  };
  public static final String PAGE_KEY = "MLLT";
  public final int bytesBetweenReference;
  public final int[] bytesDeviations;
  public final int millisecondsBetweenReference;
  public final int[] millisecondsDeviations;
  public final int mpegFramesBetweenReference;
  
  public MlltFrame(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super("MLLT");
    mpegFramesBetweenReference = paramInt1;
    bytesBetweenReference = paramInt2;
    millisecondsBetweenReference = paramInt3;
    bytesDeviations = paramArrayOfInt1;
    millisecondsDeviations = paramArrayOfInt2;
  }
  
  MlltFrame(Parcel paramParcel)
  {
    super("MLLT");
    mpegFramesBetweenReference = paramParcel.readInt();
    bytesBetweenReference = paramParcel.readInt();
    millisecondsBetweenReference = paramParcel.readInt();
    bytesDeviations = paramParcel.createIntArray();
    millisecondsDeviations = paramParcel.createIntArray();
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
      paramObject = (MlltFrame)paramObject;
      return (mpegFramesBetweenReference == mpegFramesBetweenReference) && (bytesBetweenReference == bytesBetweenReference) && (millisecondsBetweenReference == millisecondsBetweenReference) && (Arrays.equals(bytesDeviations, bytesDeviations)) && (Arrays.equals(millisecondsDeviations, millisecondsDeviations));
    }
    return false;
  }
  
  public int hashCode()
  {
    return ((((527 + mpegFramesBetweenReference) * 31 + bytesBetweenReference) * 31 + millisecondsBetweenReference) * 31 + Arrays.hashCode(bytesDeviations)) * 31 + Arrays.hashCode(millisecondsDeviations);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(mpegFramesBetweenReference);
    paramParcel.writeInt(bytesBetweenReference);
    paramParcel.writeInt(millisecondsBetweenReference);
    paramParcel.writeIntArray(bytesDeviations);
    paramParcel.writeIntArray(millisecondsDeviations);
  }
}
