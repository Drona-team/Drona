package androidx.browser.browseractions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.browser.R.id;
import androidx.browser.R.layout;
import androidx.core.content.delay.ResourcesCompat;
import java.util.List;

class BrowserActionsFallbackMenuAdapter
  extends BaseAdapter
{
  private final Context mContext;
  private final List<BrowserActionItem> mMenuItems;
  
  BrowserActionsFallbackMenuAdapter(List paramList, Context paramContext)
  {
    mMenuItems = paramList;
    mContext = paramContext;
  }
  
  public int getCount()
  {
    return mMenuItems.size();
  }
  
  public Object getItem(int paramInt)
  {
    return mMenuItems.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    BrowserActionItem localBrowserActionItem = (BrowserActionItem)mMenuItems.get(paramInt);
    Object localObject;
    if (paramView == null)
    {
      paramViewGroup = LayoutInflater.from(mContext).inflate(R.layout.browser_actions_context_menu_row, null);
      paramView = paramViewGroup;
      localObject = new ViewHolderItem();
      mIcon = ((ImageView)paramViewGroup.findViewById(R.id.browser_actions_menu_item_icon));
      mText = ((TextView)paramViewGroup.findViewById(R.id.browser_actions_menu_item_text));
      paramViewGroup.setTag(localObject);
      paramViewGroup = (ViewGroup)localObject;
    }
    else
    {
      paramViewGroup = (ViewHolderItem)paramView.getTag();
    }
    mText.setText(localBrowserActionItem.getTitle());
    if (localBrowserActionItem.getIconId() != 0)
    {
      localObject = ResourcesCompat.getDrawable(mContext.getResources(), localBrowserActionItem.getIconId(), null);
      mIcon.setImageDrawable((Drawable)localObject);
      return paramView;
    }
    mIcon.setImageDrawable(null);
    return paramView;
  }
  
  static class ViewHolderItem
  {
    ImageView mIcon;
    TextView mText;
    
    ViewHolderItem() {}
  }
}
