package com.weidiao.print.util;

import android.app.Activity;
import android.content.Context;


import java.util.Stack;

public class AppActivityManager {
    private static Stack<Activity> activityStack;
    private static AppActivityManager instance;

    public static AppActivityManager getAppManager() {
        if (instance == null) {
            synchronized (AppActivityManager.class) {
                if (instance == null) {
                    instance = new AppActivityManager();
                }
            }
        }

        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }
        activityStack.push(activity);
    }


    public Stack<Activity> getActivityStack() {
        return activityStack == null ? new Stack<Activity>() : activityStack;
    }

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    public Activity removeActivity(Activity activity) {
        if (activityStack != null) {
            activityStack.remove(activity);
        }
        return activity;
    }

    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }


    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }


    public void finishActivityTop(Class<?> cls) {
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (activityStack.get(i).getClass().equals(cls)) {
                finishActivity(activity);
                return;
            }
        }
    }


    //结束栈中指定Activity上面的所有Activity
    public void finishTopAllActivityButFirst(Class<?> cls) {
        int size = activityStack.size();
        Stack<Activity> tempStack = new Stack<>();
        boolean actExist = false;
        for (int i = size - 2; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            tempStack.add(activity);
            if (activityStack.get(i).getClass().equals(cls)) {
                actExist = true;
                break;
            }
        }

        if (actExist) {
            for (int i = 0; i < tempStack.size(); i++) {
                Activity activity = tempStack.get(i);
                activity.finish();
            }
        }
    }

    /**
     * 结束栈中指定Activity上面的所有Activity，不包含指定的Activity
     * @param cls
     * @param offset 跳过上面几个
     */
    public void finishTopAllActivityUntil(Class<?> cls, int offset) {
        int size = activityStack.size();
        Stack<Activity> tempStack = new Stack<>();
        boolean actExist = false;
        for (int i = size - 1 - offset; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (activityStack.get(i).getClass().equals(cls)) {
                actExist = true;
                break;
            }else {
                tempStack.add(activity);
            }
        }

        if (actExist) {
            for (int i = 0; i < tempStack.size(); i++) {
                Activity activity = tempStack.get(i);
                activity.finish();
            }
        }
    }





    public void finishAllActivity() {
        if (activityStack == null) return;
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
        System.exit(0);
    }

    public void finishAllActivityButFirst() {
        if (activityStack == null) return;
        for (int i = 0; i < activityStack.size() - 1; i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
            }
        }


        Activity activity = activityStack.get(activityStack.size() - 1);
        activityStack.clear();
        activityStack.add(activity);
    }

    public void appExit(Context context) {
        try {
            finishAllActivity();
            // AppActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.restartPackage(context.getPackageName());
//            System.exit(0);
        } catch (Exception localException) {
        }
    }


    /**
     * 调用此方法用于退出整个Project
     */
    public void exit() {
        try {
            for (Activity activity : activityStack) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }


}