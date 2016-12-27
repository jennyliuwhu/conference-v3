package cmu.cconfs;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton mProfileImage;
    private AppBarLayout mAppBarLayout;
    private Button mLogoutButton;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mSettingButton;
    private FloatingActionButton mSendNotesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProfileImage = (ImageButton) findViewById(R.id.user_profile_photo);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Profile image clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppBarLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.main_background));
            }
        });

        mLogoutButton = (Button) findViewById(R.id.btn_sign_out);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Log out button clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });


        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mSettingButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_setting);
        mSendNotesButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_email_notes);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "setting clicked");
            }
        });

        mSendNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "send notes clicked");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void snackNotify(View v, String s) {
        Snackbar.make(v, s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
