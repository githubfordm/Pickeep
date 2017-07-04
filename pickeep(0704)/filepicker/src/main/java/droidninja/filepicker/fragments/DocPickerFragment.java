package droidninja.filepicker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.internal.util.Predicate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.SectionsPagerAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.utils.MediaStoreHelper;
import droidninja.filepicker.utils.TabLayoutHelper;
import droidninja.filepicker.utils.Utils;


public class DocPickerFragment extends BaseFragment {

    private static final String TAG = DocPickerFragment.class.getSimpleName();
    private static String dir;

    TabLayout tabLayout;

    ViewPager viewPager;
    private ArrayList<String> selectedPaths;
    private ProgressBar progressBar;

    public DocPickerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doc_picker, container, false);
    }

    public static DocPickerFragment newInstance(String folder_path) {
        DocPickerFragment docPickerFragment = new DocPickerFragment();
        dir = folder_path;
        return  docPickerFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        initView();
    }

    private void initView() {
        setUpViewPager();
        setData();
    }

    private void setViews(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void setData() {
        MediaStoreHelper.getDocs(getActivity(), new FileResultCallback<Document>() {
            @Override
            public void onResultCallback(List<Document> files) {
                progressBar.setVisibility(View.GONE);
                setDataOnFragments(files);
            }
        });
    }

    private void setDataOnFragments(List<Document> files) {
        SectionsPagerAdapter sectionsPagerAdapter = (SectionsPagerAdapter) viewPager.getAdapter();
        if(sectionsPagerAdapter!=null)
        {
            for (int index = 0; index < sectionsPagerAdapter.getCount(); index++) {
                DocFragment docFragment = (DocFragment) getChildFragmentManager()
                        .findFragmentByTag(
                                "android:switcher:" + R.id.viewPager + ":"+index);
                Log.i("Doc","size is "+files.size());
                if(docFragment!=null)
                {
                    FileType fileType = docFragment.getFileType();  // 각각의 DocFragment가 어떤 형식 파일을 지원하는지 찾고
                    if(fileType!=null)
                        docFragment.updateList(filterDocuments(fileType.extensions, files));    // 리사이클러뷰만 달랑 존재하던 각각의 DocFragment에 리스트를 업데이트하는 작업을 해준다. (Layout은 이전에 이미 설정해놓았으므로 데이터만 쏘옥 넣으면 됨)
                }
            }
        }
    }

    private void setUpViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        ArrayList<FileType> supportedTypes = PickerManager.getInstance().getFileTypes();
        for (int index = 0; index < supportedTypes.size(); index++) {
            adapter.addFragment(DocFragment.newInstance(supportedTypes.get(index)),supportedTypes.get(index).title); //Fragment 리스트를 adapter에 추가하는 작업이며, 각각의 Fragment는 기존에 설정한 fileType list(pdf,ppt,txt 등등) 를 하나씩
            // 받아와서 해당 파일타입만 지원하는 fileType객체를 만들어 자기자신의 Bundle에 집어넣은 상태며, default view는 리사이클러 뷰랑 정가운데 텍스트 뷰로 이루어져있다.
        }

        viewPager.setOffscreenPageLimit(supportedTypes.size()); // pager라는 중개인이 몇개까지의 fragment 고객을 담당하는지 설정해주고
        viewPager.setAdapter(adapter); // 중개인 pager에 실제 링크루트 정보가 저장된 adapter를 적용시켜 완벽한 중개인으로 다시 태어난다. 마치 공인중개사처럼. adapter는 직방 어플리케이션같은 느낌?
        tabLayout.setupWithViewPager(viewPager); // 공간을 제공해주는 tabLayout하고 중개인 pager를 소개시켜준다. 중개인이 가지고 있는 각 고객(fragment)를 위한 공간은 tabLayout이 제공해준다.
        
        TabLayoutHelper mTabLayoutHelper = new TabLayoutHelper(tabLayout, viewPager); // 내가 이해한 부분으로는 Pager에서 드래그했을 때 적합한 Tab 타이틀이 선택되게 하거나 Tab을 클릭했을 때 적합ㅎ나 page가 보여지도록 하거나 이런 잡다한 내부적 처리를 담당하는 놈 같음
        mTabLayoutHelper.setAutoAdjustTabModeEnabled(true);
    }

    private ArrayList<Document> filterDocuments(final String[] type, List<Document> documents)
    {
        final Predicate<Document> docType = new Predicate<Document>() {
            public boolean apply(Document document) {
                File temp = new File(document.getPath());
                return (document.isThisType(type) && temp.exists() && !document.getPath().contains(dir));       // 일단은 모든 확장자의 집한인 documents에서 현재 pager가 지원하는 확장자 string 리스트중에서 일치하는 부분이 있는지 확인하는 필터링 작업을 진행한다.
            }
        };
        return new ArrayList<>(Utils.filter(new HashSet<>(documents),docType));
    }
}
