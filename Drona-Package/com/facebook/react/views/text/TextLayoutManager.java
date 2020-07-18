package com.facebook.react.views.text;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.LruCache;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.yoga.YogaMeasureMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextLayoutManager
{
  private static LruCache<String, Spannable> sSpannableCache = new LruCache(100);
  private static final Object sSpannableCacheLock;
  private static final TextPaint sTextPaintInstance = new TextPaint(1);
  private static final int spannableCacheSize = 100;
  
  static
  {
    sSpannableCacheLock = new Object();
  }
  
  public TextLayoutManager() {}
  
  private static void buildSpannableFromFragment(Context paramContext, ReadableArray paramReadableArray, SpannableStringBuilder paramSpannableStringBuilder, List paramList)
  {
    int j = paramReadableArray.size();
    int i = 0;
    while (i < j)
    {
      ReadableMap localReadableMap = paramReadableArray.getMap(i);
      int k = paramSpannableStringBuilder.length();
      TextAttributeProps localTextAttributeProps = new TextAttributeProps(new ReactStylesDiffMap(localReadableMap.getMap("textAttributes")));
      paramSpannableStringBuilder.append(TextTransform.apply(localReadableMap.getString("string"), mTextTransform));
      int m = paramSpannableStringBuilder.length();
      if (m >= k)
      {
        if (mIsColorSet) {
          paramList.add(new SetSpanOperation(k, m, new ReactForegroundColorSpan(mColor)));
        }
        if (mIsBackgroundColorSet) {
          paramList.add(new SetSpanOperation(k, m, new ReactBackgroundColorSpan(mBackgroundColor)));
        }
        if ((Build.VERSION.SDK_INT >= 21) && (!Float.isNaN(mLetterSpacing))) {
          paramList.add(new SetSpanOperation(k, m, new CustomLetterSpacingSpan(mLetterSpacing)));
        }
        paramList.add(new SetSpanOperation(k, m, new ReactAbsoluteSizeSpan(mFontSize)));
        if ((mFontStyle != -1) || (mFontWeight != -1) || (mFontFamily != null)) {
          paramList.add(new SetSpanOperation(k, m, new CustomStyleSpan(mFontStyle, mFontWeight, mFontFamily, paramContext.getAssets())));
        }
        if (mIsUnderlineTextDecorationSet) {
          paramList.add(new SetSpanOperation(k, m, new ReactUnderlineSpan()));
        }
        if (mIsLineThroughTextDecorationSet) {
          paramList.add(new SetSpanOperation(k, m, new ReactStrikethroughSpan()));
        }
        if ((mTextShadowOffsetDx != 0.0F) || (mTextShadowOffsetDy != 0.0F)) {
          paramList.add(new SetSpanOperation(k, m, new ShadowStyleSpan(mTextShadowOffsetDx, mTextShadowOffsetDy, mTextShadowRadius, mTextShadowColor)));
        }
        if (!Float.isNaN(localTextAttributeProps.getEffectiveLineHeight())) {
          paramList.add(new SetSpanOperation(k, m, new CustomLineHeightSpan(localTextAttributeProps.getEffectiveLineHeight())));
        }
        paramList.add(new SetSpanOperation(k, m, new ReactTagSpan(localReadableMap.getInt("reactTag"))));
      }
      i += 1;
    }
  }
  
  private static Spannable createSpannableFromAttributedString(Context paramContext, ReadableMap paramReadableMap)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    ArrayList localArrayList = new ArrayList();
    buildSpannableFromFragment(paramContext, paramReadableMap.getArray("fragments"), localSpannableStringBuilder, localArrayList);
    paramContext = localArrayList.iterator();
    int i = 0;
    while (paramContext.hasNext())
    {
      ((SetSpanOperation)paramContext.next()).execute(localSpannableStringBuilder, i);
      i += 1;
    }
    return localSpannableStringBuilder;
  }
  
  /* Error */
  protected static Spannable getOrCreateSpannableForText(Context paramContext, ReadableMap paramReadableMap)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 250	java/lang/Object:toString	()Ljava/lang/String;
    //   4: astore_2
    //   5: getstatic 31	com/facebook/react/views/text/TextLayoutManager:sSpannableCacheLock	Ljava/lang/Object;
    //   8: astore_3
    //   9: aload_3
    //   10: monitorenter
    //   11: getstatic 36	com/facebook/react/views/text/TextLayoutManager:sSpannableCache	Landroid/util/LruCache;
    //   14: aload_2
    //   15: invokevirtual 254	android/util/LruCache:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   18: checkcast 256	android/text/Spannable
    //   21: astore 4
    //   23: aload 4
    //   25: ifnull +8 -> 33
    //   28: aload_3
    //   29: monitorexit
    //   30: aload 4
    //   32: areturn
    //   33: aload_3
    //   34: monitorexit
    //   35: aload_0
    //   36: aload_1
    //   37: invokestatic 258	com/facebook/react/views/text/TextLayoutManager:createSpannableFromAttributedString	(Landroid/content/Context;Lcom/facebook/react/bridge/ReadableMap;)Landroid/text/Spannable;
    //   40: astore_1
    //   41: getstatic 31	com/facebook/react/views/text/TextLayoutManager:sSpannableCacheLock	Ljava/lang/Object;
    //   44: astore_0
    //   45: aload_0
    //   46: monitorenter
    //   47: getstatic 36	com/facebook/react/views/text/TextLayoutManager:sSpannableCache	Landroid/util/LruCache;
    //   50: aload_2
    //   51: aload_1
    //   52: invokevirtual 262	android/util/LruCache:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   55: pop
    //   56: aload_0
    //   57: monitorexit
    //   58: aload_1
    //   59: areturn
    //   60: astore_1
    //   61: aload_0
    //   62: monitorexit
    //   63: aload_1
    //   64: athrow
    //   65: astore_0
    //   66: aload_3
    //   67: monitorexit
    //   68: aload_0
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	paramContext	Context
    //   0	70	1	paramReadableMap	ReadableMap
    //   4	47	2	str	String
    //   8	59	3	localObject	Object
    //   21	10	4	localSpannable	Spannable
    // Exception table:
    //   from	to	target	type
    //   47	58	60	java/lang/Throwable
    //   61	63	60	java/lang/Throwable
    //   11	23	65	java/lang/Throwable
    //   28	30	65	java/lang/Throwable
    //   33	35	65	java/lang/Throwable
    //   66	68	65	java/lang/Throwable
  }
  
  public static long measureText(Context paramContext, ReadableMap paramReadableMap1, ReadableMap paramReadableMap2, float paramFloat1, YogaMeasureMode paramYogaMeasureMode1, float paramFloat2, YogaMeasureMode paramYogaMeasureMode2)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a18 = a17\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public static class SetSpanOperation
  {
    protected int end;
    protected int start;
    protected ReactSpan what;
    
    SetSpanOperation(int paramInt1, int paramInt2, ReactSpan paramReactSpan)
    {
      start = paramInt1;
      end = paramInt2;
      what = paramReactSpan;
    }
    
    public void execute(SpannableStringBuilder paramSpannableStringBuilder, int paramInt)
    {
      int i;
      if (start == 0) {
        i = 18;
      } else {
        i = 34;
      }
      paramSpannableStringBuilder.setSpan(what, start, end, paramInt << 16 & 0xFF0000 | i & 0xFF00FFFF);
    }
  }
}