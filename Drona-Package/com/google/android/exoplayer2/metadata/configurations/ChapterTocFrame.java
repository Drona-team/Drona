package com.google.android.exoplayer2.metadata.configurations;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ChapterTocFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.metadata.id3.ChapterTocFrame> CREATOR = new Parcelable.Creator()
  {
    public ChapterTocFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ChapterTocFrame(paramAnonymousParcel);
    }
    
    public ChapterTocFrame[] newArray(int paramAnonymousInt)
    {
      return new ChapterTocFrame[paramAnonymousInt];
    }
  };
  public static final String SQL_UPDATE_6_4 = "CTOC";
  public final String[] children;
  public final String elementId;
  public final boolean isOrdered;
  public final boolean isRoot;
  private final Id3Frame[] subFrames;
  
  ChapterTocFrame(Parcel paramParcel)
  {
    super("CTOC");
    elementId = ((String)Util.castNonNull(paramParcel.readString()));
    int j = paramParcel.readByte();
    int i = 0;
    boolean bool2 = true;
    boolean bool1;
    if (j != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    isRoot = bool1;
    if (paramParcel.readByte() != 0) {
      bool1 = bool2;
    } else {
      bool1 = false;
    }
    isOrdered = bool1;
    children = paramParcel.createStringArray();
    j = paramParcel.readInt();
    subFrames = new Id3Frame[j];
    while (i < j)
    {
      subFrames[i] = ((Id3Frame)paramParcel.readParcelable(com.google.android.exoplayer2.metadata.id3.Id3Frame.class.getClassLoader()));
      i += 1;
    }
  }
  
  public ChapterTocFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString, Id3Frame[] paramArrayOfId3Frame)
  {
    super("CTOC");
    elementId = paramString;
    isRoot = paramBoolean1;
    isOrdered = paramBoolean2;
    children = paramArrayOfString;
    subFrames = paramArrayOfId3Frame;
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
      paramObject = (ChapterTocFrame)paramObject;
      return (isRoot == isRoot) && (isOrdered == isOrdered) && (Util.areEqual(elementId, elementId)) && (Arrays.equals(children, children)) && (Arrays.equals(subFrames, subFrames));
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
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(elementId);
    paramParcel.writeByte((byte)isRoot);
    paramParcel.writeByte((byte)isOrdered);
    paramParcel.writeStringArray(children);
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
