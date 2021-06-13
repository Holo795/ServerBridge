package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class DiscordSendMessage {

    DiscordApi api;

    public DiscordSendMessage(DiscordApi api){
        this.api = api;
    }

    public void sendMessage(Long channelId, String message){
        TextChannel textChannel = api.getJda().getTextChannelById(channelId);
        assert textChannel != null;
        if(textChannel.canTalk()) {
            textChannel.sendMessage(message).queue();
        }
    }

    public String getTextChannelId(String name) {
        List<TextChannel> channel = api.getJda().getTextChannelsByName(name, true);
        for(TextChannel ch : channel)
        {
            return ch.getId();
        }
        return null;
    }

}
