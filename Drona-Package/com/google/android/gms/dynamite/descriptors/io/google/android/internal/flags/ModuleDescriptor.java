package com.google.android.gms.dynamite.descriptors.io.google.android.internal.flags;

import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.common.util.RetainForClient;

@DynamiteApi
@RetainForClient
public class ModuleDescriptor
{
  @RetainForClient
  public static final String MODULE_ID = "com.google.android.gms.flags";
  @RetainForClient
  public static final int MODULE_VERSION = 3;
  
  public ModuleDescriptor() {}
}
