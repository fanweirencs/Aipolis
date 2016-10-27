package com.aibang.aipolis.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.lzy.ninegrid.ImageInfo;

import java.util.List;

/**
 * 公共方法
 * Created by zcf on 2016/5/8.
 */
public class ComUtils {
    /**
     * 开启沉浸式状态栏
     */
    public static void openImmerseStatasBarMode(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }




    /**
     * 将图片地址解析
     *
     * @param imgList 图片集合
     * @param img     图片源地址 例如 www.aa.jpg&www.bb.jpg&www.cc.jpg
     */
    public static void splitImageUrls(List<ImageInfo> imgList, String img) {
        //图片地址
        String[] listPath = img.split("&");

        if (!((listPath[0].equals("No") || listPath[0].equals("NO")))) {
            for (int k = 0; k < listPath.length; k++) {
                ImageInfo item = new ImageInfo();
                item.setThumbnailUrl(listPath[k]);//缩略图地址
                item.setBigImageUrl(listPath[k]);//大图地址

                imgList.add(item);

            }
        }
    }



}
