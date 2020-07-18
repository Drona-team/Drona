package com.google.android.exoplayer2.upgrade;

import java.util.UUID;

public abstract interface MediaDrmCallback
{
  public abstract byte[] executeKeyRequest(UUID paramUUID, ExoMediaDrm.KeyRequest paramKeyRequest)
    throws Exception;
  
  public abstract byte[] executeProvisionRequest(UUID paramUUID, ExoMediaDrm.ProvisionRequest paramProvisionRequest)
    throws Exception;
}
