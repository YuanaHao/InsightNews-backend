package com.sosd.insightnews;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class test {

    private final String rednote = "http://xhslink.com/a/MoDfqZlTIOY5";
    private final String toutiao = "https://www.toutiao.com/article/7480438328316445211/?log_from=aa82371b8bcf9_1741783011928";
    private final String wechat = "https://mp.weixin.qq.com/s/HNu_34lhHloHRn87cxrTYQ";
    private final String douyin = "https://v.douyin.com/iPXds7gH/";
    private final String bilibili = "https://b23.tv/vxs6Cop";
    private final String wangyi = "https://www.163.com/news/article/JPV6OODC000189FH.html";
    private final String wangyicssSelector = ".post_body";
    private final String toutiaocssSelector = ".expand-container";
    private final String wechatcssSelector = ".rich_media_area_primary";
    private final String rednotecssSelector = "div.article-content";
    private final String bilibilicssSelector = "div.article-content";
    private final String douyincssSelector = "div.article-content";

    @Test
    public void parseHTML() throws InterruptedException {
        // 1. 设置ChromeDriver路径（需与本地Chrome版本匹配）
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
        System.setProperty("webdriver.chrome.whitelistedIps", "");


        // 2. 配置Chrome选项（无需手动设置IP白名单）
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");          // 使用新版无头模式
        options.addArguments("--disable-gpu");          // 禁用GPU加速
        options.addArguments("--no-sandbox");           // 禁用沙箱（Linux/Mac必需）
        options.addArguments("--disable-dev-shm-usage");// 避免内存不足崩溃
        options.addArguments("--remote-allow-origins=*");// 允许跨域请求（Selenium 4必需）
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation")); // 隐藏自动化标志
        options.addArguments("--remote-debugging-pipe");


        // 3. 初始化WebDriver
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(toutiao);

            // 等待特定元素加载完成
            WebDriverWait wait = new WebDriverWait(driver, 10);
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(toutiaocssSelector +" img"))); // 等待图片加载
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(toutiaocssSelector +" video"))); // 等待视频加载
            WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1")));



            String title = titleElement.getText();

            // 提取文章正文 根据实际网页结构调整选择器
            List<WebElement> contentElements = driver.findElements(By.cssSelector(toutiaocssSelector));
            // 输出每个元素的 HTML 内容
            for (WebElement element : contentElements) {
                System.out.println(element.getAttribute("outerHTML"));
            }
            StringBuilder articleContent = new StringBuilder();
            for (WebElement element : contentElements) {
                articleContent.append(element.getText()).append("\n");
            }

            // 输出标题和文章正文
            System.out.println("新闻标题: " + title);
            System.out.println("文章正文: ");
            System.out.println(articleContent.toString());


//             提取图片和视频
//            List<WebElement> images = driver.findElements(By.tagName("img"));
            // 提取特定范围内的图片
            List<WebElement> images = driver.findElements(By.cssSelector(toutiaocssSelector +" img")); // 选择 expand-container 中的所有 img 标签
            List<WebElement> videos = driver.findElements(By.cssSelector(toutiaocssSelector + " video"));

            // 输出结果
            System.out.println("图片数量: " + images.size());
            images.forEach(img -> System.out.println("图片源: " + img.getAttribute("src")));

            System.out.println("视频数量: " + videos.size());
            videos.forEach(video -> System.out.println("视频源: " + video.getAttribute("src")));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // 确保退出浏览器
        }
    }


    }

