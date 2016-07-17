package org.lskk.shesop.steppy.gameelement;

public class LevelingManager {

	private ILevelingModel levelModel;
	private int currentLevel;
	private int currentTotalExperience;
	private int nextLevelTotalExperience;
	private int nextLevelExperience;
	
	public LevelingManager(ILevelingModel levelModel){
		this.currentLevel = 1;
		this.currentTotalExperience = 0;
		this.levelModel = levelModel;
		this.nextLevelTotalExperience = currentTotalExperience + levelModel.getNextLevelExperience(currentLevel);
		this.nextLevelExperience = levelModel.getNextLevelExperience(currentLevel);
	}

	public ILevelingModel getLevelModel() {
		return levelModel;
	}

	public void setLevelModel(ILevelingModel levelModel) {
		this.levelModel = levelModel;
	}

	public int getCurrentTotalExperience() {
		return currentTotalExperience;
	}

	public void setCurrentTotalExperience(int currentExperience) {
		this.currentTotalExperience = currentExperience;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getNextLevelExperience() {
		return nextLevelExperience;
	}

	public void setNextLevelExperience(int nextLevelExperience) {
		this.nextLevelExperience = nextLevelExperience;
	}

	public int getNextLevelTotalExperience() {
		return nextLevelTotalExperience;
	}

	public void setNextLovelTotalExperience(int nextLovelTotalExperience) {
		this.nextLevelTotalExperience = nextLovelTotalExperience;
	}
	
	// this method called for adding new experience to current total experience
	public int addExperience(int experience){
		int levelUp = 0;
		currentTotalExperience += experience;		
		
		// if the new total experience bigger than the next level total experience 
		// then the level is up by one. Do this checking until next level total experience
		// bigger than the current total experience
		while(currentTotalExperience > nextLevelTotalExperience){
			levelUp++;
			currentLevel++;
			nextLevelTotalExperience += levelModel.getNextLevelExperience(currentLevel);
		}
		
		return levelUp;
	}
	
	public void resetLevel(){
		this.currentLevel = 1;
		this.currentTotalExperience = 0;
		this.nextLevelTotalExperience = currentTotalExperience + levelModel.getNextLevelExperience(currentLevel);
		this.nextLevelExperience = levelModel.getNextLevelExperience(currentLevel);
	}
	
}
