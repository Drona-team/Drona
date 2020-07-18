package com.google.android.gms.common.package_6.internal;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.internal.base.zap;

final class zaco
  extends zap
{
  public zaco(zacm paramZacm, Looper paramLooper)
  {
    super(paramLooper);
  }
  
  public final void handleMessage(Message paramMessage)
  {
    switch (what)
    {
    default: 
      int i = what;
      paramMessage = new StringBuilder(70);
      paramMessage.append("TransformationResultHandler received unknown message type: ");
      paramMessage.append(i);
      Log.e("TransformedResultImpl", paramMessage.toString());
      return;
    case 1: 
      localObject = (RuntimeException)obj;
      paramMessage = String.valueOf(((Exception)localObject).getMessage());
      if (paramMessage.length() != 0) {
        paramMessage = "Runtime exception on the transformation worker thread: ".concat(paramMessage);
      } else {
        paramMessage = new String("Runtime exception on the transformation worker thread: ");
      }
      Log.e("TransformedResultImpl", paramMessage);
      throw ((Throwable)localObject);
    }
    Object localObject = (PendingResult)obj;
    paramMessage = zacm.getBuffers(zakw);
    if (localObject == null) {}
    try
    {
      zacm.status(zacm.loadView(zakw), new Status(13, "Transform returned null"));
      break label210;
      if ((localObject instanceof zacd)) {
        zacm.status(zacm.loadView(zakw), ((zacd)localObject).getStatus());
      } else {
        zacm.loadView(zakw).onResultReceived((PendingResult)localObject);
      }
      label210:
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
    throw ((Throwable)localObject);
  }
}
