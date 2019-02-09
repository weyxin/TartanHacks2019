package com.example.tartanhacks2019;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import java.util.ArrayList;


import java.io.File;
import java.util.Arrays;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), uri);
                ImageView imageView = getView().findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);

                detectAndFrame(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Detect faces by uploading a face image.
    // Frame faces after detection.
    private void detectAndFrame(final Bitmap imageBitmap) {
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
                                    new FaceServiceClient.FaceAttributeType[] {
                                            FaceServiceClient.FaceAttributeType.Age,
                                            FaceServiceClient.FaceAttributeType.Gender, FaceServiceClient.FaceAttributeType.Smile,
                                            FaceServiceClient.FaceAttributeType.FacialHair, FaceServiceClient.FaceAttributeType.Hair,
                                            FaceServiceClient.FaceAttributeType.Makeup}         // returnFaceAttributes:
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
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
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

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
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
    protected Face[] readIn (InputStream p) {
        try {
            // Start detection.
            return faceServiceClient.detect(
                    p,  /* Input stream of image to detect */
                    true,         // returnFaceId
                    false,        // returnFaceLandmarks
                    new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender, FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.FacialHair, FaceServiceClient.FaceAttributeType.Hair,
                            FaceServiceClient.FaceAttributeType.Makeup});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Face[1];
    }

    public void peopleGroup(Face f) {


        ParseQuery<Person> query= ParseQuery.getQuery(Person.class);
        ArrayList<Person> people = new ArrayList<Person>();
        try {
            List<Person> people2 = query.find();
            people = (ArrayList<Person>) people2;
            Log.d(("Tab2Fragment"), Integer.toString(people.size()));

        } catch(com.parse.ParseException e){ e.printStackTrace();}

        for(Person p: people) {
            try{
                Face[] info = readIn(p.getProfileImage().getDataStream());
                if (info != null) {
                    for (Face face : info) {
                        VerifyResult f2 = faceServiceClient.verify(face.faceId, f.faceId);
                        Log.d("Name",p.getName() + f2.confidence);
                        if(f2.isIdentical) {
                            Log.d("Name",p.getName());
                            return;
                        }
                    }
                }
            }
            catch(Exception e) {
            e.printStackTrace();
        }

            }
            /*VerifyResult f2 = new VerifyResult();

            try{
               // Face[] works = doInBackground(p);
                if(works[0] == null) {
                    Log.d("Name", "Null");
                }
                 f2= faceServiceClient.verify(f.faceId, works[0].faceId);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            Log.d("Name",p.getName() + f2.confidence);
            if(f2.isIdentical) {
                Log.d("Name",p.getName());
                return;
            }*/
        }

    }

    /*public void peopleGroup(Face f) {
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

    }*/

    /*public String identifyFace() {
        // Step 5. Call Verify method.
        ObjectResult<VerifyValue> result5 = face.Verify(result1.getInstance().get(0).getFaceId(),
                "testGroup", result3.getStringValue());
    // Step 6. Get verify result.
        if(!result5.getInstance().isIdentical())
            fail("face verification failed.");
        System.out.println(String.format("Confidence = %f", result5.getInstance().getConfidence()));

    }*/

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


