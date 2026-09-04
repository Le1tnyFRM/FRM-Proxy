package net.raphimc.viaproxy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.Random;

public class SplashScreen extends JFrame {
    private final ProgressPanel progressPanel = new ProgressPanel();
    private final AnimatedSplashPanel splashPanel = new AnimatedSplashPanel(progressPanel);
    public SplashScreen() throws IOException {
        setAlwaysOnTop(true); setUndecorated(true);
        try { setBackground(new Color(0,0,0,0)); } catch (UnsupportedOperationException ignored) {}
        setType(Window.Type.UTILITY); setSize(560,380); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(splashPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); splashPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class AnimatedSplashPanel extends JPanel {
        final ProgressPanel prog;
        Timer timer; float t=0; Random rnd=new Random(7);
        float[] px=new float[18], py=new float[18], pvx=new float[18], pvy=new float[18], pr=new float[18];
        AnimatedSplashPanel(ProgressPanel p){ this.prog=p; setOpaque(false); setBackground(new Color(0,0,0,0));
            for(int i=0;i<px.length;i++){ px[i]=260+rnd.nextFloat()*80-40; py[i]=180+rnd.nextFloat()*60-30; pvx[i]=(rnd.nextFloat()-0.5f)*0.7f; pvy[i]=-0.6f - rnd.nextFloat()*1.2f; pr[i]=2+ rnd.nextFloat()*3; }
        }
        void start(){ timer=new Timer(16, e->{ t+=0.011f; if(t>1) t=1; for(int i=0;i<px.length;i++){ px[i]+=pvx[i]; py[i]+=pvy[i]; pvy[i]+=0.02f; if(py[i]>300) {py[i]= -10; px[i]=120+rnd.nextFloat()*320;}} repaint(); if(t>=1) timer.stop(); }); timer.start(); }
        float ease(float x){ return 1-(float)Math.pow(1-x,3); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w=getWidth(), h=getHeight();
            // bg
            g2.setColor(new Color(14,14,18)); g2.fillRoundRect(10,10,w-20,h-46,24,24);
            // moving orbs
            float orbT=t*2;
            drawOrb(g2, 90 + (float)Math.sin(orbT*0.9)*18, 90 + (float)Math.cos(orbT*0.7)*10, 160, new Color(0,220,255,55));
            drawOrb(g2, w-110 + (float)Math.cos(orbT*0.8)*14, h-90 + (float)Math.sin(orbT*1.1)*12, 180, new Color(160,80,255,45));
            drawOrb(g2, w/2 + (float)Math.sin(orbT*0.6)*22, h/2 + (float)Math.cos(orbT*0.5)*8, 200, new Color(255,180,60,28));
            // particles fun
            for(int i=0;i<px.length;i++){ g2.setColor(new Color(255,255,255,(int)(110+ 60*Math.sin(t*4+i)))); g2.fill(new Ellipse2D.Float(px[i], py[i], pr[i], pr[i])); }
            // glass card
            int cardW=420, cardH=150; int cx=w/2, cy=h/2 - 10;
            g2.setColor(new Color(255,255,255,14)); g2.fillRoundRect(cx-cardW/2, cy-cardH/2, cardW, cardH, 20,20);
            g2.setColor(new Color(255,255,255,22)); g2.setStroke(new BasicStroke(1.2f)); g2.drawRoundRect(cx-cardW/2, cy-cardH/2, cardW, cardH, 20,20);
            // logo icon - pixel grass block fun
            float iconPop=Math.min(1, ease(Math.min(1, t/0.45f)));
            int bx=cx-130, by=cy-38;
            Graphics2D gb=(Graphics2D)g2.create(); gb.translate(bx, by- (1-iconPop)*14); gb.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, iconPop));
            drawBlock(gb, 0,0, 44); gb.dispose();
            // FRM
            float frmT=Math.min(1, Math.max(0,(t-0.18f)/0.42f));
            float frmScale=0.7f + ease(frmT)*0.3f; float frmAlpha=ease(frmT);
            if(frmT>0){
                Graphics2D gt=(Graphics2D)g2.create(); gt.translate(cx-26, cy-6); gt.scale(frmScale, frmScale);
                gt.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, frmAlpha));
                // glow
                gt.setColor(new Color(0,220,255,60)); gt.setFont(new Font("Arial", Font.BOLD, 42)); gt.drawString("FRM", -1, 2);
                GradientPaint gp=new GradientPaint(0,-16, new Color(80,255,255), 0,24, new Color(120,90,255));
                gt.setPaint(gp); gt.setFont(new Font("Arial", Font.BOLD, 42)); gt.drawString("FRM", 0,0);
                gt.dispose();
            }
            // PROXY
            float proxT=Math.min(1, Math.max(0,(t-0.38f)/0.42f));
            if(proxT>0){
                float py2= (1-ease(proxT))*14;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease(proxT)));
                g2.setColor(new Color(255,255,255,210)); g2.setFont(new Font("Arial", Font.BOLD, 16)); 
                String s="P R O X Y"; // spaced
                FontMetrics fm=g2.getFontMetrics(); int tw=fm.stringWidth(s);
                g2.drawString(s, cx - 26 - tw/2 + 52, cy+26 + (int)py2);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            // tagline
            if(t>0.75f){
                float a=Math.min(1,(t-0.75f)/0.18f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
                g2.setColor(new Color(255,255,255,130)); g2.setFont(new Font("Arial", Font.PLAIN, 11));
                String tag="1.1.2  •  6b6t ready  •  7 themes";
                FontMetrics fm=g2.getFontMetrics(); g2.drawString(tag, cx - fm.stringWidth(tag)/2, cy+46);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }
        void drawOrb(Graphics2D g2,float x,float y,float r, Color c){
            g2.setColor(c); g2.fill(new Ellipse2D.Float(x-r/2,y-r/2,r,r));
            g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(), c.getAlpha()/3)); g2.fill(new Ellipse2D.Float(x-r/3,y-r/3,r*0.6f,r*0.6f));
        }
        void drawBlock(Graphics2D g,int x,int y,int s){
            // grass block pixel
            g.setColor(new Color(120,80,40)); g.fillRect(x,y+s/2,s,s/2);
            g.setColor(new Color(95,180,70)); g.fillRect(x,y,s,s/2);
            g.setColor(new Color(70,150,45)); g.fillRect(x,y+2,s,s/2-4);
            g.setColor(new Color(255,255,255,35)); g.fillRect(x,y, s,4);
            g.setColor(new Color(0,0,0,90)); g.setStroke(new BasicStroke(2)); g.drawRect(x,y,s,s);
            // shine
            g.setColor(new Color(255,255,255,90)); g.fillRect(x+4,y+4,8,8);
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text="FRM Proxy Loading";
        ProgressPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); setPreferredSize(new Dimension(getWidth(),36)); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            // pill bg
            g2.setColor(new Color(255,255,255,18)); g2.fillRoundRect(18,10,w-36,h-18,14,14);
            int pw=(int)((w-36)*progress);
            // gradient fill
            GradientPaint gp=new GradientPaint(18,0, new Color(0,220,255), 18+pw,0, new Color(140,90,255));
            g2.setPaint(gp); g2.fillRoundRect(18,10,pw,h-18,14,14);
            // shimmer
            if(progress>0.05f){
                long tm=System.currentTimeMillis()%1400; float sh=(tm/1400f);
                int sx=18 + (int)(pw*sh) - 24;
                g2.setColor(new Color(255,255,255,85)); g2.fillRoundRect(sx,12,28,h-22,8,8);
            }
            g2.setColor(Color.WHITE); g2.setFont(g2.getFont().deriveFont(Font.BOLD,11f)); FontMetrics fm=g2.getFontMetrics();
            int tw=fm.stringWidth(text); g2.drawString(text,(w-tw)/2, h/2 + fm.getAscent()/2);
        }
    }
}
