package basic.exam.nfcscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetSeatScannerActivity extends AppCompatActivity {
    private EditText trainId;
    private EditText trainNo;
    private EditText seatNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_seat_scanner);

        Button btn = (Button) findViewById(R.id.button);

        trainId = (EditText) findViewById(R.id.input_trainId);
        trainNo = (EditText) findViewById(R.id.input_trainNo);
        seatNo = (EditText) findViewById(R.id.input_seatNo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), SeatScannerActivity.class);
                intent_01.putExtra("trainId",trainId.getText().toString());
                intent_01.putExtra("trainNo",trainNo.getText().toString());
                intent_01.putExtra("seatNo",seatNo.getText().toString());

                startActivity(intent_01);
            }
        });
    }
}
