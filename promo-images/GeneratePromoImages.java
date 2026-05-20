import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class GeneratePromoImages {

    // Original design coordinates (1080x1920)
    static final int DESIGN_W = 1080;
    static final int DESIGN_H = 1920;

    // Output dimensions
    static final int W = 720;
    static final int H = 1280;

    // Scale factor
    static final double S = (double) W / DESIGN_W;  // = 2/3

    public static void main(String[] args) throws Exception {
        generatePage1();
        generatePage2();
        generatePage3();
        System.out.println("Done: 01-home.jpg, 02-quiz.jpg, 03-leaderboard.jpg");
    }

    static void generatePage1() throws Exception {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.scale(S, S);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(0xE8F8E8), 0, DESIGN_H, new Color(0xF5FFF5));
        g.setPaint(bgGrad);
        g.fillRect(0, 0, DESIGN_W, DESIGN_H);

        g.setColor(new Color(0x1AAD19, true));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
        g.fill(new Ellipse2D.Double(-300, -400, 1200, 1200));
        g.fill(new Ellipse2D.Double(300, 1400, 900, 900));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        RoundRectangle2D logoBg = new RoundRectangle2D.Double(440, 200, 200, 200, 44, 44);
        GradientPaint logoGrad = new GradientPaint(440, 200, new Color(0x1AAD19), 640, 400, new Color(0x2ECC40));
        g.setPaint(logoGrad);
        g.fill(logoBg);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 100));
        drawCenteredString(g, "Q", 540, 340);

        g.setColor(new Color(0x1AAD19));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 96));
        drawCenteredString(g, "答题挑战", 540, 510);

        g.setColor(new Color(0x666666));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 42));
        drawCenteredString(g, "每天十分钟 · 知识大不同", 540, 580);

        String[][] features = {
            {"海量题库", "涵盖Java、网络、数据库等"},
            {"限时挑战", "每题15秒，锻炼快速反应"},
            {"排行榜PK", "与好友比拼，争夺榜首"}
        };
        Color[] iconBgColors = {new Color(0xE8F4FD), new Color(0xFEF5E7), new Color(0xF3E8FF)};
        String[] icons = {"1", "2", "3"};

        int y = 700;
        for (int i = 0; i < features.length; i++) {
            RoundRectangle2D card = new RoundRectangle2D.Double(140, y, 800, 160, 36, 36);
            g.setColor(Color.WHITE);
            g.fill(card);
            g.setColor(new Color(0, 0, 0, 15));
            g.setStroke(new BasicStroke(1f));
            g.draw(card);

            RoundRectangle2D iconBg = new RoundRectangle2D.Double(190, y + 30, 100, 100, 28, 28);
            g.setColor(iconBgColors[i]);
            g.fill(iconBg);
            g.setColor(new Color(0x333333));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 42));
            drawCenteredString(g, icons[i], 240, y + 98);

            g.setColor(new Color(0x333333));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 40));
            g.drawString(features[i][0], 330, y + 68);

            g.setColor(new Color(0x999999));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 30));
            g.drawString(features[i][1], 330, y + 116);

            y += 210;
        }

        g.setColor(new Color(0xBBBBBB));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 28));
        drawCenteredString(g, "扫码即刻开始挑战", 540, 1800);

        g.dispose();
        saveJpeg(img, new File("01-home.jpg"));
    }

    static void generatePage2() throws Exception {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.scale(S, S);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g.setColor(new Color(0xF5F5F5));
        g.fillRect(0, 0, DESIGN_W, DESIGN_H);

        int pad = 80;

        g.setColor(new Color(0xE0E0E0));
        g.fill(new RoundRectangle2D.Double(pad, pad, DESIGN_W - 2 * pad, 16, 8, 8));
        g.setColor(new Color(0x1AAD19));
        g.fill(new RoundRectangle2D.Double(pad, pad, (int)((DESIGN_W - 2 * pad) * 0.3), 16, 8, 8));

        g.setColor(new Color(0x666666));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 28));
        g.drawString("第 3 / 10 题", pad, 140);
        g.setColor(new Color(0xE64340));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 56));
        drawRightAlignedString(g, "09s", DESIGN_W - pad, 140);
        g.setColor(new Color(0x1AAD19));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 32));
        drawCenteredString(g, "得分: 2", 540, 140);

        int qy = 200;
        RoundRectangle2D qCard = new RoundRectangle2D.Double(pad, qy, DESIGN_W - 2 * pad, 220, 28, 28);
        g.setColor(Color.WHITE);
        g.fill(qCard);
        g.setColor(new Color(0, 0, 0, 10));
        g.setStroke(new BasicStroke(1f));
        g.draw(qCard);
        g.setColor(new Color(0x333333));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 44));
        drawWrappedString(g, "Java 中哪个关键字用于实现继承？", pad + 60, qy + 120, DESIGN_W - 2 * pad - 120, 55);

        String[][] options = {
            {"A", "extends", "correct"},
            {"B", "implements", "wrong"},
            {"C", "inherit", ""},
            {"D", "super", ""}
        };
        Color[] optColors = {new Color(0xE8F8E8), new Color(0xFEF0F0), Color.WHITE, Color.WHITE};
        Color[] borderColors = {new Color(0x1AAD19), new Color(0xE64340), new Color(0xE0E0E0), new Color(0xE0E0E0)};
        String[] optionIcons = {"O", "X", "", ""};
        Color[] iconColors = {new Color(0x1AAD19), new Color(0xE64340), Color.WHITE, Color.WHITE};

        int oy = 480;
        for (int i = 0; i < options.length; i++) {
            RoundRectangle2D opt = new RoundRectangle2D.Double(pad, oy, DESIGN_W - 2 * pad, 120, 24, 24);
            g.setColor(optColors[i]);
            g.fill(opt);
            g.setColor(borderColors[i]);
            g.setStroke(new BasicStroke(4f));
            g.draw(opt);

            g.setColor(new Color(0x1AAD19));
            g.fill(new Ellipse2D.Double(pad + 40, oy + 20, 80, 80));
            g.setColor(Color.WHITE);
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 36));
            drawCenteredString(g, options[i][0], pad + 80, oy + 72);

            g.setColor(new Color(0x333333));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 38));
            g.drawString(options[i][1], pad + 160, oy + 78);

            if (!optionIcons[i].isEmpty()) {
                g.setColor(iconColors[i]);
                g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 40));
                drawRightAlignedString(g, optionIcons[i], DESIGN_W - pad - 60, oy + 78);
            }
            oy += 160;
        }

        g.setColor(new Color(0x999999));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 36));
        drawCenteredString(g, "限时 15 秒 · 答对得分", 540, 1200);

        g.dispose();
        saveJpeg(img, new File("02-quiz.jpg"));
    }

    static void generatePage3() throws Exception {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.scale(S, S);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int headerH = 500;
        GradientPaint headerGrad = new GradientPaint(0, 0, new Color(0x1AAD19), 0, headerH, new Color(0x169A15));
        g.setPaint(headerGrad);
        g.fillRect(0, 0, DESIGN_W, headerH);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 72));
        drawCenteredString(g, "排行榜 TOP 3", 540, 200);
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 32));
        drawCenteredString(g, "谁是答题之王？", 540, 280);

        g.setColor(new Color(0xF5F5F5));
        g.fillRect(0, headerH, DESIGN_W, DESIGN_H - headerH);

        String[][] players = {
            {"Gold", "编程小达人", "05-20 10:30", "10", "gold"},
            {"Silver", "Java爱好者", "05-20 09:15", "9", "silver"},
            {"Bronze", "代码诗人", "05-20 08:45", "8", "bronze"},
        };
        Color[] medalBorders = {new Color(0xF0AD4E), new Color(0xBBBBBB), new Color(0xD4A574)};
        Color[] medalBgs = {new Color(0xFFFBE6), new Color(0xF8F8F8), new Color(0xFEF5EE)};
        Color[] medalTexts = {new Color(0xD4A017), new Color(0x888888), new Color(0xCD7F32)};

        int ly = 580;
        for (int i = 0; i < players.length; i++) {
            RoundRectangle2D card = new RoundRectangle2D.Double(80, ly, DESIGN_W - 160, 160, 28, 28);
            g.setColor(medalBgs[i]);
            g.fill(card);
            g.setColor(medalBorders[i]);
            g.setStroke(new BasicStroke(4f));
            g.draw(card);

            // Rank badge
            g.setColor(medalTexts[i]);
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 32));
            g.drawString("#" + (i + 1), 155, ly + 95);

            g.setColor(new Color(0x333333));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 40));
            g.drawString(players[i][1], 220, ly + 65);

            g.setColor(new Color(0xBBBBBB));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 26));
            g.drawString(players[i][2], 220, ly + 110);

            g.setColor(new Color(0x1AAD19));
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 64));
            drawRightAlignedString(g, players[i][3], DESIGN_W - 160, ly + 95);
            g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 28));
            g.setColor(new Color(0x999999));
            g.drawString("分", DESIGN_W - 110, ly + 95);

            ly += 200;
        }

        g.setColor(new Color(0x1AAD19));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.BOLD, 40));
        drawCenteredString(g, "快来挑战，超越他们！", 540, 1400);
        g.setColor(new Color(0x999999));
        g.setFont(new Font("Microsoft YaHei,PingFang SC", Font.PLAIN, 28));
        drawCenteredString(g, "扫码立即参与答题", 540, 1460);

        g.dispose();
        saveJpeg(img, new File("03-leaderboard.jpg"));
    }

    static void saveJpeg(BufferedImage img, File file) throws Exception {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.85f);
        writer.setOutput(new FileImageOutputStream(file));
        writer.write(null, new IIOImage(img, null, null), param);
        writer.dispose();
    }

    static void drawCenteredString(Graphics2D g, String text, int cx, int y) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        int x = cx - (int)(r.getWidth() / 2);
        g.drawString(text, x, y);
    }

    static void drawRightAlignedString(Graphics2D g, String text, int rightX, int y) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        int x = rightX - (int)r.getWidth();
        g.drawString(text, x, y);
    }

    static void drawWrappedString(Graphics2D g, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g.getFontMetrics();
        StringBuilder line = new StringBuilder();
        int curY = y;

        for (int i = 0; i < text.length(); i++) {
            String test = line.toString() + text.charAt(i);
            if (fm.stringWidth(test) > maxWidth && line.length() > 0) {
                g.drawString(line.toString(), x, curY);
                line = new StringBuilder().append(text.charAt(i));
                curY += lineHeight;
            } else {
                line.append(text.charAt(i));
            }
        }
        if (line.length() > 0) {
            g.drawString(line.toString(), x, curY);
        }
    }
}
