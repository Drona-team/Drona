package androidx.room;

import androidx.annotation.RequiresApi;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.TYPE})
@RequiresApi(16)
public @interface Fts3
{
  String tokenizer();
  
  String[] tokenizerArgs();
}
