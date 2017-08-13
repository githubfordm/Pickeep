package droidninja.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import droidninja.filepicker.models.FileType;

/**
 * Created by droidNinja on 29/07/16.
 */
public class FilePickerBuilder {

    private final Bundle mPickerOptionsBundle;

    public FilePickerBuilder()
    {
        mPickerOptionsBundle = new Bundle();
    }

    public static FilePickerBuilder getInstance()
    {
        return new FilePickerBuilder();
    }

    public FilePickerBuilder setMaxCount(int maxCount)
    {
        return this;
    }

    public FilePickerBuilder setActivityTheme(int theme)
    {
        PickerManager.getInstance().setTheme(theme);
        return this;
    }

    public FilePickerBuilder setSelectedFiles(ArrayList<String> selectedPhotos)
    {
        mPickerOptionsBundle.putStringArrayList(FilePickerConst.KEY_SELECTED_MEDIA, selectedPhotos);
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE,FilePickerConst.DOC_PICKER);
        return this;
    }

    public void start(Activity context, int pickerType)
    {
        Intent intent = new Intent(context, FilePickerActivity.class);
        context.startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

    public void start(Fragment fragment, int pickerType)
    {
        Intent intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
        fragment.startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

}
