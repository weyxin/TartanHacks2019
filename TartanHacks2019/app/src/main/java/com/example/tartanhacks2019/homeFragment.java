package com.example.tartanhacks2019;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/*
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
*/

public class homeFragment extends Fragment implements View.OnClickListener {
    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    private final String apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "25e1068cc3ef4b30aa9987477caf27bb";
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_home);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button button1 = (Button) view.findViewById(R.id.button1);
        button1.setOnClickListener(this);
        detectionProgressDialog = new ProgressDialog(getActivity());
        //return R.layout.fragment_home;
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(
                intent, "Select Picture"), PICK_IMAGE);
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(
                        intent, "Select Picture"), PICK_IMAGE);
            }
        });

        detectionProgressDialog = new ProgressDialog(this);
    }*/

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri uri = data.getData();
            Log.d("homeFragment", getFileName(uri));
            String fileName = getFileName(uri);
            String personName = "NA";
            if(fileName.contains("anna")) {
                Log.d("homeFragment", "contains weyxin");
                personName = "Anna";
            }
            if(fileName.contains("matt")) {
                personName = "Matt";
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), uri);
                ImageView imageView = getView().findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);

                detectAndFrame(bitmap, personName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Detect faces by uploading a face image.
    // Frame faces after detection.
    private void detectAndFrame(final Bitmap imageBitmap, String personName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

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
                            if (result == null){
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

                        /*if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }*/
                        if (result == null) return;

                        ImageView imageView = getView().findViewById(R.id.imageView1);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result, personName));
                        for (Face f: result) {
                            peopleGroup(f);
                        }
                        imageBitmap.recycle();
                    }
                };

        detectTask.execute(inputStream);
    }

    private void showError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
    }

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces, String personName) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(3);
        paint.setTextSize(20);
        String nameText = "Unknown";
        if(personName == "Matt" | personName == "Anna") nameText = personName;
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
                canvas.drawText(nameText, faceRectangle.left, faceRectangle.top-10, paint);
            }
        }
        return bitmap;
    }

    public void peopleGroup(Face f) {
        String personGroupID = "friends";
        try{
            faceServiceClient.createPersonGroup(personGroupID, "friends", null);
            CreatePersonResult friend1 = faceServiceClient.createPerson("friends", personGroupID, "Bill");
            CreatePersonResult friend2 = faceServiceClient.createPerson("friends", personGroupID, "Anna");
            ParseQuery<Person> query= ParseQuery.getQuery(Person.class);
            ArrayList<Person> people = new ArrayList<Person>();
            try {
                List<Person> people2 = query.find();
                people = (ArrayList<Person>) people2;
                Log.d(("Tab2Fragment"), Integer.toString(people.size()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ParseFile profile = people.get(0).getProfileImage();
            if(profile != null) {
                //Glide.with(getContext()).load(profile.getUrl()).into(frontProfile);
                faceServiceClient.addPersonFace(personGroupID, friend1.personId, profile.getUrl(), null, null);
            }
            ParseFile profile1 = people.get(1).getProfileImage();
            if(profile1 != null) {
                faceServiceClient.addPersonFace(personGroupID, friend2.personId, profile1.getUrl(), null, null);
            }
            faceServiceClient.trainPersonGroup(personGroupID);
            TrainingStatus trainingStatus = null;
            while(true) {
                trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(personGroupID);
                if (trainingStatus.status != TrainingStatus.Status.Running) {
                    break;
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*public async Task<ActionResult> Train(String id)
    {
        await FaceClient.TrainPersonGroupAsync(id);
        return RedirectToAction("Details", new { id = id });
    }

    private void train(){
        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/{personGroupId}/train");
            Task<string> task = Task.Run<string>(async () => await GetHttpResponseString(url, contentModerationKey, image, contentType));
            string result = task.Result;
            /*URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", "{subscription key}");


            // Request body
            StringEntity reqEntity = new StringEntity("{body}");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                System.out.println(EntityUtils.toString(entity));
            }*/
        /*}
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }*/
    }
