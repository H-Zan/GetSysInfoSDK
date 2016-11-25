package com.admai.sdk.view;

/**
 * Created by ZAN on 16/8/26.
 */
public class FullScreenVideo{
//
//    private static final String TAG = "FV";
//    private RelativeLayout mRootLayout;
//    private int mScreenWidth;
//    private int mScreenHeight;
//    private int currentOrientation;
//    private MaiVideoView maiVideoView;
//    private MaiListener maiListener;
//    private int currentPosition;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        L.e(TAG, "onCreate");
//         init();
////
////        initView();
////        initData();
//        createUI();
//    }
//
//    private void init() {
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        getWindow().setAttributes(params);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        currentOrientation = this.getResources().getConfiguration().orientation;
//
//        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
//        mScreenWidth = displayMetrics.widthPixels;
//        mScreenHeight = displayMetrics.heightPixels;
//
//        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
//
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//        }
//        L.e(TAG, mScreenWidth + "onCreate2" + mScreenHeight);
//
//
//    }
//
//    private void initView() {
//
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        getWindow().setAttributes(params);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//
//        currentOrientation = this.getResources().getConfiguration().orientation;
//
//        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
//        mScreenWidth = displayMetrics.widthPixels;
//        mScreenHeight = displayMetrics.heightPixels;
//
////
////        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
////
////            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
////
////        }
//        L.e(TAG, mScreenWidth + "onCreate2" + mScreenHeight);
//    }
//
//    private void initData() {
////        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//        RelativeLayout relativeLayout = new RelativeLayout(this);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        relativeLayout.setBackgroundColor(Color.BLUE);
//        relativeLayout.setLayoutParams(layoutParams);
//
//
//        MaiVideoView maiVideoView = null;
//        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
//            maiVideoView = new MaiVideoView(this, mScreenHeight, mScreenWidth);
//
//        } else {
//            maiVideoView = new MaiVideoView(this, mScreenWidth, mScreenHeight);
//
//        }
//
//
//        maiVideoView.setLayoutParams(layoutParams);
//        relativeLayout.addView(maiVideoView);
//        Intent intent = getIntent();
//        currentPosition = intent.getIntExtra("currentPosition", 0);
//        String url = intent.getStringExtra("url");
//
//        Uri uri = Uri.parse(url.trim());
//        maiVideoView.setVideoURI(uri);
//        maiVideoView.bringToFront();
//        maiVideoView.start();
//        this.setContentView(relativeLayout);
//    }
//
//    private void createUI() {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        this.createRootLayout(params);
//        this.createMaiVideoView(params);
//        this.setContentView(mRootLayout);
//
//    }
//
//    private void createRootLayout(RelativeLayout.LayoutParams params) {
//
//        mRootLayout = new RelativeLayout(this);
//        mRootLayout.setLayoutParams(params);
//        mRootLayout.setPadding(0, 0, 0, 0);
//        mRootLayout.setBackgroundColor(Color.BLACK);
//        mRootLayout.setBackgroundColor(Color.BLUE);
//
//
//    }
//
//    private void createMaiVideoView(RelativeLayout.LayoutParams params) {
//        Intent intent = getIntent();
//        currentPosition = intent.getIntExtra("currentPosition", 0);
//        String url = intent.getStringExtra("url");
//        Uri uri = Uri.parse(url.trim());
//
//
//
//
//
//        maiVideoView = new MaiVideoView(this);
//
//        maiVideoView.setLayoutParams(params);
//        maiVideoView.bringToFront();
//        maiVideoView.setVideoURI(uri);
//        mRootLayout.addView(maiVideoView);
//        maiVideoView.seekTo(currentOrientation);
//        maiVideoView.start();
//    }
//
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (currentPosition!=0) {
//            maiVideoView.seekTo(currentPosition);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        currentPosition = maiVideoView.getCurrentPosition();
////        maiVideoView.getPosition(currentPosition);
//        MaiAdapter maiAdapter = new MaiAdapter(this);
//        maiAdapter.setCurrentPosition(currentPosition);
//        maiAdapter=null;
//
//    }
}
