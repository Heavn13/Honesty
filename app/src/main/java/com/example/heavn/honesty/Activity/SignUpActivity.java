package com.example.heavn.honesty.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Sign;
import com.example.heavn.honesty.Bean.SignUp;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.BitmapCache;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.ImageDownloadTask;
import com.example.heavn.honesty.Util.MyApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SignUpActivity extends BaseActivity implements View.OnClickListener{
    public static final int CHOOSE_PICTURE = 1;
    public static final int SHOW_PICTURE = 2;
    private ImageView back,reGet;
    private ImageView picture;
    private TextView taskName,beginTime,endTime,beginDate,endDate,signStatus,location;
    private Button signUp;
    private Button take_photo,choose_albums,pop_cancel;
    private String taskId,currentId,image_path;
    private Task task;
    //用于存储临时照片
    private File proveImage = null;
    private int index,width,height;
    private Uri uri,imageUri;
    private MyApp app;
    //当前用户对象
    private MyUser user;
    private PopupWindow popupWindow;
    //用于储存当前任务的signs数组，并进行修改
    private List<Sign> signs = new ArrayList<>();
    private List<SignUp> signUps = new ArrayList<>();
    //百度地图
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //获取全局的参数
        app = (MyApp)getApplication();
        taskId = app.getTaskId();
        currentId = app.getCurrentId();

        user = MyUser.getCurrentUser(MyUser.class);

        //获取手机屏幕宽度
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        taskName = findViewById(R.id.taskName);
        beginTime = findViewById(R.id.task_beginTime);
        endTime = findViewById(R.id.task_endTime);
        beginDate = findViewById(R.id.beginDate);
        endDate = findViewById(R.id.endDate);
        signStatus = findViewById(R.id.taskStatus);
        location = findViewById(R.id.location);
        reGet = findViewById(R.id.reGet);
        reGet.setOnClickListener(this);
        picture = findViewById(R.id.picture);
        picture.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(this);

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        initView(taskId,currentId);

        starLocate();

        Log.e("time",""+DateUtil.compareTime(beginTime.getText().toString(),DateUtil.getCurrentTime()));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proveImage != null){
            //活动销毁删除系统中的图片
            proveImage.delete();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.reGet:
                //开始定位
                starLocate();
                break;
            case R.id.picture:
                displayPopupWindow();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindowView();
                    //设置popupWindow从底部弹出
                    popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                }
                break;
            case R.id.signUp:
                if(signUp.getText().equals("打卡"))
                    //打卡时间必须在任务要求时间范围之内
                    if(DateUtil.compareTime(DateUtil.getCurrentTime(),beginTime.getText().toString()) && DateUtil.compareTime(endTime.getText().toString(),DateUtil.getCurrentTime()))
                        upLoad(index,image_path);
                    else
                        Toast.makeText(this, "不在打卡时间范围，请在固定时间打卡", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "今日已打卡，无需重复打卡", Toast.LENGTH_SHORT).show();
                break;
            case R.id.take_photo:
                proveImage = new File(Environment.getExternalStorageDirectory(),"proveImage.jpg");
                try{
                    if(proveImage.exists()){
                        proveImage.delete();
                    }
                    proveImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //如果android7.0以上的系统，需要做个判断才能调用相机
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(SignUpActivity.this, "com.example.heavn.honesty.provider", proveImage);//7.0
                } else {
                    imageUri = Uri.fromFile(proveImage); //7.0以下
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,SHOW_PICTURE);
                break;
            case R.id.choose_albums:
                proveImage = new File(Environment.getExternalStorageDirectory(),"proveImage.jpg");
                try{
                    if(proveImage.exists()){
                        proveImage.delete();
                    }
                    proveImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(proveImage);
                Intent intent2 = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 打开手机相册,设置请求码
                startActivityForResult(intent2, CHOOSE_PICTURE);
                break;
            case R.id.pop_cancel:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    //初始化
    private void initView(String id, final String currentId){
        //初始化信息
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.getObject(id, new QueryListener<Task>() {
            @Override
            public void done(Task object, BmobException e) {
                if(e==null){
                    task = object;
                    index = DateUtil.getIndex(object.getBeginDate());
                    taskName.setText(object.getName());
                    beginTime.setText(object.getBeginTime());
                    endTime.setText(object.getEndTime());
                    beginDate.setText(object.getBeginDate());
                    endDate.setText(object.getEndDate());
                    signs = object.getSigns();
                    BmobQuery<Task_User> q= new BmobQuery<Task_User>();
                    q.getObject(currentId, new QueryListener<Task_User>() {
                        @Override
                        public void done(Task_User object, BmobException e) {
                            if(e == null){
                                signStatus.setText(object.getSignUps().get(index).getIsSign());
                                if(!object.getSignUps().get(index).getProveImage().equals("")){
                                    //下载图片
                                    ImageDownloadTask imgTask = new ImageDownloadTask();
                                    imgTask.execute(object.getSignUps().get(index).getProveImage(),picture);
                                    //设置该图片无法修改
                                    picture.setClickable(false);
                                }
                                if(object.getSignUps().get(index).getIsSign().equals("今日已打卡")){
                                    signUp.setText("已打卡");
                                }else{
                                    signUp.setText("打卡");
                                }
                                location.setText(object.getSignUps().get(index).getLocation());
                                signUps = object.getSignUps();
                            }else{
                                e.printStackTrace();
                            }
                        }
                    });

                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    //弹出窗口初始化
    public void initPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popupwindow_photo_style_layout, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView, width, height);
        // 自定义view添加触摸事件，设置点击其他区域弹窗消失
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow= null;
                }
                return false;
            }
        });

        //下拉菜单按钮
        take_photo = customView.findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
        choose_albums = customView.findViewById(R.id.choose_albums);
        choose_albums.setOnClickListener(this);
        pop_cancel = customView.findViewById(R.id.pop_cancel);
        pop_cancel.setOnClickListener(this);

    }

    //弹出窗口
    public void displayPopupWindow(){
        //关闭软键盘,防止popupWindow覆盖软键盘
        if(getCurrentFocus()!=null)
        {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }

    //对图片的处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //选择图片并剪切
            case CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    //此处启动裁剪程序
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, SHOW_PICTURE);
                }
                break;
                //显示图片
            case SHOW_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //image_path = uri.getPath();
                        if(bitmap.getHeight()*bitmap.getWidth() >= 2048000){
                            sizeCompress(bitmap,proveImage);
                        }
                        picture.setImageBitmap(bitmap);
                        //直接获取图片的绝对路径，这么简单的方法竟然弄了两个多小时没有解决
                        image_path = proveImage.getAbsolutePath();
                        //图片完成设置之后，使popupWindow消失
                        popupWindow.dismiss();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(app, "错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //将图片上传到Bmob,并更新数据
    private void upLoad(final int index, final String path){
        if(proveImage != null){
            final BmobFile bmobFile = new BmobFile(new File(path));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        changeArrays(index,bmobFile.getFileUrl());
                        Task task = new Task();
                        task.setSigns(signs);
                        task.update(taskId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    Task_User task_user = new Task_User();
                                    task_user.setSignUps(signUps);
                                    task_user.increment("totalSign");
                                    //判断当日打卡时间与任务截止时间相同，设定任务完成
                                    if(DateUtil.getCurrentDate().equals(endDate.getText().toString())){
                                        task_user.setMyComplete(true);
                                        Toast.makeText(SignUpActivity.this, "相同", Toast.LENGTH_SHORT).show();
                                    }
                                    task_user.update(currentId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e == null){
                                                Toast.makeText(SignUpActivity.this, "打卡成功", Toast.LENGTH_SHORT).show();
                                                signUp.setText("已打卡");
                                            }else{
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }else{
                                    e.printStackTrace();
                                }
                            }
                        });

                    }else{
                        e.printStackTrace();
                    }
                }
                //进度上传对话框,有比较大的问题，不使用
                @Override
                public void onProgress(final Integer value) {
                }
            });
        }else{
            Toast.makeText(this, "请上传证明照片", Toast.LENGTH_SHORT).show();
        }

    }

    //修改签到的两个数组
    private void changeArrays(int index, String path){
        int signNumber = signs.get(index).getSignNumber() + 1;
        signs.get(index).setSignNumber(signNumber);
        signUps.get(index).setIsSign("今日已打卡");
        signUps.get(index).setLocation(location.getText().toString());
        signUps.get(index).setProveImage(path);
    }

    //位置监听器
    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation l){
            String address = l.getAddrStr();    //获取详细地址信息
            String country = l.getCountry();    //获取国家
            String province = l.getProvince();    //获取省份
            String city = l.getCity();    //获取城市
            String district = l.getDistrict();    //获取区县
            String street = l.getStreet();    //获取街道信息
            String d = l.getStreetNumber();     //获取门牌号
            location.setText(province+city+district+street+d);
        }

    }

    //百度地图开始定位
    private void starLocate(){
        //设置信息
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
    }

    //尺寸压缩来压缩图片
    public static void sizeCompress(Bitmap bmp, File file) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = 14;
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
