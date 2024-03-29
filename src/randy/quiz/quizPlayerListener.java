package randy.quiz;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import com.nijikokun.register.payment.Methods;

public class quizPlayerListener extends PlayerListener{

	public void onPlayerChat(final PlayerChatEvent event){
		if(quiz.banlist.containsKey(event.getPlayer().getDisplayName()) && !quiz.banlist.get(event.getPlayer().getDisplayName())){
			if(event.getPlayer().hasPermission("epicquiz.awnser")){
				final Player player = event.getPlayer();
				String message = event.getMessage().toLowerCase();
				final String world = player.getWorld().getName();
				final int question = quiz.current.get(world+".question");
				if(question != -1){
					String awnser = quiz.questions.get(world+"."+question+".awnser").toLowerCase().replace(".", "");
					if(message.equals(awnser)){
						Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("EpicQuiz"), new Runnable() {
				            @SuppressWarnings("unchecked")
							public void run(){
								int itemid = quiz.rewards.get(world+"."+question+".reward.item.id");
								int itemamount = quiz.rewards.get(world+"."+question+".reward.item.amount");
								int moneyget = quiz.rewards.get(world+"."+question+".reward.money");
				            	String rewards = null;
								if(itemid != 0 && itemamount != 0){
									ItemStack items = new ItemStack(itemid, itemamount);
									player.getInventory().addItem(items);
									rewards = itemamount + " " + Material.getMaterial(itemid).toString().toLowerCase().replace("_", "");
								}
								if(Methods.getMethod() != null && quiz.moneyrewards.get("enabled") && quiz.rewards.get(player.getWorld().getName()+"."+question+".reward.money") != 0){
									Methods.getMethod().getAccount(player.getName()).add(moneyget);
									if(rewards != null){
										rewards = rewards + " and " + moneyget + " " + quiz.announcer.get("moneyname");
									}else{
										rewards = moneyget + " " + quiz.announcer.get("moneyname");
									}
								}
				            	String playername = ChatColor.stripColor(player.getDisplayName());
								String announcerwinmessage = quiz.announcer.get("correct").replace("[player]", ChatColor.stripColor(playername));
				            	String rewardsmessage = quiz.announcer.get("reward").replace("[rewards]", rewards).replace("[player]", ChatColor.stripColor(player.getDisplayName()));
				            	List<Player> players = Bukkit.getServer().getWorld(world).getPlayers();
				            	int e;
				            	for(e = 0; e < players.size(); e++){
				            		players.get(e).sendMessage(announcerwinmessage);
				            		players.get(e).sendMessage(rewardsmessage);
				            	}
								if(quiz.score.get(playername) == null){
									quiz.score.put(playername, 0);
								}
								int points = Integer.parseInt(quiz.score.get(playername).toString());
								points++;
								quiz.score.put(playername, points);
								if(quiz.achievements.containsKey(points+".reward.money")){
									int newitemid = quiz.achievements.get(points+".reward.item.id");
									int newitemamount = quiz.achievements.get(points+".reward.item.amount");
									int newmoneyget = quiz.achievements.get(points+".reward.money");
					            	String newrewards = null;
									if(newitemid != 0 && newitemamount != 0){
										ItemStack newitems = new ItemStack(newitemid, newitemamount);
										player.getInventory().addItem(newitems);
										newrewards = newitemamount + " " + Material.getMaterial(newitemid).toString().toLowerCase().replace("_", "");
									}
									if(Methods.getMethod() != null && quiz.moneyrewards.get("enabled") && quiz.rewards.get(player.getWorld().getName()+"."+question+".reward.money") != 0){
										Methods.getMethod().getAccount(player.getName()).add(newmoneyget);
										if(newrewards != null){
											newrewards = newrewards + " and " + newmoneyget + " " + quiz.announcer.get("moneyname");
										}else{
											newrewards = newmoneyget + " " + quiz.announcer.get("moneyname");
										}
									}
									String announcerachievement = quiz.announcer.get("achievement").replace("[amount]", ""+points).replace("[player]", ChatColor.stripColor(playername));
									for(e = 0; e < players.size(); e++){
					            		players.get(e).sendMessage(announcerachievement);
					            	}
					            	String newrewardsmessage = quiz.announcer.get("achievementreward").replace("[amount]", ""+points).replace("[rewards]", rewards).replace("[player]", ChatColor.stripColor(player.getDisplayName()));
					            	player.sendMessage(newrewardsmessage);
								}
								quiz.stopSystem(world);
								quiz.startSystem(world);
				            }
				        }, 5);
					}
				}
			}
		}
	}
}
