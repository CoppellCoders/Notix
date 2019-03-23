/**
 * ImagePrint for printing
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */
package ml.coppellcoders.notixbus.printprocess;

import android.content.Context;
import android.graphics.Bitmap;

import com.brother.ptouch.sdk.PrinterInfo.ErrorCode;

import java.util.ArrayList;

public class ImagePrint extends BasePrint {

    private Bitmap mImage;

    public ImagePrint(Context context) {
        super(context);
    }

    /**
     * set print data
     */
    public Bitmap getFiles() {
        return mImage;
    }

    /**
     * set print data
     */
    public void setFiles(Bitmap files) {
        mImage = files;
    }

    /**
     * do the particular print
     */
    @Override
    protected void doPrint() {







            mPrintResult = mPrinter.printImage(mImage);

            // if error, stop print next files
            if (mPrintResult.errorCode != ErrorCode.ERROR_NONE) {

                return;
                }

    }

}