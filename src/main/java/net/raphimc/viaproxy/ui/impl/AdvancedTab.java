package net.raphimc.viaproxy.ui.impl;

import com.viaversion.viaversion.util.DumpUtil;
import gs.mclo.api.MclogsClient;
import gs.mclo.api.response.UploadLogResponse;
import net.lenni0451.commons.swing.GBC;
import net.lenni0451.lambdaevents.EventHandler;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.ui.I18n;
import net.raphimc.viaproxy.ui.UITab;
import net.raphimc.viaproxy.ui.ViaProxyWindow;
import net.raphimc.viaproxy.ui.events.UICloseEvent;
import net.raphimc.viaproxy.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import static net.raphimc.viaproxy.ui.ViaProxyWindow.BODY_BLOCK_PADDING;
import static net.raphimc.viaproxy.ui.ViaProxyWindow.BORDER_PADDING;

public class AdvancedTab extends UITab {
    JTextField bindAddress; JTextField proxy;
    JCheckBox proxyOnlineMode; public JCheckBox legacySkinLoading;
    JCheckBox chatSigning; JCheckBox ignorePacketTranslationErrors; JCheckBox allowBetaPinging; JCheckBox simpleVoiceChatSupport; JCheckBox fakeAcceptResourcePacks;
    JCheckBox autoSetup; JSlider thresholdSlider;
    JButton viaVersionDumpButton; JButton uploadLogsButton;
    private static final int[] THRESHOLDS={64,96,128,192,256,384,512};
    public AdvancedTab(final ViaProxyWindow f){ super(f,"advanced"); }
    @Override protected void init(JPanel c){ c.setLayout(new BorderLayout()); addBody(c); addFooter(c); }
    private void addBody(final Container parent){
        JPanel body=new JPanel(new GridBagLayout());
        JPanel boxes=new JPanel(new GridLayout(0,2,BORDER_PADDING,BORDER_PADDING));
        int y=0;
        {
            JLabel l=new JLabel(I18n.get("tab.advanced.bind_address.label")); l.setToolTipText(I18n.get("tab.advanced.bind_address.tooltip"));
            GBC.create(body).grid(0,y++).insets(BORDER_PADDING,BORDER_PADDING,0,0).anchor(GBC.NORTHWEST).add(l);
            this.bindAddress=new JTextField(); this.bindAddress.setToolTipText(I18n.get("tab.advanced.bind_address.tooltip")); this.bindAddress.setText("0.0.0.0:25568");
            ViaProxy.getSaveManager().uiSave.loadTextField("bind_address",this.bindAddress);
            GBC.create(body).grid(0,y++).weightx(1).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(this.bindAddress);
        }
        {
            JLabel l=new JLabel(I18n.get("tab.advanced.proxy_url.label")); l.setToolTipText(I18n.get("tab.advanced.proxy_url.tooltip"));
            GBC.create(body).grid(0,y++).insets(BODY_BLOCK_PADDING,BORDER_PADDING,0,0).anchor(GBC.NORTHWEST).add(l);
            this.proxy=new JTextField(); this.proxy.setToolTipText(I18n.get("tab.advanced.proxy_url.tooltip"));
            ViaProxy.getSaveManager().uiSave.loadTextField("proxy",this.proxy);
            GBC.create(body).grid(0,y++).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(this.proxy);
        }
        this.proxyOnlineMode=new JCheckBox(I18n.get("tab.advanced.proxy_online_mode.label")); this.proxyOnlineMode.setToolTipText(I18n.get("tab.advanced.proxy_online_mode.tooltip")); this.proxyOnlineMode.setSelected(ViaProxy.getConfig().isProxyOnlineMode()); boxes.add(this.proxyOnlineMode);
        this.legacySkinLoading=new JCheckBox(I18n.get("tab.advanced.legacy_skin_loading.label")); this.legacySkinLoading.setToolTipText(I18n.get("tab.advanced.legacy_skin_loading.tooltip")); ViaProxy.getSaveManager().uiSave.loadCheckBox("legacy_skin_loading",this.legacySkinLoading); boxes.add(this.legacySkinLoading);
        this.chatSigning=new JCheckBox(I18n.get("tab.advanced.chat_signing.label")); this.chatSigning.setToolTipText(I18n.get("tab.advanced.chat_signing.tooltip")); this.chatSigning.setSelected(ViaProxy.getConfig().shouldSignChat()); boxes.add(this.chatSigning);
        this.ignorePacketTranslationErrors=new JCheckBox(I18n.get("tab.advanced.ignore_packet_translation_errors.label")); this.ignorePacketTranslationErrors.setToolTipText(I18n.get("tab.advanced.ignore_packet_translation_errors.tooltip")); this.ignorePacketTranslationErrors.setSelected(ViaProxy.getConfig().shouldIgnoreProtocolTranslationErrors()); boxes.add(this.ignorePacketTranslationErrors);
        this.allowBetaPinging=new JCheckBox(I18n.get("tab.advanced.allow_beta_pinging.label")); this.allowBetaPinging.setToolTipText(I18n.get("tab.advanced.allow_beta_pinging.tooltip")); this.allowBetaPinging.setSelected(ViaProxy.getConfig().shouldAllowBetaPinging()); boxes.add(this.allowBetaPinging);
        this.simpleVoiceChatSupport=new JCheckBox(I18n.get("tab.advanced.simple_voice_chat_support.label")); this.simpleVoiceChatSupport.setToolTipText(I18n.get("tab.advanced.simple_voice_chat_support.tooltip")); this.simpleVoiceChatSupport.setSelected(ViaProxy.getConfig().shouldSupportSimpleVoiceChat()); boxes.add(this.simpleVoiceChatSupport);
        this.fakeAcceptResourcePacks=new JCheckBox(I18n.get("tab.advanced.fake_accept_resource_packs.label")); this.fakeAcceptResourcePacks.setToolTipText(I18n.get("tab.advanced.fake_accept_resource_packs.tooltip")); this.fakeAcceptResourcePacks.setSelected(ViaProxy.getConfig().shouldFakeAcceptResourcePacks()); boxes.add(this.fakeAcceptResourcePacks);
        this.autoSetup=new JCheckBox("Auto Setup"); this.autoSetup.setToolTipText("When ON: if join fails with Badly compressed / timeout, proxy auto-adjusts threshold and retries."); this.autoSetup.setSelected(ViaProxy.getConfig().shouldAutoSetup()); boxes.add(this.autoSetup);
        GBC.create(body).grid(0,y++).insets(BODY_BLOCK_PADDING,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.BOTH).weight(1,1).add(boxes);
        {
            JLabel sLabel=new JLabel("Packet size Threshold (recommended 128 for 6b6t) -- 64 to 512");
            sLabel.setFont(sLabel.getFont().deriveFont(Font.BOLD,11f));
            GBC.create(body).grid(0,y++).insets(BORDER_PADDING,BORDER_PADDING,0,BORDER_PADDING).anchor(GBC.WEST).add(sLabel);
            this.thresholdSlider=new JSlider(0,THRESHOLDS.length-1, idxFor(ViaProxy.getConfig().getCompressionThreshold()));
            this.thresholdSlider.setMajorTickSpacing(1); this.thresholdSlider.setPaintTicks(true); this.thresholdSlider.setSnapToTicks(true);
            Hashtable<Integer,JLabel> tbl=new Hashtable<>();
            for(int i=0;i<THRESHOLDS.length;i++) tbl.put(i,new JLabel(String.valueOf(THRESHOLDS[i])));
            this.thresholdSlider.setLabelTable(tbl); this.thresholdSlider.setPaintLabels(true);
            JLabel val=new JLabel(String.valueOf(ViaProxy.getConfig().getCompressionThreshold()));
            this.thresholdSlider.addChangeListener(e->{ int v=THRESHOLDS[this.thresholdSlider.getValue()]; val.setText(String.valueOf(v)); ViaProxy.getConfig().setCompressionThreshold(v); });
            JPanel row=new JPanel(new BorderLayout(8,0)); row.add(this.thresholdSlider,BorderLayout.CENTER); row.add(val,BorderLayout.EAST);
            GBC.create(body).grid(0,y++).weightx(1).insets(0,BORDER_PADDING,0,BORDER_PADDING).fill(GBC.HORIZONTAL).add(row);
        }
        parent.add(body,BorderLayout.NORTH);
    }
    private int idxFor(int v){ for(int i=0;i<THRESHOLDS.length;i++) if(THRESHOLDS[i]==v) return i; return 4; }
    private void addFooter(final Container c){
        JPanel f=new JPanel(new GridLayout(1,2,BORDER_PADDING,0));
        this.viaVersionDumpButton=new JButton(I18n.get("tab.advanced.create_viaversion_dump.label"));
        this.viaVersionDumpButton.addActionListener(e->{ this.viaVersionDumpButton.setEnabled(false); DumpUtil.postDump(null).whenComplete((url,ex)->{ if(ex!=null){ Logger.LOGGER.error("Failed",ex); SwingUtilities.invokeLater(()->ViaProxyWindow.showError(ex.getMessage())); } else { ViaProxyWindow.openURL(url); Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url),new StringSelection(url)); SwingUtilities.invokeLater(()->ViaProxyWindow.showInfo(I18n.get("tab.advanced.create_viaversion_dump.success"))); } SwingUtilities.invokeLater(()->this.viaVersionDumpButton.setEnabled(true)); }); });
        f.add(this.viaVersionDumpButton);
        this.uploadLogsButton=new JButton(I18n.get("tab.advanced.upload_latest_log.label"));
        this.uploadLogsButton.addActionListener(e->{ org.apache.logging.log4j.core.Logger lg=(org.apache.logging.log4j.core.Logger)LogManager.getRootLogger(); RollingRandomAccessFileAppender ap=(RollingRandomAccessFileAppender)lg.getAppenders().get("LatestFile"); ap.getManager().flush(); File log=new File(ap.getFileName()); try{ this.uploadLogsButton.setEnabled(false); MclogsClient mc=new MclogsClient("ViaProxy",ViaProxy.VERSION); UploadLogResponse r=mc.uploadLog(log.toPath()).get(); ViaProxyWindow.openURL(r.getUrl()); Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(r.getUrl()),new StringSelection(r.getUrl())); ViaProxyWindow.showInfo("<html>"+I18n.get("tab.advanced.upload_latest_log.success","<a href=\"\">"+r.getUrl()+"</a>")+"</html>"); }catch(ExecutionException ex){ if(ex.getCause() instanceof FileNotFoundException) ViaProxyWindow.showError(I18n.get("tab.advanced.upload_latest_log.error_not_found")); else{ Logger.LOGGER.error("Failed",ex.getCause()); ViaProxyWindow.showError(I18n.get("tab.advanced.upload_latest_log.error_generic",ex.getCause().getMessage())); } }catch(Throwable ex){ Logger.LOGGER.error("Failed",ex); ViaProxyWindow.showError(I18n.get("tab.advanced.upload_latest_log.error_generic",ex.getMessage())); } finally{ this.uploadLogsButton.setEnabled(true);} });
        f.add(this.uploadLogsButton);
        JPanel p=new JPanel(new GridBagLayout()); GBC.create(p).grid(0,0).weightx(1).insets(0,BORDER_PADDING,BORDER_PADDING,BORDER_PADDING).fill(GBC.HORIZONTAL).add(f);
        c.add(p,BorderLayout.SOUTH);
    }
    @EventHandler(events = UICloseEvent.class) void applyGuiState(){
        ViaProxy.getSaveManager().uiSave.put("bind_address",this.bindAddress.getText());
        ViaProxy.getSaveManager().uiSave.put("proxy",this.proxy.getText());
        ViaProxy.getConfig().setProxyOnlineMode(this.proxyOnlineMode.isSelected());
        ViaProxy.getSaveManager().uiSave.put("legacy_skin_loading",String.valueOf(this.legacySkinLoading.isSelected()));
        ViaProxy.getConfig().setChatSigning(this.chatSigning.isSelected());
        ViaProxy.getConfig().setIgnoreProtocolTranslationErrors(this.ignorePacketTranslationErrors.isSelected());
        ViaProxy.getConfig().setAllowBetaPinging(this.allowBetaPinging.isSelected());
        ViaProxy.getConfig().setSimpleVoiceChatSupport(this.simpleVoiceChatSupport.isSelected());
        ViaProxy.getConfig().setFakeAcceptResourcePacks(this.fakeAcceptResourcePacks.isSelected());
        ViaProxy.getConfig().setAutoSetup(this.autoSetup.isSelected());
    }
}
