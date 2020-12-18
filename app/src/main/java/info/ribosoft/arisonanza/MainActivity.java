package info.ribosoft.arisonanza;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.MessageFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spnResist, spnInduct, spnCapacit, spnVoltage;
    Button btnCalculate;
    EditText edtResist,edtInduct, edtCapacit, edtVoltage;
    TextView txtCoeffQ, txtOverVoltage, txtFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieves the reference to two objects placed in the user interface
        edtResist = findViewById(R.id.editTextResist);
        spnResist = findViewById(R.id.spinnerResist);
        edtInduct = findViewById(R.id.editTextInduct);
        spnInduct = findViewById(R.id.spinnerInduct);
        edtCapacit = findViewById(R.id.editTextCapacit);
        spnCapacit = findViewById(R.id.spinnerCapacit);
        edtVoltage = findViewById(R.id.editTextVolt);
        spnVoltage = findViewById(R.id.spinnerVolt);
        btnCalculate = findViewById(R.id.buttonCalculate);
        txtCoeffQ = findViewById(R.id.textViewCoeffQ);
        txtOverVoltage = findViewById(R.id.textViewOverVolt);
        txtFrequency = findViewById(R.id.textViewFrequen);

        // to specify an action when the button is pressed
        btnCalculate.setOnClickListener(this);

        // creates a list of text elements to display in the spinner element
        ArrayAdapter <CharSequence> adpResist = ArrayAdapter.createFromResource(this,
                R.array.listResist, android.R.layout.simple_spinner_item);
        ArrayAdapter <CharSequence> adpIndut = ArrayAdapter.createFromResource(this,
                R.array.listInduct, android.R.layout.simple_spinner_item);
        ArrayAdapter <CharSequence> adpCapacita = ArrayAdapter.createFromResource(this,
                R.array.listCapacit, android.R.layout.simple_spinner_item);
        ArrayAdapter <CharSequence> adpTens = ArrayAdapter.createFromResource(this,
                R.array.listVoltage, android.R.layout.simple_spinner_item);

        // specifies the layout that the adapter should use to display the list of choices
        adpResist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adpIndut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adpCapacita.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adpTens.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply the adapter to the spinner
        spnResist.setAdapter(adpResist);
        spnInduct.setAdapter(adpIndut);
        spnCapacit.setAdapter(adpCapacita);
        spnVoltage.setAdapter(adpTens);

        // sets the currently selected item
        spnResist.setSelection(0);
        spnInduct.setSelection(3);
        spnCapacit.setSelection(4);
        spnVoltage.setSelection(0);
    }

    public void onClick (View view) {
        int[] valResist = {0, 3, 6};
        int[] valIndut = {3, 0, -3, -6, -12};
        int[] valCapacit = {0, -3, -6, -9, -12};
        int[] valTensione = {0, 3, 6};
        String[] lstVolt = {"Volt", "-", "-", "Kilo Volt", "-", "-", "Mega Volt"};
        String[] lstFrequenza = {"Herz", "-", "-", "Kilo Herz", "-", "-", "Mega Hertz"};
        int iRes, iInd, iCap, iVol;
        double depoRes, depoInd, depoCap, depoVol, valFreq, valCoefQ, valOverV;
        Double depoI;

        // return the position of the currently selected item within the adapter's data set
        iRes = valResist[spnResist.getSelectedItemPosition()];
        iInd = valIndut[spnInduct.getSelectedItemPosition()];
        iCap = valCapacit[spnCapacit.getSelectedItemPosition()];
        iVol = valTensione[spnVoltage.getSelectedItemPosition()];

        // calculates the base value
        depoRes = Math.pow(10, iRes) * Double.parseDouble(edtResist.getText().toString());
        depoInd = Math.pow(10, iInd) * Double.parseDouble(edtInduct.getText().toString());
        depoCap = Math.pow(10, iCap) * Double.parseDouble(edtCapacit.getText().toString());
        depoVol = Math.pow(10, iVol) * Double.parseDouble(edtVoltage.getText().toString());

        // calculates the value of resonance frequency
        valFreq = Math.sqrt(depoInd * depoCap);
        valFreq = 1/(2*3.14*valFreq);

        // calculates the resonance coefficient q
        valCoefQ = (2 * 3.14 * valFreq * depoInd) / depoRes;

        // calculates the overvoltage value in resonance conditions
        valOverV = depoVol * valCoefQ;

        // format the value of the Q coefficient for a correct display
        txtCoeffQ.setText(numFormat(valCoefQ, 4));

        // calculates the unit of measurement
        depoI = unitConversion(valOverV);
        valOverV = valOverV / Math.pow(10, depoI);

        // format the value of the overvoltage for a correct display
        txtOverVoltage.setText(MessageFormat.format("{0} {1}", numFormat(valOverV, 4), lstVolt[depoI.intValue()]));

        // calculates the unit of measurement
        depoI = unitConversion(valFreq);
        valFreq = valFreq / Math.pow(10, depoI);

        // format the value of the resonant frequency for a correct display
        txtFrequency.setText(MessageFormat.format("{0} {1}", numFormat(valFreq, 4), lstFrequenza[depoI.intValue()]));
    }

    // calculate the unit of measurement
    public Double unitConversion(double valore) {
        double i = 3.0, risult;

        risult = valore / Math.pow(10, i);
        while (risult > 1.0) {
            i = i + 3;
            risult = valore / Math.pow(10, i);
        }
        i = i - 3;

        return i;
    }

    // format the value for a correct display
    public String numFormat(Double numImput, int maxDigit) {
        NumberFormat numIn;
        double depoNum;
        String strDepo1, strDepoOut;
        int carPunto;
        boolean flagErr = true;

        strDepo1 = numImput.toString();

        // checks if the number exceeds the number of displayable digits
        if (!strDepo1.contains(".")) {
            if (strDepo1.length() <= maxDigit) {
                flagErr = false;
            }
        } else if (strDepo1.indexOf(".") <= maxDigit) {
            flagErr = false;
        }

        if (!flagErr) {
            carPunto = maxDigit - strDepo1.indexOf(".");
            depoNum = Double.parseDouble(strDepo1);

            // returns a general-purpose number format for the specified locale
            numIn = NumberFormat.getNumberInstance();

            // sets the maximum number of digits allowed in the fractional part of a number
            numIn.setMaximumFractionDigits(carPunto);

            // set whether or not grouping will be used in this format
            numIn.setGroupingUsed(false);

            // format the number according to the parameters entered
            strDepoOut = numIn.format(depoNum);
        } else {
            strDepoOut = "Errore";
        }
        return (strDepoOut);
    }
}
