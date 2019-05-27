package and.conachegroup.dramaitalia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import and.conachegroup.dramaitalia.fragments.Announced2016Fragment;
import and.conachegroup.dramaitalia.fragments.Announced2017Fragment;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import and.conachegroup.dramaitalia.DramaItalia;
import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.app.Documents;
import and.conachegroup.dramaitalia.fragments.HomeFragment;
import and.conachegroup.dramaitalia.fragments.JapanDramasFragment;
import and.conachegroup.dramaitalia.fragments.JapanFilmsFragment;
import and.conachegroup.dramaitalia.fragments.KoreanDramasFragment;
import and.conachegroup.dramaitalia.fragments.KoreanFilmsFragment;
import and.conachegroup.dramaitalia.fragments.LatestAnnouncedFragment;
import and.conachegroup.dramaitalia.fragments.LatestCompletedFragment;
import and.conachegroup.dramaitalia.fragments.LatestReleasedFragment;
import and.conachegroup.dramaitalia.fragments.OtherDramasFragment;
import and.conachegroup.dramaitalia.fragments.OtherFilmsFragment;
import and.conachegroup.dramaitalia.tasks.RetrieveDocuments;
import and.conachegroup.dramaitalia.utils.Constants;
import and.conachegroup.dramaitalia.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, Constants {

    private static final String TAG = "MainActivity";

    private Context mContext;
    private Fragment mCurrentFragment;
    private DrawerLayout mDrawer;
    private boolean mLoadDocumentsAtBoot = true;
    private NavigationView mNavigationView;
    private SharedPreferences mPrefs;
    private RetrieveDocuments mRetrieveDocuments;
    private SearchView mSearchView;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init
        mPrefs = DramaItalia.getInstance().prefs;
        mContext = this;
        boolean isFirstRun = mPrefs.getBoolean("first_run", true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set drawer
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void onDrawerStateChanged(int i) {
                super.onDrawerStateChanged(i);
                if (mSearchView != null) {
                    mSearchView.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(
                            mSearchView.getWindowToken(), 0);
                }
            }
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // select home item menu
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.setNavigationItemSelectedListener(this);

        View headerView = mNavigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationView.setCheckedItem(R.id.nav_home);
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                }
                mCurrentFragment = showFragment(R.id.nav_home);
            }
        });

        // set swipe layout
        mSwipeLayout = findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent);

        // set home fragment
        if (savedInstanceState == null) mCurrentFragment = showFragment(R.id.nav_home);

        // check for internet connection
        if (!Utils.isConnected(getApplicationContext())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.app_name));
            alertDialog.setMessage(getString(R.string.no_connection));
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            alertDialog.show();
        }

        // retrieve documents
        mLoadDocumentsAtBoot =
                mPrefs.getBoolean(getString(R.string.key_load_documents_at_boot), true);
        mRetrieveDocuments = new RetrieveDocuments();
        if (isFirstRun) {
            TapTargetView.showFor(this, TapTarget.forToolbarNavigationIcon(toolbar,
                    getString(R.string.welcome),
                    getString(R.string.tutorial)).cancelable(false),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            mDrawer.openDrawer(GravityCompat.START);
                            if (mLoadDocumentsAtBoot) {
                                retrieveDocuments();
                            } else {
                                killAsyncTasks();
                            }
                        }
                    });
            mPrefs.edit().putBoolean("first_run", false).apply();
        } else {
            if (mLoadDocumentsAtBoot) {
                Snackbar.make(mDrawer, getString(R.string.loading_documents),
                        Snackbar.LENGTH_SHORT).show();
                retrieveDocuments();
            } else {
                killAsyncTasks();
            }
        }
    }

    private void retrieveDocuments() {
        mRetrieveDocuments.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);

        if (mSearchView != null) {
            mSearchView.clearFocus();
        }

        if (mCurrentFragment != null && mCurrentFragment.getTag() != null) {
            switch (mCurrentFragment.getTag()) {
                case URL_LATEST_RELEASED:
                    Documents.setLatestReleased(null);
                    break;
                case URL_LATEST_ANNOUNCED:
                    Documents.setLatestAnnounced(null);
                    break;
                case URL_LATEST_COMPLETED:
                    Documents.setLatestCompleted(null);
                    break;
                case URL_DRAMA_JAPAN:
                    Documents.setJapanDrama(null);
                    break;
                case URL_DRAMA_KOREA:
                    Documents.setKoreanDrama(null);
                    break;
                case URL_DRAMA_OTHER:
                    Documents.setOtherDrama(null);
                    break;
                case URL_FILM_JAPAN:
                    Documents.setJapanFilm(null);
                    break;
                case URL_FILM_KOREA:
                    Documents.setKoreanFilm(null);
                    break;
                case URL_FILM_OTHER:
                    Documents.setOtherFilm(null);
                    break;
                case URL_ANNOUNCED_2017:
                    Documents.setAnnounced2017(null);
                    break;
                case URL_ANNOUNCED_2016:
                    Documents.setAnnounced2016(null);
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(mCurrentFragment);
            transaction.attach(mCurrentFragment);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mSearchView != null && !mSearchView.isIconified()) {
                mSearchView.setQuery("", false);
                mSearchView.setIconified(true);
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                        mCurrentFragment.getClass() != HomeFragment.class) {
                    // set home fragment
                    if (mNavigationView != null)
                        mNavigationView.setCheckedItem(R.id.nav_home);
                    mCurrentFragment = showFragment(R.id.nav_home);
                    Toast.makeText(mContext, getString(R.string.press_to_exit), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // close app
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // handle navigation view item clicks
        mCurrentFragment = showFragment(item.getItemId());

        // close drawer
        mDrawer = findViewById(R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);

        mNavigationView.setCheckedItem(item.getItemId());

        return true;
    }

    public Fragment showFragment(int viewId) {
        Fragment fragment = null;
        String title;
        String url = null;

        // switch fragments
        switch (viewId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                title = getString(R.string.home);
                break;
            case R.id.nav_latest_released:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(LatestReleasedFragment.class)) {
                    fragment = new LatestReleasedFragment();
                    url = Constants.URL_LATEST_RELEASED;
                }
                title = getString(R.string.latest_released);
                break;
            case R.id.nav_latest_announced:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(LatestAnnouncedFragment.class)) {
                    fragment = new LatestAnnouncedFragment();
                    url = Constants.URL_LATEST_ANNOUNCED;
                }
                title = getString(R.string.latest_announced);
                break;
            case R.id.nav_latest_completed:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(LatestCompletedFragment.class)) {
                    fragment = new LatestCompletedFragment();
                    url = Constants.URL_LATEST_COMPLETED;
                }
                title = getString(R.string.latest_completed);
                break;
            case R.id.nav_japanese_dramas:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(JapanDramasFragment.class)) {
                    fragment = new JapanDramasFragment();
                    url = Constants.URL_DRAMA_JAPAN;
                }
                title = getString(R.string.japanese_dramas);
                break;
            case R.id.nav_korean_dramas:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(KoreanDramasFragment.class)) {
                    fragment = new KoreanDramasFragment();
                    url = Constants.URL_DRAMA_KOREA;
                }
                title = getString(R.string.korean_dramas);
                break;
            case R.id.nav_other_dramas:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(OtherDramasFragment.class)) {
                    fragment = new OtherDramasFragment();
                    url = Constants.URL_DRAMA_OTHER;
                }
                title = getString(R.string.other_dramas);
                break;
            case R.id.nav_japanese_films:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(JapanFilmsFragment.class)) {
                    fragment = new JapanFilmsFragment();
                    url = Constants.URL_FILM_JAPAN;
                }
                title = getString(R.string.japanese_films);
                break;
            case R.id.nav_korean_films:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(KoreanFilmsFragment.class)) {
                    fragment = new KoreanFilmsFragment();
                    url = Constants.URL_FILM_KOREA;
                }
                title = getString(R.string.korean_films);
                break;
            case R.id.nav_other_films:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(OtherFilmsFragment.class)) {
                    fragment = new OtherFilmsFragment();
                    url = Constants.URL_FILM_OTHER;
                }
                title = getString(R.string.other_films);
                break;
            case R.id.nav_announced_2017:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(Announced2017Fragment.class)) {
                    fragment = new Announced2017Fragment();
                    url = Constants.URL_ANNOUNCED_2017;
                }
                title = getString(R.string.announced_2017);
                break;
            case R.id.nav_announced_2016:
                if (mCurrentFragment != null && !mCurrentFragment.getClass()
                        .equals(Announced2016Fragment.class)) {
                    fragment = new Announced2016Fragment();
                    url = Constants.URL_ANNOUNCED_2016;
                }
                title = getString(R.string.announced_2016);
                break;
            default:
                // set home fragment
                fragment = new HomeFragment();
                title = getString(R.string.home);
                break;
        }

        // replace current fragment with new one
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            supportInvalidateOptionsMenu();

            // show animations
            if (mPrefs.getBoolean(getString(R.string.key_show_animations), true)) {
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            // update fragment
            transaction.replace(R.id.content_frame, fragment, url);
            transaction.addToBackStack(null);
            transaction.commit();

            // enable or disable swipe to refresh
            updateSwipeState(fragment);
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        return fragment != null ? fragment : mCurrentFragment;
    }

    private void updateSwipeState(Fragment fragment) {
        if (mSwipeLayout == null) return;

        if (fragment instanceof HomeFragment) {
            mSwipeLayout.setEnabled(false);
        } else {
            mSwipeLayout.setEnabled(true);
            mSwipeLayout.setOnRefreshListener(this);
        }
    }

    private void killAsyncTasks() {
        if(mRetrieveDocuments.getStatus().equals(AsyncTask.Status.RUNNING)) {
            Log.d(TAG, "killAsyncTasks() called");
            mRetrieveDocuments.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        killAsyncTasks();

        super.onDestroy();
    }
}
