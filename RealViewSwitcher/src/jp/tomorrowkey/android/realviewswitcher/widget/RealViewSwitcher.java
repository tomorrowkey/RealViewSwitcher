
package jp.tomorrowkey.android.realviewswitcher.widget;

import java.util.HashMap;
import java.util.Map;

import jp.tomorrowkey.android.realviewswitcher.R;
import jp.tomorrowkey.android.realviewswitcher.util.MotionEventUtil;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

/**
 * ページのようなViewGroup<br>
 * 指に吸いついてページを遷移する<br>
 * さらにページは循環する
 * 
 * @author tomorrowkey@gmail.com
 */
public class RealViewSwitcher extends ViewGroup {

    public static final String LOG_TAG = RealViewSwitcher.class.getSimpleName();

    public static final int ACCELERATE_DECELERATE_INTERPOLATOR = 1;

    public static final int ACCELERATE_INTERPOLATOR = 2;

    public static final int ANTICIPATE_INTERPOLATOR = 3;

    public static final int ANTICIPATE_OVERSHOOT_INTERPOLATOR = 4;

    public static final int BOUNCE_INTERPOLATOR = 5;

    public static final int DECELERATE_INTERPOLATOR = 6;

    public static final int LINEAR_INTERPOLATOR = 7;

    public static final int OVERSHOOT_INTERPOLATOR = 8;

    /**
     * 加速度の計算に使用する単位
     */
    private static final int VELOCITY_UNIT = 100;

    /**
     * スワイプ方向を判定するのに、最低限必要な移動距離
     */
    private static final int COMPUTE_MIN_DIFF = 30;

    /**
     * 加速度の閾値<br>
     * この値を超える加速度でスクロールすると現在のスクロール具合に関係せず、ページスクロールする
     */
    private static final int VELOCITY_THRESHOLD = 100;

    /**
     * スクロールのリフレッシュレート<br>
     * 0でもいい気がする
     */
    private static final int REFRESH_INTERVAL = 10;

    /**
     * スクロール時間（デフォルト）
     */
    private static int SCROLL_DURATION_DEFAULT = 500;

    /**
     * スクロール時間（下限）
     */
    private static int SCROLL_DURATION_LOW_THRESHOLD = 100;

    /**
     * 表示するべきページ番号<br>
     * 最大3ページだろうが5という値が入る場合がある<br>
     * その時は2ページ目が表示されている
     */
    private int mCurrentPageIndex = 0;

    /**
     * 前回のタッチ位置<br>
     * スクロールに使用される
     */
    private float mPrevX;

    /**
     * タッチダウン位置
     */
    private PointF mDownPoint = new PointF();

    /**
     * ViewGroupの横幅
     */
    private int mScreenWidth;

    /**
     * ViewGroupの高さ
     */
    private int mScreenHeight;

    /**
     * 加速度計算クラス
     */
    private VelocityTracker mVelocityTracker;

    /**
     * スクロール計算クラス
     */
    private Scroller mScroller;

    /**
     * 子Viewのレイアウト情報を保持します<br>
     * 何度もView#layoutを呼び出すことを防ぐために使います
     */
    private Map<Integer, Rect> mChildViewLayoutMap = new HashMap<Integer, Rect>();

