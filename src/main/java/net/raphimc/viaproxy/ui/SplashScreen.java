package net.raphimc.viaproxy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Random;

public class SplashScreen extends JFrame {
    private final ProgressPanel progressPanel = new ProgressPanel();
    private final CoolPanel cool = new CoolPanel();
    public SplashScreen() throws IOException {
        setAlwaysOnTop(true); setUndecorated(true);
        try { setBackground(new Color(0,0,0,0)); } catch (Exception ignored) {}
        setType(Window.Type.UTILITY); setSize(560,360); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(cool, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); cool.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class CoolPanel extends JPanel {
        Timer tm; float t=0; Random r=new Random(8);
        CoolPanel(){ setOpaque(false); }
        void start(){ tm=new Timer(16, e->{ t+=0.014f; if(t>1) t=1; repaint(); if(t>=1) tm.stop(); }); tm.start(); }
        float ease(float x){ return 1-(float)Math.pow(1-x,3); }
        float back(float x){ float c1=1.70158f, c3=c1+1; return 1 + c3*(float)Math.pow(x-1,3) + c1*(float)Math.pow(x-1,2); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), cx=w/2, cy=h/2-10;
            // bg
            g2.setColor(new Color(11,14,19)); g2.fillRoundRect(8,8,w-16,h-44,22,22);
            // grid subtle
            g2.setColor(new Color(255,255,255,10)); g2.setStroke(new BasicStroke(1));
            for(int i=0;i<w;i+=28){ g2.drawLine(i,12,i-24,h-32); }
            // underline draw
            float ul = Math.min(1, Math.max(0,(t-0.15f)/0.45f));
            int ulW=(int)(300*ease(ul)); g2.setColor(new Color(0,220,255)); g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx-ulW/2, cy+36, cx-ulW/2 + ulW, cy+36);
            g2.setColor(new Color(0,220,255,70)); g2.drawLine(cx-ulW/2, cy+38, cx-ulW/2 + ulW, cy+38);
            // FRM cool pop
            float frmT=Math.min(1, Math.max(0,(t-0.05f)/0.5f));
            float sc=0.6f + back(frmT)*0.4f; float a= ease(frmT);
            if(frmT>0){
                Graphics2D gg=(Graphics2D)g2.create(); gg.translate(cx, cy-8); gg.scale(sc,sc); gg.translate(-84, -18);
                gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
                // glow
                gg.setColor(new Color(0,220,255,50)); gg.setFont(new Font("Arial", Font.BOLD, 52)); gg.drawString("FRM",2,36);
                GradientPaint gp=new GradientPaint(0,0,new Color(110,255,255), 120,40,new Color(120,90,255));
                gg.setPaint(gp); gg.setFont(new Font("Arial", Font.BOLD, 52)); gg.drawString("FRM",0,34);
                gg.dispose();
            }
            // PROXY staggered
            String pxy="PROXY"; g2.setFont(new Font("Arial", Font.BOLD, 15)); FontMetrics fm=g2.getFontMetrics();
            int total=0; for(char c:pxy.toCharArray()) total+=fm.stringWidth(String.valueOf(c))+8;
            total-=8; int sx=cx - total/2, sy=cy+62;
            for(int i=0;i<pxy.length();i++){
                float d=i*0.11f; float lt=Math.min(1, Math.max(0,(t-0.42f-d)/0.38f));
                float yoff=(1-ease(lt))*18; float al=ease(lt);
                String s=String.valueOf(pxy.charAt(i));
                Graphics2D gg=(Graphics2D)g2.create(); gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, al));
                gg.setColor(new Color(255,255,255,200)); gg.setFont(new Font("Arial", Font.BOLD, 15));
                gg.drawString(s, sx, sy + (int)yoff);
                // underline tick
                if(lt>0.8f){ gg.setColor(new Color(255,215,0)); gg.fillRect(sx, sy+6, fm.stringWidth(s),2); }
                g2.dispose(); // not needed
                sx+=fm.stringWidth(s)+8;
                g2.setColor(new Color(255,255,255,(int)(200*al))); g2.setFont(new Font("Arial", Font.BOLD, 15)); g2.drawString(s, sx - fm.stringWidth(s)-8, sy + (int)yoff);
            }
            // orbiting dots fun
            if(t>0.25f){
                for(int i=0;i<8;i++){
                    double ang= i*0.785 + t*2.2;
                    float rad= 92 + (float)Math.sin(t*3+i)*4;
                    float dx=cx + (float)Math.cos(ang)*rad;
                    float dy=cy -8 + (float)Math.sin(ang)*rad*0.62f;
                    float al= (float)(0.5+0.5*Math.sin(t*5+i));
                    g2.setColor(new Color(0,220,255,(int)(90*al))); g2.fillOval((int)dx-3,(int)dy-3,6,6);
                }
            }
            // version tag
            if(t>0.85f){
                float va=Math.min(1,(t-0.85f)/0.15f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, va));
                g2.setColor(new Color(255,255,255,120)); g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String tag="FRM 1.1.2  •  BUILT FOR 6B6T"; FontMetrics fm2=g2.getFontMetrics();
                g2.drawString(tag, cx - fm2.stringWidth(tag)/2, h-52);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text="Loading";
        ProgressPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); setPreferredSize(new Dimension(getWidth(),34)); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g2.setColor(new Color(255,255,255,16)); g2.fillRoundRect(16,10,w-32,h-16,12,12);
            int pw=(int)((w-32)*progress);
            GradientPaint gp=new GradientPaint(16,0,new Color(0,220,255),16+pw,0,new Color(120,90,255));
            g2.setPaint(gp); g2.fillRoundRect(16,10,pw,h-16,12,12);
            if(pw>20){ long tm=System.currentTimeMillis()%1100; float sh=(tm/1100f); int sx=16+(int)(pw*sh)-18; g2.setColor(new Color(255,255,255,80)); g2.fillRoundRect(sx,12,22,h-20,6,6); }
            g2.setColor(Color.WHITE); g2.setFont(g2.getFont().deriveFont(Font.BOLD,11f)); FontMetrics fm=g2.getFontMetrics();
            int tw=fm.stringWidth(text); g2.drawString(text,(w-tw)/2,h/2+fm.getAscent()/2-1);
        }
    }
}
