package net.raphimc.viaproxy.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class SplashScreen extends JFrame {
    private final ProgressPanel progressPanel = new ProgressPanel();
    private final FluidPanel fluidPanel;
    private final CountDownLatch latch = new CountDownLatch(1);
    public SplashScreen() throws IOException {
        setTitle("FRM Proxy v" + net.raphimc.viaproxy.ViaProxy.VERSION);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(620,460); setLocationRelativeTo(null);
        setUndecorated(false);
        fluidPanel=new FluidPanel(latch, progressPanel);
        JPanel c=new JPanel(new BorderLayout()); c.setBackground(new Color(18,18,20));
        c.add(fluidPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); fluidPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }
    public void awaitEnter() throws InterruptedException { latch.await(); }
    public void morphToMain(ViaProxyWindow win){
        getContentPane().removeAll();
        setContentPane(win.contentPane);
        setTitle(win.getTitle());
        revalidate(); repaint();
        latch.countDown();
    }

    private static class FluidPanel extends JPanel {
        final CountDownLatch latch; final ProgressPanel prog;
        Timer timer; float t=0; int phase=0; float fill=0; boolean dingPlayed=false; float fade=1f;
        FluidPanel(CountDownLatch l, ProgressPanel p){ this.latch=l; this.prog=p; setOpaque(false); setBackground(new Color(18,18,20)); }
        void start(){
            timer=new Timer(16, e->{
                t+=0.016f;
                float target=prog.progress;
                if(phase==0){ if(t>0.35f){ phase=1; t=0; } }
                else if(phase==1){ fill+=(target - fill)*0.14f; if(fill>0.99f && target>=0.99f){ fill=1; phase=2; t=0; } }
                else if(phase==2){ if(!dingPlayed){ ding(); dingPlayed=true; } if(t>0.30f){ phase=3; t=0; } }
                else if(phase==3){ fade-=0.07f; if(fade<=0){ fade=0; latch.countDown(); timer.stop(); } }
                repaint();
            }); timer.start();
        }
        void ding(){ new Thread(()->{ try{ float sr=44100; int len=(int)(sr*0.16); byte[] buf=new byte[len*2]; for(int i=0;i<len;i++){ double tt=i/sr; double freq=880*Math.exp(-tt*1.8); double env=Math.exp(-tt*7)*0.85; short s=(short)(Math.sin(2*Math.PI*freq*tt)*env*14000); buf[2*i]=(byte)(s&0xff); buf[2*i+1]=(byte)((s>>8)&0xff); } AudioFormat fmt=new AudioFormat(sr,16,1,true,false); Clip c=AudioSystem.getClip(); c.open(fmt,buf,0,buf.length); FloatControl vol=(FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN); try{vol.setValue(-6f);}catch(Exception ignored){} c.start(); Thread.sleep(200); c.close(); }catch(Exception ignored){}}).start(); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            if(phase==3){
                float f=1-fade; Color bg=new Color(18,18,20);
                g2.setColor(new Color((int)(bg.getRed()*f + bg.getRed()*(1-f)), (int)(bg.getGreen()*f+bg.getGreen()*(1-f)), (int)(bg.getBlue()*f+bg.getBlue()*(1-f)))); g2.fillRect(0,0,w,h);
                g2.setColor(new Color(255,255,255,(int)(180*fade))); g2.setFont(new Font("Arial", Font.BOLD, 44)); FontMetrics fm=g2.getFontMetrics();
                String txt="FRM PROXY"; int totalW=fm.stringWidth(txt); int tx=w/2 - totalW/2, ty=h/2+8;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
                drawFluid(g2, txt, tx, ty, fm, fill);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                return;
            }
            float popup = phase==0 ? Math.min(1, t/0.35f) : 1f;
            float popScale = 0.85f + (float)(1-Math.pow(1-popup,3))*0.15f;
            float popAlpha = Math.min(1, popup*2);
            Graphics2D gg=(Graphics2D)g2.create();
            gg.translate(w/2, h/2 -6); gg.scale(popScale, popScale); gg.translate(-w/2, -(h/2-6));
            gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, popAlpha));
            gg.setColor(new Color(18,18,20)); gg.fillRect(0,0,w,h);
            String txt="FRM PROXY"; gg.setFont(new Font("Arial", Font.BOLD, 44)); FontMetrics fm=gg.getFontMetrics();
            int totalW=fm.stringWidth(txt); int tx=w/2 - totalW/2, ty=h/2+8;
            drawFluid(gg, txt, tx, ty, fm, phase==1?fill:1);
            gg.dispose();
        }
        void drawFluid(Graphics2D g, String txt, int x, int y, FontMetrics fm, float fill){
            g.setColor(Color.WHITE);
            for(int i=0;i<txt.length();i++){ String s=String.valueOf(txt.charAt(i)); if(s.equals(" ")) { x+=fm.stringWidth(s); continue; } g.drawString(s,x,y); x+=fm.stringWidth(s); }
            if(fill<=0) return;
            int cx=0; { int tw=fm.stringWidth(txt); cx=getWidth()/2 - tw/2; }
            int curX=cx;
            for(int i=0;i<txt.length();i++){
                String s=String.valueOf(txt.charAt(i)); int cw=fm.stringWidth(s);
                if(!s.equals(" ")){
                    Shape old=g.getClip();
                    int charH=fm.getAscent()+fm.getDescent();
                    int fillH=(int)(charH*fill);
                    int clipY=y - fm.getAscent() + (charH - fillH);
                    g.setClip(new Rectangle(curX, clipY, cw, fillH));
                    g.setColor(new Color(255,215,0));
                    g.drawString(s,curX,y);
                    g.setClip(old);
                }
                curX+=cw;
            }
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text="";
        ProgressPanel(){ setOpaque(false); setBackground(new Color(18,18,20)); setPreferredSize(new Dimension(getWidth(),34)); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            FluidPanel fp=null; Container p=getParent(); while(p!=null){ if(p instanceof SplashScreen) { fp=((SplashScreen)p).fluidPanel; break; } p=p.getParent(); }
            boolean greying = fp!=null && fp.phase==3;
            Color barBg=new Color(18,18,20);
            if(greying) barBg=new Color(18,18,20);
            g2.setColor(new Color(255,255,255,18)); g2.fillRoundRect(10,10,w-20,h-18,6,6);
            int pw=(int)((w-20)*progress);
            g2.setColor(greying? new Color(110,110,110) : new Color(255,215,0)); g2.fillRoundRect(10,10,pw,h-18,6,6);
            g2.setColor(Color.WHITE); g2.setFont(g2.getFont().deriveFont(Font.BOLD,11f)); FontMetrics fm=g2.getFontMetrics();
            int tw=fm.stringWidth(text); g2.drawString(text,(w-tw)/2, h/2 + fm.getAscent()/2 -1);
        }
    }
}
