package androidx.lifecycle;

public class MutableLiveData<T>
  extends LiveData<T>
{
  public MutableLiveData() {}
  
  public MutableLiveData(Object paramObject)
  {
    super(paramObject);
  }
  
  public void postValue(Object paramObject)
  {
    super.postValue(paramObject);
  }
  
  public void setValue(Object paramObject)
  {
    super.setValue(paramObject);
  }
}
