package randy.quiz;

import java.util.HashMap;
import java.util.List;
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
	static HashMap<String, Integer> score = new HashMap<String, Integer>();
	static HashMap<String, Integer> current = new HashMap<String, Integer>();
	static HashMap<String, Integer> delay = new HashMap<String, Integer>();
	static HashMap<String, String> announcer = new HashMap<String, String>();
	static HashMap<String, Boolean> moneyrewards = new HashMap<String, Boolean>();
	static HashMap<String, Integer> rewards = new HashMap<String, Integer>();
	
	
	private final quizPlayerListener playerListener = new quizPlayerListener();
	
	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
		quizLoader.loadPlugin();
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
	
	public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args){
		if(sender.hasPermission("epicquiz.next")){
			if(commandName.equalsIgnoreCase("quiz")){
				if(args.length == 2){
					if(args[0].equalsIgnoreCase("next")){
						//TODO World exists?
						current.put("question", -1);
						quiz.nextQuestion(args[1]);
						return true;
					}
				}
			}
		}else{
			sender.sendMessage(ChatColor.RED + "EpicQuiz: You don't have permission to do that.");
			return true;
		}
		return false;
	}
}
