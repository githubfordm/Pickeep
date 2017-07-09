package vi.filepicker;

import org.junit.Test;

import permissions.dispatcher.PermissionUtils;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void book_count_test() throws Exception{
        EnterActivity enterActivity = new EnterActivity();
        int count = enterActivity.getCount();
        assertEquals(1,count);
    }
    @Test
    public void get_title_test() throws Exception{
        EnterActivity enterActivity = new EnterActivity();
        String temp_path = "storage/emulated/0/KakaoTalkDownload/";
        String temp_title = "KakaoTalkDownload";
        assertEquals(temp_title,enterActivity.getTitleFromPath(temp_path));
    }
}