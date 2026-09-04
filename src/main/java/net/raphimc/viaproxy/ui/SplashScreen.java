package net.raphimc.viaproxy.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class SplashScreen extends JFrame {
    private final ProgressPanel progressPanel = new ProgressPanel();
    private final FluidPanel fluidPanel;
    private final CountDownLatch enterLatch = new CountDownLatch(1);
    public SplashScreen() throws IOException {
        setAlwaysOnTop(true); setUndecorated(true);
        try { setBackground(new Color(0,0,0,0)); } catch (Exception ignored) {}
        setType(Window.Type.UTILITY); setSize(560,380); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        fluidPanel=new FluidPanel(enterLatch);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(fluidPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); fluidPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }
    public void awaitEnter() throws InterruptedException { enterLatch.await(); }

    private static class FluidPanel extends JPanel {
        final CountDownLatch latch;
        Timer timer; float t=0; int phase=0; //0 popup,1 fill,2 ding,3 zoom,4 enter
        float fill=0, zoom=1f; boolean dingPlayed=false;
        Rectangle enterBounds=new Rectangle();
        FluidPanel(CountDownLatch l){ this.latch=l; setOpaque(false); setBackground(new Color(0,0,0,0));
            addMouseListener(new MouseAdapter(){ public void mouseClicked(MouseEvent e){ if(phase==4 && enterBounds.contains(e.getPoint())) latch.countDown(); }});
            addMouseMotionListener(new MouseAdapter(){ public void mouseMoved(MouseEvent e){ setCursor(phase==4 && enterBounds.contains(e.getPoint()) ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor()); }});
        }
        void start(){
            timer=new Timer(16, e->{
                t+=0.016f;
                if(phase==0){ if(t>0.45f){ phase=1; t=0; } }
                else if(phase==1){ fill=Math.min(1, t/1.6f); if(fill>=1){ phase=2; t=0; } }
                else if(phase==2){ if(!dingPlayed){ ding(); dingPlayed=true; } if(t>0.35f){ phase=3; t=0; } }
                else if(phase==3){ float z=Math.min(1,t/0.55f); zoom=1 + easeOutBack(z)*0.22f; if(z>=1){ phase=4; t=0; zoom=1.22f; } }
                repaint();
            }); timer.start();
        }
        float easeOutBack(float x){ float c1=1.70158f, c3=c1+1; return 1 + c3*(float)Math.pow(x-1,3) + c1*(float)Math.pow(x-1,2); }
        void ding(){ try{ Toolkit.getDefaultToolkit().beep(); new Thread(()->{ try{ float sr=44100; byte[] buf=new byte[(int)(sr*0.18)]; for(int i=0;i<buf.length;i++){ double ang=2*Math.PI*880*Math.exp(-i/(sr*0.12))*i/sr; double env=Math.exp(-i/(sr*0.09)); buf[i]=(byte)(Math.sin(ang)*env*110); } AudioFormat fmt=new AudioFormat(sr,8,1,true,false); Clip c=AudioSystem.getClip(); c.open(fmt,buf,0,buf.length); c.start(); Thread.sleep(220); c.close(); }catch(Exception ignored){}}).start(); }catch(Exception ignored){} }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            float popup = phase==0 ? Math.min(1, t/0.45f) : 1f;
            float popScale = 0.82f + (float)(1-Math.pow(1-popup,3))*0.18f;
            float popAlpha = Math.min(1, popup*2);
            Graphics2D gg=(Graphics2D)g2.create();
            gg.translate(w/2, h/2 - 6); gg.scale(popScale, popScale); gg.translate(-w/2, -(h/2 -6));
            gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, popAlpha));
            // bg card
            gg.setColor(new Color(10,10,12)); gg.fillRoundRect(14,14,w-28,h-56,22,22);
            gg.setColor(new Color(255,255,255,14)); gg.setStroke(new BasicStroke(1)); gg.drawRoundRect(14,14,w-28,h-56,22,22);
            // FRM PROXY fluid fill
            String txt="FRM PROXY"; gg.setFont(new Font("Arial", Font.BOLD, 44)); FontMetrics fm=gg.getFontMetrics();
            int totalW=fm.stringWidth(txt); int tx=w/2 - totalW/2, ty=h/2 + 8;
            // apply zoom around center
            if(phase>=3){
                Graphics2D gz=(Graphics2D)gg.create();
                gz.translate(w/2, h/2); gz.scale(zoom,zoom); gz.translate(-w/2, -h/2);
                // draw again with zoom for enter phase
                drawFluidText(gz, txt, tx, ty, fm, fill);
                gz.dispose();
            } else {
                drawFluidText(gg, txt, tx, ty, fm, phase==1?fill:(phase>=2?1:0));
            }
            gg.dispose();
            // enter
            if(phase==4){
                g2.setFont(new Font("Arial", Font.BOLD, 20)); FontMetrics fm2=g2.getFontMetrics();
                String enter="> enter <"; int ew=fm2.stringWidth(enter); int ex=w/2 - ew/2, ey=h/2 + 62;
                enterBounds.setBounds(ex-18, ey-22, ew+36, 28);
                // glow
                g2.setColor(new Color(255,215,0,30)); g2.fillRoundRect(ex-14, ey-18, ew+28, 24,12,12);
                g2.setColor(new Color(255,215,0)); g2.setStroke(new BasicStroke(1.2f)); g2.drawRoundRect(ex-14, ey-18, ew+28, 24,12,12);
                g2.drawString(enter, ex, ey);
                // hint
                g2.setColor(new Color(255,255,255,110)); g2.setFont(new Font("Arial", Font.PLAIN, 10)); String hint="click to continue";
                FontMetrics fm3=g2.getFontMetrics(); g2.drawString(hint, w/2 - fm3.stringWidth(hint)/2, ey+18);
            }
        }
        void drawFluidText(Graphics2D g, String txt, int x, int y, FontMetrics fm, float fill){
            // white base
            g.setColor(Color.WHITE);
            for(int i=0;i<txt.length();i++){ String s=String.valueOf(txt.charAt(i)); if(s.equals(" ")) { x+=fm.stringWidth(s); continue; } g.drawString(s,x,y); x+=fm.stringWidth(s); }
            if(fill<=0) return;
            // yellow fluid fill clip from bottom
            x=g.getFontMetrics().getFont().getSize(); // reset x
            // recompute x start
            int startX=g.getClipBounds()!=null?0:0;
            // we need original x
            int ox=0; { FontMetrics f2=g.getFontMetrics(); int tw=f2.stringWidth(txt); ox=g.getClipBounds()==null? (getWidth()/2 - tw/2) : 0; }
            // simpler: use shape clip for each char
            int cx=0; { FontMetrics f2=g.getFontMetrics(); int tw=f2.stringWidth(txt); cx=getWidth()/2 - tw/2; }
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
                    // wavy top line
                    g.drawString(s,curX,y);
                    // wave highlight
                    g.setColor(new Color(255,235,120));
                    if(fill>0.05f && fill<0.98f){
                        int waveY=clipY;
                        g.setStroke(new BasicStroke(1.2f));
                        Path2D wave=new Path2D.Float();
                        wave.moveTo(curX, waveY);
                        for(int px=0;px<cw;px++){
                            float wy=waveY + (float)Math.sin((px+System.currentTimeMillis()*0.01)*0.35)*1.2f;
                            if(px==0) wave.moveTo(curX+px, wy); else wave.lineTo(curX+px, wy);
                        }
                        g.draw(wave);
                    }
                    g.setClip(old);
                }
                curX+=cw;
            }
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text="";
        ProgressPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); setPreferredSize(new Dimension(getWidth(),34)); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g2.setColor(new Color(255,255,255,18)); g2.fillRoundRect(18,10,w-36,h-18,12,12);
            int pw=(int)((w-36)*progress);
            g2.setColor(new Color(255,215,0)); g2.fillRoundRect(18,10,pw,h-18,12,12);
            g2.setColor(new Color(255,255,255,70)); if(pw>12) g2.fillRoundRect(18,10,pw,4,6,6);
            g2.setColor(Color.WHITE); g2.setFont(g2.getFont().deriveFont(Font.BOLD,11f)); FontMetrics fm=g2.getFontMetrics();
            int tw=fm.stringWidth(text); g2.drawString(text,(w-tw)/2, h/2 + fm.getAscent()/2 -1);
        }
    }
}
