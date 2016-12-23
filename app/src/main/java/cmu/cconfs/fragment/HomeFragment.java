package cmu.cconfs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dk.view.folder.ResideMenu;
import com.squareup.picasso.Picasso;

import cmu.cconfs.MainActivity;
import cmu.cconfs.R;

/**
 * Created by zmhbh on 7/24/15.
 */
public class HomeFragment extends Fragment {

    private View parentView;
    private ResideMenu resideMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_main, container, false);
        setUpViews();


        return parentView;
    }

    private void setUpViews() {
        MainActivity parentActivity = (MainActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();

        parentView.findViewById(R.id.btn_open_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        // add gesture operation's ignored views
        //  FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        //  resideMenu.addIgnoredView(ignored_view);
    }

}