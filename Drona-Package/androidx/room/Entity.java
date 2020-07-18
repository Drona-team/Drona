package androidx.room;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface Entity
{
  ForeignKey[] foreignKeys();
  
  String[] ignoredColumns();
  
  Index[] indices();
  
  boolean inheritSuperIndices();
  
  String[] primaryKeys();
  
  String tableName();
}
