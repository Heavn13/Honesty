package com.example.heavn.honesty.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.honesty.Bean.MyUser;
import com.example.heavn.honesty.Bean.Task;
import com.example.heavn.honesty.Bean.Task_User;
import com.example.heavn.honesty.R;
import com.example.heavn.honesty.Util.CircleImageView;
import com.example.heavn.honesty.Util.DateUtil;
import com.example.heavn.honesty.Util.ImageDownloadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ChangeUserDetailActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    public static final int CHOOSE_PICTURE = 1;
    public static final int SHOW_PICTURE = 2;
    private ImageView back;
    private RadioGroup sex;
    private RadioButton male,female;
    private CircleImageView head;
    private EditText username,signature,location,school;
    private TextView save,birthday;
    private String s_sex,image_path,origin_username;
    private PopupWindow popupWindow;
    //用于存储临时照片
    private File proveImage = null;
    private int width,height;
    private Uri imageUri;
    private Button take_photo,choose_albums,pop_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_detail);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        head = findViewById(R.id.head);
        head.setOnClickListener(this);
        username = findViewById(R.id.username);
        sex = findViewById(R.id.sex);
        sex.setOnCheckedChangeListener(this);
        male = findViewById(R.id.male);
        male.setChecked(true);
        female = findViewById(R.id.female);
        signature = findViewById(R.id.signature);
        location = findViewById(R.id.location);
        school = findViewById(R.id.school);
        birthday = findViewById(R.id.birthday);
        birthday.setOnClickListener(this);
        save = findViewById(R.id.save);
        save.setOnClickListener(this);

        //获取手机屏幕宽度
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        init();
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
            case R.id.head:
                displayPopupWindow();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindowView();
                    //设置popupWindow从底部弹出
                    popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                }
                break;
                //保存用户信息
            case R.id.save:
                //如果没有更改头像
                if(proveImage == null){
                    String s_username = username.getText().toString();
                    String s_birthday = birthday.getText().toString();
                    String s_school = school.getText().toString();
                    String s_location = location.getText().toString();
                    String s_signature = signature.getText().toString();
                    MyUser newUser = new MyUser();
                    //用户名唯一
                    if(!origin_username.equals(s_username)){
                        newUser.setUsername(s_username);
                    }
                    newUser.setBirthday(s_birthday);
                    newUser.setSex(s_sex);
                    newUser.setLocation(s_location);
                    newUser.setSchool(s_school);
                    newUser.setSignature(s_signature);
                    MyUser user = MyUser.getCurrentUser(MyUser.class);
                    newUser.update(user.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(ChangeUserDetailActivity.this, "用户信息修改成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChangeUserDetailActivity.this, "该昵称已存在，请修改", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //如果有更改头像
                else{
                    upLoad(image_path);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);//1秒后执行Runnable中的run方法
                break;
            case R.id.birthday:
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if(monthOfYear < 10 && dayOfMonth < 10){
                            birthday.setText(year+"-0"+(monthOfYear+1)+"-0"+dayOfMonth);
                        }else if(monthOfYear < 10 && dayOfMonth >= 10){
                            birthday.setText(year+"-0"+(monthOfYear+1)+"-"+dayOfMonth);
                        }else if(monthOfYear >= 10 && dayOfMonth < 10){
                            birthday.setText(year+"-"+(monthOfYear+1)+"-0"+dayOfMonth);
                        }else{
                            birthday.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
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
                    imageUri = FileProvider.getUriForFile(ChangeUserDetailActivity.this, "com.example.heavn.honesty.provider", proveImage);//7.0
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.male:
                s_sex = "男";
                break;
            case R.id.female:
                s_sex = "女";
                break;
            default:
                break;
        }
    }

    //初始化个人信息
    private void init() {
        MyUser user = MyUser.getCurrentUser(MyUser.class);
        ImageDownloadTask imgTask = new ImageDownloadTask();
        if (user.getAvater() != null)
            imgTask.execute(user.getAvater(), head);
        if (user.getSex() != null && user.getSex().equals("男"))
            male.setChecked(true);
        else if( user.getSex() != null && user.getSex().equals("女"))
            female.setChecked(true);
        else
            male.setChecked(true);
        origin_username = user.getUsername();
        username.setText(user.getUsername());
        if (user.getSignature() != null)
            signature.setText(user.getSignature());
        if (user.getLocation() != null)
            location.setText(user.getLocation());
        if (user.getSchool() != null)
            school.setText(user.getSchool());
        if (user.getBirthday() != null)
            birthday.setText(user.getBirthday());

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
                        head.setImageBitmap(bitmap);
                        //直接获取图片的绝对路径，这么简单的方法竟然弄了两个多小时没有解决
                        image_path = proveImage.getAbsolutePath();
                        //图片完成设置之后，使popupWindow消失
                        popupWindow.dismiss();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ChangeUserDetailActivity.this, "错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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

    //将图片上传到Bmob,并更新数据
    private void upLoad(final String path){
        if(proveImage != null){
            final BmobFile bmobFile = new BmobFile(new File(path));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        String s_username = username.getText().toString();
                        String s_birthday = birthday.getText().toString();
                        String s_school = school.getText().toString();
                        String s_location = location.getText().toString();
                        String s_signature = signature.getText().toString();
                        MyUser newUser = new MyUser();
                        newUser.setAvater(bmobFile.getFileUrl());
                        //用户名唯一
                        if(!origin_username.equals(s_username)){
                            newUser.setUsername(s_username);
                        }
                        newUser.setBirthday(s_birthday);
                        newUser.setSex(s_sex);
                        newUser.setLocation(s_location);
                        newUser.setSchool(s_school);
                        newUser.setSignature(s_signature);
                        MyUser user = MyUser.getCurrentUser(MyUser.class);
                        newUser.update(user.getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(ChangeUserDetailActivity.this, "用户信息修改成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ChangeUserDetailActivity.this, "该昵称已存在，请修改", Toast.LENGTH_SHORT).show();
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
}
