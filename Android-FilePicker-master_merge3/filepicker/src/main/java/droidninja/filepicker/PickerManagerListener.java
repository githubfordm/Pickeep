package droidninja.filepicker;

import java.util.ArrayList;

public interface PickerManagerListener{
        void onItemSelected(int count);
        void onSingleItemSelected(ArrayList<String> paths);
    }