package fr.vmarchaud.mineweb.discord.methods;

import fr.vmarchaud.mineweb.discord.DiscordApi;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class DiscordAddRole {

    DiscordApi api;

    public DiscordAddRole(DiscordApi api) {this.api = api;}

    public void addRole(Guild guild, Role role, Member member) {
        guild.addRoleToMember(member, role);
    }

}
