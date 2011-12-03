
package jp.tomorrowkey.android.realviewswitcher.util;

import java.lang.reflect.InvocationTargetException;

import android.os.Build;
import android.view.MotionEvent;

/**
 * MotionEventのユーティリティ
 * 
 * @author tomorrowkey@gmail.com
 */
public class MotionEventUtil {

    public static final String LOG_TAG = MotionEventUtil.class.getSimpleName();

    /**
     * MotionEventからポインタの数を抽出する
     * 
     * @param event
     * @return
     */
    public static int getPointerCount(MotionEvent event) {
        int pointerCount;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR) {
                pointerCount = 1;
            } else {
                pointerCount = ((Integer)MotionEvent.class.getMethod("getPointerCount",
                        new Class[0]).invoke(event, new Object[0])).intValue();
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return pointerCount;
    }

    /**
     * MotionEventのgetX(:int)を実行する
     * 
     * @param event
     * @return
     */
    public static float getX(MotionEvent event, int index) {
        float value;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR) {
                if (index == 0) {
                    value = event.getX();
                } else {
                    value = 0;
                }
            } else {
                value = ((Float)(event.getClass().getMethod("getX", new Class[] {
                    int.class
                }).invoke(event, new Object[] {
                    Integer.valueOf(index)
                }))).floatValue();
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    /**
     * MotionEventのgetY(:int)を実行する
     * 
     * @param event
     * @return
     */
    public static float getY(MotionEvent event, int index) {
        float value;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR) {
                if (index == 0) {
                    value = event.getX();
                } else {
                    value = 0;
                }
            } else {
                value = ((Float)(event.getClass().getMethod("getY", new Class[] {
                    int.class
                }).invoke(event, new Object[] {
                    Integer.valueOf(index)
                }))).floatValue();
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}
