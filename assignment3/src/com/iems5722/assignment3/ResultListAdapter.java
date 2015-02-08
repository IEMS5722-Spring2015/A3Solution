package com.iems5722.assignment3;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ResultListAdapter extends ArrayAdapter<DisplayObject> {
	private static final String TAG = "ListAdapter";
	
	private Context context;
	private List<DisplayObject> values;
	
	public ResultListAdapter(Context context, List<DisplayObject> dataList) {
		super(context, R.layout.list_item, dataList);
		this.context = context;
		this.values = dataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.list_item, parent, false);
		TextView title = (TextView) rowView.findViewById(R.id.title);
		TextView desc = (TextView) rowView.findViewById(R.id.desc);
		ImageView img = (ImageView) rowView.findViewById(R.id.imgView);
		
		title.setText(values.get(position).title);
		desc.setText(values.get(position).desc);
		
		DownloadImageTask imageTask = new DownloadImageTask(img);
		imageTask.execute(values.get(position).url);
		
		return rowView;
	}
}


