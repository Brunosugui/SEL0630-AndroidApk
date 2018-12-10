package com.iwami.bruno.homesensor;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String topic0 = "SEL0630/GALILEO/HUMIDITY";
    private static final String topic1 = "SEL0630/GALILEO/TEMPERATURE";
    private static final String topic2 = "SEL0630/GALILEO/SOIL_MOISTURE";
    private static final String topic3 = "SEL0630/GALILEO/LUMINOSITY";
    private static final String topic4 = "SEL0630/GALILEO/SERVO";
    private static final String topic5 = "SEL0630/ANDROID/SERVO";
    private static final String ServoOn = "ON";

    MQTT mqttClient;
    TextView txtHumidity, txtTemperature, txtLight, txtSM;
    Button btnServo;
    MqttCallback mqttCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHumidity =  (TextView) findViewById(R.id.txtHum);
        txtTemperature =  (TextView) findViewById(R.id.txtTemp);
        txtLight =  (TextView) findViewById(R.id.txtLight);
        txtSM =  (TextView) findViewById(R.id.txtSm);

        btnServo = (Button) findViewById(R.id.btnServo);

        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.d(MQTT.TAGmqtt, "Message rcvd: " + mqttMessage.toString());
                Log.d(MQTT.TAGmqtt, "String rcvd: " + s);
                parseRcvdData(s, mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };

        mqttClient = new MQTT(this, mqttCallback);
        mqttClient.connectMQTTserver();

        btnServo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mqttClient != null){
                    mqttClient.publish(topic5, ServoOn);
                }
            }
        });

    }

    private void parseRcvdData(final String topic, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(topic.contains(MQTT.topic0)){
                    txtHumidity.setText("Humidity = " + message);
                }else if(topic.contains(MQTT.topic1)){
                    txtTemperature.setText("Temperature = " + message);
                }else if(topic.contains(MQTT.topic2)){
                    txtSM.setText("Soil Moisture = " + message);
                }else if(topic.contains(MQTT.topic3)){
                    txtLight.setText("Luminosity = " + message);
                }else if(topic.contains(MQTT.topic4)){

                }else if(topic.contains(MQTT.topic5)){

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET},
                1);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WAKE_LOCK},
                2);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                3);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                4);
    }

}
