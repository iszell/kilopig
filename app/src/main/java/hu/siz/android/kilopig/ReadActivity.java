package hu.siz.android.kilopig;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.StringTokenizer;

public class ReadActivity extends AppCompatActivity {

    public static final String SEPARATOR = "|";
    private EditText idView;
    private EditText nameView;
    private EditText ageView;
    private EditText weightView;
    private ImageView readyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onResume();
        idView = findViewById(R.id.id);
        nameView = findViewById(R.id.name);
        ageView = findViewById(R.id.age);
        weightView = findViewById(R.id.weight);
        readyView = findViewById(R.id.slaughterready);
        idView.setEnabled(false);
        nameView.setEnabled(false);
        ageView.setEnabled(false);
        weightView.setEnabled(false);

        Intent intent = getIntent();
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        Parcelable message = messages[0];

        if (message instanceof NdefMessage) {
            NdefMessage ndefMessage = (NdefMessage) message;
            String msgText = new String(Base64.decode(ndefMessage.getRecords()[0].getPayload(),
                    Base64.DEFAULT));
            StringTokenizer tokenizer = new StringTokenizer(msgText);
            idView.setText(tokenizer.nextToken(MainActivity.SEPARATOR));
            nameView.setText(tokenizer.nextToken(MainActivity.SEPARATOR));
            ageView.setText(tokenizer.nextToken(MainActivity.SEPARATOR));
            weightView.setText(tokenizer.nextToken(MainActivity.SEPARATOR));

            int image = R.drawable.disc_grey;
            try {
                final Integer weightValue = Integer.valueOf(weightView.getText().toString());
                if (weightValue < 80) {
                    image = R.drawable.disc_orange;
                } else if (weightValue > 100) {
                    image = R.drawable.disc_red;
                } else {
                    image = R.drawable.disc_green;
                }
            } catch (NumberFormatException nfe) {
            }
            readyView.setImageResource(image);
        }
    }
}
