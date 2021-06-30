package fr.vmarchaud.mineweb.discord.methods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@MethodHandler
public class DiscordGetRoles implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token_empty";
        if(!DiscordApi.isLogin())
            return "error_bot_login";

        Map<String, String> rolesList = new HashMap<>();

        Guild guild = DiscordApi.getJda().getGuilds().get(0);

        for(Role role : guild.getRoles()) {
            rolesList.put(role.getName(), role.getId());
        }

        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>(){}.getType();

        return rolesList.isEmpty() ? "error_no_role" : gson.toJson(rolesList,gsonType);

    }
}
