package am.l5z1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import am.l5z1.game.ArkanoidGame;

public class MainActivity extends AppCompatActivity {

    private ArkanoidGame arkanoidGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arkanoidGame != null) {
            arkanoidGame = arkanoidGame.copyGame();
            arkanoidGame.registerListener();
            arkanoidGame.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (arkanoidGame != null) {
            arkanoidGame.stopGame();
            arkanoidGame.unregisterListener();
        }
    }

    public void newGame(View view) {
        ArkanoidView arkanoidView = (ArkanoidView) findViewById(R.id.arkanoidView);
        if (arkanoidGame != null) {
            arkanoidGame.stopGame();
        }
        arkanoidGame = new ArkanoidGame(this, arkanoidView);
        arkanoidView.setGame(arkanoidGame);
        arkanoidView.invalidate();
        arkanoidGame.registerListener();
        arkanoidGame.start();
    }
}
