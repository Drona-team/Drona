package androidx.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.delay.TypedArrayUtils;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TransitionInflater
{
  private static final ArrayMap<String, Constructor<?>> CONSTRUCTORS = new ArrayMap();
  private static final Class<?>[] CONSTRUCTOR_SIGNATURE = { Context.class, AttributeSet.class };
  private final Context mContext;
  
  private TransitionInflater(Context paramContext)
  {
    mContext = paramContext;
  }
  
  private Object createCustom(AttributeSet paramAttributeSet, Class paramClass, String paramString)
  {
    String str = paramAttributeSet.getAttributeValue(null, "class");
    if (str != null)
    {
      ArrayMap localArrayMap = CONSTRUCTORS;
      try
      {
        Constructor localConstructor = (Constructor)CONSTRUCTORS.get(str);
        paramString = localConstructor;
        if (localConstructor == null)
        {
          Class localClass = Class.forName(str, false, mContext.getClassLoader()).asSubclass(paramClass);
          paramString = localConstructor;
          if (localClass != null)
          {
            localConstructor = localClass.getConstructor(CONSTRUCTOR_SIGNATURE);
            paramString = localConstructor;
            localConstructor.setAccessible(true);
            CONSTRUCTORS.put(str, localConstructor);
          }
        }
        paramAttributeSet = paramString.newInstance(new Object[] { mContext, paramAttributeSet });
        return paramAttributeSet;
      }
      catch (Throwable paramAttributeSet)
      {
        try
        {
          throw paramAttributeSet;
        }
        catch (Exception paramAttributeSet)
        {
          paramString = new StringBuilder();
          paramString.append("Could not instantiate ");
          paramString.append(paramClass);
          paramString.append(" class ");
          paramString.append(str);
          throw new InflateException(paramString.toString(), paramAttributeSet);
        }
      }
    }
    paramAttributeSet = new StringBuilder();
    paramAttributeSet.append(paramString);
    paramAttributeSet.append(" tag must have a 'class' attribute");
    throw new InflateException(paramAttributeSet.toString());
  }
  
  private Transition createTransitionFromXml(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Transition paramTransition)
    throws XmlPullParserException, IOException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a13 = a12\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  private TransitionManager createTransitionManagerFromXml(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, ViewGroup paramViewGroup)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    TransitionManager localTransitionManager = null;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        break label146;
      }
      if (j == 2)
      {
        String str = paramXmlPullParser.getName();
        if (str.equals("transitionManager"))
        {
          localTransitionManager = new TransitionManager();
        }
        else
        {
          if ((!str.equals("transition")) || (localTransitionManager == null)) {
            break;
          }
          loadTransition(paramAttributeSet, paramXmlPullParser, paramViewGroup, localTransitionManager);
        }
      }
    }
    paramAttributeSet = new StringBuilder();
    paramAttributeSet.append("Unknown scene name: ");
    paramAttributeSet.append(paramXmlPullParser.getName());
    throw new RuntimeException(paramAttributeSet.toString());
    label146:
    return localTransitionManager;
  }
  
  public static TransitionInflater from(Context paramContext)
  {
    return new TransitionInflater(paramContext);
  }
  
  private void getTargetIds(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Transition paramTransition)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    TypedArray localTypedArray;
    Object localObject;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        return;
      }
      if (j == 2)
      {
        if (!paramXmlPullParser.getName().equals("target")) {
          break label306;
        }
        localTypedArray = mContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION_TARGET);
        j = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "targetId", 1, 0);
        if (j != 0)
        {
          paramTransition.addTarget(j);
        }
        else
        {
          j = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "excludeId", 2, 0);
          if (j != 0)
          {
            paramTransition.excludeTarget(j, true);
          }
          else
          {
            localObject = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "targetName", 4);
            if (localObject != null)
            {
              paramTransition.addTarget((String)localObject);
            }
            else
            {
              localObject = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "excludeName", 5);
              if (localObject != null)
              {
                paramTransition.excludeTarget((String)localObject, true);
              }
              else
              {
                String str = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "excludeClass", 3);
                localObject = str;
                if (str != null)
                {
                  try
                  {
                    paramTransition.excludeTarget(Class.forName(str), true);
                  }
                  catch (ClassNotFoundException paramXmlPullParser)
                  {
                    break;
                  }
                }
                else
                {
                  str = TypedArrayUtils.getNamedString(localTypedArray, paramXmlPullParser, "targetClass", 0);
                  if (str != null) {
                    try
                    {
                      paramTransition.addTarget(Class.forName(str));
                    }
                    catch (ClassNotFoundException paramXmlPullParser)
                    {
                      localObject = str;
                      break;
                    }
                  }
                }
              }
            }
          }
        }
        localTypedArray.recycle();
      }
    }
    localTypedArray.recycle();
    paramAttributeSet = new StringBuilder();
    paramAttributeSet.append("Could not create ");
    paramAttributeSet.append((String)localObject);
    throw new RuntimeException(paramAttributeSet.toString(), paramXmlPullParser);
    label306:
    paramAttributeSet = new StringBuilder();
    paramAttributeSet.append("Unknown scene name: ");
    paramAttributeSet.append(paramXmlPullParser.getName());
    throw new RuntimeException(paramAttributeSet.toString());
  }
  
  private void loadTransition(AttributeSet paramAttributeSet, XmlPullParser paramXmlPullParser, ViewGroup paramViewGroup, TransitionManager paramTransitionManager)
    throws Resources.NotFoundException
  {
    TypedArray localTypedArray = mContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION_MANAGER);
    int i = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "transition", 2, -1);
    int j = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "fromScene", 0, -1);
    Object localObject = null;
    if (j < 0) {
      paramAttributeSet = null;
    } else {
      paramAttributeSet = Scene.getSceneForLayout(paramViewGroup, j, mContext);
    }
    j = TypedArrayUtils.getNamedResourceId(localTypedArray, paramXmlPullParser, "toScene", 1, -1);
    if (j < 0) {
      paramXmlPullParser = localObject;
    } else {
      paramXmlPullParser = Scene.getSceneForLayout(paramViewGroup, j, mContext);
    }
    if (i >= 0)
    {
      paramViewGroup = inflateTransition(i);
      if (paramViewGroup != null) {
        if (paramXmlPullParser != null)
        {
          if (paramAttributeSet == null) {
            paramTransitionManager.setTransition(paramXmlPullParser, paramViewGroup);
          } else {
            paramTransitionManager.setTransition(paramAttributeSet, paramXmlPullParser, paramViewGroup);
          }
        }
        else
        {
          paramAttributeSet = new StringBuilder();
          paramAttributeSet.append("No toScene for transition ID ");
          paramAttributeSet.append(i);
          throw new RuntimeException(paramAttributeSet.toString());
        }
      }
    }
    localTypedArray.recycle();
  }
  
  public Transition inflateTransition(int paramInt)
  {
    XmlResourceParser localXmlResourceParser = mContext.getResources().getXml(paramInt);
    try
    {
      Transition localTransition = createTransitionFromXml(localXmlResourceParser, Xml.asAttributeSet(localXmlResourceParser), null);
      localXmlResourceParser.close();
      return localTransition;
    }
    catch (Throwable localThrowable) {}catch (IOException localIOException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(localXmlResourceParser.getPositionDescription());
      localStringBuilder.append(": ");
      localStringBuilder.append(localIOException.getMessage());
      throw new InflateException(localStringBuilder.toString(), localIOException);
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      throw new InflateException(localXmlPullParserException.getMessage(), localXmlPullParserException);
    }
    localXmlResourceParser.close();
    throw localXmlPullParserException;
  }
  
  public TransitionManager inflateTransitionManager(int paramInt, ViewGroup paramViewGroup)
  {
    XmlResourceParser localXmlResourceParser = mContext.getResources().getXml(paramInt);
    try
    {
      paramViewGroup = createTransitionManagerFromXml(localXmlResourceParser, Xml.asAttributeSet(localXmlResourceParser), paramViewGroup);
      localXmlResourceParser.close();
      return paramViewGroup;
    }
    catch (Throwable paramViewGroup) {}catch (IOException paramViewGroup)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(localXmlResourceParser.getPositionDescription());
      ((StringBuilder)localObject).append(": ");
      ((StringBuilder)localObject).append(paramViewGroup.getMessage());
      localObject = new InflateException(((StringBuilder)localObject).toString());
      ((Exception)localObject).initCause(paramViewGroup);
      throw ((Throwable)localObject);
    }
    catch (XmlPullParserException paramViewGroup)
    {
      Object localObject = new InflateException(paramViewGroup.getMessage());
      ((Exception)localObject).initCause(paramViewGroup);
      throw ((Throwable)localObject);
    }
    localXmlResourceParser.close();
    throw paramViewGroup;
  }
}
