package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpliceScheduleCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceScheduleCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceScheduleCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpliceScheduleCommand(paramAnonymousParcel, null);
    }
    
    public SpliceScheduleCommand[] newArray(int paramAnonymousInt)
    {
      return new SpliceScheduleCommand[paramAnonymousInt];
    }
  };
  public final List<Event> events;
  
  private SpliceScheduleCommand(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      localArrayList.add(Event.createFromParcel(paramParcel));
      i += 1;
    }
    events = Collections.unmodifiableList(localArrayList);
  }
  
  private SpliceScheduleCommand(List paramList)
  {
    events = Collections.unmodifiableList(paramList);
  }
  
  static SpliceScheduleCommand parseFromSection(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.readUnsignedByte();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      localArrayList.add(Event.parseFromSection(paramParsableByteArray));
      i += 1;
    }
    return new SpliceScheduleCommand(localArrayList);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = events.size();
    paramParcel.writeInt(i);
    paramInt = 0;
    while (paramInt < i)
    {
      ((Event)events.get(paramInt)).writeToParcel(paramParcel);
      paramInt += 1;
    }
  }
  
  public static final class ComponentSplice
  {
    public final int componentTag;
    public final long utcSpliceTime;
    
    private ComponentSplice(int paramInt, long paramLong)
    {
      componentTag = paramInt;
      utcSpliceTime = paramLong;
    }
    
    private static ComponentSplice createFromParcel(Parcel paramParcel)
    {
      return new ComponentSplice(paramParcel.readInt(), paramParcel.readLong());
    }
    
    private void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(componentTag);
      paramParcel.writeLong(utcSpliceTime);
    }
  }
  
  public static final class Event
  {
    public final boolean autoReturn;
    public final int availNum;
    public final int availsExpected;
    public final long breakDurationUs;
    public final List<SpliceScheduleCommand.ComponentSplice> componentSpliceList;
    public final boolean outOfNetworkIndicator;
    public final boolean programSpliceFlag;
    public final boolean spliceEventCancelIndicator;
    public final long spliceEventId;
    public final int uniqueProgramId;
    public final long utcSpliceTime;
    
    private Event(long paramLong1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, List paramList, long paramLong2, boolean paramBoolean4, long paramLong3, int paramInt1, int paramInt2, int paramInt3)
    {
      spliceEventId = paramLong1;
      spliceEventCancelIndicator = paramBoolean1;
      outOfNetworkIndicator = paramBoolean2;
      programSpliceFlag = paramBoolean3;
      componentSpliceList = Collections.unmodifiableList(paramList);
      utcSpliceTime = paramLong2;
      autoReturn = paramBoolean4;
      breakDurationUs = paramLong3;
      uniqueProgramId = paramInt1;
      availNum = paramInt2;
      availsExpected = paramInt3;
    }
    
    private Event(Parcel paramParcel)
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
      int j = paramParcel.readInt();
      ArrayList localArrayList = new ArrayList(j);
      i = 0;
      while (i < j)
      {
        localArrayList.add(SpliceScheduleCommand.ComponentSplice.createFromParcel(paramParcel));
        i += 1;
      }
      componentSpliceList = Collections.unmodifiableList(localArrayList);
      utcSpliceTime = paramParcel.readLong();
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
    
    private static Event createFromParcel(Parcel paramParcel)
    {
      return new Event(paramParcel);
    }
    
    private static Event parseFromSection(ParsableByteArray paramParsableByteArray)
    {
      long l3 = paramParsableByteArray.readUnsignedInt();
      boolean bool4;
      if ((paramParsableByteArray.readUnsignedByte() & 0x80) != 0) {
        bool4 = true;
      } else {
        bool4 = false;
      }
      Object localObject = new ArrayList();
      int i;
      boolean bool1;
      boolean bool2;
      long l1;
      int k;
      int j;
      long l2;
      boolean bool3;
      if (!bool4)
      {
        i = paramParsableByteArray.readUnsignedByte();
        if ((i & 0x80) != 0) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        if ((i & 0x40) != 0) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        if ((i & 0x20) != 0) {
          i = 1;
        } else {
          i = 0;
        }
        if (bool2) {
          l1 = paramParsableByteArray.readUnsignedInt();
        } else {
          l1 = -9223372036854775807L;
        }
        if (!bool2)
        {
          k = paramParsableByteArray.readUnsignedByte();
          ArrayList localArrayList = new ArrayList(k);
          j = 0;
          for (;;)
          {
            localObject = localArrayList;
            if (j >= k) {
              break;
            }
            localArrayList.add(new SpliceScheduleCommand.ComponentSplice(paramParsableByteArray.readUnsignedByte(), paramParsableByteArray.readUnsignedInt(), null));
            j += 1;
          }
        }
        if (i != 0)
        {
          l2 = paramParsableByteArray.readUnsignedByte();
          if ((0x80 & l2) != 0L) {
            bool3 = true;
          } else {
            bool3 = false;
          }
          l2 = ((l2 & 1L) << 32 | paramParsableByteArray.readUnsignedInt()) * 1000L / 90L;
        }
        else
        {
          bool3 = false;
          l2 = -9223372036854775807L;
        }
        k = paramParsableByteArray.readUnsignedShort();
        i = paramParsableByteArray.readUnsignedByte();
        j = paramParsableByteArray.readUnsignedByte();
      }
      else
      {
        bool1 = false;
        l1 = -9223372036854775807L;
        bool3 = false;
        l2 = -9223372036854775807L;
        k = 0;
        i = 0;
        j = 0;
        bool2 = false;
      }
      return new Event(l3, bool4, bool1, bool2, (List)localObject, l1, bool3, l2, k, i, j);
    }
    
    private void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeLong(spliceEventId);
      paramParcel.writeByte((byte)spliceEventCancelIndicator);
      paramParcel.writeByte((byte)outOfNetworkIndicator);
      paramParcel.writeByte((byte)programSpliceFlag);
      int j = componentSpliceList.size();
      paramParcel.writeInt(j);
      int i = 0;
      while (i < j)
      {
        ((SpliceScheduleCommand.ComponentSplice)componentSpliceList.get(i)).writeToParcel(paramParcel);
        i += 1;
      }
      paramParcel.writeLong(utcSpliceTime);
      paramParcel.writeByte((byte)autoReturn);
      paramParcel.writeLong(breakDurationUs);
      paramParcel.writeInt(uniqueProgramId);
      paramParcel.writeInt(availNum);
      paramParcel.writeInt(availsExpected);
    }
  }
}
