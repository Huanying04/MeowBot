package net.nekomura.controller;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.github.hiking93.graphemesplitterlite.GraphemeSplitter;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.message.Message;
import com.icecreamqaq.yuq.message.MessageItem;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import com.icecreamqaq.yuq.message.Text;
import net.nekomura.game.GameSystem;
import net.nekomura.game.User;
import net.nekomura.game.UserUtil;
import com.icecreamqaq.yuq.entity.Group;
import net.nekomura.utils.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@GroupController
public class GameController {
    @Inject
    private YuQ yuq;

    @Inject
    private MessageItemFactory mif;

    /***
     * Before 则为具体的控制器的动作前置验证，也可以称作拦截器，负责在 Action 处理消息之前进行验证。
     *
     * Before 方法接收 0 - 多个参数，通常，您所写下的参数，将会以名称匹配来进行依赖注入。
     *   支持注入的名称 - 注入的内容
     *     qq - 发送消息的 QQ 账号
     *     group - 发送消息的 QQ 群号
     *     message - 具体的 Message 对象
     *     messageId - 消息 ID
     *     sourceMessage - 未经处理的源消息内容，则为具体的 Runtime 的消息内容
     *     actionContext - 当前消息的 ActionContext 对象
     *     以及，您 Before 传递回来的需要保存的对象。
     *
     * Before 方法可以接受任何类型的返回值，当您返回了一个值的时候，框架会帮您保存起来，名称则为将类名的第一个字母转化小写后的名字。
     *
     * Before 方法可以抛出异常，来作为验证失败的中断处理方法。
     * 当您抛出了一个 Message 类型的异常后，如果您没有设置任何接收的 QQ，或 QQ 群，那么我们将会将消息发送至当前消息来源者，如果您设置了接收对象，那么发送至您的接收对象。
     * 当您想中断处理链路，并且不进行任何返回的时候，您可以抛出 DoNone 类型的异常。
     *
     * 一个 Controller 类内，可以接受多个 Before，他们按照一定的顺序依次执行，当所有 Before 执行完成之后，将继续执行 Action。
     */
    @Before
    public void before(long qq) throws IOException {
        if (GameSystem.USER_LIST.stream().noneMatch(user -> user.getQq() == qq))
            GameSystem.USER_LIST.add(new User(qq));
        // throw new DoNone();
    }

    @Action("签到")
    public void sign(Group group, long qq) {
        User user = UserUtil.getUserFromUserList(qq);
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Calendar lastSignTime = Calendar.getInstance();
        lastSignTime.setTime(new Date(user.getLastSignedTime()));
        lastSignTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // 不是在簽到之後發的 (不可能吧)
        //if (!now.after(lastSignTime)) throw new DoNone();

        // 是今天，則
        if (now.get(Calendar.DAY_OF_YEAR) == lastSignTime.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == lastSignTime.get(Calendar.YEAR))
            throw mif.text("你今天已经签到过了，请明天再来吧！人(￣ω￣;)").toMessage().toThrowable();

        user.setLastSignedTime(now.getTimeInMillis());
        int gain = ThreadLocalRandom.current().nextInt(8, 11);
        user.addPoint(gain);
        group.sendMessage("签到成功！+" + gain + "点数");

        if (ThreadLocalRandom.current().nextFloat() >= 0.2f) throw new DoNone();

        user.addFukubukuro(1);
        group.sendMessage("嗯？怎么地上有一个袋子？");
        group.sendMessage("福袋+1");

        throw new DoNone();
    }

