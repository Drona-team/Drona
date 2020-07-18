package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ShowFirstParty;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@ShowFirstParty
@SafeParcelable.Class(creator="FieldMappingDictionaryEntryCreator")
public final class AppInfo
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zal> CREATOR = new DiscreteSeekBar.CustomState.1();
  @SafeParcelable.Field(id=2)
  final String className;
  @SafeParcelable.VersionField(id=1)
  private final int versionCode;
  @SafeParcelable.Field(id=3)
  final ArrayList<zam> zaqy;
  
  AppInfo(int paramInt, String paramString, ArrayList paramArrayList)
  {
    versionCode = paramInt;
    className = paramString;
    zaqy = paramArrayList;
  }
  
  AppInfo(String paramString, Map paramMap)
  {
    versionCode = 1;
    className = paramString;
    if (paramMap == null)
    {
      paramString = null;
    }
    else
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramMap.keySet().iterator();
      for (;;)
      {
        paramString = localArrayList;
        if (!localIterator.hasNext()) {
          break;
        }
        paramString = (String)localIterator.next();
        localArrayList.add(new CustomTile.ExpandedStyle(paramString, (FastJsonResponse.Field)paramMap.get(paramString)));
      }
    }
    zaqy = paramString;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, versionCode);
    SafeParcelWriter.writeString(paramParcel, 2, className, false);
    SafeParcelWriter.writeTypedList(paramParcel, 3, zaqy, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}
