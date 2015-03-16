package cn.picksomething.getmyblog;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * @author caobin
 */
public class MainActivity extends Activity {

    private String[] mSorts;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomePage);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);
        findViews();
        initDatas();
        setListeners();
    }

    private void initDatas() {

        mSorts = getResources().getStringArray(R.array.sorts_array);
        clickItem(0);
    }

    private void setListeners() {
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_items, mSorts));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListner());
    }


    private void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class DrawerItemClickListner implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clickItem(position);
        }
    }

    private void clickItem(int position) {
        Fragment fragment = new SortsFragment();
        Bundle args = new Bundle();
        args.putInt(SortsFragment.ARG_SORT_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mSorts[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
