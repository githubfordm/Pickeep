package droidninja.filepicker;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


import droidninja.filepicker.models.BaseFile;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.utils.DMediaScanner;
import droidninja.filepicker.utils.Utils;


public class PickerManager {
    private static PickerManager ourInstance = new PickerManager();
    private int currentCount;
    private int Mode;
    private PickerManagerListener pickerManagerListener;
    private ArrayList<String> alreadySelectedFiles;

    public static PickerManager getInstance() {
        return ourInstance;
    }

    private ArrayList<String> mediaFiles;
    private ArrayList<String> docFiles;
    private ArrayList<String> removeDocFiles;

    private ArrayList<FileType> fileTypes;

    private int theme = R.style.LibAppTheme;

    private boolean docSupport = true;

    private boolean enableOrientation = false;


    private String providerAuthorities;

    private PickerManager() {
        mediaFiles = new ArrayList<>();
        docFiles = new ArrayList<>();
        removeDocFiles = new ArrayList<>();
        fileTypes = new ArrayList<>();
        Mode = FilePickerConst.READ_MODE;
    }

    public boolean isFirst() {
        return fileTypes.isEmpty();
    }

    public int getModeType() { return Mode; }
    public void setModeType(int type) { Mode = type; }

    public void setPickerManagerListener(PickerManagerListener pickerManagerListener) {
        this.pickerManagerListener = pickerManagerListener;
    }

    public void add(String path, int type) {
        if (path != null) {
            if (!mediaFiles.contains(path) && type == FilePickerConst.FILE_TYPE_MEDIA)
                mediaFiles.add(path);
            else if (type == FilePickerConst.FILE_TYPE_DOCUMENT)
                docFiles.add(path);
            else
                return;

            currentCount++;

            if (pickerManagerListener != null) {
                pickerManagerListener.onItemSelected(currentCount);
            }
        }
    }

    public void delete_add(String path, int type)
    {
        if(path != null)
        {
            if(type == FilePickerConst.FILE_TYPE_DOCUMENT)
                removeDocFiles.add(path);
        }
    }

    public void add(ArrayList<String> paths, int type) {
        for (int index = 0; index < paths.size(); index++) {
            add(paths.get(index), type);
        }
    }

    public void remove(String path, int type) {
        if ((type == FilePickerConst.FILE_TYPE_MEDIA) && mediaFiles.contains(path)) {
            mediaFiles.remove(path);
            currentCount--;

        } else if (type == FilePickerConst.FILE_TYPE_DOCUMENT) {
            docFiles.remove(path);

            currentCount--;
        }

        if (pickerManagerListener != null) {
            pickerManagerListener.onItemSelected(currentCount);
        }
    }

    public void delete_remove(String path, int type)
    {
        if(type == FilePickerConst.FILE_TYPE_DOCUMENT)
            removeDocFiles.remove(path);
    }

    public void delete_remove_all(Context context) // ㅅㅂ 좆같네
    {
        int max = removeDocFiles.size(), count = 0;

        for(int c = 0; c<max; c++)  // ArrayList는 for each문으로 접근이 안된다?
        {
            String path = removeDocFiles.get(c);
            Log.i("Manager","path = "+path);
            File file = new File(path); // 한번도 읽지 않은 데이터는 인식을 못함.
            Log.i("Manager","file path = "+file.getPath());
            if(file.exists() && !file.isDirectory()) // 폴더를 가리키는게아니고 파일이 NULL값이 아닌 경우
            {
                boolean result = file.delete();
                context.getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                        MediaStore.Files.FileColumns.DATA+" like ? ",
                        new String[]{path});    // db를 업데이트 할 수 없다면(스캔) 그냥 물리적으로 삭제해버리자!
                Log.i("Manager","Result = "+result);
                Log.i("Manager","file still exists? = "+file.exists());
            }
        }
        removeDocFiles.clear();
        Toast.makeText(context,"파일이 모두 삭제되었습니다.",Toast.LENGTH_LONG).show();
    }

    private static void broadCastToMediaScanner(Context context,Uri contentUri)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    public ArrayList<String> getSelectedPhotos() {
        return mediaFiles;
    }

    public ArrayList<String> getSelectedFiles() {
        return docFiles;
    }

    public ArrayList<String> getRemoveDocFiles() { return removeDocFiles; }

    public ArrayList<String> getSelectedFilePaths(ArrayList<BaseFile> files) {
        ArrayList<String> paths = new ArrayList<>();
        for (int index = 0; index < files.size(); index++) {
            paths.add(files.get(index).getPath());
        }
        return paths;
    }

    public void clearSelections() {
        docFiles.clear();
        mediaFiles.clear();
        fileTypes.clear();
        currentCount = 0;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public void addDocTypes()
    {
        String[] pdfs = {"pdf"};
        fileTypes.add(new FileType(FilePickerConst.PDF,pdfs,R.drawable.ic_pdf));

        String[] docs = {"doc","docx", "dot","dotx"};
        fileTypes.add(new FileType(FilePickerConst.DOC,docs,R.drawable.ic_word));

        String[] hwps = {"hwp"};
        fileTypes.add(new FileType(FilePickerConst.HWP,hwps,R.drawable.ic_hwp));

        String[] ppts = {"ppt","pptx"};
        fileTypes.add(new FileType(FilePickerConst.PPT,ppts,R.drawable.ic_ppt));

        String[] xlss = {"xls","xlsx"};
        fileTypes.add(new FileType(FilePickerConst.XLS,xlss,R.drawable.ic_excel));

        String[] txts = {"txt"};
        fileTypes.add(new FileType(FilePickerConst.TXT,txts,R.drawable.ic_txt));
    }

    public ArrayList<FileType> getFileTypes()
    {
        return fileTypes;
    }

    public boolean isDocSupport() {
        return docSupport;
    }

    public void setDocSupport(boolean docSupport) {
        this.docSupport = docSupport;
    }


    public boolean isEnableOrientation() {
        return enableOrientation;
    }

    public void setEnableOrientation(boolean enableOrientation) {
        this.enableOrientation = enableOrientation;
    }

    public String getProviderAuthorities() {
        return providerAuthorities;
    }

    public void setProviderAuthorities(String providerAuthorities) {
        this.providerAuthorities = providerAuthorities;
    }
}


