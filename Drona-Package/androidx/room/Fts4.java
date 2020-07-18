package androidx.room;

import androidx.annotation.RequiresApi;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({java.lang.annotation.ElementType.TYPE})
@RequiresApi(16)
public @interface Fts4
{
  Class contentEntity();
  
  String languageId();
  
  FtsOptions.MatchInfo matchInfo();
  
  String[] notIndexed();
  
  FtsOptions.Order order();
  
  int[] prefix();
  
  String tokenizer();
  
  String[] tokenizerArgs();
}
