package br.com.mateus.sugarme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.mateus.sugarme.Presenter.MedicalInfoController;

public class PacienteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.paciente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            MedicalInfoController medicalInfoController = new MedicalInfoController();
            medicalInfoController.recebeInfoMedica(PacienteActivity.this);
//                Intent intent = new Intent(PacienteActivity.this, MedicalInfoActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PacienteActivity.this.startActivity(intent);
        } else if (id == R.id.nav_diario) {

        } else if (id == R.id.nav_exames) {

        } else if (id == R.id.nav_intercor) {

        } else if (id == R.id.nav_medicacao) {

        } else if (id == R.id.nav_agenda) {

        } else if (id == R.id.nav_relatorio) {

        }else if (id == R.id.nav_chat) {

        }else if (id == R.id.nav_vincular) {

        }else if (id == R.id.nav_sair) {
            /*<item name="ic_menu_perfil" type="drawable">@android:drawable/ic_menu_send</item>
    <item name="ic_menu_diario" type="drawable">@android:drawable/ic_menu_camera</item>
    <item name="ic_menu_intercor" type="drawable">@android:drawable/ic_menu_gallery</item>
    <item name="ic_menu_medicacao" type="drawable">@android:drawable/ic_menu_slideshow</item>
    <item name="ic_menu_exames" type="drawable">@android:drawable/ic_menu_send</item>
    <item name="ic_menu_agenda" type="drawable">@android:drawable/ic_menu_manage</item>
    <item name="ic_menu_relatorio" type="drawable">@android:drawable/ic_menu_share</item>
    <item name="ic_menu_chat" type="drawable">@android:drawable/ic_menu_send</item>
    <item name="ic_menu_vincular" type="drawable">@android:drawable/ic_menu_send</item>
    <item name="ic_menu_sair" type="drawable">@android:drawable/ic_menu_send</item>
/*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
