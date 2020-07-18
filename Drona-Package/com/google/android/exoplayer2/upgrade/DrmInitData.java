package com.google.android.exoplayer2.upgrade;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class DrmInitData
  implements Comparator<com.google.android.exoplayer2.drm.DrmInitData.SchemeData>, Parcelable
{
  public static final Parcelable.Creator<com.google.android.exoplayer2.drm.DrmInitData> CREATOR = new Parcelable.Creator()
  {
    public DrmInitData createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DrmInitData(paramAnonymousParcel);
    }
    
    public DrmInitData[] newArray(int paramAnonymousInt)
    {
      return new DrmInitData[paramAnonymousInt];
    }
  };
  private int hashCode;
  public final int schemeDataCount;
  private final SchemeData[] schemeDatas;
  @Nullable
  public final String schemeType;
  
  DrmInitData(Parcel paramParcel)
  {
    schemeType = paramParcel.readString();
    schemeDatas = ((SchemeData[])paramParcel.createTypedArray(SchemeData.CREATOR));
    schemeDataCount = schemeDatas.length;
  }
  
  public DrmInitData(String paramString, List paramList)
  {
    this(paramString, false, (SchemeData[])paramList.toArray(new SchemeData[paramList.size()]));
  }
  
  private DrmInitData(String paramString, boolean paramBoolean, SchemeData... paramVarArgs)
  {
    schemeType = paramString;
    paramString = paramVarArgs;
    if (paramBoolean) {
      paramString = (SchemeData[])paramVarArgs.clone();
    }
    Arrays.sort(paramString, this);
    schemeDatas = paramString;
    schemeDataCount = paramString.length;
  }
  
  public DrmInitData(String paramString, SchemeData... paramVarArgs)
  {
    this(paramString, true, paramVarArgs);
  }
  
  public DrmInitData(List paramList)
  {
    this(null, false, (SchemeData[])paramList.toArray(new SchemeData[paramList.size()]));
  }
  
  public DrmInitData(SchemeData... paramVarArgs)
  {
    this(null, paramVarArgs);
  }
  
  private static boolean containsSchemeDataWithUuid(ArrayList paramArrayList, int paramInt, UUID paramUUID)
  {
    int i = 0;
    while (i < paramInt)
    {
      if (getuuid.equals(paramUUID)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static DrmInitData createSessionCreationData(DrmInitData paramDrmInitData1, DrmInitData paramDrmInitData2)
  {
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    Object localObject1;
    int k;
    int i;
    if (paramDrmInitData1 != null)
    {
      localObject1 = schemeType;
      localObject2 = schemeDatas;
      k = localObject2.length;
      i = 0;
      for (;;)
      {
        paramDrmInitData1 = (DrmInitData)localObject1;
        if (i >= k) {
          break;
        }
        paramDrmInitData1 = localObject2[i];
        if (paramDrmInitData1.hasData()) {
          localArrayList.add(paramDrmInitData1);
        }
        i += 1;
      }
    }
    paramDrmInitData1 = null;
    Object localObject2 = paramDrmInitData1;
    if (paramDrmInitData2 != null)
    {
      localObject1 = paramDrmInitData1;
      if (paramDrmInitData1 == null) {
        localObject1 = schemeType;
      }
      k = localArrayList.size();
      paramDrmInitData1 = schemeDatas;
      int m = paramDrmInitData1.length;
      i = j;
      for (;;)
      {
        localObject2 = localObject1;
        if (i >= m) {
          break;
        }
        paramDrmInitData2 = paramDrmInitData1[i];
        if ((paramDrmInitData2.hasData()) && (!containsSchemeDataWithUuid(localArrayList, k, uuid))) {
          localArrayList.add(paramDrmInitData2);
        }
        i += 1;
      }
    }
    if (localArrayList.isEmpty()) {
      return null;
    }
    return new DrmInitData((String)localObject2, localArrayList);
  }
  
  public int compare(SchemeData paramSchemeData1, SchemeData paramSchemeData2)
  {
    if (IpAddress.UUID_NIL.equals(uuid))
    {
      if (IpAddress.UUID_NIL.equals(uuid)) {
        return 0;
      }
      return 1;
    }
    return uuid.compareTo(uuid);
  }
  
  public DrmInitData copyWithSchemeType(String paramString)
  {
    if (Util.areEqual(schemeType, paramString)) {
      return this;
    }
    return new DrmInitData(paramString, false, schemeDatas);
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
      paramObject = (DrmInitData)paramObject;
      return (Util.areEqual(schemeType, schemeType)) && (Arrays.equals(schemeDatas, schemeDatas));
    }
    return false;
  }
  
  public SchemeData get(UUID paramUUID)
  {
    SchemeData[] arrayOfSchemeData = schemeDatas;
    int j = arrayOfSchemeData.length;
    int i = 0;
    while (i < j)
    {
      SchemeData localSchemeData = arrayOfSchemeData[i];
      if (localSchemeData.matches(paramUUID)) {
        return localSchemeData;
      }
      i += 1;
    }
    return null;
  }
  
  public SchemeData getLanguage(int paramInt)
  {
    return schemeDatas[paramInt];
  }
  
  public int hashCode()
  {
    if (hashCode == 0)
    {
      int i;
      if (schemeType == null) {
        i = 0;
      } else {
        i = schemeType.hashCode();
      }
      hashCode = (i * 31 + Arrays.hashCode(schemeDatas));
    }
    return hashCode;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(schemeType);
    paramParcel.writeTypedArray(schemeDatas, 0);
  }
  
  public final class SchemeData
    implements Parcelable
  {
    public static final Parcelable.Creator<com.google.android.exoplayer2.drm.DrmInitData.SchemeData> CREATOR = new Parcelable.Creator()
    {
      public DrmInitData.SchemeData createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DrmInitData.SchemeData(paramAnonymousParcel);
      }
      
      public DrmInitData.SchemeData[] newArray(int paramAnonymousInt)
      {
        return new DrmInitData.SchemeData[paramAnonymousInt];
      }
    };
    public final byte[] data;
    private int hashCode;
    @Nullable
    public final String licenseServerUrl;
    public final String mimeType;
    public final boolean requiresSecureDecryption;
    private final UUID uuid;
    
    SchemeData()
    {
      uuid = new UUID(this$1.readLong(), this$1.readLong());
      licenseServerUrl = this$1.readString();
      mimeType = this$1.readString();
      data = this$1.createByteArray();
      boolean bool;
      if (this$1.readByte() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      requiresSecureDecryption = bool;
    }
    
    public SchemeData(String paramString1, String paramString2, byte[] paramArrayOfByte, boolean paramBoolean)
    {
      uuid = ((UUID)Assertions.checkNotNull(this$1));
      licenseServerUrl = paramString1;
      mimeType = ((String)Assertions.checkNotNull(paramString2));
      data = paramArrayOfByte;
      requiresSecureDecryption = paramBoolean;
    }
    
    public SchemeData(String paramString, byte[] paramArrayOfByte)
    {
      this(paramString, paramArrayOfByte, false);
    }
    
    public SchemeData(String paramString, byte[] paramArrayOfByte, boolean paramBoolean)
    {
      this(null, paramString, paramArrayOfByte, paramBoolean);
    }
    
    public boolean canReplace(SchemeData paramSchemeData)
    {
      return (hasData()) && (!paramSchemeData.hasData()) && (matches(uuid));
    }
    
    public SchemeData copyWithData(byte[] paramArrayOfByte)
    {
      return new SchemeData(uuid, licenseServerUrl, mimeType, paramArrayOfByte, requiresSecureDecryption);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof SchemeData)) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      paramObject = (SchemeData)paramObject;
      return (Util.areEqual(licenseServerUrl, licenseServerUrl)) && (Util.areEqual(mimeType, mimeType)) && (Util.areEqual(uuid, uuid)) && (Arrays.equals(data, data));
    }
    
    public boolean hasData()
    {
      return data != null;
    }
    
    public int hashCode()
    {
      if (hashCode == 0)
      {
        int j = uuid.hashCode();
        int i;
        if (licenseServerUrl == null) {
          i = 0;
        } else {
          i = licenseServerUrl.hashCode();
        }
        hashCode = (((j * 31 + i) * 31 + mimeType.hashCode()) * 31 + Arrays.hashCode(data));
      }
      return hashCode;
    }
    
    public boolean matches(UUID paramUUID)
    {
      return (IpAddress.UUID_NIL.equals(uuid)) || (paramUUID.equals(uuid));
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(uuid.getMostSignificantBits());
      paramParcel.writeLong(uuid.getLeastSignificantBits());
      paramParcel.writeString(licenseServerUrl);
      paramParcel.writeString(mimeType);
      paramParcel.writeByteArray(data);
      paramParcel.writeByte((byte)requiresSecureDecryption);
    }
  }
}
