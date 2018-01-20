package javax.microedition.lcdui.view;

import javax.microedition.lcdui.Graphics;

public class LoadingScreen extends Screen{

	int frm = 0;

	public LoadingScreen(int screenId) {
		super(screenId);
	}

	@Override
	public void init() {
		frm = 0;
	}

	@Override
	public void update() {
		frm++;
		frm%=10240;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(0xffffff);
		g.setFont(LoadingView.largeFont);
		g.drawString("正在加载，请稍候", 640-LoadingView.fontSize*4+LoadingView.fontSize/2, 360, Graphics.TOP| Graphics.LEFT);
//		if((frm/5)%4==1){
//			g.drawString(".", 640+LoadingView.fontSize*3+LoadingView.fontSize/2, 360, Graphics.TOP|Graphics.LEFT);
//		}else if((frm/5)%4==2){
//			g.drawString("..", 640+LoadingView.fontSize*3+LoadingView.fontSize/2, 360, Graphics.TOP|Graphics.LEFT);
//		}else if((frm/5)%4==3){
//			g.drawString("...", 640+LoadingView.fontSize*3+LoadingView.fontSize/2, 360, Graphics.TOP|Graphics.LEFT);
//		}
		if((frm/5)%4==1){
			g.drawString(".", 680+LoadingView.fontSize*3+LoadingView.fontSize/2, 360, Graphics.TOP| Graphics.LEFT);
		}else if((frm/5)%4==2){
			g.drawString("..", 680+LoadingView.fontSize*3+LoadingView.fontSize/2, 360, Graphics.TOP| Graphics.LEFT);
		}else if((frm/5)%4==3){
			g.drawString("...", 680+LoadingView.fontSize*3+LoadingView.fontSize/2, 360, Graphics.TOP| Graphics.LEFT);
		}


	}

	@Override
	public void clear() {

	}

}
