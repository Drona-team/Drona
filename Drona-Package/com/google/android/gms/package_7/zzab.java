package com.google.android.gms.package_7;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.iid.zzz;
import com.google.android.gms.tasks.TaskCompletionSource;

final class zzab
  extends zzz<Bundle>
{
  zzab(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    super(paramInt1, 1, paramBundle);
  }
  
  final void add(Bundle paramBundle)
  {
    Object localObject = paramBundle.getBundle("data");
    paramBundle = (Bundle)localObject;
    if (localObject == null) {
      paramBundle = Bundle.EMPTY;
    }
    if (Log.isLoggable("MessengerIpcClient", 3))
    {
      localObject = String.valueOf(this);
      String str = String.valueOf(paramBundle);
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject).length() + 16 + String.valueOf(str).length());
      localStringBuilder.append("Finishing ");
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(" with ");
      localStringBuilder.append(str);
      Log.d("MessengerIpcClient", localStringBuilder.toString());
    }
    zzcq.setResult(paramBundle);
  }
  
  final boolean delete()
  {
    return false;
  }
}
