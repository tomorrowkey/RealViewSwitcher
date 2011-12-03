
package jp.tomorrowkey.android.realviewswitchersample;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SampleListActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<ActivityEntry> sampleList = new ArrayList<ActivityEntry>();
        sampleList.add(new ActivityEntry("1. Three Page", Sample1Activity.class));
        sampleList.add(new ActivityEntry("2. Switched by button", Sample2Activity.class));
        sampleList.add(new ActivityEntry("3. Change interplator", Sample3Activity.class));
        sampleList.add(new ActivityEntry("4. One Page", Sample4Activity.class));
        sampleList.add(new ActivityEntry("5. Two Page", Sample5Activity.class));
        sampleList.add(new ActivityEntry("6. ViewSwitcher in ListView", Sample6Activity.class));
        sampleList.add(new ActivityEntry("7. Jump page", Sample7Activity.class));
        sampleList.add(new ActivityEntry("8. Jump page directly", Sample8Activity.class));
        setListAdapter(new ArrayAdapter<ActivityEntry>(this, android.R.layout.simple_list_item_1,
                sampleList));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ActivityEntry entry = (ActivityEntry)l.getItemAtPosition(position);
        Intent intent = new Intent(this, entry.getValue());
        startActivity(intent);
    }

    static class ActivityEntry implements Map.Entry<String, Class<? extends Activity>> {

        private String key;

        private Class<? extends Activity> value;

        public ActivityEntry(String key, Class<? extends Activity> value) {
            this.key = key;
            setValue(value);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Class<? extends Activity> getValue() {
            return value;
        }

        @Override
        public Class<? extends Activity> setValue(Class<? extends Activity> value) {
            this.value = value;
            return value;
        }

        @Override
        public String toString() {
            return getKey();
        }
    }
}
