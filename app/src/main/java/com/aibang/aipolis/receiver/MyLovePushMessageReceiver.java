package com.aibang.aipolis.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aibang.aipolis.R;
import com.aibang.aipolis.activity.MainActivity;
import com.aibang.aipolis.event.MsgRefreshEvent;
import com.aibang.aipolis.event.SetNotificationEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class MyLovePushMessageReceiver extends BroadcastReceiver {
    String style="";//1代表评论   101 代表推送给发求助时 所有关注我的人的人  102 代表发送推送 邀约

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 获取推送消息

        String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
        String trueMsg;
        trueMsg = parseJSONdATA(message);

        Log.i("BmobClient", "收到的推送消息：" + message);
//        Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
        // 发送通知
        // 第一步：获取状态通知栏管理：
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //第二步：实例化通知栏构造器NotificationCompat.Builder：
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//        第三步：对Builder进行配置：

        mBuilder
                .setContentTitle("消息")//设置通知栏标题
                .setContentText(trueMsg) //设置通知栏显示内容
//  .setNumber(3) //设置通知集合的数量
                .setTicker("桃花源") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo
                ))
        ;//设置通知小ICON

        Intent intent2 = new Intent(context, MainActivity.class);

        intent2.putExtra("message", trueMsg);
        if (trueMsg.contains("你关注的人"))
        intent2.putExtra("style",style);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.setAction(System.currentTimeMillis() + "");
        //params 2 can
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);//设置通知栏点击意图
        int requestCode = (int) System.currentTimeMillis();
        Notification notification = mBuilder.build();
        mNotificationManager.notify(requestCode, notification);
        if (style.equals("1")) {
            EventBus.getDefault().post(new SetNotificationEvent());//通知主界面设置数字
            EventBus.getDefault().post(new MsgRefreshEvent());//通知主界面刷新消息
        }

        //每次通知完，通知ID递增一下，避免消息覆盖掉

    }

    /**
     * 解析json数据
     */
    private String parseJSONdATA(String jsonData) {
        try {
            JSONObject js = new JSONObject(jsonData);
            String s = js.getString("aps");
            JSONObject j = new JSONObject(s);
            String alert = j.getString("alert");
            style = j.getString("style");
//            Log.d("alert007", alert);
//            Log.d("alert007", style+"  is ");
            return alert;
        } catch (JSONException e) {

            try {
                JSONObject js2 = new JSONObject(jsonData);
                String s = js2.getString("aps");
                JSONObject j2 = new JSONObject(s);
                String alert2 = j2.getString("alert");
//                Log.d("alert007", alert2);
                return alert2;
            } catch (JSONException e1) {
                e1.printStackTrace();
                return "您有一条消息";
            }

        }
    }


}
