package lab.prada.android.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class HomeActivity extends Activity implements OnClickListener {

    protected static final int AR_FINISH_CAMERA = 1;
    private ListView mListView;
    private GalleryAdapter mAdapter;
    private GalleryManager mGalleryMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mListView = (ListView)findViewById(R.id.list_view);
        mAdapter = new GalleryAdapter(this);

        View header = LayoutInflater.from(this).inflate(R.layout.header_actions, null);
        header.findViewById(R.id.btn_create_circle).setOnClickListener(this);
        header.findViewById(R.id.btn_create_grid).setOnClickListener(this);

        mListView.addHeaderView(header);
        mListView.setAdapter(mAdapter);
        mGalleryMgr = GalleryManager.getInstance();
        refreshData();
    }

    private void refreshData() {
        mAdapter.clear();
        mAdapter.addAll(mGalleryMgr.getFiles());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
        case R.id.menu_refresh:
            refreshData();
            break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
        case AR_FINISH_CAMERA:
            refreshData();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
        case R.id.btn_create_circle:
            intent = new Intent(HomeActivity.this, CircleCameraActivity.class);
            HomeActivity.this.startActivityForResult(intent, AR_FINISH_CAMERA);
            break;
        case R.id.btn_create_grid:
            intent = new Intent(HomeActivity.this, GridCameraActivity.class);
            HomeActivity.this.startActivityForResult(intent, AR_FINISH_CAMERA);
            break;
        }
    }
}