    @Action("喝妹汁")
    public void drinkAiEki(Group group, long qq) {
        User user = UserUtil.getUserFromUserList(qq);
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Calendar lastSignTime = Calendar.getInstance();
        lastSignTime.setTime(new Date(user.getAiekiTime()));
        lastSignTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // 不是在簽到之後發的 (不可能吧)
        //if (!now.after(lastSignTime)) throw new DoNone();

        // 是今天，則
        if (now.get(Calendar.DAY_OF_YEAR) == lastSignTime.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == lastSignTime.get(Calendar.YEAR))
            throw mif.text("今天的妹汁已经喝过了吧！那就等明天再来啦！(。·ˇ_ˇ·。)").toMessage().toThrowable();

        int hGain = ThreadLocalRandom.current().nextInt(1, 4); // H值
        int fGain = ThreadLocalRandom.current().nextInt(2, 6); // 好感度

        user.addHValue(hGain);
        user.addFavorability(fGain);

        group.sendMessage("妹汁真好喝！咕嘟咕嘟");
        group.sendMessage(String.format("H值+%d\n好感度+%d", hGain, fGain));
        user.setAiekiTime(now.getTimeInMillis());

        throw new DoNone();
    }

    @Action("最喜欢喵喵了")
    public void loveDeclare(Group group, long qq) {
        User user = UserUtil.getUserFromUserList(qq);
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Calendar lastSignTime = Calendar.getInstance();
        lastSignTime.setTime(new Date(user.getLastLoveDeclarationTime()));
        lastSignTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // 不是在簽到之後發的 (不可能吧)
        //if (!now.after(lastSignTime)) throw new DoNone();

        // 是今天，則
        if (now.get(Calendar.DAY_OF_YEAR) == lastSignTime.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == lastSignTime.get(Calendar.YEAR))
            throw new DoNone();

        int[] pool = {-3, -2, -1, 1, 2, 3, 4, 5};
        int gain = pool[ThreadLocalRandom.current().nextInt(0, pool.length)];
        user.addFavorability(gain);

        if (gain >= 0) {
            group.sendMessage(mif.text("突然间告白什么的......喵喵会害羞啦！(／≧ω＼)").toMessage());
        }else {
            group.sendMessage("哼！只有随便的人才会随便把喜欢挂在嘴边(╯⊙ ω ⊙╰ )");
        }
        group.sendMessage(String.format("%+d好感度", gain));
        user.setLastLoveDeclarationTime(now.getTimeInMillis());

        throw new DoNone();
    }

    @Action("我的数据")
    @Synonym({"我的统计"})
    public void myStat(Group group, long qq) {
        yuq.refreshGroups();
        User user = UserUtil.getUserFromUserList(qq);

        StringBuilder sb = new StringBuilder();
        sb.append("    【用户数据】    \n");
        sb.append("用户名　　: ").append(group.getMembers().get(qq).getName()).append("\r\n");
        sb.append("称呼　　　: ").append((user.getNick() == null) ? "（无）" : user.getNick()).append("\r\n");
        sb.append("好感度　　: ").append(user.getFavorability()).append("\r\n");
        sb.append("Ｈ值　　　: ").append(user.getHValue()).append("\r\n");
        sb.append("点数　　　: ").append(user.getPoint()).append("\r\n");
        sb.append("综合评比　: ").append(String.format("%.2f", UserUtil.getUserIntegratePoint(user))).append("\r\n");
        sb.append("（欲查看所持有道具可发送\"我的道具\"查看）");

        throw mif.text(sb.toString()).toMessage().toThrowable();
    }

    @Action("我的道具")
    @Synonym({"我的物品", "背包"})
    public void myInventory(Group group, long qq) {
        yuq.refreshGroups();
        User user = UserUtil.getUserFromUserList(qq);
        String nick = UserUtil.getUserNickOrMemberName(user, group);

        StringBuilder sb = new StringBuilder();
        sb.append("     【").append(nick).append("的道具栏】     \n");
        sb.append("福袋　　　：").append(user.getFukubukuro()).append("\r\n");
        sb.append("鲷鱼烧　　：").append(user.getTaiyaiki()).append("\r\n");
        sb.append("焦糖布丁　：").append(user.getCremeCaramel()).append("\r\n");
        sb.append("马卡龙　　：").append(user.getMacaron()).append("\r\n");
        sb.append("珍珠奶茶　：").append(user.getBubbleMilkTea()).append("\r\n");
        sb.append("猫猫快乐水：").append(user.getCatHappyWater()).append("\r\n");
        sb.append("可尔喵思　：").append(user.getCalMeowis()).append("\r\n");
        sb.append("小猫娃娃　：").append(user.getKittenDoll()).append("\r\n");
        sb.append("工口本　　：").append(user.getEroHon()).append("\r\n");
        sb.append("（欲使用福袋请发送\"开启福袋\"，使用食物饮料类物品发送\"喂食 【道具名称】\"，除上面提到的以外则发送\"给喵喵【道具名称】\"）");

        throw mif.text(sb.toString()).toMessage().toThrowable();
    }

