package com.google.android.gms.flags;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collection;

public class FlagRegistry
{
  private final Collection<Flag> children = new ArrayList();
  private final Collection<Flag.StringFlag> feeds = new ArrayList();
  private final Collection<Flag.StringFlag> tags = new ArrayList();
  
  public FlagRegistry() {}
  
  public static void initialize(Context paramContext)
  {
    Singletons.Instance().initialize(paramContext);
  }
  
  public final void scan(Flag paramFlag)
  {
    children.add(paramFlag);
  }
}
