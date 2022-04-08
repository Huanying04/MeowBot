package net.nekomura.job;

import com.IceCreamQAQ.Yu.annotation.Cron;
import com.IceCreamQAQ.Yu.annotation.JobCenter;
import com.icecreamqaq.yuq.YuQ;
import net.nekomura.game.GameSystem;
import net.nekomura.game.GameSystemUtils;

import javax.inject.Inject;
import java.io.IOException;

@JobCenter
public class AutoSaveJob {
    @Cron("10m")
    public void ten() throws IOException {
        GameSystemUtils.saveUserData(GameSystem.USER_LIST);
        System.out.println("已自動存檔");
    }
}
