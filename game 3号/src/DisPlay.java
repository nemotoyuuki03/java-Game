import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DisPlay extends JFrame implements KeyListener {
    private BufferedImage backgroundImage;
    private BufferedImage mapImage;
    private Player player;
    private Collision collision;
    private GamePanel gamePanel;
    private int mapPosUpdateY;  // スクロール用の位置変数
    private boolean gameOver = false;
    private int countdown = 5;  // ゲーム開始前のカウントダウン
    private boolean gameStarted = false;
    private boolean gameInProgress = false;

    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

    private JButton startButton;
    private int score = 0;
    private Timer scoreTimer;
    private Ranking ranking;  // ランキング用オブジェクト
    private JButton retryButton;  // リトライボタン

    private JFrame gameOverFrame;  // ゲームオーバーウィンドウを保持する変数

    private static final int SCROLL_SPEED = 1;

    public DisPlay(String name) {
        super(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        gamePanel = new GamePanel();
        add(gamePanel);
        addKeyListener(this);
        setLocationRelativeTo(null);
        setSize(800, 600);
        setVisible(true);

        mapPosUpdateY = 0;
        collision = new Collision();
        ranking = new Ranking();  // ランキングオブジェクトの初期化

        showStartScreen();
    }

    private void showStartScreen() {
        gamePanel.removeAll();
        gamePanel.setBackground(Color.CYAN);
        gameInProgress = false;

        startButton = new JButton("ゲーム開始");
        startButton.setBounds(350, 250, 100, 50);
        startButton.setFocusable(false);
        startButton.addActionListener(e -> startGame());

        gamePanel.setLayout(null);
        gamePanel.add(startButton);
        gamePanel.repaint();
    }

    private void startGame() {
        startButton.setEnabled(false);
        startButton.setVisible(false);
        startCountdown();

        scoreTimer = new Timer();
        scoreTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (gameStarted && !gameOver) {
                    score += 10;
                    gamePanel.repaint();
                }
            }
        }, 0, 1000);
    }

    private void startCountdown() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (countdown > 0) {
                    countdown--;
                    gamePanel.repaint();
                } else {
                    gameStarted = true;
                    gameInProgress = true;
                    gameLoop();
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    public void gameLoop() {
        long lastLoopTime = System.nanoTime();
        long previousTime = lastLoopTime;

        while (!gameOver) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;

            if (gameStarted) {
                performGameAction();
                player.applyGravity();
                checkCollisions();
            }

            gamePanel.repaint();

            long frameTime = now - previousTime;
            previousTime = now;

            long sleepTime = (OPTIMAL_TIME - frameTime) / 1000000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (gameOver) {
                ranking.saveRanking(score);  // スコアをランキングに保存
                showGameOver();
                break;
            }
        }
    }

    private void checkCollisions() {
        for (Rectangle rect : collision.getCollidableAreas()) {
            Rectangle adjustedRect = new Rectangle(rect.x + mapPosUpdateY, rect.y, rect.width, rect.height);
            if (player.getBoundingBox().intersects(adjustedRect)) {
                gameOver = true;
                break;
            }
        }
    }

    private void showGameOver() {
        if (gameOverFrame == null) {
            // ゲームオーバーウィンドウが存在しない場合にのみ新しく作成
            gameOverFrame = new JFrame("ゲームオーバー");
            gameOverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 閉じるときにゲームオーバーウィンドウだけ閉じる
            gameOverFrame.setSize(400, 400);
            gameOverFrame.setLocationRelativeTo(null);

            JPanel gameOverPanel = new JPanel();
            gameOverPanel.setLayout(null);
            gameOverFrame.add(gameOverPanel);

            String message = "ゲームオーバー!\nスコア: " + score;
            javax.swing.JLabel label = new javax.swing.JLabel(message, javax.swing.SwingConstants.CENTER);
            label.setBounds(50, 50, 300, 50);
            gameOverPanel.add(label);

            // ランキングの表示
            javax.swing.JLabel rankingLabel = new javax.swing.JLabel("ランキング:\n", javax.swing.SwingConstants.CENTER);
            rankingLabel.setBounds(50, 110, 300, 150);
            gameOverPanel.add(rankingLabel);

            List<Integer> top10 = ranking.getTop10();
            StringBuilder rankingText = new StringBuilder("<html>");
            for (int i = 0; i < top10.size(); i++) {
                rankingText.append(i + 1).append(". ").append(top10.get(i)).append("<br>");
            }
            rankingText.append("</html>");
            rankingLabel.setText(rankingText.toString());

            // リトライボタンの作成
            retryButton = new JButton("リトライ");
            retryButton.setBounds(150, 270, 100, 50);
            retryButton.addActionListener(e -> restartGame());
            gameOverPanel.add(retryButton);

            gameOverFrame.setVisible(true);
        }
    }

    private void restartGame() {
        // ゲームをリセットして開始
        gameOver = false;
        gameStarted = false;
        score = 0;
        countdown = 5;

        // プレイヤーをリセット (引数を渡す)
        player.resetPlayer(getHeight());

        // マップ位置のリセット
        mapPosUpdateY = 0;

        // スタート画面を表示
        showStartScreen();

        // ゲームオーバーウィンドウを閉じる
        if (gameOverFrame != null) {
            gameOverFrame.dispose();  // ゲームオーバーウィンドウを閉じる
            gameOverFrame = null;  // gameOverFrameをnullに戻す
        }
    }

    private void performGameAction() {
        mapPosUpdateY -= SCROLL_SPEED;
    }

    // 追加したメソッド群
    public void loadBackground(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("背景画像の読み込みエラー: " + imagePath);
            e.printStackTrace();
        }
    }

    public void loadMap(String imagePath) {
        try {
            mapImage = ImageIO.read(new File(imagePath));
            setSize(800, mapImage.getHeight());
            setLocationRelativeTo(null);
        } catch (IOException e) {
            System.err.println("マップ画像の読み込みエラー: " + imagePath);
            e.printStackTrace();
        }
    }

    public void loadPlayer(String imagePath) {
        try {
            BufferedImage playerImage = ImageIO.read(new File(imagePath));
            player = new Player(playerImage, 16, 16, getHeight());
        } catch (IOException e) {
            System.err.println("プレイヤー画像の読み込みエラー: " + imagePath);
            e.printStackTrace();
        }
    }

    public void loadCollisionData(String csvFilePath, int tileWidth, int tileHeight) {
        collision.loadCollidableAreas(csvFilePath, tileWidth, tileHeight);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.jump();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setPlayerPosY(player.getPlayerPosY() + (int) player.getVelocityY());
        }
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
            if (mapImage != null) {
                int y = getHeight() - mapImage.getHeight();
                g.drawImage(mapImage, mapPosUpdateY, y, this);
            }
            if (player != null) {
                player.draw(g);
            }

            g.setColor(new Color(0, 0, 0, 0));
            for (Rectangle rect : collision.getCollidableAreas()) {
                g.fillRect(rect.x + mapPosUpdateY, rect.y, rect.width, rect.height);
            }

            if (!gameStarted) {
                g.setColor(Color.BLACK);
                g.setFont(g.getFont().deriveFont(48f));
                g.drawString(String.valueOf(countdown), getWidth() / 2, getHeight() / 2);
            }

            if (gameStarted) {
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(24f));
                g.drawString("スコア: " + score, 10, 30);
            }
        }
    }
}
