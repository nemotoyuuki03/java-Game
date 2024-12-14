import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player {
    private BufferedImage playerImage;
    private int width;
    private int height;
    private int playerPosX;
    private int playerPosY;
    private double velocityY = 0;
    private static final double GRAVITY = 0.1;
    private static final double JUMP_STRENGTH = -3.5;
    private Rectangle boundingBox;

    // コンストラクタ
    public Player(BufferedImage image, int width, int height, int windowHeight) {
        this.playerImage = image;
        this.width = width;
        this.height = height;
        this.playerPosX = 100;  // 初期X位置
        this.playerPosY = (windowHeight - this.height) / 2;  // 初期Y位置
        this.boundingBox = new Rectangle(playerPosX, playerPosY, width, height);
    }

    // プレイヤーのY位置を設定
    public void setPlayerPosY(int playerPosY) {
        this.playerPosY = playerPosY;
        updateBoundingBox();
    }

    public int getPlayerPosY() {
        return this.playerPosY;
    }

    // プレイヤーのY方向の速度を設定
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getVelocityY() {
        return this.velocityY;
    }

    // ジャンプ処理
    public void jump() {
        this.velocityY = JUMP_STRENGTH;
    }

    // 重力を適用してY位置を更新
    public void applyGravity() {
        this.velocityY += GRAVITY;
        this.playerPosY += (int) velocityY;
        updateBoundingBox();
    }

    // バウンディングボックスを更新
    private void updateBoundingBox() {
        this.boundingBox.setBounds(playerPosX, playerPosY, width, height);
    }

    // プレイヤーの描画
    public void draw(Graphics g) {
        int spriteIndex = 0;
        int spriteWidth = 64;
        int spriteHeight = 16;
        g.drawImage(playerImage, playerPosX, playerPosY,
                playerPosX + spriteWidth, playerPosY + spriteHeight,
                spriteIndex * spriteWidth, 0,
                (spriteIndex + 1) * spriteWidth, spriteHeight,
                null);

        // バウンディングボックスを描画（デバッグ用）
        g.setColor(new Color(0, 0, 0, 0));
        g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

    // プレイヤーのバウンディングボックスを取得
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    // ゲームのリセット時にプレイヤーの位置と速度を初期状態に戻す
    public void resetPlayer(int windowHeight) {
        this.playerPosX = 100;  // 初期X位置
        this.playerPosY = (windowHeight - this.height) / 2;  // 初期Y位置
        this.velocityY = 0;  // 初期の速度
        updateBoundingBox();  // バウンディングボックスも更新
    }
}
