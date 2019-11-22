package utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.*;

/**
 * @author xiaolongchen
 * @create 2019/3/27
 */
public class DragCaptchaUtil {
    public static final String LOCATION_X_KEY = "captcha_";
    private static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) " +
            "AppleWebKit/534.13 (KHTML, like Gecko) Chrome/9.0.597.107 Safari/534.13";
    private static final int shadowWidth = 4; //阴影宽度
    private static final int lightHeightWidth = 5; //图片边缘亮色（黄色）宽度。
    private static final int arc = 10; //圆弧直径
    private static final Color clrGlowInnerHi = new Color(253, 239, 175, 148);
    private static final Color clrGlowInnerLo = new Color(255, 209, 0);
    private static final Color clrGlowOuterHi = new Color(253, 239, 175, 124);
    private static final Color clrGlowOuterLo = new Color(255, 179, 0);


    private int tailoring_w = 50; //小图的宽
    private int tailoring_h = 50; //小图的高
    private int location_x = 0; //随机X位置
    private int location_y = 0; //随机Y位置


    public DragCaptchaUtil() {
    }

    private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
        float[] clr1 = c1.getComponents(null);
        float[] clr2 = c2.getComponents(null);
        for (int i = 0; i < clr1.length; i++) {
            clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
        }
        return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
    }


    /**
     * 对图片进行裁剪
     *
     * @param sourceImage 图片
     * @param x           裁剪图左上方X位置
     * @param y           裁剪图左上方Y位置
     * @param w           裁剪的宽
     * @param h           裁剪的宽
     * @return 裁剪之后的图片Buffered
     * @throws IOException
     */
    private BufferedImage cutImg(BufferedImage sourceImage, int x, int y, int w, int h) throws IOException {
        Iterator iterator = ImageIO.getImageReadersByFormatName("png");
        ImageReader render = (ImageReader) iterator.next();
        byte[] imageInByte = getImageBytes(sourceImage);
        ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(imageInByte));
        render.setInput(in, true);
        BufferedImage bufferedImage;
        try {
            ImageReadParam param = render.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, w, h);
            param.setSourceRegion(rect);
            bufferedImage = render.read(0, param);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return bufferedImage;
    }

    private byte[] getImageBytes(BufferedImage sourceImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    /**
     * 创建图片并上传云端， 图片的云端路径
     *
     * @param imageUrl
     * @return
     * @throws IOException
     */
    //public Map<String, String> create(HttpServletRequest request, String imageUrl) throws IOException {
    public Map<String, String> create(String imageUrl) throws IOException {
        //从原始图片中随机截取小图，同时处理背景大图
        Map<String, String> result = createImg(imageUrl);
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        //long lock = RedisClientUtil.insertNXKey(LOCATION_X_KEY + key, String.valueOf(location_x), 60);
        //while (lock <= 0) {
        //    key = LOCATION_X_KEY + UUID.randomUUID().toString().replaceAll("-", "");
        //    lock = RedisClientUtil.insertNXKey(key, String.valueOf(location_x), 60);
        //}
        //将x 轴位置作为验证码
        //result.put("location_x", key);
        return result;
    }


    private String createBigImg(BufferedImage smllImage, BufferedImage sourceImage) throws IOException {
        //创建一个灰度化图层， 将生成的小图，覆盖到该图层，使其灰度化，用于作为一个水印图
        //将灰度化之后的图片，整合到原有图片上
        BufferedImage bigImg = addWatermark(sourceImage, smllImage, 0.6F);
        String bigImgUrl = uploadImage(bigImg);
        return bigImgUrl;
    }

    /**
     * 添加水印
     *
     * @param source
     * @param smallImage
     */
    private BufferedImage addWatermark(BufferedImage source, BufferedImage smallImage, float alpha) throws IOException {
        Graphics2D graphics2D = source.createGraphics();
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        graphics2D.drawImage(smallImage, location_x, location_y, null);
        graphics2D.dispose(); //释放
        return source;
    }

    private Map<String, String> createImg(String imageUrl) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) (new URL(imageUrl)).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-agent", CHROME_USER_AGENT);
        conn.setRequestProperty("Connection", "Keep-Alive");
        inputStream = conn.getInputStream();
        BufferedImage sourceBuff = ImageIO.read(inputStream);
        if (inputStream != null)
            inputStream.close();
        if (conn != null)
            conn.disconnect();

        int width = sourceBuff.getWidth();
        int height = sourceBuff.getHeight();
        //生成随机x，y
        Random random = new Random();
        //X轴距离右端tailoring_w 以上）  Y轴距离底部tailoring_y以上
        this.location_x = random.nextInt(width - tailoring_w * 2) + tailoring_w;
        this.location_y = random.nextInt(height - tailoring_h);
        //裁剪小图
        BufferedImage sourceSmall = cutImg(sourceBuff, location_x, location_y, tailoring_w, tailoring_h);
        //创建shape区域
        List<Shape> shapes = createSmallShape();
        Shape area = shapes.get(0);
        Shape bigarea = shapes.get(1);
        //创建图层用于处理小图的阴影
        BufferedImage bfm1 = new BufferedImage(tailoring_w, tailoring_h, BufferedImage.TYPE_INT_ARGB);
        //创建图层用于处理大图的凹槽
        BufferedImage bfm2 = new BufferedImage(tailoring_w, tailoring_h, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < tailoring_w; i++) {
            for (int j = 0; j < tailoring_h; j++) {
                if (area.contains(i, j)) {
                    bfm1.setRGB(i, j, sourceSmall.getRGB(i, j));
                }
                if (bigarea.contains(i, j)) {
                    bfm2.setRGB(i, j, Color.black.getRGB());
                }
            }
        }
        ////处理图片的边缘高亮及其阴影效果
        BufferedImage resultImgBuff = dealLightAndShadow(bfm1, area);

        String smallImgUrl = uploadImage(resultImgBuff);

        Map<String, String> result = new HashMap<>();

        result.put("smallImgName", smallImgUrl);
        //将灰色图当做水印印到原图上
        String bigImgUrl = createBigImg(bfm2, sourceBuff);
        result.put("bigImgName", bigImgUrl);
        result.put("location_y", String.valueOf(location_y));
        result.put("sourceImgName", imageUrl);
        return result;
    }

    private String uploadImage(BufferedImage resultImgBuff) throws IOException {
        byte[] imageInByte = getImageBytes(resultImgBuff);
        //云端路径
        //String url = FileUploadUtil.uploadFile(imageInByte);
        String url = "";
        return url;
    }

    private List<Shape> createSmallShape() {
        //处理小图，在4个方向上 随机找到2个方向添加凸出
        Random random = new Random(System.currentTimeMillis());
        int face1 = random.nextInt(3); //凸出1
        int face2; //凸出2
        //使凸出1 与 凸出2不在同一个方向
        while (true) {
            face2 = random.nextInt(3);
            if (face1 != face2) {
                break;
            }
        }
        //生成随机区域值， （10-20）之间
        int position1 = random.nextInt((tailoring_h - arc * 2) / 2) + (tailoring_h - arc * 2) / 2;
        Shape shape1 = createShape(face1, 0, position1);
        Shape bigshape1 = createShape(face1, 2, position1);

        //生成中间正方体Shape, (具体边界+弧半径 = x坐标位)
        Shape centre = new Rectangle2D.Float(arc, arc, tailoring_w - 2 * 10, tailoring_h - 2 * 10);
        int position2 = random.nextInt((tailoring_h - arc * 2) / 2) + (tailoring_h - arc * 2) / 2;
        Shape shape2 = createShape(face2, 0, position2);

        //因为后边图形需要生成阴影， 所以生成的小图shape + 阴影宽度 = 灰度化的背景小图shape（即大图上的凹槽）
        Shape bigshape2 = createShape(face2, shadowWidth / 2, position2);
        Shape bigcentre = new Rectangle2D.Float(10 - shadowWidth / 2, 10 - shadowWidth / 2, 30 + shadowWidth, 30 + shadowWidth);

        //合并Shape
        Area area = new Area(centre);
        area.add(new Area(shape1));
        area.add(new Area(shape2));
        //合并大Shape
        Area bigArea = new Area(bigcentre);
        bigArea.add(new Area(bigshape1));
        bigArea.add(new Area(bigshape2));
        List<Shape> list = new ArrayList<Shape>();
        list.add(area);
        list.add(bigArea);
        return list;
    }

    //处理小图的边缘灯光及其阴影效果
    private BufferedImage dealLightAndShadow(BufferedImage bfm, Shape shape) throws IOException {
        //创建新的透明图层，该图层用于边缘化阴影， 将生成的小图合并到该图上
        BufferedImage buffimg = ((Graphics2D) bfm.getGraphics()).getDeviceConfiguration().createCompatibleImage(50, 50, Transparency.TRANSLUCENT);
        Graphics2D graphics2D = buffimg.createGraphics();
        Graphics2D g2 = (Graphics2D) bfm.getGraphics();
        //原有小图，边缘亮色处理
        paintBorderGlow(g2, lightHeightWidth, shape);
        //新图层添加阴影
        paintBorderShadow(graphics2D, shadowWidth, shape);
        graphics2D.drawImage(bfm, 0, 0, null);
        return buffimg;
    }

    /**
     * 处理阴影
     *
     * @param g2
     * @param shadowWidth
     * @param clipShape
     */
    private void paintBorderShadow(Graphics2D g2, int shadowWidth, Shape clipShape) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int sw = shadowWidth * 2;
        for (int i = sw; i >= 2; i -= 2) {
            float pct = (float) (sw - i) / (sw - 1);
            //pct<03. 用于去掉阴影边缘白边，  pct>0.8用于去掉过深的色彩， 如果使用Color.lightGray. 可去掉pct>0.8
            if (pct < 0.3 || pct > 0.8) {
                continue;
            }
            g2.setColor(getMixedColor(new Color(54, 54, 54), pct, Color.WHITE, 1.0f - pct));
            g2.setStroke(new BasicStroke(i));
            g2.draw(clipShape);
        }
    }

    /**
     * 处理边缘亮色
     *
     * @param g2
     * @param glowWidth
     * @param clipShape
     */
    public void paintBorderGlow(Graphics2D g2, int glowWidth, Shape clipShape) {
        int gw = glowWidth * 2;
        for (int i = gw; i >= 2; i -= 2) {
            float pct = (float) (gw - i) / (gw - 1);
            Color mixHi = getMixedColor(clrGlowInnerHi, pct, clrGlowOuterHi, 1.0f - pct);
            Color mixLo = getMixedColor(clrGlowInnerLo, pct, clrGlowOuterLo, 1.0f - pct);
            g2.setPaint(new GradientPaint(0.0f, 35 * 0.25f, mixHi, 0.0f, 35, mixLo));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, pct));
            g2.setStroke(new BasicStroke(i));
            g2.draw(clipShape);
        }
    }

    //创建圆形区域, 半径为5  type , 0：上方，1：右方 2：下方，3：左方
    private Shape createShape(int type, int size, int position) {
        Arc2D.Float d;
        switch (type) {
            case 0:
                d = new Arc2D.Float(position, 5, 10 + size, 10 + size, 0, 190, Arc2D.CHORD);
                break;
            case 1:
                d = new Arc2D.Float(35, position, 10 + size, 10 + size, 270, 190, Arc2D.CHORD);
                break;
            case 2:
                d = new Arc2D.Float(position, 35, 10 + size, 10 + size, 180, 190, Arc2D.CHORD);
                break;
            case 3:
                d = new Arc2D.Float(5, position, 10 + size, 10 + size, 90, 190, Arc2D.CHORD);
                break;
            default:
                d = new Arc2D.Float(5, position, 10 + size, 10 + size, 90, 190, Arc2D.CHORD);
        }
        return d;
    }
}
