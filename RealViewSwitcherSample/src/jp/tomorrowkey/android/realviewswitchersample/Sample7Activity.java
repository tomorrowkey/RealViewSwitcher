
package jp.tomorrowkey.android.realviewswitchersample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.tomorrowkey.android.realviewswitcher.widget.RealViewSwitcher;

public class Sample7Activity extends Activity {

    private static final int DIALOG_INITIAL_LIST = 1;

    /**
     * ViewSwitcher
     */
    private RealViewSwitcher mViewSwitcher;

    /**
     * 頭文字リスト
     */
    private List<String> mInitialCharList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample6);

        initViewSwitcher();
        initLeftButton();
        initRightButton();

        Toast.makeText(getApplicationContext(), "please open menu, and select page",
                Toast.LENGTH_LONG).show();
    }

    private void initViewSwitcher() {
        mViewSwitcher = (RealViewSwitcher)findViewById(R.id.viewSwitcher);
        mInitialCharList = new ArrayList<String>();

        String[] countries = getResources().getStringArray(R.array.countries);

        // 各頭文字ごとにListViewを作って、ViewSwitcherに追加する
        for (char initialChar = 'A'; initialChar <= 'Z'; initialChar++) {
            String initialStr = String.valueOf(initialChar);
            List<String> items = new ArrayList<String>();
            for (String country : countries) {
                if (country.toUpperCase().startsWith(initialStr)) {
                    items.add(country);
                }
            }
            if (!items.isEmpty()) {
                mInitialCharList.add(initialStr);
                ListView listView = new ListView(this);
                listView.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, items));
                mViewSwitcher.addView(listView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }
        }
    }

    private void initLeftButton() {
        Button leftButton = (Button)findViewById(R.id.left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcher.switchLeftPage();
            }
        });
    }

    private void initRightButton() {
        Button rightButton = (Button)findViewById(R.id.right_button);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcher.switchRightPage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        showDialog(DIALOG_INITIAL_LIST);
        return false;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(mInitialCharList.toArray(new String[0]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewSwitcher.switchPage(which);
                    }
                });
        Dialog dialog = builder.create();
        return dialog;
    }
}
