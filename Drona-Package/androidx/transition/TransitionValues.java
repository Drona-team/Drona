package androidx.transition;

import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TransitionValues
{
  final ArrayList<Transition> mTargetedTransitions = new ArrayList();
  public final Map<String, Object> values = new HashMap();
  public View view;
  
  public TransitionValues() {}
  
  public TransitionValues(View paramView)
  {
    view = paramView;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof TransitionValues))
    {
      View localView = view;
      paramObject = (TransitionValues)paramObject;
      if ((localView == view) && (values.equals(values))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return view.hashCode() * 31 + values.hashCode();
  }
  
  public String toString()
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("TransitionValues@");
    ((StringBuilder)localObject1).append(Integer.toHexString(hashCode()));
    ((StringBuilder)localObject1).append(":\n");
    localObject1 = ((StringBuilder)localObject1).toString();
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("    view = ");
    ((StringBuilder)localObject2).append(view);
    ((StringBuilder)localObject2).append("\n");
    localObject1 = ((StringBuilder)localObject2).toString();
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("    values:");
    localObject1 = ((StringBuilder)localObject2).toString();
    localObject2 = values.keySet().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      String str = (String)((Iterator)localObject2).next();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append((String)localObject1);
      localStringBuilder.append("    ");
      localStringBuilder.append(str);
      localStringBuilder.append(": ");
      localStringBuilder.append(values.get(str));
      localStringBuilder.append("\n");
      localObject1 = localStringBuilder.toString();
    }
    return localObject1;
  }
}
