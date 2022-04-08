package net.nekomura.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.nekomura.utils.FileUtils;
import org.json.JSONObject;

public class GameSystem {
    public static List<User> USER_LIST = new ArrayList<>();
    public static JSONObject DIALOGUES = new JSONObject();

    static {
        // read all userdata files
        List<File> userData = FileUtils.getAllFilesInDir(new File("./userdata"));
        if (userData != null) {
            for (File f : userData) {
                long id = Long.parseLong(f.getName());
                try {
                    USER_LIST.add(new User(id));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // read dialogues file
        String content = "{}";
        try {
            content = FileUtils.readFile(new File("./config/dialogues.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DIALOGUES = new JSONObject(content);
    }
}
