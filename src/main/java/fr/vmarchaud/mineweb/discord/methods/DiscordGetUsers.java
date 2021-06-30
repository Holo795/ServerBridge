package fr.vmarchaud.mineweb.discord.methods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@MethodHandler
public class DiscordGetUsers implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        if(instance.config().discordToken.isEmpty())
            return "error_token_empty";
        if(!DiscordApi.isLogin())
            return "error_bot_login";

        Map<String, String> userList = new HashMap<>();

        Guild guild = DiscordApi.getJda().getGuilds().get(0);

        for(User user : guild.getJDA().getUsers()) {
            userList.put(user.getName(), user.getId());
        }

        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>(){}.getType();

        return userList.isEmpty() ? "error_no_user" : gson.toJson(userList,gsonType);

    }
}
