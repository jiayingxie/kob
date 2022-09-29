package com.jiayingxie.kob.backend.service.impl.user.bot;

import com.jiayingxie.kob.backend.mapper.BotMapper;
import com.jiayingxie.kob.backend.pojo.Bot;
import com.jiayingxie.kob.backend.pojo.User;
import com.jiayingxie.kob.backend.service.impl.utils.UserDetailsImpl;
import com.jiayingxie.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        Map<String, String> map = new HashMap<>();

        int botId = Integer.parseInt(data.get("bot_id"));
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        if (title == null || title.length() == 0) {
            map.put("error_message", "标题不能为空");
            return map;
        }

        if (title.length() > 100) {
            map.put("error_message", "标题长度不能大于100");
            return map;
        }

        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 300) {
            map.put("error_message", "Bot描述的长度不能大于300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("error_message", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("error_message", "代码长度不能超过10000");
            return map;
        }

        // get current user
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        // get the old bot
        Bot oldBot = botMapper.selectById(botId);

        if (oldBot == null) {
            map.put("error_message", "The bot does not exist or is already been deleted");
            return map;
        }

        if (!oldBot.getId().equals(user.getId())) {
            map.put("error_message", "Sorry, you do not have the access to update this bot.");
            return map;
        }

        // update bot
        Bot newBot = new Bot(
                oldBot.getId(),
                user.getId(),
                title,
                description,
                content,
                oldBot.getRating(),
                oldBot.getCreatetime(),
                new Date()
        );
        botMapper.updateById(newBot);

        map.put("error_message", "Updated your bot successfully");
        return map;
    }
}
