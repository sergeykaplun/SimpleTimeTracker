package com.senya.simpletimetracker.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.senya.simpletimetracker.R;
import com.senya.simpletimetracker.adapters.TaskListCursorAdapter;
import com.senya.simpletimetracker.database.DatabaseHelper;
import com.senya.simpletimetracker.database.RunTimePeriodHelper;
import com.senya.simpletimetracker.database.TasksDatabaseHelper;
import com.senya.simpletimetracker.models.Task;

import java.util.Date;

/**
 * Created by sergeykaplun on 10/10/13.
 */
public class NewTaskDialogFragment extends DialogFragment {
    public interface CreationListener{
        public void OnTaskCreated();
    }
    private Dialog dialog;
    private AutoCompleteTextView titleView;
    private EditText descriptionView;
    private CheckBox startImmediately;
    private Button cancelButton;
    private Button createButton;
    private Context context;
    private CreationListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (CreationListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        View rootView = View.inflate(getActivity(), R.layout.create_new_task_dialog_layout, null);
        titleView = (AutoCompleteTextView) rootView.findViewById(R.id.title_autocomplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line,
                TasksDatabaseHelper.getInstance().getPreviousTitles(context));
        titleView.setAdapter(adapter);
        titleView.setThreshold(1);

        descriptionView = (EditText) rootView.findViewById(R.id.description_edittext);
        startImmediately = (CheckBox) rootView.findViewById(R.id.start_checkbox);
        cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        createButton = (Button) rootView.findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ((EditText) titleView).getText().toString();
                if(TextUtils.isEmpty(title))
                    title = TaskListCursorAdapter.sdf.format(new Date());
                saveTask(title, descriptionView.getText().toString(), startImmediately.isChecked());
                dismiss();
            }
        });

        dialog = new AlertDialog.Builder(context).
                setView(rootView).
                setCancelable(false).
                create();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }

    private void saveTask(String title, String description, boolean started){
        long creationDate = new Date().getTime();
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(Task.COL_TITLE, title);
        contentValues.put(Task.COL_DESCRIPTION, description);
        contentValues.put(Task.COL_CREATION_DATE, creationDate);
        contentValues.put(Task.COL_STR_CREATION_DATE, TasksDatabaseHelper.sdf.format(creationDate));
        contentValues.put(Task.COL_STATE, Task.TaskState.NEW.toString());
        long id = TasksDatabaseHelper.getInstance().insertTask(context, contentValues);

        if(started){
            RunTimePeriodHelper.getInstance(getActivity()).addRunTimePeriod((int) id);

            contentValues.clear();
            contentValues.put(Task.COL_START_DATE, creationDate);
            contentValues.put(Task.COL_STATE, Task.TaskState.RUNNING.toString());
            new DatabaseHelper(getActivity()).getWritableDatabase().update(DatabaseHelper.TASKS_TABLE_NAME, contentValues, "_id = ?", new String[]{String.valueOf(id)});
        }
        listener.OnTaskCreated();
    }
}
