package com.bugsnag.android;

import android.app.ActivityManager;
import android.app.ActivityManager.ProcessErrorStateInfo;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(bv={1, 0, 3}, d1={"\000D\n\002\030\002\n\002\020\000\n\002\b\002\n\002\030\002\n\000\n\002\020\002\n\000\n\002\030\002\n\000\n\002\030\002\n\002\b\003\n\002\030\002\n\000\n\002\020\b\n\002\b\003\n\002\030\002\n\002\b\003\n\002\030\002\n\002\b\003\b\000\030\000 \0322\0020\001:\001\032B\005?\006\002\020\002J\035\020\005\032\0020\0062\006\020\007\032\0020\b2\006\020\t\032\0020\nH\000?\006\002\b\013J\037\020\f\032\004\030\0010\n2\006\020\r\032\0020\0162\006\020\017\032\0020\020H\001?\006\002\b\021J\027\020\022\032\004\030\0010\n2\006\020\023\032\0020\024H\000?\006\002\b\025J\035\020\026\032\0020\0062\006\020\027\032\0020\0302\006\020\007\032\0020\bH\000?\006\002\b\031R\016\020\003\032\0020\004X?\004?\006\002\n\000?\006\033"}, d2={"Lcom/bugsnag/android/AnrDetailsCollector;", "", "()V", "handlerThread", "Landroid/os/HandlerThread;", "addErrorStateInfo", "", "error", "Lcom/bugsnag/android/Error;", "anrState", "Landroid/app/ActivityManager$ProcessErrorStateInfo;", "addErrorStateInfo$bugsnag_plugin_android_anr_release", "captureProcessErrorState", "am", "Landroid/app/ActivityManager;", "pid", "", "captureProcessErrorState$bugsnag_plugin_android_anr_release", "collectAnrDetails", "ctx", "Landroid/content/Context;", "collectAnrDetails$bugsnag_plugin_android_anr_release", "collectAnrErrorDetails", "client", "Lcom/bugsnag/android/Client;", "collectAnrErrorDetails$bugsnag_plugin_android_anr_release", "Companion", "bugsnag-plugin-android-anr_release"}, k=1, mv={1, 1, 16})
public final class AnrDetailsCollector
{
  public static final Companion Companion = new Companion(null);
  private static final long INFO_POLL_THRESHOLD_MS = 100L;
  private static final int MAX_ATTEMPTS = 300;
  private final HandlerThread handlerThread = new HandlerThread("bugsnag-anr-collector");
  
  public AnrDetailsCollector()
  {
    handlerThread.start();
  }
  
  public final void addErrorStateInfo$bugsnag_plugin_android_anr_release(Error paramError, ActivityManager.ProcessErrorStateInfo paramProcessErrorStateInfo)
  {
    Intrinsics.checkParameterIsNotNull(paramError, "error");
    Intrinsics.checkParameterIsNotNull(paramProcessErrorStateInfo, "anrState");
    String str = shortMsg;
    Intrinsics.checkExpressionValueIsNotNull(str, "msg");
    paramProcessErrorStateInfo = str;
    if (StringsKt.startsWith$default(str, "ANR", false, 2, null)) {
      paramProcessErrorStateInfo = StringsKt.replaceFirst$default(str, "ANR", "", false, 4, null);
    }
    paramError.setExceptionMessage(paramProcessErrorStateInfo);
  }
  
  public final ActivityManager.ProcessErrorStateInfo captureProcessErrorState$bugsnag_plugin_android_anr_release(ActivityManager paramActivityManager, int paramInt)
  {
    Intrinsics.checkParameterIsNotNull(paramActivityManager, "am");
    try
    {
      Object localObject = paramActivityManager.getProcessesInErrorState();
      paramActivityManager = (ActivityManager)localObject;
      if (localObject == null) {
        paramActivityManager = CollectionsKt.emptyList();
      }
      paramActivityManager = (Iterable)paramActivityManager;
      Iterator localIterator = paramActivityManager.iterator();
      int i;
      do
      {
        boolean bool = localIterator.hasNext();
        if (!bool) {
          break;
        }
        localObject = localIterator.next();
        paramActivityManager = (ActivityManager)localObject;
        i = pid;
        if (i == paramInt) {
          i = 1;
        } else {
          i = 0;
        }
      } while (i == 0);
      break label96;
      paramActivityManager = null;
      label96:
      paramActivityManager = (ActivityManager.ProcessErrorStateInfo)paramActivityManager;
      return paramActivityManager;
    }
    catch (RuntimeException paramActivityManager)
    {
      for (;;) {}
    }
    return null;
  }
  
  public final ActivityManager.ProcessErrorStateInfo collectAnrDetails$bugsnag_plugin_android_anr_release(Context paramContext)
  {
    Intrinsics.checkParameterIsNotNull(paramContext, "ctx");
    paramContext = paramContext.getSystemService("activity");
    if (paramContext != null) {
      return captureProcessErrorState$bugsnag_plugin_android_anr_release((ActivityManager)paramContext, Process.myPid());
    }
    throw ((Throwable)new TypeCastException("null cannot be cast to non-null type android.app.ActivityManager"));
  }
  
  public final void collectAnrErrorDetails$bugsnag_plugin_android_anr_release(final Client paramClient, final Error paramError)
  {
    Intrinsics.checkParameterIsNotNull(paramClient, "client");
    Intrinsics.checkParameterIsNotNull(paramError, "error");
    final Handler localHandler = new Handler(handlerThread.getLooper());
    localHandler.post((Runnable)new Runnable()
    {
      public void run()
      {
        Object localObject = AnrDetailsCollector.this;
        Context localContext = paramClientappContext;
        Intrinsics.checkExpressionValueIsNotNull(localContext, "client.appContext");
        localObject = ((AnrDetailsCollector)localObject).collectAnrDetails$bugsnag_plugin_android_anr_release(localContext);
        if (localObject == null)
        {
          if ($attempts.getAndIncrement() < 300) {
            localHandler.postDelayed((Runnable)this, 100L);
          }
        }
        else
        {
          addErrorStateInfo$bugsnag_plugin_android_anr_release(paramError, (ActivityManager.ProcessErrorStateInfo)localObject);
          paramClient.notify(paramError, DeliveryStyle.ASYNC_WITH_CACHE, null);
        }
      }
    });
  }
  
  @Metadata(bv={1, 0, 3}, d1={"\000\030\n\002\030\002\n\002\020\000\n\002\b\002\n\002\020\t\n\000\n\002\020\b\n\000\b?\003\030\0002\0020\001B\007\b\002?\006\002\020\002R\016\020\003\032\0020\004X?T?\006\002\n\000R\016\020\005\032\0020\006X?T?\006\002\n\000?\006\007"}, d2={"Lcom/bugsnag/android/AnrDetailsCollector$Companion;", "", "()V", "INFO_POLL_THRESHOLD_MS", "", "MAX_ATTEMPTS", "", "bugsnag-plugin-android-anr_release"}, k=1, mv={1, 1, 16})
  public static final class Companion
  {
    private Companion() {}
  }
}
