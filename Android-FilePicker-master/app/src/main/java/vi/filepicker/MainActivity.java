package vi.filepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import droidninja.filepicker.FilePickerActivity;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
    }

    public void pickDocClicked(View view) {
        MainActivityPermissionsDispatcher.onPickDocWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        Intent intent = new Intent(this, FilePickerActivity.class);
        startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  // 여기서 받은 path를 폴더에 저장하면 된다. FilePickerActivity의 returnData() 참조할 것
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FilePickerConst.REQUEST_CODE_DOC)
        {
            ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
            editText.setText(""+paths.size());
            for(int i=0; i<paths.size(); i++)
            {
                //editText.setText("i = "+i);
                editText.append(paths.get(i)+"\n");
            }
        }
    }
}
