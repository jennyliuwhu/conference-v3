package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.parse.ParseException;

import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.PreferencesManager;


public class AboutActivity extends AppCompatActivity {

    private WebView webView;
    public static PreferencesManager mPreferencesManager;

    // set up drawer
    private static final String TAG = AboutActivity.class.getName();
    public final static int REQUEST_SIGN_IN = 1;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mPreferencesManager = new PreferencesManager(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle("About");

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);

        String link = getIntent().getExtras().getString("link");
        if (!link.equals("about")) {
            actionBar.setTitle(getIntent().getExtras().getString("name"));
            webView.loadUrl(link);
        } else {
            webView.loadDataWithBaseURL(null, "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<body>\n" +
                    "<div class=\"article\">\n" +
                    "\n" +
                    "    <h2 style=\"color: rgb(255, 0, 0);\"><strong>About IEEE ICWS 2015</strong><br>\n" +
                    "        <br>\n" +
                    "    </h2>\n" +
                    "    <p>IEEE International Conference on Web Services (ICWS) has been\n" +
                    "        a prime international forum for both researchers and industry\n" +
                    "        practitioners to exchange the latest fundamental advances in the\n" +
                    "        state of the art and practice of <b>Web-based services</b>,\n" +
                    "        identify emerging research topics, and define the future of\n" +
                    "        Web-based services. All topics regarding Web-based services\n" +
                    "        lifecycle study and management <i></i>align with the theme of\n" +
                    "        ICWS. In 2015, we will celebrate our 22nd version of gathering, to\n" +
                    "        strive to advance the largest international professional forum on\n" +
                    "        Web-based services.<br><br></p>\n" +
                    "    <p>ICWS is under the umbrella of IEEE 2015 World Congress on Services (<a target=\"_blank\" href=\"http://www.servicescongress.org/2015\">SERVICES\n" +
                    "        2015</a>) that is the biggest international forum, formally\n" +
                    "        promoted by IEEE Computer Society since 2003, to explore the\n" +
                    "        science and technology of Services Computing. ICWS 2015 will co-locate with the following\n" +
                    "        service-oriented sister conferences: the 8th IEEE International Conference on Cloud Computing (<a target=\"_blank\" href=\"http://www.thecloudcomputing.org/2015\">CLOUD\n" +
                    "            2015</a>), the 12th IEEE International Conference on\n" +
                    "        Services Computing (<a target=\"_blank\" href=\"http://conferences.computer.org/scc/2015\">SCC\n" +
                    "            2015</a>), the 4th International Congress on Big\n" +
                    "        Data (<a target=\"_blank\" href=\"http://www.ieeebigdata.org/2015\">BigData Congress 2015</a>), and the 4th IEEE International Conference on Mobile\n" +
                    "        Services (<a target=\"_blank\" href=\"http://conferences.computer.org/ms/2015\">MS\n" +
                    "            2015</a>). The five co-located theme topic conferences will all\n" +
                    "        center around \"<b>services</b>,\" while each focusing on exploring\n" +
                    "        different themes (web-based services, cloud-based services,\n" +
                    "        Big Data-based services, services innovation lifecycle, and mobile services).<br><br></p>\n" +
                    "    <p style=\"text-align: left;\">From technology foundation perspective,\n" +
                    "        Services Computing has become the default discipline in the modern\n" +
                    "        services industry. As a major implementation technology for\n" +
                    "        modernizing services industry, Web services are Internet-based\n" +
                    "        programmable application components published using standard\n" +
                    "        interface description languages and universally available via\n" +
                    "        uniform communication protocols. In its 22nd version, the program\n" +
                    "        of ICWS 2015 will continue to feature research papers with a wide\n" +
                    "        range of topics, focusing on various aspects of <b>web-based services</b>.\n" +
                    "        Some of the topics include Web services discovery and composition, Web services specifications and\n" +
                    "        enhancements, Web services QoS (e.g., security, performance, reliability, fault tolerance,\n" +
                    "        etc.), Web services standards and formalizations, Web services\n" +
                    "        modeling, Web services engineering, Web services testing, Web\n" +
                    "        services-based applications and solutions, Web services\n" +
                    "        realizations, semantics in Web services, Web services supporting\n" +
                    "        Cloud Computing, Web services lifecycle management, and all\n" +
                    "        aspects of Service-Oriented Architecture (SOA) infrastructure and\n" +
                    "        middleware.<br><br>\n" +
                    "\n" +
                    "    </p><p>Extended versions of selected research track papers\n" +
                    "    will be invited for special issues in the <a target=\"_blank\" href=\"http://www.computer.org/tsc/\">IEEE Transactions on\n" +
                    "        Services Computing (TSC)</a>, <a target=\"_blank\" href=\"http://hipore.com/ijsc/\">International Journal of Services Computing</a>, <a target=\"_blank\" href=\"http://www.igi-global.com/journal/international-journal-web-services-research/1079\">International\n" +
                    "        Journal of Web Services Research (JWSR)</a>, and <a target=\"_blank\" href=\"http://www.inderscience.com/jhome.php?jcode=IJBPIM\">International\n" +
                    "        Journal of Business Process Integration and Management (IJBPIM)</a>.\n" +
                    "    <strong>Both TSC and JWSR are indexed by SCI and EI <a target=\"_blank\" href=\"http://science.thomsonreuters.com/cgi-bin/jrnlst/jlresults.cgi?PC=D&amp;Word=service*\">[Link]</a>.\n" +
                    "        ICWS Proceedings are EI indexed. To date, ICWS has become the most prestigious international conference in the field. The acceptance rates\n" +
                    "        of its Research Track are listed as follows:</strong></p><strong><br>\n" +
                    "    <p style=\"text-align: left;\"><span style=\"color: rgb(255, 0, 0); font-weight: bold;\">\n" +
                    "\t\t\t\t2015 ICWS Acceptance Rate: 17.4%</span><br>\n" +
                    "        2014 ICWS Acceptance Rate: 18%<br>\n" +
                    "        2013 ICWS Acceptance Rate: 19%<br>\n" +
                    "        2012 ICWS Acceptance Rate: 17%<br>\n" +
                    "        2011 ICWS Acceptance Rate: 14%<br>\n" +
                    "        2010 ICWS Acceptance Rate: 17.6%<br>\n" +
                    "        2009 ICWS Acceptance Rate: 15.6%<br>\n" +
                    "        2008 ICWS Acceptance Rate: 16%<br>\n" +
                    "        2007 ICWS Acceptance Rate: 18%<br>\n" +
                    "        2006 ICWS Acceptance Rate: 16%<br>\n" +
                    "        2005 ICWS Acceptance Rate: 18.9%<br>\n" +
                    "        2004 ICWS Acceptance Rate: 28.7%<br>\n" +
                    "        2003 ICWS Acceptance Rate: 29.8%<br>\n" +
                    "    </p>\n" +
                    "    <br>\n" +
                    "    <p>The long-term goal of ICWS is to build up the most reputable\n" +
                    "        conference for the international community on Web-based services.\n" +
                    "        It is very clear that the ICWS belongs to everyone. </p><br>\n" +
                    "    <p><b>IEEE World Congress on Services</b>\n" +
                    "\n" +
                    "    </p><p>Sponsored by IEEE Computer Society's Technical Committee on\n" +
                    "        Services Computing (<a target=\"_blank\" href=\"http://tab.computer.org/tcsc\">TC-SVC</a>),\n" +
                    "        2015 IEEE World Congress on Services (<a href=\"http://www.servicescongress.org/2015\">SERVICES\n" +
                    "            2015</a>) aims to serve as a federation to host the following\n" +
                    "        theme topic conferences to explore the deep knowledge space\n" +
                    "        of <span style=\"font-weight: bold;\">Services Computing </span>in\n" +
                    "        various directions.</p><br>\n" +
                    "\n" +
                    "    <ul>\n" +
                    "        <li>\n" +
                    "            <a href=\"http://conferences.computer.org/icws/2015\" target=\"_blank\">The\n" +
                    "                IEEE 22nd International Conference on Web Services\n" +
                    "                (ICWS 2015)</a> is the <span style=\"font-weight: bold;\">flagship\n" +
                    "                    theme-topic conference </span>for <span style=\"font-weight: bold;\">Web-based\n" +
                    "                    services</span>, featuring <span style=\"font-weight: bold;\">Web\n" +
                    "                    services</span> modeling, development, publishing,\n" +
                    "            discovery, composition, testing, adaptation, delivery, as well as standards.<br><br><p></p>\n" +
                    "        </li>\n" +
                    "        <li>\n" +
                    "            <a target=\"_blank\" href=\"http://www.thecloudcomputing.org/2015\">The\n" +
                    "                IEEE 8th International Conference on Cloud Computing\n" +
                    "                (CLOUD 2015) </a>is the <span style=\"font-weight: bold;\">flagship\n" +
                    "                    theme-topic conference </span>for<span style=\"font-weight: bold;\">\n" +
                    "                  </span>modeling, developing, publishing, monitoring, managing,\n" +
                    "            delivering <span style=\"font-weight: bold;\">XaaS</span>\n" +
                    "            (everything as a service) in the context of various types of\n" +
                    "            cloud environments.<br><br>\n" +
                    "        </li>\n" +
                    "        <li>\n" +
                    "            <p><a target=\"_blank\" href=\"http://conferences.computer.org/scc/2015\">The\n" +
                    "                IEEE 12th International Conference on Services\n" +
                    "                Computing (SCC 2015)</a> is the <span style=\"font-weight: bold;\">flagship\n" +
                    "                    theme-topic conference </span>for <span style=\"font-weight: bold;\">services\n" +
                    "                    innovation lifecycle </span>that includes enterprise\n" +
                    "                modeling, business consulting, solution creation, services\n" +
                    "                orchestration, services optimization, services management,\n" +
                    "                services marketing, business process integration and\n" +
                    "                management.<br><br>\n" +
                    "            </p></li>\n" +
                    "        <li>\n" +
                    "            <p><a target=\"_blank\" href=\"http://www.themobileservices.org/2015/\">The\n" +
                    "                IEEE 4th International Conference on Mobile Services\n" +
                    "                (MS 2015)</a> is the <span style=\"font-weight: bold;\">emerging\n" +
                    "                    theme-topic conference </span>for the development,\n" +
                    "                publication, discovery, orchestration, invocation, testing,\n" +
                    "                delivery, and certification of <span style=\"font-weight: bold;\">mobile</span>\n" +
                    "                applications and services.<br><br>\n" +
                    "            </p></li>\n" +
                    "    </ul>\n" +
                    "\n" +
                    "    <p>SERVICES 2015 will also co-locate with <a target=\"_blank\" href=\"http://www.ieeese.org/2015\">The IEEE 4th\n" +
                    "        International Congress on Big Data (BigData Congress 2015)</a>,\n" +
                    "        which is the <span style=\"font-weight: bold;\">emerging theme-topic\n" +
                    "                    conference </span>for quantitative analysis of impact on\n" +
                    "        business insights from Big\n" +
                    "        Data analytics.<br></p><br>\n" +
                    "\n" +
                    "    <p><b>About IEEE</b>\n" +
                    "    </p><p><a target=\"_blank\" href=\"http://www.ieee.org/\">IEEE</a> is the\n" +
                    "        world's largest professional association advancing innovation and\n" +
                    "        technological excellence for the benefit of humanity. IEEE and its\n" +
                    "        members inspire a global community to innovate for a better\n" +
                    "        tomorrow through its highly cited publications, conferences,\n" +
                    "        technology standards, and professional and educational activities.\n" +
                    "        IEEE is the trusted \"voice\" for engineering, computing and\n" +
                    "        technology information around the globe.</p><br>\n" +
                    "    <p style=\"font-weight: bold;\">About IEEE Computer Society</p>\n" +
                    "    <p>With nearly 85,000 members, the <a target=\"_blank\" href=\"http://www.computer.org/\">IEEE\n" +
                    "        Computer Society</a> (CS) is the world's leading organization of\n" +
                    "        computing professionals. Founded in 1946, and the largest of the\n" +
                    "        38 societies of the Institute of Electrical and Electronics\n" +
                    "        Engineers (IEEE), the CS is dedicated to advancing the theory and\n" +
                    "        application of computer and information-processing technology.</p><br>\n" +
                    "    <p style=\"font-weight: bold;\">About the Technical Committee on\n" +
                    "        Services Computing</p>\n" +
                    "    <p>Founded in 2003, IEEE Computer Society's Technical Committee on\n" +
                    "        Services Computing (<a target=\"_blank\" href=\"http://tab.computer.org/tcsc\">TC-SVC</a>)\n" +
                    "        is a multidisciplinary group whose purpose is to advance and\n" +
                    "        coordinate work in the field of Services Computing carried out\n" +
                    "        throughout the IEEE in scientific, engineering, standard, literary\n" +
                    "        and educational areas</p>\n" +
                    "    <p>Services Computing has become a cross-discipline that covers the\n" +
                    "        science and technology of bridging the gap between Business\n" +
                    "        Services and IT Services. The underneath breaking technology suite\n" +
                    "        includes Web services and service-oriented architecture (SOA),\n" +
                    "        cloud computing, business consulting methodology and utilities,\n" +
                    "        business process modeling, transformation and integration. This\n" +
                    "        scope of Services Computing covers the whole lifecycle of services\n" +
                    "        innovation research that includes business componentization,\n" +
                    "        services modeling, services creation, services realization,\n" +
                    "        services annotation, services deployment, services discovery,\n" +
                    "        services composition, services delivery, service-to-service\n" +
                    "        collaboration, services monitoring, services optimization, as well\n" +
                    "        as services management. The goal of Services Computing is to\n" +
                    "        enable IT services and computing technology to perform business\n" +
                    "        services more efficiently and effectively.</p><br>\n" +
                    "    <p><strong>Contact Information</strong><br>\n" +
                    "    </p>\n" +
                    "    <p>If you have any questions or queries on ICWS 2015, please send\n" +
                    "        email to icws DOT ieeecs AT gmail.com.<br>\n" +
                    "    </p>\n" +
                    "    <p>==================================================================</p>\n" +
                    "    <p>Please join us at IEEE Services Computing Community (<a href=\"http://services.oc.ieee.org/\" target=\"_blank\">http://services.oc.ieee.org/</a>). Press the\n" +
                    "        \"Register\" button to apply for a FREE IEEE Web Account. As a\n" +
                    "        member, you will be permitted to login and participate in the\n" +
                    "        community. This invitation allows you to join a community designed\n" +
                    "        to facilitate collaboration among a group while minimizing e-mails\n" +
                    "        to your inbox. As a registered member of the Services Computing\n" +
                    "        Community, you can also access the IEEE Body of Knowledge on\n" +
                    "        Services Computing (<a href=\"http://www.servicescomputing.tv/\" target=\"_blank\">http://www.servicescomputing.tv</a>).\n" +
                    "    </p><br>\n" +
                    "</strong></div>\n" +
                    "</body>\n" +
                    "</html>", "text/html", "utf-8", null);
        }
        // setup drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setupNavigationView(mNavigationView);
    }
    private void setupNavigationView(NavigationView navView) {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Intent i = new Intent();
        switch (menuItem.getItemId()) {
            case R.id.nav_dash_board:
                Toast.makeText(getApplicationContext(), "dash board clicked", Toast.LENGTH_SHORT).show();
                i.setClass(this, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.nav_my_schedule:
                Toast.makeText(getApplicationContext(), "my schedule clicked", Toast.LENGTH_SHORT).show();
                i.setClass(getApplicationContext(), ScheduleActivity.class);
                startActivity(i);
                break;
            case R.id.nav_my_profile:
                Toast.makeText(getApplicationContext(), "my profile clicked", Toast.LENGTH_SHORT).show();
                i = getLoginStatusIntent(ProfileActivity.class, LoginActivity.class);
                startActivityForResult(i, REQUEST_SIGN_IN);
                break;
            case R.id.nav_my_to_do_list:
                Toast.makeText(getApplicationContext(), "my todo list clicked", Toast.LENGTH_SHORT).show();
                i.setClass(this, TodoListActivity.class);
                startActivity(i);
                break;
            case R.id.nav_sync_data:
                syncScheduleData(this);
                break;
            case R.id.nav_log_in:
                i = getLoginStatusIntent(UserActivity.class, LoginActivity.class);
                startActivityForResult(i, REQUEST_SIGN_IN);
                break;
        }

        menuItem.setCheckable(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private Intent getLoginStatusIntent(Class loginTarget, Class notLoginTarget) {
        boolean loggedIn = mPreferencesManager.getBooleanPreference("LoggedIn",false);
        Toast .makeText(this, loggedIn + "", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        if(!loggedIn) {
            i.setClass(getApplicationContext(), notLoginTarget);
        } else {
            i.setClass(getApplicationContext(), loginTarget);
        }
        return i;
    }

    private void syncScheduleData(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Sync Data");
        builder.setMessage("You sure want to reload the backend data?");
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog pd = new ProgressDialog(context);
                String st = "Syncing data...";
                pd.setMessage(st);
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            LoadingUtils.loadFromParse();
                            LoadingUtils.populateDataProvider();
                            LoadingUtils.populateRoomProvider();
                        } catch (ParseException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        pd.dismiss();
                    }
                }.execute();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
