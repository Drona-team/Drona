package client.testing.util;

public class StringUtils
{
  public StringUtils() {}
  
  public static boolean isEmpty(CharSequence paramCharSequence)
  {
    return (paramCharSequence == null) || (paramCharSequence.length() == 0);
  }
}
