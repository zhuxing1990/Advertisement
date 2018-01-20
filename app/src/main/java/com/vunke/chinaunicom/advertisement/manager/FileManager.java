package com.vunke.chinaunicom.advertisement.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.vunke.chinaunicom.advertisement.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhuxi on 2018/1/2.
 */
public class FileManager {
    private static final String TAG = "FileManager";
    public static List<String> initVideoFile(Context context) {
        LogUtil.i(TAG, "initVideoFile: ");
        List<String> fileList = null;//= new ArrayList<String>();
        try {
//            String imagePath = mcontext.getFilesDir().getAbsolutePath() + DownloadManager.ADVERT_READ_IMAGE_PATH;
            String videoPath = context.getFilesDir().getAbsolutePath() + DownloadManager.ADVERT_READ_VIDEO_PATH;
            File file = new File(videoPath);
            if (file.exists()) {
                LogUtil.i(TAG, "initVideoFile: getVideo exists");
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File videoFile : files) {
                        LogUtil.i(TAG, "initVideoFile: videoFile:" + videoFile.getName());
                        boolean isVideoFile = FileManager.isVideoFile(videoFile);
                        LogUtil.i(TAG, "initVideoFile: isVideoFile:"+isVideoFile);
                        if (!isVideoFile){
                            LogUtil.i(TAG, "initVideoFile: get file is not exists");
                            continue;
                        }
                        fileList = new ArrayList<>();
                        fileList.add(videoFile.getPath());
                    }
                }else{
                    LogUtil.i(TAG, "initVideoFile: get files is null");
                }
            }else{
                LogUtil.i(TAG, "initVideoFile: getVideo not exists");
                File video = new File(context.getFilesDir().getAbsolutePath()+DownloadManager.ADVERT_BASE_VIDEO_PATH);
                if (!video.exists()){
                    video.mkdirs();
                    File videoFile = new File(context.getFilesDir().getAbsolutePath()+DownloadManager.ADVERT_BASE_VIDEO_PATH+File.separator+"base_video.mp4");
                    videoFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(videoFile);
                    LogUtil.i(TAG, "initVideoFile: get base video file failed,create file");
                    InputStream is= context.getResources().getAssets().open("base_video.mp4");
                    byte[] buffer = new byte[1024];
                    int byteCount=0;
                    while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                        fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                    }
                    fos.flush();//刷新缓冲区
                    is.close();
                    fos.close();
                    fileList = new ArrayList<>();
                    fileList.add(videoFile.getPath());
                }else{
                    LogUtil.i(TAG, "initVideoFile: get base video file success");
                    fileList = new ArrayList<>();
                    fileList.add(video.getPath()+File.separator+"base_video.mp4");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public static boolean isVideoFile(String name) {
        if (TextUtils.isEmpty(name)){
            return false;
        }
        return getVideoFile(name);
    }
    public static boolean isVideoFile(File file) {
        if (!file.exists())return false;
        return getVideoFile(file.getName());
    }
    private static boolean getVideoFile(String name) {
        int i = name.indexOf('.');
        if (i != -1) {
            name = name.substring(i);
            if (name.equalsIgnoreCase(".mp4")
                    || name.equalsIgnoreCase(".3gp")
                    || name.equalsIgnoreCase(".wmv")
                    || name.equalsIgnoreCase(".ts")
                    || name.equalsIgnoreCase(".rmvb")
                    || name.equalsIgnoreCase(".mov")
                    || name.equalsIgnoreCase(".m4v")
                    || name.equalsIgnoreCase(".avi")
                    || name.equalsIgnoreCase(".m3u8")
                    || name.equalsIgnoreCase(".3gpp")
                    || name.equalsIgnoreCase(".3gpp2")
                    || name.equalsIgnoreCase(".mkv")
                    || name.equalsIgnoreCase(".flv")
                    || name.equalsIgnoreCase(".divx")
                    || name.equalsIgnoreCase(".f4v")
                    || name.equalsIgnoreCase(".rm")
                    || name.equalsIgnoreCase(".asf")
                    || name.equalsIgnoreCase(".ram")
                    || name.equalsIgnoreCase(".mpg")
                    || name.equalsIgnoreCase(".v8")
                    || name.equalsIgnoreCase(".swf")
                    || name.equalsIgnoreCase(".m2v")
                    || name.equalsIgnoreCase(".asx")
                    || name.equalsIgnoreCase(".ra")
                    || name.equalsIgnoreCase(".ndivx")
                    || name.equalsIgnoreCase(".xvid")) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static void localFileDelete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                localFileDelete(f);
            }
            file.delete();
        }
    }


