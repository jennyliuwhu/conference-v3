package cmu.cconfs.utils.csv;

import java.util.Arrays;
import java.util.List;

/**
 * Created by qiuzhexin on 12/11/16
 *
 * SessionCSVEntry.csv object
 */

public class SessionCSVEntry extends CSVAbstractEntry {

    static String[] columns = {"session_name", "date", "timeslot", "room", "session_title", "chair", "papers" };
    private String mSessionName;
    private String mDate;
    private String mTimeslot;
    private String mRoom;
    private String mSessionTitle;
    private String mChair;
    private String mPapers;

    public SessionCSVEntry() {}

    public SessionCSVEntry(String sessionName, String date, String timeslot, String room, String sessionTitle, String chair, String papers) {
        mSessionName = sessionName;
        mDate = date;
        mTimeslot = timeslot;
        mRoom = room;
        mSessionTitle = sessionTitle;
        mChair = chair;
        mPapers = papers;
    }

    public String getSessionName() {
        return mSessionName;
    }

    public void setSessionName(String sessionName) {
        mSessionName = sessionName == null ? "" : sessionName.replace("\n", " ");
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date == null ? "" : date.replace("\n", " ");
    }

    public String getTimeslot() {
        return mTimeslot;
    }

    public void setTimeslot(String timeslot) {
        mTimeslot = timeslot == null ? "" : timeslot.replace("\n", " ");
    }

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(String room) {
        mRoom = room == null ? "" : room.replace("\n", " ");
    }

    public String getSessionTitle() {
        return mSessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        mSessionTitle = sessionTitle == null ? "" : sessionTitle.replace("\n", " ");
    }

    public String getChair() {
        return mChair;
    }

    public void setChair(String chair) {
        mChair = chair == null ? "" : chair.replace("\n", " ");
    }

    public String getPapers() {
        return mPapers;
    }

    public void setPapers(String papers) {
        mPapers = papers == null ? "" : papers.replace("\n", " ");
    }

    @Override
    public String toString() {
        return "SessionCSVEntry{" +
                "mSessionName='" + mSessionName + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mTimeslot='" + mTimeslot + '\'' +
                ", mRoom='" + mRoom + '\'' +
                ", mSessionTitle='" + mSessionTitle + '\'' +
                ", mChair='" + mChair + '\'' +
                ", mPapers='" + mPapers + '\'' +
                '}';
    }

    public static void setColumns(String[] columns) {
        SessionCSVEntry.columns = columns;
    }

    @Override
    public String[] getColumns() {
        return columns;
    }

    // get header for csv file
    @Override
    public String getHeader() {
        StringBuffer sb = new StringBuffer();
        for (String column : columns) {
            sb.append(column).append(",");
        }
        return sb.toString().substring(0, sb.length()-1);
    }

    // get a row
    @Override
    public List<String> getRow() {
        return Arrays.asList(new String[] { getSessionName(), getDate(), getTimeslot(), getRoom(), getSessionTitle(), getChair(), getPapers() });
    }

    // return file type
    @Override
    public String getFileType() {
        return "Session";
    }



}
