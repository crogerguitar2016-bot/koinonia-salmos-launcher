package com.croger.koinoniasalmoslauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_RUN_COMMAND = 1001;

    private static final String TERMUX_PERMISSION =
            "com.termux.permission.RUN_COMMAND";

    private static final String SCRIPT =
            "/data/data/com.termux/files/home/.shortcuts/Koinonia-Salmos.sh";

    private static final String HOME =
            "/data/data/com.termux/files/home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verificarPermissao();
    }


    private void verificarPermissao() {

        if (android.os.Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(TERMUX_PERMISSION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{TERMUX_PERMISSION},
                    REQUEST_RUN_COMMAND
            );

            return;
        }

        abrirKoinonia();
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_RUN_COMMAND) {

            if (grantResults.length > 0 &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {

                abrirKoinonia();

            } else {

                Toast.makeText(
                        this,
                        "Permita que Koinonia Salmos execute comandos no Termux.",
                        Toast.LENGTH_LONG
                ).show();

                finish();
            }
        }
    }


    private void abrirKoinonia() {

        try {

            Intent intent = new Intent();

            intent.setClassName(
                    "com.termux",
                    "com.termux.app.RunCommandService"
            );

            intent.setAction(
                    "com.termux.RUN_COMMAND"
            );

            intent.putExtra(
                    "com.termux.RUN_COMMAND_PATH",
                    SCRIPT
            );

            intent.putExtra(
                    "com.termux.RUN_COMMAND_WORKDIR",
                    HOME
            );

            /*
             * false = executar em uma sessão visível
             * do terminal Termux.
             */
            intent.putExtra(
                    "com.termux.RUN_COMMAND_BACKGROUND",
                    false
            );

            /*
             * Abre/troca para a nova sessão.
             */
            intent.putExtra(
                    "com.termux.RUN_COMMAND_SESSION_ACTION",
                    "0"
            );

            startService(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Não foi possível abrir o Termux: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }
}
