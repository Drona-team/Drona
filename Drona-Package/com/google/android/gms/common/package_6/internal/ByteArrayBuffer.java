package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.internal.zac;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class ByteArrayBuffer<ResultT>
  extends zac
{
  private final TaskCompletionSource<ResultT> zacn;
  private final com.google.android.gms.common.api.internal.TaskApiCall<Api.AnyClient, ResultT> zacr;
  private final StatusExceptionMapper zacs;
  
  public ByteArrayBuffer(int paramInt, TaskApiCall paramTaskApiCall, TaskCompletionSource paramTaskCompletionSource, StatusExceptionMapper paramStatusExceptionMapper)
  {
    super(paramInt);
    zacn = paramTaskCompletionSource;
    zacr = paramTaskApiCall;
    zacs = paramStatusExceptionMapper;
  }
  
  public final boolean isEmpty(GoogleApiManager.zaa paramZaa)
  {
    return zacr.shouldAutoResolveMissingFeatures();
  }
  
  /* Error */
  public final void readFrom(GoogleApiManager.zaa paramZaa)
    throws android.os.DeadObjectException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 22	com/google/android/gms/common/package_6/internal/ByteArrayBuffer:zacr	Lcom/google/android/gms/common/package_6/internal/TaskApiCall;
    //   4: astore_2
    //   5: aload_1
    //   6: invokevirtual 47	com/google/android/gms/common/package_6/internal/GoogleApiManager$zaa:zaab	()Lcom/google/android/gms/common/package_6/Api$Client;
    //   9: astore_1
    //   10: aload_0
    //   11: getfield 20	com/google/android/gms/common/package_6/internal/ByteArrayBuffer:zacn	Lcom/google/android/gms/tasks/TaskCompletionSource;
    //   14: astore_3
    //   15: aload_2
    //   16: aload_1
    //   17: aload_3
    //   18: invokevirtual 51	com/google/android/gms/common/package_6/internal/TaskApiCall:doExecute	(Lcom/google/android/gms/common/package_6/Api$AnyClient;Lcom/google/android/gms/tasks/TaskCompletionSource;)V
    //   21: return
    //   22: astore_1
    //   23: aload_0
    //   24: aload_1
    //   25: invokevirtual 57	com/google/android/gms/common/package_6/internal/Location:toString	(Ljava/lang/RuntimeException;)V
    //   28: return
    //   29: astore_1
    //   30: aload_0
    //   31: aload_1
    //   32: invokestatic 61	com/google/android/gms/common/package_6/internal/Location:call	(Landroid/os/RemoteException;)Lcom/google/android/gms/common/package_6/Status;
    //   35: invokevirtual 64	com/google/android/gms/common/package_6/internal/Location:toString	(Lcom/google/android/gms/common/package_6/Status;)V
    //   38: return
    //   39: astore_1
    //   40: aload_1
    //   41: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	ByteArrayBuffer
    //   0	42	1	paramZaa	GoogleApiManager.zaa
    //   4	12	2	localTaskApiCall	TaskApiCall
    //   14	4	3	localTaskCompletionSource	TaskCompletionSource
    // Exception table:
    //   from	to	target	type
    //   0	5	22	java/lang/RuntimeException
    //   5	10	22	java/lang/RuntimeException
    //   15	21	22	java/lang/RuntimeException
    //   5	10	29	android/os/RemoteException
    //   15	21	29	android/os/RemoteException
    //   5	10	39	android/os/DeadObjectException
    //   15	21	39	android/os/DeadObjectException
  }
  
  public final void readFrom(zaab paramZaab, boolean paramBoolean)
  {
    paramZaab.setPriority(zacn, paramBoolean);
  }
  
  public final void toString(Status paramStatus)
  {
    zacn.trySetException(zacs.getException(paramStatus));
  }
  
  public final void toString(RuntimeException paramRuntimeException)
  {
    zacn.trySetException(paramRuntimeException);
  }
  
  public final Feature[] toString(GoogleApiManager.zaa paramZaa)
  {
    return zacr.zabt();
  }
}
