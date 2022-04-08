package net.nekomura.game;

import net.nekomura.utils.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GameSystemUtils {
    public static void saveUserData(List<User> userList) throws IOException {
        for (User user : userList) {
            FileUtils.writeFile(new File("./userdata/" + user.getQq()), UserUtil.userToUserDataJsonObject(user).toString());
        }
    }

    public static void refreshDialogues() throws IOException {
        String content;
        content = FileUtils.readFile(new File("./config/dialogues.json"));
        GameSystem.DIALOGUES = new JSONObject(content);
    }
}
