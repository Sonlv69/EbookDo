package com.kiluss.ebookdo;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_download)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }





//    // search view
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //bookPreviewAdapter.getFilter().filter(query);
//                searchViewText = query;
//                searchViewText = processSearchInput(query);
//                new DownloadTask().execute(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //bookPreviewAdapter.getFilter().filter(newText);
//                //searchViewText = newText;
//                return false;
//            }
//        });
//        return true;
//    }
//
//    public String processSearchInput(String input) {
//        String result = "";
//        String[] separate = input.split(" ");
//        int count = 0;
//        for (String s : separate) {
//            Log.i("test",s);
//            Log.i("count",Integer.toString(count));
//            count++;
//        }
//        return result;
//    }
//
//    // an back de tat search view khong thoat app
//    @Override
//    public void onBackPressed() {
//        if(!searchView.isIconified()) {
//            searchView.setQuery("", false);
//            searchView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }
}