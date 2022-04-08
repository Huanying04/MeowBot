package net.nekomura.game;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import net.nekomura.utils.FileUtils;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.*;

public class UserUtil {
    @Inject
    private MessageItemFactory mif;

    public static JSONObject getUserData(long qq) throws IOException {
        File dataFile = new File("./userdata/" + qq);
        if(dataFile.exists()) {
            String content = FileUtils.readFile(dataFile);
            return new JSONObject(content);
        }else {
            System.out.println(dataFile.getParentFile());
            dataFile.getParentFile().mkdirs();
            dataFile.createNewFile();
            FileUtils.writeFile(dataFile, "{}");
            return new JSONObject("{}");
        }
    }

    public static JSONObject userToUserDataJsonObject(User user) {
        JSONObject userdata = new JSONObject();
        userdata.put("lastSignedTime", user.getLastSignedTime());
        userdata.put("aiekiTime", user.getAiekiTime());
        userdata.put("lastLoveDeclarationTime", user.getLastLoveDeclarationTime());
        userdata.put("favorability", user.getFavorability());
        userdata.put("hValue", user.getHValue());
        userdata.put("point", user.getPoint());
        userdata.put("nick", user.getNick());
        userdata.put("fukubukuro", user.getFukubukuro());
        userdata.put("taiyaki", user.getTaiyaiki());
        userdata.put("cremeCaramel", user.getCremeCaramel());
        userdata.put("macaron", user.getMacaron());
        userdata.put("bubbleMilkTea", user.getBubbleMilkTea());
        userdata.put("catHappyWater", user.getCatHappyWater());
        userdata.put("calMeowis", user.getCalMeowis());
        userdata.put("eroHon", user.getEroHon());
        userdata.put("kittenDoll", user.getKittenDoll());
        return userdata;
    }

    public static double getUserIntegratePoint(User user) {
        double bonus = user.getFavorability() * user.getHValue();
        if (bonus < 0) bonus = 0;
        return Math.pow(bonus, 1d/4d) + 0.6 * user.getFavorability() + 0.3 * user.getHValue();
    }

    public static User getUserFromUserList(long qq) {
        return GameSystem.USER_LIST.stream()
                .filter(user -> user.getQq() == qq)
                .findAny()
                .orElse(null);
    }

    /**
     * 指定用戶增加所持物品或點數
     * @param user 指定用戶的User物件
     * @param itemName 指定要增加物品的名稱
     * @param gain 指定要增加的數量
     * @return itemName是否可使用 (存在該物品)
     */
    public static boolean addItems(User user, String itemName, int gain) {
        switch (itemName) {
            case "point":
                user.addPoint(gain);
                break;
            case "favorability":
                user.addFavorability(gain);
                break;
            case "hValue":
                user.addHValue(gain);
                break;
            case "fukubukuro":
                user.addFukubukuro(gain);
                break;
            case "taiyaki":
                user.addTaiyaki(gain);
                break;
            case "creme_caramel":
                user.addCremeCaramel(gain);
                break;
            case "macaron":
                user.addMacaron(gain);
                break;
            case "bubble_milk_tea":
                user.addBubbleMilkTea(gain);
                break;
            case "cat_happy_water":
                user.addCatHappyWater(gain);
                break;
            case "calmeowis":
                user.addCalMeowis(gain);
                break;
            case "erohon":
                user.addEroHon(gain);
                break;
            case "kitten_doll":
                user.addKittenDoll(gain);
                break;
            default:
                return false;
        }

        return true;
    }

    public static Integer getItemCount(User user, String itemName) {
        switch (itemName) {
            case "point":
                return user.getPoint();
            case "favorability":
                return user.getFavorability();
            case "hValue":
                return user.getHValue();
            case "fukubukuro":
                return user.getFukubukuro();
            case "taiyaki":
                return user.getTaiyaiki();
            case "creme_caramel":
                return user.getCremeCaramel();
            case "macaron":
                return user.getMacaron();
            case "bubble_milk_tea":
                return user.getBubbleMilkTea();
            case "cat_happy_water":
                return user.getCatHappyWater();
            case "calmeowis":
                return user.getCalMeowis();
            case "erohon":
                return user.getEroHon();
            case "kitten_doll":
                return user.getKittenDoll();
            default:
                return null;
        }
    }

    public static String getUserNickOrMemberName(User user, Group group) {
        return user.getNick() == null ? group.getMembers().get(user.getQq()).getName() : user.getNick();
    }

    public static String getUserNickOrNone(User user, Group group) {
        return user.getNick() == null ? "（无）" : user.getNick();
    }

    public static MessageItem getUserNickOrAt(User user, Group group, MessageItemFactory mif) {
        return user.getNick() == null ? mif.at(user.getQq()) : mif.text(user.getNick());
    }
}
