package ucast.com.padupdate.androidTools;

import android.app.Activity;
import android.os.Bundle;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import ucast.com.padupdate.R;

/**
 * Created by pj on 2016/11/25.
 */
@ContentView(R.layout.tishi_update)
public class Second  extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
