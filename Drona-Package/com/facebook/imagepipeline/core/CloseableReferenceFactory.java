package com.facebook.imagepipeline.core;

import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.CloseableReference.LeakHandler;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.common.references.SharedReference;
import com.facebook.imagepipeline.debug.CloseableReferenceLeakTracker;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CloseableReferenceFactory
{
  private final CloseableReference.LeakHandler mLeakHandler;
  
  public CloseableReferenceFactory(final CloseableReferenceLeakTracker paramCloseableReferenceLeakTracker)
  {
    mLeakHandler = new CloseableReference.LeakHandler()
    {
      public void reportLeak(SharedReference paramAnonymousSharedReference, Throwable paramAnonymousThrowable)
      {
        paramCloseableReferenceLeakTracker.trackCloseableReferenceLeak(paramAnonymousSharedReference, paramAnonymousThrowable);
        FLog.w("Fresco", "Finalized without closing: %x %x (type = %s).\nStack:\n%s", new Object[] { Integer.valueOf(System.identityHashCode(this)), Integer.valueOf(System.identityHashCode(paramAnonymousSharedReference)), paramAnonymousSharedReference.getValue().getClass().getName(), CloseableReferenceFactory.getStackTraceString(paramAnonymousThrowable) });
      }
      
      public boolean requiresStacktrace()
      {
        return paramCloseableReferenceLeakTracker.isSet();
      }
    };
  }
  
  private static String getStackTraceString(Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return "";
    }
    StringWriter localStringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(localStringWriter));
    return localStringWriter.toString();
  }
  
  public CloseableReference create(Closeable paramCloseable)
  {
    return CloseableReference.loadClass(paramCloseable, mLeakHandler);
  }
  
  public CloseableReference create(Object paramObject, ResourceReleaser paramResourceReleaser)
  {
    return CloseableReference.loadClass(paramObject, paramResourceReleaser, mLeakHandler);
  }
}