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
    public UISettingsTab(final ViaProxyWindow f){ super(f,"ui_settings"); }
    @Override protected void init(JPanel c){
        JPanel b=new JPanel(new GridBagLayout()); int y=0;
        JLabel th=new JLabel("Theme"); th.setFont(th.getFont().deriveFont(Font.BOLD,13f));
        GBC.create(b).grid(0,y++).insets(BORDER_PADDING,BORDER_PADDING,0,BORDER_PADDING).anchor(GBC.NORTHWEST).add(th);
        JComboBox<String> box=new JComboBox<>(new String[]{"Dark","aagaming mod","Ocean","Midnight","Nord","Crimson"});
        box.addActionListener(e->applyTheme(this.viaProxyWindow,(String)box.getSelectedItem()));
        GBC.create(b).grid(0,y++).weightx(1).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(box);
        GBC.create(b).grid(0,y++).insets(16,BORDER_PADDING,0,BORDER_PADDING).anchor(GBC.NORTHWEST).add(new JLabel("Tooltip delay (ms)"));
        JSlider s=new JSlider(0,2000,ToolTipManager.sharedInstance().getInitialDelay());
        s.addChangeListener(ev->ToolTipManager.sharedInstance().setInitialDelay(s.getValue()));
        GBC.create(b).grid(0,y++).weightx(1).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(s);
        c.setLayout(new BorderLayout()); c.add(b,BorderLayout.NORTH);
    }
    public static void applyTheme(ViaProxyWindow win, String t){
        try{
            for(String k:new String[]{"Panel.background","TabbedPane.background","TabbedPane.contentAreaColor","Component.accentColor"}){ UIManager.put(k,null); try{UIManager.getDefaults().remove(k);}catch(Exception ignored){} }
            if("aagaming mod".equals(t)) FlatLightLaf.setup();
            else if("Ocean".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(0,105,148)); UIManager.put("TabbedPane.contentAreaColor",new Color(0,80,120)); UIManager.put("TabbedPane.background",new Color(0,80,120)); UIManager.put("Component.accentColor",new Color(0,180,220));}
            else if("Midnight".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(18,18,30)); UIManager.put("TabbedPane.contentAreaColor",new Color(24,24,44)); UIManager.put("TabbedPane.background",new Color(24,24,44));}
            else if("Nord".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(46,52,64)); UIManager.put("TabbedPane.contentAreaColor",new Color(59,66,82)); UIManager.put("TabbedPane.background",new Color(59,66,82)); UIManager.put("Component.accentColor",new Color(136,192,208));}
            else if("Crimson".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(72,18,22)); UIManager.put("TabbedPane.contentAreaColor",new Color(95,25,30)); UIManager.put("TabbedPane.background",new Color(95,25,30)); UIManager.put("Component.accentColor",new Color(220,60,70));}
            else if("Light".equals(t)) FlatLightLaf.setup();
            else FlatDarkLaf.setup();
            FlatLaf.updateUI();
            Color bg = UIManager.getColor("Panel.background");
            Color tabBg = UIManager.getColor("TabbedPane.contentAreaColor");
            if(tabBg==null) tabBg=bg;
            if(win!=null) forceAll(win, bg, tabBg);
            for(Window w: Window.getWindows()) forceAll(w, bg, tabBg);
        }catch(Exception ex){ex.printStackTrace();}
    }
    private static void forceAll(Container root, Color bg, Color tabBg){
        if(bg==null) return;
        recolor(root, bg, tabBg);
        root.repaint(); root.revalidate();
    }
    private static void recolor(Container c, Color bg, Color tabBg){
        for(Component comp: c.getComponents()){
            if(comp instanceof JPanel) comp.setBackground(bg);
            if(comp instanceof JTabbedPane){ comp.setBackground(tabBg); ((JTabbedPane)comp).setOpaque(true); }
            if(comp instanceof JScrollPane){ comp.setBackground(bg); ((JScrollPane)comp).getViewport().setBackground(bg); }
            if(comp instanceof Container) recolor((Container)comp, bg, tabBg);
        }
        if(c instanceof JPanel || c instanceof JTabbedPane || c instanceof JFrame || c instanceof JWindow) c.setBackground(bg);
    }
}
