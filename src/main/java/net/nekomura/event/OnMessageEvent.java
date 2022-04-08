package net.nekomura.event;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import com.icecreamqaq.yuq.event.GroupMessageEvent;
import com.icecreamqaq.yuq.message.*;
import net.nekomura.game.GameSystem;
import net.nekomura.game.GameSystemUtils;
import net.nekomura.game.User;
import net.nekomura.game.UserUtil;
import net.nekomura.utils.BotUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventListener
public class OnMessageEvent {

    static int SAVE_COOL_DOWN = 0;

    @Inject
    private YuQ yuq;

    @Inject
    private MessageItemFactory mif;

    @Event(weight = Event.Weight.high)
    public void onGroupMessageHigh(GroupMessageEvent event) {
        if (!BotUtils.isApplyGroup(event.getGroup().getId()))
            event.setCancel(true);
    }

    @Event(weight = Event.Weight.normal)
    public void onGroupMessage(GroupMessageEvent event) throws IOException {
        // 每經過20次接收訊息即自動存檔
        if ((++SAVE_COOL_DOWN) % 20 == 0) {
            GameSystemUtils.saveUserData(GameSystem.USER_LIST);
            System.out.println("已自動存檔");
            SAVE_COOL_DOWN = 0;
        }
    }

    /**
     * 處理辭庫及回覆。
     * 醜到快炸掉的程式碼連我都不知道該怎麼搞，反正能跑就行吧
     * @param event 群消息事件
     */
    @Event(weight = Event.Weight.normal)
    public void handleAnswer(GroupMessageEvent event) {
        Message message = event.getMessage();
        MessageItemChain list = message.getBody();
        StringBuilder raw = new StringBuilder(); // 傳入之訊息文本化
        for (MessageItem messageItem : list) {
            if (messageItem instanceof Text) {
                raw.append(((Text) messageItem).getText());
            }else if (messageItem instanceof At) {
                raw.append("[at=").append(((At) messageItem).getUser()).append("]");
            }else if (messageItem instanceof Face) {
                raw.append("[face=").append(((Face) messageItem).getFaceId()).append("]");
            }else if (messageItem instanceof Image) {
                raw.append("[imageUrl=").append(((Image) messageItem).getUrl()).append("]");
            }else {
                // 含有其他不處理的物件，直接忽略
                return;
            }
        }

        JSONObject dialogues = GameSystem.DIALOGUES;
        Set<String> keys = dialogues.keySet();
        AtomicReference<String> key = new AtomicReference<>();
        AtomicReference<Map<String, List<Integer>>> fuzzySeqAndId = new AtomicReference<>(new HashMap<>());
        AtomicReference<Pattern> p = new AtomicReference<>(Pattern.compile(""));
        AtomicReference<Matcher> m = new AtomicReference<>();
        AtomicInteger seq = new AtomicInteger(2);

        if (keys.stream().filter(x -> {
            key.set(x);

            // 將所有模糊對照關鍵字 [fuzzy:xxx] replace 為 (.*) 並記錄其位置
            Pattern fuzzyPattern = Pattern.compile("(\\[fuzzy:[a-zA-Z0-9]*])");
            Matcher fuzzyMatcher = fuzzyPattern.matcher(x);
            while (fuzzyMatcher.find()) {
                String id = fuzzyMatcher.group(1);
                if (!fuzzySeqAndId.get().containsKey(id)) {
                    List<Integer> l = new ArrayList<>();
                    l.add(seq.getAndAdd(2));
                    fuzzySeqAndId.get().put(id, l);
                }else {
                    List<Integer> l = fuzzySeqAndId.get().get(id);
                    l.add(seq.getAndAdd(2));
                    fuzzySeqAndId.get().put(id, l);
                }
                x = x.replaceFirst("(\\[fuzzy:)[a-zA-Z0-9]*(\\])", ")(.+)(");
                fuzzyMatcher = fuzzyPattern.matcher(x);
            }
            x = "(" + x + ")";

            // 檢驗最終處理後的pattern是否能對照上message
            p.set(Pattern.compile(x));
            m.set(p.get().matcher(raw.toString()));
            if (!m.get().matches()) {
                fuzzySeqAndId.set(new HashMap<>());
                seq.set(2);
                return false;
            }
            return true;
        }).findAny().orElse(null) != null) {
            Matcher matcher = m.get();

            // 確認是否每個相同id的模糊對照都是相同的
            // 對我知道這個不應該出現在這種地方，但我就爛 QAQ
            for (String id : fuzzySeqAndId.get().keySet()) {
                List<Integer> l = fuzzySeqAndId.get().get(id);
                for (int i = 0; i < l.size() - 1; i++) {
                    if (!matcher.group(l.get(i)).equals(matcher.group(l.get(i + 1)))) return;
                }
            }

            JSONArray answersArr = dialogues.getJSONObject(key.get()).getJSONArray("answer");
            String answer = answersArr.getString(ThreadLocalRandom.current().nextInt(answersArr.length()));

            for (String id : fuzzySeqAndId.get().keySet()) {
                answer = answer.replace(id, matcher.group(fuzzySeqAndId.get().get(id).get(0)));
            }

            List<String> answerElements = new ArrayList<>();
            for (int i = 0; i < answer.length(); i++) {
                if (answer.charAt(i) == '[') {
                    StringBuilder elementId = new StringBuilder();
                    while (answer.charAt(i) != ']') {
                        elementId.append(answer.charAt(i));
                        i++;
                    }
                    //elementId.append(']');
                    answerElements.add(elementId.toString());
                }else {
                    StringBuilder sb = new StringBuilder();
                    while (i < answer.length() && answer.charAt(i) != '[') {
                        sb.append(answer.charAt(i));
                        i++;
                    }
                    answerElements.add(sb.toString());
                    i--;
                }
            }

            Message sendMessage = new Message();
            Member sender = event.getSender();
            Group group = event.getGroup();
            User user = UserUtil.getUserFromUserList(sender.getId());

            for (String element : answerElements) {
                if (element.equals("[at")) {
                    sendMessage = sendMessage.plus(mif.at(sender.getId()));
                }else if (element.equals("[nick")) {
                    String nick = UserUtil.getUserNickOrMemberName(user, group);
                    sendMessage = sendMessage.plus(nick);
                }else if (element.equals("[lsb")) {
                    sendMessage = sendMessage.plus("[");
                }else if (element.equals("[rsb")) {
                    sendMessage = sendMessage.plus("]");
                }else if (element.matches("(\\[imageUrl=)(.+)")) {
                    String url = element.split("=")[1];
                    sendMessage = sendMessage.plus(mif.imageByUrl(url));
                }else if (element.matches("(\\[face=)(.+)")) {
                    String faceId = element.split("=")[1];
                    sendMessage = sendMessage.plus(mif.face(Integer.parseInt(faceId)));
                }else if (element.matches("(\\[at=)(.+)")) {
                    String qq = element.split("=")[1];
                    sendMessage = sendMessage.plus(mif.at(Long.parseLong(qq)));
                }else {
                    sendMessage = sendMessage.plus(element);
                }
            }

            group.sendMessage(sendMessage);
        }
    }
}
