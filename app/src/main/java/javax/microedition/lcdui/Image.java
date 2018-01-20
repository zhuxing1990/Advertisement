package javax.microedition.lcdui;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;

import java.io.IOException;
import java.io.InputStream;

public class Image {
	
	Bitmap bitmap = null;
	public Image mirBitmap = null;
	Graphics imgGraphics = null;
	public static boolean isZoom = false;
	public static float scaleWidht = 0; 
	public static float scaleHeight = 0;
	public static Context context;
	public boolean isJPG = false;

	public static void setConfig(boolean is,float scw,float sch,Context cont){
		isZoom = is;
		scaleWidht = scw;
		scaleHeight = sch;
		context = cont;
	}
	
	
	public Bitmap getBitmap(){
		return bitmap;
	}
	public Image(Bitmap map){
		bitmap = map;
	}
	
	public Image(Bitmap map,boolean is){
		bitmap = map;
		this.isJPG = is;
	}

	public Image(int width, int height){
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		//imgGraphics = new Graphics(bitmap);
	}
	
	public Image(int width, int height,boolean isJpg){
		if(isJpg){
			bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		}else{
			bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}
		
		//imgGraphics = new Graphics(bitmap);
	}

	private Image(Image img){
		bitmap=img.bitmap.copy(img.bitmap.getConfig(),true);
	}
	
	private Image(int id){
		
		bitmap = BitmapFactory.decodeResource(context.getResources(),id);
	}
	
	public void clear(){
		if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
	}
	
	public Image(InputStream stream){
		System.gc();
		bitmap = BitmapFactory.decodeStream(stream);
	}
	
	  
  
	private Image(byte[] imageData, int imageOffset, int imageLength) {
      //decodeImage(imageData, imageOffset, imageLength);
  	
  	bitmap=BitmapFactory.decodeByteArray(imageData, imageOffset, imageLength);
	}
	

	private Image(int[] rgbImageData, int width, int height, boolean parseAlpha) {
    	if (rgbImageData == null)
			throw new NullPointerException();
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException();
		// TODO processAlpha is not handled natively, check whether we need to create copy of rgb

		int[] newrgb = rgbImageData;
//		if (!parseAlpha) {
//			newrgb = new int[rgbImageData.length];
//			for (int i = 0; i < rgbImageData.length; i++) {
//				newrgb[i] = rgbImageData[i] | 0xff000000;
//			}
//		}
		if(!parseAlpha)
			bitmap=Bitmap.createBitmap(width, height, Config.RGB_565);
		else
			bitmap=Bitmap.createBitmap(width, height, Config.ARGB_8888);	
	  	 Canvas canvas=new Canvas(bitmap);
	  	 Paint paint = new Paint();
	  	 canvas.drawBitmap(rgbImageData, 0, width, 0, 0, width, height, parseAlpha, paint);
      
    }
    
   
	private Image( Resources res,int id){
    	bitmap = BitmapFactory.decodeResource(res, id);
    }
    
    public static Image createImage(Resources res, int id){
    	return new Image(res,id);
    }
    
	public static Image createImage(int width, int heigth){
		return new Image(width,heigth);
	}
	
	public static Image createImage(int id){
		return new Image(id);
	}
	
