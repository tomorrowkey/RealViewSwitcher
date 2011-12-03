
package jp.tomorrowkey.android.realviewswitchersample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jp.tomorrowkey.android.realviewswitcher.widget.RealViewSwitcher;

public class Sample6Activity extends Activity {

    /**
     * ViewSwitcher
     */
    private RealViewSwitcher mViewSwitcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample6);

        initViewSwitcher();
        initLeftButton();
        initRightButton();
    }

    private void initViewSwitcher() {
        mViewSwitcher = (RealViewSwitcher)findViewById(R.id.viewSwitcher);

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
}
