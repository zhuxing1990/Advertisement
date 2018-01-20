package javax.microedition.lcdui;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class Graphics{
	public Canvas graphics;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Rect clipRect = new Rect();
	private RectF drawRect = new RectF();
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BOTTOM = 32;
    public static final int BASELINE = 64;
    public static final int SOLID = 0;
    public static final int DOTTED = 1;
    //�������䷽ʽ
    public static final int POLYGON_FILL = 0;
    public static final int POLYGON_STROKE = 1;
    public static final int POLYGON_DASHED = 2;
    //���ߵ���ʾ��ʽ
    private static float phase = 0;
    public static final int DASHED_NONFLOWING = 0;
    public static final int DASHED_FLOWING = 1;
    /** The current FontR */
    private Font font= Font.getDefaultFont();
    
    /** Line stroke style */
    private int style;          // line stroke style
    
    public static boolean isZoom = false;
	public static float scaleWidht = 0; 
	public static float scaleHeight = 0;

	public static void setConfig(boolean is,float scw,float sch){
		isZoom = is;
		scaleWidht = scw;
		scaleHeight = sch;
	}
    /**
     * constructor function1
     */
    public Graphics(){
    	
    }
    
    
    /**
     * constructor function2
     * for current package use.
     */
    public Graphics(Bitmap bitmap){
    	graphics=new Canvas(bitmap);
    }
    
    public Graphics(Canvas canvas){
    	this.graphics = canvas;
    }
    
    /**
     * constructor function3
     * for game application use
     */
    public Graphics(Image image){
    	
    }
    
    private int translateX = 0;
    private int translateY = 0;
    /**
     * Translates the origin of the graphics context to the point
     * <code>(x, y)</code> in the current coordinate system. All coordinates
     * used in subsequent rendering operations on this graphics
     * context will be relative to this new origin.<p>
     *
     * The effect of calls to <code>translate()</code> are
     * cumulative. For example, calling
     * <code>translate(1, 2)</code> and then <code>translate(3,
     * 4)</code> results in a translation of
     * <code>(4, 6)</code>. <p>
     *
     * The application can set an absolute origin <code>(ax,
     * ay)</code> using the following
     * technique:<p>
     * <code>
     * g.translate(ax - g.getTranslateX(), ay - g.getTranslateY())
     * </code><p>
     *
     * @param x the x coordinate of the new translation origin
     * @param y the y coordinate of the new translation origin
     * @see #getTranslateX()
     * @see #getTranslateY()
     */
    public void translate(int x, int y){
    	translateX = x;
    	translateY = y;
    	graphics.translate(translateX, translateY);
    }
    
    /**
     * Gets the X coordinate of the translated origin of this graphics context.
     * @return X of current origin
     */
    public int getTranslateX() {
    	
        return translateX;
    }   
    /**
     * Gets the Y coordinate of the translated origin of this graphics context.
     * @return Y of current origin
     */
    public int getTranslateY() {
        return translateY;
    }
    
    /**
     * Gets the current color.
     * @return an integer in form <code>0x00RRGGBB</code>
     * @see #setColor(int, int, int)
     */
    public int getColor() {
        return paint.getColor();
    }
    /**
     * 
     */
    public void setAlpha(int alpha){
    	paint.setAlpha(alpha&0xff);
    }
    /**
     * Sets the current color to the specified RGB values. All subsequent
     * rendering operations will use this specified color. The RGB value
     * passed in is interpreted with the least significant eight bits
     * giving the blue component, the next eight more significant bits
     * giving the green component, and the next eight more significant
     * bits giving the red component. That is to say, the color component
     * is specified in the form of <code>0x00RRGGBB</code>. The high
     * order byte of
     * this value is ignored.
     *
     * @param RGB the color being set
     * @see #getColor
     */
    public void setColor(int rgb) {
    	paint.setColor(0xff000000 | rgb);
    }
    /**
     * Sets the current color to the specified RGB values. All subsequent
     * rendering operations will use this specified color.
     * @param red the red component of the color being set in range
     * <code>0-255</code>
     * @param green the green component of the color being set in range
     * <code>0-255</code>
     * @param blue the blue component of the color being set in range
     * <code>0-255</code>
     * @throws IllegalArgumentException if any of the color components
     * are outside of range <code>0-255</code>
     * @see #getColor
     */
    public void setColor(int red, int green, int blue){
    	if ((red < 0)   || (red > 255) 
                || (green < 0) || (green > 255)
                || (blue < 0)  || (blue > 255)) {
                throw new IllegalArgumentException("Value out of range");
            }
		Paint pt = new Paint();
		pt.setARGB(255, red, green, blue);
		setColor(pt.getColor());
    }
    
    /**
     * Gets the current font.
     * @return current font
     * @see javax.javax.lcdui.Font
     * @see #setFont(javax.javax.lcdui.Font)
     */
    public Font getFont() {
        return font;
    }
    
    
    /**
     * Sets the font for all subsequent text rendering operations.  If font is 
     * <code>null</code>, it is equivalent to
     * <code>setFont(Font.getDefaultFont())</code>.
     * 
     * @param font the specified font
     * @see javax.javax.lcdui.Font
     * @see #getFont()
     * @see #drawString(String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void setFont(Font font) {
        this.font = ((font == null) ? Font.getDefaultFont() : font);
    }
    
    /**
     * Sets the stroke style used for drawing lines, arcs, rectangles, and 
     * rounded rectangles.  This does not affect fill, text, and Image3 
     * operations.
     * @param style can be <code>SOLID</code> or <code>DOTTED</code>
     * @throws IllegalArgumentException if the <code>style</code> is illegal
     * @see #getStrokeStyle
     */
    public void setStrokeStyle(int style) {
        if ((style != SOLID) && (style != DOTTED)) {
            throw new IllegalArgumentException("Invalid line style");
        }

        this.style = style;
    }

    /**
     * Gets the stroke style used for drawing operations.
     * @return stroke style, <code>SOLID</code> or <code>DOTTED</code>
     * @see #setStrokeStyle
     */
    public int getStrokeStyle() {
        return style;
    }

    /**
     * Gets the X offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return X offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipX() {
    	return clipRect.left;
    }

    /**
     * Gets the Y offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return Y offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipY() {
    	return clipRect.top;
    }

    /**
     * Gets the width of the current clipping area.
     * @return width of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipWidth() {
        return clipRect.right - clipRect.left;
    }



    public int getClipHeight()	{
        return clipRect.bottom - clipRect.top;
    }

    /**
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use the <code>setClip</code> method.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     */
//    public boolean clipRect(int x, int y, int width, int height) {
////		graphics.clipRect(x, y, x + width, y + height);
//		return graphics.clipRect(x, y, x + width, y + height);
////		clipRect = graphics.getClipBounds();
//    }

    /**
     * Sets the current clip to the rectangle specified by the
     * given coordinates.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the new clip rectangle
     * @param y the y coordinate of the new clip rectangle
     * @param width the width of the new clip rectangle
     * @param height the height of the new clip rectangle
     * @see #clipRect(int, int, int, int)
     */
    
//    Rect rrr  = null;
    public void setClip(int x, int y, int width, int height) {
    		if(graphics.getSaveCount()==Canvas.CLIP_SAVE_FLAG)
    		{
    			graphics.restore();
    		}
    		graphics.save(Canvas.CLIP_SAVE_FLAG);
    		if(isZoom){
    			x = Math.round(x*scaleWidht)-1;
    			y = Math.round(y*scaleHeight)-1;
    			width = Math.round(width*scaleWidht)+2;
    			height = Math.round(height*scaleHeight)+2;
    		}
    		clipRect.set(x, y, x + width, y + height);
    		graphics.clipRect(clipRect, android.graphics.Region.Op.REPLACE);

    }
    
    
    /**
     * Draws a line between the coordinates <code>(x1,y1)</code> and
     * <code>(x2,y2)</code> using
     * the current color and stroke style.
     * @param x1 the x coordinate of the start of the line
     * @param y1 the y coordinate of the start of the line
     * @param x2 the x coordinate of the end of the line
     * @param y2 the y coordinate of the end of the line
     */
    public   void drawLine(int x1, int y1, int x2, int y2)
    {
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(0xff);
        if(isZoom){
        	x1 = (int) (x1*scaleWidht);
        	y1 = (int) (y1*scaleHeight);
        	x2 = (int) (x2*scaleWidht);
        	y2 = (int) (y2*scaleHeight);
        }
        graphics.drawLine(x1, y1, x2, y2, paint);
    }
    
    /**
     * Fills the specified rectangle with the current color.
     * If either width or height is zero or less,
     * nothing is drawn.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @see #drawRect(int, int, int, int)
     */
    public void fillRect(int x, int y, int width, int height){
    	if(isZoom){
			x = Math.round(x*scaleWidht)-1;
			y = Math.round(y*scaleHeight)-1;
			width = Math.round(width*scaleWidht)+1;
			height = Math.round(height*scaleHeight)+1;
		}
       	paint.setStyle(Paint.Style.FILL);
        drawRect.set(x, y, x+width, y+height);	        	
        graphics.drawRect(drawRect, paint);
    }
 
    /**
     * Draws the outline of the specified rectangle using the current
     * color and stroke style.
     * The resulting rectangle will cover an area <code>(width + 1)</code>
     * pixels wide by <code>(height + 1)</code> pixels tall.
     * If either width or height is less than
     * zero, nothing is drawn.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @see #fillRect(int, int, int, int)
     */
    public   void drawRect(int x, int y, int width, int height)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
			width = (int) (width*scaleWidht);
			height = (int) (height*scaleHeight);
		}
    	paint.setStyle(Paint.Style.STROKE);
        drawRect.set(x, y, x+width, y+height);	        	
        graphics.drawRect(drawRect, paint);
    }
    
    /**
     * Draws the outline of the specified rounded corner rectangle
     * using the current color and stroke style.
     * The resulting rectangle will cover an area <code>(width +
     * 1)</code> pixels wide
     * by <code>(height + 1)</code> pixels tall.
     * If either <code>width</code> or <code>height</code> is less than
     * zero, nothing is drawn.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #fillRoundRect(int, int, int, int, int, int)
     */
    public   void drawRoundRect(int x, int y, int width, int height,
                                     int arcWidth, int arcHeight)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
			width = (int) (width*scaleWidht);
			height = (int) (height*scaleHeight);
			arcWidth = (int) (arcWidth*scaleWidht);
			arcHeight = (int) (arcHeight*scaleHeight);
		}
    	paint.setStyle(Paint.Style.STROKE);
    	drawRect.set(x, y, x+width, y+height);	        	
    	graphics.drawRoundRect(drawRect, arcWidth, arcHeight, paint);
    }
 
    /**
     * Fills the specified rounded corner rectangle with the current color.
     * If either <code>width</code> or <code>height</code> is zero or less,
     * nothing is drawn.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @param arcWidth the horizontal diameter of the arc at the four
     * corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #drawRoundRect(int, int, int, int, int, int)
     */
    public   void fillRoundRect(int x, int y, int width, int height,
                                     int arcWidth, int arcHeight)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
			width = (int) (width*scaleWidht);
			height = (int) (height*scaleHeight);
			arcWidth = (int) (arcWidth*scaleWidht);
			arcHeight = (int) (arcHeight*scaleHeight);
		}
    	paint.setStyle(Paint.Style.FILL);
        drawRect.set(x, y, x+width, y+height);	        	
        graphics.drawRoundRect(drawRect, arcWidth, arcHeight, paint);
    }
    
    
    public   void fillArc(int x, int y, int width, int height,
                               int startAngle, int arcAngle)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
			width = (int) (width*scaleWidht);
			height = (int) (height*scaleHeight);
			startAngle = (int) (startAngle*scaleWidht);
		}
    	paint.setStyle(Paint.Style.FILL);
        drawRect.set(x, y, x+width, y+height);	        	
        graphics.drawArc(drawRect, startAngle, arcAngle, false, paint);
    }

   
    public   void drawArc(int x, int y, int width, int height,
                               int startAngle, int arcAngle)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
			width = (int) (width*scaleWidht);
			height = (int) (height*scaleHeight);
			startAngle = (int) (startAngle*scaleWidht);
		}
    	paint.setStyle(Paint.Style.STROKE);
        drawRect.set(x, y, x+width, y+height);	        	
