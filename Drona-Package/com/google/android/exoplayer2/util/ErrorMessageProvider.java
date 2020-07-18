package com.google.android.exoplayer2.util;

import android.util.Pair;

public abstract interface ErrorMessageProvider<T extends Throwable>
{
  public abstract Pair getErrorMessage(Throwable paramThrowable);
}
