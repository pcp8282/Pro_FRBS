package basic.exam.nfcscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetDoorScannerActivity extends AppCompatActivity {

    private EditText trainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_door_scanner);

        Button btn = (Button) findViewById(R.id.button);

        trainId = (EditText) findViewById(R.id.input_trainId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), DoorScannerActivity.class);
                intent_01.putExtra("trainId",trainId.getText().toString());
                startActivity(intent_01);
            }
        });
    }
}
