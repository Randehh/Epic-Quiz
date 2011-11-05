package randy.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

public class quiz extends JavaPlugin{
	
	static HashMap<String, String> questions = new HashMap<String, String>();
	@SuppressWarnings("rawtypes")
	static Map score = new HashMap<String, Integer>();
	static HashMap<String, Integer> current = new HashMap<String, Integer>();
	static HashMap<String, Integer> delay = new HashMap<String, Integer>();
	static HashMap<String, String> announcer = new HashMap<String, String>();
	static HashMap<String, Boolean> moneyrewards = new HashMap<String, Boolean>();
	static HashMap<String, Boolean> banlist = new HashMap<String, Boolean>();
	static HashMap<String, Integer> rewards = new HashMap<String, Integer>();
	static HashMap<String, Integer> achievements = new HashMap<String, Integer>();
	
	
	private final quizPlayerListener playerListener = new quizPlayerListener();
	
	@Override
	public void onDisable() {
		quizSaver.saveScore();
	}

	@Override
	public void onEnable() {
		quizLoader.loadPlugin();
		quizSaver.loadScore();
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, (Listener) playerListener, Priority.Normal, Bukkit.getServer().getPluginManager().getPlugin("EpicQuiz"));
	}
	
	public static void startSystem(final String world){
		int time = delay.get("delay");
		nextQuestion(world);
		int timer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("EpicQuiz"), new Runnable() {
            public void run(){
            	nextQuestion(world);
            }
        }, time * 20L, time * 20L);
		//TODO World.timer
    	current.put(world+".timer", timer);
	}
	
	public static void nextQuestion(final String world){
		//TODO Use new world string
		current.put("question", -1);
		int total = current.get(world+".total");
    	Random generator = new Random();
    	final int questionno = generator.nextInt(total);
    	final String question = questions.get(world+"."+questionno+".question");
    	final List<Player> players = Bukkit.getServer().getWorld(world).getPlayers();
    	int a;
    	for(a = 0; a < players.size(); a++){
    		players.get(a).sendMessage(announcer.get("ready"));
    	}
    	Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("EpicQuiz"), new Runnable() {
            public void run(){
            	String askstring = announcer.get("ask");
            	askstring = askstring.replace("[question]", question);
            	int e;
            	for(e = 0; e < players.size(); e++){
            		players.get(e).sendMessage(askstring);
            		current.put(world+".question", questionno);
            	}
            }
        }, 2 * 20L);
	}
	
	public static void stopSystem(String world){
		int timer = current.get(world+".timer");
		Bukkit.getServer().getScheduler().cancelTask(timer);
		current.put(world+".question", -1);
	}
	
	@SuppressWarnings("rawtypes")
	public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args){
		if(commandName.equalsIgnoreCase("quiz")){
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("help")){
					if(sender.hasPermission("epicquiz.help")){
						sender.sendMessage(ChatColor.GOLD + "EpicQuiz Help");
						sender.sendMessage(ChatColor.GOLD + "/quiz help - Display this page.");
						sender.sendMessage(ChatColor.GOLD + "/quiz next [world] - Goes to the next question of the specified world.");
						sender.sendMessage(ChatColor.GOLD + "/quiz top [number] - Shows the top scorers.");
						sender.sendMessage(ChatColor.GOLD + "/quiz ban [playername] - Bans a player form the quiz.");
						sender.sendMessage(ChatColor.GOLD + "/quiz unban [playername] - Unbans a player from the quiz.");
					}else{
						sender.sendMessage(ChatColor.RED + "EpicQuiz: You don't have permission to do that.");
						return true;
					}
				}
			}
			if(args.length == 2){
				if(args[0].equalsIgnoreCase("ban")){
					if(sender.hasPermission("epicquiz.ban")){
						String playername = args[1];
						if(banlist.containsKey(playername)){
							if(banlist.get(playername).equals(true)){
								sender.sendMessage(playername + " is already banned from the quiz.");
								return true;
							}else{
								banlist.put(playername, true);
								sender.sendMessage(playername + " is now banned from the quiz.");
								return true;
							}
						}else{
							banlist.put(playername, true);
							sender.sendMessage(playername + " is now banned from the quiz.");
							return true;
						}
					}
				}
				if(args[0].equalsIgnoreCase("unban")){
					if(sender.hasPermission("epicquiz.unban")){
						String playername = args[1];
						if(banlist.containsKey(playername)){
							if(banlist.get(playername).equals(false)){
								sender.sendMessage(playername + " is not banned.");
								return true;
							}else{
								banlist.put(playername, false);
								sender.sendMessage(playername + " is now banned from the quiz.");
								return true;
							}
						}else{
							banlist.put(playername, false);
							sender.sendMessage(playername + " is now banned from the quiz.");
							return true;
						}
					}
				}
				if(args[0].equalsIgnoreCase("top")){
					if(sender.hasPermission("epicquiz.top")){
						sender.sendMessage(ChatColor.GOLD + "[====   Top Players   ====]");
						int e = 0;
						for (Iterator i = sortByValue(score).iterator(); e < Integer.parseInt(args[1]); ) {
							try{
								String key = ChatColor.stripColor((String) i.next());
								sender.sendMessage(ChatColor.YELLOW + key + ": " + score.get(key));
								e++;
								if(!i.hasNext()){
									break;
								}
							}catch(NoSuchElementException a){
							}
				        }
						sender.sendMessage(ChatColor.GOLD + "[======================]");
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "EpicQuiz: You don't have permission to do that.");
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("next")){
					if(sender.hasPermission("epicquiz.next")){
						if(current.get(args[1]+".question") != -1){
							current.put(args[1]+".question", -1);
							quiz.nextQuestion(args[1]);
						}else{
							sender.sendMessage(ChatColor.RED + "[EpicQuiz]: That world doesn't exist or the next question is incoming.");
						}
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "EpicQuiz: You don't have permission to do that.");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List sortByValue(final Map m) {
        List keys = new ArrayList();
        keys.addAll(m.keySet());
        Collections.sort(keys, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = m.get(o1);
                Object v2 = m.get(o2);
                if (v1 == null) {
                    return (v2 == null) ? 0 : 1;
                }
                else if (v1 instanceof Comparable) {
                    return ((Comparable) v1).compareTo(v2);
                }
                else {
                    return 0;
                }
            }
        });
        return keys;
    }
}
