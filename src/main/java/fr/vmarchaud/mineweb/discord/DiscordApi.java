package fr.vmarchaud.mineweb.discord;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.discord.events.EventsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class DiscordApi {

    ICore api;
    JDABuilder builder;
    static JDA jda;

    public DiscordApi(ICore api) {
        this.api = api;

        builder = JDABuilder.createDefault(api.config().discordToken);
        builder.setActivity(Activity.watching("Mineweb !"));
        builder.addEventListeners(new EventsListener());

        try {
            if(!api.config().discordToken.isEmpty()) jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        if(jda != null && jda.getGuilds().size() > 1) {
            System.err.println("Your bot is already on an other server | Bot Shutdown");
            jda.shutdown();
        }

    }

    public static JDA getJda() {
        return jda;
    }

    public ICore getICore(){
        return api;
    }
}