    public RealViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RealViewSwitcher);
        int interpolator = a.getInteger(R.styleable.RealViewSwitcher_interpolator,
                DECELERATE_INTERPOLATOR);
        setSwitchInterpolator(interpolator);

        mVelocityTracker = VelocityTracker.obtain();
    }

    public void setSwitchInterpolator(int interpolator) {
        switch (interpolator) {
            case ACCELERATE_DECELERATE_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
                break;
            case ACCELERATE_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new AccelerateInterpolator());
                break;
            case ANTICIPATE_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new AnticipateInterpolator());
                break;
            case ANTICIPATE_OVERSHOOT_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new AnticipateOvershootInterpolator());
                break;
            case BOUNCE_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new BounceInterpolator());
                break;
            case DECELERATE_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new DecelerateInterpolator());
                break;
            case LINEAR_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new LinearInterpolator());
                break;
            case OVERSHOOT_INTERPOLATOR:
                mScroller = new Scroller(getContext(), new OvershootInterpolator());
                break;
            default:
                throw new IllegalArgumentException("unknown interpolator id. id=" + interpolator);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mScreenWidth = getSuggestedMinimumWidth();
        mScreenHeight = getSuggestedMinimumHeight();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            child.measure(widthMeasureSpec, heightMeasureSpec);

            mScreenWidth = Math.max(mScreenWidth, child.getMeasuredWidth());
            mScreenHeight = Math.max(mScreenHeight, child.getMeasuredHeight());
        }

        setMeasuredDimension(mScreenWidth, mScreenHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        float scrollX = getScrollX();

        if (mScreenWidth == 0 || mScreenHeight == 0)
            return;

        if (childCount == 1) {
            // ページが一つしかなければ、要求された所のみにレイアウトする
            layoutChildViewIfNeed(0, left, top, right, bottom);
        } else if (childCount > 1) {
            // 左側のページの座標とインデックスの計算
            int leftX = (int)(Math.floor(scrollX / mScreenWidth) * mScreenWidth);
            int leftIndex = (leftX / mScreenWidth) % childCount;
            while (leftIndex < 0) {
                leftIndex += childCount;
            }
            // 右側のページの座標とインデックスの計算
            int rightX = leftX + mScreenWidth;
            int rightIndex = (leftIndex + 1) % childCount;

            // レイアウト
            layoutChildViewIfNeed(rightIndex, rightX, top, rightX + mScreenWidth, bottom);
            layoutChildViewIfNeed(leftIndex, leftX, top, leftX + mScreenWidth, bottom);
        }
    }

    /**
     * 子Viewをレイアウトします<br>
     * 現在レイアウトされている位置と異なる場合のみ、{@link View#layout(int, int, int, int)}を呼び出します
     * 
     * @param index
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void layoutChildViewIfNeed(int index, int left, int top, int right, int bottom) {
        Integer key = Integer.valueOf(index);
        Rect rect = mChildViewLayoutMap.get(key);
        boolean needLayout = false;
        if (rect == null) {
            needLayout = true;
            rect = new Rect(left, top, right, bottom);
            mChildViewLayoutMap.put(key, rect);
        } else {
            if (rect.left == left && rect.top == top && rect.right == right
                    && rect.bottom == bottom) {
                needLayout = false;
            } else {
                needLayout = true;
                rect.set(left, top, right, bottom);
            }
        }

        if (needLayout) {
            getChildAt(index).layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        super.onInterceptTouchEvent(event);

        if (MotionEventUtil.getPointerCount(event) > 1) {
            // マルチタッチ時にスクロールはしないので判定終了
            requestDisallowInterceptTouchEvent(true);
            return false;
        }

        // onTouch()のために加速度の計算を始める
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPrevX = event.getX();
            mDownPoint.x = event.getX();
            mDownPoint.y = event.getY();
        }

        // スクロールなのか、コンテンツにアクセスなのか判定する
        float diffX = Math.abs(event.getX() - mDownPoint.x);
        float diffY = Math.abs(event.getY() - mDownPoint.y);

        if (!mScroller.isFinished()) {
            // スクロール中なので、インターセプトする
            mScroller.abortAnimation();
            return true;
        } else if (Math.max(diffX, diffY) < COMPUTE_MIN_DIFF) {
            // まだ判定できる移動距離に達していないので見送り
            return false;
        } else if (diffX > (diffY * 2)) {
            // 横スクロールなので、インターセプトする
            mDownPoint.x = event.getX();
            mDownPoint.y = event.getY();
            return true;
        } else if (diffY > (diffX * 2)) {
            // 縦スクロールなので、これ以降の判定を止める
            requestDisallowInterceptTouchEvent(true);
            return false;
        } else {
            // 縦スクロールなのか、横スクロールなのか微妙なので見送り
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEventUtil.getPointerCount(event) > 1) {
            return true;
        } else if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }

        int action = event.getAction();

        // 加速度の処理
        if (action == MotionEvent.ACTION_DOWN) {
            mVelocityTracker.clear();

            mDownPoint.x = event.getX();
            mDownPoint.y = event.getY();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                requestLayout();
                scrollBy((int)(mPrevX - event.getX()), 0);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(VELOCITY_UNIT);
                float xVelocity = mVelocityTracker.getXVelocity();

                // 現在のスクロールピクセル数を取得する
                int currentDiffX = (mCurrentPageIndex * mScreenWidth) - getScrollX();

                // スクロールしている方向|加速度によって表示するページを変更する
                int nextPageIndex = mCurrentPageIndex;
                if (getChildCount() == 1) {
                    // 1ページしかなければ、スクロールしない
                    nextPageIndex = 0;
                } else if (currentDiffX < -(mScreenWidth * 0.5) || xVelocity < -VELOCITY_THRESHOLD) {
                    // 右ページにスクロール
                    nextPageIndex = mCurrentPageIndex + 1;
                } else if (currentDiffX > (mScreenWidth * 0.5) || xVelocity > VELOCITY_THRESHOLD) {
                    // 左ページにスクロール
                    nextPageIndex = mCurrentPageIndex - 1;
                }

                // ページ移動する
                switchPage(nextPageIndex, (int)(SCROLL_DURATION_DEFAULT - Math.abs(xVelocity)));

                break;
        }
        mPrevX = event.getX();

        return true;
    }

    private Runnable mScrollerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScroller.isFinished()) {
                if (mListener != null)
                    mListener.onSwitched(getPageIndex(), getChildCount());
                return;
            }

            if (mScroller.computeScrollOffset()) {
                int x = mScroller.getCurrX();
                int y = mScroller.getCurrY();
                scrollTo(x, y);
            }

            requestLayout();

            postDelayed(this, REFRESH_INTERVAL);
        }
    };

    /**
     * 左ページに移動します
     */
    public void switchLeftPage() {
        switchPage(getRawPageIndex() - 1);
    }

    /**
     * 右ページに移動します
     */
    public void switchRightPage() {
        switchPage(getRawPageIndex() + 1);
    }

    /**
     * ページ移動する
     * 
     * @param pageNo
     */
    public void switchPage(int pageIndex) {
        switchPage(pageIndex, SCROLL_DURATION_DEFAULT);
    }

    /**
     * ページ移動する
     * 
     * @param pageNo
     * @param duration
     */
    public void switchPage(int pageIndex, int duration) {
        if (duration < SCROLL_DURATION_LOW_THRESHOLD)
            duration = SCROLL_DURATION_LOW_THRESHOLD;

        int pageCount = getChildCount();

        // ページ番号を補正して保存
        mCurrentPageIndex = nearPage(pageCount, mCurrentPageIndex, pageIndex);

        // 遷移するページへのXの差分を計算する
        int moveScrollX = mCurrentPageIndex * mScreenWidth - getScrollX();

        if (mListener != null)
            mListener.onRequestSwitch(getPageIndex(), pageCount);

        // スクロールを開始する
        mScroller.startScroll(getScrollX(), 0, moveScrollX, 0, duration);
        post(mScrollerRunnable);
    }

    /**
     * ページ移動する アニメーションなし
     * 
     * @param pageNo
     * @param duration
     */
    public void switchPageDirectly(int pageIndex) {
        int pageCount = getChildCount();

        // ページ番号を補正して保存
        mCurrentPageIndex = nearPage(pageCount, mCurrentPageIndex, pageIndex);

        // 遷移するページへのXの差分を計算する
        int moveScrollX = mCurrentPageIndex * mScreenWidth - getScrollX();

        if (mListener != null)
            mListener.onRequestSwitch(getPageIndex(), pageCount);

        // スクロールする
        scrollBy(moveScrollX, 0);
        requestLayout();

        if (mListener != null)
            mListener.onSwitched(getPageIndex(), pageCount);
    }

    /**
     * 左ページに移動します<br>
     * アニメーションなし
     */
    public void switchLeftPageDirectly() {
        switchPageDirectly(getRawPageIndex() - 1);
    }

    /**
     * 右ページに移動します<br>
     * アニメーションなし
     */
    public void switchRightPageDirectly() {
        switchPageDirectly(getRawPageIndex() + 1);
    }

    /**
     * 現在のページから一番近いtargetページを返します
     * 
     * @param currentPage
     * @param targetPage
     * @return
     */
    private int nearPage(int pageCount, int currentPage, int targetPage) {
        if (targetPage < currentPage) {
            while (targetPage < currentPage) {
                targetPage += pageCount;
            }
            if ((targetPage - mCurrentPageIndex) >= (currentPage - (targetPage - pageCount))) {
                targetPage -= pageCount;
            }
        } else {
            while (targetPage > currentPage) {
                targetPage -= pageCount;
            }
            if ((currentPage - targetPage) >= ((targetPage + pageCount) - currentPage)) {
                targetPage += pageCount;
            }
        }

        return targetPage;
    }

    /**
     * ページ番号を返す<br>
     * ・最大ページ数を超えている場合 <br>
     * ・負数の場合<br>
     * がある
     * 
     * @return
     */
    public int getRawPageIndex() {
        return mCurrentPageIndex;
    }

    /**
     * ページ番号を返す<br>
     * 現在表示しているページ番号を返す<br>
     * 最大ページ数内に収まる正数を返す
     * 
     * @return
     */
    public int getPageIndex() {
        int pageIndex = mCurrentPageIndex;
        int childCount = getChildCount();
        while (pageIndex < 0) {
            pageIndex += childCount;
        }
        return pageIndex % childCount;
    }

    /**
     * リスナ
     */
    private OnViewSwitchListener mListener;

    /**
     * リスナーを設定します
     * 
     * @param l
     */
    public void setOnViewSwitchListener(OnViewSwitchListener l) {
        mListener = l;
    }

    /**
     * リスナーインターフェイス
     * 
     * @author tomorrowkey@gmail.com
     */
    public interface OnViewSwitchListener {
        void onRequestSwitch(int pageIndex, int pageCount);

        void onSwitched(int pageIndex, int pageCount);
    }

}
