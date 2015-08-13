package world.ignite.piczilla.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import world.ignite.piczilla.R;
import world.ignite.piczilla.nodes.HttpRequestManager;
import world.ignite.piczilla.nodes.ImageNode;
import world.ignite.piczilla.nodes.JSONParser;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private ImageView mImageView;
    private Button mNextButton;
    private Button mPrevButton;
    private TextView indexView;
    private int mCurrentIndex;
    private static String LOG_TAG = "HOME_FRAGMENT";
    private static String KEY_INDEX = "INDEX";
    private static String baseString = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&start=";
    private int baseIndex = 0;
    private int totalIndex = 1;
    private Activity mActivity;
    private List<ImageNode> imagesList;
    private ProgressBar pb;

   // private HashMap<String, Bitmap> drawableMap;
    private LruCache<String, Bitmap> cache;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
        mActivity = getActivity();
        //drawableMap = new HashMap<String, Bitmap>();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory/8;

        cache = new LruCache<>(cacheSize);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        bindViews();
        return view;
    }

    private void initViews(View rootView) {
        mImageView = (ImageView) rootView.findViewById(R.id.iv_image_holder);
        mNextButton = (Button) rootView.findViewById(R.id.btn_next);
        mPrevButton = (Button) rootView.findViewById(R.id.btn_prev);
        indexView = (TextView) rootView.findViewById(R.id.index_text_view);
        pb = (ProgressBar) rootView.findViewById(R.id.progress);
    }

    private void bindViews() {
        mNextButton.setOnClickListener(this);
        mPrevButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                updateNextImage();
                break;
            case R.id.btn_prev:
                updatePreviousImage();
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (imagesList == null) {
            if (isOnline()) {
                requestData(baseString+baseIndex);
            } else {
                Toast.makeText(mActivity, "Network isn't available", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    private void updateNextImage(){
        pb.setVisibility(View.GONE);
        mPrevButton.setEnabled(true);
        totalIndex++;
        indexView.setText(""+totalIndex);
        if(totalIndex%4 == 1){
            baseIndex++;
            requestData(baseString+baseIndex);
        }
        int tempInt = (totalIndex-1)%4;
        ImageNode tempNode = imagesList.get(tempInt);
        final Bitmap b = cache.get(tempNode.getImageURL());
        if(b!=null)
            mImageView.setImageBitmap(b);
        else{
        LazyImageLoadTask mTask = new LazyImageLoadTask();
        mTask.execute(tempNode);
        }
    }

    private void updatePreviousImage(){
        pb.setVisibility(View.GONE);
        totalIndex--;
        indexView.setText(""+totalIndex);
        if(totalIndex%4 == 0){
            baseIndex--;
            requestData(baseString+baseIndex);
        }
        int tempInt = (totalIndex-1)%4;
        ImageNode tempNode = imagesList.get(tempInt);
        if(baseIndex == 0 && totalIndex == 1){
            mPrevButton.setEnabled(false);
        }
        final Bitmap b = cache.get(tempNode.getImageURL());
        if(b!=null)
        mImageView.setImageBitmap(b);
        else{
            LazyImageLoadTask mTask = new LazyImageLoadTask();
            mTask.execute(tempNode);
        }

    }

    private class MyTask extends AsyncTask<String, String, List<ImageNode>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<ImageNode> doInBackground(String... params) {

            String content = HttpRequestManager.getData(params[0]);
            imagesList = JSONParser.parseFeed(content);
            return imagesList;
        }

        @Override
        protected void onPostExecute(List<ImageNode> result) {
        }
    }


    private class LazyImageLoadTask extends AsyncTask<ImageNode, Void, Bitmap>{

        ImageNode tempNode;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(ImageNode... params) {
            tempNode = params[0];
            try {
                String imageUrl = tempNode.getImageURL();
                InputStream in = (InputStream)new URL(imageUrl).getContent();
                final Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                cache.put(imageUrl,bitmap);
                return bitmap;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            pb.setVisibility(View.GONE);
            mImageView.setImageBitmap(result);
            //.put(tempNode.getImageURL(), result);
            super.onPostExecute(result);
        }
    }

}
