package javax.microedition.lcdui;

import android.graphics.Paint;
import android.graphics.Typeface;

public class Font{
	 public static final int FACE_MONOSPACE =32 ;
	 public static final int FACE_PROPORTIONAL =64 ;
	 public static final int FACE_SYSTEM =0 ;
	 public static final int FONT_INPUT_TEXT =1 ;
	 public static final int FONT_STATIC_TEXT =0 ;
	 public static final int SIZE_LARGE =16 ;
	 public static final int SIZE_MEDIUM =0 ;
	 public static final int SIZE_SMALL =8 ;
	 public static final int STYLE_BOLD =1 ;
	 public static final int STYLE_ITALIC =2 ;
	 public static final int STYLE_PLAIN =0 ;
	 public static final int STYLE_UNDERLINED= 4 ;
	 
	 public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	 public Font(){
		 
	 }
	 
	 public int charWidth(char ch){ 
//		 String s = Character.valueOf(ch).toString();
		 char [] chs = new char[1];
		 float [] width = new float[1];
		 chs[0] = ch;
		 paint.getTextWidths(chs, 0, 1, width);
		 return (int) width[0];
	 }
	 
	 public int charsWidth(char []ch,int offset, int length){
		 int len = 0;
		 //check the length whether is out of bounds of ch
		 if(offset+length > ch.length){
			 length = ch.length - offset;
		 }
		 float [] charWidth = new float[length];
		 paint.getTextWidths(ch, offset, length, charWidth);
		 for(int i = 0; i<charWidth.length; i++){
			 len+=charWidth[i];
		 }
		 return (int)(len/Graphics.scaleWidht);
	 }

	 public int stringWidth(String str,int start,int end){
		 int len = 0;
		 if(end > str.length()){
			 end = str.length();
		 }
		 float [] charWidth = new float[end - start +1];
		 paint.getTextWidths(str, start, end, charWidth);
		 for(int i = 0; i<charWidth.length; i++){
			 len+=charWidth[i];
		 }
		 return (int)(len/Graphics.scaleWidht);
	 }

	 public int stringWidth(String str){
		 int len = str.length();
		 return stringWidth(str, 0 , len);
	 }
	 

	 public int substringWidth(String str, int start, int length){
		 return stringWidth(str, start , start + length -1);
	 }
//	 public int getFace(){
//		 Typeface tf = getTypeface();
//		 return 0;
//	 }
	 
	 public  static Font getFont(int face, int style, int size){
		 Font font = new Font();
		 font.paint.setTextSize(Image.scaleWidht*size);
		 Typeface tf;
		 //set face
		 if(face == FACE_SYSTEM){
			 tf = Typeface.DEFAULT ;
		 }else if(face == FACE_MONOSPACE){
			 tf = Typeface.MONOSPACE;
		 }else /*if(face == FACE_PROPORTIONAL)*/{
			 //haven't find the correspond style,so use default
			 tf = Typeface.DEFAULT;
		 }
		 //set style
		 if(style == STYLE_BOLD){
			 tf = Typeface.create(tf,Typeface.BOLD);
		 }
		 else if (style == STYLE_ITALIC){
			 tf = Typeface.create(tf,Typeface.ITALIC);
		 }
		 else if (style == STYLE_PLAIN){
			 tf = Typeface.create(tf,Typeface.NORMAL);	 
		 }
		 font.paint.setTypeface(tf);
		 if (style == STYLE_UNDERLINED){
			 font.paint.setFlags(Paint.UNDERLINE_TEXT_FLAG) ;
		 }
		return font; 
	 }
	 
	 public static Font getDefaultFont(){
		 Font font = new Font();
		 font.paint.setTextSize(Image.scaleWidht*12);
		 font.paint.setTypeface(Typeface.DEFAULT);
		 return font;
	 }
	 
	 public static Font getArbitraryFont(int size){
		 Font font = new Font();
		 font.paint.setTextSize(size);
		 font.paint.setTypeface(Typeface.DEFAULT);
		 return font;
	 }
	 
	 public boolean isBold(){
		 return paint.getTypeface().isBold(); 
	 }
	 
	 public boolean isItalic(){
		 return paint.getTypeface().isItalic();
	 }
	 
	 public boolean isUnderlined(){
		 return paint.isUnderlineText();
	 }

	 public int getSize(){
		 return (int)paint.getTextSize();
	 }

	 public int getHeight(){
		 return ((int)(paint.getTextSize()));
	 }

	 public void setSize(int size){
		 paint.setTextSize(size);
	 }
	 
}
