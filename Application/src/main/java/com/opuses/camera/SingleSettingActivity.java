package com.opuses.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SingleSettingActivity extends Activity {
    private DBAdapter _dbAdapter;

    private ListView _settingListView;

    private String _nameStr;
    private String _focusStr;
    private String _exposureStr;
    private String _isoStr;
    private String _flashStr;

    private String _theSetting = "";

    private Integer _checkedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_single);

        _settingListView = (ListView)findViewById(R.id.SingleSetting_listView);
        _settingListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //nothing is checked by default
        _checkedPosition = -1;

        _settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _checkedPosition = position;

                _nameStr = ((TextView) view.findViewById(R.id.name_checkedTextView)).getText().toString();
                _focusStr = ((TextView) view.findViewById(R.id.focus_textView)).getText().toString();
                _exposureStr = ((TextView) view.findViewById(R.id.exposure_textView)).getText().toString();
                _isoStr = ((TextView) view.findViewById(R.id.iso_textView)).getText().toString();
                _flashStr = ((TextView) view.findViewById(R.id.flash_textView)).getText().toString();
            }
        });

        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentMessage = new Intent();

                // if a valid setting is selected, pass the setting back
                if (_checkedPosition >= 0) {

                    _theSetting = _nameStr + ","
                            + _focusStr + ","
                            + _exposureStr + ","
                            + _isoStr + ","
                            + _flashStr;

                    intentMessage.putExtra("SINGLE_SETTING", _theSetting);
                    setResult(RESULT_OK, intentMessage);

                } else {
                    // else do noting
                    setResult(RESULT_CANCELED, intentMessage);
                }

                // finish The activity
                closeDB();
                finish();
            }
        });

        Button clearDBButton = (Button)findViewById(R.id.clearDB_button);
        clearDBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDB();
                populateListViewFromDB();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelSingle_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);

                closeDB();
                finish();
            }
        });


        // open database
        openDB();
        populateListViewFromDB();
    }

    @Override
    protected void onStop() {
        super.onStop();

        _dbAdapter.close();
    }

    private void openDB() {
        _dbAdapter = new DBAdapter(getBaseContext());
        _dbAdapter.open();
    }

    private void closeDB() {
        _dbAdapter.close();
    }

    private void clearDB() {
        _dbAdapter.deleteAll_setting();
    }

    private void populateListViewFromDB() {
        Cursor cursor = _dbAdapter.getAllRows_setting();

        //noinspection deprecation,deprecation
        startManagingCursor(cursor);

        String[] fromFieldNames = new String[]{
                DBAdapter.KEY_NAME_SETTING,
                DBAdapter.KEY_FOCUS,
                DBAdapter.KEY_EXPOSURE,
                DBAdapter.KEY_ISO,
                DBAdapter.KEY_FLASH
        };

        int[] toViewIDs = new int[] {
                R.id.name_checkedTextView,
                R.id.focus_textView,
                R.id.exposure_textView,
                R.id.iso_textView,
                R.id.flash_textView
        };

        @SuppressWarnings("deprecation") SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(
                        this,
                        R.layout.list_items,
                        cursor,
                        fromFieldNames,
                        toViewIDs
                );

        _settingListView.setAdapter(cursorAdapter);
    }

}
