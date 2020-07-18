package com.google.android.gms.common.package_6.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.BiConsumer;
import com.google.android.gms.tasks.TaskCompletionSource;

@KeepForSdk
public abstract class TaskApiCall<A extends com.google.android.gms.common.api.Api.AnyClient, ResultT>
{
  private final Feature[] zake;
  private final boolean zakl;
  
  public TaskApiCall()
  {
    zake = null;
    zakl = false;
  }
  
  private TaskApiCall(Feature[] paramArrayOfFeature, boolean paramBoolean)
  {
    zake = paramArrayOfFeature;
    zakl = paramBoolean;
  }
  
  public static Builder builder()
  {
    return new Builder(null);
  }
  
  protected abstract void doExecute(com.google.android.gms.common.package_6.Api.AnyClient paramAnyClient, TaskCompletionSource paramTaskCompletionSource)
    throws RemoteException;
  
  public boolean shouldAutoResolveMissingFeatures()
  {
    return zakl;
  }
  
  public final Feature[] zabt()
  {
    return zake;
  }
  
  @KeepForSdk
  public class Builder<A extends com.google.android.gms.common.api.Api.AnyClient, ResultT>
  {
    private Feature[] zake;
    private boolean zakl = true;
    private com.google.android.gms.common.api.internal.RemoteCall<A, TaskCompletionSource<ResultT>> zakm;
    
    private Builder() {}
    
    public TaskApiCall build()
    {
      boolean bool;
      if (zakm != null) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "execute parameter required");
      return new zack(this, zake, zakl);
    }
    
    public Builder execute(BiConsumer paramBiConsumer)
    {
      zakm = new zacj(paramBiConsumer);
      return this;
    }
    
    public Builder setAutoResolveMissingFeatures(boolean paramBoolean)
    {
      zakl = paramBoolean;
      return this;
    }
    
    public Builder setFeatures(Feature... paramVarArgs)
    {
      zake = paramVarArgs;
      return this;
    }
    
    public Builder xor(RemoteCall paramRemoteCall)
    {
      zakm = paramRemoteCall;
      return this;
    }
  }
}
