package org.lskk.shesop.steppy.data;


public class Friend {
	
	private String name;
	private String telp;
	
	private int highScore;
	private int level;
	private int todayStep;
	private int weeklyStep;
	
	
	public Friend(String name, String telp) {
		this.setName(name);
		this.setTelp(telp);
	}
	
	public Friend(String name, String telp, int level, int highScore,
			int todayStep, int weeklyStep) {
		this.setName(name);
		this.setTelp(telp);
		this.setLevel(level);
		this.setHighScore(highScore);
		this.setTodayStep(todayStep);
		this.setWeeklyStep(weeklyStep);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the telp
	 */
	public String getTelp() {
		return telp;
	}

	/**
	 * @param telp the telp to set
	 */
	public void setTelp(String telp) {
		this.telp = telp;
	}

	/**
	 * @return the highScore
	 */
	public int getHighScore() {
		return highScore;
	}

	/**
	 * @param highScore the highScore to set
	 */
	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * @return the todayStep
	 */
	public int getTodayStep() {
		return todayStep;
	}

	/**
	 * @param todayStep the todayStep to set
	 */
	public void setTodayStep(int todayStep) {
		this.todayStep = todayStep;
	}

	/**
	 * @return the weeklyStep
	 */
	public int getWeeklyStep() {
		return weeklyStep;
	}

	/**
	 * @param weeklyStep the weeklyStep to set
	 */
	public void setWeeklyStep(int weeklyStep) {
		this.weeklyStep = weeklyStep;
	}

	public interface Listener {
		public abstract void dataChanged();
	}

}
