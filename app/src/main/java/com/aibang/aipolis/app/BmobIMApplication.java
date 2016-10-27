package com.aibang.aipolis.app;

import android.app.Application;
import android.content.Context;

import com.aibang.aipolis.utils.SDCardUtils;
import com.aibang.aipolis.view.UILImageLoader;
import com.aibang.aipolis.view.UILPauseOnScrollListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.PlatformConfig;

import org.litepal.LitePalApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.bmob.newim.BmobIM;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * @author :smile
 * @project:BmobIMApplication
 * @date :2016-01-13-10:19
 */
public class BmobIMApplication extends Application {
    public static final String appId = "7959be2ae1fa0af5c8e58bbcc1e6bca9";
    //拍照的保存路径
    public final static String rootFilePath = SDCardUtils.getSDPath() + File.separator + "Aipolis"+
            File.separator+"photo";
    //图片缓存路径
    public final static String cacheFilePath = SDCardUtils.getSDPath() + File.separator
            + "Aipolis"+ File.separator+"cache";

    private static BmobIMApplication INSTANCE;
    public static BmobIMApplication INSTANCE(){
        return INSTANCE;
    }
    private void setInstance(BmobIMApplication app) {
        setBmobIMApplication(app);
    }
    private static void setBmobIMApplication(BmobIMApplication a) {
        BmobIMApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        copyDb();

        LitePalApplication.initialize(this);
        //初始化图片加载的配置
        initImageLoader(getApplicationContext());
        //初始化图片控件 galleryFinal
        initGalleryFinal();

        Bmob.initialize(this, appId);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
//         启动推送服务
        BmobPush.startWork(this);

        //初始化
        Logger.init("smile");
        //只有主进程运行的时候才需要初始化

//        //uil初始化
//        UniversalImageLoader.initImageLoader(this);

        setInstance(this);
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }

        //初始化分享集成
        initShare();
    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 初始化图片控件 galleryFinal
     */

    private void initGalleryFinal() {
        //设置主题
        ThemeConfig theme = ThemeConfig.DEFAULT;
        File file = new File(rootFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        //配置功能
        final FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setMutiSelectMaxSize(9)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        CoreConfig coreConfig = new CoreConfig.Builder(this, new UILImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setTakePhotoFolder(file)
                .setNoAnimcation(true)
                .setPauseOnScrollListener(new UILPauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);
    }

    /**
     * 初始化图片加载的配置
     *
     * @param context
     */
    public static void initImageLoader(Context context) {

        File file = new File(cacheFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCache(new UnlimitedDiskCache(file)); // default
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
    /**
     * 将数据库复制到本地
     */
    private void copyDb(){
        String DB_PATH = "/data/data/com.aibang.aipolis/databases/";
        String DB_NAME = "school.db";

        if((new File(DB_PATH + DB_NAME).exists()) == false)
        {
            File dir = new File(DB_PATH);
            if (!dir.exists())
            {
                dir.mkdir();
            }

            try {
                InputStream is = getBaseContext().getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                byte[] buffer = new byte[1024];
                int length;

                while((length = is.read(buffer)) > 0)
                {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
//                Toast.makeText(this, "数据库不存在 复制到本地", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void initShare(){
        PlatformConfig.setWeixin("wx7ba2bb3281a26df0", "39639964aec39c166a94a358cba62b92");
        PlatformConfig.setQQZone("1105426618", "6FT8nydtlykKBbaG");
    }
}
