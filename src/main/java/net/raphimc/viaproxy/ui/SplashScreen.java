package net.raphimc.viaproxy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

public class SplashScreen extends JFrame {
    private final ProgressPanel progressPanel = new ProgressPanel();
    private final AnimatedSplashPanel splashPanel = new AnimatedSplashPanel();
    public SplashScreen() throws IOException {
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);
        try { this.setBackground(new Color(0,0,0,0)); } catch (UnsupportedOperationException ignored) {}
        this.setType(Window.Type.UTILITY);
        this.setSize(420, 320);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        init();
        setVisible(true);
        splashPanel.start();
    }
    private void init() {
        JPanel c=new JPanel(new BorderLayout()); c.setOpaque(false); c.setBackground(new Color(0,0,0,0));
        c.add(splashPanel, BorderLayout.CENTER); c.add(progressPanel, BorderLayout.SOUTH);
        setContentPane(c);
    }
    public float getProgress(){ return progressPanel.progress; }
    public void setProgress(float p){ progressPanel.progress=Math.max(0,Math.min(1,p)); progressPanel.reveal = p>0.05; progressPanel.repaint(); }
    public void setText(String t){ progressPanel.text=t; progressPanel.repaint(); }

    private static class AnimatedSplashPanel extends JPanel {
        float arrow1X=-120, arrow2X=600; boolean arrowsDone=false;
        float splashRadius=0; boolean splashDone=false;
        float[] letterY=new float[8]; boolean[] landed=new boolean[8];
        String letters="FRM PROXY"; int nextLetter=0; Timer timer;
        long startTime;
        AnimatedSplashPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); for(int i=0;i<letterY.length;i++) letterY[i]=-40; }
        void start(){
            startTime=System.currentTimeMillis();
            timer=new Timer(16, e->{
                long t=System.currentTimeMillis()-startTime;
                if(!arrowsDone){
                    arrow1X+=14; arrow2X-=14;
                    if(arrow1X>180 && arrow2X<220){ arrowsDone=true; }
                } else if(!splashDone){
                    splashRadius+=16;
                    if(splashRadius>220) splashDone=true;
                } else {
                    if(nextLetter<letters.length()){
                        letterY[nextLetter]+=18;
                        if(letterY[nextLetter]>=0){ landed[nextLetter]=true; letterY[nextLetter]=0; nextLetter++; if(nextLetter<letters.length()) letterY[nextLetter]=-40; }
                    }
                }
                repaint();
                if(splashDone && nextLetter>=letters.length()) timer.stop();
            }); timer.start();
        }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx=getWidth()/2, cy=getHeight()/2 - 10;
            if(!arrowsDone){
                g2.setColor(new Color(2,188,216)); g2.setStroke(new BasicStroke(6,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                Polygon a1=new Polygon(new int[]{(int)arrow1X,(int)arrow1X+40,(int)arrow1X+40}, new int[]{cy-18,cy,cy+18},3);
                g2.fill(a1); g2.drawLine((int)arrow1X-40, cy, (int)arrow1X+10, cy);
                Polygon a2=new Polygon(new int[]{(int)arrow2X,(int)arrow2X-40,(int)arrow2X-40}, new int[]{cy-18,cy,cy+18},3);
                g2.fill(a2); g2.drawLine((int)arrow2X+40, cy, (int)arrow2X-10, cy);
            }
            if(arrowsDone){
                float alpha = Math.min(1f, splashRadius/180f);
                g2.setColor(new Color(0,0,0,(int)(alpha*210)));
                Ellipse2D e=new Ellipse2D.Float(cx - splashRadius/2, cy - splashRadius/2, splashRadius, splashRadius);
                g2.fill(e);
                if(splashDone){
                    g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 36));
                    FontMetrics fm=g2.getFontMetrics(); String txt="FRM PROXY";
                    int totalW=fm.stringWidth(txt); int x=cx - totalW/2; int y=cy+8;
                    for(int i=0;i<txt.length();i++){
                        char ch=txt.charAt(i); String s=String.valueOf(ch);
                        int cw=fm.stringWidth(s);
                        int ly=(int)letterY[i];
                        if(ch==' ') { x+=cw; continue; }
                        // shadow
                        g2.setColor(new Color(0,0,0,120)); g2.drawString(s, x+2, y+ly+2);
                        g2.setColor(Color.WHITE); g2.drawString(s, x, y+ly);
                        x+=cw;
                    }
                }
            }
        }
    }
    private static class ProgressPanel extends JPanel {
        float progress=0; String text=""; boolean reveal=false;
        ProgressPanel(){ setOpaque(false); setBackground(new Color(0,0,0,0)); setPreferredSize(new Dimension(getWidth(),30)); }
        @Override protected void paintComponent(Graphics g){
            if(g instanceof Graphics2D g2d){ g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); }
            int w=getWidth(), h=getHeight();
            // slide-in from left when reveal
            int barW = reveal ? w : 0;
            if(reveal){
                long t=System.currentTimeMillis()%1200; float slide=Math.min(1f, progress*3);
                barW=(int)(w*slide);
            }
            g.setColor(Color.WHITE); g.fillRect(0,5,w,h-5);
            g.setColor(new Color(0,69,104)); g.fillRect(2,7,w-4,h-9);
            g.setColor(new Color(2,188,216)); int pw=(int)((w-4)*progress); g.fillRect(2,7,pw,h-9);
            g.setColor(Color.WHITE); g.setFont(g.getFont().deriveFont(13f)); FontMetrics fm=g.getFontMetrics();
            int tw=fm.stringWidth(text); g.drawString(text,(w-tw)/2,(h - fm.getHeight())/2 + fm.getAscent()+4);
        }
    }
}
