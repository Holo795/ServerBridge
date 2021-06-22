package fr.vmarchaud.mineweb.discord;

import fr.vmarchaud.mineweb.common.ICore;
import fr.vmarchaud.mineweb.discord.events.EventsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class DiscordApi {

    ICore api;
    JDABuilder builder;
    static JDA jda;

    static boolean login = false;

    public DiscordApi(ICore api) {
        this.api = api;

        builder = JDABuilder.createDefault(api.config().discordToken);
        builder.setActivity(Activity.watching("Mineweb !"));
        builder.addEventListeners(new EventsListener());

        try {
            if(!api.config().discordToken.isEmpty()) jda = builder.build();
            login = true;
        } catch (LoginException e) {
            System.err.println(e.getMessage());
            login = false;
        }

        if(jda != null && jda.getGuilds().size() > 1) {
            while(jda.getGuilds().size() > 1) {
                jda.getGuilds().get(1).leave().queue();
            }
            System.err.println("Your bot is already on an other server | Server left");
        }
    }

    public static JDA getJda() {
        return jda;
    }

    public ICore getICore(){
        return api;
    }
    
    public static boolean getLogin() { return login; }
}
