package cmu.cconfs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

import cmu.cconfs.R;

/**
 * Created by qiuzhexin on 1/15/17.
 */

public class AppointmentDetailFragment extends DialogFragment {

    private TextView mTitleTv;
    private TextView mDetailTv;

    public AppointmentDetailFragment() {

    }

    public static AppointmentDetailFragment newInstance(String title, String detail) {
        AppointmentDetailFragment frag = new AppointmentDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("detail", detail);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_card_view, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        view.setMinimumWidth(width / 5 * 4);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTitleTv = (TextView) view.findViewById(R.id.noti_title_tv);
        mDetailTv = (TextView) view.findViewById(R.id.noti_detail_tv);
        TextView typeLabel = (TextView) view.findViewById(R.id.detail_type_label);
        typeLabel.setVisibility(View.GONE);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "");
        String detail = getArguments().getString("detail", "");
        // Show soft keyboard automatically and request focus to field
        mTitleTv.setText(title);
        mDetailTv.setText(detail);
    }
}
