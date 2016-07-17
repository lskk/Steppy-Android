package org.lskk.shesop.steppy.gameelement;

public class ExponentialLevelingModel implements ILevelingModel{

	private int startingExperience;
	private double baseGrowth;
	
	public ExponentialLevelingModel(int startingExperience, double baseGrowth){
		this.startingExperience = startingExperience;
		this.baseGrowth = baseGrowth;
	}
	
	@Override
	public int getNextLevelExperience(int currentLevel) {
		return (int) (startingExperience * Math.pow(this.baseGrowth, currentLevel - 1));
	}

}
