package androidx.room;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public @interface ForeignKey
{
  public static final int CASCADE = 5;
  public static final int NO_ACTION = 1;
  public static final int RESTRICT = 2;
  public static final int SET_DEFAULT = 4;
  public static final int SET_NULL = 3;
  
  String[] childColumns();
  
  boolean deferred();
  
  Class entity();
  
  int onDelete();
  
  int onUpdate();
  
  String[] parentColumns();
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Action {}
}