    @Action("商店")
    public void seeStore(long qq) {
        User user = UserUtil.getUserFromUserList(qq);
        String str = "     【商店】     \r\n" +
                "剩余点数: " + user.getPoint() + "点\r\n" +
                "福袋　　　　　" + "35点" + "\r\n" +
                "（开启后有随机物品）" + "\r\n" +
                "小猫娃娃　　　" + "25点" + "\r\n" +
                "（制作精美的娃娃，讨喜的模样相当可爱）" + "\r\n" +
                "猫猫快乐水　　" + "65点" + "\r\n" +
                "（足以让猫猫十分兴奋的饮料）" + "\r\n" +
                "可尔喵思　　　" + "85点" + "\r\n" +
                "（白色的液体，甜甜的饮料，好喝！）" + "\r\n" +
                "工口本　　　　" + "100点" + "\r\n" +
                "（内容尽是是些色色的东西，未成年勿碰！）" + "\r\n" +
                "（欲购买道具请发送\"购买 【商品名称】\"完成购买）";

        throw mif.text(str).toMessage().toThrowable();
    }

    @Action("购买 {goods}")
    public void buy(long qq, String goods) {
        yuq.refreshGroups();
        User user = UserUtil.getUserFromUserList(qq);

        switch (goods) {
            case "福袋":
                if (user.getPoint() < 35)
                    break;
                user.addFukubukuro(1);
                user.addPoint(-35);
                throw mif.text("已购买福袋*1\r\n点数-35").toMessage().toThrowable();
            case "小猫娃娃":
                if (user.getPoint() < 25)
                    break;
                user.addKittenDoll(1);
                user.addPoint(-25);
                throw mif.text("已购买小猫娃娃*1\r\n点数-25").toMessage().toThrowable();
            case "猫猫快乐水":
                if (user.getPoint() < 65)
                    break;
                user.addCatHappyWater(1);
                user.addPoint(-65);
                throw mif.text("已购买猫猫快乐水*1\r\n点数-65").toMessage().toThrowable();
            case "可尔喵思":
                if (user.getPoint() < 85)
                    break;
                user.addCalMeowis(1);
                user.addPoint(-85);
                throw mif.text("已购买可尔喵思*1\r\n点数-85").toMessage().toThrowable();
            case "工口本":
                if (user.getPoint() < 100)
                    break;
                user.addEroHon(1);
                user.addPoint(-100);
                throw mif.text("已购买工口本*1\r\n点数-100").toMessage().toThrowable();
            default:
                throw mif.text("商店里没有卖这个物品哦( →_→)").toMessage().toThrowable();
        }

        throw mif.text("点数不足，无法购买").toMessage().toThrowable();
    }

    @Action("点数排行榜")
    @Synonym("点数排名")
    public void pointRank(Group group) {
        List<User> userList = new ArrayList<>(GameSystem.USER_LIST);
        userList.sort(
                Comparator.comparing(User::getPoint).thenComparing(UserUtil::getUserIntegratePoint).reversed()
        );

        StringBuilder sb = new StringBuilder("    【点数排名】    ");
        for (int i = 0; i < 10 && i < userList.size(); i++) {
            User user = userList.get(i);
            String nick = user.getNick() == null ? "" : "(" + user.getNick() + ")";
            sb.append("\r\n【").append(i + 1).append("】").append(user.getQq()).append(nick).append(": ").append(user.getPoint());
        }
        throw mif.text(sb.toString()).toMessage().toThrowable();
    }

