package com.google.android.exoplayer2;

import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ExoPlaybackException
  extends Exception
{
  public static final int TYPE_RENDERER = 1;
  public static final int TYPE_SOURCE = 0;
  public static final int TYPE_UNEXPECTED = 2;
  private final Throwable cause;
  public final int rendererIndex;
  public final int type;
  
  private ExoPlaybackException(int paramInt1, Throwable paramThrowable, int paramInt2)
  {
    super(paramThrowable);
    type = paramInt1;
    cause = paramThrowable;
    rendererIndex = paramInt2;
  }
  
  public static ExoPlaybackException createForRenderer(Exception paramException, int paramInt)
  {
    return new ExoPlaybackException(1, paramException, paramInt);
  }
  
  public static ExoPlaybackException createForSource(IOException paramIOException)
  {
    return new ExoPlaybackException(0, paramIOException, -1);
  }
  
  static ExoPlaybackException createForUnexpected(RuntimeException paramRuntimeException)
  {
    return new ExoPlaybackException(2, paramRuntimeException, -1);
  }
  
  public Exception getRendererException()
  {
    int i = type;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    Assertions.checkState(bool);
    return (Exception)cause;
  }
  
  public IOException getSourceException()
  {
    boolean bool;
    if (type == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    return (IOException)cause;
  }
  
  public RuntimeException getUnexpectedException()
  {
    boolean bool;
    if (type == 2) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    return (RuntimeException)cause;
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Type {}
}
