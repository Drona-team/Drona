package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.server.response.FastJsonResponse.FieldConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@KeepForSdk
@SafeParcelable.Class(creator="StringToIntConverterCreator")
public final class StringToIntConverter
  extends AbstractSafeParcelable
  implements FastJsonResponse.FieldConverter<String, Integer>
{
  public static final Parcelable.Creator<StringToIntConverter> CREATOR = new DiscreteSeekBar.CustomState.1();
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  private final HashMap<String, Integer> zapm;
  private final SparseArray<String> zapn;
  @SafeParcelable.Field(getter="getSerializedMap", id=2)
  private final ArrayList<zaa> zapo;
  
  public StringToIntConverter()
  {
    zalf = 1;
    zapm = new HashMap();
    zapn = new SparseArray();
    zapo = null;
  }
  
  StringToIntConverter(int paramInt, ArrayList paramArrayList)
  {
    zalf = paramInt;
    zapm = new HashMap();
    zapn = new SparseArray();
    zapo = null;
    paramArrayList = (ArrayList)paramArrayList;
    int i = paramArrayList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      Object localObject = paramArrayList.get(paramInt);
      paramInt += 1;
      localObject = (zaa)localObject;
      add(zapp, zapq);
    }
  }
  
  public final StringToIntConverter add(String paramString, int paramInt)
  {
    zapm.put(paramString, Integer.valueOf(paramInt));
    zapn.put(paramInt, paramString);
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = zapm.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new zaa(str, ((Integer)zapm.get(str)).intValue()));
    }
    SafeParcelWriter.writeTypedList(paramParcel, 2, localArrayList, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final int zacj()
  {
    return 7;
  }
  
  public final int zack()
  {
    return 0;
  }
  
  @SafeParcelable.Class(creator="StringToIntConverterEntryCreator")
  public static final class zaa
    extends AbstractSafeParcelable
  {
    public static final Parcelable.Creator<zaa> CREATOR = new DownloadProgressInfo.1();
    @SafeParcelable.VersionField(id=1)
    private final int versionCode;
    @SafeParcelable.Field(id=2)
    final String zapp;
    @SafeParcelable.Field(id=3)
    final int zapq;
    
    zaa(int paramInt1, String paramString, int paramInt2)
    {
      versionCode = paramInt1;
      zapp = paramString;
      zapq = paramInt2;
    }
    
    zaa(String paramString, int paramInt)
    {
      versionCode = 1;
      zapp = paramString;
      zapq = paramInt;
    }
    
    public final void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
      SafeParcelWriter.writeInt(paramParcel, 1, versionCode);
      SafeParcelWriter.writeString(paramParcel, 2, zapp, false);
      SafeParcelWriter.writeInt(paramParcel, 3, zapq);
      SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
    }
  }
}
