public class Main {
    public static void main(String[] args) {
        DisPlay window = new DisPlay("Flappy Bird");
        window.loadBackground("src/img/backGround.png");
        window.loadMap("src/img/map.png");
        window.loadPlayer("src/img/OneBird1.png");
        window.loadCollisionData("src/csv/map 1.csv", 32, 31);
    }
}
