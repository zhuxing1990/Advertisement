package javax.microedition.lcdui.view;


import javax.microedition.lcdui.Globe;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class Screen {
	public int screenId = 0;
	private boolean isActive = false;
	public static int SW = 1280;
	public static int SH = 720;
	public int screenLoadIndex = 0;
	public Screen(int screenId){
		this.screenId = screenId;
		System.gc();
	}
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics g);
	public abstract void clear();
	
	
	
	public void setActive(boolean isActive){
		this.isActive = isActive;
	}
	
	public boolean getActive(){
		return isActive;
	}

	public void drawBg(Graphics g, Image imgBg){
		g.drawImage(imgBg, Globe.SW>>1, 0, Graphics.RIGHT| Graphics.TOP);
		g.drawRegion(imgBg, 0,0,imgBg.getWidth(),imgBg.getHeight(), Globe.TRANS_MIRROR,
				Globe.SW>>1, 0, Graphics.LEFT| Graphics.TOP);
	}
	
}
