package androidx.room;

import java.lang.annotation.Annotation;

public @interface Update
{
  int onConflict();
}
