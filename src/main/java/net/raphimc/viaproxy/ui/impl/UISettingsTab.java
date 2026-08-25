package net.raphimc.viaproxy.ui.impl;

import net.lenni0451.commons.swing.GBC;
import net.raphimc.viaproxy.ui.UITab;
import net.raphimc.viaproxy.ui.ViaProxyWindow;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import javax.swing.*;
import java.awt.*;
import static net.raphimc.viaproxy.ui.ViaProxyWindow.BORDER_PADDING;

public class UISettingsTab extends UITab {
    private static final String[] K = {"Panel.background","TabbedPane.background","TabbedPane.contentAreaColor","Button.background","TextField.background","Component.accentColor"};
    public UISettingsTab(final ViaProxyWindow f){ super(f,"ui_settings"); }
    @Override protected void init(JPanel c){
        JPanel b=new JPanel(new GridBagLayout()); int y=0;
        GBC.create(b).grid(0,y++).insets(BORDER_PADDING,BORDER_PADDING,0,BORDER_PADDING).anchor(GBC.NORTHWEST).add(new JLabel("Theme"));
        JComboBox<String> box=new JComboBox<>(new String[]{"Dark","Light","Ocean","Midnight","Nord","Crimson"});
        box.addActionListener(e->applyTheme((String)box.getSelectedItem()));
        GBC.create(b).grid(0,y++).weightx(1).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(box);
        GBC.create(b).grid(0,y++).insets(BORDER_PADDING,BORDER_PADDING,0,BORDER_PADDING).anchor(GBC.NORTHWEST).add(new JLabel("Tooltip delay (ms)"));
        JSlider s=new JSlider(0,2000,ToolTipManager.sharedInstance().getInitialDelay());
        s.addChangeListener(ev->ToolTipManager.sharedInstance().setInitialDelay(s.getValue()));
        GBC.create(b).grid(0,y++).weightx(1).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(s);
        c.setLayout(new BorderLayout()); c.add(b,BorderLayout.NORTH);
    }
    public static void applyTheme(String t){
        try{
            // only clear BEFORE installing new LAF - do not clear after or you delete base Light/Dark keys
            for(String k:K){
                UIManager.put(k,null);
                try{UIManager.getDefaults().remove(k);}catch(Exception ignored){}
            }
            if("Light".equals(t)) FlatLightLaf.setup();
            else if("Ocean".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(0,105,148)); UIManager.put("TabbedPane.contentAreaColor",new Color(0,80,120)); UIManager.put("Component.accentColor",new Color(0,180,220));}
            else if("Midnight".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(18,18,30)); UIManager.put("TabbedPane.contentAreaColor",new Color(22,22,40));}
            else if("Nord".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(46,52,64)); UIManager.put("TabbedPane.contentAreaColor",new Color(59,66,82)); UIManager.put("Component.accentColor",new Color(136,192,208));}
            else if("Crimson".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(60,15,20)); UIManager.put("TabbedPane.contentAreaColor",new Color(90,20,25)); UIManager.put("Component.accentColor",new Color(200,40,60));}
            else FlatDarkLaf.setup();
            FlatLaf.updateUI();
        }catch(Exception ex){ex.printStackTrace();}
    }
}
