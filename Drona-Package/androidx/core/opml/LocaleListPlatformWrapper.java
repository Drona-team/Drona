package androidx.core.opml;

import android.os.LocaleList;
import androidx.annotation.RequiresApi;
import java.util.Locale;

@RequiresApi(24)
final class LocaleListPlatformWrapper
  implements LocaleListInterface
{
  private final LocaleList mLocaleList;
  
  LocaleListPlatformWrapper(LocaleList paramLocaleList)
  {
    mLocaleList = paramLocaleList;
  }
  
  public boolean equals(Object paramObject)
  {
    return mLocaleList.equals(((LocaleListInterface)paramObject).getLocaleList());
  }
  
  public Locale getFirstMatch(String[] paramArrayOfString)
  {
    return mLocaleList.getFirstMatch(paramArrayOfString);
  }
  
  public Locale getList(int paramInt)
  {
    return mLocaleList.get(paramInt);
  }
  
  public Object getLocaleList()
  {
    return mLocaleList;
  }
  
  public int hashCode()
  {
    return mLocaleList.hashCode();
  }
  
  public int indexOf(Locale paramLocale)
  {
    return mLocaleList.indexOf(paramLocale);
  }
  
  public boolean isEmpty()
  {
    return mLocaleList.isEmpty();
  }
  
  public int size()
  {
    return mLocaleList.size();
  }
  
  public String toLanguageTags()
  {
    return mLocaleList.toLanguageTags();
  }
  
  public String toString()
  {
    return mLocaleList.toString();
  }
}
