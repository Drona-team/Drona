package com.airbnb.lottie.model;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyPath
{
  private final List<String> keys;
  @Nullable
  private KeyPathElement resolvedElement;
  
  private KeyPath(KeyPath paramKeyPath)
  {
    keys = new ArrayList(keys);
    resolvedElement = resolvedElement;
  }
  
  public KeyPath(String... paramVarArgs)
  {
    keys = Arrays.asList(paramVarArgs);
  }
  
  private boolean endsWithGlobstar()
  {
    return ((String)keys.get(keys.size() - 1)).equals("**");
  }
  
  private boolean isContainer(String paramString)
  {
    return "__container".equals(paramString);
  }
  
  public KeyPath addKey(String paramString)
  {
    KeyPath localKeyPath = new KeyPath(this);
    keys.add(paramString);
    return localKeyPath;
  }
  
  public boolean fullyResolvesTo(String paramString, int paramInt)
  {
    if (paramInt >= keys.size()) {
      return false;
    }
    int i;
    if (paramInt == keys.size() - 1) {
      i = 1;
    } else {
      i = 0;
    }
    String str = (String)keys.get(paramInt);
    int j;
    if (!str.equals("**"))
    {
      if ((!str.equals(paramString)) && (!str.equals("*"))) {
        j = 0;
      } else {
        j = 1;
      }
      if (((i != 0) || ((paramInt == keys.size() - 2) && (endsWithGlobstar()))) && (j != 0)) {
        return true;
      }
    }
    else
    {
      if ((i == 0) && (((String)keys.get(paramInt + 1)).equals(paramString))) {
        j = 1;
      } else {
        j = 0;
      }
      if (j != 0)
      {
        if ((paramInt == keys.size() - 2) || ((paramInt == keys.size() - 3) && (endsWithGlobstar()))) {
          return true;
        }
      }
      else
      {
        if (i != 0) {
          return true;
        }
        paramInt += 1;
        if (paramInt < keys.size() - 1) {
          return false;
        }
        return ((String)keys.get(paramInt)).equals(paramString);
      }
    }
    return false;
  }
  
  public KeyPathElement getResolvedElement()
  {
    return resolvedElement;
  }
  
  public int incrementDepthBy(String paramString, int paramInt)
  {
    if (isContainer(paramString)) {
      return 0;
    }
    if (!((String)keys.get(paramInt)).equals("**")) {
      return 1;
    }
    if (paramInt == keys.size() - 1) {
      return 0;
    }
    if (((String)keys.get(paramInt + 1)).equals(paramString)) {
      return 2;
    }
    return 0;
  }
  
  public String keysToString()
  {
    return keys.toString();
  }
  
  public boolean matches(String paramString, int paramInt)
  {
    if (isContainer(paramString)) {
      return true;
    }
    if (paramInt >= keys.size()) {
      return false;
    }
    if ((!((String)keys.get(paramInt)).equals(paramString)) && (!((String)keys.get(paramInt)).equals("**"))) {
      return ((String)keys.get(paramInt)).equals("*");
    }
    return true;
  }
  
  public boolean propagateToChildren(String paramString, int paramInt)
  {
    if ("__container".equals(paramString)) {
      return true;
    }
    if (paramInt >= keys.size() - 1) {
      return ((String)keys.get(paramInt)).equals("**");
    }
    return true;
  }
  
  public KeyPath resolve(KeyPathElement paramKeyPathElement)
  {
    KeyPath localKeyPath = new KeyPath(this);
    resolvedElement = paramKeyPathElement;
    return localKeyPath;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("KeyPath{keys=");
    localStringBuilder.append(keys);
    localStringBuilder.append(",resolved=");
    boolean bool;
    if (resolvedElement != null) {
      bool = true;
    } else {
      bool = false;
    }
    localStringBuilder.append(bool);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}