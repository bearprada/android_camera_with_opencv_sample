package lab.prada.android.test;

import com.cengalabs.flatui.views.FlatButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class HomeActivity extends Activity {

    private ListView mListView;
    private GalleryAdapter mAdapter;
    private GalleryManager mGalleryMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mListView = (ListView)findViewById(R.id.list_view);
        mAdapter = new GalleryAdapter(this);
        FlatButton button = new FlatButton(this);
        button.setText("CREATE!");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });
        mListView.addHeaderView(button);
        mListView.setAdapter(mAdapter);
        mGalleryMgr = GalleryManager.getInstance();
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
            mAdapter.clear();
            mAdapter.addAll(mGalleryMgr.getFiles());
            mAdapter.notifyDataSetChanged();
            break;
        }
        return false;
    }
}
