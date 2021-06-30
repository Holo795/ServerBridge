package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.common.IMethod;
import fr.vmarchaud.mineweb.common.MethodHandler;
import fr.vmarchaud.mineweb.discord.DiscordApi;

@MethodHandler(inputs = 1, types = {String.class})
public class DiscordSetToken implements IMethod {

    @Override
    public Object execute(ICore instance, Object... inputs) {

        String token = (String) inputs[0];
        if (token != null && !token.isEmpty()) {
            instance.config().setDiscordToken(token);
            instance.config().save(instance);
            if (!DiscordApi.isLogin()) {
                new DiscordApi(instance);
            }
        }

        return true;

    }
}
