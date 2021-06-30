package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@MethodHandler(inputs = 2, types = {String.class, String.class})
public class DiscordSetRoles implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token_empty";
        if(!DiscordApi.isLogin())
            return "error_bot_login";

        String userId = (String) inputs[0];
        String roleId = (String) inputs[1];

        Guild guild = DiscordApi.getJda().getGuilds().get(0);
        Role role = guild.getRoleById(roleId);
        assert role != null;
        guild.addRoleToMember(userId, role);

        return true;
    }
}
