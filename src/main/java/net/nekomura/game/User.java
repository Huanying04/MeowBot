package net.nekomura.game;

import org.json.JSONObject;

import java.io.IOException;

public class User {
    // 用戶id
    private long qq;
    // 最後簽到時間
    private long lastSignedTime;
    // 最後喝妹汁時間
    private long aiekiTime;
    // 最後告白時間
    private long lastLoveDeclarationTime;
    // 好感度
    private int favorability;
    // H值
    private int hValue;
    // 點數
    private int point;
    // 暱稱
    private String nick;
    // 福袋
    private int fukubukuro;
    // 鯛魚燒
    private int taiyaiki;
    // 焦糖布丁
    private int cremeCaramel;
    // 馬卡龍
    private int macaron;
    // 珍珠奶茶
    private int bubbleMilkTea;
    // 貓貓快樂水
    private int catHappyWater;
    // 可爾喵思
    private int calMeowis;
    // 工口本
    private int eroHon;
    // 小猫娃娃
    private int kittenDoll;

    public User(long qqId) throws IOException {
        JSONObject userData = UserUtil.getUserData(qqId);
        this.qq = qqId;
        this.lastSignedTime = userData.has("lastSignedTime") ? userData.getLong("lastSignedTime") : 0;
        this.aiekiTime = userData.has("aiekiTime") ? userData.getLong("aiekiTime") : 0;
        this.lastLoveDeclarationTime = userData.has("lastLoveDeclarationTime") ? userData.getLong("lastLoveDeclarationTime") : 0;
        this.favorability = userData.has("favorability") ? userData.getInt("favorability") : 0;
        this.hValue = userData.has("hValue") ? userData.getInt("hValue") : 0;
        this.point = userData.has("point") ? userData.getInt("point") : 0;
        this.nick = userData.has("nick") ? userData.getString("nick") : null;
        this.fukubukuro = userData.has("fukubukuro") ? userData.getInt("fukubukuro") : 0;
        this.taiyaiki = userData.has("taiyaiki") ? userData.getInt("taiyaiki") : 0;
        this.cremeCaramel = userData.has("cremeCaramel") ? userData.getInt("cremeCaramel") : 0;
        this.macaron = userData.has("macaron") ? userData.getInt("macaron") : 0;
        this.bubbleMilkTea = userData.has("bubbleMilkTea") ? userData.getInt("bubbleMilkTea") : 0;
        this.catHappyWater = userData.has("catHappyWater") ? userData.getInt("catHappyWater") : 0;
        this.calMeowis = userData.has("calMeowis") ? userData.getInt("calMeowis") : 0;
        this.eroHon = userData.has("eroHon") ? userData.getInt("eroHon") : 0;
        this.kittenDoll = userData.has("kittenDoll") ? userData.getInt("kittenDoll") : 0;
    }

    @Override
    public String toString() {
        return UserUtil.userToUserDataJsonObject(this).toString();
    }

    public long getQq() {
        return qq;
    }

    public void setQq(long qq) {
        this.qq = qq;
    }

    public long getLastSignedTime() {
        return lastSignedTime;
    }

    public void setLastSignedTime(long lastSignedTime) {
        this.lastSignedTime = lastSignedTime;
    }

    public long getAiekiTime() {
        return aiekiTime;
    }

    public void setAiekiTime(long aiekiTime) {
        this.aiekiTime = aiekiTime;
    }

    public long getLastLoveDeclarationTime() {
        return lastLoveDeclarationTime;
    }

    public void setLastLoveDeclarationTime(long lastLoveDeclarationTime) {
        this.lastLoveDeclarationTime = lastLoveDeclarationTime;
    }

    public int getFavorability() {
        return favorability;
    }

    public void setFavorability(int favorability) {
        this.favorability = favorability;
    }

    public int getHValue() {
        return hValue;
    }

    public void setHValue(int hValue) {
        this.hValue = hValue;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void addFavorability(int favorability) {
        this.favorability += favorability;
    }

    public int getFukubukuro() {
        return fukubukuro;
    }

    public void setFukubukuro(int fukubukuro) {
        this.fukubukuro = fukubukuro;
    }

    public int getTaiyaiki() {
        return taiyaiki;
    }

    public void setTaiyaiki(int taiyaiki) {
        this.taiyaiki = taiyaiki;
    }

    public int getCremeCaramel() {
        return cremeCaramel;
    }

    public void setCremeCaramel(int cremeCaramel) {
        this.cremeCaramel = cremeCaramel;
    }

    public int getMacaron() {
        return macaron;
    }

    public void setMacaron(int macaron) {
        this.macaron = macaron;
    }

    public int getBubbleMilkTea() {
        return bubbleMilkTea;
    }

    public void setBubbleMilkTea(int bubbleMilkTea) {
        this.bubbleMilkTea = bubbleMilkTea;
    }

    public int getCatHappyWater() {
        return catHappyWater;
    }

    public void setCatHappyWater(int catHappyWater) {
        this.catHappyWater = catHappyWater;
    }

    public int getCalMeowis() {
        return calMeowis;
    }

    public void setCalMeowis(int calMeowis) {
        this.calMeowis = calMeowis;
    }

    public int getEroHon() {
        return eroHon;
    }

    public void setEroHon(int eroHon) {
        this.eroHon = eroHon;
    }

    public int getKittenDoll() {
        return kittenDoll;
    }

    public void setKittenDoll(int kittenDoll) {
        this.kittenDoll = kittenDoll;
    }

    public void addHValue(int hValue) {
        this.hValue += hValue;
    }

    public void addPoint(int point) {
        this.point += point;
    }

    public void addFukubukuro (int fukubukuro) {
        this.fukubukuro += fukubukuro;
    }

    public void addTaiyaki(int taiyaiki) {
        this.taiyaiki += taiyaiki;
    }

    public void addCremeCaramel(int cremeCaramel) {
        this.cremeCaramel += cremeCaramel;
    }

    public void addMacaron(int macaron) {
        this.macaron += macaron;
    }

    public void addBubbleMilkTea(int bubbleMilkTea) {
        this.bubbleMilkTea += bubbleMilkTea;
    }

    public void addCatHappyWater(int catHappyWater) {
        this.catHappyWater += catHappyWater;
    }

    public void addCalMeowis(int calMeowis) {
        this.calMeowis += calMeowis;
    }

    public void addEroHon(int eroHon) {
        this.eroHon += eroHon;
    }

    public void addKittenDoll(int kittenDoll) {
        this.kittenDoll += kittenDoll;
    }
}
