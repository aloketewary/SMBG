package apexbio.smbg;

/**
 * Created by A1302 on 2015/7/7.
 */
import android.os.Bundle;

public class GameActivity extends DashBoardActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, false, true);
    }
}
