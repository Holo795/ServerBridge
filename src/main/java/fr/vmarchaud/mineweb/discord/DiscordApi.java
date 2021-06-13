package fr.vmarchaud.mineweb.discord;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.discord.methods.DiscordAddRole;
import fr.vmarchaud.mineweb.discord.methods.DiscordSendMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class DiscordApi {

    ICore api;
    JDABuilder builder;
    JDA jda;

    public DiscordApi(ICore api) {
        this.api = api;

        builder = JDABuilder.createDefault(api.config().discordToken);

        try {
            if(!api.config().discordToken.isEmpty()) jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        init();

    }

    private void init() {
        new DiscordSendMessage(this);
    }

    public JDA getJda() {
        return jda;
    }

    public ICore getICore(){
        return api;
    }
}
