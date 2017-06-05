package org.androidtown.picpicpicpic;

/**
 * Created by 박재성 on 2017-06-05.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

// 커스텀 어댑터 설정(사용자가 지정한 앨범 사진으로 책 만들기)
public class MyAdapter extends BaseAdapter {

    Context context;
    String imgData;
    String geoData;
    ArrayList<String> thumbsDataList;
    ArrayList<String> thumbsIDList;
    int layout;
    int img[];
    LayoutInflater inf;
    String filePath;
    Bitmap[] map;

    /*
    public MyAdapter(Context context, int layout, int[] img, String filePath){
        this.context = context;
        thumbsDataList = new ArrayList<String>();
        thumbsIDList = new ArrayList<String>();
        this.layout = layout;
        this.img = img;
        this.filePath = filePath;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    */

    public MyAdapter(Context context) {
        this.context = context;
        thumbsDataList = new ArrayList<String>();
        thumbsIDList = new ArrayList<String>();
        getThumbInfo(thumbsIDList, thumbsDataList);
    }

    /*
    public final void callImageViewer(int selectedIndex) {

        Intent i = new Intent(context, MainActivity.class);
        String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
        i.putExtra("filename", imgPath);
        startActivityForResult(i, 2);

    }
    */

    public int getCount() {
        return thumbsIDList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View contentView, ViewGroup parent) {

        /*
        if(contentView==null)
            contentView = inf.inflate(layout, null);
        ImageView iv = (ImageView) contentView.findViewById(R.id.bookImg);

        File file = new File(filePath);
        String str;
        int num=0;

        int imgCount = file.listFiles().length; // 파일 총 갯수 얻어오기
        map = new Bitmap[imgCount];

        if(file.listFiles().length>0){
            for(File f : file.listFiles()){
                str = f.getName(); // 파일 이름 얻어오기
                map[num] = BitmapFactory.decodeFile(filePath+"/"+str);
                iv.setImageBitmap(map[num]);
                num++;
            }
        }

        //iv.setImageResource(img[position]);

        return contentView;
     */

        ImageView imageView;

        if (contentView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) contentView;
        }

        BitmapFactory.Options bo = new BitmapFactory.Options();
        bo.inSampleSize = 8;
        Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
        Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true);
        imageView.setImageBitmap(resized);

        return imageView;

    }

    private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas) {

        String[] proj = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};

        Cursor imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj, null, null, null);

        if (imageCursor != null && imageCursor.moveToFirst()) {
            String title;
            String thumbsID;
            String thumbsImageID;
            String thumbsData;
            String data;
            String imgSize;

            int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
            int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);

            int num = 0;
            do {
                thumbsID = imageCursor.getString(thumbsIDCol);
                thumbsData = imageCursor.getString(thumbsDataCol);
                thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                imgSize = imageCursor.getString(thumbsSizeCol);
                num++;
                if (thumbsImageID != null) {
                    thumbsIDs.add(thumbsID);
                    thumbsDatas.add(thumbsData);
                }
            } while (imageCursor.moveToNext());
        }
        imageCursor.close();
        return;
    }

    private String getImageInfo(String ImageData, String Location, String thumbID) {
        String imageDataPath = null;
        String[] proj = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor imageCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj, "_ID='" + thumbID + "'", null, null);

        if (imageCursor != null && imageCursor.moveToFirst()) {
            if (imageCursor.getCount() > 0) {
                int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                imageDataPath = imageCursor.getString(imgData);
            }
        }
        imageCursor.close();
        return imageDataPath;
    }
}