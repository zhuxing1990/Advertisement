package javax.microedition.lcdui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.vunke.chinaunicom.advertisement.log.LogUtil;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


//import android.graphics.Bitmap;

@SuppressLint("ViewConstructor")
public class LoadingView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	private static final String TAG = "LoadingView";
	private static SurfaceHolder handle = null;
	private static Canvas canvas;
	private static boolean bTouched = false;

	Paint tempPaint = new Paint();
	public static boolean isHeatbeat = false;

	private static Vector<Screen> currScreenVer = new Vector<Screen>();// 当前的屏幕列表

	/********************************************************/
	/*************** 翻转参数 *****************/
	/********************************************************/
	public static final int TRANS_MIRROR = 2;
	public static final int TRANS_MIRROR_ROT180 = 1;
	public static final int TRANS_MIRROR_ROT270 = 4;
	public static final int TRANS_MIRROR_ROT90 = 7;
	public static final int TRANS_NONE = 0;
	public static final int TRANS_ROT180 = 3;
	public static final int TRANS_ROT270 = 6;
	public static final int TRANS_ROT90 = 5;

	public static boolean isZoom = false;
	public static float scaleWidht = 0;
	public static float scaleHeight = 0;
	public static int factSW = 0;
	public static int factSH = 0;
	public static Font currentFont = Font.getFont(0, 0, 15);
	public static Font largeFont = Font.getFont(0, 0, 30);

	public static int fontSize = 36;
	public static boolean isExit = false;
	public static boolean isPause = false;

	Thread th;

	public LoadingView(Context context, int w, int h) {
		super(context);
		handle = this.getHolder();
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		handle.addCallback(this);
		factSW = w;
		factSH = h;
		LogUtil.i(TAG,"---factSW:"+factSW+"|"+factSH);
		if (factSW - Screen.SW>20||factSH - Screen.SH>20||factSW - Screen.SW<-20||factSH - Screen.SH<-20) {
			isZoom = true;
			scaleWidht = ((float) factSW / Screen.SW);
			scaleHeight = scaleWidht;//((float) factSH / Screen.SH);
			Image.setConfig(isZoom, scaleWidht, scaleHeight, context);
			Graphics.setConfig(isZoom, scaleWidht, scaleHeight);
			currentFont = Font.getFont(0, 0, 15);
		} else {
			isZoom = false;
			scaleWidht = 1.0f;
			scaleHeight = 1.0f;
			Image.setConfig(false, scaleWidht, scaleHeight, context);
			Graphics.setConfig(false, scaleWidht, scaleHeight);
		}
		currentFont = Font.getFont(0, 0, (int) (25));
		largeFont = Font.getFont(0, 0, (int) (fontSize));
		initGame();

	}

	public static Random random = new Random();

	public static int getRandom(int max) {
		return (random.nextInt() % max + max) % max;
	}

	public void initGame() {
		addScreen(new LoadingScreen(1));
	}

	public static void addScreen(Screen screen) {
		screen.init();
		currScreenVer.add(screen);
		setActive(screen);
	}

	public static void delectAllScreen() {
		for (int i = 0; i < currScreenVer.size(); i++) {
			(currScreenVer.elementAt(i)).clear();
			currScreenVer.remove(i);
			i--;
		}
	}

	public static void deleteScreen(Screen screen) {
		screen.clear();
		currScreenVer.remove(screen);
		if (currScreenVer.size() > 0) {
			setActive(currScreenVer.get(currScreenVer.size() - 1));
		}
	}

	public static void setActive(Screen screen) {
		for (int i = 0; i < currScreenVer.size(); i++) {
			currScreenVer.get(i).setActive(false);
		}
		screen.setActive(true);
	}

	public static void switchToScreen(Screen toScreen) {
		delectAllScreen();
		addScreen(toScreen);
	}

	public void update() {
		for (int i = 0; i < currScreenVer.size(); i++) {
			if (currScreenVer.get(i).getActive()) {
				((Screen) currScreenVer.get(i)).update();
			}
		}
	}

	public static void draw(Graphics g) {
		g.setFont(currentFont);
		for (int i = 0; i < currScreenVer.size(); i++) {
			(currScreenVer.get(i)).draw(g);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		bTouched = true;
		return true;
	}

	public void run() {
		LogUtil.i(TAG,"run:"+isExit+"|"+isPause);
		while (!isExit) {
			if (!isPause) {
				update();
				canvas = handle.lockCanvas(null);
				if (canvas != null) {
					// 每次都清除画布，去掉残影
					Paint paint = new Paint();
					paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
					canvas.drawPaint(paint);
					paint.setXfermode(new PorterDuffXfermode(Mode.SRC));

					paint(new Graphics(canvas));
					handle.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
				} else {
					Log.d("sys", "canvas is null");
				}
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
		if (isExit) {
			for (int i = 0; i < currScreenVer.size(); i++) {
				((Screen) currScreenVer.get(i)).clear();
				currScreenVer.remove(i);
			}
		}
	}

	public void paint(Graphics g) {
		if (isPause) {
			return;
		}
		draw(g);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtil.i(TAG,"vedio contr surfaceDestroyed");
		isPause = true;
		isExit = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		LogUtil.i(TAG,"vedio surfaceCreated");
		canClearKeycode = false;
		isPause = false;
		isExit = false;
		setFocusable(true);// 设置键盘焦点
		setFocusableInTouchMode(true);// 设置触摸屏焦点
		th = new Thread(this);
		th.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		LogUtil.i(TAG,"vedio surfaceChanged");
		isPause = false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (canClearKeycode) {
			keyBuff = 0;
		}
		if (keyCode == KEY_LEFT) {
			keyReleaseBuff = keyReleaseBuff | M_KEY_LEFT;
		} else if (keyCode == KEY_RIGHT) {
			keyReleaseBuff = keyReleaseBuff | M_KEY_RIGHT;
		}
		canClearKeycode = false;
		return super.onKeyUp(keyCode, event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		LogUtil.i(TAG,"------:"+keyCode);
		keyReset();

		if (keyCode == KEY_UP) {
			keyBuff = keyBuff | M_KEY_UP;
		} else if (keyCode == KEY_DOWN) {
			keyBuff = keyBuff | M_KEY_DOWN;
		} else if (keyCode == KEY_LEFT) {
			keyBuff = keyBuff | M_KEY_LEFT;
		} else if (keyCode == KEY_RIGHT) {
			keyBuff = keyBuff | M_KEY_RIGHT;
		} else if (keyCode == KEY_OK || keyCode == KEY_OK1
				|| keyCode == KEY_OK2) {
			keyBuff = keyBuff | M_KEY_OK;
		} else if (keyCode == KEY_SOFT_R || keyCode == KEY_SOFT_R1) {
			keyBuff = keyBuff | M_KEY_KEY3;
			keyBuff = keyBuff | M_KEY_SOFT_R;
		} else if (keyCode == KEY_0) {
			keyBuff = keyBuff | M_KEY_0;
		} else if (keyCode == KEY_1 || keyCode == KEY_CAIDAN
				|| keyCode == KEY_CAIDAN1) {
			keyBuff = keyBuff | M_KEY_1;
		} else if (keyCode == KEY_2) {
			keyBuff = keyBuff | M_KEY_2;
		} else if (keyCode == KEY_3) {
			keyBuff = keyBuff | M_KEY_3;
		} else if (keyCode == KEY_4) {
			keyBuff = keyBuff | M_KEY_4;
		} else if (keyCode == KEY_5) {
			keyBuff = keyBuff | M_KEY_5;
		} else if (keyCode == KEY_6) {
			keyBuff = keyBuff | M_KEY_6;
		} else if (keyCode == KEY_7) {
			keyBuff = keyBuff | M_KEY_7;
		} else if (keyCode == KEY_8) {
			keyBuff = keyBuff | M_KEY_8;
		} else if (keyCode == KEY_9) {
			keyBuff = keyBuff | M_KEY_9;
		}
		if (keyCode == KEY_SOFT_R || keyCode == KEY_SOFT_R1) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static void keyReset() {
		if (canClearKeycode) {
			keyBuff = 0;
			canClearKeycode = false;
			LogUtil.i(TAG,"key reset in keyPresssed");
		}
	}


	public static void keyClear() {
		keyBuff = 0;
		canClearKeycode = false;
	}

	private static boolean canClearKeycode = false;

	public static boolean iskeyPressed(int keyCode) {
		if ((keyBuff & keyCode) == keyCode) {
			canClearKeycode = true;
			return true;
		} else {
			return false;
		}
	}

	public static boolean iskeyReleased(int keyCode) {
		if ((keyReleaseBuff & keyCode) == keyCode) {
			keyReleaseBuff = 0;
			return true;
		} else {
			return false;
		}
	}

	public static boolean isTouched() {
		if (bTouched) {
			bTouched = false;
			return true;
		}
		return false;
	}

	public int getActiveScreenId() {
		int id = 0;
		for (int i = 0; i < currScreenVer.size(); i++) {
			if (currScreenVer.elementAt(i).getActive()) {
				id = currScreenVer.elementAt(i).screenId;
			}
		}
		return id;
	}

	/********************************************************/
	/*************** 按键键值 *****************/
	/********************************************************/
	public static int keyBuff = 0;
	public static int keyReleaseBuff = 0;

	// public final static int KEY_UP = 0x26;
	// public final static int KEY_DOWN = 0x28;
	// public final static int KEY_LEFT = 0x25;
	// public final static int KEY_RIGHT = 0x27;
	// public final static int KEY_SOFT_L = 0x0D;//left soft key
	// public final static int KEY_SOFT_R = 0x0280;//right soft key
	// public final static int KEY_KEY3 = 7;//right soft key
	// public final static int KEY_KEY4 = -31;//right soft key
	// public final static int KEY_OK = 0x0D;
	// public final static int KEY_0 = 0x30;
	// public final static int KEY_1 = 0x31;
	// public final static int KEY_2 = 0x32;
	// public final static int KEY_3 = 0x33;
	// public final static int KEY_4 = 0x34;
	// public final static int KEY_5 = 0x35;
	// public final static int KEY_6 = 0x36;
	// public final static int KEY_7 = 0x37;
	// public final static int KEY_8 = 0x38;
	// public final static int KEY_9 = 0x30;

	public final static int KEY_UP = 19;
	public final static int KEY_DOWN = 20;
	public final static int KEY_LEFT = 21;
	public final static int KEY_RIGHT = 22;
	public final static int KEY_OK = 66;
	public final static int KEY_OK1 = 23;
	public final static int KEY_OK2 = 188;

	public final static int KEY_CAIDAN = 82;
	public final static int KEY_CAIDAN1 = 189;

	public final static int KEY_SOFT_R = 4;// right soft key
	public final static int KEY_SOFT_R1 = 111;// right soft key
	public final static int KEY_0 = 7;
	public final static int KEY_1 = 8;
	public final static int KEY_2 = 9;
	public final static int KEY_3 = 10;
	public final static int KEY_4 = 11;
	public final static int KEY_5 = 12;
	public final static int KEY_6 = 13;
	public final static int KEY_7 = 14;
	public final static int KEY_8 = 15;
	public final static int KEY_9 = 16;

	/**
	 * 模拟器数字键
	 */
	// public final static int KEY_CAIDAN = 82;
	// public final static int KEY_SOFT_R = 111;// right soft key
	// public final static int KEY_0 = 144;
	// public final static int KEY_1 = 145;
	// public final static int KEY_2 = 146;
	// public final static int KEY_3 = 147;
	// public final static int KEY_4 = 148;
	// public final static int KEY_5 = 149;
	// public final static int KEY_6 = 150;
	// public final static int KEY_7 = 151;
	// public final static int KEY_8 = 152;
	// public final static int KEY_9 = 153;

	// public final static int KEY_SOFT_L = -6;// left soft key
	// public final static int KEY_KEY3 = 31;// right soft key
	// public final static int KEY_KEY4 = -31;// right soft key

	/**
	 * 屏蔽数字键
	 */

	public final static int M_KEY_UP = 1 << 24;
	public final static int M_KEY_DOWN = 1 << 23;
	public final static int M_KEY_LEFT = 1 << 22;
	public final static int M_KEY_RIGHT = 1 << 21;
	public final static int M_KEY_SOFT_L = 1 << 20;// left soft key
	public final static int M_KEY_SOFT_R = 1 << 19;// right soft key
	public final static int M_KEY_KEY3 = 1 << 18;// right soft key
	public final static int M_KEY_OK = 1 << 17;
	public final static int M_KEY_0 = 1 << 16;
	public final static int M_KEY_1 = 1 << 15;
	public final static int M_KEY_2 = 1 << 14;
	public final static int M_KEY_3 = 1 << 13;
	public final static int M_KEY_4 = 1 << 12;
	public final static int M_KEY_5 = 1 << 11;
	public final static int M_KEY_6 = 1 << 10;
	public final static int M_KEY_7 = 1 << 9;
	public final static int M_KEY_8 = 1 << 8;
	public final static int M_KEY_9 = 1 << 7;

}