	public static Bitmap createBitMap(String str){
		Image img = null;
		try {
			img = createImage(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bit = img.bitmap;
	     return bit;
	}
	
	public static Image createImageByJpg(String str)throws IOException{
		if(str.substring(0, 1).equals("/")){
			str = str.substring(1);
		}
		Bitmap image = null;  
		  AssetManager am = context.getResources().getAssets();
	      try  
	      {  
	          InputStream is = am.open(str);
	          image = BitmapFactory.decodeStream(is);
	          is.close();
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }
	      Matrix matrix = new Matrix();
	      int add = 1;
	      if(isZoom){
	    	  float tempX = (image.getWidth()*scaleWidht+add);
	    	  float tempY = (image.getHeight()*scaleHeight+add);
	    	  float tempX1 = tempX/image.getWidth();
	    	  float tempY1 = tempY/image.getHeight();
//	    	  System.out.println("tempX1:"+tempX1+"|"+tempY1);
		      matrix.postScale(tempX1, 
		    		  tempY1);
	      }
	      Bitmap bit = null;
	      boolean isJpg = true;
    	  bit = Bitmap.createBitmap((int)(image.getWidth()*scaleWidht)+add,
    			 (int)(image.getHeight()*scaleHeight)+add, Config.ARGB_8888);
	      
	      Canvas canvas=new Canvas(bit);
	      if(isZoom){
	    	  canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
	      }
	 	  Paint paint = new Paint();
	 	  canvas.drawBitmap(image,matrix,paint);
	    return new Image(bit,isJpg);
	}
	
	public static Image createImage(String str)throws IOException{
		if(str.substring(0, 1).equals("/")){
			str = str.substring(1);
		}
		Bitmap image = null;  
		  AssetManager am = context.getResources().getAssets();
	      try  
	      {  
	          InputStream is = am.open(str);
	          image = BitmapFactory.decodeStream(is);
	          is.close();
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }
	      String str1 = str.substring(str.length()-4,str.length());
	      Matrix matrix = new Matrix();
	      if(isZoom){
		      matrix.postScale(scaleWidht, scaleHeight);
	      }
	      Bitmap bit = null;
	      boolean isJpg = false;
	      if(str1.equals(".jpg")){
	    	  isJpg = true;
	    	 bit = Bitmap.createBitmap((int)(image.getWidth()*scaleWidht),
	    			 (int)(image.getHeight()*scaleHeight), Config.RGB_565);
	      }else{
	    	  isJpg = false;
	    	  bit = Bitmap.createBitmap((int)(image.getWidth()*scaleWidht),
		    			 (int)(image.getHeight()*scaleHeight), Config.ARGB_8888);
	      }
	      Canvas canvas=new Canvas(bit);
	      if(isZoom){
	    	  canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
	      }
	 	  Paint paint = new Paint();
	 	  canvas.drawBitmap(image,matrix,paint);
	    return new Image(bit,isJpg);
	}
	
	
	public static Image createImage(InputStream stream)throws IOException{
		try{
			return new Image(stream);
		}catch(IllegalArgumentException e){
			throw new IOException();
		}
	}
	public static Image createImage(Image source){
		return new Image(source);
	}
	
	public static Image createImage(byte [] imageData, int imageOffset, int imageLength){
		if (imageOffset < 0 || imageOffset >= imageData.length ||
			    imageLength < 0 ||
			    imageOffset + imageLength > imageData.length) {
		            throw new ArrayIndexOutOfBoundsException();
		 }
		return new Image(imageData, imageOffset, imageLength);
	}
	
	public static Image createImage(Image image,
									int x, int y,
									int width, int height,
									int transform){
		if (image == null)

			throw new NullPointerException();

		if(isZoom){
			x = (int)(x*scaleWidht);
			y = (int)(y*scaleHeight);
			
			width = (int)(width*scaleWidht);
			height = (int)(height*scaleHeight)+1;
			if(x + width>image.getFWidth()){
				width = image.getFWidth()-x;
			}
			if(y + height>image.getFHeight()){
				height = image.getFHeight()-y;
			}
		}
		if (x + width > image.getFWidth() || y + height > image.getFHeight() || width <= 0 || height <= 0 || x < 0

				|| y < 0){
			throw new IllegalArgumentException("Area out of Image1111");
		}
		//create a identity matrix
		Matrix matrix = new Matrix();
		//ˮƽ�������
		float values[]= {-1,0,image.getFWidth(),
						 0,1,0,
						 0,0,1};
		boolean filter = false;
		if(transform == Globe.TRANS_ROT90){
			matrix.postRotate(90);
		}else
		if(transform == Globe.TRANS_ROT180){
			matrix.postRotate(180);
		}else
		if(transform == Globe.TRANS_ROT270){
			matrix.postRotate(270);
		}else
		if(transform == Globe.TRANS_MIRROR){
			matrix.setValues(values);
		}else
		if(transform == Globe.TRANS_MIRROR_ROT90){
			matrix.setValues(values);
			matrix.postRotate(90);//Rotate(90);
			//filter = true;
		}else
		if(transform == Globe.TRANS_MIRROR_ROT180){;
			matrix.setValues(values);
			matrix.postRotate(180);
			//filter = true;
		}else
		if(transform == Globe.TRANS_MIRROR_ROT270){
			matrix.setValues(values);
			matrix.postRotate(270);
			//filter = true;
		}
		Bitmap map = Bitmap.createBitmap(image.getBitmap(), x, y, width, height, matrix, filter);
		Bitmap bit = null;
		if(image.isJPG){
			bit = Bitmap.createBitmap(map.getWidth(),
		   			map.getHeight(), Config.RGB_565);
		}else{
			bit = Bitmap.createBitmap(map.getWidth(),
		   			map.getHeight(), Config.ARGB_8888);
		}
	      Canvas canvas=new Canvas(bit);
	      if(isZoom){
	    	  canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
	      }
	 	  Paint paint = new Paint();
	 	  canvas.drawBitmap(map,matrix,paint);
		return new Image(bit);
	}
	
//	public int change(int num,float z){
//		return Math.round(num*z); 
//	}
	
	
	public Graphics getGraphics() {

        // SYNC NOTE: Not accessing any shared data, no locking necessary
    	if(imgGraphics==null && bitmap ==null)
    		throw new IllegalStateException();
    	else if(imgGraphics == null)
    		imgGraphics = new Graphics(bitmap);
;    	return imgGraphics;
//        throw new IllegalStateException();
    }
	
    public int getFWidth() {
        // SYNC NOTE: return of atomic value, no locking necessary
    	return bitmap.getWidth();
//        return (int)(bitmap.getWidth()/scaleWidht);
    }

    public int getFHeight() {
        // SYNC NOTE: return of atomic value, no locking necessary
    	return bitmap.getHeight();
//        return (int)(bitmap.getHeight()/scaleHeight);
    }
    
    public int getWidth() {
        // SYNC NOTE: return of atomic value, no locking necessary
    	return (int)(bitmap.getWidth()/scaleWidht);
    }

    public int getHeight() {
        // SYNC NOTE: return of atomic value, no locking necessary
    	return (int)(bitmap.getHeight()/scaleHeight);
    }

    public boolean isMutable() {
        // SYNC NOTE: return of atomic value, no locking necessary
    		
    	return bitmap.isMutable();
    }
    
    
    public static Image createRGBImage(int rgb[], int width, int height, boolean processAlpha){
    	if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }

        if ((width * height) > rgb.length) { 
            throw new ArrayIndexOutOfBoundsException();
        }
        return new Image(rgb,width,height,processAlpha);
    }
    
    /**
     * �Ŵ���СͼƬ
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public Image zoomBitImage(int w, int h){
    	if(w<=0||h<=0){
    		throw new IllegalArgumentException();
    	}
    	int width = bitmap.getWidth(); 
    	int height = bitmap.getHeight(); 
    	Matrix matrix = new Matrix(); 
    	float scaleWidht = ((float)w / width); 
    	float scaleHeight = ((float)h / height); 
    	matrix.postScale(scaleWidht, scaleHeight);
    	
    	Image img = new Image(w,h);
    	img.bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    	return img;
    } 

}
