package com.google.android.gms.auth.util.accounttransfer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import com.google.android.gms.auth.api.accounttransfer.zzo;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.server.response.FastJsonResponse.Field;
import com.google.android.gms.internal.auth.zzaz;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SafeParcelable.Class(creator="AccountTransferProgressCreator")
public class Media
  extends zzaz
{
  public static final Parcelable.Creator<zzo> CREATOR = new Point.1();
  private static final ArrayMap<String, FastJsonResponse.Field<?, ?>> zzbe;
  @SafeParcelable.VersionField(id=1)
  private final int fileSize;
  @SafeParcelable.Field(getter="getRegisteredAccountTypes", id=2)
  private List<String> zzbf;
  @SafeParcelable.Field(getter="getInProgressAccountTypes", id=3)
  private List<String> zzbg;
  @SafeParcelable.Field(getter="getSuccessAccountTypes", id=4)
  private List<String> zzbh;
  @SafeParcelable.Field(getter="getFailedAccountTypes", id=5)
  private List<String> zzbi;
  @SafeParcelable.Field(getter="getEscrowedAccountTypes", id=6)
  private List<String> zzbj;
  
  static
  {
    ArrayMap localArrayMap = new ArrayMap();
    zzbe = localArrayMap;
    localArrayMap.put("registered", FastJsonResponse.Field.forStrings("registered", 2));
    zzbe.put("in_progress", FastJsonResponse.Field.forStrings("in_progress", 3));
    zzbe.put("success", FastJsonResponse.Field.forStrings("success", 4));
    zzbe.put("failed", FastJsonResponse.Field.forStrings("failed", 5));
    zzbe.put("escrowed", FastJsonResponse.Field.forStrings("escrowed", 6));
  }
  
  public Media()
  {
    fileSize = 1;
  }
  
  Media(int paramInt, List paramList1, List paramList2, List paramList3, List paramList4, List paramList5)
  {
    fileSize = paramInt;
    zzbf = paramList1;
    zzbg = paramList2;
    zzbh = paramList3;
    zzbi = paramList4;
    zzbj = paramList5;
  }
  
  public Map getFieldMappings()
  {
    return zzbe;
  }
  
  protected Object getFieldValue(FastJsonResponse.Field paramField)
  {
    switch (paramField.getSafeParcelableFieldId())
    {
    default: 
      int i = paramField.getSafeParcelableFieldId();
      paramField = new StringBuilder(37);
      paramField.append("Unknown SafeParcelable id=");
      paramField.append(i);
      throw new IllegalStateException(paramField.toString());
    case 6: 
      return zzbj;
    case 5: 
      return zzbi;
    case 4: 
      return zzbh;
    case 3: 
      return zzbg;
    case 2: 
      return zzbf;
    }
    return Integer.valueOf(fileSize);
  }
  
  protected boolean isFieldSet(FastJsonResponse.Field paramField)
  {
    return true;
  }
  
  protected void setStringsInternal(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    int i = paramField.getSafeParcelableFieldId();
    switch (i)
    {
    default: 
      throw new IllegalArgumentException(String.format("Field with id=%d is not known to be a string list.", new Object[] { Integer.valueOf(i) }));
    case 6: 
      zzbj = paramArrayList;
      return;
    case 5: 
      zzbi = paramArrayList;
      return;
    case 4: 
      zzbh = paramArrayList;
      return;
    case 3: 
      zzbg = paramArrayList;
      return;
    }
    zzbf = paramArrayList;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, fileSize);
    SafeParcelWriter.writeStringList(paramParcel, 2, zzbf, false);
    SafeParcelWriter.writeStringList(paramParcel, 3, zzbg, false);
    SafeParcelWriter.writeStringList(paramParcel, 4, zzbh, false);
    SafeParcelWriter.writeStringList(paramParcel, 5, zzbi, false);
    SafeParcelWriter.writeStringList(paramParcel, 6, zzbj, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}
