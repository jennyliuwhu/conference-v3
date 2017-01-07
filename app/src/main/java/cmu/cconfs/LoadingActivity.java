package cmu.cconfs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.IOException;
import java.net.Socket;

import cmu.cconfs.model.parseModel.Version;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.data.DataProvider;
import cmu.cconfs.utils.data.RoomProvider;

public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";
    private static final String INTERNET_CONNECTION_URL = "www.baidu.com";

    private boolean success;
    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private boolean debug  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        animatedCircleLoadingView.startIndeterminate();
        startProcessingThread();
    }

    private void startProcessingThread() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    if (!debug) {
                        preProcessing();
                    } else {
                        success = true;
                        LoadingUtils.loadAuthorPaper();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (success) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(LoadingActivity.this);
                    alert.setMessage("Please enable network connection and try again!");
                    alert.setTitle("Error!");

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            LoadingActivity.this.finish();
                        }
                    });

                    alert.show();
                }

            }
        };
        task.execute((Void[]) null);
    }


    private void preProcessing() throws ParseException {
        if (hasNetworkConnection()) {
            if (!isLocalStoreUpToDate()) {
                Log.w(TAG, "Current local parse store version is out of sync");
                loadFromParse();
            }
        } else if (getLocalStoreVersion() == null) {
            Log.w(TAG, "No parse local store available!");
            animatedCircleLoadingView.stopFailure();
            return;
        }

        long t1 = System.currentTimeMillis();
        success = true;
        populateDataProvider();
        populateRoomProvider();
        long t2 = System.currentTimeMillis();
        Log.i(TAG, "Load data completed in " + (t2 - t1) / 1000 + " seconds.");
        animatedCircleLoadingView.stopOk();
    }

    private boolean hasNetworkConnection() {
        Socket socket = null;
        boolean reachable = false;
        try {
            socket = new Socket(INTERNET_CONNECTION_URL, 80);
            reachable = true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return reachable;
    }

    private String getLocalStoreVersion() {
        String local = null;
        ParseQuery<Version> query = Version.getQuery();
        try {
            local = query.fromLocalDatastore().fromPin(Version.PIN_TAG).getFirst().getVersion();
            Log.d(TAG, "Local parse version: " + local);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return local;
    }

    private String getRemoteStoreVersion() {
        String remote = null;
        ParseQuery<Version> query = Version.getQuery();
        try {
            remote = query.getFirst().getVersion();
            Log.d(TAG, "Remote parse version: " + remote);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return remote;
    }

    private boolean isLocalStoreUpToDate() {
        String local = getLocalStoreVersion();
        if (local == null) {
            Log.w(TAG, "No parse local store available!");
            return false;
        }

        String remote = getRemoteStoreVersion();
        return local.equals(remote);
    }


    private void loadFromParse() {
        try {
            LoadingUtils.loadFromParse();
        } catch (ParseException e) {
            animatedCircleLoadingView.stopFailure();
        }
    }

    private void populateDataProvider() {
        CConfsApplication application = new CConfsApplication();
        DataProvider dataProvider = new DataProvider();
        application.setDataProvider(dataProvider);
    }

    private void populateRoomProvider() {
        CConfsApplication application = new CConfsApplication();
        RoomProvider roomProvider = new RoomProvider();
        application.setRoomProvider(roomProvider);
    }


}
