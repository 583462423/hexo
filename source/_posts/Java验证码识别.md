---
title: Java验证码识别
date: 2017-05-14 17:41:35
tags: Java
---

准备做一个掌动山威的后台项目,但是该项目有个缺点就是,经常要输入验证码,于是想要在后台实现自动识别验证码的功能,这样用户就不用自己填写.
<!--more-->
识别验证码一般算法是OCR,在网上搜有关内容,发现有个Tess4j可以实现实现验证码的功能,于是搜相关资料,发现一个可以成功实验的,需要依赖的包如下:
```
 <dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna</artifactId>
    <version>4.2.1</version>
</dependency>

<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>2.0.1</version>
    <exclusions>
        <exclusion>
            <groupId>com.sun.jna</groupId>
            <artifactId>jna</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
但是因为是Ubuntu系统,在测试的时候,报没有libtesseract.so包,这个时候需要在Ubuntu上安装Tesseract-ocr,执行命令:`sudo apt-get install tesseract-ocr`.

测试代码如下:
```
public class ReadImg {
    public static void main(String[] args) throws IOException {

        URL url = new URL("http://iecard.wh.sdu.edu.cn/Account/GetCheckCodeImg?rad=45");
        File imageFile = new File("tmp.gif");
        if (!imageFile.exists()) imageFile.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());

        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = bis.read(buf)) != -1) {
            fileOutputStream.write(buf,0,len);
            fileOutputStream.flush();
        }

        bis.close();
        fileOutputStream.close();

        Tesseract tessreact = new Tesseract();
        try {
            String result = tessreact.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
```

但是结果令人不满意,哈哈哈,玩了2,3个小时,可惜了.

但是!老子就是不服,就一个数字验证码,还降不了你了?

于是上网搜索相关资料,找到了资料后,发现了一个非常好玩的方法,参考:http://blog.csdn.net/problc/article/details/5794460

其验证数字验证码的思路是:图片预处理,分割,训练,识别.

其**图像预处理**的思路是,首先将图片进行二值化,如:
![](/images/3423.png)
二值化后:
![](/images/to3523.png)

代码为:
```
    public static BufferedImage removeBackgroud(String picFile)
            throws Exception {
        BufferedImage img = ImageIO.read(new File(picFile));
        int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isWhite(img.getRGB(x, y)) == 1) {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        /** 去除小黑点*/
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (isWhite(img.getRGB(x, y)) != 1) {
                    if( x-1 >= 0 && isWhite(img.getRGB(x-1,y)) != 1) break;
                    if (y-1 >= 0 && isWhite(img.getRGB(x,y-1)) != 1) break;
                    if (y+1 < height && isWhite(img.getRGB(x,y+1)) != 1) break;
                    if (x+1 < width && isWhite(img.getRGB(x+1,y)) != 1) break;

                    //当前点是小黑点,清除
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
            //System.out.println();
        }
        return img;
    }

  public static int isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() >450) {
            return 1;
        }
        return 0;
    }
```

其中判断是否是白色的思路是,通过像素的RGB值,如果大于某一阀值,表明该像素比较亮,那么就将该像素设置为白色.

接着是**分割**图片,分割图片遇到不少困难,起初分割的方法是,对于二值化后的图片逐层扫描,碰到黑像素,就停止,然后向下扫描黑像素持续到的位置,之后切割即可.但是这样分割后的图片不是定长的,有的长有的短,导致最后预测验证码的成功率非常低.

在碰到这个问题的时候,第一个想法是换操作系统,使用PS来查看验证码图片中像素的分布位置,看是否是有规律,但是因为重启电脑比较麻烦,于是上网找Ubuntu方面的图像处理软件,最后找到了Gimp,该软件可以将图片放大为最大值,使用软件查看验证码图片,发现每个验证码的宽高基本一样,只是位置不一样,所以思路便有了.
![](/images/big3423.png)

直接贴代码:
```
    public static List<BufferedImage> splitImage(BufferedImage bi) throws IOException {
        List<BufferedImage> splitImgs = new ArrayList<>();
        List<BufferedImage> resultImsg = new ArrayList<>();
        int w = bi.getWidth();
        int h = bi.getHeight();

        splitImgs.add(bi.getSubimage(3,0,8,h));
        splitImgs.add(bi.getSubimage(16,0,8,h));
        splitImgs.add(bi.getSubimage(29,0,8,h));
        splitImgs.add(bi.getSubimage(42,0,8,h));

        for (int i=0; i<splitImgs.size(); i++) {
            BufferedImage tmp = splitImgs.get(i);

            int startY = -1;
            startY : for (int y=0; y<h; y++) {
                        for (int x=0; x<tmp.getWidth(); x++) {
                            if (isWhite(tmp.getRGB(x,y)) != 1) {
                                //如果是不是白色
                                startY = y;
                                break startY;
                            }
                        }
                    }

            /**判断以startY为起点,是否最后一行全白,如果是,说明误判*/
            for(int x=0; x<tmp.getWidth(); x++){
                if (isWhite(tmp.getRGB(x,startY+10)) != 1){
                    //出现非白,则没有误判,退出
                    break;
                } else if (x == tmp.getWidth()-1 && isWhite(tmp.getRGB(x,startY+10)) == 1) {
                    //全白,即误判
                    startY--;
                }
            }

            tmp = tmp.getSubimage(0,startY,tmp.getWidth(),11);
            resultImsg.add(tmp);
        }
        return resultImsg;
    }
```

然后是**训练**,其实训练就是模板,对于一个数字,肯定会有一个模板图像,将目标图像和模板图像进行对比,如果相似度高,那么就可以判断是否是某一个数字,比如我的训练是通过多次分割图片而取得的结果:
```

10000011
11100011
11100011
11100011
11100111
11100111
11100111
11100011
11100011
11100011
10000001




11000011    
10000001
10110001
11110001
11110001
11110111
11100111
11001111
10011101
00000001
00000001




11000011
10000001
10110001
11110001
11110011
11000001
11110000
11111000
11111000
00011001
10000011



11111001
11110001
11110001
11100001
11000001
11010001
10110001
00000000
00000000
11110001
11110001


11000000
11000001
10011111
10000111
10000001
11110000
11111000
11111100
11111100
00011101
10000011




11111000
11100011
11000111
10001111
00000001
00011000
00011000
00011000
00011000
10000001
11000011




10000000
10000000
10111100
11111001
11111011
11111011
11110011
11110011
11110111
11100111
11101111




11000011
10011001
10011001
10001001
10000001
11000011
10000001
00011000
00011000
10011001
11000011



11000011
10011001
10011000
10011000
10011000
10011000
11000000
11111001
11110011
11000111
00001111



```

最后一步就是识别,识别直接拿分割到的图片和模板图片进行对比,看其相同位置都是0的个数是多少,个数最大即命中率最大,最后输出结果即可.

在没有优化代码的时候,成功率为95%,可以看下结果:
![](/images/成功率.png)

研究这个东西花了1天左右的时间,虽然看起来比较简单,但是因为在细节上耗费不少时间,所以用的时间还是比较多的.

在刚开始写这个的时候,感觉自己肯定实现不了,但是最后写着写着发现思路很广泛,但是因为时间关系,当前的代码就优化成这样了,以后有时间在说吧.
