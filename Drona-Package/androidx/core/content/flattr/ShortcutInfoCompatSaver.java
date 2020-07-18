package androidx.core.content.flattr;

import androidx.annotation.RestrictTo;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract class ShortcutInfoCompatSaver<T>
{
  public ShortcutInfoCompatSaver() {}
  
  public abstract Object addShortcuts(List paramList);
  
  public List getShortcuts()
    throws Exception
  {
    return new ArrayList();
  }
  
  public abstract Object removeAllShortcuts();
  
  public abstract Object removeShortcuts(List paramList);
  
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public class NoopImpl
    extends androidx.core.content.pm.ShortcutInfoCompatSaver<Void>
  {
    public NoopImpl() {}
    
    public Void addShortcuts(List paramList)
    {
      return null;
    }
    
    public Void removeAllShortcuts()
    {
      return null;
    }
    
    public Void removeShortcuts(List paramList)
    {
      return null;
    }
  }
}
