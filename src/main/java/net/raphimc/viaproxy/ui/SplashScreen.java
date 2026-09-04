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
        setType(Window.Type.UTILITY); setSize(500,380); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(splashPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c); setVisible(true); splashPanel.start();
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.reveal=p>0.02f; progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class AnimatedSplashPanel extends JPanel {
        float arrow1X=-220, arrow2X=700; boolean arrowsDone=false;
        float splash=0; boolean splashDone=false;
        final String letters="FRM PROXY";
        final float[] letterY=new float[9];
        final float[] letterVy=new float[9];
        int nextLetter=0; Timer timer;
        final Random rnd=new Random(1234);
        final float[] dropX=new float[18], dropY=new float[18], dropR=new float[18];
        Path2D cachedPath=null; float cachedSplash=-1;
        AnimatedSplashPanel(){
            setOpaque(false); setBackground(new Color(0,0,0,0));
            for(int i=0;i<letterY.length;i++){ letterY[i]=-90; letterVy[i]=0; }
            for(int i=0;i<dropX.length;i++){ dropR[i]=3+rnd.nextFloat()*5; }
        }
        void start(){
            timer=new Timer(20, e->{
                if(!arrowsDone){
                    arrow1X+=10; arrow2X-=10;
                    if(arrow1X>170 && arrow2X<310) arrowsDone=true;
                } else if(!splashDone){
                    splash+=10;
                    if(splash>280) { splashDone=true; splash=280; }
                    if(splashDone){
                        Random fr=new Random(99);
                        for(int i=0;i<dropX.length;i++){
                            double ang=i*0.55 + fr.nextFloat()*0.12;
                            double dist=splash*0.54 + fr.nextFloat()*12;
                            dropX[i]=(float)(Math.cos(ang)*dist);
                            dropY[i]=(float)(Math.sin(ang)*dist*0.68);
                        }
                        cachedPath=null;
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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // left arrow - detailed: shaft stops at base, head at tip, with outline and fletching
                int ax1=(int)arrow1X, ax2=(int)arrow2X;
                drawDetailedArrow(g2, ax1-70, cy, ax1-12, cy, true);
                drawDetailedArrow(g2, ax2+70, cy, ax2+12, cy, false);
            }
            if(arrowsDone){
                float a = Math.min(1f, splash/160f);
                if(cachedPath==null || cachedSplash!=splash){
                    cachedPath=buildSplashPath(cx,cy,splash);
                    cachedSplash=splash;
                }
                g2.setColor(new Color(0,0,0,(int)(255*a)));
                g2.fill(cachedPath);
                if(splashDone){
                    g2.setColor(new Color(0,0,0,(int)(255*a)));
                    for(int i=0;i<dropX.length;i++){
                        float dx=cx+dropX[i], dy=cy+dropY[i];
                        Ellipse2D d=new Ellipse2D.Float(dx-dropR[i], dy-dropR[i], dropR[i]*2, dropR[i]*2);
                        g2.fill(d);
                    }
                }
                if(splashDone){
                    g2.setFont(new Font("Arial", Font.BOLD, 32));
                    FontMetrics fm=g2.getFontMetrics(); int totalW=fm.stringWidth(letters); int x=cx - totalW/2; int y=cy+11;
                    float maxW=splash*0.58f;
                    if(totalW > maxW){
                        float scale=maxW/totalW;
                        g2.setFont(new Font("Arial", Font.BOLD, (int)(32*scale)));
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
        private void drawDetailedArrow(Graphics2D g2,int x0,int y0,int x1,int y1,boolean right){
            int dx=x1-x0, dy=y1-y0; double len=Math.hypot(dx,dy); double ang=Math.atan2(dy,dx);
            // shaft
            g2.setColor(new Color(2,188,216)); g2.setStroke(new BasicStroke(18,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.drawLine(x0,y0,x1,y1);
            g2.setColor(new Color(0,130,170,90)); g2.setStroke(new BasicStroke(18,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.drawLine(x0+6,y0+2,x1-4,y1+2);
            // head - sharp, tip exactly at x1,y1
            int headLen=36, headW=32;
            double tipX=x1, tipY=y1;
            double baseX=x1 - Math.cos(ang)*headLen;
            double baseY=y1 - Math.sin(ang)*headLen;
            double perpX=-Math.sin(ang)*headW, perpY=Math.cos(ang)*headW;
            Polygon head=new Polygon(
                new int[]{(int)tipX,(int)(baseX+perpX),(int)(baseX-perpX)},
                new int[]{(int)tipY,(int)(baseY+perpY),(int)(baseY-perpY)},3);
            g2.setColor(new Color(2,188,216)); g2.fillPolygon(head);
            g2.setColor(new Color(0,90,120)); g2.setStroke(new BasicStroke(2)); g2.drawPolygon(head);
            // fletching at tail
            int fx0=x0, fy0=y0;
            int fLen=18, fW=14;
            double fBaseX=x0 + Math.cos(ang)*fLen;
            double fBaseY=y0 + Math.sin(ang)*fLen;
            Polygon fletch=new Polygon(
                new int[]{(int)fx0,(int)(fBaseX+perpX*0.45),(int)(fBaseX-perpX*0.45)},
                new int[]{(int)fy0,(int)(fBaseY+perpY*0.45),(int)(fBaseY-perpY*0.45)},3);
            // tail is V shape - draw as two lines
            g2.setColor(new Color(2,188,216,200));
            g2.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.drawLine(fx0,fy0,(int)(fBaseX+perpX*0.5),(int)(fBaseY+perpY*0.5));
            g2.drawLine(fx0,fy0,(int)(fBaseX-perpX*0.5),(int)(fBaseY-perpY*0.5));
        }
        private Path2D buildSplashPath(int cx,int cy,float splash){
            Path2D p=new Path2D.Float();
            int points=28; float r=splash/2 *1.08f;
            float[] xs=new float[points], ys=new float[points];
            Random r2=new Random(42);
            for(int i=0;i<points;i++){
                double ang=i*2*Math.PI/points;
                float spike = (i%4==0) ? 0.30f : (i%6==1 ? 0.18f : 0.06f);
                float rad = r * (0.86f + spike * (float)Math.sin(i*2.2) + r2.nextFloat()*0.02f);
                if(i==3 || i==10 || i==16 || i==22) rad += 14 + r2.nextFloat()*6;
                xs[i]=cx + (float)Math.cos(ang)*rad;
                ys[i]=cy + (float)Math.sin(ang)*rad*0.74f;
            }
            p.moveTo(xs[0], ys[0]);
            for(int i=0;i<points;i++){
                int j=(i+1)%points;
                float mx=(xs[i]+xs[j])/2, my=(ys[i]+ys[j])/2;
                float cxi = (xs[i]+mx)/2;
                float cyi = (ys[i]+my)/2;
                p.quadTo(cxi, cyi, mx, my);
            }
            p.closePath(); return p;
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
