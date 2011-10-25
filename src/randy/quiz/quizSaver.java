package randy.quiz;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class quizSaver {
	
	static File file = new File("plugins" + File.separator + "EpicQuiz" + File.separator + "players.yml");
	static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

	@SuppressWarnings("unchecked")
	public static void loadScore() {
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			if(config.getString("Players") != null){
				String[] players = config.getString("Players").split(", ");
				int e;
				for(e = 0; e < players.length; e++){
					quiz.score.put(players[e], config.getString(players[e]));
				}
			}
		}
		System.out.print("[EpicQuiz]: Score succesfully loaded.");
	}

	public static void saveScore() {
		Object[] entryset = quiz.score.entrySet().toArray();
		String players = null;
		int e;
		for(e = 0; e < entryset.length; e++){
			String[] split = entryset[e].toString().split("=");
			config.set(split[0], split[1]);
			if(e == 0){
				players = split[0];
			}else{
				players = players + ", " + split[0];
			}
		}
		config.set("Players", players);
		try {
			config.save(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.print("[EpicQuiz]: Score succesfully saved.");
	}
}