    @Action("好感度排行榜")
    @Synonym("好感度排名")
    public void favorabilityRank(Group group) {
        List<User> userList = new ArrayList<>(GameSystem.USER_LIST);
        userList.sort(
                Comparator.comparing(User::getFavorability).thenComparing(UserUtil::getUserIntegratePoint).reversed()
        );

        StringBuilder sb = new StringBuilder("    【好感度排名】    ");
        for (int i = 0; i < 10 && i < userList.size(); i++) {
            User user = userList.get(i);
            String nick = user.getNick() == null ? "" : "(" + user.getNick() + ")";
            sb.append("\r\n【").append(i + 1).append("】").append(user.getQq()).append(nick).append(": ").append(user.getFavorability());
        }
        throw mif.text(sb.toString()).toMessage().toThrowable();
    }

    @Action("H值排行榜")
    @Synonym("H值排名")
    public void hValueRank(Group group) {
        List<User> userList = new ArrayList<>(GameSystem.USER_LIST);
        userList.sort(
                Comparator.comparing(User::getHValue).thenComparing(UserUtil::getUserIntegratePoint).reversed()
        );

        StringBuilder sb = new StringBuilder("    【H值排名】    ");
        for (int i = 0; i < 10 && i < userList.size(); i++) {
            User user = userList.get(i);
            String nick = user.getNick() == null ? "" : "(" + user.getNick() + ")";
            sb.append("\r\n【").append(i + 1).append("】").append(user.getQq()).append(nick).append(": ").append(user.getHValue());
        }
        throw mif.text(sb.toString()).toMessage().toThrowable();
    }

    @Action("综合排行榜")
    @Synonym("综合排名")
    public void integratePointRank(Group group) {
        List<User> userList = new ArrayList<>(GameSystem.USER_LIST);
        userList.sort(
                Comparator.comparing(UserUtil::getUserIntegratePoint).thenComparing(User::getFavorability).thenComparing(User::getHValue).thenComparing(User::getPoint).reversed()
        );

        StringBuilder sb = new StringBuilder("    【综合排名】    ");
        for (int i = 0; i < 10 && i < userList.size(); i++) {
            User user = userList.get(i);
            String nick = user.getNick() == null ? "" : "(" + user.getNick() + ")";
            sb.append("\r\n【").append(i + 1).append("】").append(user.getQq()).append(nick).append(": ").append(String.format("%.2f", UserUtil.getUserIntegratePoint(user)));
        }
        throw mif.text(sb.toString()).toMessage().toThrowable();
    }

    @Action("开启福袋")
    public void openFukubukuro(Group group, long qq) {
        User user = UserUtil.getUserFromUserList(qq);

        if (user.getFukubukuro() == 0) {
            MessageItem nick = UserUtil.getUserNickOrAt(user, group, mif);
            throw nick.plus("没有福袋呢").toMessage().toThrowable();
        }

        float rand = ThreadLocalRandom.current().nextFloat();
        if (rand < 0.4) {
            group.sendMessage("好可惜呢，里面什么都没有哦！");
        }else if (rand < 0.52) {
            group.sendMessage("哇！福袋里面开出来了1个鲷鱼烧！\r\n鲷鱼烧+1");
            user.addTaiyaki(1);
        }else if (rand < 0.64) {
            group.sendMessage("哇！福袋里面开出来了1个焦糖布丁！\r\n焦糖布丁+1");
            user.addCremeCaramel(1);
        }else if (rand < 0.76) {
            group.sendMessage("哇！福袋里面开出来了1个马卡龙！\r\n马卡龙+1");
            user.addMacaron(1);
        }else if (rand < 0.88) {
            group.sendMessage("哇！福袋里面开出来了1个珍珠奶茶！\r\n珍珠奶茶+1");
            user.addBubbleMilkTea(1);
        }else {
            group.sendMessage("哇！福袋里面开出来了1个猫猫快乐水！\r\n猫猫快乐水+1");
            user.addCatHappyWater(1);
        }

        user.addPoint(6);
        user.addFukubukuro(-1);
        throw mif.text("+6点数").toMessage().toThrowable();
    }

