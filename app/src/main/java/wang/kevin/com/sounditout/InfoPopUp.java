package wang.kevin.com.sounditout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by kevin on 2016-08-18.
 */
public class InfoPopUp extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Need Some Help?")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setMessage("Press the microphone to speak. This app will display the first word that is said. I thought this might be useful for kids learning to spell. I have a seven year old sister and I made this to help her spell. Hope this will help you as well =).");
        return builder.create();
    }
}
