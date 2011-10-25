package randy.quiz;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import com.nijikokun.register.payment.Methods;

public class quizPlayerListener extends PlayerListener{

	public void onPlayerChat(final PlayerChatEvent event){
		if(event.getPlayer().hasPermission("epicquiz.awnser")){
			final Player player = event.getPlayer();
			String message = event.getMessage().toLowerCase();
			final String world = player.getWorld().getName();
			final int question = quiz.current.get(world+".question");
			if(question != -1){
				String awnser = quiz.questions.get(world+"."+question+".awnser").toLowerCase();
				if(message.equals(awnser)){
					Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("EpicQuiz"), new Runnable() {
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
							String announcerwinmessage = quiz.announcer.get("correct").replace("[player]", player.getDisplayName());
			            	String rewardsmessage = quiz.announcer.get("reward").replace("[rewards]", rewards).replace("[player]", player.getDisplayName());
			            	List<Player> players = Bukkit.getServer().getWorld(world).getPlayers();
			            	int e;
			            	for(e = 0; e < players.size(); e++){
			            		players.get(e).sendMessage(announcerwinmessage);
			            		players.get(e).sendMessage(rewardsmessage);
			            	}
			            	String playername = player.getDisplayName();
							if(quiz.score.get(playername) == null){
								quiz.score.put(playername, 0);
							}
							int points = quiz.score.get(playername);
							points++;
							quiz.score.put(playername, points);
							quiz.stopSystem(world);
							quiz.startSystem(world);
			            }
			        }, 5);
				}
			}
		}
	}
}