    @Action("喂食 {food}")
    public void feed(Group group, long qq, String food) throws IOException {
        User user = UserUtil.getUserFromUserList(qq);
        MessageItem nick = UserUtil.getUserNickOrAt(user, group, mif);
        String foodId = "";
        Message foodReply = null;

        int favGain = 0;
        int pointGain = 0;
        int hValueGain = 0;

        switch (food) {
            case "鲷鱼烧":
                foodId = "taiyaki";
                foodReply = mif.text("鲷鱼烧！好吃好吃！是说").plus(nick).plus("知道鲷鱼烧应该从头开始吃还是尾巴开始吃呢？").toMessage();
                favGain = ThreadLocalRandom.current().nextInt(3, 6);
                break;
            case "焦糖布丁":
                foodId = "creme_caramel";
                foodReply = mif.text("焦糖布丁！喵喵最喜欢了！阿姆一口直接吃掉！o(*´○｀*)o ｧ‐ﾝ o(*´～｀*)o ﾓｸﾞﾓｸﾞ").toMessage();
                favGain = ThreadLocalRandom.current().nextInt(6, 13);
                break;
            case "马卡龙":
                foodId = "macaron";
                foodReply = mif.text("马卡龙！好吃！一口吃掉！(@^p^@) 阿姆阿姆好次♪").toMessage();
                favGain = ThreadLocalRandom.current().nextInt(5, 11);
                break;
            case "珍珠奶茶":
                foodId = "bubble_milk_tea";
                foodReply = mif.text("好喝！谢谢").plus(nick).plus("！咕嘟咕嘟  ▇ o(^- ^)").toMessage();
                favGain = ThreadLocalRandom.current().nextInt(7, 13);
                break;
            case "猫猫快乐水":
                foodId = "cat_happy_water";
                foodReply = mif.text("猫猫快乐水！耶耶耶耶耶耶耶啊啊啊啊啊啊啊啊！").toMessage();
                favGain = ThreadLocalRandom.current().nextInt(5, 15);
                pointGain = ThreadLocalRandom.current().nextInt(5, 13);
                break;
            case "可尔喵思":
                foodId = "calmeowis";
                foodReply = mif.text("可尔妹思！喵喵最喜欢喝这种白色的饮料了！").toMessage();
                favGain = ThreadLocalRandom.current().nextInt(6, 13);
                hValueGain = ThreadLocalRandom.current().nextInt(5, 10);
                break;
            default:
                foodReply = mif.text("?").toMessage();
                break;
        }

        Integer count = UserUtil.getItemCount(user, foodId);

        if (count == null) {
            File file = FileUtils.getResourceFile("./images/cat_confuse.jpg");
            throw mif.text("你想喂食喵喵什么东西呢owo").plus(mif.imageByFile(file)).toMessage().toThrowable();
        }

        if (count == 0) {
            throw nick.plus("没有" + food + "呢").toMessage().toThrowable();
        }

        UserUtil.addItems(user, foodId, -1);
        group.sendMessage(foodReply);
        if (favGain > 0)
            group.sendMessage("好感度+" + favGain);
        if (pointGain > 0)
            group.sendMessage("点数+" + pointGain);
        if (hValueGain > 0)
            group.sendMessage("H值+" + hValueGain);

        throw new DoNone();
    }

