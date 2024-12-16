package com.example.synthwavechat;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import org.eclipse.paho.client.mqttv3.*;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView messagesRecyclerView;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private MqttClient mqttClient;
    private static final String MQTT_BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String TOPIC = "chat/messages";
    private DatabaseReference messagesDatabase;

    private String currentNickname;
    private String currentProfilePictureUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        // referencia al imagen view
        ImageView chatBackground = findViewById(R.id.chatBackground);
        Glide.with(this)
                .asGif()
                .load(R.raw.synthwave_2) //archivo GIF
                .into(chatBackground);

        // Inicializar RecyclerView y lista de mensajes
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(chatAdapter);

        // Inicializar Firebase
        messagesDatabase = FirebaseDatabase.getInstance().getReference("messages");

        // Cargar perfil de usuario
        loadUserProfile();

        // Configurar MQTT
        setupMQTT();

        // Manejar envío de mensajes
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void loadUserProfile() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentNickname = task.getResult().child("nickname").getValue(String.class);
                currentProfilePictureUrl = task.getResult().child("profilePictureUrl").getValue(String.class);

                // Si no hay foto de perfil, usa una por defecto
                if (currentProfilePictureUrl == null) {
                    currentProfilePictureUrl = "urlDeImagenPerfil";
                }
            }
        });
    }

    private void setupMQTT() {
        try {
            mqttClient = new MqttClient(MQTT_BROKER_URL, MqttClient.generateClientId(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "Conexión perdida", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String receivedMessage = new String(message.getPayload());
                    Log.d(TAG, "Mensaje recibido: " + receivedMessage);

                    // Crear un ChatMessage con valores predeterminados para nickname y URL de perfil
                    messageList.add(new ChatMessage(currentNickname != null ? currentNickname : "Usuario", receivedMessage, currentProfilePictureUrl));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Mensaje entregado");
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe(TOPIC);

        } catch (Exception e) {
            Log.e(TAG, "Error al configurar MQTT", e);
        }
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        try {
            // Encriptar el mensaje
            String hashedMessage = hashMessage(messageText);

            // Publicar en MQTT
            MqttMessage message = new MqttMessage(hashedMessage.getBytes());
            mqttClient.publish(TOPIC, message);

            // Guardar en Firebase
            messagesDatabase.push().setValue(hashedMessage);

            // Mostrar en la lista
            messageList.add(new ChatMessage(currentNickname != null ? currentNickname : "Usuario", messageText, currentProfilePictureUrl));
            chatAdapter.notifyItemInserted(messageList.size() - 1);

            // Limpiar el campo de texto
            etMessageInput.setText("");

        } catch (Exception e) {
            Log.e(TAG, "Error al enviar el mensaje", e);
        }
    }

    private String hashMessage(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error al encriptar el mensaje", e);
            return message;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cerrar la conexión MQTT", e);
        }
    }
}
