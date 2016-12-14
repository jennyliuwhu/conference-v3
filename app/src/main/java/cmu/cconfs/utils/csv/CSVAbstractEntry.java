package cmu.cconfs.utils.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuzhexin on 12/13/16.
 */

public abstract class CSVAbstractEntry {

    // column names for a csv file
    public String[] getColumns() {
        return new String[0];
    }

    // get header for csv file
    public String getHeader() {
        return "";
    }
    // get a row for a csv
    public List<String> getRow() {
        return new ArrayList<>();
    }

    public String getFileType() { return  ""; }


}

