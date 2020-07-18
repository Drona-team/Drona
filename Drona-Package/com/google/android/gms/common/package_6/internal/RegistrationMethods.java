package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.Feature;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.BiConsumer;
import com.google.android.gms.tasks.TaskCompletionSource;

@KeepForSdk
public class RegistrationMethods<A extends Api.AnyClient, L>
{
  public final com.google.android.gms.common.api.internal.RegisterListenerMethod<A, L> zajz;
  public final com.google.android.gms.common.api.internal.UnregisterListenerMethod<A, L> zaka;
  
  private RegistrationMethods(RegisterListenerMethod paramRegisterListenerMethod, UnregisterListenerMethod paramUnregisterListenerMethod)
  {
    zajz = paramRegisterListenerMethod;
    zaka = paramUnregisterListenerMethod;
  }
  
  public static Builder builder()
  {
    return new Builder(null);
  }
  
  @KeepForSdk
  public class Builder<A extends Api.AnyClient, L>
  {
    private boolean zajw = true;
    private com.google.android.gms.common.api.internal.RemoteCall<A, TaskCompletionSource<Void>> zakb;
    private com.google.android.gms.common.api.internal.RemoteCall<A, TaskCompletionSource<Boolean>> zakc;
    private com.google.android.gms.common.api.internal.ListenerHolder<L> zakd;
    private Feature[] zake;
    
    private Builder() {}
    
    public RegistrationMethods build()
    {
      RemoteCall localRemoteCall = zakb;
      boolean bool2 = false;
      if (localRemoteCall != null) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      Preconditions.checkArgument(bool1, "Must set register function");
      if (zakc != null) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      Preconditions.checkArgument(bool1, "Must set unregister function");
      boolean bool1 = bool2;
      if (zakd != null) {
        bool1 = true;
      }
      Preconditions.checkArgument(bool1, "Must set holder");
      return new RegistrationMethods(new zaca(this, zakd, zake, zajw), new zacb(this, zakd.getListenerKey()), null);
    }
    
    public Builder register(RemoteCall paramRemoteCall)
    {
      zakb = paramRemoteCall;
      return this;
    }
    
    public Builder register(BiConsumer paramBiConsumer)
    {
      zakb = new zaby(paramBiConsumer);
      return this;
    }
    
    public Builder setAutoResolveMissingFeatures(boolean paramBoolean)
    {
      zajw = paramBoolean;
      return this;
    }
    
    public Builder setFeatures(Feature[] paramArrayOfFeature)
    {
      zake = paramArrayOfFeature;
      return this;
    }
    
    public Builder unregister(RemoteCall paramRemoteCall)
    {
      zakc = paramRemoteCall;
      return this;
    }
    
    public Builder unregister(BiConsumer paramBiConsumer)
    {
      zakb = new zabz(this);
      return this;
    }
    
    public Builder withHolder(ListenerHolder paramListenerHolder)
    {
      zakd = paramListenerHolder;
      return this;
    }
  }
}
