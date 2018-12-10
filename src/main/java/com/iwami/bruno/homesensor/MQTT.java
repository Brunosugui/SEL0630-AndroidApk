package com.iwami.bruno.homesensor;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static android.content.ContentValues.TAG;

public class MQTT {

    public static final String TAGmqtt = "mqttTAG";
    private static final String USERNAME = "Buddha";
    private static final String PASSWORD = "Buddha";
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    public static final String topic0 = "SEL0630/GALILEO/HUMIDITY";
    public static final String topic1 = "SEL0630/GALILEO/TEMPERATURE";
    public static final String topic2 = "SEL0630/GALILEO/SOIL_MOISTURE";
    public static final String topic3 = "SEL0630/GALILEO/LUMINOSITY";
    public static final String topic4 = "SEL0630/GALILEO/SERVO";
    public static final String topic5 = "SEL0630/ANDROID/SERVO";
    private static final int QOS = 0;
    private MqttAndroidClient client;
    private Context context;
    private boolean connected;
    private MqttCallback mqttCallback;

    public MQTT(Context context, MqttCallback mqttCallback) {
        this.context = context;
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, BROKER,
                clientId);
        this.mqttCallback = mqttCallback;
    }

    public void connectMQTTserver() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            Log.d(TAGmqtt, "attempt to connect");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAGmqtt, "Connection Success");
                    connected = true;
                    subscribe(topic0);
                    subscribe(topic1);
                    subscribe(topic2);
                    subscribe(topic3);
                    subscribe(topic4);
                    subscribe(topic5);
                    client.setCallback(mqttCallback);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAGmqtt, "Connection Failure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void publish(String Topic, String Payload) {
        try {
            client.publish(Topic, Payload.getBytes(), 0, false);
            Log.d(TAGmqtt, "Publish Success. msg = " + Payload + " on topic " + Topic);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAGmqtt, "Publish Failed");
        }
    }


    public void subscribe(String Topic) {
        try {
            client.subscribe(Topic, QOS);
            Log.d(TAGmqtt, "Subscribe Success on topic " + Topic);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAGmqtt, "Subscribe Failed");
        }
    }

}

