package cmu.cconfs;

/**
 * Created by qiuzhexin on 12/13/16.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Query;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmu.cconfs.instantMessage.activities.AlertDialog;
import cmu.cconfs.model.parseModel.Paper;
import cmu.cconfs.model.parseModel.Program;
import cmu.cconfs.model.parseModel.Room;
import cmu.cconfs.model.parseModel.Session_Room;
import cmu.cconfs.model.parseModel.Session_Timeslot;
import cmu.cconfs.model.parseModel.Timeslot;
import cmu.cconfs.model.parseModel.TransferJob;
import cmu.cconfs.model.parseModel.Version;
import cmu.cconfs.utils.csv.CSVAbstractEntry;
import cmu.cconfs.utils.csv.CSVUtils;
import cmu.cconfs.utils.csv.PaperCSVEntry;
import cmu.cconfs.utils.csv.SessionCSVEntry;

/**
 * Android Drive Quickstart activity. This activity takes a photo and saves it
 * in Google Drive. The user is prompted with a pre-made dialog which allows
 * them to choose the file location.
 */
public class TransferActivity extends BaseActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "drive-quickstart";
    private static final String FILE_TYPE_EXTRA = "export-file-type";

    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_OPEN_FILE_LIST_1 = 2;
    private static final int REQUEST_CODE_OPEN_FILE_LIST_2 = 3;
    private static final int REQUEST_CODE_CREATE_CSV_1 = 4;
    private static final int REQUEST_CODE_CREATE_CSV_2 = 5;

    private GoogleApiClient mGoogleApiClient;

    private DriveId mPaperCSVFileDriveId = null;
    private DriveId mSessionCSVFileDriveId = null;

    private TextView mPaperFileName;
    private TextView mSessionFileName;

    private IndeterminateProgressButton mExpPaperBtn;
    private IndeterminateProgressButton mExpSessionBtn;
    private IndeterminateProgressButton mImpBtn;

    private static final int MORPH_BUTTON_SUC = 0;
    private static final int MORPH_BUTTON_INIT = 1;

    // show a list of files in google drive
    private void displayDriveFiles(int requestCode) {
        if (!mGoogleApiClient.isConnected()) { // restart the activity if account not connected
            Log.e(TAG, "Google API client not connected!");
            finish();
            startActivity(getIntent());
            return;
        }
        Log.i(TAG, "Open a file in google drive");
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"text/plain", "text/html", "text/csv"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(intentSender, requestCode, null, 0, 0, 0);
        } catch (SendIntentException e) {
            Log.w(TAG, "Unable to view google drive content");
        }
    }

    /**
     * EXPORT SECTION
     */
    // export data from parse and create paper.csv
    private void exportPaperDataFromParse() {
        Log.i(TAG, "Export paper data start...");
        ParseQuery<Paper> query = ParseQuery.getQuery(Paper.class);
        query.setLimit(1000);
        try {
            List<Paper> papers = query.find();
            List<PaperCSVEntry> paperCSVEntries = new ArrayList<PaperCSVEntry>();
            for (Paper p : papers) {
                PaperCSVEntry entry = new PaperCSVEntry();
                entry.map(p);
                paperCSVEntries.add(entry);
            }
            Log.i(TAG, "load " + paperCSVEntries.size() + " paper entries");
            saveCSVToDrive(paperCSVEntries, REQUEST_CODE_CREATE_CSV_1);
        } catch (ParseException e) {
            Log.w(TAG, "Error: " + e.getMessage());
        }
    }

    // export session data from parse
    private void exportSessionDataFromParse() {
        Log.i(TAG, "Export session data start...");
        ParseQuery<Session_Room> query1 = ParseQuery.getQuery(Session_Room.class).setLimit(1000);
        ParseQuery<Room> query2 = ParseQuery.getQuery(Room.class).setLimit(1000);
        ParseQuery<Program> query3 = ParseQuery.getQuery(Program.class).setLimit(1000);

        try {
            List<Session_Room> sessionRooms = query1.find();
            Log.i(TAG, "retrieve session_rooms: " + sessionRooms.size());
            List<Room> rooms = query2.find();
            Log.i(TAG, "retrieve rooms: " + rooms.size());
            List<Program> programs = query3.find();
            Log.i(TAG, "retrieve programs: " + programs.size());

            Map<Integer, String> roomIdToRoomName = new HashMap<>();
            Map<Integer, String> programIdToDate = new HashMap<>();
            Map<Integer, Integer> roomIdToProgramId = new HashMap<>();

            // load maps for relations
            for (Room r : rooms) {
                roomIdToRoomName.put(r.getRoomId(), r.getRoom());
                roomIdToProgramId.put(r.getRoomId(), r.getProgramId());
            }
            for (Program p : programs) {
                programIdToDate.put(p.getProgramId(), p.getDate());
            }

            Log.i(TAG, "roomIdToProgramId: " + roomIdToProgramId);
            Log.i(TAG, "programIdToDate: " + programIdToDate);

            List<SessionCSVEntry> sessions = new ArrayList<>();
            for (Session_Room sr : sessionRooms) {
                SessionCSVEntry s = new SessionCSVEntry();
                s.setChair(sr.getChair());
                s.setPapers(sr.getPapers());
                s.setSessionName(sr.getSessionName());
                s.setSessionTitle(sr.getSessionTitle());
                s.setTimeslot(sr.getTimeslot());
                s.setRoom(roomIdToRoomName.get(sr.getRoomId()));
                s.setDate(programIdToDate.get(roomIdToProgramId.get(sr.getRoomId())));
                sessions.add(s);
            }

            Log.i(TAG, "load " + sessions.size() + " session entries");
            saveCSVToDrive(sessions, REQUEST_CODE_CREATE_CSV_2);
        } catch (ParseException e) {
            Log.w(TAG, "Error: " + e.getMessage());
        }
    }

    // convert models to csv file to google drive
    private void saveCSVToDrive(final List<? extends CSVAbstractEntry> entries, final int requestCode) {
        if (entries.isEmpty()) { // no data to write
            return;
        }
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new google drive content");
                    return;
                }
                // writing the paper data to google content
                OutputStream outStream = driveContentsResult.getDriveContents().getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outStream);
                try {
                    writer.write(entries.get(0).getHeader() + "\n");
                    for (CSVAbstractEntry e : entries) {
                        CSVUtils.writeLine(writer, e.getRow(), ',', '"');
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    Log.w(TAG, "write csv header/row error");
                }

                // show user the metadata of file about to be created
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("text/csv")
                        .setTitle(entries.get(0).getFileType() + ".csv")
                        .build();
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContentsResult.getDriveContents())
                        .build(mGoogleApiClient);
                // inform the saved file type when come back
                Intent extra = new Intent().putExtra(FILE_TYPE_EXTRA, 9527);
                try {
                    startIntentSenderForResult(intentSender, requestCode, extra, 0, 0, 0);
                } catch (SendIntentException e) {
                    Log.i(TAG, "Failed to open file chooser");
                }
            }
        });
    }

    /**
     * IMPORT SECTION
     */
    // import paper.csv and session.csv file into parse backend
    private void importData() {
        // wipe all schedule backend data before import
        List<Class> parseClasses = Arrays.asList(new Class[] {Paper.class, Session_Room.class, Session_Timeslot.class, Timeslot.class, Room.class, Program.class});
        for (Class parseClass : parseClasses) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClass).setLimit(1000);
            try {
                List<ParseObject> objects = query.find();
                Log.d(TAG, "Get " + objects.size() + " parse objects");
                ParseObject.deleteAll(objects);
            } catch (ParseException e) {
                Log.e(TAG, "Error delete parse objects: " + e.getMessage());
                return;
            }
        }
        // start to import the data
        importPaperData();
        importSessionData();
        // update the backend version
        try {
            Version version = Version.getQuery().getFirst();
            double v2 = Double.parseDouble(version.getVersion());
            version.setVersion("" + (v2 + 1));
            version.save();
            Version.unpinAll();
        } catch (ParseException e) {
            Log.e(TAG, "Error update db version");
        }
    }

    private boolean validateFileFormat() {
        DriveContents paperContents = getDriveContents(mPaperCSVFileDriveId);
        DriveContents sessionContents = getDriveContents(mSessionCSVFileDriveId);
        return validateCSVFile(new PaperCSVEntry(), paperContents) && validateCSVFile(new SessionCSVEntry(), sessionContents);
    }

    private boolean validateCSVFile(CSVAbstractEntry e, DriveContents contents) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
        try {
            String header = reader.readLine();
            String[] columns = header.split(",");
            Set<String> cols = new HashSet<>();

            for (String field : columns) {
                cols.add(field);
            }
            for (String c : e.getColumns()) {
                if (!cols.contains(c)) {
                    return false;
                }
            }
            return cols.size() == e.getColumns().length;
        } catch (IOException err) {
            Log.e(TAG, "Read paper file header error: " + err.getMessage());
        }
        Log.d(TAG, "Validate " + e.getFileType() + " completed");
        return false;
    }

    private DriveContents getDriveContents(DriveId driveId) {
        DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
        return file.open(mGoogleApiClient,  DriveFile.MODE_READ_ONLY, null).await().getDriveContents();
    }

    private void importPaperData() {
        long startTime = System.currentTimeMillis();
        // read paper.csv and save to Parse
        DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, mPaperCSVFileDriveId);
        DriveContents contents = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await().getDriveContents();
        BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
        try {
            String header = reader.readLine();
            String[] columns = header.split(",");
            // loading entries to parse
            String line = null;
            int paperId = 1;
            List<Paper> papers = new ArrayList<Paper>();
            while((line = reader.readLine()) != null) {
                List<String> values = CSVUtils.parseLine(line);
                Paper paper = new Paper();
                paper.setPaperId(paperId++);
                for (int i = 0; i < values.size(); i++) {
                    paper.put(columns[i], values.get(i));
                }
                papers.add(paper);
            }
            // save all paper data to parse in foreground
            ParseObject.saveAll(papers);
            reader.close();
        } catch (Exception e) {
            Log.w(TAG, "Error to read data from drive: " + e.getMessage());
        }

        Log.d(TAG, "Import paper finish in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
    }

    private void importSessionData() {
        long startTime = System.currentTimeMillis();
        // read session.csv, construct program, timeslot, session_timeslot, room, session_room tables, and save to Parse
        DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, mSessionCSVFileDriveId);
        DriveContents contents = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await().getDriveContents();

        BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
        try {
            String header = reader.readLine();
            String[] columns = header.split(",");
            Map<String, Integer> columnToPos = new HashMap<String, Integer>();
            // map column name to csv position
            for (int i = 0; i < columns.length; i++) {
                columnToPos.put(columns[i], i);
            }
            // cache the all files first
            List<List<String>> lines = new ArrayList<List<String>>();
            String line = null;
            while((line = reader.readLine()) != null) {
                lines.add(CSVUtils.parseLine(line));
            }
            reader.close();
            Log.i(TAG, "Read " + lines.size() + " sessions.");

            // populate program table
            Set<String> dates = new HashSet<>();
            for (List<String> l : lines) {
                String date = l.get(columnToPos.get("date"));
                if (!date.trim().isEmpty()) {
                    dates.add(date.trim());
                }
            }
            List<String> sortedDates = new ArrayList<String>(dates);
            Collections.sort(sortedDates, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");
                        return sdf.parse(s2).compareTo(sdf.parse(s1));
                    } catch (java.text.ParseException e) {
                        Log.w(TAG, "Parse dates error");
                        return 0;
                    }
                }
            });
            Map<String, Integer> dateToProgramId = new HashMap<String, Integer>();
            List<Program> programs = new ArrayList<Program>();
            for (int i = 1; i <= sortedDates.size(); i++) {
                dateToProgramId.put(sortedDates.get(i-1), i);
                Program p = new Program();
                p.setDate(sortedDates.get(i-1));
                p.setProgramId(i);
                programs.add(p);
            }
            // save all program data to Parse
            ParseObject.saveAll(programs);
            Log.i(TAG, "Populate " + programs.size() + " programs.");

            // populate timeslot table
            List<Timeslot> timeslots = new ArrayList<Timeslot>();
            for (int i = 0; i < lines.size(); i++) {
                List<String> fields = lines.get(i);
                Timeslot t = new Timeslot();
                t.setTimeslotId(i+1);
                String date = fields.get(columnToPos.get("date"));
                if (dateToProgramId.get(date) != null) {
                    t.setProgramId(dateToProgramId.get(date));
                }
                t.setValue(fields.get(columnToPos.get("timeslot")));
                timeslots.add(t);
            }
            // save timeslot data into parse
            ParseObject.saveAll(timeslots);
            Log.i(TAG, "Populate " + timeslots.size() + " timeslots.");

            // populate session_timeslot table
            List<Session_Timeslot> sessionTimeslots = new ArrayList<Session_Timeslot>();
            for (int i = 0; i < lines.size(); i++) {
                List<String> fields = lines.get(i);
                Session_Timeslot st = new Session_Timeslot();
                st.setSessionId(i+1);
                st.setTimeslotId(i+1);
                st.setChair(fields.get(columnToPos.get("chair")));
                st.setValue(fields.get(columnToPos.get("session_name")));
                st.setPapers(fields.get(columnToPos.get("papers")));
                st.setRoom(fields.get(columnToPos.get("room")));
                st.setSelected(0);
                st.setSessionTitle(fields.get(columnToPos.get("session_title")));
                sessionTimeslots.add(st);
            }
            // save session_timeslot to Parse
            ParseObject.saveAll(sessionTimeslots);
            Log.i(TAG, "Populate " + timeslots.size() + " timeslots.");

            // populate room table
            Map<String, Integer> roomIds = new HashMap<String, Integer>();
            List<Room> rooms = new ArrayList<Room>();
            int roomId = 1;
            for (int i = 0; i < lines.size(); i++) {
                List<String> fields = lines.get(i);
                String room = fields.get(columnToPos.get("room"));
                String date = fields.get(columnToPos.get("date"));
                String key = room + "!" + date;
                if (roomIds.containsKey(key)) {
                    continue;
                } else {
                    roomIds.put(key, roomId++);
                    Room r = new Room();
                    r.setRoom(room);
                    if (dateToProgramId.get(date) != null) {
                        r.setProgramId(dateToProgramId.get(date));
                    }
                    r.setRoomId(roomIds.get(key));
                    rooms.add(r);
                }
            }
            // save room to parse
            ParseObject.saveAll(rooms);

            // populate session_room table
            List<Session_Room> sessionRooms = new ArrayList<Session_Room>();
            for (int i = 0; i < lines.size(); i++) {
                List<String> fields = lines.get(i);
                Session_Room sr = new Session_Room();
                String room = fields.get(columnToPos.get("room"));
                sr.setRoomId(roomIds.get(room + "!" + fields.get(columnToPos.get("date"))));
                sr.setSessionTitle(fields.get(columnToPos.get("session_title")));
                sr.setChair(fields.get(columnToPos.get("chair")));
                sr.setPapers(fields.get(columnToPos.get("papers")));
                sr.setSessionName(fields.get(columnToPos.get("session_name")));
                sr.setTimeslot(fields.get(columnToPos.get("timeslot")));
                sr.setSelected(0);
                sr.setSessionId(i+1);
                sessionRooms.add(sr);
            }
            // save all session_room to Parse
            ParseObject.saveAll(sessionRooms);
        } catch (IOException e) {
            Log.w(TAG, "Error to read data from drive: " + e.getMessage());
        } catch (ParseException e) {
            Log.w(TAG, "Error saving data to parse: " + e.getMessage());
        }

        Log.d(TAG, "Import session finish in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google client not connected!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Google client connected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            Toast.makeText(this, "Google client disconnected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.e(TAG, "intent passed: " + data);
        switch (requestCode) {
            case REQUEST_CODE_OPEN_FILE_LIST_1:
                if (resultCode == RESULT_OK) {
                    mPaperCSVFileDriveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    mGoogleApiClient.connect();
                    while (!mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                        Log.i(TAG, "Try to connect to client");
                    }
                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, mPaperCSVFileDriveId);
                    file.getMetadata(mGoogleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                        @Override
                        public void onResult(@NonNull DriveResource.MetadataResult metadataResult) {
                            String filename = metadataResult.getMetadata().getOriginalFilename();
                            mPaperFileName.setText(filename);
                        }
                    });

                }
                break;
            case REQUEST_CODE_OPEN_FILE_LIST_2:
                if (resultCode == RESULT_OK) {
                    mSessionCSVFileDriveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, mSessionCSVFileDriveId);
                    file.getMetadata(mGoogleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                        @Override
                        public void onResult(@NonNull DriveResource.MetadataResult metadataResult) {
                            String filename = metadataResult.getMetadata().getOriginalFilename();
                            mSessionFileName.setText(filename);
                        }
                    });
                }
                break;
            case REQUEST_CODE_CREATE_CSV_1:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "CSV file saved in google drive");
                    // change the exp button status correspondingly
                    Log.i(TAG, "morph exp paper button to success.");
                    morphToSuccess(mExpPaperBtn);
                } else {
                    Log.i(TAG, "morph exp paper button to init.");
                    morphToSquare(mExpPaperBtn, integer(R.integer.mb_animation), R.string.export_paper_btn);
                }
                break;
            case REQUEST_CODE_CREATE_CSV_2:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "CSV file saved in google drive");
                    Log.i(TAG, "morph exp session button to success.");
                    morphToSuccess(mExpSessionBtn);
                } else {
                    Log.i(TAG, "morph exp session button to init.");
                    morphToSquare(mExpSessionBtn, integer(R.integer.mb_animation), R.string.export_session_btn);
                }
                break;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        mPaperFileName = (TextView) findViewById(R.id.paper_file_name);
        mSessionFileName = (TextView) findViewById(R.id.session_file_name);
        mExpPaperBtn = (IndeterminateProgressButton) findViewById(R.id.export_paper_data_btn);
        mExpSessionBtn = (IndeterminateProgressButton) findViewById(R.id.export_session_data_btn);
        mImpBtn = (IndeterminateProgressButton) findViewById(R.id.import_data_btn);

        mPaperFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDriveFiles(REQUEST_CODE_OPEN_FILE_LIST_1);
            }
        });

        mSessionFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDriveFiles(REQUEST_CODE_OPEN_FILE_LIST_2);
            }
        });

        mExpPaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMorphButtonClicked(mExpPaperBtn, R.string.export_paper_btn, 1);

            }
        });

        mExpSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMorphButtonClicked(mExpSessionBtn, R.string.export_session_btn, 2);

            }
        });

        mImpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPaperCSVFileDriveId == null || mSessionCSVFileDriveId == null) {
                    Toast.makeText(view.getContext(), "Files supplied error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                onMorphButtonClicked(mImpBtn, R.string.import_btn, 3);
            }
        });
    }

    private void onMorphButtonClicked(final IndeterminateProgressButton btn, int resId, int btnType) {
        if (btn.getTag() != null && (int) btn.getTag() == MORPH_BUTTON_SUC) {
            morphToSquare(btn, integer(R.integer.mb_animation), resId);
        } else {
            switch (btnType) {
                case 1:
                    setButtonInProgress(btn);
                    new AsyncTask<Void, Void, Void>() { // export paper async task
                        @Override
                        protected Void doInBackground(Void... voids) {
                            exportPaperDataFromParse();
                            return null;
                        }
                    }.execute();
                    break;
                case 2:
                    setButtonInProgress(btn);
                    new AsyncTask<Void, Void, Void>() { // export session async task
                        @Override
                        protected Void doInBackground(Void... voids) {
                            exportSessionDataFromParse();
                            return null;
                        }
                    }.execute();
                    break;
                case 3:
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TransferActivity.this, R.style.MyDialogTheme);
                    builder.setTitle(getString(R.string.import_dialog_title));
                    builder.setMessage(getString(R.string.import_dialog_msg));
                    builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setButtonInProgress(btn);
                            new AsyncTask<Void, Void, Boolean>() { // import async task
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    Log.d(TAG, "validate import data format...");
                                    if (!validateFileFormat()) {
                                        return false;
                                    }
                                    Log.d(TAG, "start import data task...");
                                    importData();
                                    return true;
                                }
                                @Override
                                protected void onPostExecute(Boolean success) {
                                    if (success) {
                                        Log.d(TAG, "finish import data task.");
                                        morphToSuccess(mImpBtn);
                                    } else {
                                        Log.d(TAG, "import data task failed.");
                                        morphToSquare(mImpBtn, integer(R.integer.mb_animation), R.string.import_btn);
                                        Toast.makeText(getApplicationContext(), "Import file format invalid! Please select file with correct format.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                    builder.setNegativeButton(getString(android.R.string.cancel), null);
                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    }

    private void morphToSquare(final IndeterminateProgressButton btnMorph, int duration, int resId) {
        btnMorph.unblockTouch();
        btnMorph.setTag(MORPH_BUTTON_INIT);
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .width(ViewGroup.LayoutParams.WRAP_CONTENT)
                .height(ViewGroup.LayoutParams.WRAP_CONTENT)
                .text(getString(resId));
        btnMorph.morph(square);
    }

    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        btnMorph.setTag(MORPH_BUTTON_SUC);
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
        btnMorph.unblockTouch();
    }

    private IndeterminateProgressButton setButtonInProgress(@NonNull final IndeterminateProgressButton button) {
        int progressColor1 = color(R.color.holo_blue_bright);
        int progressColor2 = color(R.color.holo_green_light);
        int progressColor3 = color(R.color.holo_orange_light);
        int progressColor4 = color(R.color.holo_red_light);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = integer(R.integer.mb_animation);

        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);

        return button;
    }
}
