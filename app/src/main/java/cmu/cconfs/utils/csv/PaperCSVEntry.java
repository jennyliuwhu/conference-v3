package cmu.cconfs.utils.csv;

import java.util.Arrays;
import java.util.List;

import cmu.cconfs.model.parseModel.Paper;

/**
 * Created by qiuzhexin on 12/12/16.
 */

public class PaperCSVEntry extends CSVAbstractEntry {

    static String[] columns = {"unique_id", "title", "author", "affiliation", "authorwithaffiliation", "abstract" };

    private String mUniqueId;
    private String mTitle;
    private String mAuthor;
    private String mAffiliation;
    private String mAuthorWithAffiliation;
    private String mAbstract;


    public PaperCSVEntry() {
    }

    public static void setColumns(String[] columns) {
        PaperCSVEntry.columns = columns;
    }

    public String getUniqueId() {
        return mUniqueId;
    }

    public void setUniqueId(String uniqueId) {
        mUniqueId = uniqueId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAffiliation() {
        return mAffiliation;
    }

    public void setAffiliation(String affiliation) {
        mAffiliation = affiliation;
    }

    public String getAuthorWithAffiliation() {
        return mAuthorWithAffiliation;
    }

    public void setAuthorWithAffiliation(String authorWithAffiliation) {
        mAuthorWithAffiliation = authorWithAffiliation;
    }

    public String getAbstract() {
        return mAbstract;
    }

    public void setAbstract(String anAbstract) {
        mAbstract = anAbstract;
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
        return Arrays.asList(new String[] {getUniqueId(), getTitle(), getAuthor(), getAffiliation(), getAuthorWithAffiliation(), getAbstract() });
    }

    // return file type
    @Override
    public String getFileType() {
        return "Paper";
    }

    // map parse paper data to csv object
    public void map(Paper p) {
        setUniqueId(p.getUniqueId());
        setAbstract(p.getAbstract().replace("\n", " "));
        setAffiliation(p.getAffiliation().replace("\n", " "));
        setAuthor(p.getAuthor().replace("\n", " "));
        setAuthorWithAffiliation(p.getAuthorWithAffiliation().replace("\n", " "));
        setTitle(p.getTitle().replace("\n", " "));
    }
}
