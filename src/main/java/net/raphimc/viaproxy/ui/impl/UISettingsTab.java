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
        JComboBox<String> box=new JComboBox<>(new String[]{"Dark","aagaming mod","Ocean","Midnight","Nord","Crimson","Sunset","Le1tny"});
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
            else if("Sunset".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(45,20,35)); UIManager.put("TabbedPane.contentAreaColor",new Color(75,30,45)); UIManager.put("TabbedPane.background",new Color(75,30,45)); UIManager.put("Component.accentColor",new Color(255,140,60));}
            else if("Le1tny".equals(t)){ FlatDarkLaf.setup(); UIManager.put("Panel.background",new Color(192,14,28)); UIManager.put("TabbedPane.contentAreaColor",new Color(12,64,118)); UIManager.put("TabbedPane.background",new Color(12,64,118)); UIManager.put("Component.accentColor",new Color(255,255,255));}
            else if("Light".equals(t)) FlatLightLaf.setup();
            else FlatDarkLaf.setup();
            FlatLaf.updateUI();
            Color bg = UIManager.getColor("Panel.background");
            Color tabBg = UIManager.getColor("TabbedPane.contentAreaColor");
            if(tabBg==null) tabBg=bg;
            if(win!=null) applySerbianFlag(win, t, bg, tabBg);
            if(win!=null) forceAll(win, bg, tabBg);
            for(Window w: Window.getWindows()) forceAll(w, bg, tabBg);
        }catch(Exception ex){ex.printStackTrace();}
    }
    private static void applySerbianFlag(ViaProxyWindow win, String t, Color bg, Color tabBg){
        if(!"Le1tny".equals(t)) return;
        // Serbian flag: red top, blue middle, white bottom - paint as layered background
        // We do this by setting panel background to red and adding overlay logic via UIManager
        // Actual flag applied in forceAll via custom painting hook - store flag marker
        win.getRootPane().putClientProperty("serbianFlag", true);
    }
    private static void forceAll(Container root, Color bg, Color tabBg){
        if(bg==null) return;
        boolean isSerbian = false;
        if(root instanceof ViaProxyWindow) isSerbian = Boolean.TRUE.equals(((ViaProxyWindow)root).getRootPane().getClientProperty("serbianFlag"));
        // fallback check via UIManager colors: Le1tny theme has red bg
        if(bg.getRed()==192 && bg.getGreen()==14) isSerbian=true;
        if(isSerbian){
            // Serbian tri-color: paint panels with flag stripes via background
            // Instead use vertical split: top red, middle blue, bottom white via recursive
            recolorSerbian(root, bg, tabBg);
        } else {
            recolor(root, bg, tabBg);
        }
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
    private static void recolorSerbian(Container c, Color bg, Color tabBg){
        // Use flag colors: red 192,14,28 / blue 12,64,118 / white 255,255,255
        Color red=new Color(192,14,28), blue=new Color(12,64,118), white=Color.WHITE;
        for(Component comp: c.getComponents()){
            if(comp instanceof JTabbedPane){ comp.setBackground(blue); ((JTabbedPane)comp).setOpaque(true); }
            else if(comp instanceof JPanel){
                // alternate stripes by depth for visual flag feel
                comp.setBackground(comp.getY()%3==0?red:(comp.getY()%3==1?blue:white));
                // ensure readable text
                comp.setForeground(Color.WHITE);
            }
            if(comp instanceof Container) recolorSerbian((Container)comp, bg, tabBg);
        }
        c.setBackground(red);
    }
}
