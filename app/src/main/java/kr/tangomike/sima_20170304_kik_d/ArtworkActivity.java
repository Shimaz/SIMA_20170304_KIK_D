package kr.tangomike.sima_20170304_kik_d;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by shimaz on 2017-02-24.
 */


public class ArtworkActivity extends Activity {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private IntentFilter mFilter = new IntentFilter("shimaz.restart");

    private DataCollection dc;

    private ViewPager pager;
    private SimaPagerAdapter adapter;
    private Button btnHome;
    private Button btnCaption;

    private RelativeLayout rlMain;

    private ImageView ivCaption;

    private boolean isCaptionOn;
    private boolean isPageSet;
    private boolean isDimmed;


    private Animation fadeIn;
    private Animation fadeOut;

    private Animation rotate;
    private Animation rotateReverse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork);
        super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        registerReceiver(mReceiver, mFilter);

        dc = (DataCollection)getApplicationContext();
        dc.startTick();


        rlMain = (RelativeLayout)findViewById(R.id.activity_artwork);
        isPageSet = true;

        btnHome = (Button)findViewById(R.id.btn_home);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);

            }
        });

        isCaptionOn = false;
        isDimmed = false;

        btnCaption = (Button)findViewById(R.id.btn_caption);
        btnCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dc.resetTimer();

                if(isPageSet){
                    if(isCaptionOn){
                        HideCaption();
                        btnCaption.startAnimation(rotateReverse);
                    }else{
                        ShowCaption();
                        btnCaption.startAnimation(rotate);
                    }
                }




            }
        });






        adapter = new SimaPagerAdapter();



        pager = (ViewPager)findViewById(R.id.vp_artwork);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);

        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                dc.resetTimer();

                switch (state){
                    case 0: // page set
                        isPageSet = true;
                        isDimmed = false;
                        if(isCaptionOn) UndimCaption();

                        break;

                    case 1: // start scroll
                        isPageSet = false;
                        if(isCaptionOn & !isDimmed) {
                            DimCaption();
                            isDimmed = true;
                        }

                        break;

                    case 2: // on scroll
                        isPageSet = false;

                        break;


                    default:
                        break;

                }



            }
        };

        pager.addOnPageChangeListener(listener);






        ivCaption = (ImageView)findViewById(R.id.iv_caption);




        fadeIn = AnimationUtils.loadAnimation(this, R.anim.page_fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.page_fade_out);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ivCaption.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateReverse = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse);


    }




    private void ShowCaption(){
        isCaptionOn = true;

        ivCaption.setVisibility(View.VISIBLE);
        ivCaption.setImageResource(getResources().getIdentifier("artwork_img_" + pager.getCurrentItem() + "_caption", "drawable", getPackageName()));

        ivCaption.startAnimation(fadeIn);

    }

    private void HideCaption(){
        isCaptionOn = false;

        ivCaption.startAnimation(fadeOut);

    }

    private void DimCaption(){

        btnCaption.setAlpha(0.5f);
        ivCaption.startAnimation(fadeOut);

    }

    private void UndimCaption(){

        btnCaption.setAlpha(1.0f);
        ivCaption.setVisibility(View.VISIBLE);
        ivCaption.setImageResource(getResources().getIdentifier("artwork_img_" + pager.getCurrentItem() + "_caption", "drawable", getPackageName()));
        ivCaption.startAnimation(fadeIn);
    }

    public class SimaPagerAdapter extends PagerAdapter {


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = getLayoutInflater().inflate(R.layout.layout_page, container, false);
            ImageView iv = (ImageView)page.findViewById(R.id.iv_page);
            iv.setImageResource(getResources().getIdentifier("artwork_img_" + position + "_bg", "drawable", getPackageName()));



            container.addView(page);

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return 11;
        }

        @Override
        public float getPageWidth(int position) {
            return 1.0f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }





        //        @Override
//        public int getCount() {
//            return 10;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return false;
//         }
//
//        public Object instantiateItem(View collection, int position) {
//
//
//            ImageView imgview = new ImageView(getBaseContext());
//            imgview.setImageResource(getResources().getIdentifier("artwork_img_"+position, "drawable", getPackageName()));
//
//            ((ViewPager)collection).addView(imgview, 0);
//
//
//
//
//            return imgview;
//        }

    }

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
