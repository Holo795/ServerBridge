package fr.vmarchaud.mineweb.discord.methods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@MethodHandler(inputs = 1, types = {String.class})
public class DiscordGetChannelList implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token_empty";
        if(!DiscordApi.getLogin())
            return "error_bot_login";

        String type = (String) inputs[0];

        Map<String, String> textChannelList = new HashMap<>(), voiceChannelList = new HashMap<>();

        Guild guild = DiscordApi.getJda().getGuilds().get(0);

        for(GuildChannel channel : guild.getTextChannels()) {
            textChannelList.put(channel.getName(), channel.getId());
        }

        for(GuildChannel channel : guild.getVoiceChannels()) {
            voiceChannelList.put(channel.getName(), channel.getId());
        }

        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>(){}.getType();

        switch(type) {
            case "text":
                return textChannelList.isEmpty() ? "no_channel" : gson.toJson(textChannelList,gsonType);
            case "voice":
                return voiceChannelList.isEmpty() ? "no_channel" : gson.toJson(voiceChannelList,gsonType);
            default:
                return "no_channel";
        }
    }
}
