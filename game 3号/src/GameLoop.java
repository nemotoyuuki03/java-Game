public class GameLoop
{
    private int targetFPS;
    private static final int FPS_60 = 60;
    private static final int FPS_30 = 30;

    public GameLoop() {
        this.targetFPS = FPS_60; // デフォルトは60fps
    }

    public void startLoop(Runnable gameAction) 
    {
        new Thread(() -> 
        {
            long lastTime = System.nanoTime();
            double nsPerTick = 1000000000.0 / targetFPS;
            double delta = 0;

            while (true) 
            {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerTick;
                lastTime = now;

                while (delta >= 1)
                {
                    gameAction.run();  // ゲームアクションの実行
                    delta -= 1;
                }

                // フレームレート制限
                try 
                {
                    Thread.sleep(2);  // 少し待機してFPSを制限
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 現在のフレームレートを元にFPSを設定
    public void adjustFPS(int currentFPS) {
        if (currentFPS >= 80) {
            this.targetFPS = FPS_60;  // 80fps以上なら60fps固定
        } else {
            this.targetFPS = FPS_30;  // 80fps未満なら30fps固定
        }
    }
}
