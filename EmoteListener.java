import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmoteListener extends ListenerAdapter
{	
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e)
	{
		if(Main.CHANNELID == null || !Main.sendLogs)
			return;
		
		e.getChannel().retrieveMessageById(e.getMessageId()).queue((msg) ->
		{
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.GREEN);
			eb.setAuthor(e.getMember().getEffectiveName(), null, e.getUser().getAvatarUrl());
			eb.setTitle(e.getMember().getNickname());
			String desc = e.getMember().getUser().getAsTag()
					+ " just added the reaction "
					+ e.getReactionEmote().getName()
					+ " to the following message:\n\n ";
			
			desc += "**" + msg.getMember().getEffectiveName() + "**\n";
			desc += msg.getContentRaw() + "\n\n";
			desc += "link to message: " + msg.getJumpUrl();
					
			eb.setDescription(desc);
			
			e.getJDA().getTextChannelById(Main.CHANNELID).sendMessage(eb.build()).queue();
		});

		
	}
	
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e)
	{
		if(Main.CHANNELID == null || !Main.sendLogs)
			return;
		
		e.getChannel().retrieveMessageById(e.getMessageId()).queue((msg) ->
		{
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(e.getMember().getEffectiveName(), null, e.getUser().getAvatarUrl());
			eb.setColor(Color.RED);
			String desc = e.getMember().getUser().getAsTag()
					+ " just removed the reaction "
					+ e.getReactionEmote().getName()
					+ " to the following message:\n\n ";
			
			desc += "**" + msg.getMember().getEffectiveName() + "**\n";
			desc += msg.getContentRaw() + "\n\n";
			desc += "link to message: " + msg.getJumpUrl();
			
			eb.setDescription(desc);
			
			e.getJDA().getTextChannelById(Main.CHANNELID).sendMessage(eb.build()).queue();
		});
	}
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e)
	{
		String msg = e.getMessage().getContentRaw();
		
		if(msg.equalsIgnoreCase(Main.PREFIX + "setchannel") && Utils.hasAdmin(e.getMember()))
		{
			Main.CHANNELID = e.getChannel().getId();
			e.getChannel().sendMessage("Log channel has been set to "
					+ e.getChannel().getAsMention()).queue();
			return;
		}
		
		if(msg.equalsIgnoreCase(Main.PREFIX + "currentchannel"))
		{
			if(Main.CHANNELID == null)
				e.getChannel().sendMessage("No channel has been set").queue();
			else
			{
				e.getChannel().sendMessage("The current log channel is: "
						+ e.getJDA().getTextChannelById(Main.CHANNELID).getAsMention())
						.queue();
			}
			
			return;
		}
		
		if(msg.equalsIgnoreCase(Main.PREFIX + "toggle"))
		{
			Main.sendLogs = !Main.sendLogs;
			if(Main.sendLogs)
				e.getChannel().sendMessage("Reaction logging has been toggled ON").queue();
			else
				e.getChannel().sendMessage("Reaction logging has been toggled OFF").queue();
		}
		
		if(msg.equalsIgnoreCase(Main.PREFIX + "help"))
		{
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Commands:");
			String desc = "";
			desc += "**" + Main.PREFIX + "setchannel**: sets the channel this command is used in as the one that reaction logs are sent to";
			desc += "\n\n**" + Main.PREFIX + "currentchannel**: shows the current channel logs are sent to";
			desc += "\n\n**" + Main.PREFIX + "toggle**: toggles reaction logging";
			desc += "\n\n**" + Main.PREFIX + "stop**: shuts down the bot";
			eb.setDescription(desc);
			e.getChannel().sendMessage(eb.build()).queue();
		}
		
		if(msg.equalsIgnoreCase(Main.PREFIX + "stop") && Utils.hasAdmin(e.getMember()))
		{
			e.getJDA().shutdownNow();
			System.exit(0);
		}
	}
	
	public void onTextChannelDelete(TextChannelDeleteEvent e)
	{
		String id = e.getChannel().getId();
		
		//if there is a set channel and it is deleted
		if(id != null && id.equals(Main.CHANNELID))
			Main.CHANNELID = null;
	}
}
