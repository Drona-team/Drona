package androidx.core.opml;

import android.os.Build.VERSION;
import androidx.annotation.NonNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

final class LocaleListCompatWrapper
  implements LocaleListInterface
{
  private static final Locale EN_LATN = LocaleListCompat.forLanguageTagCompat("en-Latn");
  private static final Locale LOCALE_AR_XB;
  private static final Locale LOCALE_EN_XA;
  private static final Locale[] sEmptyList = new Locale[0];
  private final Locale[] mList;
  @NonNull
  private final String mStringRepresentation;
  
  static
  {
    LOCALE_EN_XA = new Locale("en", "XA");
    LOCALE_AR_XB = new Locale("ar", "XB");
  }
  
  LocaleListCompatWrapper(Locale... paramVarArgs)
  {
    if (paramVarArgs.length == 0)
    {
      mList = sEmptyList;
      mStringRepresentation = "";
      return;
    }
    Locale[] arrayOfLocale = new Locale[paramVarArgs.length];
    HashSet localHashSet = new HashSet();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramVarArgs.length)
    {
      Locale localLocale = paramVarArgs[i];
      if (localLocale != null)
      {
        if (!localHashSet.contains(localLocale))
        {
          localLocale = (Locale)localLocale.clone();
          arrayOfLocale[i] = localLocale;
          toLanguageTag(localStringBuilder, localLocale);
          if (i < paramVarArgs.length - 1) {
            localStringBuilder.append(',');
          }
          localHashSet.add(localLocale);
          i += 1;
        }
        else
        {
          paramVarArgs = new StringBuilder();
          paramVarArgs.append("list[");
          paramVarArgs.append(i);
          paramVarArgs.append("] is a repetition");
          throw new IllegalArgumentException(paramVarArgs.toString());
        }
      }
      else
      {
        paramVarArgs = new StringBuilder();
        paramVarArgs.append("list[");
        paramVarArgs.append(i);
        paramVarArgs.append("] is null");
        throw new NullPointerException(paramVarArgs.toString());
      }
    }
    mList = arrayOfLocale;
    mStringRepresentation = localStringBuilder.toString();
  }
  
  private Locale computeFirstMatch(Collection paramCollection, boolean paramBoolean)
  {
    int i = computeFirstMatchIndex(paramCollection, paramBoolean);
    if (i == -1) {
      return null;
    }
    return mList[i];
  }
  
  private int computeFirstMatchIndex(Collection paramCollection, boolean paramBoolean)
  {
    if (mList.length == 1) {
      return 0;
    }
    if (mList.length == 0) {
      return -1;
    }
    int j;
    int i;
    if (paramBoolean)
    {
      j = findFirstMatchIndex(EN_LATN);
      i = j;
      if (j == 0) {
        return 0;
      }
      if (j < Integer.MAX_VALUE) {}
    }
    else
    {
      i = Integer.MAX_VALUE;
    }
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      j = findFirstMatchIndex(LocaleListCompat.forLanguageTagCompat((String)paramCollection.next()));
      if (j == 0) {
        return 0;
      }
      if (j < i) {
        i = j;
      }
    }
    if (i == Integer.MAX_VALUE) {
      return 0;
    }
    return i;
  }
  
  private int findFirstMatchIndex(Locale paramLocale)
  {
    int i = 0;
    while (i < mList.length)
    {
      if (matchScore(paramLocale, mList[i]) > 0) {
        return i;
      }
      i += 1;
    }
    return Integer.MAX_VALUE;
  }
  
  private static String getLikelyScript(Locale paramLocale)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramLocale = paramLocale.getScript();
      if (!paramLocale.isEmpty()) {
        return paramLocale;
      }
      return "";
    }
    return "";
  }
  
  private static boolean isPseudoLocale(Locale paramLocale)
  {
    return (LOCALE_EN_XA.equals(paramLocale)) || (LOCALE_AR_XB.equals(paramLocale));
  }
  
  private static int matchScore(Locale paramLocale1, Locale paramLocale2)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:659)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  static void toLanguageTag(StringBuilder paramStringBuilder, Locale paramLocale)
  {
    paramStringBuilder.append(paramLocale.getLanguage());
    String str = paramLocale.getCountry();
    if ((str != null) && (!str.isEmpty()))
    {
      paramStringBuilder.append('-');
      paramStringBuilder.append(paramLocale.getCountry());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof LocaleListCompatWrapper)) {
      return false;
    }
    paramObject = mList;
    if (mList.length != paramObject.length) {
      return false;
    }
    int i = 0;
    while (i < mList.length)
    {
      if (!mList[i].equals(paramObject[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public Locale getFirstMatch(String[] paramArrayOfString)
  {
    return computeFirstMatch(Arrays.asList(paramArrayOfString), false);
  }
  
  public Locale getList(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < mList.length)) {
      return mList[paramInt];
    }
    return null;
  }
  
  public Object getLocaleList()
  {
    return null;
  }
  
  public int hashCode()
  {
    int j = 1;
    int i = 0;
    while (i < mList.length)
    {
      j = j * 31 + mList[i].hashCode();
      i += 1;
    }
    return j;
  }
  
  public int indexOf(Locale paramLocale)
  {
    int i = 0;
    while (i < mList.length)
    {
      if (mList[i].equals(paramLocale)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public boolean isEmpty()
  {
    return mList.length == 0;
  }
  
  public int size()
  {
    return mList.length;
  }
  
  public String toLanguageTags()
  {
    return mStringRepresentation;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    int i = 0;
    while (i < mList.length)
    {
      localStringBuilder.append(mList[i]);
      if (i < mList.length - 1) {
        localStringBuilder.append(',');
      }
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}
