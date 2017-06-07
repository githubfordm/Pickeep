package droidninja.filepicker.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * Created by hoon on 2017-06-04.
 */

public class DMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private String mFilepath = null;
    private MediaScannerConnection mScanner;

    public DMediaScanner(Context context) {
        mScanner = new MediaScannerConnection(context, this);
    }

    public void startScan(String filepath) {
        mFilepath = filepath;
        Log.i("Manager","startScan() start!");
        mScanner.connect(); // 이 함수 호출 후 onMediaScannerConnected가 호출 됨.
        Log.i("Manager","connect() Out!");
    }

    @Override
    public void onMediaScannerConnected() {
        if(mFilepath != null) {
            Log.i("Manager","connected!!!");
            String filepath = new String(mFilepath);
            mScanner.scanFile(filepath, null); // MediaStore의 정보를 업데이트
            Log.i("Manager",filepath+"스캔 완료!");
        }

        mFilepath = null;
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        Log.i("Manager","Before disconnect()");
        mScanner.disconnect(); // onMediaScannerConnected 수행이 끝난 후 연결 해제
        Log.i("Manager","After disconnect()");
    }

    public void disConnect()
    {
        mScanner.disconnect();
    }
}
