package androidx.core.content.delay;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.Xml;
import androidx.annotation.RestrictTo;
import androidx.core.R.attr;
import androidx.core.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public final class ColorStateListInflaterCompat
{
  private ColorStateListInflaterCompat() {}
  
  public static ColorStateList createFromXml(Resources paramResources, XmlPullParser paramXmlPullParser, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
    if (i == 2) {
      return createFromXmlInner(paramResources, paramXmlPullParser, localAttributeSet, paramTheme);
    }
    throw new XmlPullParserException("No start tag found");
  }
  
  public static ColorStateList createFromXmlInner(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    String str = paramXmlPullParser.getName();
    if (str.equals("selector")) {
      return inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    }
    paramResources = new StringBuilder();
    paramResources.append(paramXmlPullParser.getPositionDescription());
    paramResources.append(": invalid color state list tag ");
    paramResources.append(str);
    throw new XmlPullParserException(paramResources.toString());
  }
  
  public static ColorStateList inflate(Resources paramResources, int paramInt, Resources.Theme paramTheme)
  {
    try
    {
      paramResources = createFromXml(paramResources, paramResources.getXml(paramInt), paramTheme);
      return paramResources;
    }
    catch (Exception paramResources)
    {
      Log.e("CSLCompat", "Failed to inflate ColorStateList.", paramResources);
    }
    return null;
  }
  
  private static ColorStateList inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    int i2 = paramXmlPullParser.getDepth() + 1;
    Object localObject1 = new int[20][];
    Object localObject2 = new int[localObject1.length];
    int i = 0;
    for (;;)
    {
      int k = paramXmlPullParser.next();
      if (k == 1) {
        break;
      }
      int m = paramXmlPullParser.getDepth();
      if ((m < i2) && (k == 3)) {
        break;
      }
      Object localObject3 = localObject1;
      int j = i;
      Object localObject4 = localObject2;
      if (k == 2)
      {
        localObject3 = localObject1;
        j = i;
        localObject4 = localObject2;
        if (m <= i2) {
          if (!paramXmlPullParser.getName().equals("item"))
          {
            localObject3 = localObject1;
            j = i;
            localObject4 = localObject2;
          }
          else
          {
            localObject3 = obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.ColorStateListItem);
            int i3 = ((TypedArray)localObject3).getColor(R.styleable.ColorStateListItem_android_color, -65281);
            float f = 1.0F;
            if (((TypedArray)localObject3).hasValue(R.styleable.ColorStateListItem_android_alpha)) {
              f = ((TypedArray)localObject3).getFloat(R.styleable.ColorStateListItem_android_alpha, 1.0F);
            } else if (((TypedArray)localObject3).hasValue(R.styleable.ColorStateListItem_alpha)) {
              f = ((TypedArray)localObject3).getFloat(R.styleable.ColorStateListItem_alpha, 1.0F);
            }
            ((TypedArray)localObject3).recycle();
            int i4 = paramAttributeSet.getAttributeCount();
            localObject3 = new int[i4];
            j = 0;
            for (k = 0; j < i4; k = m)
            {
              int i1 = paramAttributeSet.getAttributeNameResource(j);
              int n = i1;
              m = k;
              if (i1 != 16843173)
              {
                m = k;
                if (i1 != 16843551)
                {
                  m = k;
                  if (i1 != R.attr.alpha)
                  {
                    if (!paramAttributeSet.getAttributeBooleanValue(j, false)) {
                      n = -i1;
                    }
                    localObject3[k] = n;
                    m = k + 1;
                  }
                }
              }
              j += 1;
            }
            localObject3 = StateSet.trimStateSet((int[])localObject3, k);
            localObject4 = GrowingArrayUtils.append((int[])localObject2, i, modulateColorAlpha(i3, f));
            localObject3 = (int[][])GrowingArrayUtils.append((Object[])localObject1, i, localObject3);
            j = i + 1;
          }
        }
      }
      localObject1 = localObject3;
      i = j;
      localObject2 = localObject4;
    }
    paramResources = new int[i];
    paramXmlPullParser = new int[i][];
    System.arraycopy(localObject2, 0, paramResources, 0, i);
    System.arraycopy(localObject1, 0, paramXmlPullParser, 0, i);
    return new ColorStateList(paramXmlPullParser, paramResources);
  }
  
  private static int modulateColorAlpha(int paramInt, float paramFloat)
  {
    return paramInt & 0xFFFFFF | Math.round(Color.alpha(paramInt) * paramFloat) << 24;
  }
  
  private static TypedArray obtainAttributes(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, int[] paramArrayOfInt)
  {
    if (paramTheme == null) {
      return paramResources.obtainAttributes(paramAttributeSet, paramArrayOfInt);
    }
    return paramTheme.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, 0, 0);
  }
}
