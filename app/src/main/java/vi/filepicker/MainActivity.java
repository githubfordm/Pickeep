package vi.filepicker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerActivity;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.utils.FragmentUtil;
import droidninja.filepicker.DocMainFragment;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;

import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

@RuntimePermissions
public class MainActivity extends AppCompatActivity{

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    DocMainFragment photoFragment;
    private String dir; // 폴더 경로 저장되어있음.
    String title;
    private static final String[] PERMISSION_ONPICKDOC = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};
    ImageButton button;
    EditText getSearch; // 검색어 찾기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSearch = (EditText)findViewById(R.id.ed_search);

        button = (ImageButton)findViewById(R.id.pick_doc);
        Intent temp = getIntent();
        if(temp != null) // 여기서 dir 변수 대입해주기.
        {
            dir = temp.getStringExtra("path");

            if(PermissionUtils.hasSelfPermissions(this,PERMISSION_ONPICKDOC))  // 권한 허락을 한 경우
            {
                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                title = getTitleFromPath(dir);
                getSupportActionBar().setTitle(title);
                PickerManager.getInstance().setModeType(FilePickerConst.READ_MODE);
                openSpecificFragment();
            }
            else    // 권한 허락을 안한 경우
               finish();
        }


        // 검색창 실행시 리스너 실행
        getSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // input 창에 문자 입력할때마다 호출
                String text = getSearch.getText().toString();
               // searchText(text);
            }
        });

    }

    public void pickDocClicked(View view) {
        if(PickerManager.getInstance().getModeType() == FilePickerConst.READ_MODE) {// 읽기 모드였을 때 버튼을 누르면 권한 체크 후, 다음 Activty로 intent 보냄.
            Toast.makeText(this,"읽기 모드에서는 지원되지 않는 기능입니다.",Toast.LENGTH_LONG).show();
        }
        else if(PickerManager.getInstance().getModeType() == FilePickerConst.DELETE_MODE)   // 지우기 모드였을 때, 버튼을 누르면 현재 선택한 파일 리스트 삭제한 후 모드를 자동으로 변경함.
        {
            PickerManager.getInstance().delete_remove_all(getApplicationContext());    // 삭제하려고 선택한 파일 리스트들 전부 삭제해버려!
            PickerManager.getInstance().setModeType(FilePickerConst.READ_MODE);  // 삭제한 다음에 자동으로 Default Mode인 읽기 모드로 전환하고
            button.setImageResource(R.drawable.read);   // 버튼의 이미지도 그에 맞게 변경.
            if(photoFragment != null)   // re-query
            {
                photoFragment.onDestroy();
                openSpecificFragment();
            }
        }
        else if(PickerManager.getInstance().getModeType() == FilePickerConst.SELECT_MODE)  // 무쓸모. 만약의 경우를 대비.
        {
            onPickDoc();
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra("path",dir);

        PickerManager.getInstance().setModeType(FilePickerConst.SELECT_MODE);  // 다음 Activity로 넘기기 전에 Mode 변환해주고
        PickerManager.getInstance().getRemoveDocFiles().clear();  // 삭제할 파일 리스트는 물건너갔으니까 깨끗하게 청소하고 intent보내기.
        startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  // 여기서 받은 path를 폴더에 저장하면 된다. FilePickerActivity의 returnData() 참조할 것
        super.onActivityResult(requestCode, resultCode, data);  // 좆같네 ㅅㅂ 2
        PickerManager.getInstance().setModeType(FilePickerConst.READ_MODE);   // file path 리스트 보내기전에 모드를 Read Mode(Default)로 바꿔주고
        button.setImageResource(R.drawable.read);
        if(requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == RESULT_OK)
        {
            //button.setImageResource(R.drawable.read);
            int send_mode = data.getIntExtra("MODE",1);
            String msg = "파일을 옮기는데 에러가 발생했습니다.";
            try {
                ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS); // InputFile
                int count = 0;
                if (send_mode == FilePickerConst.MOVE_FILE)
                {
                    msg = "파일이 이동되었습니다.";
                    for (int i = 0; i < paths.size(); i++) {
                        boolean result = moveFile(paths.get(i), dir + getTitleFromPath(paths.get(i)));  // 원본 파일 경로 -> 현재 파일 경로 + 원본 파일 이름
                        if (result == true)
                            count += 1;
                    }
                }
                else if (send_mode == FilePickerConst.COPY_FILE)
                {
                    msg = "파일이 복사되었습니다.";
                    for (int i=0; i< paths.size(); i++){
                        boolean result = copyFile(paths.get(i), dir + getTitleFromPath(paths.get(i)));  // 원본 파일 경로 -> 현재 파일 경로 + 원본 파일 이름
                        if (result == true)
                            count += 1;
                    }
                }
                PickerManager.getInstance().getSelectedFiles().clear();  // DocFiles<> clear하기.
                if(photoFragment != null)   // re-query
                {
                    photoFragment.onDestroy();
                    openSpecificFragment();
                }
                Toast.makeText(this, count +"개의 " + msg,Toast.LENGTH_LONG).show();
            } catch (Throwable e) { e.printStackTrace(); }
        }
    }

    public void openSpecificFragment() {
        if(PickerManager.getInstance().isFirst())
            PickerManager.getInstance().addDocTypes();

        photoFragment = DocMainFragment.newInstance(dir);      // 모든 각각의 Fragment를 담고있는 DocPickerFragment를 만들어서
        FragmentUtil.addFragment(this, R.id.container, photoFragment);          // 현재 빈 FrameLayout에 덮어씌운다.


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if(i == R.id.Menu_Read) // PickerManger 의 Type을 바꿔줌.
        {
            PickerManager.getInstance().setModeType(FilePickerConst.READ_MODE);
            button.setImageResource(R.drawable.read);
        }
        else if(i == R.id.Menu_Delete)
        {
            PickerManager.getInstance().setModeType(FilePickerConst.DELETE_MODE);
            button.setImageResource(R.drawable.delete);
        }
        else if(i == R.id.Menu_Select)
        {
            PickerManager.getInstance().setModeType(FilePickerConst.SELECT_MODE);
            //button.setImageResource(R.drawable.plus);
            onPickDoc();
        }
        else if(i == R.id.Name)
        {
            if (photoFragment != null)
            {
                Log.d("gege","sort in MainActivity");
                photoFragment.sort("Name");
                Toast.makeText(getApplicationContext(), "정렬이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(i == android.R.id.home)
        {
            PickerManager.getInstance().getRemoveDocFiles().clear();  // 혹시 모르니까.
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getTitleFromPath(String path)
    {
        String[] list = path.split("/");  // storage/emulated/0/KakaoTalkDownload/ -> storage[0] , emulated[1] , 0[2] , KakaoTalDownload[3]
        return list[list.length-1];
    }

    private boolean moveFile(String departure, String dest)
    {
        try
        {
            File origin_file = new File(departure);
            if (!origin_file.exists())
                return false;

            FileInputStream in = new FileInputStream(origin_file);
            FileOutputStream out = new FileOutputStream(new File(dest)); // 현재 폴더 + 파일 이름으로 outStream 구별.
            FileChannel fcin = in.getChannel();
            FileChannel fcout = out.getChannel();

            long size = 0;
            size = fcin.size();
            fcin.transferTo(0, size, fcout);

            fcout.close();
            fcin.close();
            out.close();
            in.close();

            ContentValues newPath = new ContentValues();
            newPath.put(MediaStore.Files.FileColumns.DATA, dest);  // copy 사실 DB에 알려주기
            getContentResolver().update(MediaStore.Files.getContentUri("external"),
                    newPath,
                    MediaStore.Files.FileColumns.DATA + " like ? ",
                    new String[]{departure});
            origin_file.delete();
        } catch (Throwable e) { e.printStackTrace(); }
        return true;
    }

    private boolean copyFile(String departure, String dest)
    {
        try
        {
            File origin_file = new File(departure);
            if (!origin_file.exists())
                return false;

            FileInputStream in = new FileInputStream(origin_file);
            FileOutputStream out = new FileOutputStream(new File(dest)); // 현재 폴더 + 파일 이름으로 outStream 구별.
            FileChannel fcin = in.getChannel();
            FileChannel fcout = out.getChannel();

            long size = 0;
            size = fcin.size();
            fcin.transferTo(0, size, fcout);

            fcout.close();
            fcin.close();
            out.close();
            in.close();

            String[] DOC_PROJECTION = {
                    MediaStore.Files.FileColumns.DATA,
                    MIME_TYPE,
                    SIZE,
                    MediaStore.Files.FileColumns.TITLE
            };
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE+ " and "+MediaStore.Files.FileColumns.DATA+" like ? ";
            Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    DOC_PROJECTION,
                    selection,
                    new String[]{departure+"%"},
                    null);
            Log.d("cursor","cursor made");
            Log.d("cursor","count column = "+cursor.getColumnCount());
            if (cursor != null) {
                Log.d("cursor","cursor is not null");

                //String mtype = cursor.getString(cursor.getColumnIndexOrThrow(MIME_TYPE));
                //Log.d("cursor","mtype good");
                //String file_size = cursor.getString(cursor.getColumnIndexOrThrow(SIZE));
                //Log.d("cursor","size good");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.TITLE, title);
                values.put(MediaStore.Files.FileColumns.DATA, dest);
                //values.put(MIME_TYPE, mtype);
                //values.put(SIZE, file_size);

                getContentResolver().insert(MediaStore.Files.getContentUri("external"),
                        values);
            }

        } catch (Throwable e) { e.printStackTrace(); }
        return true;
    }

}
