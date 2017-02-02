package cmu.cconfs;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.adapter.SponsorListAdapter;
import cmu.cconfs.model.parseModel.Sponsor;


public class SponsorActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SponsorListAdapter adapter;

    List<Sponsor> sponsors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sponsor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.menu_paper);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Sponsor");

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new SponsorListAdapter(this.sponsors);
        recyclerView.setAdapter(adapter);

        populate();

    }

    private void populate() {
        ParseQuery<Sponsor> query = Sponsor.getQuery();
        query.fromLocalDatastore();
        query.fromPin(Sponsor.PIN_TAG);
        List<Sponsor> sponsorList = null;
        try {
            sponsorList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Sponsor sponsor : sponsorList) {
            sponsors.add(sponsor);
        }
//        this.sponsors.add(new Sponsor("IEEE Technical", "http://tab.computer.org/tcsc/", "https://lh3.googleusercontent.com/-E7uX3DtJB-k/AAAAAAAAAAI/AAAAAAAAAAA/B_Bao68bswo/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("IEEE Computer Society", "http://www.computer.org/web/guest", "https://lh6.googleusercontent.com/-l7sOVB2N87o/AAAAAAAAAAI/AAAAAAAAAAA/X8RO1wjyeRI/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("ERICSSON", "http://www.ericsson.com", "https://lh5.googleusercontent.com/-LxR6S5MMeW0/AAAAAAAAAAI/AAAAAAAAAAA/ywVsXJf0dfo/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("HP", "http://m.hp.com/us/en/home.html", "https://lh4.googleusercontent.com/-CKNnBHvWTN0/AAAAAAAAAAI/AAAAAAAAAAA/1yqw2lShG5o/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("IBM", "http://m.ibm.com/us/en", "https://lh3.googleusercontent.com/-ApNLZ2_15_U/AAAAAAAAAAI/AAAAAAAAAAA/12GouyuqIwo/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("SAP", "http://go.sap.com/index.html", "https://lh3.googleusercontent.com/-q2M7Q9v6gno/AAAAAAAAAAI/AAAAAAAAAAA/TguyrMPHUa8/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("HUAWEI", "http://www.huawei.com/en", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Huawei.svg/1000px-Huawei.svg.png"));
//        this.sponsors.add(new Sponsor("OMG", "http://www.omg.org/", "https://upload.wikimedia.org/wikipedia/en/f/f1/OMG-logo.jpg"));
//        this.sponsors.add(new Sponsor("IBM Research", "http://www.research.ibm.com", "https://pbs.twimg.com/profile_images/2453018418/fn1i02hac59i02ccd9c1_400x400.jpeg"));
    }


}
