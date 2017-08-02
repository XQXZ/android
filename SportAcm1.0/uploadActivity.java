package com.sdutacm.sportacm;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class uploadActivity extends AppCompatActivity {

    public static  final  int TAKEPHOTO = 1;
    public static final  int CHOOSE_PHOTO = 2;

    private String newName = "temp.png"; //上传后在服务器上文件的名称

    File file = Environment.getExternalStorageDirectory();
    File fileAbs = new File(file,"temp.png");
    private String uploadFile = fileAbs.getAbsolutePath(); //要上传的文件路径
    private String actionUrl = "http://yun.meik.pw/index.php/sports/index/upimage";

    private ImageView picture;
    private Uri imageUri;

    private EditText studentName,studentMajorAndClass,studentID;
    private TextView mText1,mText2;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mText1 = (TextView) findViewById(R.id.myText2);
        // mText1.setText("文件路径:\n"+uploadFile);
        mText2 = (TextView) findViewById(R.id.myText3);
        mText2.setText("上传网址:\n"+actionUrl);
        mButton = (Button) findViewById(R.id.myButton);

        //详见StrictMode文档
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

//        //拍照片按钮
//        Button takePhoto = (Button) findViewById(R.id.take_photo);
        //从相册中选照片按钮
        Button ChooseFromAlbum = (Button) findViewById(R.id.choose_from_album);


        picture = (ImageView) findViewById(R.id.picture);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(uploadActivity.this,"正在上传文件,请稍后......",Toast.LENGTH_SHORT).show();
                uploadFile();
            }
        });
//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(uploadActivity.this,"正在打开相机驱动,请稍后......",Toast.LENGTH_SHORT).show();
//                //创建File对象，用于存储拍照后的照片
//                File outputImage = new File(getExternalCacheDir(),
//                        "output_image.jpg");
//                try {
//                    if(outputImage.exists()){
//                        outputImage.delete();
//                    }
//                    outputImage.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if(Build.VERSION.SDK_INT >= 24){
//                    imageUri = FileProvider.getUriForFile(uploadActivity.this,
//                            "com.sdutacm.sportacm.fileprovider",outputImage);
//                }else {
//                    imageUri = Uri.fromFile(outputImage);
//                }
//                //启动相机程序
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//                startActivityForResult(intent,TAKEPHOTO);
//            }
//        });
        ChooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(uploadActivity.this,"正在打开相册,请稍后......",Toast.LENGTH_SHORT).show();
                if(ContextCompat.checkSelfPermission(uploadActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(uploadActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void uploadFile() {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /*
            * 允许向服务器读写数据
            * 不使用Cache
            * */
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            /*设置传送的Method = POST*/
            con.setUseCaches(false);
            con.setRequestProperty("Connection","Keep-Alive");
            con.setRequestProperty("Charset","UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary="+boundary);
            /*设置DataOutputStream*/
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens+boundary + end);
            ds.writeBytes("Content-Disposition: from-data; "+
                    "name=\"file1\";filename=\""+
                    newName +"\"" +end);
            ds.writeBytes(end);
            /*取得文件的FileInputStream*/
            FileInputStream fStream = new FileInputStream(uploadFile);
            /*设置每次写入1024 bytes*/
            int bufferSize = 1024;
            byte [] buffer = new byte[bufferSize];
            int length = -1;
            //从文件读取数据到缓冲区
            while ((length = fStream.read(buffer))!=-1){
                //将文件写入DataOutStream中
                ds.write(buffer,0,length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            fStream.close();
            ds.flush();
            /*取得Response内容*/
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            Log.d("Error","b first is "+b);
            int count =0;
            while ((ch = is.read())!=-1){
                b.append((char)ch);
                count++;
                Log.d("Error","b is the "+b);

            }
            /**
             * 将Response 显示到Dialog对话框中
             */
            Log.d("Error","Error is the "+b);
            Log.d("Error","Error is the "+b.toString());
            Log.d("Error","Error is the "+b.toString().trim());
            Log.d("Error","Error is the "+b.append("啊哈"));
            Log.d("Error","Error is the "+b.toString().trim());
            Log.d("Error","Error is the "+b.toString().length());
            Log.d("Error","Error is the "+base64(b.toString()));

            showDialog(b.toString().trim());
            /*关闭Dataoutputstream*/
            ds.close();
        } catch (Exception e) {

            showDialog(""+e);
        }
    }

    private void showDialog(String trim) {
        new AlertDialog.Builder(uploadActivity.this).setTitle("消息提示")
                .setMessage(trim)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    public String base64(String content){
        //对字符串进行Base64编码
        try {
            content = Base64.encodeToString(content.getBytes("utf-8"),Base64.DEFAULT);
            content = URLEncoder.encode(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }
    /**
     * 功能：打开相册
     */
    private void openAlbum() {
        //动态申请write_external_storage这个危险权限
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO); //打开相机
        Log.d("Picture","1");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"你否认了许可",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }

    /**
     * 对封装过的Uri进行解析
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handlerImageOnKitKat(Intent data) {
        Log.d("Picture","2");
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            //如果Uri的authority是media格式的话，还需要进一步解析
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                //通过字符串分割取出后半部分才能得到真正的数字
                String id = docId.split(":")[1]; //解析出数字格式的id
                //重新构建和条件语句
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download.public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri ,则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else  if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();

        }
        dispalyImage(imagePath); //根据图片路径显示图片
    }

    private  void handleImageBeforeKitkat(Intent data){
        Log.d("Picture","3");
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        dispalyImage(imagePath);
    }

    /**
     * 获得图片真实路径
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri, String selection) {
        Log.d("Picture","得到照片");
        String path = null;
        //通过Uri 和 selection获得真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    /**
     * 函数名称：dispalyImage
     * 返回值类型：无参
     * 传入的参数：imagePath
     * 传出的参数：无
     * 功能：将图片显示到街面上
     * @param imagePath
     */
    private void dispalyImage(String imagePath) {
        Log.d("Picture","拿到照片了吗？"+imagePath);
        if(imagePath != null){
            Log.d("Picture","拿到照片了吗？");
            Toast.makeText(this,"你成功的打开了图片",Toast.LENGTH_SHORT).show();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            Log.d("Picture","拿到照片了ba？");
            mText1.setText("文件路径:\n"+imagePath);
            uploadFile = imagePath;
            mText1.setTextSize(20);
            mText1.setTextColor(Color.BLUE);

        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

            case TAKEPHOTO:
                if(resultCode == RESULT_OK){
                    Log.d("Picture","到这里了没有1111？");
                    try {
                        //将拍摄照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                Log.d("Picture","到这里了没有？");
                if(resultCode == RESULT_OK){
                    Log.d("Picture","？？？2222");
                    //判断手机系统版本
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        handlerImageOnKitKat(data);
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitkat(data);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }






}