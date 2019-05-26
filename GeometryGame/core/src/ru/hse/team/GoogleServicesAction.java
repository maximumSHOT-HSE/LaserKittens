package ru.hse.team;

interface GoogleServicesAction {

    void signIn();
    void signOut();
    boolean isSignedIn();
    void rateGame();
    void submitScore(long score);
    void showScores();
    void unlockAchievement(String achievementId);
    void showAchievements();

    String accelerometerAchievement = "CgkIg87J7v4LEAIQAg";
}
