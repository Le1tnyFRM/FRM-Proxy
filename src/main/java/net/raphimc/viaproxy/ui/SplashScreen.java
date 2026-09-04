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
        setType(Window.Type.UTILITY); setSize(460,340); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(splashPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); splashPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.reveal=p>0.02f; progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class AnimatedSplashPanel extends JPanel {
        float arrow1X=-160, arrow2X=620; boolean arrowsDone=false;
        float splash=0; boolean splashDone=false; float splashFade=1f;
        final String letters="FRM PROXY";
        final float[] letterY=new float[9];
        final float[] letterVy=new float[9];
        int nextLetter=0; Timer timer;
        final Random rnd=new Random(42);
        final float[] dropX=new float[12], dropY=new float[12], dropR=new float[12];
        AnimatedSplashPanel(){
            setOpaque(false); setBackground(new Color(0,0,0,0));
            for(int i=0;i<letterY.length;i++){ letterY[i]=-80; letterVy[i]=0; }
            for(int i=0;i<dropX.length;i++){ dropR[i]=4+rnd.nextFloat()*6; }
        }
        void start(){
            timer=new Timer(16, e->{
                if(!arrowsDone){
                    arrow1X+=18; arrow2X-=18;
                    if(arrow1X>170 && arrow2X<290) arrowsDone=true;
                } else if(!splashDone){
                    splash+=18;
                    if(splash>220) splashDone=true;
                    for(int i=0;i<dropX.length;i++){ dropX[i]=(float)(Math.cos(i*0.52)* (splash*0.45 + rnd.nextFloat()*20)); dropY[i]=(float)(Math.sin(i*0.52)* (splash*0.35 + rnd.nextFloat()*20)); }
                } else {
                    if(nextLetter < letters.length()){
                        if(letters.charAt(nextLetter)==' '){ nextLetter++; if(nextLetter<letters.length()) letterY[nextLetter]=-80; }
                        else {
                            letterVy[nextLetter]+=2.2f;
                            letterY[nextLetter]+=letterVy[nextLetter];
                            if(letterY[nextLetter]>=0){
                                letterY[nextLetter]=0; letterVy[nextLetter]=0; nextLetter++;
                                if(nextLetter<letters.length() && letters.charAt(nextLetter)==' ') nextLetter++;
                                if(nextLetter<letters.length()) letterY[nextLetter]=-80;
                            }
                        }
                    } else {
                        splashFade-=0.015f; if(splashFade<0.75f) splashFade=0.75f;
                    }
                }
                repaint();
                if(splashDone && nextLetter>=letters.length()){
                    if(splashFade<=0.75f) timer.stop();
                }
            }); timer.start();
        }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int cx=getWidth()/2, cy=getHeight()/2 - 14;
            if(!arrowsDone){
                g2.setColor(new Color(2,188,216)); g2.setStroke(new BasicStroke(12,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2.drawLine((int)arrow1X-60, cy, (int)arrow1X+20, cy);
                Polygon h1=new Polygon(new int[]{(int)arrow1X+20,(int)arrow1X-10,(int)arrow1X-10}, new int[]{cy,cy-22,cy+22},3);
                g2.fillPolygon(h1);
                g2.drawLine((int)arrow2X+60, cy, (int)arrow2X-20, cy);
                Polygon h2=new Polygon(new int[]{(int)arrow2X-20,(int)arrow2X+10,(int)arrow2X+10}, new int[]{cy,cy-22,cy+22},3);
                g2.fillPolygon(h2);
            }
            if(arrowsDone){
                float a = Math.min(1f, splash/160f) * splashFade;
                g2.setColor(new Color(0,0,0,(int)(210*a)));
                Path2D splashPath=new Path2D.Float();
                int points=14; float r=splash/2;
                for(int i=0;i<=points;i++){
                    double ang=i*2*Math.PI/points;
                    float rad=r * (0.78f + 0.22f * (float)Math.sin(i*1.7) + rnd.nextFloat()*0.05f);
                    float x=cx + (float)Math.cos(ang)*rad;
                    float y=cy + (float)Math.sin(ang)*rad*0.75f;
                    if(i==0) splashPath.moveTo(x,y); else splashPath.lineTo(x,y);
                }
                splashPath.closePath(); g2.fill(splashPath);
                g2.setColor(new Color(0,0,0,(int)(180*a)));
                for(int i=0;i<dropX.length;i++){
                    float dx=cx+dropX[i], dy=cy+dropY[i];
                    Ellipse2D d=new Ellipse2D.Float(dx-dropR[i], dy-dropR[i], dropR[i]*2, dropR[i]*2);
                    g2.fill(d);
                }
                g2.setColor(new Color(2,188,216,(int)(90*a))); g2.setStroke(new BasicStroke(3)); g2.draw(splashPath);
                if(splashDone){
                    g2.setFont(new Font("Arial", Font.BOLD, 38));
                    FontMetrics fm=g2.getFontMetrics(); int totalW=fm.stringWidth(letters); int x=cx - totalW/2; int y=cy+10;
                    for(int i=0;i<letters.length();i++){
                        char ch=letters.charAt(i); String s=String.valueOf(ch); int cw=fm.stringWidth(s);
                        if(ch==' '){ x+=cw; continue; }
                        float ly=letterY[i];
                        if(ly < -100) ly=-100;
                        g2.setColor(new Color(0,0,0,140)); g2.drawString(s, x+3, y+(int)ly+3);
                        g2.setColor(Color.WHITE); g2.drawString(s, x, y+(int)ly);
                        x+=cw;
                    }
                }
            }
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text=""; boolean reveal=false;
        ProgressPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); setPreferredSize(new Dimension(getWidth(),32)); }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            g.setColor(Color.WHITE); g.fillRoundRect(0,6,w,h-8,10,10);
            g.setColor(new Color(0,69,104)); g.fillRoundRect(2,8,w-4,h-12,8,8);
            int pw=(int)((w-4)*progress);
            if(reveal){
                g.setColor(new Color(2,188,216)); g.fillRoundRect(2,8,pw,h-12,8,8);
                g.setColor(new Color(255,255,255,50)); g.fillRoundRect(2,8,pw, (h-12)/2,8,8);
            }
            g.setColor(Color.WHITE); g.setFont(g.getFont().deriveFont(Font.BOLD,13f)); FontMetrics fm=g.getFontMetrics();
            int tw=fm.stringWidth(text); g.drawString(text,(w-tw)/2, h/2 + fm.getAscent()/2 +2);
        }
    }
}
