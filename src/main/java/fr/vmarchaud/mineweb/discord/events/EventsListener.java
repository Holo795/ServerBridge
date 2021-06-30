package fr.vmarchaud.mineweb.discord.events;

import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class EventsListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        if(DiscordApi.getJda().getGuilds().size() > 1) {
            System.err.println("Your bot is already on an other server | Server left");
            Objects.requireNonNull(DiscordApi.getJda().getGuildById(e.getGuild().getId())).leave().queue();
        }
    }

    @Override
    public void onReady(ReadyEvent e){
        DiscordApi.setLogin(true);
    }

}
