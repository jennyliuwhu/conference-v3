package cmu.cconfs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.model.parseModel.Program;

public class LoadingActivityTest extends Activity {

    private TextView textView;

    private List results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            ParseObject.unpinAll();
            ParseObject.unpinAll(Program.PIN_TAG);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_test);
        textView = (TextView) findViewById(R.id.test);

        load();

        loadFromDatastore();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            sb.append(((Program) (results.get(i))).getDate());
            sb.append("\n");
        }
        textView.setText(sb.toString());
    }

    private void loadFromDatastore() {
        ParseQuery query = Program.getQuery();
        query.fromLocalDatastore();
        query.fromPin(Program.PIN_TAG);
        Program program = null;
        try {
            program = (Program) query.get("OXakPF2Y19");
            program.setDate("XXXXXX");
            program.pin(Program.PIN_TAG);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        query = Program.getQuery();
//        query.clearCachedResult();

        //query.orderByAscending("createdAt");

        query.fromLocalDatastore();
        ArrayList values = new ArrayList();
        values.add(1);
        values.add(2);
        query.whereContainedIn("program_id", values);
        try {
            results = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void load() {
        ParseQuery query = Program.getQuery();
        List<Program> result = null;

        try {
           /* // Paper
            result=query.find();
            ParseObject.pinAll(Paper.PIN_TAG,result);*/

            // Program
            result = query.find();
            ParseObject.pinAll(Program.PIN_TAG, result);

           /* // Room
            query= Room.getQuery();
            result=query.find();
            ParseObject.pinAll(Room.PIN_TAG,result);

            // Session_Room
            query= Session_Room.getQuery();
            result=query.find();
            ParseObject.pinAll(Session_Room.PIN_TAG,result);

            // Session_Timeslot
            query= Session_Timeslot.getQuery();
            result=query.find();
            ParseObject.pinAll(Session_Timeslot.PIN_TAG,result);

            // Timeslot
            query= Timeslot.getQuery();
            result=query.find();
            ParseObject.pinAll(Timeslot.PIN_TAG,result);*/

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadFromParse() {
//        ParseQuery<Todo> query = Todo.getQuery();
//        query.whereEqualTo("isDraft", false);
////        try {
////            results=query.find();
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
////
////        StringBuilder sb= new StringBuilder();
////        for (Todo todo: results){
////            sb.append(todo.getTitle());
////            sb.append("\n");
////        }
////        textView.setText(sb.toString());
//
//        query.findInBackground(new FindCallback<Todo>() {
//            public void done(List<Todo> todos, ParseException e) {
//                if (e == null) {
//                    ParseObject.pinAllInBackground("ALL_TODOS", (List<Todo>) todos,
//                            new SaveCallback() {
//                                public void done(ParseException e) {
//                                    if (e == null) {
//
//                                    } else {
//                                        Log.e("TodoListActivity",
//                                                "Error pinning todos: "
//                                                        + e.getMessage());
//                                    }
//                                }
//                            });
//                } else {
//                    Log.e("TodoListActivity",
//                            "loadFromParse: Error finding pinned todos: "
//                                    + e.getMessage());
//                }
//            }
//        });
    }
}
