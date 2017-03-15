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

package net.dv8tion.jda.core.entities;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.impl.EmoteImpl;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * An object representing a single MessageReaction from Discord.
 * This is an immutable object and is not updated by method calls or changes in Discord. A new snapshot instance
 * built from Discord is needed to see changes.
 *
 * @since  3.0
 * @author Florian Spieß
 */
public class MessageReaction
{

    private final MessageChannel channel;
    private final ReactionEmote emote;
    private final long messageId;
    private final boolean self;
    private final int count;

    /**
     * Creates a new MessageReaction instance
     *
     * @param  channel
     *         The {@link net.dv8tion.jda.core.entities.MessageChannel} this Reaction was used in
     * @param  emote
     *         The {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote ReactionEmote} that was used
     * @param  messageId
     *         The message id this reaction is attached to
     * @param  self
     *         Whether we already reacted with this Reaction
     * @param  count
     *         The amount of people that reacted with this Reaction
     */
    public MessageReaction(MessageChannel channel, ReactionEmote emote, long messageId, boolean self, int count)
    {
        this.channel = channel;
        this.emote = emote;
        this.messageId = messageId;
        this.self = self;
        this.count = count;
    }

    /**
     * The JDA instance of this Reaction
     *
     * @return The JDA instance of this Reaction
     */
    public JDA getJDA()
    {
        return channel.getJDA();
    }

    /**
     * Whether the currently logged in account has reacted with this reaction
     *
     * @return True, if we reacted with this reaction
     */
    public boolean isSelf()
    {
        return self;
    }

    /**
     * The amount of users that already reacted with this Reaction
     * <br><b>This is not updated, it is a {@code final int} per Reaction instance</b>
     *
     * @return The amount of users that reacted with this Reaction
     */
    public int getCount()
    {
        return count;
    }

    /**
     * The {@link net.dv8tion.jda.core.entities.MessageChannel MessageChannel}
     * this Reaction was used in.
     *
     * @return The channel this Reaction was used in
     */
    public MessageChannel getChannel()
    {
        return channel;
    }

    /**
     * The {@link net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote ReactionEmote}
     * of this Reaction
     *
     * @return The final instance of this Reaction's Emote/Emoji
     */
    public ReactionEmote getEmote()
    {
        return emote;
    }

    /**
     * The message id this reaction is attached to
     *
     * @return The message id this reaction is attached to
     */
    public String getMessageId()
    {
        return String.valueOf(messageId);
    }

    /**
     * Retrieves the {@link net.dv8tion.jda.core.entities.User Users} that
     * already reacted with this MessageReaction.
     * <br>This is an overload of {@link #getUsers(int)} with {@code 100}.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @return {@link net.dv8tion.jda.core.requests.RestAction RestAction} - Type: List{@literal <}{@link net.dv8tion.jda.core.entities.User User}{@literal >}
     *         <br>Retrieves an immutable list of users that reacted with this Reaction.
     */
    public RestAction<List<User>> getUsers()
    {
        return getUsers(100);
    }

