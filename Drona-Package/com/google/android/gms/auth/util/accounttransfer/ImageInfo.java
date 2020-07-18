package com.google.android.gms.auth.util.accounttransfer;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.accounttransfer.zzr;
import com.google.android.gms.auth.api.accounttransfer.zzt;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Indicator;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.common.server.response.FastJsonResponse.Field;
import com.google.android.gms.internal.auth.zzaz;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SafeParcelable.Class(creator="AuthenticatorAnnotatedDataCreator")
public class ImageInfo
  extends zzaz
{
  public static final Parcelable.Creator<zzr> CREATOR = new VerticalProgressBar.SavedState.1();
  private static final HashMap<String, FastJsonResponse.Field<?, ?>> zzaz;
  @SafeParcelable.VersionField(id=1)
  private final int mInputType;
  @SafeParcelable.Field(getter="getPackageName", id=4)
  private String mPackageName;
  @SafeParcelable.Indicator
  private final Set<Integer> zzba;
  @SafeParcelable.Field(getter="getInfo", id=2)
  private MapPack zzbk;
  @SafeParcelable.Field(getter="getSignature", id=3)
  private String zzbl;
  @SafeParcelable.Field(getter="getId", id=5)
  private String zzbm;
  
  static
  {
    HashMap localHashMap = new HashMap();
    zzaz = localHashMap;
    localHashMap.put("authenticatorInfo", FastJsonResponse.Field.forConcreteType("authenticatorInfo", 2, zzt.class));
    zzaz.put("signature", FastJsonResponse.Field.forString("signature", 3));
    zzaz.put("package", FastJsonResponse.Field.forString("package", 4));
  }
  
  public ImageInfo()
  {
    zzba = new HashSet(3);
    mInputType = 1;
  }
  
  ImageInfo(Set paramSet, int paramInt, MapPack paramMapPack, String paramString1, String paramString2, String paramString3)
  {
    zzba = paramSet;
    mInputType = paramInt;
    zzbk = paramMapPack;
    zzbl = paramString1;
    mPackageName = paramString2;
    zzbm = paramString3;
  }
  
  public void addConcreteTypeInternal(FastJsonResponse.Field paramField, String paramString, FastJsonResponse paramFastJsonResponse)
  {
    int i = paramField.getSafeParcelableFieldId();
    if (i == 2)
    {
      zzbk = ((MapPack)paramFastJsonResponse);
      zzba.add(Integer.valueOf(i));
      return;
    }
    throw new IllegalArgumentException(String.format("Field with id=%d is not a known custom type. Found %s", new Object[] { Integer.valueOf(i), paramFastJsonResponse.getClass().getCanonicalName() }));
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
    case 4: 
      return mPackageName;
    case 3: 
      return zzbl;
    case 2: 
      return zzbk;
    }
    return Integer.valueOf(mInputType);
  }
  
  protected boolean isFieldSet(FastJsonResponse.Field paramField)
  {
    return zzba.contains(Integer.valueOf(paramField.getSafeParcelableFieldId()));
  }
  
  protected void setStringInternal(FastJsonResponse.Field paramField, String paramString1, String paramString2)
  {
    int i = paramField.getSafeParcelableFieldId();
    switch (i)
    {
    default: 
      throw new IllegalArgumentException(String.format("Field with id=%d is not known to be a string.", new Object[] { Integer.valueOf(i) }));
    case 4: 
      mPackageName = paramString2;
      break;
    case 3: 
      zzbl = paramString2;
    }
    zzba.add(Integer.valueOf(i));
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    Set localSet = zzba;
    if (localSet.contains(Integer.valueOf(1))) {
      SafeParcelWriter.writeInt(paramParcel, 1, mInputType);
    }
    if (localSet.contains(Integer.valueOf(2))) {
      SafeParcelWriter.writeParcelable(paramParcel, 2, (Parcelable)zzbk, paramInt, true);
    }
    if (localSet.contains(Integer.valueOf(3))) {
      SafeParcelWriter.writeString(paramParcel, 3, zzbl, true);
    }
    if (localSet.contains(Integer.valueOf(4))) {
      SafeParcelWriter.writeString(paramParcel, 4, mPackageName, true);
    }
    if (localSet.contains(Integer.valueOf(5))) {
      SafeParcelWriter.writeString(paramParcel, 5, zzbm, true);
    }
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
