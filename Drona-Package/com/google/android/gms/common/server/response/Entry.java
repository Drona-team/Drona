package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ShowFirstParty;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@ShowFirstParty
@SafeParcelable.Class(creator="FieldMappingDictionaryCreator")
public final class Entry
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zak> CREATOR = new DownloadProgressInfo.1();
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  private final HashMap<String, Map<String, FastJsonResponse.Field<?, ?>>> zaqv;
  @SafeParcelable.Field(getter="getSerializedDictionary", id=2)
  private final ArrayList<zal> zaqw;
  @SafeParcelable.Field(getter="getRootClassName", id=3)
  private final String zaqx;
  
  Entry(int paramInt, ArrayList paramArrayList, String paramString)
  {
    zalf = paramInt;
    zaqw = null;
    HashMap localHashMap1 = new HashMap();
    int j = paramArrayList.size();
    paramInt = 0;
    while (paramInt < j)
    {
      AppInfo localAppInfo = (AppInfo)paramArrayList.get(paramInt);
      String str = className;
      HashMap localHashMap2 = new HashMap();
      int k = zaqy.size();
      int i = 0;
      while (i < k)
      {
        CustomTile.ExpandedStyle localExpandedStyle = (CustomTile.ExpandedStyle)zaqy.get(i);
        localHashMap2.put(zaqz, zara);
        i += 1;
      }
      localHashMap1.put(str, localHashMap2);
      paramInt += 1;
    }
    zaqv = localHashMap1;
    zaqx = ((String)Preconditions.checkNotNull(paramString));
    zacr();
  }
  
  public Entry(Class paramClass)
  {
    zalf = 1;
    zaqw = null;
    zaqv = new HashMap();
    zaqx = paramClass.getCanonicalName();
  }
  
  public final boolean accept(Class paramClass)
  {
    return zaqv.containsKey(paramClass.getCanonicalName());
  }
  
  public final void addAttribute(Class paramClass, Map paramMap)
  {
    zaqv.put(paramClass.getCanonicalName(), paramMap);
  }
  
  public final Map getData(String paramString)
  {
    return (Map)zaqv.get(paramString);
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator1 = zaqv.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(":\n");
      localObject = (Map)zaqv.get(localObject);
      Iterator localIterator2 = ((Map)localObject).keySet().iterator();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        localStringBuilder.append("  ");
        localStringBuilder.append(str);
        localStringBuilder.append(": ");
        localStringBuilder.append(((Map)localObject).get(str));
      }
    }
    return localStringBuilder.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = zaqv.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new AppInfo(str, (Map)zaqv.get(str)));
    }
    SafeParcelWriter.writeTypedList(paramParcel, 2, localArrayList, false);
    SafeParcelWriter.writeString(paramParcel, 3, zaqx, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final void zacr()
  {
    Iterator localIterator1 = zaqv.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (String)localIterator1.next();
      localObject = (Map)zaqv.get(localObject);
      Iterator localIterator2 = ((Map)localObject).keySet().iterator();
      while (localIterator2.hasNext()) {
        ((FastJsonResponse.Field)((Map)localObject).get((String)localIterator2.next())).removeEntry(this);
      }
    }
  }
  
  public final void zacs()
  {
    Iterator localIterator1 = zaqv.keySet().iterator();
    while (localIterator1.hasNext())
    {
      String str1 = (String)localIterator1.next();
      Map localMap = (Map)zaqv.get(str1);
      HashMap localHashMap = new HashMap();
      Iterator localIterator2 = localMap.keySet().iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        localHashMap.put(str2, ((FastJsonResponse.Field)localMap.get(str2)).zacl());
      }
      zaqv.put(str1, localHashMap);
    }
  }
  
  public final String zact()
  {
    return zaqx;
  }
}
