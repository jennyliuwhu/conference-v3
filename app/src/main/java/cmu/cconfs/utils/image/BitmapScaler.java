package cmu.cconfs.utils.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import cmu.cconfs.CConfsApplication;

public class BitmapScaler
{
	// scale and keep aspect ratio
	public static Bitmap scaleToFitWidth(Bitmap b, int width)
	{
		float factor = width / (float) b.getWidth();
		return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
	}


	// scale and keep aspect ratio
	public static Bitmap scaleToFitHeight(Bitmap b, int height)
	{
		float factor = height / (float) b.getHeight();
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
	}


	// scale and keep aspect ratio
	public static Bitmap scaleToFill(Bitmap b, int width, int height)
	{
		float factorH = height / (float) b.getWidth();
		float factorW = width / (float) b.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), 
		  (int) (b.getHeight() * factorToUse), true);
	}


	// scale and don't keep aspect ratio
	public static Bitmap strechToFill(Bitmap b, int width, int height)
	{
		float factorH = height / (float) b.getHeight();
		float factorW = width / (float) b.getWidth();
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorW), 
		  (int) (b.getHeight() * factorH), true);
	}

    public static RoundedBitmapDrawable scaleToCircle(Bitmap b, int radius) {
        RoundedBitmapDrawable rd = RoundedBitmapDrawableFactory.create(CConfsApplication.getInstance().getResources(), b);
        rd.setCornerRadius(radius);
        rd.setCircular(true);
        return rd;
    }

}