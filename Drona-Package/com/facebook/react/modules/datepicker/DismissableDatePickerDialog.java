package com.facebook.react.modules.datepicker;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.DatePicker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DismissableDatePickerDialog
  extends DatePickerDialog
{
  public DismissableDatePickerDialog(Context paramContext, int paramInt1, DatePickerDialog.OnDateSetListener paramOnDateSetListener, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramContext, paramInt1, paramOnDateSetListener, paramInt2, paramInt3, paramInt4);
    fixSpinner(paramContext, paramInt2, paramInt3, paramInt4);
  }
  
  public DismissableDatePickerDialog(Context paramContext, DatePickerDialog.OnDateSetListener paramOnDateSetListener, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramContext, paramOnDateSetListener, paramInt1, paramInt2, paramInt3);
    fixSpinner(paramContext, paramInt1, paramInt2, paramInt3);
  }
  
  private static Field findField(Class paramClass1, Class paramClass2, String paramString)
  {
    try
    {
      paramString = paramClass1.getDeclaredField(paramString);
      paramString.setAccessible(true);
      return paramString;
    }
    catch (NoSuchFieldException paramString)
    {
      int j;
      int i;
      for (;;) {}
    }
    paramClass1 = paramClass1.getDeclaredFields();
    j = paramClass1.length;
    i = 0;
    while (i < j)
    {
      paramString = paramClass1[i];
      if (paramString.getType() == paramClass2)
      {
        paramString.setAccessible(true);
        return paramString;
      }
      i += 1;
    }
    return null;
  }
  
  private void fixSpinner(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    if (Build.VERSION.SDK_INT == 24) {
      try
      {
        Object localObject1 = Class.forName("com.android.internal.R$styleable");
        Object localObject2 = ((Class)localObject1).getField("DatePicker").get(null);
        localObject2 = (int[])localObject2;
        localObject2 = paramContext.obtainStyledAttributes(null, (int[])localObject2, 16843612, 0);
        int i = ((TypedArray)localObject2).getInt(((Class)localObject1).getField("DatePicker_datePickerMode").getInt(null), 2);
        ((TypedArray)localObject2).recycle();
        if (i == 2)
        {
          localObject1 = findField(DatePickerDialog.class, DatePicker.class, "mDatePicker").get(this);
          localObject1 = (DatePicker)localObject1;
          localObject2 = findField(DatePicker.class, Class.forName("android.widget.DatePickerSpinnerDelegate"), "mDelegate");
          Object localObject4 = ((Field)localObject2).get(localObject1);
          Object localObject3 = Class.forName("android.widget.DatePickerSpinnerDelegate");
          localObject4 = localObject4.getClass();
          if (localObject4 != localObject3)
          {
            ((Field)localObject2).set(localObject1, null);
            ((ViewGroup)localObject1).removeAllViews();
            localObject3 = Integer.TYPE;
            localObject4 = Integer.TYPE;
            localObject3 = DatePicker.class.getDeclaredMethod("createSpinnerUIDelegate", new Class[] { Context.class, AttributeSet.class, localObject3, localObject4 });
            ((Method)localObject3).setAccessible(true);
            ((Field)localObject2).set(localObject1, ((Method)localObject3).invoke(localObject1, new Object[] { paramContext, null, Integer.valueOf(16843612), Integer.valueOf(0) }));
            ((DatePicker)localObject1).setCalendarViewShown(false);
            ((DatePicker)localObject1).init(paramInt1, paramInt2, paramInt3, this);
            return;
          }
        }
      }
      catch (Exception paramContext)
      {
        throw new RuntimeException(paramContext);
      }
    }
  }
  
  protected void onStop()
  {
    if (Build.VERSION.SDK_INT > 19) {
      super.onStop();
    }
  }
}
