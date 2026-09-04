package net.raphimc.viaproxy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Random;

public class SplashScreen extends JFrame {
    private final ProgressPanel progressPanel = new ProgressPanel();
    private final AnimatedSplashPanel splashPanel = new AnimatedSplashPanel();
    public SplashScreen() throws IOException {
        setAlwaysOnTop(true); setUndecorated(true);
        try { setBackground(new Color(0,0,0,0)); } catch (UnsupportedOperationException ignored) {}
        setType(Window.Type.UTILITY); setSize(520,380); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(splashPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); splashPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class AnimatedSplashPanel extends JPanel {
        long start=System.nanoTime(); Timer timer;
        float t=0; // 0..1 overall
        final String text="FRM PROXY";
        final float[] letterScale=new float[9];
        Path2D cachedSplash=null;
        AnimatedSplashPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); }
        void start(){ timer=new Timer(16, e->{ t+=0.012f; if(t>1f) t=1f; repaint(); if(t>=1f && getProgressDone()) timer.stop(); }); timer.start(); }
        boolean getProgressDone(){ return true; }
        float easeOutCubic(float x){ return 1 - (float)Math.pow(1-x,3); }
        float easeOutBack(float x){ float c1=1.70158f, c3=c1+1; return 1 + c3*(float)Math.pow(x-1,3) + c1*(float)Math.pow(x-1,2); }
        float easeInCubic(float x){ return x*x*x; }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w=getWidth(), h=getHeight(), cx=w/2, cy=h/2 - 8;
            // bg soft gradient
            g2.setColor(new Color(18,18,22, 180)); g2.fillRoundRect(12,12,w-24,h-48,28,28);
            // drop falling 0-0.28
            float dropT=Math.min(1, t/0.28f);
            float dropY = -80 + easeInCubic(dropT)* (cy+80);
            float dropScale = 1 - dropT*0.2f;
            if(dropT<1){
                g2.setColor(new Color(0,0,0,230));
                Ellipse2D drop=new Ellipse2D.Float(cx-14*dropScale, dropY-18*dropScale, 28*dropScale, 36*dropScale);
                g2.fill(drop);
                // trail
                g2.setColor(new Color(0,0,0,60));
                for(int i=1;i<=4;i++){ float a=1-i*0.18f; if(a<0) a=0; Ellipse2D tr=new Ellipse2D.Float(cx-6, dropY - i*12, 12, 10); g2.setColor(new Color(0,0,0,(int)(40*a))); g2.fill(tr); }
            }
            // splash 0.28-0.62
            float splashT=0; if(t>0.28f) splashT=Math.min(1,(t-0.28f)/0.34f);
            float splashA=easeOutBack(splashT);
            float splashR= splashA * 148f;
            if(splashT>0){
                if(cachedSplash==null || Math.abs(splashR - 10)<100) cachedSplash=buildSplash(cx,cy,splashR);
                // shadow
                g2.setColor(new Color(0,0,0,30)); g2.fillOval(cx- (int)splashR/2 +6, cy- (int)(splashR*0.74)/2+6, (int)splashR, (int)(splashR*0.74));
                g2.setColor(Color.BLACK); g2.fill(cachedSplash);
                // droplets
                if(splashT>0.7f){
                    Random r=new Random(99);
                    g2.setColor(Color.BLACK);
                    for(int i=0;i<16;i++){
                        double ang=r.nextDouble()*Math.PI*2;
                        double dist= splashR*0.58 + r.nextDouble()*18;
                        float dx=cx+(float)Math.cos(ang)* (float)dist;
                        float dy=cy+(float)Math.sin(ang)* (float)dist*0.68f;
                        float rr=2+r.nextFloat()*4;
                        g2.fill(new Ellipse2D.Float(dx-rr, dy-rr, rr*2, rr*2));
                    }
                }
                // rim glow
                g2.setColor(new Color(40,180,220, (int)(60*splashA))); g2.setStroke(new BasicStroke(2)); g2.draw(cachedSplash);
            }
            // text reveal 0.55-0.95
            float textT=0; if(t>0.55f) textT=Math.min(1,(t-0.55f)/0.40f);
            if(textT>0){
                g2.setFont(new Font("Inter", Font.BOLD, 38));
                // fallback if Inter not available
                if(!g2.getFont().getFamily().equals("Inter")) g2.setFont(new Font("Arial", Font.BOLD, 38));
                FontMetrics fm=g2.getFontMetrics();
                String s=text; int totalW=fm.stringWidth(s);
                float maxW=splashR*0.78f; if(maxW<140) maxW=140;
                if(totalW>maxW){ float sc=maxW/totalW; g2.setFont(g2.getFont().deriveFont(38*sc)); fm=g2.getFontMetrics(); totalW=fm.stringWidth(s); }
                int x=cx - totalW/2, y=cy+10;
                for(int i=0;i<s.length();i++){
                    char ch=s.charAt(i); String cs=String.valueOf(ch); int cw=fm.stringWidth(cs);
                    if(ch==' '){ x+=cw; continue; }
                    float delay=i*0.07f; float lt=Math.min(1, Math.max(0, (textT-delay)/0.35f));
                    float sc=easeOutBack(lt); float ly=(1-sc)*18;
                    float alpha=lt;
                    // shadow
                    g2.setColor(new Color(0,0,0,(int)(160*alpha))); g2.drawString(cs, x+2, y+(int)ly+2);
                    // yellow gradient text
                    GradientPaint gp=new GradientPaint(x,y-18, new Color(255,235,80), x,y+14, new Color(255,180,0));
                    g2.setPaint(gp);
                    // composite alpha
                    Composite old=g2.getComposite(); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2.drawString(cs, x, y+(int)ly);
                    g2.setComposite(old);
                    // shine dot
                    if(lt>0.6f){ g2.setColor(new Color(255,255,255,(int)(120*(lt-0.6f)*2.5f))); g2.fillOval(x+2, y+(int)ly-14, 4,4); }
                    x+=cw;
                }
            }
            // subtle vignette
            if(t>0.2f){ float a=Math.min(0.18f, (t-0.2f)*0.2f); g2.setColor(new Color(0,0,0,(int)(255*a))); g2.setStroke(new BasicStroke(1)); g2.drawRoundRect(12,12,w-24,h-48,28,28); }
        }
        private Path2D buildSplash(int cx,int cy,float r){
            Path2D p=new Path2D.Float(); int pts=26; float base=r*0.92f;
            float[] xs=new float[pts], ys=new float[pts]; Random rr=new Random(7);
            for(int i=0;i<pts;i++){
                double ang=i*2*Math.PI/pts;
                float wob = 0.08f + 0.14f*(float)Math.sin(i*1.8) + rr.nextFloat()*0.03f;
                float rad = base * (0.88f + wob);
                if(i%7==0) rad+=9;
                xs[i]=cx+(float)Math.cos(ang)*rad;
                ys[i]=cy+(float)Math.sin(ang)*rad*0.74f;
            }
            p.moveTo(xs[0],ys[0]);
            for(int i=0;i<pts;i++){
                int j=(i+1)%pts; float mx=(xs[i]+xs[j])/2, my=(ys[i]+ys[j])/2;
                float cxi=(xs[i]+mx)/2, cyi=(ys[i]+my)/2;
                p.quadTo(cxi,cyi,mx,my);
            }
            p.closePath(); return p;
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text="";
        ProgressPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); setPreferredSize(new Dimension(getWidth(),34)); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g.setColor(new Color(255,255,255,220)); g.fillRoundRect(8,8,w-16,h-16,12,12);
            g.setColor(new Color(18,18,22)); g.fillRoundRect(10,10,w-20,h-20,10,10);
            int pw=(int)((w-20)*progress);
            // shimmer bar
            GradientPaint gp=new GradientPaint(10,0,new Color(2,188,216), 10+pw,0,new Color(80,220,255));
            g2.setPaint(gp); g2.fillRoundRect(10,10,pw,h-20,10,10);
            if(pw>10){
                long tm=System.currentTimeMillis()%1200; float shine=(tm/1200f);
                int sx=(int)(10 + pw*shine) - 30;
                g2.setColor(new Color(255,255,255,70)); g2.fillRect(sx,10,30,h-20);
            }
            g2.setColor(Color.WHITE); g2.setFont(g2.getFont().deriveFont(Font.BOLD,12f)); FontMetrics fm=g2.getFontMetrics();
            int tw=fm.stringWidth(text); g2.drawString(text,(w-tw)/2, h/2 + fm.getAscent()/2 -1);
        }
    }
}
