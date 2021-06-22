package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

@MethodHandler(inputs = 2, types = {String.class, String.class})
public class DiscordSendMessage implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token_empty";
        if(!DiscordApi.getLogin())
            return "error_bot_login";


        String channelId = (String) inputs[0];
        String message = (String) inputs[1];

        Guild guild = DiscordApi.getJda().getGuilds().get(0);
        TextChannel textChannel = guild.getTextChannelById(channelId);

        assert textChannel != null;
        textChannel.sendMessage(message.replaceAll("\\{ENTER}", "\n")).queue();
        return true;
    }

}
