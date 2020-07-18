package com.google.android.gms.common.util;

import android.text.TextUtils;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.Scope;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@KeepForSdk
public final class ScopeUtil
{
  private ScopeUtil() {}
  
  public static Set fromScopeString(Collection paramCollection)
  {
    Preconditions.checkNotNull(paramCollection, "scopeStrings can't be null.");
    return fromScopeString((String[])paramCollection.toArray(new String[paramCollection.size()]));
  }
  
  public static Set fromScopeString(String... paramVarArgs)
  {
    Preconditions.checkNotNull(paramVarArgs, "scopeStrings can't be null.");
    HashSet localHashSet = new HashSet(paramVarArgs.length);
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      String str = paramVarArgs[i];
      if (!TextUtils.isEmpty(str)) {
        localHashSet.add(new Scope(str));
      }
      i += 1;
    }
    return localHashSet;
  }
  
  public static String[] toScopeString(Set paramSet)
  {
    Preconditions.checkNotNull(paramSet, "scopes can't be null.");
    return toScopeString((Scope[])paramSet.toArray(new Scope[paramSet.size()]));
  }
  
  public static String[] toScopeString(Scope[] paramArrayOfScope)
  {
    Preconditions.checkNotNull(paramArrayOfScope, "scopes can't be null.");
    String[] arrayOfString = new String[paramArrayOfScope.length];
    int i = 0;
    while (i < paramArrayOfScope.length)
    {
      arrayOfString[i] = paramArrayOfScope[i].getScopeUri();
      i += 1;
    }
    return arrayOfString;
  }
}
