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
        JComboBox<String> box=new JComboBox<>(new String[]{"Dark","Light","Ocean","Midnight","Nord","Crimson"});
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
            Color bg=null, tabBg=null, accent=null;
            boolean isLight="Light".equals(t);
            // reset any prior UIManager overrides
            for(String k:new String[]{"Panel.background","TabbedPane.background","TabbedPane.contentAreaColor","Component.accentColor"}){ UIManager.put(k,null); try{UIManager.getDefaults().remove(k);}catch(Exception ignored){} }
            if(isLight) FlatLightLaf.setup();
            else if("Ocean".equals(t)){ FlatDarkLaf.setup(); bg=new Color(0,105,148); tabBg=new Color(0,80,120); accent=new Color(0,180,220); }
            else if("Midnight".equals(t)){ FlatDarkLaf.setup(); bg=new Color(18,18,30); tabBg=new Color(24,24,44); accent=new Color(120,120,255); }
            else if("Nord".equals(t)){ FlatDarkLaf.setup(); bg=new Color(46,52,64); tabBg=new Color(59,66,82); accent=new Color(136,192,208); }
            else if("Crimson".equals(t)){ FlatDarkLaf.setup(); bg=new Color(72,18,22); tabBg=new Color(95,25,30); accent=new Color(220,60,70); }
            else FlatDarkLaf.setup();
            if(bg!=null){ UIManager.put("Panel.background",bg); UIManager.put("TabbedPane.contentAreaColor",tabBg); UIManager.put("Component.accentColor",accent); UIManager.put("TabbedPane.background",tabBg); }
            FlatLaf.updateUI();
            if(bg!=null && win!=null) forceColors(win, bg, tabBg, accent);
            // for Light/Dark ensure no leftover custom paint by forcing revalidate on contentPane
            if(win!=null){ win.repaint(); win.revalidate(); }
        }catch(Exception ex){ex.printStackTrace();}
    }
    private static void forceColors(Container root, Color bg, Color tabBg, Color accent){
        for(Window w: Window.getWindows()) recolor(w, bg, tabBg);
        recolor(root, bg, tabBg);
        if(accent!=null) UIManager.put("Component.accentColor",accent);
    }
    private static void recolor(Container c, Color bg, Color tabBg){
        for(Component comp: c.getComponents()){
            if(comp instanceof JPanel) comp.setBackground(bg);
            if(comp instanceof JTabbedPane) {
                comp.setBackground(tabBg);
                ((JTabbedPane)comp).setOpaque(true);
            }
            if(comp instanceof JLabel && !(comp.getParent() instanceof JComboBox)) comp.setForeground(Color.WHITE);
            if(comp instanceof Container) recolor((Container)comp, bg, tabBg);
        }
        c.setBackground(bg);
    }
}
