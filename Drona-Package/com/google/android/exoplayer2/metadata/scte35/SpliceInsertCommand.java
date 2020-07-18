package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpliceInsertCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceInsertCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceInsertCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpliceInsertCommand(paramAnonymousParcel, null);
    }
    
    public SpliceInsertCommand[] newArray(int paramAnonymousInt)
    {
      return new SpliceInsertCommand[paramAnonymousInt];
    }
  };
  public final boolean autoReturn;
  public final int availNum;
  public final int availsExpected;
  public final long breakDurationUs;
  public final List<ComponentSplice> componentSpliceList;
  public final boolean outOfNetworkIndicator;
  public final boolean programSpliceFlag;
  public final long programSplicePlaybackPositionUs;
  public final long programSplicePts;
  public final boolean spliceEventCancelIndicator;
  public final long spliceEventId;
  public final boolean spliceImmediateFlag;
  public final int uniqueProgramId;
  
  private SpliceInsertCommand(long paramLong1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, long paramLong2, long paramLong3, List paramList, boolean paramBoolean5, long paramLong4, int paramInt1, int paramInt2, int paramInt3)
  {
    spliceEventId = paramLong1;
    spliceEventCancelIndicator = paramBoolean1;
    outOfNetworkIndicator = paramBoolean2;
    programSpliceFlag = paramBoolean3;
    spliceImmediateFlag = paramBoolean4;
    programSplicePts = paramLong2;
    programSplicePlaybackPositionUs = paramLong3;
    componentSpliceList = Collections.unmodifiableList(paramList);
    autoReturn = paramBoolean5;
    breakDurationUs = paramLong4;
    uniqueProgramId = paramInt1;
    availNum = paramInt2;
    availsExpected = paramInt3;
  }
  
  private SpliceInsertCommand(Parcel paramParcel)
  {
    spliceEventId = paramParcel.readLong();
    int i = paramParcel.readByte();
    boolean bool2 = false;
    if (i == 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    spliceEventCancelIndicator = bool1;
    if (paramParcel.readByte() == 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    outOfNetworkIndicator = bool1;
    if (paramParcel.readByte() == 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    programSpliceFlag = bool1;
    if (paramParcel.readByte() == 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    spliceImmediateFlag = bool1;
    programSplicePts = paramParcel.readLong();
    programSplicePlaybackPositionUs = paramParcel.readLong();
    int j = paramParcel.readInt();
    ArrayList localArrayList = new ArrayList(j);
    i = 0;
    while (i < j)
    {
      localArrayList.add(ComponentSplice.createFromParcel(paramParcel));
      i += 1;
    }
    componentSpliceList = Collections.unmodifiableList(localArrayList);
    boolean bool1 = bool2;
    if (paramParcel.readByte() == 1) {
      bool1 = true;
    }
    autoReturn = bool1;
    breakDurationUs = paramParcel.readLong();
    uniqueProgramId = paramParcel.readInt();
    availNum = paramParcel.readInt();
    availsExpected = paramParcel.readInt();
  }
  
  static SpliceInsertCommand parseFromSection(ParsableByteArray paramParsableByteArray, long paramLong, TimestampAdjuster paramTimestampAdjuster)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a26 = a25\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(spliceEventId);
    paramParcel.writeByte((byte)spliceEventCancelIndicator);
    paramParcel.writeByte((byte)outOfNetworkIndicator);
    paramParcel.writeByte((byte)programSpliceFlag);
    paramParcel.writeByte((byte)spliceImmediateFlag);
    paramParcel.writeLong(programSplicePts);
    paramParcel.writeLong(programSplicePlaybackPositionUs);
    int i = componentSpliceList.size();
    paramParcel.writeInt(i);
    paramInt = 0;
    while (paramInt < i)
    {
      ((ComponentSplice)componentSpliceList.get(paramInt)).writeToParcel(paramParcel);
      paramInt += 1;
    }
    paramParcel.writeByte((byte)autoReturn);
    paramParcel.writeLong(breakDurationUs);
    paramParcel.writeInt(uniqueProgramId);
    paramParcel.writeInt(availNum);
    paramParcel.writeInt(availsExpected);
  }
  
  public static final class ComponentSplice
  {
    public final long componentSplicePlaybackPositionUs;
    public final long componentSplicePts;
    public final int componentTag;
    
    private ComponentSplice(int paramInt, long paramLong1, long paramLong2)
    {
      componentTag = paramInt;
      componentSplicePts = paramLong1;
      componentSplicePlaybackPositionUs = paramLong2;
    }
    
    public static ComponentSplice createFromParcel(Parcel paramParcel)
    {
      return new ComponentSplice(paramParcel.readInt(), paramParcel.readLong(), paramParcel.readLong());
    }
    
    public void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(componentTag);
      paramParcel.writeLong(componentSplicePts);
      paramParcel.writeLong(componentSplicePlaybackPositionUs);
    }
  }
}
