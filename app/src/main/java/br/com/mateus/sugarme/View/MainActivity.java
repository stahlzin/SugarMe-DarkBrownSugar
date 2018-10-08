package br.com.mateus.sugarme.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import br.com.mateus.sugarme.R;


public class MainActivity extends AppCompatActivity {
    MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainController = new MainController();

        //Maximixar a tela
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

       if(mainController.verificaLogin()){
           mainController.verificaTipoUsuario(MainActivity.this);
       }
       else{
           mainController.fazerLogin(this);
       }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainController.onLoginResult(this, requestCode);
    }
}
