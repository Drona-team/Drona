package com.android.installreferrer.opt;

import android.content.Context;
import android.os.RemoteException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class InstallReferrerClient
{
  public InstallReferrerClient() {}
  
  public static Builder newBuilder(Context paramContext)
  {
    return new Builder(null);
  }
  
  public abstract void endConnection();
  
  public abstract ReferrerDetails getInstallReferrer()
    throws RemoteException;
  
  public abstract boolean isReady();
  
  public abstract void startConnection(InstallReferrerStateListener paramInstallReferrerStateListener);
  
  public final class Builder
  {
    private Builder() {}
    
    public InstallReferrerClient build()
    {
      Context localContext = InstallReferrerClient.this;
      if (localContext != null) {
        return new InstallReferrerClientImpl(localContext);
      }
      throw new IllegalArgumentException("Please provide a valid Context.");
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public @interface InstallReferrerResponse
  {
    public static final int DEVELOPER_ERROR = 3;
    public static final int FEATURE_NOT_SUPPORTED = 2;
    public static final int SERVICE_DISCONNECTED = -1;
    public static final int SERVICE_UNAVAILABLE = 1;
    public static final int VIEW_LIST = 0;
  }
}
