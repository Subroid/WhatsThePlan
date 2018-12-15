package platinum.whatstheplan.activities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import platinum.whatstheplan.R;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViewsAndVariables ();

        performActions ();
    }

        private void initViewsAndVariables() {
            mContext = ChatActivity.this;
            bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);

        }

        private void performActions() {
            performBottomNavigationViewExActions ();

        }

    private void performBottomNavigationViewExActions() {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
        // ^disable shifting mode of item when clicked

        BottomNavigationViewHelper.settingBottomNavigationViewListener
                                    (bottomNavigationViewEx, mContext, getResources());
        bottomNavigationViewEx.getIconAt(3).setImageDrawable(getDrawable(R.drawable.ic_chat_blue));

    }
}
