package ml.coppellcoders.notixbus;



import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import ml.coppellcoders.notixbus.printprocess.ImagePrint;



public class PrintAsync extends AsyncTask<Void, Integer, Void> {

    private Context mContext;

    ImagePrint myPrint;
    String event;
    String name;
    String date;
    String venue;
    String id;
    String qrcode;
    private ProgressDialog pdia;

    public PrintAsync(Context context,String event, String name, String date, String venue, String id, String qrcode) {
        mContext = context;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.id = id;
        this.qrcode = qrcode;
        this.event = event;
    }


    //    @NonNull
    //  private final static String LEADER_SELECT = "a";
    @RequiresApi(api = Build.VERSION_CODES.P)
    @NonNull







    @Override
    protected Void doInBackground(final Void... voids) {

        myPrint = new ImagePrint(mContext);
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        myPrint.setBluetoothAdapter(bluetoothAdapter);


        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.template);


        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix qrCode = writer.encode(qrcode, BarcodeFormat.QR_CODE, 600, 600);
            int width = qrCode.getWidth();
            int height = qrCode.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, qrCode.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            Bitmap bitmap =  overlay(icon,bmp,2914,1560);
            Bitmap bitma = drawText(event,2500,150,"#FFFFFF",true);
            bitmap = overlay(bitmap,bitma,84,80);
            bitma = drawText(name,1337,120,"#FFFFFF",false);
            bitmap = overlay(bitmap,bitma,84,250);
            bitma = drawText(date,1337,120,"#FFFFFF",false);
            bitmap = overlay(bitmap,bitma,84,400);
            bitma = drawText(venue,2500,160,"#676767",false);
            bitmap = overlay(bitmap,bitma,459,760);
            bitma = drawText("Ticket ID: "+ id,2500,120,"#676767",false);
            bitmap = overlay(bitmap,bitma,459,1100);
            //img.setImageBitmap(bitmap);
            myPrint.setFiles(bitmap);
            myPrint.print(pdia);

        }catch (Exception e){

        }

        return null;


    }




    @Override
    protected void onPostExecute(final Void aVoid) {
        super.onPostExecute(aVoid);



    }





    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pdia = new ProgressDialog(mContext);
        pdia.setMessage("Please wait while printing label");
        pdia.setTitle("Printing");
        pdia.setCanceledOnTouchOutside(false);
        pdia.show();
    }


    BluetoothAdapter getBluetoothAdapter() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            final Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(enableBtIntent);
        }
        return bluetoothAdapter;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2, int x, int y) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, x,y, null);
        return bmOverlay;
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public Bitmap drawText(String text, int width, int size, String color,boolean bold) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor(color));
        textPaint.setTextSize(size);
        if(bold)
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(mTextLayout.getWidth(), mTextLayout.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);


        paint.setColor(Color.TRANSPARENT);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


}
