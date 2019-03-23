/**
 * BasePrint for printing
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package ml.coppellcoders.notixbus.printprocess;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterInfo.ErrorCode;
import com.brother.ptouch.sdk.PrinterInfo.Model;
import com.brother.ptouch.sdk.PrinterStatus;


@SuppressWarnings("ALL")
public abstract class BasePrint {
    private static final long BLE_RESOLVE_TIMEOUT = 5000;
    static Printer mPrinter;
    static boolean mCancel;

 //   private final SharedPreferences sharedPreferences;
    private final Context mContext;
    PrinterStatus mPrintResult;
    private String customSetting;
    private PrinterInfo mPrinterInfo;
    private ProgressDialog pdia;

    BasePrint(Context context) {

        mContext = context;


        mCancel = false;
        // initialization for print
        mPrinterInfo = new PrinterInfo();
        mPrinter = new Printer();
        mPrinterInfo = mPrinter.getPrinterInfo();

    }

    public static void cancel() {
        if (mPrinter != null)
            mPrinter.cancel();
        mCancel = true;
    }

    protected abstract void doPrint();

    /**
     * set PrinterInfo
     */
    public void setPrinterInfo() {

        getPreferences();
        setCustomPaper();
        mPrinter.setPrinterInfo(mPrinterInfo);
        if (mPrinterInfo.port == PrinterInfo.Port.USB) {
            while (true) {
                if (Common.mUsbRequest != 0)
                    break;
            }
            if (Common.mUsbRequest != 1) {
            }
        }
    }

    /**
     * get PrinterInfo
     */
    public PrinterInfo getPrinterInfo() {
        getPreferences();
        return mPrinterInfo;
    }

    /**
     * get Printer
     */
    public Printer getPrinter() {

        return mPrinter;
    }

    /**
     * get Printer
     */
    public PrinterStatus getPrintResult() {
        return mPrintResult;
    }

    /**
     * get Printer
     */
    public void setPrintResult(PrinterStatus printResult) {
        mPrintResult = printResult;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        mPrinter.setBluetooth(bluetoothAdapter);
        mPrinter.setBluetoothLowEnergy(mContext, bluetoothAdapter, BLE_RESOLVE_TIMEOUT);
    }

    @TargetApi(12)
    public UsbDevice getUsbDevice(UsbManager usbManager) {
        return mPrinter.getUsbDevice(usbManager);
    }

    /**
     * get the printer settings from the SharedPreferences
     */
    private void getPreferences() {
        if (mPrinterInfo == null) {
            mPrinterInfo = new PrinterInfo();
            return;
        }
        String input;
        mPrinterInfo.printerModel = Model.QL_820NWB;
        mPrinterInfo.port = PrinterInfo.Port.BLUETOOTH;
        mPrinterInfo.macAddress = "A4:34:F1:FC:26:74";
        if (isLabelPrinter(mPrinterInfo.printerModel)) {
            mPrinterInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
            switch (mPrinterInfo.printerModel) {
                case QL_820NWB:
                    mPrinterInfo.labelNameIndex = LabelInfo.QL700.W62RB.ordinal();
                    mPrinterInfo.isAutoCut = true;
                    mPrinterInfo.isCutAtEnd = true;
                    break;
                default:
                    break;
            }
        }
        mPrinterInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;
        input = "1";
        if (input.equals(""))
            input = "1";
        mPrinterInfo.numberOfCopies = Integer.parseInt(input);
        mPrinterInfo.halftone = PrinterInfo.Halftone.PATTERNDITHER;
        mPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;

        mPrinterInfo.pjCarbon = true;

            input = "5";
        mPrinterInfo.pjDensity = Integer.parseInt(input);
        mPrinterInfo.pjFeedMode = PrinterInfo.PjFeedMode.PJ_FEED_MODE_FREE;

        mPrinterInfo.align = PrinterInfo.Align.CENTER;

            input = "0";
        mPrinterInfo.margin.left = Integer.parseInt(input);
        mPrinterInfo.valign = PrinterInfo.VAlign.TOP;

            input = "0";
        mPrinterInfo.margin.top = Integer.parseInt(input);

            input = "0";
        mPrinterInfo.customPaperWidth = Integer.parseInt(input);


            input = "0";

        mPrinterInfo.customPaperLength = Integer.parseInt(input);

            input = "0";
        mPrinterInfo.customFeed = Integer.parseInt(input);

        customSetting = "";
        mPrinterInfo.paperPosition = PrinterInfo.Align.LEFT;

        mPrinterInfo.dashLine = false;

        mPrinterInfo.rjDensity = 0;
        mPrinterInfo.rotate180 =false;
        mPrinterInfo.peelMode = false;

        mPrinterInfo.mode9 = false;
        mPrinterInfo.dashLine = false;
        input = "2";
        mPrinterInfo.pjSpeed = Integer.parseInt(input);

        mPrinterInfo.pjPaperKind = PrinterInfo.PjPaperKind
                .valueOf("PJ_CUT_PAPER");

        mPrinterInfo.rollPrinterCase = PrinterInfo.PjRollCase
                .valueOf("PJ_ROLLCASE_OFF");

        mPrinterInfo.skipStatusCheck = false;

        mPrinterInfo.checkPrintEnd = PrinterInfo.CheckPrintEnd.CPE_CHECK;

        mPrinterInfo.printQuality = PrinterInfo.PrintQuality.NORMAL;
        mPrinterInfo.overwrite = true;

        mPrinterInfo.trimTapeAfterData = false;


            input = "127";
        mPrinterInfo.thresholdingValue = Integer.parseInt(input);


            input = "0";
        try {
            mPrinterInfo.scaleValue = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            mPrinterInfo.scaleValue = 1.0;
        }

        if (mPrinterInfo.printerModel == Model.TD_4000
                || mPrinterInfo.printerModel == Model.TD_4100N) {
            mPrinterInfo.isAutoCut = Boolean.parseBoolean("");
            mPrinterInfo.isCutAtEnd = Boolean.parseBoolean("");
        }

        input = "";
        mPrinterInfo.savePrnPath = input;


        mPrinterInfo.workPath = "";
        mPrinterInfo.softFocusing = false;
        mPrinterInfo.enabledTethering = Boolean.parseBoolean("false");
        mPrinterInfo.rawMode = false;


            input = "0";
        mPrinterInfo.timeout.processTimeoutSec = Integer.parseInt(input);


            input = "60";
        mPrinterInfo.timeout.sendTimeoutSec = Integer.parseInt(input);


            input = "180";
        mPrinterInfo.timeout.receiveTimeoutSec = Integer.parseInt(input);


            input = "0";
        mPrinterInfo.timeout.connectionWaitMSec = Integer.parseInt(input);


            input = "3";
        mPrinterInfo.timeout.closeWaitDisusingStatusCheckSec = Integer.parseInt(input);

        mPrinterInfo.useLegacyHalftoneEngine = false;
    }

    /**
     * Launch the thread to print
     */
    public void print(ProgressDialog pdia) {
        mCancel = false;
        PrinterThread printTread = new PrinterThread();
        this.pdia = pdia;
        printTread.start();
    }

    /**
     * Launch the thread to get the printer's status
     */
    public void getPrinterStatus() {
        mCancel = false;
        getStatusThread getTread = new getStatusThread();
        getTread.start();
    }

    /**
     * Launch the thread to print
     */
    public void sendFile() {


        SendFileThread getTread = new SendFileThread();
        getTread.start();
    }

    /**
     * set custom paper for RJ and TD
     */
    private void setCustomPaper() {

        switch (mPrinterInfo.printerModel) {
            case RJ_4030:
            case RJ_4030Ai:
            case RJ_4040:
            case RJ_3050:
            case RJ_3150:
            case TD_2020:
            case TD_2120N:
            case TD_2130N:
            case TD_4100N:
            case TD_4000:
            case RJ_2030:
            case RJ_2140:
            case RJ_2150:
            case RJ_2050:
            case RJ_3050Ai:
            case RJ_3150Ai:
            case RJ_4230B:
            case RJ_4250WB:
                mPrinterInfo.customPaper = Common.CUSTOM_PAPER_FOLDER + customSetting;
                break;
            default:
                break;
        }
    }

    /**
     * get the end message of print
     */
    @SuppressWarnings("UnusedAssignment")
    public String showResult() {

        String result;
        if (mPrintResult.errorCode == ErrorCode.ERROR_NONE) {
            result = "LOL";
        } else {
            result = mPrintResult.errorCode.toString();
        }

        return result;
    }

    /**
     * show information of battery
     */


    public String getBatteryDetail() {
        return  String.format("%d/%d(AC=%s,BM=%s)",
                mPrintResult.batteryResidualQuantityLevel,
                mPrintResult.maxOfBatteryResidualQuantityLevel,
                mPrintResult.isACConnected.name(),
                mPrintResult.isBatteryMounted.name());
    }

    private boolean isLabelPrinter(PrinterInfo.Model model) {
        switch (model) {
            case QL_710W:
            case QL_720NW:
            case PT_E550W:
            case PT_E500:
            case PT_P750W:
            case PT_D800W:
            case PT_E800W:
            case PT_E850TKW:
            case PT_P900W:
            case PT_P950NW:
            case QL_810W:
            case QL_800:
            case QL_820NWB:
            case PT_P300BT:
            case QL_1100:
            case QL_1110NWB:
            case QL_1115NWB:
            case PT_P710BT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Thread for printing
     */
    private class PrinterThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();

            // start message

            mPrintResult = new PrinterStatus();

            mPrinter.startCommunication();
            if (!mCancel) {
                doPrint();
            } else {
                mPrintResult.errorCode = ErrorCode.ERROR_CANCEL;
            }
            pdia.dismiss();
            mPrinter.endCommunication();

            // end message

        }
    }

    /**
     * Thread for getting the printer's status
     */
    private class getStatusThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();



        }
    }

    /**
     * Thread for getting the printer's status
     */
    private class SendFileThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();

            // start message

            mPrintResult = new PrinterStatus();

            mPrinter.startCommunication();

            doPrint();

            mPrinter.endCommunication();
            // end message

        }
    }
}
