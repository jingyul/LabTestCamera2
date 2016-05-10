package com.opuses.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MultipleSettingActivity extends Activity {
    private DBAdapter dbAdapter;

    private ListView _settingListView;

    private String _nameStr;
    private String _focusStr;
    private String _exposureStr;
    private String _isoStr;
    private String _flashStr;

    private final ArrayList<String> _lineOfSettings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_burst);
        _settingListView = (ListView)findViewById(R.id.BurstSetting_listView);
        _settingListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        Button okButton = (Button) findViewById(R.id.burstOk_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SparseBooleanArray checked = _settingListView.getCheckedItemPositions();

                int listSize = checked.size();

                //if nothing is selected, to save means to cancel
                if (listSize == 0) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);

                    closeDB();
                    finish();
                    return;
                }

                for (int i = 0; i < listSize; i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i)) {
                        View view = getViewByPosition(position, _settingListView);

                        _nameStr = ((TextView) view.findViewById(R.id.name_checkedTextView)).getText().toString();
                        _focusStr = ((TextView) view.findViewById(R.id.focus_textView)).getText().toString();
                        _exposureStr = ((TextView) view.findViewById(R.id.exposure_textView)).getText().toString();
                        _isoStr = ((TextView) view.findViewById(R.id.iso_textView)).getText().toString();
                        _flashStr = ((TextView) view.findViewById(R.id.flash_textView)).getText().toString();

                        _lineOfSettings.add( _nameStr + ","
                                + _focusStr + ","
                                + _exposureStr + ","
                                + _isoStr + ","
                                + _flashStr);
                    }
                }

                // passing the info to the main fragment
                Intent intentMessage = new Intent();
                intentMessage.putExtra("BURST_SETTINGS", _lineOfSettings);

                setResult(RESULT_OK, intentMessage);

                // finish The activity
                closeDB();
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelMultiple_button);
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

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        dbAdapter.close();
    }

    private void openDB() {
        dbAdapter = new DBAdapter(getBaseContext());
        dbAdapter.open();
    }

    private void closeDB() {
        dbAdapter.close();
    }


    private void populateListViewFromDB() {
        Cursor cursor = dbAdapter.getAllRows_setting();

        //noinspection deprecation,deprecation,deprecation
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
