package hu.siz.android.kilopig;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String SEPARATOR = "|";

    private EditText idView;
    private EditText nameView;
    private EditText ageView;
    private EditText weightView;
    private ImageView readyView;

    private NfcAdapter adapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        idView = findViewById(R.id.id);
        nameView = findViewById(R.id.name);
        ageView = findViewById(R.id.age);
        weightView = findViewById(R.id.weight);
        readyView = findViewById(R.id.slaughterready);
        idView.setEnabled(true);
        nameView.setEnabled(true);
        ageView.setEnabled(true);
        weightView.setEnabled(true);
        readyView.setVisibility(View.GONE);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().contains("nfc")) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            try {
                final byte[] data =
                        (Base64.encodeToString(getData().getBytes(), Base64.DEFAULT)).getBytes(
                                "utf8");
                NdefRecord record = NdefRecord.createMime("application/vnd.hu.siz.android" +
                        ".kilopig", data);
                NdefMessage message = new NdefMessage(new NdefRecord[]{record});
                Ndef ndef = Ndef.get(tag);
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                Toast.makeText(this, "Címke megírva", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Címke írása sikertelen", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private String getData() {
        StringBuilder result = new StringBuilder();
        result.append(idView.getText());
        result.append(SEPARATOR);
        result.append(nameView.getText());
        result.append(SEPARATOR);
        result.append(ageView.getText());
        result.append(SEPARATOR);
        result.append(weightView.getText());

        return result.toString();
    }
}
