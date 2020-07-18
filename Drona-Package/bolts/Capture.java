package bolts;

public class Capture<T>
{
  private T value;
  
  public Capture() {}
  
  public Capture(Object paramObject)
  {
    value = paramObject;
  }
  
  public void execute(Object paramObject)
  {
    value = paramObject;
  }
  
  public Object get()
  {
    return value;
  }
}
