package droidninja.filepicker.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.views.SmoothCheckBox;

/**
 * Created by droidNinja on 29/07/16.
 */
public class FileListAdapter extends SelectableAdapter<FileListAdapter.FileViewHolder, Document>{


    private final Context context;

    public FileListAdapter(Context context, List<Document> items, List<String> selectedPaths) {
        super(items, selectedPaths);
        this.context = context;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_doc_layout, parent, false);

        return new FileViewHolder(itemView);    // 각각의 파일에 어떤 틀의 뷰를 hold할지 결정해서 리턴
    }

    @Override
    public void onBindViewHolder(final FileViewHolder holder, int position) {
        final Document document = getItems().get(position);

        holder.imageView.setImageResource(document.getFileType().getDrawable());
        holder.fileNameTextView.setText(document.getTitle());
        holder.fileSizeTextView.setText(Formatter.formatShortFileSize(context, Long.parseLong(document.getSize())));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked(document,holder);
            }
        });

        //in some cases, it will prevent unwanted situations
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClicked(document,holder);
            }
        });

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.setChecked(isSelected(document));

        holder.itemView.setBackgroundResource(isSelected(document)?R.color.bg_gray:android.R.color.white);
        holder.checkBox.setVisibility(isSelected(document) ? View.VISIBLE : View.GONE);

        holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                toggleSelection(document);
                holder.itemView.setBackgroundResource(isChecked?R.color.bg_gray:android.R.color.white);

            }
        });
    }

    private void onItemClicked(Document document, FileViewHolder holder)  // 아이템 클릭할 때 작동되는 함수!
    {
        if(PickerManager.getInstance().getModeType() == FilePickerConst.DELETE_MODE)
        {
            if (holder.checkBox.isChecked()) {      // 체크된 거 다시 클릭해서 체크 풀 때
                holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
                holder.checkBox.setVisibility(View.GONE);
                PickerManager.getInstance().delete_remove(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT); //removeDocFiles 리스트 목록에서 삭제
            } else {    // 클릭해서 체크됬을 때
                holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
                holder.checkBox.setVisibility(View.VISIBLE);
                PickerManager.getInstance().delete_add(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT);  //removeDocFiles 리스트 목록에 추가
            }
        }
        else if(PickerManager.getInstance().getModeType() == FilePickerConst.SELECT_MODE)
        {
            if (holder.checkBox.isChecked()) {      // 체크된 거 다시 클릭해서 체크 풀 때
                holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
                holder.checkBox.setVisibility(View.GONE);
                PickerManager.getInstance().remove(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT); //removeDocFiles 리스트 목록에서 삭제
            } else {    // 클릭해서 체크됬을 때
                holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
                holder.checkBox.setVisibility(View.VISIBLE);
                PickerManager.getInstance().add(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT);  //removeDocFiles 리스트 목록에 추가
            }
        }
        else if(PickerManager.getInstance().getModeType() == FilePickerConst.READ_MODE)
        {
            Uri path = Uri.parse("content://"+document.getPath());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            String mimeType = myMime.getMimeTypeFromExtension(document.getFileType().extensions[0]);
            intent.setDataAndType(path,mimeType);

            Intent chooser = Intent.createChooser(intent,"Choose best application");
            if(intent.resolveActivity(context.getPackageManager()) != null)
            {
                context.startActivity(chooser);
            }
        }
    }

    private void cleanAll()
    {
        int size = getItems().size();
        for(int i=0; i<size; i++)
        {
            Document document = getItems().get(i);

        }
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        SmoothCheckBox checkBox;

        ImageView imageView;

        TextView fileNameTextView;

        TextView fileSizeTextView;

        public FileViewHolder(View itemView) {
            super(itemView);
            checkBox = (SmoothCheckBox) itemView.findViewById(R.id.checkbox);
            imageView = (ImageView) itemView.findViewById(R.id.file_iv);
            fileNameTextView = (TextView) itemView.findViewById(R.id.file_name_tv);
            fileSizeTextView = (TextView) itemView.findViewById(R.id.file_size_tv);
        }
    }
}
