package com.google.android.gms.package_7;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class Dictionary<T>
{
  final int what;
  final int zzcp;
  final TaskCompletionSource<T> zzcq = new TaskCompletionSource();
  final Bundle zzcr;
  
  Dictionary(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    zzcp = paramInt1;
    what = paramInt2;
    zzcr = paramBundle;
  }
  
  abstract void add(Bundle paramBundle);
  
  abstract boolean delete();
  
  public String toString()
  {
    int i = what;
    int j = zzcp;
    delete();
    StringBuilder localStringBuilder = new StringBuilder(55);
    localStringBuilder.append("Request { what=");
    localStringBuilder.append(i);
    localStringBuilder.append(" id=");
    localStringBuilder.append(j);
    localStringBuilder.append(" oneWay=false}");
    return localStringBuilder.toString();
  }
  
  final void write(zzaa paramZzaa)
  {
    if (Log.isLoggable("MessengerIpcClient", 3))
    {
      String str1 = String.valueOf(this);
      String str2 = String.valueOf(paramZzaa);
      StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str1).length() + 14 + String.valueOf(str2).length());
      localStringBuilder.append("Failing ");
      localStringBuilder.append(str1);
      localStringBuilder.append(" with ");
      localStringBuilder.append(str2);
      Log.d("MessengerIpcClient", localStringBuilder.toString());
    }
    zzcq.setException(paramZzaa);
  }
}
