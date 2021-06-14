package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

@MethodHandler(inputs = 3, types = {String.class, String.class, String.class})
public class DiscordSendMessage implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token";


        String guildId = (String) inputs[0];
        String channelId = (String) inputs[1];
        String message = (String) inputs[2];

        Guild guild = DiscordApi.getJda().getGuildById(guildId);
        assert guild != null;
        TextChannel textChannel = guild.getTextChannelById(channelId);

        assert textChannel != null;
        textChannel.sendMessage(message).queue();
        return true;
    }

}
