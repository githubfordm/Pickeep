package droidninja.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import droidninja.filepicker.fragments.DocFragment;
import droidninja.filepicker.fragments.DocPickerFragment;
import droidninja.filepicker.fragments.MediaFolderPickerFragment;
import droidninja.filepicker.fragments.MediaDetailPickerFragment;
import droidninja.filepicker.fragments.MediaPickerFragment;
import droidninja.filepicker.utils.FragmentUtil;
import droidninja.filepicker.utils.ImageCaptureManager;

public class FilePickerActivity extends AppCompatActivity implements
        MediaDetailPickerFragment.PhotoPickerFragmentListener,
        DocFragment.PhotoPickerFragmentListener,
        MediaFolderPickerFragment.PhotoPickerFragmentListener,
        MediaPickerFragment.MediaPickerFragmentListener,
        PickerManagerListener {

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    private ArrayList<String> docPaths = new ArrayList<>();
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PickerManager.getInstance().getTheme());
        setContentView(R.layout.activity_file_picker);
        Log.i("FilePicker","onCreate()");
        if(!PickerManager.getInstance().isEnableOrientation())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      // 핸드폰 돌려도 화면 세로(portrait)으로 유지되도록 세팅
        initView();
    }

    private void initView() {       // 아무것도 없는 FrameLayout에 view를 초기화하는 과정이다.
        Log.i("FilePicker","initView()");
        Intent intent = getIntent();
        if (intent != null) {
            if(getSupportActionBar()!=null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // ActionBar가 보여지도록 설정안되있으면 보여지도록 세팅
            PickerManager.getInstance().setPickerManagerListener(this);
            openSpecificFragment(); // 실제 구체적인 fragment를 추가해준다.
        }
    }

    private void openSpecificFragment() {
        Log.i("FilePicker","openCpecific...()");
        if(PickerManager.getInstance().isFirst())
            PickerManager.getInstance().addDocTypes();

        DocPickerFragment photoFragment = DocPickerFragment.newInstance();      // 모든 각각의 Fragment를 담고있는 DocPickerFragment를 만들어서
        FragmentUtil.addFragment(this, R.id.container, photoFragment);          // 현재 빈 FrameLayout에 덮어씌운다.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("FilePicker","onCreateOptionMenu()");
        getMenuInflater().inflate(R.menu.picker_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("FilePicker","onOptionsItemSelected()");
        int i = item.getItemId();
        if (i == R.id.action_done) {
            returnData(PickerManager.getInstance().getSelectedFiles());
            return true;
        } else if (i == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int currentCount) {
    }

    @Override
    public void onSingleItemSelected(ArrayList<String> paths) {
        returnData(paths);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        //PickerManager.getInstance().clearSelections();  // 추가
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("FilePicker","onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE_MEDIA_DETAIL:
                if(resultCode== Activity.RESULT_OK)
                {
                    returnData(PickerManager.getInstance().getSelectedFiles());
                }
                break;
        }
    }

    private void returnData(ArrayList<String> paths)
    {
        Log.i("FilePicker","returnData()");
        Intent intent = new Intent();
        intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths);
        //PickerManager.getInstance().clearSelections();
        PickerManager.getInstance().setModeType(FilePickerConst.READ_MODE);   // file path 리스트 보내기전에 모드를 Read Mode(Default)로 바꿔주고
        setResult(RESULT_OK, intent);
        finish();
    }
}
