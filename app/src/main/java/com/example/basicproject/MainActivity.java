package com.example.basicproject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity
{

    private ArrayList<String> mToDoList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ToDoListAdapter mAdapter;
    private Fragment sortfragment;
    private FragmentTransaction fragmentTransaction;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private static boolean FragmentVisible;
    private listener lis = new listener();
    private DatabaseHelper mDatabeseHelper;
    private static boolean sortedList;

    @SuppressLint("RestrictedApi")
    private void SetFragmentVisible() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
        fragmentTransaction.show(sortfragment);
        fragmentTransaction.commit();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(lis);
    }

    private class listener implements OnClickListener {
        @Override
        public void onClick(View v) {
            FragmentVisible = false;
            MainActivity.this.recreate();
        }
    }

    @SuppressLint("RestrictedApi")
    private void SetListVisible() {
        mRecyclerView.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.VISIBLE);
        fragmentTransaction.hide(sortfragment);
        fragmentTransaction.commit();
        if (sortedList)
            Collections.sort(mToDoList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        mRecyclerView = findViewById(R.id.recyclerview);
        mDatabeseHelper = new DatabaseHelper(this);

        if (savedInstanceState != null) {
            mToDoList = savedInstanceState.getStringArrayList(getString(R.string.ToDoListArrayKey));
            mAdapter = new ToDoListAdapter(MainActivity.this, mToDoList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            new Thread(new Runnable() {
                public void run() {
                    mToDoList = mDatabeseHelper.SelectAllToDos();
                    mAdapter = new ToDoListAdapter(MainActivity.this, mToDoList);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.recreate();
                        }
                    });
                }
            }).start();
            FragmentVisible = false;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(mToDoList, from, to);
                mAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                String Item = mToDoList.get(viewHolder.getAdapterPosition());
                mToDoList.remove(viewHolder.getAdapterPosition());
                mDatabeseHelper.deleteItem(Item);
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        helper.attachToRecyclerView(mRecyclerView);

        sortfragment = new settings();
        fragmentTransaction = getFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
            fragmentTransaction.add(R.id.layout, sortfragment, getString(R.string.settings_fragment_tag));
        } else {
            sortfragment = getFragmentManager().findFragmentByTag(getString(R.string.settings_fragment_tag));
        }
        if (FragmentVisible) {
            SetFragmentVisible();
        } else {
            SetListVisible();
        }
    }

    @Override
    public void onBackPressed() {
        if (FragmentVisible) {
            FragmentVisible = false;
            this.recreate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void AddItemToList(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.AddItemPopupTitle);
        builder.setMessage(R.string.AddItemPopupMessage);

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(R.string.PositiveButtonsText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = input.getText().toString();
                mDatabeseHelper.addItem(item);
                addToList(item);
                if (sortedList)
                    mAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(R.string.NegativeButtonsText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(getString(R.string.ToDoListArrayKey), mToDoList);

    }

    public void settingsHandler(MenuItem item) {
        FragmentVisible = true;
        this.recreate();
    }

    private void addToList(String Item) {
        mToDoList.add(Item);
        if (sortedList)
            Collections.sort(mToDoList);
    }

    public static class settings extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            SwitchPreference switchPreference = (SwitchPreference) findPreference(getString( R.string.Sort_preference_key));
            sortedList = switchPreference.isChecked();
        }
    }

}
