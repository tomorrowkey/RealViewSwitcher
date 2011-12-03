
package jp.tomorrowkey.android.realviewswitchersample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.tomorrowkey.android.realviewswitcher.widget.RealViewSwitcher;

public class Sample2Activity extends Activity {

    private RealViewSwitcher mViewSwitcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample2);

        initViewSwitcher();
        initLeftButton();
        initRightButton();
    }

    private void initViewSwitcher() {
        mViewSwitcher = (RealViewSwitcher)findViewById(R.id.viewSwitcher);
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
