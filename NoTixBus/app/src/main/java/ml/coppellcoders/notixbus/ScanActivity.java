package ml.coppellcoders.notixbus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleObserver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanActivity extends Activity {
    ImageView scanImage;
    TextView scanEventName;
    TextView scanTime;
    ImageView scanPicture;
    Button takePicture;
    String currentPhotoPath = "";
    String eventName = "";
    private CameraView mCameraView;

    private final String apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "8cbcd7079f334206968fbba199aaff0a";
    FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);
    ;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    long time;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scanImage = findViewById(R.id.scan_image);
        scanEventName = findViewById(R.id.scan_event_name);
        scanTime = findViewById(R.id.scan_time);
        scanPicture = findViewById(R.id.scan_picture);
        takePicture = findViewById(R.id.scan_take_photo);
        String imageSrc = getIntent().getExtras().get("image").toString();
        eventName = getIntent().getExtras().get("name").toString();
        time = getIntent().getLongExtra("time", 0);
        scanEventName.setText(eventName);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        mCameraView = findViewById(R.id.camera);
        mCameraView.setLayoutParams(new LinearLayout.LayoutParams(width, height/2));
        Log.i("imgSrc", imageSrc);
        if (imageSrc.startsWith("http")) {
            Picasso.with(this).load(imageSrc).fit().centerCrop().into(scanImage);
        } else {
            scanImage.setBackground(null);
            scanImage.setImageBitmap(decodeBase64(imageSrc));
        }
        long timeLeft = time - System.currentTimeMillis();
        int minutes = (int) ((timeLeft / (1000 * 60)) % 60);
        int hours = (int) ((timeLeft / (1000 * 60 * 60)));
        scanTime.setText(hours + " hours " + minutes + " minutes");

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Button Pressed");
                mCameraView.capturePicture();
            }
        });
        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                showImageView();
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Bitmap converetdImage = getResizedBitmap(bitmap, 250);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Events");
                final UUID firstUserId = detectAndFrame(converetdImage, true);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean foundTicket = false;
                        for (DataSnapshot children : dataSnapshot.getChildren()) {

                            if (children.child("name").getValue().toString().equals(eventName)) {
                                for (DataSnapshot faces : children.child("tickets").getChildren()) {
                                    String name = faces.child("guestname").getValue().toString();
                                    String image = faces.child("faceimg").getValue().toString();
                                    String quant = faces.child("quant").getValue().toString();
                                    Bitmap temp = decodeBase64(image);
                                    UUID curUserId = detectAndFrame(temp, false);
                                    Log.i("User ID's", firstUserId + " " + curUserId);
                                    try {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        double b = faceServiceClient.verify(firstUserId, curUserId).confidence;
                                        if (b > .5) {
                                            System.out.println("Found ticket exiting");
                                            //Toast.makeText(ScanActivity.this, "Match Found " + b, Toast.LENGTH_LONG).show();
                                            View alertView = getLayoutInflater().inflate(R.layout.print_id_dialog, null);
                                            TextView info = alertView.findViewById(R.id.alert_info);
                                            Button print = alertView.findViewById(R.id.alert_print);
                                            ImageView cancel = alertView.findViewById(R.id.alert_cancel);
                                            info.setText(String.format("Found %s ticket(s) for %s", quant, name));

                                            AlertDialog dialog = new AlertDialog.Builder(ScanActivity.this)
                                                    .setView(alertView)
                                                    .create();
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss();
                                                }

                                            });
                                            Log.e("QR Code Data: ", children.getKey() + "[" + faces.getKey());
                                            dialog.show();
                                            /*new AlertDialog.Builder(ScanActivity.this)
                                                    .setTitle("Proceed to Printout")
                                                    .setMessage(String.format("Found %s ticket(s) for %s", quant, name))
                                                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            QRCodeWriter writer = new QRCodeWriter();
                                                            try {
                                                                BitMatrix qrCode = writer.encode(children.getKey() + "[" + faces.getKey(), BarcodeFormat.QR_CODE, 512, 512);
                                                                int width = qrCode.getWidth();
                                                                int height = qrCode.getHeight();
                                                                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                                                for (int x = 0; x < width; x++) {
                                                                    for (int y = 0; y < height; y++) {
                                                                        bmp.setPixel(x, y, qrCode.get(x, y) ? Color.BLACK : Color.WHITE);
                                                                    }
                                                                }
                                                                //Name of Event: eventName
                                                                //Name of Person: name
                                                                //Time: time (long)
                                                                //Venue
                                                                String venue = children.child("venue").getValue().toString();
                                                                //Ticket ID:
                                                                String ticketID = faces.getKey();
                                                                //QR Code qrCode {Bit Map)
                                                                Log.i("Info", String.format("Event Name: %s%n Person Name: %s%n Time: %d%n Venue: %s%n Ticket ID: %s%n"
                                                                        , eventName, name, time, venue, ticketID));
                                                                Log.e("QR Code Data: ", children.getKey() + "[" + faces.getKey());
                                                            } catch (WriterException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            hideImageView();
                                                        }
                                                    })
                                                    .show();*/
                                            foundTicket = true;
                                            break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                        if (!foundTicket) {
                            new AlertDialog.Builder(ScanActivity.this)
                                    .setTitle("Error")
                                    .setMessage("No tickets found for user")
                                    .setPositiveButton("Ok", null)
                                    .show();
                            hideImageView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ml.coppellcoders.notixbus",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private UUID detectAndFrame(final Bitmap imageBitmap, final boolean frame) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        final ArrayList<String> id = new ArrayList<>();
        AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";
                    ProgressDialog detectionProgressDialog = new ProgressDialog(ScanActivity.this);

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                                /* new FaceServiceClient.FaceAttributeType[] {
                                    FaceServiceClient.FaceAttributeType.Age,
                                    FaceServiceClient.FaceAttributeType.Gender }
                                */
                            );
                            if (result == null) {
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();

                        if (!exceptionMessage.equals("")) {
                            showError(exceptionMessage);
                        }
                        if (result == null) return;
                        if (frame) {
                            scanPicture.setImageBitmap(
                                    drawFaceRectanglesOnBitmap(imageBitmap, result));
                            imageBitmap.recycle();
                        }
                    }
                };
        try {
            return detectTask.execute(inputStream).get()[0].faceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UUID(1, 1);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create().show();
    }

    private static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        if (faces != null) {
            for (Face face : faces) {

                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }

    public String getFaceId(Face[] faces) {
        return faces[0].faceId + "";
    }


    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
        mCameraView.clearFrameProcessors();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
        mCameraView.clearFrameProcessors();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void showImageView(){
        mCameraView.setVisibility(View.GONE);
        takePicture.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        scanPicture.setLayoutParams(new LinearLayout.LayoutParams(width, height/2));

    }

    public void hideImageView(){
        mCameraView.setVisibility(View.VISIBLE);
        takePicture.setVisibility(View.VISIBLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        scanPicture.setLayoutParams(new LinearLayout.LayoutParams(width, 0));
    }

}

