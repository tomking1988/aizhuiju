package com.tomking.aizhuiju.common;

/**
 * Created by xtang on 13-10-13.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Set font of a view or return typeface of a font
 */
public class FontManager {

    private static Typeface robotoTypeFace;

    public static void setRobotoFont (Context context, View view)
    {
        if (robotoTypeFace == null)
        {
            robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        }
        setFont(view, robotoTypeFace);
    }

    private static void setFont (View view, Typeface robotoTypeFace)
    {
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
            {
                setFont(((ViewGroup)view).getChildAt(i), robotoTypeFace);
            }
        }
        else if (view instanceof TextView)
        {
            ((TextView) view).setTypeface(robotoTypeFace);
        }
    }

    public static void setRobotoLight(Context context, View view){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        setFont(view, robotoTypeFace);
    }

    public static void setRobotoRegular(Context context, View view){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");

        setFont(view, robotoTypeFace);
    }

    public static void setRobotoMedium(Context context, View view){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");

        setFont(view, robotoTypeFace);
    }

    public static Typeface getRobotoThin(Context context){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        return robotoTypeFace;
    }

    public static Typeface getRobotoLight(Context context){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        return robotoTypeFace;
    }

    public static Typeface getRobotoCondensedLight(Context context){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf");
        return robotoTypeFace;
    }

    public static Typeface getRobotoRegular(Context context){
        robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        return robotoTypeFace;
    }
}
