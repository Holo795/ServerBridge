package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;

@MethodHandler
public class DiscordGuildId implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token_empty";
        if(!DiscordApi.isLogin())
            return "error_bot_login";

        return DiscordApi.getJda().getGuilds().get(0).getIdLong();
    }
}
