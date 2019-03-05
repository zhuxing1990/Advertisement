package javax.microedition.lcdui.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vunke.chinaunicom.advertisement.log.LogUtil;



/**
 * Created by zhuxi on 2017/6/26.
 */
public class VideoWindowSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,View.OnClickListener {
    private static final String TAG = "VideoWindowSurfaceView";
    public static SurfaceHolder handleVedio = null;
    public static boolean isEnd = true;
    Context activity;
    public static int currentP = 0;
    //是否从起始位置开始播放
    public static boolean isPlayFromStart = false;
    public static boolean isPlayPause = false;
    MediaPlayer mediaPlayer;
    private Context context;
    private String videoPath;

    public VideoWindowSurfaceView(Context context, String videoPath) {
        super(context);
        this.context = context;
        this.videoPath = videoPath;
        handleVedio = this.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        handleVedio.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        handleVedio.addCallback(this);
    }
    private Uri videouri;
    public VideoWindowSurfaceView(Context context, Uri uri) {
        super(context);
        this.context = context;
        this.videouri = uri;
        handleVedio = this.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        handleVedio.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        handleVedio.addCallback(this);
    }
    /**
     * 播放视频
     */
    public void playVideo() {
        LogUtil.i(TAG, "playVideo:");
        isEnd = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            LogUtil.i(TAG, "playVideo mediaPlayer!=null");
        }
        // 初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        // 重置mediaPaly,建议在初始滑mediaplay立即调用。
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        try {
            if (TextUtils.isEmpty(videoPath)&&videouri!=null){
                mediaPlayer.setDataSource(context, videouri);
            }else if (videouri == null && !TextUtils.isEmpty(videoPath)){
                mediaPlayer.setDataSource(context,  Uri.parse(videoPath));
            }
            // mediaPlayer.reset();
            // mediaPlayer.setDataSource(pathString);

            // mediaPlayer.setDataSource(SurfaceViewTestActivity.this, uri);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
				mediaPlayer.prepareAsync();
//            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "加载视频错误！", Toast.LENGTH_LONG).show();
            LogUtil.i(TAG, "playVideo close from init error.");
        }
    }

    public void setNextVideo(String videoPath){
        LogUtil.i(TAG, "setNextVideo: ");
        this.videoPath = videoPath;
        try {
            if (isPlaying()){
                LogUtil.i(TAG, "setNextVideo: isPlaying:"+isPlaying());
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context,Uri.parse(videoPath));
                mediaPlayer.prepareAsync();
            }else{
                LogUtil.i(TAG, "setNextVideo: isPlaying:"+isPlaying());
                if (mediaPlayer != null) {
                    LogUtil.i(TAG, "mediaPlayer !=null");
                    mediaPlayer.release();
                }else{
                    LogUtil.i(TAG, "mediaPlayer is null");
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.reset();
                    // 设置声音效果
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    // 设置播放完成监听
                    mediaPlayer.setOnCompletionListener(this);
                    // 设置媒体加载完成以后回调函数。
                    mediaPlayer.setOnPreparedListener(this);
                    // 错误监听回调函数
                    mediaPlayer.setOnErrorListener(this);
                }
                mediaPlayer.setDataSource(context,Uri.parse(videoPath));
                mediaPlayer.prepareAsync();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean isPlaying(){
        if (mediaPlayer!=null){
            return    mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated window surfaceCreated....");
        setFocusable(false);// 设置键盘焦点
        setFocusableInTouchMode(false);// 设置触摸屏焦点
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.i(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceDestroyed: ");
        close();
    }
    public void close(){
        LogUtil.i(TAG, "close: ");
        if (null != mediaPlayer) {
            currentP = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlayPause = true;
        }
        isEnd = true;
    }
    public void pause(){
        if (null != mediaPlayer) {
            currentP = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    public void play(){
        if (null != mediaPlayer) {
            mediaPlayer.start();
            // 设置显示到屏幕
            mediaPlayer.setDisplay(handleVedio);
        }
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.i(TAG, "onCompletion: ");
        if (mediaPlayer!=null){
                mediaPlayer.start();
        }
    }
    public void changeVedioSize(int width,int height){
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.height = width;
        lp.width = height;
        this.setLayoutParams(lp);
    }
    public void setLoop(boolean isloop){
        try {
            if (mediaPlayer!=null){
                if (mediaPlayer.isLooping()!=isloop){
                    mediaPlayer.setLooping(isloop);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:

                LogUtil.i(TAG, "onError: MEDIA_ERROR_UNKNOWN");
//                Toast.makeText(context, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT)
//                        .show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                LogUtil.i(TAG,"MEDIA_ERROR_SERVER_DIED");
//                Toast.makeText(context, "MEDIA_ERROR_SERVER_DIED",
//                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                LogUtil.i(TAG,"MEDIA_ERROR_IO");
//                Toast.makeText(context, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT)
//                        .show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                LogUtil.i(TAG,"MEDIA_ERROR_MALFORMED");
//                Toast.makeText(context, "MEDIA_ERROR_MALFORMED",
//                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                LogUtil.i(TAG,"MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
//                Toast.makeText(context,
//                        "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
//                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                LogUtil.i(TAG,"MEDIA_ERROR_TIMED_OUT");
//                Toast.makeText(context, "MEDIA_ERROR_TIMED_OUT",
//                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                LogUtil.i(TAG,"MEDIA_ERROR_UNSUPPORTED");
//                Toast.makeText(context, "MEDIA_ERROR_UNSUPPORTED",
//                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        if (mediaPlayer!=null){
            mediaPlayer.reset();
        }
        return false;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener){
        if (mediaPlayer!=null){
            mediaPlayer.setOnPreparedListener(onPreparedListener);
        }
    }
    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener){
        if (mediaPlayer!=null)mediaPlayer.setOnErrorListener(onErrorListener);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtil.i(TAG,"onPrepared isPlayPause:"+isPlayPause+"|"+currentP);
        // 播放视频
        if(isPlayFromStart){
            mediaPlayer.seekTo(0);
            //马上置为false，因为视频开始播放以后，按home键切出去，再进来的话要从上次播放位置开始
            isPlayFromStart = false;
        }else{
            if(isPlayPause){
                if(currentP>=mediaPlayer.getDuration()){
                    currentP = 0;
                }
                mediaPlayer.seekTo(currentP);
            }else{
                mediaPlayer.seekTo(0);
            }
        }
        LogUtil.i(TAG,"onPrepared start ..");
        mediaPlayer.start();
        // 设置显示到屏幕
        mediaPlayer.setDisplay(handleVedio);
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        handleVedio.setKeepScreenOn(true);
//        MainActivity.removLoadingView();
        LogUtil.i(TAG,"onPrepared end");
    }
}
