package com.google.android.gms.common.package_6.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;

final class DatabaseManager
  implements zabt
{
  private DatabaseManager(SocketIOClient paramSocketIOClient) {}
  
  public final void removeAccount(int paramInt, boolean paramBoolean)
  {
    SocketIOClient.lock(zaeq).lock();
    try
    {
      boolean bool = SocketIOClient.connect(zaeq);
      if (bool)
      {
        SocketIOClient.disconnect(zaeq, false);
        SocketIOClient.disconnect(zaeq, paramInt, paramBoolean);
        SocketIOClient.lock(zaeq).unlock();
        return;
      }
      SocketIOClient.disconnect(zaeq, true);
      SocketIOClient.access$getMHandler(zaeq).onConnectionSuspended(paramInt);
      SocketIOClient.lock(zaeq).unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      SocketIOClient.lock(zaeq).unlock();
      throw localThrowable;
    }
  }
  
  public final void removeAccount(Bundle paramBundle)
  {
    SocketIOClient.lock(zaeq).lock();
    try
    {
      SocketIOClient.addChild(zaeq, ConnectionResult.RESULT_SUCCESS);
      SocketIOClient.disconnect(zaeq);
      SocketIOClient.lock(zaeq).unlock();
      return;
    }
    catch (Throwable paramBundle)
    {
      SocketIOClient.lock(zaeq).unlock();
      throw paramBundle;
    }
  }
  
  public final void removeAccount(ConnectionResult paramConnectionResult)
  {
    SocketIOClient.lock(zaeq).lock();
    try
    {
      SocketIOClient.addChild(zaeq, paramConnectionResult);
      SocketIOClient.disconnect(zaeq);
      SocketIOClient.lock(zaeq).unlock();
      return;
    }
    catch (Throwable paramConnectionResult)
    {
      SocketIOClient.lock(zaeq).unlock();
      throw paramConnectionResult;
    }
  }
}
