package ru.hse.team;

interface GoogleServicesAction {

    void signIn();
    void signOut();
    boolean isSignedIn();
    void rateGame();
    void submitScore(long score);
    void showScores();
}
