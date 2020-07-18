package com.google.android.gms.common.server.response;

import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ParseException;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@KeepForSdk
@SafeParcelable.Class(creator="SafeParcelResponseCreator")
@VisibleForTesting
public class SafeParcelResponse
  extends FastSafeParcelableJsonResponse
{
  @KeepForSdk
  public static final Parcelable.Creator<SafeParcelResponse> CREATOR = new Point.1();
  private final String mClassName;
  @SafeParcelable.VersionField(getter="getVersionCode", id=1)
  private final int zalf;
  @SafeParcelable.Field(getter="getFieldMappingDictionary", id=3)
  private final Entry zapz;
  @SafeParcelable.Field(getter="getParcel", id=2)
  private final Parcel zarb;
  private final int zarc;
  private int zard;
  private int zare;
  
  SafeParcelResponse(int paramInt, Parcel paramParcel, Entry paramEntry)
  {
    zalf = paramInt;
    zarb = ((Parcel)Preconditions.checkNotNull(paramParcel));
    zarc = 2;
    zapz = paramEntry;
    if (zapz == null) {
      mClassName = null;
    } else {
      mClassName = zapz.zact();
    }
    zard = 2;
  }
  
  private SafeParcelResponse(SafeParcelable paramSafeParcelable, Entry paramEntry, String paramString)
  {
    zalf = 1;
    zarb = Parcel.obtain();
    paramSafeParcelable.writeToParcel(zarb, 0);
    zarc = 1;
    zapz = ((Entry)Preconditions.checkNotNull(paramEntry));
    mClassName = ((String)Preconditions.checkNotNull(paramString));
    zard = 2;
  }
  
  public SafeParcelResponse(Entry paramEntry, String paramString)
  {
    zalf = 1;
    zarb = Parcel.obtain();
    zarc = 0;
    zapz = ((Entry)Preconditions.checkNotNull(paramEntry));
    mClassName = ((String)Preconditions.checkNotNull(paramString));
    zard = 0;
  }
  
  private final void append(FastJsonResponse.Field paramField)
  {
    int i;
    if (zapw != -1) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      if (zarb != null)
      {
        switch (zard)
        {
        default: 
          throw new IllegalStateException("Unknown parse state in SafeParcelResponse.");
        case 2: 
          throw new IllegalStateException("Attempted to parse JSON with a SafeParcelResponse object that is already filled with data.");
        case 1: 
          return;
        }
        zare = SafeParcelWriter.beginObjectHeader(zarb);
        zard = 1;
        return;
      }
      throw new IllegalStateException("Internal Parcel object is null.");
    }
    throw new IllegalStateException("Field does not have a valid safe parcelable field id.");
  }
  
  private static void copy(Entry paramEntry, FastJsonResponse paramFastJsonResponse)
  {
    Object localObject1 = paramFastJsonResponse.getClass();
    if (!paramEntry.accept((Class)localObject1))
    {
      Map localMap = paramFastJsonResponse.getFieldMappings();
      paramEntry.addAttribute((Class)localObject1, localMap);
      localObject1 = localMap.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        paramFastJsonResponse = (FastJsonResponse.Field)localMap.get((String)((Iterator)localObject1).next());
        Object localObject2 = zapx;
        if (localObject2 != null) {
          try
          {
            localObject2 = ((Class)localObject2).newInstance();
            localObject2 = (FastJsonResponse)localObject2;
            copy(paramEntry, (FastJsonResponse)localObject2);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            paramEntry = String.valueOf(zapx.getCanonicalName());
            if (paramEntry.length() != 0) {
              paramEntry = "Could not access object of type ".concat(paramEntry);
            } else {
              paramEntry = new String("Could not access object of type ");
            }
            throw new IllegalStateException(paramEntry, localIllegalAccessException);
          }
          catch (InstantiationException localInstantiationException)
          {
            paramEntry = String.valueOf(zapx.getCanonicalName());
            if (paramEntry.length() != 0) {
              paramEntry = "Could not instantiate an object of type ".concat(paramEntry);
            } else {
              paramEntry = new String("Could not instantiate an object of type ");
            }
            throw new IllegalStateException(paramEntry, localInstantiationException);
          }
        }
      }
    }
  }
  
  public static SafeParcelResponse from(FastJsonResponse paramFastJsonResponse)
  {
    String str = paramFastJsonResponse.getClass().getCanonicalName();
    Entry localEntry = new Entry(paramFastJsonResponse.getClass());
    copy(localEntry, paramFastJsonResponse);
    localEntry.zacs();
    localEntry.zacr();
    return new SafeParcelResponse((SafeParcelable)paramFastJsonResponse, localEntry, str);
  }
  
  private final void processOptions(StringBuilder paramStringBuilder, FastJsonResponse.Field paramField, Object paramObject)
  {
    if (zaps)
    {
      paramObject = (ArrayList)paramObject;
      paramStringBuilder.append("[");
      int j = paramObject.size();
      int i = 0;
      while (i < j)
      {
        if (i != 0) {
          paramStringBuilder.append(",");
        }
        set(paramStringBuilder, zapr, paramObject.get(i));
        i += 1;
      }
      paramStringBuilder.append("]");
      return;
    }
    set(paramStringBuilder, zapr, paramObject);
  }
  
  private static void set(StringBuilder paramStringBuilder, int paramInt, Object paramObject)
  {
    switch (paramInt)
    {
    default: 
      paramStringBuilder = new StringBuilder(26);
      paramStringBuilder.append("Unknown type = ");
      paramStringBuilder.append(paramInt);
      throw new IllegalArgumentException(paramStringBuilder.toString());
    case 11: 
      throw new IllegalArgumentException("Method does not accept concrete type.");
    case 10: 
      MapUtils.writeStringMapToJson(paramStringBuilder, (HashMap)paramObject);
      return;
    case 9: 
      paramStringBuilder.append("\"");
      paramStringBuilder.append(Base64Utils.encodeUrlSafe((byte[])paramObject));
      paramStringBuilder.append("\"");
      return;
    case 8: 
      paramStringBuilder.append("\"");
      paramStringBuilder.append(Base64Utils.encode((byte[])paramObject));
      paramStringBuilder.append("\"");
      return;
    case 7: 
      paramStringBuilder.append("\"");
      paramStringBuilder.append(JsonUtils.escapeString(paramObject.toString()));
      paramStringBuilder.append("\"");
      return;
    }
    paramStringBuilder.append(paramObject);
  }
  
  private final void write(StringBuilder paramStringBuilder, Map paramMap, Parcel paramParcel)
  {
    SparseArray localSparseArray = new SparseArray();
    paramMap = paramMap.entrySet().iterator();
    Object localObject1;
    while (paramMap.hasNext())
    {
      localObject1 = (Map.Entry)paramMap.next();
      localSparseArray.put(((FastJsonResponse.Field)((Map.Entry)localObject1).getValue()).getSafeParcelableFieldId(), localObject1);
    }
    paramStringBuilder.append('{');
    int j = SafeParcelReader.validateObjectHeader(paramParcel);
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = SafeParcelReader.readHeader(paramParcel);
      paramMap = (Map.Entry)localSparseArray.get(SafeParcelReader.getFieldId(k));
      if (paramMap != null)
      {
        if (i != 0) {
          paramStringBuilder.append(",");
        }
        localObject1 = (String)paramMap.getKey();
        paramMap = (FastJsonResponse.Field)paramMap.getValue();
        paramStringBuilder.append("\"");
        paramStringBuilder.append((String)localObject1);
        paramStringBuilder.append("\":");
        Object localObject2;
        if (paramMap.zacn())
        {
          switch (zapt)
          {
          default: 
            i = zapt;
            paramStringBuilder = new StringBuilder(36);
            paramStringBuilder.append("Unknown field out type = ");
            paramStringBuilder.append(i);
            throw new IllegalArgumentException(paramStringBuilder.toString());
          case 11: 
            throw new IllegalArgumentException("Method does not accept concrete type.");
          case 10: 
            localObject1 = SafeParcelReader.createBundle(paramParcel, k);
            localObject2 = new HashMap();
            Iterator localIterator = ((BaseBundle)localObject1).keySet().iterator();
            while (localIterator.hasNext())
            {
              String str = (String)localIterator.next();
              ((HashMap)localObject2).put(str, ((BaseBundle)localObject1).getString(str));
            }
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, localObject2));
            break;
          case 8: 
          case 9: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, SafeParcelReader.createByteArray(paramParcel, k)));
            break;
          case 7: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, SafeParcelReader.createString(paramParcel, k)));
            break;
          case 6: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, Boolean.valueOf(SafeParcelReader.readBoolean(paramParcel, k))));
            break;
          case 5: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, SafeParcelReader.createBigDecimal(paramParcel, k)));
            break;
          case 4: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, Double.valueOf(SafeParcelReader.readDouble(paramParcel, k))));
            break;
          case 3: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, Float.valueOf(SafeParcelReader.readFloat(paramParcel, k))));
            break;
          case 2: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, Long.valueOf(SafeParcelReader.readLong(paramParcel, k))));
            break;
          case 1: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, SafeParcelReader.createBigInteger(paramParcel, k)));
            break;
          case 0: 
            processOptions(paramStringBuilder, paramMap, FastJsonResponse.valueOf(paramMap, Integer.valueOf(SafeParcelReader.readInt(paramParcel, k))));
            break;
          }
        }
        else if (zapu)
        {
          paramStringBuilder.append("[");
          switch (zapt)
          {
          default: 
            throw new IllegalStateException("Unknown field type out.");
          case 11: 
            localObject1 = SafeParcelReader.createParcelArray(paramParcel, k);
            k = localObject1.length;
            i = 0;
          }
          while (i < k)
          {
            if (i > 0) {
              paramStringBuilder.append(",");
            }
            localObject1[i].setDataPosition(0);
            write(paramStringBuilder, paramMap.zacq(), localObject1[i]);
            i += 1;
            continue;
            throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
            ArrayUtils.writeStringArray(paramStringBuilder, SafeParcelReader.createStringArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createBooleanArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createBigDecimalArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createDoubleArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createFloatArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createLongArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createBigIntegerArray(paramParcel, k));
            break;
            ArrayUtils.writeArray(paramStringBuilder, SafeParcelReader.createIntArray(paramParcel, k));
          }
          paramStringBuilder.append("]");
        }
        else
        {
          switch (zapt)
          {
          default: 
            throw new IllegalStateException("Unknown field type out");
          case 11: 
            localObject1 = SafeParcelReader.createParcel(paramParcel, k);
            ((Parcel)localObject1).setDataPosition(0);
            write(paramStringBuilder, paramMap.zacq(), (Parcel)localObject1);
            break;
          case 10: 
            paramMap = SafeParcelReader.createBundle(paramParcel, k);
            localObject1 = paramMap.keySet();
            ((Set)localObject1).size();
            paramStringBuilder.append("{");
            localObject1 = ((Set)localObject1).iterator();
            for (i = 1; ((Iterator)localObject1).hasNext(); i = 0)
            {
              localObject2 = (String)((Iterator)localObject1).next();
              if (i == 0) {
                paramStringBuilder.append(",");
              }
              paramStringBuilder.append("\"");
              paramStringBuilder.append((String)localObject2);
              paramStringBuilder.append("\"");
              paramStringBuilder.append(":");
              paramStringBuilder.append("\"");
              paramStringBuilder.append(JsonUtils.escapeString(paramMap.getString((String)localObject2)));
              paramStringBuilder.append("\"");
            }
            paramStringBuilder.append("}");
            break;
          case 9: 
            paramMap = SafeParcelReader.createByteArray(paramParcel, k);
            paramStringBuilder.append("\"");
            paramStringBuilder.append(Base64Utils.encodeUrlSafe(paramMap));
            paramStringBuilder.append("\"");
            break;
          case 8: 
            paramMap = SafeParcelReader.createByteArray(paramParcel, k);
            paramStringBuilder.append("\"");
            paramStringBuilder.append(Base64Utils.encode(paramMap));
            paramStringBuilder.append("\"");
            break;
          case 7: 
            paramMap = SafeParcelReader.createString(paramParcel, k);
            paramStringBuilder.append("\"");
            paramStringBuilder.append(JsonUtils.escapeString(paramMap));
            paramStringBuilder.append("\"");
            break;
          case 6: 
            paramStringBuilder.append(SafeParcelReader.readBoolean(paramParcel, k));
            break;
          case 5: 
            paramStringBuilder.append(SafeParcelReader.createBigDecimal(paramParcel, k));
            break;
          case 4: 
            paramStringBuilder.append(SafeParcelReader.readDouble(paramParcel, k));
            break;
          case 3: 
            paramStringBuilder.append(SafeParcelReader.readFloat(paramParcel, k));
            break;
          case 2: 
            paramStringBuilder.append(SafeParcelReader.readLong(paramParcel, k));
            break;
          case 1: 
            paramStringBuilder.append(SafeParcelReader.createBigInteger(paramParcel, k));
            break;
          case 0: 
            paramStringBuilder.append(SafeParcelReader.readInt(paramParcel, k));
          }
        }
        i = 1;
      }
    }
    if (paramParcel.dataPosition() == j)
    {
      paramStringBuilder.append('}');
      return;
    }
    paramStringBuilder = new StringBuilder(37);
    paramStringBuilder.append("Overread allowed size end=");
    paramStringBuilder.append(j);
    throw new SafeParcelReader.ParseException(paramStringBuilder.toString(), paramParcel);
  }
  
  private final Parcel zacu()
  {
    switch (zard)
    {
    default: 
      break;
    case 0: 
      zare = SafeParcelWriter.beginObjectHeader(zarb);
    case 1: 
      SafeParcelWriter.finishObjectHeader(zarb, zare);
      zard = 2;
    }
    return zarb;
  }
  
  protected final void addAll(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new float[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((Float)paramArrayList.get(i)).floatValue();
      i += 1;
    }
    SafeParcelWriter.writeFloatArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  public void addConcreteTypeArrayInternal(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    paramString = new ArrayList();
    paramArrayList.size();
    paramArrayList = (ArrayList)paramArrayList;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = paramArrayList.get(i);
      i += 1;
      paramString.add(((SafeParcelResponse)localObject).zacu());
    }
    SafeParcelWriter.writeParcelList(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  public void addConcreteTypeInternal(FastJsonResponse.Field paramField, String paramString, FastJsonResponse paramFastJsonResponse)
  {
    append(paramField);
    paramString = ((SafeParcelResponse)paramFastJsonResponse).zacu();
    SafeParcelWriter.writeParcel(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  protected final void copyAll(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new long[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((Long)paramArrayList.get(i)).longValue();
      i += 1;
    }
    SafeParcelWriter.writeLongArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  protected final void forEach(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new boolean[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((Boolean)paramArrayList.get(i)).booleanValue();
      i += 1;
    }
    SafeParcelWriter.writeBooleanArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  public Map getFieldMappings()
  {
    if (zapz == null) {
      return null;
    }
    return zapz.getData(mClassName);
  }
  
  public Object getValueObject(String paramString)
  {
    throw new UnsupportedOperationException("Converting to JSON does not require this method.");
  }
  
  protected final void i(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new BigDecimal[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((BigDecimal)paramArrayList.get(i));
      i += 1;
    }
    SafeParcelWriter.writeBigDecimalArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  public boolean isPrimitiveFieldSet(String paramString)
  {
    throw new UnsupportedOperationException("Converting to JSON does not require this method.");
  }
  
  protected final void scheduleNext(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new double[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((Double)paramArrayList.get(i)).doubleValue();
      i += 1;
    }
    SafeParcelWriter.writeDoubleArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  protected void setBooleanInternal(FastJsonResponse.Field paramField, String paramString, boolean paramBoolean)
  {
    append(paramField);
    SafeParcelWriter.writeBoolean(zarb, paramField.getSafeParcelableFieldId(), paramBoolean);
  }
  
  protected void setDecodedBytesInternal(FastJsonResponse.Field paramField, String paramString, byte[] paramArrayOfByte)
  {
    append(paramField);
    SafeParcelWriter.writeByteArray(zarb, paramField.getSafeParcelableFieldId(), paramArrayOfByte, true);
  }
  
  protected final void setDetails(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new int[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((Integer)paramArrayList.get(i)).intValue();
      i += 1;
    }
    SafeParcelWriter.writeIntArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  protected void setIntegerInternal(FastJsonResponse.Field paramField, String paramString, int paramInt)
  {
    append(paramField);
    SafeParcelWriter.writeInt(zarb, paramField.getSafeParcelableFieldId(), paramInt);
  }
  
  protected void setLongInternal(FastJsonResponse.Field paramField, String paramString, long paramLong)
  {
    append(paramField);
    SafeParcelWriter.writeLong(zarb, paramField.getSafeParcelableFieldId(), paramLong);
  }
  
  protected final void setPlaybackSpeed(FastJsonResponse.Field paramField, String paramString, float paramFloat)
  {
    append(paramField);
    SafeParcelWriter.writeFloat(zarb, paramField.getSafeParcelableFieldId(), paramFloat);
  }
  
  protected void setStringInternal(FastJsonResponse.Field paramField, String paramString1, String paramString2)
  {
    append(paramField);
    SafeParcelWriter.writeString(zarb, paramField.getSafeParcelableFieldId(), paramString2, true);
  }
  
  protected void setStringsInternal(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new String[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((String)paramArrayList.get(i));
      i += 1;
    }
    SafeParcelWriter.writeStringArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  public String toString()
  {
    Preconditions.checkNotNull(zapz, "Cannot convert to JSON on client side.");
    Parcel localParcel = zacu();
    localParcel.setDataPosition(0);
    StringBuilder localStringBuilder = new StringBuilder(100);
    write(localStringBuilder, zapz.getData(mClassName), localParcel);
    return localStringBuilder.toString();
  }
  
  protected final void write(FastJsonResponse.Field paramField, String paramString, BigInteger paramBigInteger)
  {
    append(paramField);
    SafeParcelWriter.writeBigInteger(zarb, paramField.getSafeParcelableFieldId(), paramBigInteger, true);
  }
  
  protected final void write(FastJsonResponse.Field paramField, String paramString, ArrayList paramArrayList)
  {
    append(paramField);
    int j = paramArrayList.size();
    paramString = new BigInteger[j];
    int i = 0;
    while (i < j)
    {
      paramString[i] = ((BigInteger)paramArrayList.get(i));
      i += 1;
    }
    SafeParcelWriter.writeBigIntegerArray(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  protected final void write(FastJsonResponse.Field paramField, String paramString, Map paramMap)
  {
    append(paramField);
    paramString = new Bundle();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramString.putString(str, (String)paramMap.get(str));
    }
    SafeParcelWriter.writeBundle(zarb, paramField.getSafeParcelableFieldId(), paramString, true);
  }
  
  protected final void writeBinary(FastJsonResponse.Field paramField, String paramString, BigDecimal paramBigDecimal)
  {
    append(paramField);
    SafeParcelWriter.writeBigDecimal(zarb, paramField.getSafeParcelableFieldId(), paramBigDecimal, true);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeParcel(paramParcel, 2, zacu(), false);
    Entry localEntry;
    switch (zarc)
    {
    default: 
      paramInt = zarc;
      paramParcel = new StringBuilder(34);
      paramParcel.append("Invalid creation type: ");
      paramParcel.append(paramInt);
      throw new IllegalStateException(paramParcel.toString());
    case 2: 
      localEntry = zapz;
      break;
    case 1: 
      localEntry = zapz;
      break;
    case 0: 
      localEntry = null;
    }
    SafeParcelWriter.writeParcelable(paramParcel, 3, localEntry, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  protected final void writeValue(FastJsonResponse.Field paramField, String paramString, double paramDouble)
  {
    append(paramField);
    SafeParcelWriter.writeDouble(zarb, paramField.getSafeParcelableFieldId(), paramDouble);
  }
}
