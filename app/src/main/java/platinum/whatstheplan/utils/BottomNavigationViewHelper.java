package platinum.whatstheplan.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.BookingsActivity;
import platinum.whatstheplan.activities.HomeActivity;
import platinum.whatstheplan.activities.MapActivity;

public class BottomNavigationViewHelper {

    public static void  settingBottomNavigationViewListener(BottomNavigationViewEx bottomNavigationViewEx,
                                                            Context context, Resources resources) {

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(getOnNavigationItemSelectedListenerInstance (context, resources));
    }

        private static BottomNavigationView.OnNavigationItemSelectedListener
                         getOnNavigationItemSelectedListenerInstance(final Context context,
                                                                     final Resources resources) {

            BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                            switch (menuItem.getItemId()) {
                                case R.id.ic_home :
                                        Intent homeIntent = new Intent(context, HomeActivity.class);
                                    context.startActivity(homeIntent);
                                    break;
                                 case R.id.ic_events :
                                        Intent eventsIntent = new Intent(context, BookingsActivity.class);
                                    context.startActivity(eventsIntent);
                                    break;
                               /* case R.id.ic_add :
                                    Intent addEventIntent = new Intent(context, AddEventActivity.class);
                                context.startActivity(addEventIntent);
                                break;
                                case R.id.ic_chat :
                                    Intent chatIntent = new Intent(context, ChatActivity.class);
                                context.startActivity(chatIntent);
                                break;*/
                                case R.id.ic_map :
                                    Intent mapIntent = new Intent(context, MapActivity.class);
                                context.startActivity(mapIntent);
                                break;

                            }

                    return true;

                        }
                    };

            return onNavigationItemSelectedListener;
        }

}