//        super.drawRoundRect(drawRect, arc, arcHeight, paint);
        graphics.drawArc(drawRect, startAngle, arcAngle, false, paint);
    }
    
    public void drawArc(int x, int y ,int width, int height,
			int startAngle, int arcAngle, boolean useCenter,Paint.Style style){
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
			width = (int) (width*scaleWidht);
			height = (int) (height*scaleHeight);
			startAngle = (int) (startAngle*scaleWidht);
		}
		paint.setStyle(style);
		drawRect.set(x, y, x+width, y+height);	        	
		//super.drawRoundRect(drawRect, arc, arcHeight, paint);
		graphics.drawArc(drawRect, startAngle, arcAngle, useCenter, paint);
	}
	
	public void drawCircle(int x, int y, int r){
		graphics.drawCircle(x, y, r, paint);
	}

    /**
     * Draws the specified <code>String</code> using the current FontR and color.
     * The <code>x,y</code> position is the position of the anchor point.
     * See <a href="#anchor">anchor points</a>.
     * @param str the <code>String</code> to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     * @throws IllegalArgumentException if anchor is not a legal value
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void drawString(String str,
                                  int x, int y, int anchor){
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
		}
        int newx = x;
        int newy = y;
        if (anchor == 0) {
            anchor = TOP | LEFT;
        }
        if ((anchor & TOP) != 0) {
            newy -= font.paint.getFontMetricsInt().ascent;
        } else if ((anchor & BOTTOM) != 0) {
            newy -= font.paint.getFontMetricsInt().descent;
        }
        if ((anchor & HCENTER) != 0) {
            newx -= ((int)font.paint.measureText(str)) >>1;
        } else if ((anchor & RIGHT) != 0) {
            newx -= font.paint.measureText(str);
        }
        font.paint.setColor(paint.getColor());
        graphics.drawText(str, newx, newy , font.paint);
    }
    
    
  
    public   void drawSubstring(String str, int offset, int len,
                                     int x, int y, int anchor)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
		}
    	drawString(str.substring(offset, offset+len) ,x,y,anchor);
    }
    
    
    /**
     * Draws the specified character using the current FontR and color.
     * @param character the character to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text; see
     * <a href="#anchor">anchor points</a>
     *
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     *
     * @see #drawString(String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public   void drawChar(char character, int x, int y, int anchor)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
		}
        int newx = x;
        int newy = y;
        if (anchor == 0) {
            anchor = TOP | LEFT;
        }
        if ((anchor & TOP) != 0) {
            newy -= font.paint.getFontMetricsInt().ascent;
        } else if ((anchor & BOTTOM) != 0) {
            newy -= font.paint.getFontMetricsInt().descent;
        }
        if ((anchor & HCENTER) != 0) {
            newx -= font.charWidth(character)>>1;
        } else if ((anchor & RIGHT) != 0) {
            newx -= font.charWidth(character);
        }
        font.paint.setColor(paint.getColor());
        graphics.drawText(""+character, newx, newy, font.paint);
    }
   
  
    public void drawChars(char[] data, int offset, int length,int x, int y, int anchor)
    {
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
		}
        int newx = x;
        int newy = y;
        if (anchor == 0) {
            anchor = TOP | LEFT;
        }
        if ((anchor & TOP) != 0) {
            newy -= font.paint.getFontMetricsInt().ascent;
        } else if ((anchor & BOTTOM) != 0) {
            newy -= font.paint.getFontMetricsInt().descent;
        }
        if ((anchor & HCENTER) != 0) {
            newx -= font.charsWidth(data,offset,length)>>1;
        } else if ((anchor & RIGHT) != 0) {
            newx -= font.charsWidth(data,offset,length);
        }
    	  font.paint.setColor(paint.getColor());
    	  graphics.drawText(data, offset, length, newx, newy, font.paint);
    }
 
    public void drawImage(Image image, int x, int y, int anchor)
    {
    	if(image==null)
    		throw new NullPointerException("Image Object is Null");
    	
    	if(image.getBitmap()==null)
    		throw new NullPointerException("OMG!! Maybe you create a wrong Bitmap!");
    	if(isZoom){
			x = (int)(x*scaleWidht);
			y = (int)(y*scaleHeight);
		}
        int newx = x;
        int newy = y;
        if (anchor == 0) {
            anchor = TOP | LEFT;
        }
        if ((anchor & RIGHT) != 0) {
            newx -= image.getFWidth();
        } else if ((anchor & HCENTER) != 0) {
            newx -= image.getFWidth()>>1;
        }
        if ((anchor & BOTTOM) != 0) {
            newy -= image.getFHeight();
        } else if ((anchor & VCENTER) != 0) {
            newy -= image.getFHeight()>>1;
        }
        graphics.drawBitmap(image.getBitmap(), newx, newy, paint);
   }
    
    public void drawImage(Image image, int x, int y, int anchor, int transform)
    {
    	if(image==null)
    		throw new NullPointerException("Image Object is Null");
    	
    	if(image.getBitmap()==null)
    		throw new NullPointerException("OMG!! Maybe you create a wrong Bitmap!");
    	
    	if(isZoom){
			x = (int) (x*scaleWidht);
			y = (int) (y*scaleHeight);
		}
    	//��������ת
        Matrix matrix = new Matrix();
        matrix.setTranslate(-image.getFWidth()>>1, -image.getFHeight()>>1);
        matrix.postRotate(transform);
        graphics.save();
        graphics.translate(x, y);
        graphics.drawBitmap(image.getBitmap(), matrix, paint);
        graphics.restore();
   }


    
    
    public void drawRegion(Image src,
                                  int x_src, int y_src,
                                  int width, int height, 
                                  int transform,
                                  int x_dest, int y_dest, 
                                  int anchor)
    {
	 if(isZoom){
      	x_dest = (int)(x_dest*scaleWidht);
      	y_dest = (int)(y_dest*scaleHeight);
      	x_src = (int)(x_src*scaleWidht);
      	y_src = (int)(y_src*scaleHeight);
      	width = (int)(width*scaleWidht);
      	height = (int)(height*scaleHeight);
		}
     if (x_src + width > src.getFWidth() || y_src + height > src.getFHeight() || width < 0 || height < 0 || x_src < 0
             || y_src < 0)
         throw new IllegalArgumentException("Area out of Image");
    
     Matrix matrix = new Matrix();
		//ˮƽ�������
		float values[]= {-1,0,src.getFWidth(),
						 0,1,0,
						 0,0,1};
		int new_src_x = (int)(x_src);//��¼ͼƬ�ھ����������Ҫ����������µ�����
		int new_src_y = (int)(y_src);//�����Ͻǵ������Ϊ׼
		//��ת��˳ʱ���
		if(transform == Globe.TRANS_ROT90){
			new_src_x = /*src.getHeight()*/- y_src - height/* - src.getHeight()*/;
			new_src_y = x_src;
			matrix.postRotate(90);
			int temp =height;
			height = width;
			width = temp;
		}else
		if(transform == Globe.TRANS_ROT180){
			new_src_x = /*src.getWidth()*/- x_src - width;
			new_src_y = /*src.getHeight()*/- y_src - height;
			matrix.postRotate(180);
		}else
		if(transform == Globe.TRANS_ROT270){
			new_src_x = y_src;
			new_src_y = /*src.getWidth()*/ - x_src- width;
			matrix.postRotate(270);
			int temp =height;
			height = width;
			width = temp;
		}else
		if(transform == Globe.TRANS_MIRROR){
			if(src.mirBitmap==null){
				Image imgTemp = new Image(src.getFWidth(),src.getFHeight());
				Canvas tempG = (imgTemp.getGraphics()).graphics;
				
				tempG.save();
				matrix.postScale(-1, 1);
				tempG.translate(src.getFWidth(), 0);
				tempG.drawBitmap(src.getBitmap(), matrix, paint);
		        tempG.restore();
		        src.mirBitmap = imgTemp;
			}
        if (anchor == 0) {
             anchor = TOP | LEFT;
         }
         if ((anchor & RIGHT) != 0) {
        	 x_dest -= width;
         } else if ((anchor & HCENTER) != 0) {
        	 x_dest -= width>>1;
         }
         if ((anchor & BOTTOM) != 0) {
        	 y_dest -= height;
         } else if ((anchor & VCENTER) != 0) {
        	 y_dest -= height>>1;
         }
         graphics.save();
         int tempx = x_dest-(src.getFWidth()-x_src-width);
         int tempy = y_dest-y_src;
         int clipX = src.getFWidth()-x_src-width;
         int clipY = y_src;
         graphics.translate(tempx, tempy);//ԭ��ƽ�ƣ�����ʹ��@graphics.drawBitmap(Bitmap, Matrix, Paint)����
         //��������������Ҫ����clip
         graphics.clipRect(clipX, clipY, 
        		 clipX+width, y_src + height 
         		, android.graphics.Region.Op.REPLACE );
         //ʹ�þ���matrix��ͼ
         graphics.drawBitmap(src.mirBitmap.getBitmap(), matrix, paint);
         
         graphics.translate(-tempx, -tempy);//��ԭԭ������
         graphics.restore();
        
        return;
		}else
		if(transform == Globe.TRANS_MIRROR_ROT90){
			new_src_x = -y_src - height;
			new_src_y = src.getFWidth() - x_src - width;
			matrix.setValues(values);
			matrix.postRotate(90);
			int temp =height;
			height = width;
			width = temp;
			//filter = true;
		}else
		if(transform == Globe.TRANS_MIRROR_ROT180){
			new_src_x = -src.getFWidth() + x_src;
			new_src_y = - y_src - height;
			matrix.setValues(values);
			matrix.postRotate(180);
			//filter = true;
		}else
		if(transform == Globe.TRANS_MIRROR_ROT270){
			new_src_x = y_src;
			new_src_y = - src.getFWidth() + x_src;
			matrix.setValues(values);
			matrix.postRotate(270);
			int temp =height;
			height = width;
			width = temp;
		}
		
     int newx = x_dest;//��¼ͼƬsrc�ڴ������Ӧ������Ļ�ϵ�����
     int newy = y_dest;
     newx -= new_src_x;
     newy -= new_src_y;
     if (anchor == 0) {
         anchor = TOP | LEFT;
     }
     if ((anchor & RIGHT) != 0) {
         newx -= width;
     } else if ((anchor & HCENTER) != 0) {
         newx -= width>>1;
     }
     if ((anchor & BOTTOM) != 0) {
         newy -= height;
     } else if ((anchor & VCENTER) != 0) {
         newy -= height>>1;
     }
     if (src.isMutable() && src.getGraphics() == this)
         throw new IllegalArgumentException("Image is source and target");
     Bitmap img=src.getBitmap();
  
