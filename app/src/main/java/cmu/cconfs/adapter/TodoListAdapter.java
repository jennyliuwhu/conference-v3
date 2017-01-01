package cmu.cconfs.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import cmu.cconfs.R;
import cmu.cconfs.model.parseModel.Todo;

/**
 * Created by qiuzhexin on 12/30/16.
 */

public class TodoListAdapter extends ParseQueryAdapter<Todo> {

    public TodoListAdapter(Context context, QueryFactory<Todo> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(Todo todo, View v, ViewGroup parent) {
        ViewHolder viewHolder;

        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_todo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.todoTitle = (TextView) v.findViewById(R.id.todo_title);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        TextView todoTitle = viewHolder.todoTitle;
        todoTitle.setText(todo.getTitle());
        if (todo.isDraft()) {
            todoTitle.setTypeface(null, Typeface.ITALIC);
        } else {
            todoTitle.setTypeface(null, Typeface.NORMAL);
        }
        return v;
    }

    private static class ViewHolder {
        TextView todoTitle;
    }
}
