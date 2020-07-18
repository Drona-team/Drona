package androidx.core.content.delay;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.XmlResourceParser;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import androidx.annotation.RestrictTo;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public final class ComplexColorCompat
{
  private static final String LOG_TAG = "ComplexColorCompat";
  private int mColor;
  private final ColorStateList mColorStateList;
  private final Shader mShader;
  
  private ComplexColorCompat(Shader paramShader, ColorStateList paramColorStateList, int paramInt)
  {
    mShader = paramShader;
    mColorStateList = paramColorStateList;
    mColor = paramInt;
  }
  
  private static ComplexColorCompat createFromXml(Resources paramResources, int paramInt, Resources.Theme paramTheme)
    throws IOException, XmlPullParserException
  {
    XmlResourceParser localXmlResourceParser = paramResources.getXml(paramInt);
    AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
    int i;
    do
    {
      i = localXmlResourceParser.next();
      paramInt = 1;
    } while ((i != 2) && (i != 1));
    if (i == 2)
    {
      String str = localXmlResourceParser.getName();
      i = str.hashCode();
      if (i != 89650992)
      {
        if ((i == 1191572447) && (str.equals("selector")))
        {
          paramInt = 0;
          break label102;
        }
      }
      else {
        if (str.equals("gradient")) {
          break label102;
        }
      }
      paramInt = -1;
      switch (paramInt)
      {
      default: 
        paramResources = new StringBuilder();
        paramResources.append(localXmlResourceParser.getPositionDescription());
        paramResources.append(": unsupported complex color tag ");
        paramResources.append(str);
        throw new XmlPullParserException(paramResources.toString());
      case 1: 
        label102:
        return from(GradientColorInflaterCompat.createFromXmlInner(paramResources, localXmlResourceParser, localAttributeSet, paramTheme));
      }
      return from(ColorStateListInflaterCompat.createFromXmlInner(paramResources, localXmlResourceParser, localAttributeSet, paramTheme));
    }
    throw new XmlPullParserException("No start tag found");
  }
  
  static ComplexColorCompat from(int paramInt)
  {
    return new ComplexColorCompat(null, null, paramInt);
  }
  
  static ComplexColorCompat from(ColorStateList paramColorStateList)
  {
    return new ComplexColorCompat(null, paramColorStateList, paramColorStateList.getDefaultColor());
  }
  
  static ComplexColorCompat from(Shader paramShader)
  {
    return new ComplexColorCompat(paramShader, null, 0);
  }
  
  public static ComplexColorCompat inflate(Resources paramResources, int paramInt, Resources.Theme paramTheme)
  {
    try
    {
      paramResources = createFromXml(paramResources, paramInt, paramTheme);
      return paramResources;
    }
    catch (Exception paramResources)
    {
      Log.e("ComplexColorCompat", "Failed to inflate ComplexColor.", paramResources);
    }
    return null;
  }
  
  public int getColor()
  {
    return mColor;
  }
  
  public Shader getShader()
  {
    return mShader;
  }
  
  public boolean isGradient()
  {
    return mShader != null;
  }
  
  public boolean isStateful()
  {
    return (mShader == null) && (mColorStateList != null) && (mColorStateList.isStateful());
  }
  
  public boolean onStateChanged(int[] paramArrayOfInt)
  {
    if (isStateful())
    {
      int i = mColorStateList.getColorForState(paramArrayOfInt, mColorStateList.getDefaultColor());
      if (i != mColor)
      {
        mColor = i;
        return true;
      }
    }
    return false;
  }
  
  public void setColor(int paramInt)
  {
    mColor = paramInt;
  }
  
  public boolean willDraw()
  {
    return (isGradient()) || (mColor != 0);
  }
}