//     Rect old = graphics.getClipBounds();
     graphics.save();
     graphics.translate(newx, newy);//ԭ��ƽ�ƣ�����ʹ��@graphics.drawBitmap(Bitmap, Matrix, Paint)����
     //��������������Ҫ����clip
     graphics.clipRect(new_src_x, new_src_y, 
     		new_src_x+ width, new_src_y + height 
     		, android.graphics.Region.Op.REPLACE );
     //ʹ�þ���matrix��ͼ
     graphics.drawBitmap(img, matrix, paint);
     
//     graphics.translate(-newx, -newy);//��ԭԭ������
     graphics.restore();
}

    
    public void copyArea(int x_src, int y_src, int width, int height,
			 int x_dest, int y_dest, int anchor) {
    	
    }

    /**
     * Fills the specified triangle will the current color.  The lines
     * connecting each pair of points are included in the filled
     * triangle.
     *
     * @param x1 the x coordinate of the first vertex of the triangle
     * @param y1 the y coordinate of the first vertex of the triangle
     * @param x2 the x coordinate of the second vertex of the triangle
     * @param y2 the y coordinate of the second vertex of the triangle
     * @param x3 the x coordinate of the third vertex of the triangle
     * @param y3 the y coordinate of the third vertex of the triangle
     *
     *
     */
    public   void fillTriangle(int x1, int y1, 
				    int x2, int y2,
				    int x3, int y3)
    {
    	if(isZoom){
			x1 = (int) (x1*scaleWidht);
			y1 = (int) (y1*scaleHeight);
			x2 = (int) (x2*scaleWidht);
			y2 = (int) (y2*scaleHeight);
			x3 = (int) (x3*scaleWidht);
			y3 = (int) (y3*scaleHeight);
		}
    	//first need justice the number,make sure they are valid
    	if((x1 == x2 && x1 == x3)||(y1 == y2 && y1 == y3)){
    		//three points are in a line or covered by each other
    		return;
    	}
    	
//    	float maxX,minX,midX;
//    	float maxY = (y1 > y2)?(y1>y3?y1:y3):(y2>y3?y2:y3);
//    	float minY = (y1 < y2)?(y1<y3?y1:y3):(y2<y3?y2:y3);
//    	float midY = 0;
//    	if(y1 == maxY){
//    		maxX = x1;
//    		if(y2==minY) {
//    			midY = y3;
//    			minX = x2;
//    			midX = x3;
//    		}
//    		else{
//    			midY = y2;
//    			midX = x2;
//    			minX = x3;
//    		}
//    	}else{
//    		if(y1 == minY){
//    			minX = x1;
//    			if(y2 == maxY) {
//    				midY = y3;
//    				midX = x3;	
//    				maxX = x2;
//    			}
//    			else{
//    				midY = y2;
//    				midX = x2;
//    				maxX = x3;
//    			}
//    		}else{
//    			midY = y1;
//    			midX = x1;
//    			if(y2 > y3){
//    				maxX = x2;
//    				minX = x3;
//    			}else{
//    				maxX = x3;
//    				minX = x2;
//    			}
//    		}
//    	}
//    	
//    	float k1 = (maxX - midX)/(maxY - midY);
//    	float k2 = (maxX - minX)/(maxY - minY);
//    	float k3 = (midX - minX)/(midY - minY);
//    	if(maxY == midY){
//    		k1 = 0;
//    	}
//    	if(minY == midY){
//    		k3 = 0;
//    	}//������maxY==minY,�������ڵ�һ����return
//    	Long time = System.currentTimeMillis();
    	
    	//��һ�ֻ�����
    	//////////////////***����Picture�ࣨ�ٶ�������****************************************////////////////
//    	Picture myPicture = new Picture();
//    	float max = (x1 > x2)?(x1>x3?x1:x3):(x2>x3?x2:x3);
//    	float mix = (x1 < x2)?(x1<x3?x1:x3):(x2<x3?x2:x3);
//    	Canvas canvas = myPicture.beginRecording((int)Math.abs(max - mix), (int)Math.abs(maxY - minY));
//    	float startY = maxY - minY;
//    	float endY = 0;//minY-minY;
//    	float mY = midY - minY;
//    	float startX = maxX - minX;
//    	paint.setAlpha(0xff);
//    	paint.setStyle(Paint.Style.FILL);
//    	for(float line = startY; line >= endY ; line --){
//    		float pointX1,pointX2;
//    		if(line >=mY){
//    			pointX1 = ((line - startY)*k1 + startX);
//    			pointX2 = ((line - startY)*k2 + startX);
//    			canvas.drawLine(pointX1,line,pointX2,line,paint);
//    		}else{
//    			pointX1 = line * k3;//((line - 0)*k3 + 0);
//    			pointX2 = line * k2;//((line - 0)*k2 + 0);
//    			canvas.drawLine(pointX1,line,pointX2,line,paint);
//    		}
//    	}
//    	graphics.drawPicture(myPicture, new RectF(mix,minY,max,maxY));
    	
    	//�ڶ��ֻ�����
    	//////////////////***����Drawline���ٶ���Σ�*************************************////////////////
//    	paint.setAlpha(0xff);
//    	paint.setStyle(Paint.Style.FILL);
//    	for(float line = maxY; line >= minY ; line --){
//    		float pointX1,pointX2;
//    		if(line >=midY){
//    			pointX1 = ((line - maxY)*k1 + maxX);
//    			pointX2 = ((line - maxY)*k2 + maxX);
//    			graphics.drawLine(pointX1,line,pointX2,line,paint);
//    		}else{
//    			pointX1 = ((line - minY)*k3 + minX);
//    			pointX2 = ((line - minY)*k2 + minX);
//    			graphics.drawLine(pointX1,line,pointX2,line,paint);
//    		}
//    	}
//    	graphics.drawLine(x1,y1,x2,y2, paint);
//    	graphics.drawLine(x1,y1,x3,y3, paint);
//    	graphics.drawLine(x2,y2,x3,y3, paint);
    	//////////////////***�϶λ�һ�������500(px)���ҵ������ε�ƽ��ʱ����3-4ms***////////////////
    	
    	//�����ֻ�����
    	//////////////////***����Drawlines���ٶȺܿ죩**************************************////////////////
//    	float [] points = new float[((int)(maxY - minY+1)<<2)];
//    	int k = 0;
//    	for(float line = maxY; line >= minY ; line --){
//    		float pointX1,pointX2;
//    		if(line >=midY){
//    			pointX1 = ((line - maxY)*k1 + maxX);
//    			pointX2 = ((line - maxY)*k2 + maxX);
//    			//graphics.drawLine(pointX1,line,pointX2,line,paint);
//    		}else{
//    			pointX1 = ((line - minY)*k3 + minX);
//    			pointX2 = ((line - minY)*k2 + minX);
////    			graphics.drawLine(pointX1,line,pointX2,line,paint);
//    		}
//    		points[k] = pointX1; points[k + 1] = line; 
//    		points[k+2] = pointX2; points[k+3] = line; 
//    		k+=4;
//    	}
//    	graphics.drawLines(points, paint);
//    	graphics.drawLine(x1,y1,x2,y2, paint);
//    	graphics.drawLine(x1,y1,x3,y3, paint);
//    	graphics.drawLine(x2,y2,x3,y3, paint);
    	//////////////////����������һ�������500(px)���ҵ�������ƽ��1-2ms/////////////////////////////
    	
    	//�����ֻ���
    	//////////////////***ͨ������һ����յ�Path���ڵ���drawPath������������ٶȺ͵����ַ����൱�������ȶ���***/////////////////
    	paint.setStyle(Paint.Style.FILL);
    	Path mPath = new Path();
    	mPath.moveTo(x1, y1);
    	mPath.lineTo(x2, y2);
    	mPath.lineTo(x3, y3);
    	mPath.close();
    	mPath.setFillType(Path.FillType.WINDING);
    	/* Path.FillType.WINDING��ʾ���ڲ���仭��,������ѡ������У�
    	 * Path.FillType.EVEN_ODD
    	 * Path.FillType.INVERSE_WINDING
    	 * Path.FillType.INVERSE_EVEN_ODD*/
    	graphics.drawPath(mPath, paint);
//    	time  = System.currentTimeMillis()- time;
//    	graphics.drawText(""+time, 10, 10, paint);
    }
    
    public   void drawRGB(int[] rgbData, int offset, int scanlength,
			       int x, int y, int width, int height,
			       boolean processAlpha)
    {
    	
    	
    	graphics.drawBitmap(rgbData, offset, scanlength, x, y, width, height, processAlpha, paint);
    }
    
    /**
     * ���ݸ�������ĵ�Ͱ뾶�Լ���������һ�������
     * @param centerX ���ĵ�ĺ�����
     * @param centerY ���ĵ��������
     * @param radius  ������εİ뾶
     * @param n	      ������εı���
     */
    public void drawRegularPolygon(float centerX, float centerY, float radius, int n , int type){
    	//�����ж����ݵĺϷ���
    	if(radius<=0||n<=2)return;
    	Paint.Style style = paint.getStyle();
    	if(type == POLYGON_FILL){
        	paint.setStyle(Paint.Style.FILL);
    	}else if(type == POLYGON_STROKE)
    	{
        	paint.setStyle(Paint.Style.STROKE);
    	}else if(type == POLYGON_DASHED){
    		paint.setStyle(Paint.Style.STROKE);
    		paint.setPathEffect(new DashPathEffect(new float[]{4,2} , 0));
    	}
		Double angle = ( 2 * Math.PI)/n;//���㻡��
		//�ҵ�����1������
		float x1 = centerX - Math.abs((float)(radius* Math.sin(angle/2)));
		float y1 = centerY + Math.abs((float)(radius* Math.cos(angle/2)));
		Path mPath = new Path();
		mPath.moveTo(x1, y1);
		float[]vertex = {x1, y1};
		float[]vertexDsc = {x1 , y1};
//		graphics.save(Canvas.MATRIX_SAVE_FLAG);
		Matrix mMatrix = graphics.getMatrix();
		//����������㣬�����ӵ�Path��
		for(int i = 0 ; i < n ;i++){
			mMatrix.postRotate( (float)(360.f/n), centerX, centerY);
			mMatrix.mapPoints(vertexDsc,vertex);
			mPath.lineTo(vertexDsc[0], vertexDsc[1]);
		}
		mPath.close();
		mPath.setFillType(Path.FillType.WINDING);
		//��Path
		graphics.drawPath(mPath, paint);
//		graphics.restore();
		paint.setStyle(style);
		paint.setPathEffect(null);
    }
    /**
     * ������
     * @param xStart ���λ�õĺ�����
     * @param yStart ���λ�õ�������
     * @param xEnd   �յ�λ�õĺ�����
     * @param yEnd	 �յ�λ�õ�������
     */
    public void drawDashed(int xStart, int yStart, int xEnd, int yEnd){
    	Path path = new Path();
    	float width = paint.getStrokeWidth();
    	Paint.Style  style = paint.getStyle();
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(0);
    	path.moveTo(xStart, yStart);
    	path.lineTo(xEnd, yEnd);
    	paint.setPathEffect(new DashPathEffect(new float[] {4, 2},0));
    	graphics.drawPath(path, paint);
    	paint.setStrokeWidth(width);
    	paint.setStyle(style);
    	paint.setPathEffect(null);
    }
    /**
     * ����������
     * @param xStart ���λ�õĺ�����
     * @param yStart ���λ�õ�������
     * @param xEnd   �յ�λ�õĺ�����
     * @param yEnd	 �յ�λ�õ�������
     * @param type   ���ߵ�����
     * @param speed  �����������ٶȣ�������Ϊ��λ
     */
    public void drawDashed(int xStart, int yStart, int xEnd, int yEnd , int type , int speed){
    	Path path = new Path();
    	float width = paint.getStrokeWidth();
    	Paint.Style  style = paint.getStyle();
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(0);
    	path.moveTo(xStart, yStart);
    	path.lineTo(xEnd, yEnd);
    	if(type == DASHED_NONFLOWING)
    		paint.setPathEffect(new DashPathEffect(new float[] {4, 2},phase));
    	else
    	{
    		paint.setPathEffect(new DashPathEffect(new float[] {4, 2},phase));
    		phase +=speed;
    	}
    	graphics.drawPath(path, paint);
    	paint.setStrokeWidth(width);
    	paint.setStyle(style);
    	paint.setPathEffect(null);
    }
}

