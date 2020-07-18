package androidx.collection;

public class LongSparseArray<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private long[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public LongSparseArray()
  {
    this(10);
  }
  
  public LongSparseArray(int paramInt)
  {
    if (paramInt == 0)
    {
      mKeys = ContainerHelpers.EMPTY_LONGS;
      mValues = ContainerHelpers.EMPTY_OBJECTS;
      return;
    }
    paramInt = ContainerHelpers.idealLongArraySize(paramInt);
    mKeys = new long[paramInt];
    mValues = new Object[paramInt];
  }
  
  private void gc()
  {
    int m = mSize;
    long[] arrayOfLong = mKeys;
    Object[] arrayOfObject = mValues;
    int i = 0;
    int k;
    for (int j = 0; i < m; j = k)
    {
      Object localObject = arrayOfObject[i];
      k = j;
      if (localObject != DELETED)
      {
        if (i != j)
        {
          arrayOfLong[j] = arrayOfLong[i];
          arrayOfObject[j] = localObject;
          arrayOfObject[i] = null;
        }
        k = j + 1;
      }
      i += 1;
    }
    mGarbage = false;
    mSize = j;
  }
  
  public void append(long paramLong, Object paramObject)
  {
    if ((mSize != 0) && (paramLong <= mKeys[(mSize - 1)]))
    {
      put(paramLong, paramObject);
      return;
    }
    if ((mGarbage) && (mSize >= mKeys.length)) {
      gc();
    }
    int i = mSize;
    if (i >= mKeys.length)
    {
      int j = ContainerHelpers.idealLongArraySize(i + 1);
      long[] arrayOfLong = new long[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(mKeys, 0, arrayOfLong, 0, mKeys.length);
      System.arraycopy(mValues, 0, arrayOfObject, 0, mValues.length);
      mKeys = arrayOfLong;
      mValues = arrayOfObject;
    }
    mKeys[i] = paramLong;
    mValues[i] = paramObject;
    mSize = (i + 1);
  }
  
  public void clear()
  {
    int j = mSize;
    Object[] arrayOfObject = mValues;
    int i = 0;
    while (i < j)
    {
      arrayOfObject[i] = null;
      i += 1;
    }
    mSize = 0;
    mGarbage = false;
  }
  
  public LongSparseArray clone()
  {
    try
    {
      Object localObject1 = super.clone();
      localObject1 = (LongSparseArray)localObject1;
      Object localObject2 = mKeys;
      localObject2 = localObject2.clone();
      mKeys = ((long[])localObject2);
      localObject2 = mValues;
      localObject2 = localObject2.clone();
      mValues = ((Object[])localObject2);
      return localObject1;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
  }
  
  public boolean containsKey(long paramLong)
  {
    return indexOfKey(paramLong) >= 0;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return indexOfValue(paramObject) >= 0;
  }
  
  public void delete(long paramLong)
  {
    remove(paramLong);
  }
  
  public Object get(long paramLong)
  {
    return get(paramLong, null);
  }
  
  public Object get(long paramLong, Object paramObject)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    Object localObject = paramObject;
    if (i >= 0)
    {
      if (mValues[i] == DELETED) {
        return paramObject;
      }
      localObject = mValues[i];
    }
    return localObject;
  }
  
  public int indexOfKey(long paramLong)
  {
    if (mGarbage) {
      gc();
    }
    return ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
  }
  
  public int indexOfValue(Object paramObject)
  {
    if (mGarbage) {
      gc();
    }
    int i = 0;
    while (i < mSize)
    {
      if (mValues[i] == paramObject) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public long keyAt(int paramInt)
  {
    if (mGarbage) {
      gc();
    }
    return mKeys[paramInt];
  }
  
  public void put(long paramLong, Object paramObject)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    if (i >= 0)
    {
      mValues[i] = paramObject;
      return;
    }
    int j = i;
    if ((j < mSize) && (mValues[j] == DELETED))
    {
      mKeys[j] = paramLong;
      mValues[j] = paramObject;
      return;
    }
    i = j;
    if (mGarbage)
    {
      i = j;
      if (mSize >= mKeys.length)
      {
        gc();
        i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
      }
    }
    long[] arrayOfLong;
    Object localObject;
    if (mSize >= mKeys.length)
    {
      j = ContainerHelpers.idealLongArraySize(mSize + 1);
      arrayOfLong = new long[j];
      localObject = new Object[j];
      System.arraycopy(mKeys, 0, arrayOfLong, 0, mKeys.length);
      System.arraycopy(mValues, 0, localObject, 0, mValues.length);
      mKeys = arrayOfLong;
      mValues = ((Object[])localObject);
    }
    if (mSize - i != 0)
    {
      arrayOfLong = mKeys;
      localObject = mKeys;
      j = i + 1;
      System.arraycopy(arrayOfLong, i, localObject, j, mSize - i);
      System.arraycopy(mValues, i, mValues, j, mSize - i);
    }
    mKeys[i] = paramLong;
    mValues[i] = paramObject;
    mSize += 1;
  }
  
  public void putAll(LongSparseArray paramLongSparseArray)
  {
    int j = paramLongSparseArray.size();
    int i = 0;
    while (i < j)
    {
      put(paramLongSparseArray.keyAt(i), paramLongSparseArray.valueAt(i));
      i += 1;
    }
  }
  
  public Object putIfAbsent(long paramLong, Object paramObject)
  {
    Object localObject = get(paramLong);
    if (localObject == null) {
      put(paramLong, paramObject);
    }
    return localObject;
  }
  
  public void remove(long paramLong)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    if ((i >= 0) && (mValues[i] != DELETED))
    {
      mValues[i] = DELETED;
      mGarbage = true;
    }
  }
  
  public boolean remove(long paramLong, Object paramObject)
  {
    int i = indexOfKey(paramLong);
    if (i >= 0)
    {
      Object localObject = valueAt(i);
      if ((paramObject == localObject) || ((paramObject != null) && (paramObject.equals(localObject))))
      {
        removeAt(i);
        return true;
      }
    }
    return false;
  }
  
  public void removeAt(int paramInt)
  {
    if (mValues[paramInt] != DELETED)
    {
      mValues[paramInt] = DELETED;
      mGarbage = true;
    }
  }
  
  public Object replace(long paramLong, Object paramObject)
  {
    int i = indexOfKey(paramLong);
    if (i >= 0)
    {
      Object localObject = mValues[i];
      mValues[i] = paramObject;
      return localObject;
    }
    return null;
  }
  
  public boolean replace(long paramLong, Object paramObject1, Object paramObject2)
  {
    int i = indexOfKey(paramLong);
    if (i >= 0)
    {
      Object localObject = mValues[i];
      if ((localObject == paramObject1) || ((paramObject1 != null) && (paramObject1.equals(localObject))))
      {
        mValues[i] = paramObject2;
        return true;
      }
    }
    return false;
  }
  
  public void setValueAt(int paramInt, Object paramObject)
  {
    if (mGarbage) {
      gc();
    }
    mValues[paramInt] = paramObject;
  }
  
  public int size()
  {
    if (mGarbage) {
      gc();
    }
    return mSize;
  }
  
  public String toString()
  {
    if (size() <= 0) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder(mSize * 28);
    localStringBuilder.append('{');
    int i = 0;
    while (i < mSize)
    {
      if (i > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(keyAt(i));
      localStringBuilder.append('=');
      Object localObject = valueAt(i);
      if (localObject != this) {
        localStringBuilder.append(localObject);
      } else {
        localStringBuilder.append("(this Map)");
      }
      i += 1;
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public Object valueAt(int paramInt)
  {
    if (mGarbage) {
      gc();
    }
    return mValues[paramInt];
  }
}
