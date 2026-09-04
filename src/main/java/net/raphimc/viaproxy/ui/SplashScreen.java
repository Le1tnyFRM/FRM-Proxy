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
        setType(Window.Type.UTILITY); setSize(480,360); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(splashPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); splashPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.reveal=p>0.02f; progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class AnimatedSplashPanel extends JPanel {
        float arrow1X=-200, arrow2X=680; boolean arrowsDone=false;
        float splash=0; boolean splashDone=false;
        final String letters="FRM PROXY";
        final float[] letterY=new float[9];
        final float[] letterVy=new float[9];
        int nextLetter=0; Timer timer;
        final Random rnd=new Random(42);
        final float[] dropX=new float[22], dropY=new float[22], dropR=new float[22];
        AnimatedSplashPanel(){
            setOpaque(false); setBackground(new Color(0,0,0,0));
            for(int i=0;i<letterY.length;i++){ letterY[i]=-90; letterVy[i]=0; }
            for(int i=0;i<dropX.length;i++){ dropR[i]=2+rnd.nextFloat()*6; }
        }
        void start(){
            timer=new Timer(20, e->{
                if(!arrowsDone){
                    arrow1X+=10; arrow2X-=10;
                    if(arrow1X>170 && arrow2X<310) arrowsDone=true;
                } else if(!splashDone){
                    splash+=14;
                    if(splash>250) splashDone=true;
                    for(int i=0;i<dropX.length;i++){
                        double ang=i*0.48 + rnd.nextFloat()*0.2;
                        double dist=splash*0.52 + rnd.nextFloat()*28;
                        dropX[i]=(float)(Math.cos(ang)*dist);
                        dropY[i]=(float)(Math.sin(ang)*dist*0.72);
                    }
                } else {
                    if(nextLetter < letters.length()){
                        if(letters.charAt(nextLetter)==' '){ nextLetter++; if(nextLetter<letters.length()) letterY[nextLetter]=-90; }
                        else {
                            letterVy[nextLetter]+=2.4f;
                            letterY[nextLetter]+=letterVy[nextLetter];
                            if(letterY[nextLetter]>=0){
                                letterY[nextLetter]=0; letterVy[nextLetter]=0; nextLetter++;
                                if(nextLetter<letters.length() && letters.charAt(nextLetter)==' ') nextLetter++;
                                if(nextLetter<letters.length()) letterY[nextLetter]=-90;
                            }
                        }
                    }
                }
                repaint();
                if(splashDone && nextLetter>=letters.length()) timer.stop();
            }); timer.start();
        }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int cx=getWidth()/2, cy=getHeight()/2 - 10;
            if(!arrowsDone){
                g2.setColor(new Color(2,188,216)); g2.setStroke(new BasicStroke(16,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2.drawLine((int)arrow1X-70, cy, (int)arrow1X+28, cy);
                Polygon h1=new Polygon(new int[]{(int)arrow1X+28,(int)arrow1X-12,(int)arrow1X-12}, new int[]{cy,cy-30,cy+30},3);
                g2.fillPolygon(h1);
                g2.drawLine((int)arrow2X+70, cy, (int)arrow2X-28, cy);
                Polygon h2=new Polygon(new int[]{(int)arrow2X-28,(int)arrow2X+12,(int)arrow2X+12}, new int[]{cy,cy-30,cy+30},3);
                g2.fillPolygon(h2);
            }
            if(arrowsDone){
                float a = Math.min(1f, splash/160f);
                g2.setColor(new Color(0,0,0,(int)(255*a)));
                Path2D p=new Path2D.Float();
                int points=24; float r=splash/2 *0.92f;
                for(int i=0;i<=points;i++){
                    double ang=i*2*Math.PI/points;
                    float spike = (i%3==0) ? 0.35f : (i%5==0 ? 0.25f : 0.12f);
                    float rad = r * (0.82f + spike * (float)Math.sin(i*2.9) + rnd.nextFloat()*0.08f);
                    if(i==2 || i==7 || i==11 || i==17) rad += 18 + rnd.nextFloat()*12;
                    float x=cx + (float)Math.cos(ang)*rad;
                    float y=cy + (float)Math.sin(ang)*rad*0.78f;
                    if(i==0) p.moveTo(x,y); else p.lineTo(x,y);
                }
                p.closePath(); g2.fill(p);
                g2.setColor(new Color(0,0,0,(int)(255*a)));
                for(int i=0;i<dropX.length;i++){
                    float dx=cx+dropX[i], dy=cy+dropY[i];
                    Ellipse2D d=new Ellipse2D.Float(dx-dropR[i], dy-dropR[i], dropR[i]*2 + rnd.nextFloat()*3, dropR[i]*2);
                    g2.fill(d);
                }
                if(splashDone){
                    g2.setFont(new Font("Arial", Font.BOLD, 34));
                    FontMetrics fm=g2.getFontMetrics(); int totalW=fm.stringWidth(letters); int x=cx - totalW/2; int y=cy+11;
                    if(totalW > splash*0.82){
                        float scale=(splash*0.82f)/totalW;
                        g2.setFont(new Font("Arial", Font.BOLD, (int)(34*scale)));
                        fm=g2.getFontMetrics(); totalW=fm.stringWidth(letters); x=cx - totalW/2;
                    }
                    for(int i=0;i<letters.length();i++){
                        char ch=letters.charAt(i); String s=String.valueOf(ch); int cw=fm.stringWidth(s);
                        if(ch==' '){ x+=cw; continue; }
                        float ly=letterY[i]; if(ly<-110) ly=-110;
                        g2.setColor(new Color(0,0,0,160)); g2.drawString(s, x+2, y+(int)ly+2);
                        g2.setColor(new Color(255,215,0));
                        g2.drawString(s, x, y+(int)ly);
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
