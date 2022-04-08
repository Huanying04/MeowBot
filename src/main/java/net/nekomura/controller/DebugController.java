package net.nekomura.controller;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.annotation.Before;
import com.IceCreamQAQ.Yu.annotation.Synonym;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.*;
import net.nekomura.game.GameSystem;
import net.nekomura.game.GameSystemUtils;
import net.nekomura.game.User;
import net.nekomura.game.UserUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@PrivateController
@GroupController
public class DebugController {
    @Inject
    private YuQ yuq;

    @Inject
    private MessageItemFactory mif;

    @Before
    public void before(long qq) throws IOException {
        if (qq != 1280056591L)
            throw new DoNone();
    }

    @Action("存档")
    @Synonym({"save"})
    public void save(long qq) throws IOException {
        GameSystemUtils.saveUserData(GameSystem.USER_LIST);
        throw mif.text("已存档").toMessage().toThrowable();
    }

    @Action("刷新词库")
    @Synonym("refreshDialogues")
    public void refreshDialogues() throws IOException {
        GameSystemUtils.refreshDialogues();
        throw mif.text("已刷新").toMessage().toThrowable();
    }

    @Action("userlist")
    public void userList() {
        List<User> userlist = GameSystem.USER_LIST;
        System.out.println(userlist);
        throw mif.text(userlist.toString()).toMessage().toThrowable();
    }

    @Action("add {targetQq} {name} {count}")
    public void add(long targetQq, String name, int count) {
        // -1 = everyone
        if (targetQq == -1) {
            for (User user : GameSystem.USER_LIST) {
                if(!UserUtil.addItems(user, name, count))
                    throw mif.text("该物品不存在").toMessage().toThrowable();
            }
            throw mif.text(String.format("已给予所有用户 %d 个 %s", count, name)).toMessage().toThrowable();
        }else {
            User user = UserUtil.getUserFromUserList(targetQq);
            if (user == null)
                throw mif.text("目标用户不存在于存档中").toMessage().toThrowable();

            if(!UserUtil.addItems(user, name, count))
                throw mif.text("该物品不存在").toMessage().toThrowable();

            throw mif.text(String.format("已给予用户 %d %d 个 %s", targetQq, count, name)).toMessage().toThrowable();
        }
    }

    @Action("messageTest {anything}")
    public String messageTest(String anything) {
        System.out.println(anything);
        return anything;
    }

    @Action("messageTest2 {msg}")
    public void messageTest2(Message message, Object msg) {
        System.out.println(message.getCodeStr());
        System.out.println(message.getSourceMessage());
        System.out.println(message.toString());
        System.out.println(message.toPath());
        throw new DoNone();
    }

    @Action("getImageUrl {img}")
    public void getImageUrl(Group group, Image img) {
        throw mif.text(img.getUrl()).toMessage().toThrowable();
    }

    @Action("getImageId {img}")
    public void getImageId(Group group, Image img) {
        throw mif.text(img.getId()).toMessage().toThrowable();
    }

    @Action("imageUrl {url}")
    public void imageUrl(Group group, String url) {
        throw mif.imageByUrl(url).toMessage().toThrowable();
    }

    @Action("imageId {id}")
    public void imageId(Group group, String id) {
        throw mif.text(id).plus(mif.imageById(id)).toMessage().toThrowable();
    }

    @Action("test {msg}")
    public void testMsg(Message message, MessageItem msg) {
        System.out.println(msg);
    }
}
