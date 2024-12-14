import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ranking {
    private List<Integer> top10Scores;

    public Ranking() {
        top10Scores = new ArrayList<>();
        loadRanking();  // コンストラクタでランキングをロード
    }

    public void saveRanking(int score) {
        top10Scores.add(score);
        Collections.sort(top10Scores, Collections.reverseOrder());  // 降順でソート

        // ランキングがトップ10を超えた場合は削除
        if (top10Scores.size() > 10) {
            top10Scores.remove(top10Scores.size() - 1);
        }

        saveRankingToFile();  // ファイルに保存
    }

    private void loadRanking() {
        // ファイルからランキングを読み込む処理（例: CSV、JSON、XML など）
        // ここでは簡略化のため、仮のデータを追加
        try (BufferedReader reader = new BufferedReader(new FileReader("./ranking.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                top10Scores.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRankingToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./ranking.txt"))) {
            for (Integer score : top10Scores) {
                writer.write(score.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getTop10() {
        return top10Scores;
    }

    public void clearRanking() {
        top10Scores.clear();
        saveRankingToFile();  // リセット後、ファイルに保存
    }
}