    public static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(
                        new File(targetDir).getAbsolutePath() + File.separator
                                + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                LogUtil.i(TAG, "准备复制的源文件夹"+dir1);
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                LogUtil.i(TAG, "准备复制的目标文件夹"+dir2);
                copyDirectiory(dir1, dir2);
            }
        }
    }

    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }


    public static List<String> initImageFile(Context context) {
        LogUtil.i(TAG, "initVideoFile: ");
        List<String> fileList = null;
        try {
            String imagePath = context.getFilesDir().getAbsolutePath() + DownloadManager.ADVERT_READ_IMAGE_PATH;
            File file = new File(imagePath);
            if (file.exists()) {
                LogUtil.i(TAG, "initImageFile: get image exists");
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File imageFile : files) {
                        LogUtil.i(TAG, "initImageFile: imageFile:" + imageFile.getName());
                        boolean isImageFile = FileManager.isImageFile(imageFile);
                        LogUtil.i(TAG, "initImageFile: isImageFile:"+isImageFile);
                        if (!isImageFile){
                            LogUtil.i(TAG, "initImageFile: get file is not exists");
                            continue;
                        }
                        fileList = new ArrayList<>();
                        fileList.add(imageFile.getPath());
                    }
                }else{
                    LogUtil.i(TAG, "initImageFile: get files is null");
                }
            }else{
                LogUtil.i(TAG, "initImageFile: get Image not exists");
                File video = new File(context.getFilesDir().getAbsolutePath()+DownloadManager.ADVERT_BASE_IMAGE_PATH);
                if (!video.exists()){
                    video.mkdirs();
                    File videoFile = new File(context.getFilesDir().getAbsolutePath()+DownloadManager.ADVERT_BASE_IMAGE_PATH+File.separator+"main_backage.png");
                    videoFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(videoFile);
                    LogUtil.i(TAG, "initImageFile: get base video file failed,create file");
                    InputStream is= context.getResources().getAssets().open("base_video.mp4");
                    byte[] buffer = new byte[1024];
                    int byteCount=0;
                    while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                        fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                    }
                    fos.flush();//刷新缓冲区
                    is.close();
                    fos.close();
                    fileList = new ArrayList<>();
                    fileList.add(videoFile.getPath());
                }else{
                    LogUtil.i(TAG, "initImageFile: get base video file success");
                    fileList = new ArrayList<>();
                    fileList.add(video.getPath()+File.separator+"main_backage.png");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }
    public static boolean isImageFile(File file) {
        if (!file.exists())return false;
        return getImageFile(file.getName());
    }
    public static boolean isImageFile(String name) {
        if (TextUtils.isEmpty(name)){
            return false;
        }
        return getImageFile(name);
    }
    private static boolean getImageFile(String name) {
        int i = name.indexOf('.');
        if (i != -1) {
            name = name.substring(i);
            if (name.equalsIgnoreCase(".png")
                    ||name.equalsIgnoreCase(".jpg")
            ||name.equalsIgnoreCase(".jpeg")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }




    private final String[][] MIME_MapTable={
//{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };


    @SuppressWarnings("unchecked")
    @SuppressLint("DefaultLocale")
    public static List<Drawable> getpicsPath(String sdpath, Resources res) {
        List<Drawable> drawableList = new ArrayList<Drawable>();
        // 打开SD卡目录
        File file = new File(sdpath);
        // 获取SD卡目录列表
        File[] files = file.listFiles();
        if(null == files){
            return drawableList;
        }

        List <String>fileList = new ArrayList<String>();
        for (int z = 0; z < files.length; z++) {
            File f = files[z];
            if (f.isFile()) {
                String filename = f.getName();
                int idx = filename.lastIndexOf(".");
                if (idx <= 0) {
                    return null;
                }
                String suffix = filename.substring(idx + 1, filename.length());
                if (suffix.toLowerCase().equals("jpg")
                        || suffix.toLowerCase().equals("jpeg")
                        || suffix.toLowerCase().equals("png")) {
                    fileList.add(filename);
                }
            } else {
                LogUtil.i(TAG, "not file");
            }
        }
        Collections.sort(fileList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                String first = ((String)o1).toString();
                String second = ((String)o2).toString();
                int a=0;
                int b=0;
                try{
                    a = Integer.parseInt(first.substring(first.lastIndexOf("_")+1, first.lastIndexOf(".")));
                    b = Integer.parseInt(second.substring(second.lastIndexOf("_")+1, second.lastIndexOf(".")));
                }catch(Exception e){
                    LogUtil.i(TAG, "------exception----");
                    return 1;
                }
                return ((Integer)a).compareTo((Integer)b);
            }
        });
        LogUtil.i(TAG, "---" + fileList.toString());
        BitmapFactory.Options bfOptions;
        Bitmap bm = null;
        FileInputStream fs = null;
        for (int j = 0; j < fileList.size(); j++) {
//			BitmapFactory.Options bfOptions=new BitmapFactory.Options();
//			bfOptions.inTempStorage= new byte [10240 * 1024];
//			Bitmap bm = BitmapFactory.decodeFile(sdpath + "/"+fileList.get(j) ,bfOptions);
            bfOptions = new BitmapFactory.Options();
            bfOptions.inSampleSize = 2;
            bfOptions.inDither = false;
            bfOptions.inPurgeable = true;
            bfOptions.inTempStorage = new byte[100 * 1024];
            bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//			bfOptions.inJustDecodeBounds = true;
            bfOptions.inInputShareable = true;
            File bmfile = new File(sdpath + "/" + fileList.get(j));
            try {
                fs = new FileInputStream(bmfile);
                if (fs != null)
                    bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                Drawable drawable = new BitmapDrawable(res, bm);
                drawableList.add(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return drawableList;

    }
    @SuppressWarnings("unchecked")
    public static List<Drawable> getPicFromAsset(Context ct,Resources res){
        List<Drawable> drawableList = new ArrayList<Drawable>();
        try {
            String[] list = ct.getAssets().list("");
            List <String>fileList = new ArrayList<String>();

            for (String file : list) {
                if (file == null || "".equalsIgnoreCase(file))
                    continue;
                if (file.endsWith(".jpg") || file.endsWith(".jpeg")
                        || file.endsWith(".png")) {
                    fileList.add(file);

                }
            }
            Collections.sort(fileList, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String first = ((String)o1).toString();
                    String second = ((String)o2).toString();
                    int a=0;
                    int b=0;
                    try{
                        a = Integer.parseInt(first.substring(first.lastIndexOf("_")+1, first.lastIndexOf(".")));
                        b = Integer.parseInt(second.substring(second.lastIndexOf("_")+1, second.lastIndexOf(".")));
                    }catch(Exception e){
                        LogUtil.i(TAG, "------exception----");
                        return 1;
                    }
                    return ((Integer)a).compareTo((Integer)b);
                }
            });
            LogUtil.i(TAG, "---"+fileList.toString());
            for(int j=0;j<fileList.size();j++){
                Bitmap bm = BitmapFactory.decodeStream(ct.getAssets().open(fileList.get(j)));
                Drawable drawable =new BitmapDrawable(res,bm);
                drawableList.add(drawable);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawableList;
    }
}