    /**
     * Retrieves the {@link net.dv8tion.jda.core.entities.User Users} that
     * already reacted with this MessageReaction. The maximum amount of users
     * that can be retrieved is 100.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @param  amount
     *         the amount of users to retrieve
     *
     * @throws IllegalArgumentException
     *         if the provided amount is not between 1-100
     *
     * @return {@link net.dv8tion.jda.core.requests.RestAction RestAction} - Type: List{@literal <}{@link net.dv8tion.jda.core.entities.User User}{@literal >}
     *         <br>Retrieves an immutable list of users that reacted with this Reaction.
     */
    public RestAction<List<User>> getUsers(int amount)
    {
        if (amount < 1 || amount > 100)
            throw new IllegalArgumentException("Amount is out of range 1-100!");
        String code = emote.isEmote()
                ? emote.getName() + ":" + emote.getId()
                : encode(emote.getName());
        Route.CompiledRoute route = Route.Messages.GET_REACTION_USERS.compile(channel.getId(), getMessageId(), code, String.valueOf(amount));
        return new RestAction<List<User>>(getJDA(), route, null)
        {
            @Override
            protected void handleResponse(Response response, Request<List<User>> request)
            {
                if (!response.isOk())
                {
                    request.onFailure(response);
                    return;
                }
                List<User> users = new LinkedList<>();
                JSONArray array = response.getArray();
                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject json = array.getJSONObject(i);
                    final long userId = json.getLong("id");
                    User user = api.getUserMap().get(userId);
                    if (user == null)
                        user = api.getFakeUserMap().get(userId);
                    if (user == null)
                        user = EntityBuilder.get(api).createFakeUser(json, false);
                    users.add(user);
                }
                request.onSuccess(users);
            }
        };
    }

    /**
     * Removes this Reaction from the Message.
     * <br>This will remove our own reaction as an overload
     * of {@link #removeReaction(User)}.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @return {@link net.dv8tion.jda.core.requests.RestAction RestAction} - Type: Void
     *         Nothing is returned on success
     */
    public RestAction<Void> removeReaction()
    {
        return removeReaction(getJDA().getSelfUser());
    }

    /**
     * Removes this Reaction from the Message.
     * <br>This will remove the reaction of the {@link net.dv8tion.jda.core.entities.User User}
     * provided.
     *
     * <p>If the provided User did not react with this Reaction this does nothing.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.core.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @param  user
     *         The User of which to remove the reaction
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided {@code user} is null.
     * @throws net.dv8tion.jda.core.exceptions.PermissionException
     *         if the provided User is not us and we do not have permission to
     *         {@link net.dv8tion.jda.core.Permission#MESSAGE_MANAGE manage messages}
     *         in the channel this reaction was used in
     *
     * @return {@link net.dv8tion.jda.core.requests.RestAction RestAction} - Type: Void
     *         Nothing is returned on success
     */
    public RestAction<Void> removeReaction(User user)
    {
        if (user == null)
            throw new IllegalArgumentException("Provided User was null!");
        if (!user.equals(getJDA().getSelfUser()))
        {
            if (channel.getType() == ChannelType.TEXT)
            {
                Channel channel = (Channel) this.channel;
                if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
                    throw new PermissionException(Permission.MESSAGE_MANAGE);
            }
            else
            {
                throw new PermissionException("Unable to remove Reaction of other user in non-text channel!");
            }
        }

        String code = emote.isEmote()
                    ? emote.getName() + ":" + emote.getId()
                    : encode(emote.getName());
        Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(channel.getId(), getMessageId(), code, user.getId());
        return new RestAction<Void>(getJDA(), route, null)
        {
            @Override
            protected void handleResponse(Response response, Request<Void> request)
            {
                if (response.isOk())
                    request.onSuccess(null);
                else
                    request.onFailure(response);
            }
        };
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof MessageReaction
                && ((MessageReaction) obj).emote.equals(emote)
                && (((MessageReaction) obj).self == self)
                && ((MessageReaction) obj).messageId == messageId;
    }

    @Override
    public String toString()
    {
        return "MR:(M:(" + messageId + ") / " + emote + ")";
    }

    private static String encode(String chars)
    {
        try
        {
            return URLEncoder.encode(chars, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e); //thanks JDK 1.4
        }
    }

    /**
     * Represents an Emoji/Emote of a MessageReaction
     * <br>This is used to wrap both emojis and emotes
     */
    public static class ReactionEmote implements ISnowflake
    {

        private final JDA api;
        private final String name;
        private final Long id;

        public ReactionEmote(String name, Long id, JDA api)
        {
            this.name = name;
            this.id = id;
            this.api = api;
        }

        public ReactionEmote(Emote emote)
        {
            this(emote.getName(), emote.getIdLong(), emote.getJDA());
        }

        /**
         * Whether this is an {@link net.dv8tion.jda.core.entities.Emote Emote}
         * wrapper.
         *
         * @return True, if {@link #getId()} is not null
         */
        public boolean isEmote()
        {
            return id != null;
        }

        @Override
        public String getId()
        {
            return id != null ? String.valueOf(id) : null;
        }

        @Override
        public long getIdLong()
        {
            if (id == null)
                throw new IllegalStateException("No id available");
            return id;
        }

        /**
         * The name for this emote/emoji
         *
         * @return The name for this emote/emoji
         */
        public String getName()
        {
            return name;
        }

        /**
         * The instance of {@link net.dv8tion.jda.core.entities.Emote Emote}
         * for the Reaction instance.
         * <br>Might be null if {@link #getId()} returns null.
         *
         * @return The possibly-null Emote for the Reaction instance
         */
        public Emote getEmote()
        {
            if (!isEmote())
                return null;
            Emote e = api.getEmoteById(getIdLong());
            return e != null ? e : new EmoteImpl(id, api).setName(name);
        }

        /**
         * The current JDA instance for the Reaction
         *
         * @return The JDA instance of the Reaction
         */
        public JDA getJDA()
        {
            return api;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof ReactionEmote
                    && Objects.equals(((ReactionEmote) obj).id, id)
                    && ((ReactionEmote) obj).getName().equals(name);
        }

        @Override
        public String toString()
        {
            return "RE:" + (isEmote() ? getEmote() : getName() + "(" + id + ")");
        }
    }

}
