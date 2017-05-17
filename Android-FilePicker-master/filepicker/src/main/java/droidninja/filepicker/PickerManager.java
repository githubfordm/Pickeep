package droidninja.filepicker;

import java.util.ArrayList;


import droidninja.filepicker.models.BaseFile;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.utils.Utils;


public class PickerManager {
    private static PickerManager ourInstance = new PickerManager();
    private int currentCount;
    private PickerManagerListener pickerManagerListener;
    private ArrayList<String> alreadySelectedFiles;

    public static PickerManager getInstance() {
        return ourInstance;
    }

    private ArrayList<String> mediaFiles;
    private ArrayList<String> docFiles;

    private ArrayList<FileType> fileTypes;

    private int theme = R.style.LibAppTheme;

    private boolean docSupport = true;

    private boolean enableOrientation = false;


    private String providerAuthorities;

    private PickerManager() {
        mediaFiles = new ArrayList<>();
        docFiles = new ArrayList<>();
        fileTypes = new ArrayList<>();
    }


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

    public ArrayList<String> getSelectedPhotos() {
        return mediaFiles;
    }

    public ArrayList<String> getSelectedFiles() {
        return docFiles;
    }

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
