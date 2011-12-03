
package jp.tomorrowkey.android.realviewswitchersample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jp.tomorrowkey.android.realviewswitcher.widget.RealViewSwitcher;

public class Sample3Activity extends Activity {

    private static final int DIALOG_INTERPOLATOR_LIST = 1;

    private RealViewSwitcher mViewSwitcher;

    private static final String[] INTERPOLATOR_ENTRY_LIST = {
            "ACCELERATE_DECELERATE_INTERPOLATOR", "ACCELERATE_INTERPOLATOR ",
            "ANTICIPATE_INTERPOLATOR", "ANTICIPATE_OVERSHOOT_INTERPOLATOR", "BOUNCE_INTERPOLATOR",
            "DECELERATE_INTERPOLATOR", "LINEAR_INTERPOLATOR", "OVERSHOOT_INTERPOLATOR",
    };

    private static final int[] INTERPOLATOR_VALUE_LIST = {
            RealViewSwitcher.ACCELERATE_DECELERATE_INTERPOLATOR,
            RealViewSwitcher.ACCELERATE_INTERPOLATOR, RealViewSwitcher.ANTICIPATE_INTERPOLATOR,
            RealViewSwitcher.ANTICIPATE_OVERSHOOT_INTERPOLATOR,
            RealViewSwitcher.BOUNCE_INTERPOLATOR, RealViewSwitcher.DECELERATE_INTERPOLATOR,
            RealViewSwitcher.LINEAR_INTERPOLATOR, RealViewSwitcher.OVERSHOOT_INTERPOLATOR,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample3);

        initViewSwitcher();
        initLeftButton();
        initRightButton();

        Toast.makeText(getApplicationContext(), "please open menu, and select interplator",
                Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        showDialog(DIALOG_INTERPOLATOR_LIST);
        return false;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(INTERPOLATOR_ENTRY_LIST, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewSwitcher.setSwitchInterpolator(INTERPOLATOR_VALUE_LIST[which]);
            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }
}
