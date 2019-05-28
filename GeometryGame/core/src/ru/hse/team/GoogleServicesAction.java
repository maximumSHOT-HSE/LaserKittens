package ru.hse.team;

public interface GoogleServicesAction {

    void signIn();
    void signInSilently();
    void signOut();
    boolean isSignedIn();
    void rateGame();
    void submitScore(long score);
    void showScores();
    void unlockAchievement(String achievementId);
    void showAchievements();
    void quickGame(int role);
    void invitePlayers();


    String accelerometerAchievement = "CgkIg87J7v4LEAIQAg";
}
