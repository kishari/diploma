package hu.messaging.client.media.video;


import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.Calendar;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JComponent;



public class Imager extends Panel implements ActionListener{

    public static Player player=null;
    public CaptureDeviceInfo di=null;
    public MediaLocator ml=null;
    public JButton capture=null;
    public static Buffer buf=null;
    public static Image img=null;
    public VideoFormat vf=null;
    public static BufferToImage btoi=null;
    public static ImagePanel imgpanel=null;
    public int capturenumber=0;

    public Imager(){
        setLayout(new BorderLayout());
        setSize(320,550);
        imgpanel=new ImagePanel();
        capture= new JButton("Capture");
        capture.addActionListener(this);
        String str = "vfw:Microsoft WDM Image Capture (Win32):0";
        di=CaptureDeviceManager.getDevice(str);
        ml = new MediaLocator(str);
        try{
            player=Manager.createRealizedPlayer(ml);
            player.start();
            Component comp;
            if((comp = player.getVisualComponent())!=null){
                add(comp,BorderLayout.NORTH);
            }
            add(capture,BorderLayout.CENTER);
            add(imgpanel,BorderLayout.SOUTH);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public static void main(String[] args){
        Frame f=new Frame("Take picture");
        Imager cf = new Imager();
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
            	FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
                buf = fgc.grabFrame();
                btoi= new BufferToImage((VideoFormat)buf.getFormat());
                img = btoi.createImage(buf);
                imgpanel.setImage(img);
                Calendar cal= Calendar.getInstance();
                saveJPG(img,"E:\\Zestr.jpg");
                playerclose();
                System.exit(0);
            }
        });
        f.add(cf);
        f.pack();
        f.setSize(320, 550);
        f.setVisible(true);
    }

    public static void playerclose(){
        player.close();
        player.deallocate();
    }

    public void actionPerformed(ActionEvent e) {
        JComponent c = (JComponent) e.getSource();
        if(c==capture){
            FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
            buf = fgc.grabFrame();
            btoi= new BufferToImage((VideoFormat)buf.getFormat());
            img = btoi.createImage(buf);
            imgpanel.setImage(img);
            Calendar cal= Calendar.getInstance();
            @SuppressWarnings("static-access")
            int data = (cal.getTime().getYear()+1900)*10000+ (cal.getTime().getMonth()+1)*100+cal.getTime().getDate();
            saveJPG(img,"D:\\"+ data +capturenumber+".jpg");
            capturenumber++;
        }
    }

    private static void saveJPG(Image img, String s) {
        BufferedImage bi= new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics2D g2=bi.createGraphics();
        g2.drawImage(img, null, null);
        FileOutputStream out=null;
        try{
            out = new FileOutputStream(s);
        }
        catch(java.io.FileNotFoundException ex){
            ex.printStackTrace();
        }
        JPEGImageEncoder encoder= JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param= encoder.getDefaultJPEGEncodeParam(bi);
        param.setQuality(0.5f, false);
        encoder.setJPEGEncodeParam(param);
        try{
            encoder.encode(bi);
            out.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public class ImagePanel extends Panel{
        public Image myimg=null;
        public ImagePanel(){
            setLayout(null);
            setSize(320,240);
        }
        public void setImage(Image img){
            this.myimg=img;
            repaint();
        }
        @Override
        public void paint(Graphics g){
            if(myimg != null){
                g.drawImage(myimg,0,0,this);
            }
        }
    }

}


