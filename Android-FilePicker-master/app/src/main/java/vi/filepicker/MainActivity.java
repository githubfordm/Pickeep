package vi.filepicker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerActivity;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.PickerManagerListener;
import droidninja.filepicker.fragments.DocFragment;
import droidninja.filepicker.fragments.MediaDetailPickerFragment;
import droidninja.filepicker.fragments.MediaFolderPickerFragment;
import droidninja.filepicker.fragments.MediaPickerFragment;
import droidninja.filepicker.utils.FragmentUtil;
import droidninja.filepicker.DocMainFragment;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements
        MediaDetailPickerFragment.PhotoPickerFragmentListener,
        DocFragment.PhotoPickerFragmentListener,
        MediaFolderPickerFragment.PhotoPickerFragmentListener,
        MediaPickerFragment.MediaPickerFragmentListener,
        PickerManagerListener {

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    DocMainFragment photoFragment;
   // File sdcard = Environment.getExternalStorageDirectory();  // 추후 삭제
  //  String keyword = "/KakaoTalkDownload/";  // 추후 삭제
    private String dir; //= sdcard.getAbsolutePath()+keyword;  // storage/emulated/0/KakaoTalkDownload 절대경로가 저장되있을 거야
    String title;
    private static final String[] PERMISSION_ONPICKDOC = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};
    ImageButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (ImageButton)findViewById(R.id.pick_doc);
        Intent temp = getIntent();
        if(temp != null) // 여기서 dir 변수 대입해주기.
        {
            dir = temp.getStringExtra("path");
            if(!PermissionUtils.hasSelfPermissions(this,PERMISSION_ONPICKDOC)) // 권한 체크
                ActivityCompat.requestPermissions(this, PERMISSION_ONPICKDOC, 1);   // 권한 요청 창 띄우기

            if(PermissionUtils.hasSelfPermissions(this,PERMISSION_ONPICKDOC))  // 권한 허락을 한 경우
            {
                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                title = getTitleFromPath(dir);

                Log.i("Main", "title = " + title);
                getSupportActionBar().setTitle(title);
                PickerManager.getInstance().setPickerManagerListener(this);     // 있으나 없으나?
                PickerManager.getInstance().setModeType(FilePickerConst.READ_MODE);
                openSpecificFragment();
            }
            else    // 권한 허락을 안한 경우
               finish();
        }
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
        else if(PickerManager.getInstance().getModeType() == FilePickerConst.SELECT_MODE)  // 무쓸모.
        {
            onPickDoc();
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        Intent intent = new Intent(this, FilePickerActivity.class);
        PickerManager.getInstance().setModeType(FilePickerConst.SELECT_MODE);  // 다음 Activity로 넘기기 전에 Mode 변환해주고
        PickerManager.getInstance().getRemoveDocFiles().clear();  // 삭제할 파일 리스트는 물건너갔으니까 깨끗하게 청소하고 intent보내기.
        startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  // 여기서 받은 path를 폴더에 저장하면 된다. FilePickerActivity의 returnData() 참조할 것
        super.onActivityResult(requestCode, resultCode, data);  // 좆같네 ㅅㅂ 2
        if(requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == RESULT_OK)
        {
            button.setImageResource(R.drawable.read);
            try {
                ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS); // InputFile
                Log.i("Main","paths size() = "+paths.size());
                for(int i=0; i<paths.size(); i++)
                {
                    File add_file = new File(paths.get(i));
                    if(add_file != null && add_file.exists())
                    {
                        FileInputStream in = new FileInputStream(add_file);
                        FileOutputStream out = new FileOutputStream(new File(dir+getTitleFromPath(paths.get(i)))); // 현재 폴더 + 파일 이름으로 outStream 구별.
                        FileChannel fcin = in.getChannel();
                        FileChannel fcout = out.getChannel();
                        Log.i("Main","input file path = "+add_file.getPath());
                        Log.i("Main","output file path = "+dir+getTitleFromPath(paths.get(i)));

                        long size = 0;
                        size = fcin.size();
                        fcin.transferTo(0,size,fcout);

                        fcout.close();
                        fcin.close();
                        out.close();
                        in.close();

                        /*
                        getContentResolver().delete(MediaStore.Files.getContentUri("external"),  // delete 사실 DB에 알려주기
                                MediaStore.Files.FileColumns.DATA+" like ? ",
                                new String[]{paths.get(i)});
                                */

                        ContentValues newPath = new ContentValues();
                        newPath.put(MediaStore.Files.FileColumns.DATA,dir+getTitleFromPath(paths.get(i)));  // copy 사실 DB에 알려주기
                        getContentResolver().update(MediaStore.Files.getContentUri("external"),
                                newPath,
                                MediaStore.Files.FileColumns.DATA+" like ? ",
                                new String[]{paths.get(i)} );
                        add_file.delete();
                        /*
                        ContentValues newValues = new ContentValues();
                        newValues.put(MediaStore.Files.FileColumns.DATA,paths.get(i));
                        newValues.put(MediaStore.Files.FileColumns.TITLE,dir+getTitleFromPath(paths.get(i)));
                        getContentResolver().insert(MediaStore.Files.getContentUri("external"),
                                newValues);
                                */ // 요건 앞으로도 절대 하지 말자 이상해진다 핸드폰...

                    }
                }
                PickerManager.getInstance().getSelectedFiles().clear();  // DocFiles<> clear하기.
                if(photoFragment != null)   // re-query
                {
                    Log.i("aaa","photoFragment != null");
                    photoFragment.onDestroy();
                    openSpecificFragment();
                }
                Toast.makeText(this,"파일이 모두 이동되었습니다.",Toast.LENGTH_LONG).show();
            } catch (Throwable e) { e.printStackTrace(); }
        }
    }

    private void openSpecificFragment() {
        if(PickerManager.getInstance().isFirst())
            PickerManager.getInstance().addDocTypes();

        photoFragment = DocMainFragment.newInstance(dir);      // 모든 각각의 Fragment를 담고있는 DocPickerFragment를 만들어서
        Log.i("Main","phtoFragment made!");
        FragmentUtil.addFragment(this, R.id.container, photoFragment);          // 현재 빈 FrameLayout에 덮어씌운다.
        Log.i("Main","phtoFragment added!");
    }

    @Override
    public void onSingleItemSelected(ArrayList<String> paths) {

    }

    @Override
    public void onItemSelected(int count) {

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
            button.setImageResource(R.drawable.main_plus);
            onPickDoc();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getTitleFromPath(String path)
    {
        String[] list = path.split("/");  // storage/emulated/0/KakaoTalkDownload/ -> storage[0] , emulated[1] , 0[2] , KakaoTalDownload[3]
        return list[list.length-1];
    }

}
