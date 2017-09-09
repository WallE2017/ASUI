package testjson;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import POJO.*;

/**
 * Created by Jinffee on 2017/8/17.
 */

public class HttpClient extends IntentService {
    private String logTag = "HttpClient";
    private MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
    private MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    //address
    private String ip = "[2001:da8:215:c658:2bb:a19d:4388:4981]";
    private String port = "8080";
    private String projectname = "intelcaffe";
    //find user in database"

    private OkHttpClient client;
    private Gson gson;
    private String hql;
    LocalBroadcastManager bm  = LocalBroadcastManager.getInstance(HttpClient.this);;
    private static Connection conection;
    private static int userCode;
    public HttpClient(){
        super("HttpClient");
        client= new OkHttpClient.Builder().readTimeout(3000, TimeUnit.SECONDS)//设置读取超时时间
                                            .writeTimeout(3000, TimeUnit.SECONDS)//设置写的超时时间
                                            .connectTimeout(3000, TimeUnit.SECONDS).build();//设置连接超时时间  ;
        gson=new Gson();

    }
    public class LocalBinder extends Binder {
        public HttpClient getService() {
            // Return this instance of LocalService so clients can call public methods
            return HttpClient.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(logTag,"On Bind");
        return new LocalBinder();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int order = intent.getIntExtra("param", 0);
        switch (order){
            case 0:
                Log.i(logTag, "did't get param");
                break;
            case 1:
                login(intent.getStringExtra("phonenum"),intent.getStringExtra("password"));
                break;
            case 2:
                UserSignUp(new User(0, intent.getStringExtra("phonenum"),"user",intent.getStringExtra("password")),intent.getStringExtra("userName"));
                break;
            case 3:
                connectionInit();
                break;
            case 4:
                conection.initChat();
                break;
            case 5:
                conection.send(intent.getStringExtra("message"));
                break;
            case 6:
                tougunJudge(intent.getStringExtra("File"));
                break;
            case 7:
                break;
            default:
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(logTag,"On Start Command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(logTag, "On Destroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(logTag, "On UnBind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(logTag, "On Rebind");
        super.onRebind(intent);
    }

    //login
    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public void login(String phonenum, String password){
        //find user in database
        hql = "from User as user where user.userAccount='"+phonenum+"' and user.password='"+password+"'";
        Log.i(logTag, hql);
        Request request = new Request.Builder()
                .url("http://" +  ip + ":" + port + "/" + projectname + "/Login")//url of server
                .post(RequestBody.create(MEDIA_TYPE_TEXT,hql))
                .build();
        //request will enqueue to send
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
                                                userCode = gson.fromJson(response.body().charStream(), int.class);
                                                Intent intent = new Intent();
                                                intent.setAction("Log_in");
                                                intent.putExtra("code",userCode);
                                                bm.sendBroadcast(intent);
                                            }
                                        });
    }
    //User sign up
    public void UserSignUp(User user, final String userName) {
        // 0 is false and 1 is ready
        char signUpStatues;

        Request request = new Request.Builder()
                .url("http://" + ip + ":" + port + "/" + projectname + "/Registion")//url of server
                .post(RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(user)))
                .build();
        //request will enqueue to send
        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                if (!response.isSuccessful())  throw new IOException("Unexpected code: " + response);
                                                userCode = gson.fromJson(response.body().charStream(), int.class);
                                                //TODO: send Broadcast to activity
                                                Intent intent = new Intent();
                                                intent.setAction("REGISTER");
                                                intent.putExtra("code",userCode);
                                                bm.sendBroadcast(intent);
                                                if(userCode != -1){
                                                    try {
                                                        insert(new PersonInfo(userCode,userName));
                                                    } catch (SignUpException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
    }

   /* private byte[] imageToByte(String path) throws IOException {
        byte[] data = null;
        FileInputStream input = null;
        input = new FileInputStream(new File(path));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int numBytesRead = 0;
        while ((numBytesRead = input.read(buf)) != -1) {
            output.write(buf, 0, numBytesRead);
        }
        data = output.toByteArray();
        output.close();
        input.close();
        return data;
    }*/
    public void tougunJudge(String path) {

        File image = new File(path);
        Date date = new Date();
        //cover with json
        String dgson = gson.toJson(date);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_JPG, image);
        builder.addFormDataPart("uploadfile", image.getName(), fileBody);
        builder.addFormDataPart("code", Integer.toString(userCode));
        builder.addFormDataPart("time",dgson);
        Request request = new Request.Builder()
                .url("http://" +  ip + ":" + port + "/" + projectname + "/TongueAnalysis")//url of server
                .post(builder.build())
                .build();
        //request will enqueue to send
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
                String result = gson.fromJson(response.body().charStream(), String.class);
                Log.i(logTag, result);

            }
        });

    }
    // websocket service
    public void connectionInit(){
        Request request = new Request.Builder()
                .url("ws://" +  ip + ":" + port + "/" + projectname + "/Chat")//url of server
                .build();
        Log.i(logTag, "ws://" + ip + ":" + port + "/" + projectname + "/Chat");
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                String usercode = new Gson().toJson(userCode);
                Log.i(logTag, "UserCode:"+usercode);
                webSocket.send("UserCode:"+usercode);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Intent intent = new Intent();
                Log.i(logTag,  "onMessage: " + text);
                String[] texts = text.split(":",2);
                if( texts[0].equals("List")){
                    Type type = new TypeToken<List<Object[]>>(){}.getType();
                    List<Object[]> objects = new Gson().fromJson(texts[1], type);
                    List<Doctor> doctors = new LinkedList<Doctor>();
                    for (Object[] doctor : objects)
                           doctors.add(new Doctor((int)doctor[0],(String)doctor[1],(String)doctor[2],(Byte[])doctor[3]));
                    //TODO:add print doctors method
                    intent.setAction("Get_Doctor_List");
                    intent.putExtra("List",(Serializable)doctors);
                    sendBroadcast(intent);
                }else if(texts[0].equals("Message")){
                    String message = new Gson().fromJson(texts[1], String.class);
                    Log.i(logTag, message);
                    //TODO:add print message method and store message method
                    intent.putExtra("Message",message);
                    intent.setAction("Get_Message");
                    sendBroadcast(intent);
                }else if(texts[0].equals("Error")){
                    String errorMessage = new Gson().fromJson(texts[1], String.class);
                    Log.i(logTag,  errorMessage);
                    //TODO:add Error sort method
//                    intent.setAction("Get_Error");
//                    sendBroadcast(intent);
                }else if(texts[0].equals("Connection Setup")){
                    Log.i(logTag,   "Connection SetUp");
                    /*intent.setAction("Connection_SetUp");
                    sendBroadcast(intent);*/
                }else if (texts[0].equals("\"close\"")) {
                    Log.i(logTag, "connection close");
//                    intent.setAction("Connection_SetUp");
//                    sendBroadcast(intent);
                } else {
                    Log.i(logTag, "wrong information!");

                }

            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.i(logTag,   "onClosing: " + code + "/" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.i(logTag,   "onClosed: " + code + "/" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.i(logTag,  "onFailure: " + t.getMessage());
            }
        });
        conection =new Connection(webSocket);
    }
    public Connection getConection(){
        return this.conection;
    }
    public void Query(String hql, Class<?> clazz, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url("http://" +  ip + ":" + port + "/" + projectname + "/Query")//url of server
                .post(RequestBody.create(MEDIA_TYPE_TEXT,hql))
                .build();
        //request will enqueue to send
//        client.newCall(request).enqueue(callback);
//        if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
//        Type type = new TypeToken<List<T>>(){}.getType();
//        List<T> result = gson.fromJson(response.body().charStream() , type);
//        return result;
    }

    public void insert(Object object) throws IOException, SignUpException {
        char signUpStatues;
        String json;
        if(object instanceof User){
            json = "User;"+gson.toJson(object);
        }else if(object instanceof PersonInfo) {
            json = "PersonInfo;"+gson.toJson(object);
        }else if(object instanceof HealthInfo){
            json = "HealthInfo;"+gson.toJson(object);
        }else if(object instanceof DoctorInformation){
            json = "DoctorInformation;"+gson.toJson(object);
        }else{
            throw new SignUpException(SignUpException.Type_Wrong);
        }
        Request request = new Request.Builder()
                .url("http://" +  ip + ":" + port + "/" + projectname + "/Save")//url of server
                .post(RequestBody.create(MEDIA_TYPE_JSON,json))
                .build();
        //request will enqueue to send
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(logTag,"insert success");
            }
        });
//        if (!response.isSuccessful()) throw new SignUpException(SignUpException.CREATE_FIAL);
//        signUpStatues = gson.fromJson(response.body().charStream(), char.class);
//        Log.i(logTag, signUpStatues);
//        return signUpStatues;
    }

}
