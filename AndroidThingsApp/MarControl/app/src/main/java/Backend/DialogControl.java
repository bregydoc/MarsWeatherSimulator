package Backend;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.potatomars.chimpcode.marcontrol.R;

import org.w3c.dom.Text;

public class DialogControl {
    View viewOfDialog;

    TextView txtSelecO2;
    TextView txtSelecCO2;
    TextView txtSelecTemp;

    public float rawCO2;
    public float rawO2;


    public DialogControl(Activity activity){
        rawCO2 = 0;
        rawO2 = 0;

        MaterialDialog configDialog = new MaterialDialog.Builder(activity)
                .title("Configuracion de parametros")
                .customView(R.layout.dialog_view, false)
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View viewOfDialog1 = dialog.getCustomView();
                        TextView txtSelecO21 =  (TextView) (viewOfDialog1.findViewById(R.id.txtSelectO2));
                        TextView txtSelecCO21 =  (TextView) (viewOfDialog1.findViewById(R.id.txtSelectCO2));
                        TextView txtSelecTemp1 =  (TextView) (viewOfDialog1.findViewById(R.id.txtSelectTemp));

                        TextView txtO2 =  (TextView) (activity.findViewById(R.id.txtEspO2));
                        TextView txtCO2 =  (TextView) (activity.findViewById(R.id.txtEspCo2));
                        TextView txtTemp =  (TextView) (activity.findViewById(R.id.txtEspTemp));

                        txtO2.setText(txtSelecO21.getText());
                        txtCO2.setText(txtSelecCO21.getText());
                        txtTemp.setText(txtSelecTemp1.getText());


                    }
                })
                .show();


        viewOfDialog = configDialog.getCustomView();
        txtSelecO2 =  (TextView) (viewOfDialog.findViewById(R.id.txtSelectO2));
        txtSelecCO2 =  (TextView) (viewOfDialog.findViewById(R.id.txtSelectCO2));
        txtSelecTemp =  (TextView) (viewOfDialog.findViewById(R.id.txtSelectTemp));

        SeekBar sckO2 = (SeekBar) viewOfDialog.findViewById(R.id.sckO2);
        sckO2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rawO2 = progress;
                txtSelecO2.setText(String.valueOf(progress) + " %ppm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar sckCO2 = (SeekBar) viewOfDialog.findViewById(R.id.sckCO2);
        sckCO2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rawCO2 = progress;
                txtSelecCO2.setText(String.valueOf(progress) + " %ppm");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar sckTemp = (SeekBar) viewOfDialog.findViewById(R.id.sckTemp);
        sckTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = seekBar.getMax()/2;
                float temp = (float) (((progress - max)/(float) max)*28.0);
                txtSelecTemp.setText(String.format("%.2f", temp) + " °C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

}