    @Action("给喵喵工口本")
    public void giveEroHon(Group group, long qq) throws IOException {
        User user = UserUtil.getUserFromUserList(qq);
        MessageItem nickOrAt = UserUtil.getUserNickOrAt(user, group, mif);

        if (user.getEroHon() == 0) {
            throw nickOrAt.plus("没有工口本呢？\r\n原来是正经人啊？真无趣￣Д￣　＝３").toMessage().toThrowable();
        }

        int pGain = ThreadLocalRandom.current().nextInt(5, 10);
        int fGain = ThreadLocalRandom.current().nextInt(6, 15);
        int hGain = ThreadLocalRandom.current().nextInt(10, 20);
        File file = FileUtils.getResourceFile("./images/catTere.jpg");

        group.sendMessage(mif.text("哇！谢谢").plus(nickOrAt).plus("的工口本！").toMessage());
        group.sendMessage(mif.text("喔？原来")
                .plus(nickOrAt)
                .plus("喜欢这样子的东西啊？真是恶趣味呢！\n")
                .plus(mif.imageByFile(file)).toMessage());
        user.addEroHon(-1);
        user.addPoint(pGain);
        user.addFavorability(fGain);
        user.addHValue(hGain);

        group.sendMessage(mif.text(String.format("点数+%d\n好感度+%d\nH值+%d", pGain, fGain, hGain)).toMessage());

        throw new DoNone();
    }

    @Action("给喵喵猫咪娃娃")
    public void giveKittenDoll(Group group, long qq) throws IOException{
        User user = UserUtil.getUserFromUserList(qq);
        MessageItem nickOrAt = UserUtil.getUserNickOrAt(user, group, mif);

        if (user.getKittenDoll() == 0) {
            throw nickOrAt.plus("没有猫咪娃娃呢").toMessage().toThrowable();
        }

        int gain = ThreadLocalRandom.current().nextInt(6, 13);
        group.sendMessage(mif.text("哇！好可爱的猫猫啊！谢谢").plus(nickOrAt).plus("！(///≧∀≦///)").toMessage());
        user.addKittenDoll(-1);
        user.addFavorability(gain);
        group.sendMessage(mif.text(String.format("好感度+%d", gain)).toMessage());

        throw new DoNone();
    }

    @Action("我要专属群头衔 {name}")
    @Synonym({"我要专属头衔 {name}", "我要群专属头衔 {name}"})
    public void setCustomGroupRank(Group group, long qq, MessageItem name) {
        yuq.refreshGroups();
        if (group.getBot().getPermission() < 2)
            throw mif.text("喵喵不是群主，没法设置专属头衔！").toMessage().toThrowable();

        if (!(name instanceof Text))
            throw mif.text("头衔必须只能由文字（包括emoji）组成").toMessage().toThrowable();

        String nameCard = ((Text) name).getText();
        GraphemeSplitter gs = new GraphemeSplitter();

        if (gs.split(nameCard).size() > 6)
            throw mif.text("头衔不得超过6个字").toMessage().toThrowable();

        group.getMembers().get(qq).setTitle(nameCard);
        throw mif.text("好的！已帮").plus(mif.at(qq)).plus("设置群头衔为" + nameCard + "了！").toMessage().toThrowable();
    }

    @Action("喵喵，叫我{nick}")
    public void setNick(long qq, String nick) {
        User user = UserUtil.getUserFromUserList(qq);
        user.setNick(nick);
        throw mif.text("好的！那么喵喵以后就改叫").plus(mif.at(qq)).plus("为" + nick + "啦！").toMessage().toThrowable();
    }

    @Action("喵喵我想被禁言")
    @Synonym({"我想被禁言"})
    public void timeoutDraw(Group group, long qq) {
        yuq.refreshGroups();
        User user = UserUtil.getUserFromUserList(qq);

        Member member = group.getMembers().get(qq);
        int botPermission = group.getBot().getPermission();
        int userPermission = member.getPermission();

        if (group.getBot().getPermission() < 1)
            throw mif.text("喵喵因为不是管理员的关系，没办法禁言呢（＞人＜）抱歉！！").toMessage().toThrowable();

        if (userPermission >= botPermission)
            throw mif.text("喵喵因为权限不足的关系，没办法禁言呢（＞人＜）抱歉！！").toMessage().toThrowable();

        int[] banTimePool = {60 * 10, 60 * 30, 60 * 60, 60 * 120, 60 * 180, 60 * 360, 60 * 480};
        int time = ThreadLocalRandom.current().nextInt(banTimePool.length);
        member.ban(time);
        throw mif.text("好的！已帮").plus(mif.at(qq)).plus("禁言" + (time / 60f) + "分钟啦！").toMessage().toThrowable();
    }
}
