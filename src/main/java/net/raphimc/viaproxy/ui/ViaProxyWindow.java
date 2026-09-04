package net.raphimc.viaproxy.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;
import net.lenni0451.reflect.JavaBypass;
import net.lenni0451.reflect.stream.RStream;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.ui.events.UICloseEvent;
import net.raphimc.viaproxy.ui.impl.*;
import net.raphimc.viaproxy.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ViaProxyWindow extends JFrame {
    public final LambdaManager eventManager = LambdaManager.threadSafe(new LambdaMetaFactoryGenerator(JavaBypass.TRUSTED_LOOKUP));
    public static final int BORDER_PADDING = 14;
    public static final int BODY_BLOCK_PADDING = 12;
    public final JTabbedPane contentPane = new JTabbedPane();
    private final List<UITab> tabs = new ArrayList<>();
    public final GeneralTab generalTab = new GeneralTab(this);
    public final AdvancedTab advancedTab = new AdvancedTab(this);
    public final AccountsTab accountsTab = new AccountsTab(this);
    public final UISettingsTab uiSettingsTab = new UISettingsTab(this);
    public ViaProxyWindow() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> showException(e));
        this.eventManager.register(this);
        this.setLookAndFeel();
        this.initWindow();
        this.initTabs();
        FlatInspector.install("ctrl shift I");
        FlatUIDefaultsInspector.install("ctrl shift O");
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(10_000);
        SwingUtilities.updateComponentTreeUI(this);
        this.setVisible(true);
    }
    private void setLookAndFeel() {
        try {
            FlatDarkLaf.setup();
            Color bg=new Color(48,48,48);
            Color tabBg=new Color(48,48,48);
            UIManager.put("Panel.background", bg);
            UIManager.getLookAndFeelDefaults().put("Panel.background", bg);
            UIManager.put("TabbedPane.background", bg);
            UIManager.getLookAndFeelDefaults().put("TabbedPane.background", bg);
            UIManager.put("TabbedPane.contentAreaColor", bg);
            UIManager.getLookAndFeelDefaults().put("TabbedPane.contentAreaColor", bg);
            UIManager.put("TabbedPane.tabAreaBackground", bg);
            UIManager.getLookAndFeelDefaults().put("TabbedPane.tabAreaBackground", bg);
            UIManager.put("TabbedPane.underlineColor", new Color(255,215,0));
            UIManager.put("TabbedPane.hoverColor", new Color(64,64,64));
            UIManager.put("TabbedPane.selectedBackground", tabBg);
            UIManager.getLookAndFeelDefaults().put("TabbedPane.selectedBackground", tabBg);
            UIManager.put("TabbedPane.focusColor", new Color(255,215,0));
            UIManager.put("TabbedPane.inactiveUnderlineColor", new Color(90,90,90));
            UIManager.put("Frame.background", bg);
            UIManager.put("RootPane.background", bg);
            UIManager.put("Viewport.background", bg);
            UIManager.put("ScrollPane.background", bg);
            UIManager.put("TextComponent.arc", 14);
            UIManager.put("Button.arc", 14);
            UIManager.put("Component.arc", 14);
            UIManager.put("TabbedPane.tabHeight", 34);
            UIManager.put("TabbedPane.tabInsets", new Insets(6,16,6,16));
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("TabbedPane.tabsOverlapBorder", true);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Component.accentColor", new Color(255,215,0));
        } catch (Throwable t) { t.printStackTrace(); }
    }
    private void initWindow() {
        this.setTitle("FRM Proxy v" + ViaProxy.VERSION);
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                ViaProxyWindow.this.eventManager.call(new UICloseEvent());
                ViaProxy.getConfig().save();
                ViaProxy.getSaveManager().save();
            }
        });
        this.setSize(620, 460);
        this.setMinimumSize(new Dimension(560, 420));
        this.setLocationRelativeTo(null);
        this.contentPane.setFont(this.contentPane.getFont().deriveFont(Font.BOLD, 12f));
        this.contentPane.setBackground(new Color(48,48,48));
        this.contentPane.setOpaque(true);
        JPanel trailing=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); trailing.setBackground(new Color(48,48,48)); trailing.setOpaque(true);
        JButton min=new JButton("—"); min.setFocusPainted(false); min.setBorderPainted(false); min.setContentAreaFilled(false); min.setForeground(Color.WHITE); min.setFont(min.getFont().deriveFont(Font.BOLD,12f)); min.setPreferredSize(new Dimension(36,34));
        min.addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseEntered(java.awt.event.MouseEvent e){ min.setBackground(new Color(70,70,70)); min.setContentAreaFilled(true);} public void mouseExited(java.awt.event.MouseEvent e){ min.setContentAreaFilled(false);} });
        min.addActionListener(e-> setState(JFrame.ICONIFIED));
        JButton max=new JButton("□"); max.setFocusPainted(false); max.setBorderPainted(false); max.setContentAreaFilled(false); max.setForeground(Color.WHITE); max.setFont(max.getFont().deriveFont(Font.BOLD,12f)); max.setPreferredSize(new Dimension(36,34));
        max.addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseEntered(java.awt.event.MouseEvent e){ max.setBackground(new Color(70,70,70)); max.setContentAreaFilled(true);} public void mouseExited(java.awt.event.MouseEvent e){ max.setContentAreaFilled(false);} });
        max.addActionListener(e->{ if((getExtendedState() & JFrame.MAXIMIZED_BOTH)==0) setExtendedState(JFrame.MAXIMIZED_BOTH); else setExtendedState(JFrame.NORMAL); });
        JButton close=new JButton("X"); close.setFocusPainted(false); close.setBorderPainted(false); close.setContentAreaFilled(false); close.setForeground(Color.WHITE); close.setFont(close.getFont().deriveFont(Font.BOLD,12f)); close.setPreferredSize(new Dimension(36,34));
        close.addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseEntered(java.awt.event.MouseEvent e){ close.setBackground(new Color(220,50,50)); close.setContentAreaFilled(true);} public void mouseExited(java.awt.event.MouseEvent e){ close.setContentAreaFilled(false);} });
        close.addActionListener(e-> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        trailing.add(min); trailing.add(max); trailing.add(close);
        this.contentPane.putClientProperty("JTabbedPane.trailingComponent", trailing);
        this.setContentPane(this.contentPane);
        this.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60,60,60),1));
        final Point[] drag=new Point[1];
        this.contentPane.addMouseListener(new java.awt.event.MouseAdapter(){ public void mousePressed(java.awt.event.MouseEvent e){ if(e.getY()<34) drag[0]=e.getPoint(); }});
        this.contentPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter(){ public void mouseDragged(java.awt.event.MouseEvent e){ if(drag[0]!=null){ Point p=e.getLocationOnScreen(); setLocation(p.x - drag[0].x, p.y - drag[0].y); }}});
    }
    private void initTabs() {
        RStream.of(this).fields().filter(field -> UITab.class.isAssignableFrom(field.type())).forEach(field -> {
            final UITab tab = field.get();
            this.tabs.add(field.get());
            tab.add(this.contentPane);
            this.eventManager.register(tab);
        });
        this.contentPane.addChangeListener(e -> {
            int idx = contentPane.getSelectedIndex();
            if (idx >= 0 && idx < ViaProxyWindow.this.tabs.size()) ViaProxyWindow.this.tabs.get(idx).onTabOpened();
        });
    }
    public static void openURL(final String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) Desktop.getDesktop().browse(new URI(url));
            else new ProcessBuilder("xdg-open", url).start();
        } catch (Throwable t) { showInfo(I18n.get("generic.could_not_open_url", url)); }
    }
    public static void showException(final Throwable t) {
        Logger.LOGGER.error("Caught exception in thread " + Thread.currentThread().getName(), t);
        StringBuilder b = new StringBuilder("An error occurred:\n");
        b.append("[").append(t.getClass().getSimpleName()).append("] ").append(t.getMessage()).append("\n");
        for (StackTraceElement e : t.getStackTrace()) b.append(e.toString()).append("\n");
        showError(b.toString());
    }
    public static void showInfo(final String m){ showNotification(m, JOptionPane.INFORMATION_MESSAGE); }
    public static void showWarning(final String m){ showNotification(m, JOptionPane.WARNING_MESSAGE); }
    public static void showError(final String m){ showNotification(m, JOptionPane.ERROR_MESSAGE); }
    public static void showNotification(final String message, final int type){ JOptionPane.showMessageDialog(ViaProxy.getForegroundWindow(), message, "FRM Proxy", type); }
}
