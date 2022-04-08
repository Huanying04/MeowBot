package net.nekomura.controller;

import com.IceCreamQAQ.Yu.annotation.Action;
import com.IceCreamQAQ.Yu.entity.DoNone;
import com.icecreamqaq.yuq.YuQ;
import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.message.MessageItemFactory;
import net.nekomura.game.User;
import net.nekomura.game.UserUtil;

import javax.inject.Inject;
import java.util.concurrent.ThreadLocalRandom;

@GroupController
public class GroupBasicController {
    @Inject
    private YuQ yuq;

    @Inject
    private MessageItemFactory mif;

    /***
     * Action 则为具体的控制器的动作，负责处理收到的消息。
     *
     * Action 方法接收 0 - 多个参数，通常，您所写下的参数，将会以名称匹配来进行依赖注入。
     *   支持注入的名称 - 注入的内容
     *     qq - 发送消息的 QQ 账号
     *     group - 发送消息的 QQ 群号
     *     message - 具体的 Message 对象
     *     messageId - 消息 ID
     *     sourceMessage - 未经处理的源消息内容，则为具体的 Runtime 的消息内容
     *     actionContext - 当前消息的 ActionContext 对象
     *     以及，您 Before 传递回来的需要保存的对象。
     *
     * Action 可接收方法可以接受任何类型的返回值，当您返回了一个值的时候，
     *   如果您返回的是 Message 类型的时候，我们会帮您发送这个消息，如果您没有设置任何接收的 QQ，或 QQ 群，那么我们将会将消息发送至当前消息来源者，如果您设置了接收对象，那么发送至您的接收对象。
     *   如果您返回了一个 String 类型的时候，我们会帮您构建一个 Message，并发送到当前消息的来源。
     *   如果您返回了一个 MessageItem 类型的时候，我们会帮您构建一个 Message，并发送到当前消息的来源。
     *   如果您返回的是其他类型，我们会帮您调用 toString 方法，并构建一个 Message，然后发送到当前消息的来源。
     *
     * Action 方法可以抛出异常，来返回一些信息。
     *   当您抛出了一个 Message 类型的异常后，如果您没有设置任何接收的 QQ，或 QQ 群，那么我们将会将消息发送至当前消息来源者，如果您设置了接收对象，那么发送至您的接收对象。
     *   当您想中断处理链路，并且不进行任何返回的时候，您可以抛出 DoNone 类型的异常。
     * @return help message
     */
    @Action("帮助")
    public String help(long qq) {
        return "    【 帮助 】    \n" +
                "暂无帮助";
    }
}
