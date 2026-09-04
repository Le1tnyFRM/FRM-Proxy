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
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("TabbedPane.background", Color.BLACK);
            UIManager.put("TabbedPane.contentAreaColor", Color.BLACK);
            UIManager.put("TabbedPane.tabAreaBackground", Color.BLACK);
            UIManager.put("TabbedPane.underlineColor", Color.BLACK);
            UIManager.put("TabbedPane.hoverColor", new Color(20,20,20));
            UIManager.put("TabbedPane.selectedBackground", Color.BLACK);
            UIManager.put("TabbedPane.focusColor", Color.BLACK);
            UIManager.put("TabbedPane.inactiveUnderlineColor", Color.BLACK);
            UIManager.put("Frame.background", Color.BLACK);
            UIManager.put("RootPane.background", Color.BLACK);
            UIManager.put("Viewport.background", Color.BLACK);
            UIManager.put("ScrollPane.background", Color.BLACK);
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
        this.setContentPane(this.contentPane);
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
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
