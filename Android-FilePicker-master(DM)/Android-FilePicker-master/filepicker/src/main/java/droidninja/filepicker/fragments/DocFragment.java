package droidninja.filepicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.FileListAdapter;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;


public class DocFragment extends BaseFragment {

    private static final String TAG = DocFragment.class.getSimpleName();
    private static final String tag = "DocFragment";
    RecyclerView recyclerView;

    TextView emptyView;

    private FileListAdapter fileListAdapter;
    private FileType fileType;

    public DocFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(tag,"In the onCraeteView");
        return inflater.inflate(R.layout.fragment_photo_picker, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(tag,"In the onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(tag,"In the onCreate");
        super.onCreate(savedInstanceState);
    }

    public static DocFragment newInstance(FileType fileType) {
        Log.i(tag,"In the newInstance");
        DocFragment photoPickerFragment = new DocFragment();
        Bundle bun = new Bundle();
        bun.putParcelable(FILE_TYPE, fileType);     // 이유는 잘 모르겠지만, 오브젝트를 넘겨주기 위해서 parceable이 설정된 bundle을 가진 객체를 넘겨주는 듯..?
        photoPickerFragment.setArguments(bun); // 기존에 가지고 있던 Bundle하고 스위치를 했다. FileType 객체가 담긴 Bundle로
        return  photoPickerFragment;
    }

    public FileType getFileType() {
        return getArguments().getParcelable(FILE_TYPE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        fileType =  getArguments().getParcelable(FILE_TYPE); //  같은 패키지(droidninja.filePicker) 내의 다른 parceable하는 객체를 얻어오기위한(맵핑에 의한) 과정

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // Fragment는 기본적으로 생성될 때 자기를 call한 Activity가 누군지 저장해놓는다. 누가 내 부모냐? 너구나~!
        // MainActivity가 부모 클래스가 됨에 따라 getActivity()할 때 형식을 불러오는데, ScrollView로 둘러싸여있어서 여태껏 안됬던 것임. 헐... ㅁㅊ;;
        recyclerView.setVisibility(View.GONE);
    }

    public void updateList(List<Document> dirs) {
        if(getView()==null)
            return;
        if(dirs.size()>0)
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            FileListAdapter fileListAdapter = (FileListAdapter) recyclerView.getAdapter();
            if(fileListAdapter==null) {
                fileListAdapter = new FileListAdapter(getActivity(), dirs, PickerManager.getInstance().getSelectedFiles());

                recyclerView.setAdapter(fileListAdapter);
            }
            else
            {
                fileListAdapter.setData(dirs);
                fileListAdapter.notifyDataSetChanged();
            }
        }
        else        // 만약 현재 pager에서 지원해주는 문서양식에 맞는 문서가 아예 존재하지않으면 문서 없다는 view가 보여지도록 설정
        {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

}
