package androidx.room;

import androidx.annotation.RequiresApi;

public class FtsOptions
{
  public static final String TOKENIZER_ICU = "icu";
  public static final String TOKENIZER_PORTER = "porter";
  public static final String TOKENIZER_SIMPLE = "simple";
  @RequiresApi(21)
  public static final String TOKENIZER_UNICODE61 = "unicode61";
  
  private FtsOptions() {}
  
  public static enum MatchInfo
  {
    FTS3,  FTS4;
  }
  
  public static enum Order
  {
    ASC,  DESC;
  }
}
