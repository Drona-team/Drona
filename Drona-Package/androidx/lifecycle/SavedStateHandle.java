package androidx.lifecycle;

import android.os.BaseBundle;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import androidx.savedstate.SavedStateRegistry.SavedStateProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class SavedStateHandle
{
  private static final Class[] ACCEPTABLE_CLASSES;
  private static final String KEYS = "keys";
  private static final String VALUES = "values";
  private final Map<String, SavingStateLiveData<?>> mLiveDatas = new HashMap();
  final Map<String, Object> mRegular;
  private final SavedStateRegistry.SavedStateProvider mSavedStateProvider = new SavedStateRegistry.SavedStateProvider()
  {
    public Bundle saveState()
    {
      Object localObject = mRegular.keySet();
      ArrayList localArrayList1 = new ArrayList(((Set)localObject).size());
      ArrayList localArrayList2 = new ArrayList(localArrayList1.size());
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = (String)((Iterator)localObject).next();
        localArrayList1.add(str);
        localArrayList2.add(mRegular.get(str));
      }
      localObject = new Bundle();
      ((Bundle)localObject).putParcelableArrayList("keys", localArrayList1);
      ((Bundle)localObject).putParcelableArrayList("values", localArrayList2);
      return localObject;
    }
  };
  
  static
  {
    Class localClass1 = Boolean.TYPE;
    Class localClass2 = Double.TYPE;
    Class localClass3 = Integer.TYPE;
    Class localClass4 = Long.TYPE;
    Class localClass5 = Byte.TYPE;
    Class localClass6 = Character.TYPE;
    Class localClass7 = Float.TYPE;
    Class localClass8 = Short.TYPE;
    Object localObject1;
    if (Build.VERSION.SDK_INT >= 21) {
      localObject1 = Size.class;
    } else {
      localObject1 = Integer.TYPE;
    }
    Object localObject2;
    if (Build.VERSION.SDK_INT >= 21) {
      localObject2 = SizeF.class;
    } else {
      localObject2 = Integer.TYPE;
    }
    ACCEPTABLE_CLASSES = new Class[] { localClass1, [Z.class, localClass2, [D.class, localClass3, [I.class, localClass4, [J.class, String.class, [Ljava.lang.String.class, Binder.class, Bundle.class, localClass5, [B.class, localClass6, [C.class, CharSequence.class, [Ljava.lang.CharSequence.class, ArrayList.class, localClass7, [F.class, Parcelable.class, [Landroid.os.Parcelable.class, Serializable.class, localClass8, [S.class, SparseArray.class, localObject1, localObject2 };
  }
  
  public SavedStateHandle()
  {
    mRegular = new HashMap();
  }
  
  public SavedStateHandle(Map paramMap)
  {
    mRegular = new HashMap(paramMap);
  }
  
  static SavedStateHandle createHandle(Bundle paramBundle1, Bundle paramBundle2)
  {
    if ((paramBundle1 == null) && (paramBundle2 == null)) {
      return new SavedStateHandle();
    }
    HashMap localHashMap = new HashMap();
    if (paramBundle2 != null)
    {
      Iterator localIterator = paramBundle2.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localHashMap.put(str, paramBundle2.get(str));
      }
    }
    if (paramBundle1 == null) {
      return new SavedStateHandle(localHashMap);
    }
    paramBundle2 = paramBundle1.getParcelableArrayList("keys");
    paramBundle1 = paramBundle1.getParcelableArrayList("values");
    if ((paramBundle2 != null) && (paramBundle1 != null) && (paramBundle2.size() == paramBundle1.size()))
    {
      int i = 0;
      while (i < paramBundle2.size())
      {
        localHashMap.put((String)paramBundle2.get(i), paramBundle1.get(i));
        i += 1;
      }
      return new SavedStateHandle(localHashMap);
    }
    throw new IllegalStateException("Invalid bundle passed as restored state");
  }
  
  private MutableLiveData getLiveDataInternal(String paramString, boolean paramBoolean, Object paramObject)
  {
    MutableLiveData localMutableLiveData = (MutableLiveData)mLiveDatas.get(paramString);
    if (localMutableLiveData != null) {
      return localMutableLiveData;
    }
    if (mRegular.containsKey(paramString)) {
      paramObject = new SavingStateLiveData(this, paramString, mRegular.get(paramString));
    } else if (paramBoolean) {
      paramObject = new SavingStateLiveData(this, paramString, paramObject);
    } else {
      paramObject = new SavingStateLiveData(this, paramString);
    }
    mLiveDatas.put(paramString, paramObject);
    return paramObject;
  }
  
  private static void validateValue(Object paramObject)
  {
    if (paramObject == null) {
      return;
    }
    Object localObject = ACCEPTABLE_CLASSES;
    int j = localObject.length;
    int i = 0;
    while (i < j)
    {
      if (localObject[i].isInstance(paramObject)) {
        return;
      }
      i += 1;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Can't put value with type ");
    ((StringBuilder)localObject).append(paramObject.getClass());
    ((StringBuilder)localObject).append(" into saved state");
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public boolean contains(String paramString)
  {
    return mRegular.containsKey(paramString);
  }
  
  public Object get(String paramString)
  {
    return mRegular.get(paramString);
  }
  
  public void getClusters(String paramString, Object paramObject)
  {
    validateValue(paramObject);
    MutableLiveData localMutableLiveData = (MutableLiveData)mLiveDatas.get(paramString);
    if (localMutableLiveData != null)
    {
      localMutableLiveData.setValue(paramObject);
      return;
    }
    mRegular.put(paramString, paramObject);
  }
  
  public MutableLiveData getLiveData(String paramString)
  {
    return getLiveDataInternal(paramString, false, null);
  }
  
  public MutableLiveData getLiveData(String paramString, Object paramObject)
  {
    return getLiveDataInternal(paramString, true, paramObject);
  }
  
  public Set keys()
  {
    return Collections.unmodifiableSet(mRegular.keySet());
  }
  
  public Object remove(String paramString)
  {
    Object localObject = mRegular.remove(paramString);
    paramString = (SavingStateLiveData)mLiveDatas.remove(paramString);
    if (paramString != null) {
      paramString.detach();
    }
    return localObject;
  }
  
  SavedStateRegistry.SavedStateProvider savedStateProvider()
  {
    return mSavedStateProvider;
  }
  
  static class SavingStateLiveData<T>
    extends MutableLiveData<T>
  {
    private SavedStateHandle mHandle;
    private String mKey;
    
    SavingStateLiveData(SavedStateHandle paramSavedStateHandle, String paramString)
    {
      mKey = paramString;
      mHandle = paramSavedStateHandle;
    }
    
    SavingStateLiveData(SavedStateHandle paramSavedStateHandle, String paramString, Object paramObject)
    {
      super();
      mKey = paramString;
      mHandle = paramSavedStateHandle;
    }
    
    void detach()
    {
      mHandle = null;
    }
    
    public void setValue(Object paramObject)
    {
      if (mHandle != null) {
        mHandle.mRegular.put(mKey, paramObject);
      }
      super.setValue(paramObject);
    }
  }
}
