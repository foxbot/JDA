/*
 *     Copyright 2015-2017 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.core.handle;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.client.events.message.group.GroupMessageDeleteEvent;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.core.requests.GuildLock;
import org.json.JSONObject;

public class MessageDeleteHandler extends SocketHandler
{

    public MessageDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(JSONObject content)
    {
        final String messageIdString = content.getString("id");
        final long messageId = Long.parseLong(messageIdString);
        final long channelId = content.getLong("channel_id");

        MessageChannel channel = api.getTextChannelById(channelId);
        if (channel == null)
        {
            channel = api.getPrivateChannelById(channelId);
        }
        if (channel == null)
        {
            channel = api.getFakePrivateChannelMap().get(channelId);
        }
        if (channel == null && api.getAccountType() == AccountType.CLIENT)
        {
            channel = api.asClient().getGroupById(channelId);
        }
        if (channel == null)
        {
            EventCache.get(api).cache(EventCache.Type.CHANNEL, channelId, () -> handle(responseNumber, allContent));
            EventCache.LOG.debug("Got message delete for a channel/group that is not yet cached. ChannelId: " + channelId);
            return null;
        }

        if (channel instanceof TextChannel)
        {
            TextChannel tChan = (TextChannel) channel;
            if (GuildLock.get(api).isLocked(tChan.getGuild().getIdLong()))
                return tChan.getGuild().getIdLong();
            api.getEventManager().handle(
                    new GuildMessageDeleteEvent(
                            api, responseNumber,
                            messageIdString, tChan));
        }
        else if (channel instanceof PrivateChannel)
        {
            api.getEventManager().handle(
                    new PrivateMessageDeleteEvent(
                            api, responseNumber,
                            messageIdString, (PrivateChannel) channel));
        }
        else
        {
            api.getEventManager().handle(
                    new GroupMessageDeleteEvent(
                            api, responseNumber,
                            messageIdString, (Group) channel));
        }

        //Combo event
        api.getEventManager().handle(
                new MessageDeleteEvent(
                        api, responseNumber,
                        messageIdString, channel));
        return null;
    }
}
