package cmu.cconfs;

/**
 * Created by qiuzhexin on 12/13/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmu.cconfs.model.parseModel.Paper;
import cmu.cconfs.model.parseModel.Program;
import cmu.cconfs.model.parseModel.Room;
import cmu.cconfs.model.parseModel.Session_Room;
import cmu.cconfs.model.parseModel.Session_Timeslot;
import cmu.cconfs.model.parseModel.Timeslot;
import cmu.cconfs.utils.csv.CSVAbstractEntry;
import cmu.cconfs.utils.csv.CSVUtils;
import cmu.cconfs.utils.csv.PaperCSVEntry;
import cmu.cconfs.utils.csv.SessionCSVEntry;

/**
 * Android Drive Quickstart activity. This activity takes a photo and saves it
 * in Google Drive. The user is prompted with a pre-made dialog which allows
 * them to choose the file location.
 */
public class TransferActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_CODE_OPEN_FILE_LIST_1 = 4;
    private static final int REQUEST_CODE_OPEN_FILE_LIST_2 = 5;
    private static final int REQUEST_CODE_READ_FILE = 6;
    private static final int REQUEST_CODE_CREATE_CSV = 7;


    private GoogleApiClient mGoogleApiClient;
    private Bitmap mBitmapToSave;

    private DriveId mPaperCSVFileDriveId = null;
    private DriveId mSessionCSVFileDriveId = null;

    private TextView mPaperFileName;
    private TextView mSessionFileName;
    private Button mSelectPaperButton;
    private Button mSelectSessionButton;
    private Button mExportPaperDataButton;
    private Button mExportSessionDataButton;
    private Button mImportDataButton;

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

                    @Override
                    public void onResult(DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();

                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }

    // show a list of files in google drive
    private void displayDriveFiles(int requestCode) {
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

    // export data from parse and create paper.csv
    private void exportPaperDataFromParse() {
        ParseQuery<Paper> query = ParseQuery.getQuery(Paper.class);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<Paper>() {
            @Override
            public void done(final List<Paper> papers, ParseException e) {
                if (e == null) { // received paper data
                    List<PaperCSVEntry> paperCSVEntries = new ArrayList<PaperCSVEntry>();
                    for (Paper p : papers) {
                        PaperCSVEntry entry = new PaperCSVEntry();
                        entry.map(p);
                        paperCSVEntries.add(entry);
                    }

                    Log.i(TAG, "load " + paperCSVEntries.size() + " paper entries");
                    saveCSVToDrive(paperCSVEntries);
                } else {
                    Log.w(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    // export session data from parse
    private void exportSessionDataFromParse() {
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
            saveCSVToDrive(sessions);
        } catch (ParseException e) {
            Log.w(TAG, "Error: " + e.getMessage());
        }
    }

    // convert models to csv file to google drive
    private void saveCSVToDrive(final List<? extends CSVAbstractEntry> entries) {
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
                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_CSV, null, 0, 0, 0);
                } catch (SendIntentException e) {
                    Log.i(TAG, "Failed to open file chooser");
                }
            }
        });
    }

    // import paper.csv and session.csv file into parse backend
    private void importData() {
        if (mPaperCSVFileDriveId != null && mSessionCSVFileDriveId != null) {
            importPaperData();
//            importSessionData();
        } else {
            Toast.makeText(this, "Please choose both files!", Toast.LENGTH_SHORT).show();
        }
    }

    private void importPaperData() {
        // read paper.csv and save to Parse
        DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, mPaperCSVFileDriveId);
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {

            }
        }).setResultCallback(new ResultCallback<DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.w(TAG, "Reading google drive file error");
                }
                DriveContents contents = driveContentsResult.getDriveContents();
                BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                try {
                    String header = reader.readLine();
                    String[] columns = header.split(",");
                    // verify the format is correct
                    if (!validateCSVFile(columns, new PaperCSVEntry())) {
                        Toast.makeText(getApplicationContext(), "csv file format not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
            }
        });
    }

    private void importSessionData() {
        // read session.csv, construct program, timeslot, session_timeslot, room, session_room tables, and save to Parse
        DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, mSessionCSVFileDriveId);
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
            @Override
            public void onProgress(long l, long l1) {

            }
        }).setResultCallback(new ResultCallback<DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.w(TAG, "Reading google drive file error");
                }
                DriveContents contents = driveContentsResult.getDriveContents();
                BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                try {
                    String header = reader.readLine();
                    String[] columns = header.split(",");
                    Map<String, Integer> columnToPos = new HashMap<String, Integer>();
                    // map column name to csv position
                    for (int i = 0; i < columns.length; i++) {
                        columnToPos.put(columns[i], i);
                    }
                    // verify the format is correct
                    if (!validateCSVFile(columns, new SessionCSVEntry())) {
                        Toast.makeText(getApplicationContext(), "csv file format not valid", Toast.LENGTH_SHORT).show();
                        return;
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
            }
        });
    }

    private boolean validateCSVFile(String[] columns, CSVAbstractEntry e) {
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
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
                }
                break;
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    mBitmapToSave = null;
                    // Just start the camera again for another photo.
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                            REQUEST_CODE_CAPTURE_IMAGE);
                }
                break;
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
            case REQUEST_CODE_CREATE_CSV:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "CSV file saved in google drive");
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
//        if (mBitmapToSave == null) {
//            // This activity has no UI of its own. Just start the camera.
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
//                    REQUEST_CODE_CAPTURE_IMAGE);
//            return;
//        }
//        saveFileToDrive();
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
        mSelectPaperButton = (Button) findViewById(R.id.paper_file_select_btn);
        mSelectSessionButton = (Button) findViewById(R.id.session_file_select_btn);
        mExportPaperDataButton = (Button) findViewById(R.id.export_paper_data_btn);
        mExportSessionDataButton = (Button) findViewById(R.id.export_session_data_btn);
        mImportDataButton = (Button) findViewById(R.id.import_btn);

        mSelectPaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDriveFiles(REQUEST_CODE_OPEN_FILE_LIST_1);
            }
        });

        mSelectSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDriveFiles(REQUEST_CODE_OPEN_FILE_LIST_2);
            }
        });

        mExportPaperDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportPaperDataFromParse();
            }
        });

        mExportSessionDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportSessionDataFromParse();
            }
        });

        mImportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importData();
            }
        });

    }

}
