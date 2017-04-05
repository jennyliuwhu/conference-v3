package cmu.cconfs.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cmu.cconfs.AboutActivity;
import cmu.cconfs.R;
import cmu.cconfs.model.parseModel.Sponsor;


/**
 * Created by zmhbh on 8/21/15.
 */
public class SponsorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Sponsor> items;


    /**
     * References to the views for each data item
     **/
    public class SponsorViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView authorView;
        public SimpleDraweeView imageView;

        private View view;

        public SponsorViewHolder(View v) {
            super(v);
            this.view = v;

            titleView = (TextView) v.findViewById(R.id.name);
            authorView = (TextView) v.findViewById(R.id.link);
            imageView = (SimpleDraweeView) v.findViewById(R.id.weatherimg);
        }

        public View getView() {
            return view;
        }
    }

    /**
     * Constructor
     **/
    public SponsorListAdapter(List<Sponsor> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sponsor_item, parent, false);

        return new SponsorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Sponsor item = items.get(position);
        SponsorViewHolder vh = (SponsorViewHolder) holder;

        vh.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "test: " + item.getLink(),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setClass(v.getContext(), AboutActivity.class);
                intent.putExtra("link", item.getLink());
                intent.putExtra("name", item.getName());
                v.getContext().startActivity(intent);
            }
        });

        vh.titleView.setText(item.getName());
        vh.authorView.setText(item.getLink());
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty())
            vh.imageView.setImageURI(Uri.parse(item.getImageUrl()));

    }

}
