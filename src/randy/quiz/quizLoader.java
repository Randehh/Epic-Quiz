package randy.quiz;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class quizLoader {
	
	public static void loadPlugin(){
		loadConfig();
		loadAnnouncer();
		loadAchievements();
		loadWorldQuestions();
	}
	
	/*
	 * Load achievements/benchmarks
	 */
	private static void loadAchievements() {
		File file = new File("plugins" + File.separator + "EpicQuiz" + File.separator + "achievements.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		HashMap<String, Integer> achievements = quiz.achievements;
		if(quiz.achievements.get("enabled") == 1){
			Object[] achievementmarks = config.getConfigurationSection("Achievements").getKeys(false).toArray();
			int i;
			for(i = 0; i < achievementmarks.length; i++){
				achievements.put(achievementmarks[i].toString().replace("a", "")+".reward.money", config.getInt("Achievements."+achievementmarks[i]+".Reward.Money"));
				achievements.put(achievementmarks[i].toString().replace("a", "")+".reward.item.id", config.getInt("Achievements."+achievementmarks[i]+".Reward.Item.ID"));
				achievements.put(achievementmarks[i].toString().replace("a", "")+".reward.item.amount", config.getInt("Achievements."+achievementmarks[i]+".Reward.Item.Amount"));
			}
		}
	}

	/*
	 * Load general options
	 */
	public static void loadConfig(){
		File file = new File("plugins" + File.separator + "EpicQuiz" + File.separator + "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		quiz.delay.put("delay", config.getInt("Delay"));
		quiz.moneyrewards.put("enabled", config.getBoolean("Money_Rewards"));
		quiz.announcer.put("moneyname", config.getString("Money_Name"));
		Boolean achievements = config.getBoolean("Achievements");
		if(achievements){
			quiz.achievements.put("enabled", 1);
		}else{
			quiz.achievements.put("enabled", 0);
		}
	}
	
	
	/*
	 * Load announcer text
	 */
	public static void loadAnnouncer(){
		File file = new File("plugins" + File.separator + "EpicQuiz" + File.separator + "announcer.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String announcername = config.getString("Announcer_Name");
		announcername = config.getString("Colors.Announcername") + announcername + config.getString("Colors.Rest");
		announcername = announcername.replaceAll("&0", ChatColor.BLACK+"");
		announcername = announcername.replaceAll("&1", ChatColor.DARK_BLUE+"");
		announcername = announcername.replaceAll("&2", ChatColor.DARK_GREEN+"");
		announcername = announcername.replaceAll("&3", ChatColor.DARK_AQUA+"");
		announcername = announcername.replaceAll("&4", ChatColor.DARK_RED+"");
		announcername = announcername.replaceAll("&5", ChatColor.DARK_PURPLE+"");
		announcername = announcername.replaceAll("&6", ChatColor.GOLD+"");
		announcername = announcername.replaceAll("&7", ChatColor.GRAY+"");
		announcername = announcername.replaceAll("&8", ChatColor.DARK_GRAY+"");
		announcername = announcername.replaceAll("&9", ChatColor.BLUE+"");
		announcername = announcername.replaceAll("&a", ChatColor.GREEN+"");
		announcername = announcername.replaceAll("&b", ChatColor.AQUA+"");
		announcername = announcername.replaceAll("&c", ChatColor.RED+"");
		announcername = announcername.replaceAll("&d", ChatColor.LIGHT_PURPLE+"");
		announcername = announcername.replaceAll("&e", ChatColor.YELLOW+"");
		announcername = announcername.replaceAll("&f", ChatColor.WHITE+"");
		quiz.announcer.put("ready", config.getString("Ready").replace("[announcername]", announcername));
		quiz.announcer.put("correct", config.getString("Correct").replace("[announcername]", announcername));
		quiz.announcer.put("ask", config.getString("Ask").replace("[announcername]", announcername));
		quiz.announcer.put("reward", config.getString("Reward").replace("[announcername]", announcername));
		quiz.announcer.put("achievement", config.getString("Achievement").replace("[announcername]", announcername));
		quiz.announcer.put("achievementreward", config.getString("Achievement_Reward").replace("[announcername]", announcername));
	}
	
	
	/*
	 * Load questions
	 */
	public static void loadWorldQuestions(){
		File file = new File("plugins" + File.separator + "EpicQuiz" + File.separator + "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String[] worlds = config.getString("Worlds").split(", ");
		for(int e = 0; e < worlds.length; e++){
			Boolean match = false;
			Object[] serverworlds = Bukkit.getServer().getWorlds().toArray();
			for(int a = 0; a < serverworlds.length; a++){
				String serverworld = serverworlds[a].toString().replace("CraftWorld{name=", "").replace("}", "");
				if(serverworld.equals(worlds[e])){
					loadQuestions(worlds[e]);
					match = true;
				}
			}
			if(!match){
				System.out.print("[EpicQuiz]: The world '" + worlds[e] + "' specified in the config doesn't exist!");
			}
		}
	}
	
	public static void loadQuestions(String world){
		quiz.current.put(world+".question", -1);
		File file = new File("plugins" + File.separator + "EpicQuiz" + File.separator + world +".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.contains("q0")){
			int e;
			for(e = 0; config.contains("q"+e); e++){
				quiz.questions.put(world+"."+e+".question", config.getString("q"+e+".Question").toString());
				quiz.questions.put(world+"."+e+".awnser", config.getString("q"+e+".Awnser").toString());
				quiz.rewards.put(world+"."+e+".reward.money", config.getInt("q"+e+".Reward.Money"));
				quiz.rewards.put(world+"."+e+".reward.item.id", config.getInt("q"+e+".Reward.Item.ID"));
				quiz.rewards.put(world+"."+e+".reward.item.amount", config.getInt("q"+e+".Reward.Item.Amount"));
			}
			quiz.current.put(world+".total", e);
			System.out.print("[EpicQuiz]: Succesfully loaded " + e + " questions for world '"+world+"'.");
			quiz.startSystem(world);
		}else{
			System.out.print("[EpicQuiz]: No questions to load in world '"+world+"'.");
		}
	}
}
