package com.jiusg.mainscreenshow.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.ActionBar;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;

public class SmartBarUtils {

    /**
     * 鐠嬪啰鏁?ActionBar.setTabsShowAtBottom(boolean) 閺傝纭堕妴锟?    * 
     * <p>婵″倹鐏?android:uiOptions="splitActionBarWhenNarrow"閿涘苯鍨崣顖濐啎缂冪摰ctionBar Tabs閺勫墽銇氶崷銊ョ俺閺嶅繈锟?     * 
     * <p>缁?桨绶ラ敍锟?p>
     * <pre class="prettyprint">
     * public class MyActivity extends Activity implements ActionBar.TabListener {
     * 
     *     protected void onCreate(Bundle savedInstanceState) {
     *         super.onCreate(savedInstanceState);
     *         ...
     *         
     *         final ActionBar bar = getActionBar();
     *         bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
     *         SmartBarUtils.setActionBarTabsShowAtBottom(bar, true);
     *         
     *         bar.addTab(bar.newTab().setText("tab1").setTabListener(this));
     *         ...
     *     }
     * }
     * </pre>
     */
    public static void setActionBarTabsShowAtBottom(ActionBar actionbar, boolean showAtBottom) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setTabsShowAtBottom", new Class[] { boolean.class });
            try {
                method.invoke(actionbar, showAtBottom);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 鐠嬪啰鏁?ActionBar.setActionBarViewCollapsable(boolean) 閺傝纭堕妴锟?    * 
     * <p>鐠佸墽鐤咥ctionBar妞よ埖鐖弮鐘虫▔缁?搫鍞寸?瑙勬閺勵垰鎯侀梾鎰閵嗭拷     * 
     * <p>缁?桨绶ラ敍锟?p>
     * <pre class="prettyprint">
     * public class MyActivity extends Activity {
     * 
     *     protected void onCreate(Bundle savedInstanceState) {
     *         super.onCreate(savedInstanceState);
     *         ...
     *         
     *         final ActionBar bar = getActionBar();
     *         
     *         // 鐠嬪啰鏁etActionBarViewCollapsable閿涘苯鑻熺拋鍓х枂ActionBar濞屸剝婀侀弰鍓с仛閸愬懎顔愰敍灞藉灟ActionBar妞よ埖鐖稉宥嗘▔缁?拷     *         SmartBarUtils.setActionBarViewCollapsable(bar, true);
     *         bar.setDisplayOptions(0);
     *     }
     * }
     * </pre>
     */
    public static void setActionBarViewCollapsable(ActionBar actionbar, boolean collapsable) {
    	
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setActionBarViewCollapsable", new Class[] { boolean.class });
            try {
                method.invoke(actionbar, collapsable);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 鐠嬪啰鏁?ActionBar.setActionModeHeaderHidden(boolean) 閺傝纭堕妴锟?    * 
     * <p>鐠佸墽鐤咥ctionMode妞よ埖鐖弰顖氭儊闂呮劘妫岄妴锟?    * 
     * <p>缁?桨绶ラ敍锟?p>
     * <pre class="prettyprint">
     * public class MyActivity extends Activity {
     * 
     *     protected void onCreate(Bundle savedInstanceState) {
     *         super.onCreate(savedInstanceState);
     *         ...
     *         
     *         final ActionBar bar = getActionBar();
     *         
     *         // ActionBar鏉烆兛璐烝ctionMode閺冭绱濇稉宥嗘▔缁?瘓ctionMode妞よ埖鐖?     *         SmartBarUtils.setActionModeHeaderHidden(bar, true);
     *     }
     * }
     * </pre>
     */
    public static void setActionModeHeaderHidden(ActionBar actionbar, boolean hidden) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setActionModeHeaderHidden", new Class[] { boolean.class });
            try {
                method.invoke(actionbar, hidden);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 鐠嬪啰鏁ctionBar.setBackButtonDrawable(Drawable)閺傝纭?     * 
     * <p>鐠佸墽鐤嗘潻鏂挎礀闁款喖娴橀弽锟?    * 
     * <p>缁?桨绶ラ敍锟?p>
     * <pre class="prettyprint">
     * public class MyActivity extends Activity {
     * 
     *     protected void onCreate(Bundle savedInstanceState) {
     *         super.onCreate(savedInstanceState);
     *         ...
     *         
     *         final ActionBar bar = getActionBar();
     *         // 閼奉亜鐣炬稊鍫縞tionBar閻ㄥ嫯绻戦崶鐐烘暛閸ョ偓鐖?     *         SmartBarUtils.setBackIcon(bar, getResources().getDrawable(R.drawable.ic_back));
     *         ...
     *     }
     * }
     * </pre>
     */
    public static void setBackIcon(ActionBar actionbar, Drawable backIcon) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setBackButtonDrawable", new Class[] { Drawable.class });
            try {
                method.invoke(actionbar, backIcon); 
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
}
